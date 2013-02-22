(ns core.printDB); (:use [core.core :only db]))



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

;print all fieldss in record by the columns order
(defn print-record
  "print all fieldss in record by the columns order"
  [r cols] 
  (let [value (r (first cols))] 
    (if-not (nil? cols) (doall [
                                (printf "%10s" value ) 
                                (print-record  r (next cols))
                                ])  
      (printf "\n")))
  )

;print all records in table
(defn print-all-records
  "print all records in table by the columns order"
  [recordVec cols counter]
  (when-not (nil? recordVec) 
    (doall [
            (printf "%d." counter )
            (print-record (first recordVec) cols) 
            (print-all-records (next recordVec) cols (inc counter) )
            ] )
    ))


;print the table name and table columns names and call print all records
(defn print-table
  "print the table name and table columns names and call print all records"
  [db t] 
  (doall [
          (printf "\nTable name-  %s\n" t)
          (println "==========================")
          (print-columns ((db t) :cols)) 
          (print-separator ((db t) :cols))
          (print-all-records ((db t) :data) ((db t) :cols) 1)
          ])
  )

(defn print-all-tables 
  [db tables] 
  (when (not-empty tables) (doall [
                                   (print-table db (first tables)) 
                                   (print-all-tables db (next tables))
                                   ])
    ))

;print all the tables in DB
(defn print-db
  "print all the tables in DB"
  [db] 
    (print-all-tables @db (keys @db))
    )

;(def a  (ref {"person" {:keys '("id" "age"), :cols '("id" "name" "age"), :data [{"name" "Moshe", "id" 80, "age" 26} {"name" "Dror", "id" 123, "age" 20}]}, "books" {:keys '("id"), :cols '("id" "name" "year"), :data [{"name" "book", "id" 5, "year" 1999} {"name" "english", "id" 2, "year" 2002} {"name" "book", "id" 5, "year" 1999}]}}))

;(def r3 {"id" 80 "name" "Moshe" "age" 26})
;(def personfields '("id" "name" "age"))
