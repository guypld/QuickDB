(ns core.core 
  (:use [core.printDB] [utils.constants] [utils.utils] )
  )

;db reference to save the data base
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
    (not (nil? (db tableName))) (throw (Exception.  msgErrTableNameExists))
    (check-record-validation keys fields valid-cols?) (dosync (alter db assoc tableName {:keys keys :cols fields :data []}))  
    :else (throw (Exception.  msgErrKeyNotInFields))
       )
      true)

(defn drop-table 
  "Remove table from DB by Table Name"
  [name] 
  (cond (nil? (db name))  (throw (Exception.  msgErrTableNameNotExistsDrop))   ; table not exist
    :else (dosync(alter db dissoc name))  ;drop table
    )
  true)


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
  (throw (Exception.  msgErrTableNameNotExists))
  ; Invalid Keys
  (not(check-record-validation ((db table) :keys) newRecord  valid-key?))
  (throw (Exception.  msgErrInvalidKey))
  ; Invalid Feilds
  (not(check-record-validation (keys newRecord) ((db table) :cols) valid-cols? ))
  (throw (Exception.  msgErrInvalidfield))
  ; Key already exist
  ;   TODO!
  ; OK
  :else (if (nil? (add-record table newRecord)) ; try to insert
          (throw (Exception.  msgErrInsertFailed))
        )
  )true);success


(defn insert-all 
  [table records] 
  ( do-func-with-all table records insert)
  )

(defn del-record
  [table record]
  (when (cond 
          (nil? (db table)) (throw (Exception.  msgErrTableNameNotExistsDel))
          (not (in? ((db table) :data) record)) (throw (Exception.   msgErrRecordNotExistsDel))
          :else
          (let [i (index-of record ((db table) :data))] 
            (dosync (alter db assoc-in [table :data] 
                           (if (= i 0) (subvec ((db table) :data) 1)
                                 (vec (concat 
                                  (subvec ((db table) :data) 0 (- i 1)) 
                                  (subvec ((db table) :data) (+ i 1)))) ))
          
          )
    )))true)
  
(defn creatRec 
  [fields record] 
  (if (nil? fields) nil 
    (merge {(first fields) (record (first fields)) } (creatRec (next fields) record))
    ))

(defn findRecords 
  [fields tableData] 
  (if (nil? tableData) [] 
    (conj (findRecords fields (next tableData))  (creatRec fields (first tableData)) )
    ))

(defn select
  [fields table]
  (merge {:keys []
    :cols fields}
  { :data (findRecords fields ((db table) :data) )}
  ))


;used by select
(defn where
  "Iterate over a table (Record by Record), 
   and return a Table Format with every record that match the condition list.
   The Condition List is a map with KEYS from the table 
   and a Bool Function for each key.
   Example: (def cond-list   {id: =  , :age > })
            (def target-rec {id: 11 , :age 53}) "
  [table cond-list target-rec]
  ( if (check-record-validation (keys cond-list) (table :cols) valid-cols?)
    ( let [records (table :data) ;Get All Table Records 
           new-records (ref []) ]      ;this vec will save matched records
      (do
        (doseq [rec records]       ;loop over all table and collect all matched records
          (if (match-all-keys (keys cond-list) cond-list rec target-rec)
            (dosync (alter new-records conj rec))))
        {:keys (table :keys) ; return the Table
         :cols (table :cols)  
         :data @new-records}
        )
      )
    )
  )
