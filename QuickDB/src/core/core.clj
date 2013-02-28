(ns core.core 
  (:use [core.printDB] [utils.constants] [utils.utils] )
  )

;db reference to save the data base
(def db(ref {}))

;high order function- get 2 collections and function. 
;call the function with the first element in the 
;first collection and with the second collection   
(defn- check-record-validation 
  "Call the function with the first element in the 
   first collection and with the second collection"
  [checkList baseList func] 
  (cond 
    (empty? checkList) true 
    (func 
      (first checkList) baseList) 
    (check-record-validation (next checkList) baseList func) 
    :else false
    ))

;Get Table Name + Fields map + Keys
;check if the table name doesnwt exist in DB
;and if all keys exsist in the field list
;return true if and the table add successfuly ti DB
(defn create-table
  "Create new empty table in db.
   Get Table Name + Fields map + Keys
   check if the table name exist in DB
   and if all keys exist in the field list"
  [tableName fields keys]
  (cond
    ;if table name already exsist in DB
    (not (nil? (db tableName))) (throw (Exception.  msgErrTableNameExists))
    ;if all the keys are valid, add the table
    (check-record-validation keys fields in?) (dosync (alter db assoc tableName {:keys keys :cols fields :data []}))
    ; otherwise
    :else (throw (Exception.  msgErrKeyNotInFields))
       )
      true) ;if didn't throw exception return true

;Remove table from DB by Table Name
;if the table exists in DB
(defn drop-table 
  "Remove table from DB by Table Name"
  [name] 
  ; table not exist
  (if (nil? (db name))  (throw (Exception.  msgErrTableNameNotExistsDrop))
    ;else- drop table
    (dosync(alter db dissoc name))  
    )
  true) ;return true if success


;get table name and new record and insert the recond to the table
(defn- add-record 
  "insert the new record to the table"
  [table newRecord]  
  (let [t ((db table) :data)] 
     (dosync (alter db assoc-in [table :data] (conj t newRecord))))
  )

;get table (map) , conditions list and record to compareson
;used by select
(defn where
  "Iterate over a table (Record by Record), 
   and return a Table Format with every record that match the condition list.
   The Condition List is a map with KEYS from the table 
   and a Bool Function for each key.
   Example: (def cond-list   {id: =  , :age > })
            (def target-rec {id: 11 , :age 53}) "
  [table cond-list target-rec]
  ( when (check-record-validation (keys cond-list) (table :cols) in?)
    ( let [records (table :data) ;Get All Table Records 
           new-records (ref []) ]      ;this vec will save matched records
      (do
        (doseq [rec records]       ;loop over all table and collect all matched records
          (if (match-all-keys (keys cond-list) cond-list rec target-rec)
            (dosync (alter new-records conj  rec)))))
        {:keys (table :keys) ; return the Table
         :cols (table :cols)  
         :data @new-records}
        )))

;get record
;create map of functions for where quary (cond-list)
; return map (cond list)
(defn- create-cond-list 
  "create map of functions for where quary (cond-list)
return map (cond-list)"
  [keys] 
  (when-not (empty? keys) ;if nor empty 
    (merge 
      {(first keys) =} 
      (create-cond-list (next keys))))
  ) 

;get table name and record
;check if other record not exsist in DB with some keys
;return true / false
;use where function
(defn- record-exists? 
  "get table name and record
check if other record not exsist in DB with some keys
return true / false"
  [table record]
  (let [cond-list (create-cond-list ((db table) :keys))]
    (not (empty? 
           ((where (db table) (create-cond-list ((db table) :keys)) record) :data)))  
    ))

