(ns core.core 
  (:use [core.printDB] )
  (:use [utils.constants])
  (:use [utils.utils] )
  )

;db reference
(def db(ref {}))

;get key and record and return true if the key is found in the record
(defn- valid-key?
  "Get key + record. 
   Return True if the key is found in the record"
  [key record]
  (not (nil? (record key)))
  )


(defn valid-cols?
  "Get Record + Cols.
   Return the reurn value from in?"
  [record cols] 
  (in?  cols  record))


;high order function- get 2 collections and function. 
;call the function with the first element in the 
;first collection and with the second collection   
(defn check-record-validation 
  "Call the function with the first element in the 
   first collection and with the second collection"
  [checkList baseList func] 
  (cond 
    (nil? checkList) true 
    (func (first checkList) baseList) (check-record-validation (next checkList) baseList func) 
    :else false
    ) 
  )

(defn create-table
  "Get Table Name + Fields map + Keys"
  [tableName fields keys]
  (cond
    (not (nil? (db tableName))) (println msgErrTableNameExists)
    (check-record-validation keys fields valid-cols?)
    (do
       (dosync (alter db assoc tableName {:keys keys :cols fields :data []}))
       (println msgCreateTableSuccess)
       
      )
  :else (println msgErrKeyNotInFields)
  ))

(defn drop-table 
  "Remove table from DB by Table Name"
  [name] 
  (cond (nil? (db name))  (throw (Exception. msgErrTableNameNotExistsDrop))   ; table not exist
    :else (dosync(alter db dissoc name))  ;drop table
    )
)


;get table name and new record and insert the recond to the table
;used by insert
(defn- add-record [table newRecord] 
  "insert the new record to the table" 
  (let [t ((db table) :data)] 
     (dosync (alter db assoc-in [table :data] (conj t newRecord))))
  )

;Insert new record to exist table
;get table name and new record. check record column name and keys validation.
;if the record is correct, add it to the table 
;return true on success
(defn insert
  "get table name and new record. check record column name and keys validation.
if the record is correct, add it to the table "
  [table newRecord] 
(cond
  ; Invalid Table Name
  (nil? (db table)) 
  (throw (Exception. msgErrTableNameNotExists))
  ; Invalid Keys
  (not(check-record-validation ((db table) :keys) newRecord  valid-key?))
  (throw (Exception. msgErrInvalidKey))
  ; Invalid Feilds
  (not(check-record-validation (keys newRecord) ((db table) :cols) valid-cols? ))
  (throw (Exception. msgErrInvalidfield))
  ; Key already exist
  ;   TODO!
  ; OK
  :else (if-not (nil? (add-record table newRecord)) ; try to insert
          true ;success
          (throw (Exception. msgErrInsertFailed))
        )
))


(defn insert-all 
  [table records] 
  ( do-func-with-all table records insert)
  )

(defn del-record
  [table record]
  (when (cond 
          (nil? (db table)) (throw (Exception. msgErrTableNameNotExistsDel))
          (not (in? ((db table) :data) record)) (throw (Exception. msgErrRecordNotExistsDel))
          :else
          (let [i (index-of record ((db table) :data))] 
            (dosync (alter db assoc-in [table :data] 
                           (if (= i 0) (subvec ((db table) :data) 1)
                                 (vec (concat 
                                  (subvec ((db table) :data) 0 (- i 1)) 
                                  (subvec ((db table) :data) (+ i 1)))) ))
          
          )
    ))))
  
