(ns core.query
  ( :use [core.core])
  ( :use [utils.utils])
  ( :use [utils.constants]))

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
  { :data (findRecords fields ((db table) :data) )}))


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

;2
;drop-table (tname)
;3
;del-record(t-name,rec)
;insert(t-name,rec)
;select(t-name,fields)
;4
;create-table (t-name, fields, keys)
;5
;select-where (t-name, feilds , cond, target)



(defn execute 
  [args-vec]
  (try
    (let [com (first args-vec)
          table-name (second args-vec)
          arg3 (get args-vec 2)
          arg4 (get args-vec 3)
          arg5 (get args-vec 4)
          ]
    (cond 
      (= com "drop") (drop-table table-name)
      (= com "delete") (del-record table-name arg3)
      (= com "insert") (insert table-name arg3)
      (= com "select") (select arg3 table-name)
      (= com "create table") (create-table table-name arg3 arg4) 
      (= com "select where") (select arg3 (where table-name arg4 arg5) )  
      :else (println msgErrInvalidCommand )
      ))
    (catch Exception e (println e))
    
    )
  
  )
 