;Insert new record to exist table
;get table name and new record. check record column name and keys validation.
;if the record is correct, add it to the table 
;return true if success
(defn insert
  "get table name and new record. check record column name and keys validation.
if the record is correct, add it to the table "
  [table newRecord] 
(cond
  ; Invalid Table Name
  (nil? (db table)) 
  (throw (Exception.  msgErrTableNameNotExists))
  ; Invalid Keys
  (not(check-record-validation ((db table) :keys) newRecord  #(not (nil? (%2 %1)))))
  (throw (Exception.  msgErrInvalidKey))
  ; Invalid Feilds
  (not(check-record-validation (keys newRecord) ((db table) :cols) in? ))
  (throw (Exception.  msgErrInvalidfield))
  (record-exists? table newRecord); Key already exist
  (throw (Exception.  msgErrRecordExists));   
  ; else - OK
  :else (if (nil? (add-record table newRecord)) ; try to insert
          (throw (Exception.  msgErrInsertFailed))
        )
  )true);success

;get table name and vector of record
;use insert function to insert each record
;use high order function (from utils.utils)
(defn insert-all
  "get table name and vector of record
use insert function to insert each record"
  [table records] 
  ( do-func-with-all table records insert)
  )

;get record and table name and delete the record
;check if the record exsist in table
;save the new table in DB
;return true if success
(defn del-record
  "get record and table name and delete the record
check if the record exsist in table
save the new table in DB
return true if success"
  [table record]
  (cond 
    ;if table not in DB
    (nil? (db table)) (throw (Exception.  msgErrTableNameNotExistsDel))
    ;if record not in table
    (not (in? record ((db table) :data))) (throw (Exception.   msgErrRecordNotExistsDel))
    ;else- delete the record
    :else
    (let [i (index-of record ((db table) :data))]
      ;update the table data
      (dosync (alter db assoc-in [table :data]
                     ;if the record to delete is the first record in data vector
                     (if (= i 0) (subvec ((db table) :data) 1)
                       ;else (it is not the first record in data vector)
                       (vec (concat 
                              (subvec ((db table) :data) 0 (- i 1)) 
                              (subvec ((db table) :data) (+ i 1)))) ))
        )
      ))true)
  
;get record and list of relevante fields to maping
;return new map with the relevnt fields
;used by find-record for select routine 
(defn- creat-rec 
  "create new record with all fields in field list"
  [fields record] 
  (if (nil? fields) nil 
    (merge {(first fields) (record (first fields)) } (creat-rec (next fields) record))
    ))

;get table and relevant fields list
;create vector of new record
;use creat-rec
;used by select
(defn- find-records 
  "if table is empty return empty vector
return vector of records with only the wanted fiels"
  [fields tableData] 
  (if (nil? tableData) [] 
    (conj (find-records fields (next tableData))  (creat-rec fields (first tableData)) )
    ))

;select query
;use find-record function
(defn select
  "get table name and wantwd fields
return new table with relevant data and relevant columns"
  [fields table]
  (when (not-empty table)(merge {:keys []
    :cols fields}
  { :data (find-records fields (table :data) )}
  )))

;get table name, old record and new record
;repalce the old record with the new record
;use insert and del-rec function
;use also match-all-keys
(defn update-record 
  "replace the old record with new record
in table, if the new record is valid and old record 
exists. check if old record and new record
share the same keys"
  [table old-rec new-rec] 
  (let [t (db table)]
    (cond
    ;if table not exists
    (nil? t) (throw (Exception. msgErrUpdateTableNotExists))
    ;if old record not exists
    (not (in? old-rec (t :data))) (throw (Exception. msgErrUpdateRecordNotExists))
    ;if new- record not contain all keys
    (not(check-record-validation (t :keys) new-rec  #(not (nil? (%2 %1)))))
    (throw (Exception.  msgErrInvalidKeyUpdate))
    ;if new- record not contain corrrect fields
    (not(check-record-validation (keys new-rec) ((db table) :cols) in? ))
    (throw (Exception.  msgErrInvalidfield))
    ;if new record and old record dont have the same keys
    (not (match-all-keys (t :keys) (create-cond-list (t :keys)) old-rec new-rec ))
    (throw (Exception.  msgErrInvalidKeyUpdate))
    ;else insert the new record
    :else (do
            (del-record table old-rec)
            (insert table new-rec)
            )
    ))true)
