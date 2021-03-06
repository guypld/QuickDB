(ns core.query
  ( :use [core.core] [utils.utils] [utils.constants])
         (:import [javax.swing JOptionPane])
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
      (= com "drop") (if (= 
                               (JOptionPane/showConfirmDialog nil 
                                                              (str "Are you sure you want to delete the table "
                                                                   table-name " ?") 
                                                              (str "Drop Table - " strAppName) 
                                                              JOptionPane/YES_NO_OPTION) 
                               JOptionPane/YES_OPTION)
                        (when (drop-table table-name)  
                          (println msgDropTableSuccess))
                        ;else
                        (println msgDropCancel)
                        )
      ;DELETE - delete given record from table
      (= com "delete") (if (= 
                               (JOptionPane/showConfirmDialog nil 
                                                              (str "Are you sure you want to delete the record from "
                                                                   table-name " ?") 
                                                              (str "Delete Record - " strAppName) 
                                                              JOptionPane/YES_NO_OPTION) 
                               JOptionPane/YES_OPTION)
                        (when (del-record table-name arg3)
                          (println msgDeleteRecSuccess))
                        ;else
                        (println msgDeleteCancel)
                        )
      ;INSERT - insert given record to a table
      (= com "insert") (when (insert table-name arg3)
                         (println msgInsrtRecSuccess))
      
      ;SELEST - select only given cols from table (instead all cols), returns all the recordsfrom table!
      (= com "select") (select arg3 (if (string? table-name) (db table-name) table-name))
      ;CREATE TABLE - create new empty table
      (= com "create table") (when (create-table table-name arg3 arg4)
                               (println msgCreateTableSuccess))
      ;SELECT WHERE - get only records that matched some condition, and select only wanted cols
      (= com "select where") (select arg3 (where (db table-name) arg4 arg5) )
       ;UPDATE - get table name, old-rec and new recors. and update the table
      (= com "update") (when (update-record table-name arg3 arg4)
                         (println msgUpdateRecordSuccess))
      :else (println msgErrInvalidCommand )
      ))
    (catch Exception e
      (println "Exception message: " (.getMessage e)))
    )
  )
 
