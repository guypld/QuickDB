(ns core.core 
  (:use [core.printDB] )
  (:use [utils.constants])
  (:use [utils.utils] )
  (:import [javax.swing JOptionPane])
  )

;db reference
(def db(ref {}))

;get key and record and return true if the key is found in the record
(defn valid-key?
  "get key and record and return true if the key is found in the record"
  [key record] 
  (not (nil? (record key)))
  )


(defn valid-cols? 
  [record cols] 
  (in?  cols  record))


;high order function- get 2 collections and function. 
;call the function with the first element in the 
;first collection and with the second collection   
(defn check-record-validation 
  "call the function with the first element in the 
first collection and with the second collection"
  [checkList baseList func] 
  (cond 
    (nil? checkList) true 
    (func (first checkList) baseList) (check-record-validation (next checkList) baseList func) 
    :else false
    ) 
  )

(defn create-table 
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
  [name] 
  (when (= (JOptionPane/showConfirmDialog nil "are you shure?" "delete massage" JOptionPane/YES_NO_CANCEL_OPTION) JOptionPane/YES_OPTION) 
          (dosync
    (alter db dissoc name)))
)


;get table name and new record and insert the recond to the table
(defn add-record [table newRecord] 
  "insert the new record to the table" 
  (let [t ((db table) :data)] 
     (dosync (alter db assoc-in [table :data] (conj t newRecord)))
  ))


;get table name and new record. check record column name and keys validation.
;if the record is correct, add it to the table 
(defn insert
  "get table name and new record. check record column name and keys validation.
if the record is correct, add it to the table "
  [table newRecord] 
(cond
  (nil? (db table)) (println msgErrTableNameNotExists)
  (not(check-record-validation ((db table) :keys) newRecord  valid-key?))
  (println msgErrInvalidKey)
(not(check-record-validation (keys newRecord) ((db table) :cols) valid-cols? ))
(println msgErrInvalidfield)
:else (if-not (nil? (add-record table newRecord))
          (println msgInsrtRecSuccess)
          (println msgErrInsertFailed)
        )
))


(defn insert-all 
  [table records] 
  ( do-func-with-all table records insert)
  )
