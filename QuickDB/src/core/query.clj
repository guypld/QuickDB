(ns core.query
  ( :use [core.core])
  ( :use [utils.utils])
  ( :use [utils.constants])
  (:import [javax.swing JOptionPane]))

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

; Execute - The user interface with QuickDB Data Base
(defn execute 
  "Execute QuickDB Data Base Commands.
   Get Vector of arguments.
   The first argument is the command represented by a string.
   Ex: (execute [\"drop\" \"table-name\"])
   Commands:
   drop , delete , insert , select , create table , select where"
  [args-vec]
  (try
    (let [com (first args-vec)
          table-name (second args-vec)
          arg3 (get args-vec 2)
          arg4 (get args-vec 3)
          arg5 (get args-vec 4)
          ]
    (cond 
      ;DROP - delete one table from DB
      (= com "drop") (when (= 
                               (JOptionPane/showConfirmDialog nil 
                                                              (str "Are you sure you want to delete the table "
                                                                   table-name " ?") 
                                                              (str "Drop Table - " strAppName) 
                                                              JOptionPane/YES_NO_OPTION) 
                               JOptionPane/YES_OPTION)
                         (drop-table table-name) )
      ;DELETE - delete given record from table
      (= com "delete") (when (= 
                               (JOptionPane/showConfirmDialog nil 
                                                              (str "Are you sure you want to delete the record from "
                                                                   table-name " ?") 
                                                              (str "Delete Record - " strAppName) 
                                                              JOptionPane/YES_NO_CANCEL_OPTION) 
                               JOptionPane/YES_OPTION)
                         (del-record table-name arg3))
      ;INSERT - insert given record to a table
      (= com "insert") (insert table-name arg3)
      
      ;SELEST - select only given cols from table (instead all cols), returns all the recordsfrom table!
      (= com "select") (select arg3 table-name)
      ;CREATE TABLE - create new empty table
      (= com "create table") (create-table table-name arg3 arg4)
      ;SELECT WHERE - get only records that matched some condition, and select only wanted cols
      (= com "select where") (select arg3 (where table-name arg4 arg5) )  
      :else (println msgErrInvalidCommand )
      ))
    (catch ArithmeticException e
      (println "Exception message: " (.getMessage e)))
    )
  )
 
