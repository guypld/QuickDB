(ns core.printDB )

;print all the tables in DB
(defn print-db
  "print all the tables in DB"
  [] 
  (let [tables (keys @db)] 
    (map print-table tables)
    ))

;print all records in table
(defn print-all-records
  "print all records in table by the columns order"
  [recordVec cols counter]
  (when-not (nil? recordVec) 
    (doall [
            (print-record (first recordVec) cols counter) 
            (print-all-records (next recordVec) cols (inc counter))
            ] )
    ))

;print the table name and table columns names and call print all records
(defn print-table
  "print the table name and table columns names and call print all records"
  [t] 
  (doall [
          (printf "\nTable name-  %s\n" t)
          (println "==========================")
          (print-columns ((db t) :cols)) 
          (print-separator ((db t) :cols))
          (print-all-records ((db t) :data) ((db t) :cols) 1)
          ])
  )

;print separator line under the columns names
(defn print-separator
  "print separator under the columns names"
  [cols] 
  (if-not (nil? cols) 
    (doall [
            (printf "%10s" "--------" ) 
            (print-separator  (next cols))
            ])  
      (printf "\n"))
  )

;print all columns names in table
(defn print-columns
  "print all columns names in table"
  [cols] 
  (if-not (nil? cols) 
    (doall [
            (printf "%10s" (first cols) ) 
            (print-columns  (next cols))
            ])  
      (printf "\n"))
  )

;print all fieldss in record by the columns order
(defn print-record
  "print all fieldss in record by the columns order"
  [r cols counter] 
  (let [value (r (first cols))] 
    (if-not (nil? cols) (doall [(printf "%d.%10s" counter value ) (print-records  r (next cols))])  
      (printf "\n")))
  )
