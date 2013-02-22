(ns core.core 
  (:use (core.printDB))
  (:use (utils.constants))
  (:use (utils.utils))
  )



;db with data for tests
(def db(ref {}))

(defn create-table 
  [tableName fields keys]
  (cond
    (not (nil? (db tableName))) (println utils.constants/msgErrTableNameExists)
    (check-record-validation keys fields valid-cols)
    (doall [
            (dosync (alter db assoc tableName {:keys keys :cols fields :data {}}))
            (println utils.constants/msgCreateTableSuccess)
            ]
      )
  :else (println utils.constants/msgErrKeyNotInFields)
  ))

(defn dropTable 
  [name] 
  (dosync
    (alter db dissoc name)))

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
    ) )

;get key and record and return true if the key is found in the record
(defn valid-key
  "get key and record and return true if the key is found in the record"
  [key record] 
  (not (nil? (record key)))
  )


(defn valid-cols 
  [record cols] 
  (in?  cols  record))

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
  (not(check-record-validation ((db table) :keys) newRecord  valid-key))
  (println utils.constants/msgErrInvalidKey)
(not(check-record-validation (keys newRecord) ((db table) :cols) valid-cols ))
(println utils.constants/msgErrInvalidfield)
:else (doall[
             (add-record table newRecord)
             (println utils.constants/msgInsrtRecSuccess)
             ]
        )
))


(def bookfields '("id" "name" "year"))
(def bookkeys '("id"))
(def personfields '("id" "name" "age"))
(def personKeys '("id" "age"))
(def dummyKeys '("phone"))

(def r1 {"id" 5 "name" "book" "year" 1999})
(def r2 {"id" 2 "name" "english" "year" 2002})
(def r3 {"id" 80 "name" "Moshe" "age" 26})
(def r4 {"id" 123 "name" "Dror" "age" 20})

(create-table "books" bookfields dummyKeys)
(create-table "books" bookfields bookKeys)
(create-table "books" personfields personKeys)
(create-table "person" personfields personKeys)

(insert "books" r1)
(insert "books" r2)
(insert "books" r1)
(insert "person" r1)
(insert "person" r3)
(insert "person" r4)
(core.printDB/print-db)