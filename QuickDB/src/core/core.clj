(ns core.core 
  ;(:use [core.printDB])
  ;(:use (utils.constants))
  ;(:use (utils.utils))
  )


;db with data for tests
(def db(ref {:books {:keys ["id"] :cols ["id" "name" "year"] :data [{"id" 5 "name" "book" "year" 1999}
                                                                    {"id" 2 "name" "english" "year" 2002}
                                                                    ]}
             :person {:keys ["name"] :cols ["id" "name" "age"] :data [{"id" 80 "name" "Moshe" "age" 26}
                                                                    {"id" 123 "name" "Dror" "year" 20}
                                                                    ]}}))

;high order function- get 2 collections and function. 
;call the function with the first element in the 
;first collection and with the second collection   
(defn check-record-validation 
  "call the function with the first element in the 
first collection and with the second collection"
  [checkList baseList func msg] 
  (cond 
    (nil? checkList) true 
    (func (first checkList) baseList) (check-record-validation (next checkList) baseList func) 
    :else (doall [
                  (println msg) 
                  (false)
                  ]) ))

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
(if (and
  (check-record-validation ((db table) :keys) newRecord  valid-key utils.constants/msgErrInvalidKey)
(check-record-validation (keys newRecord) ((db table) :cols) valid-cols utils.constants/msgErrInvalidfield))
(add-record table newRecord)
))

