(ns core.printDB (:require (core.core )))


(defn print-db 
  [] 
  (let [tables (keys @db)] 
    (map print-table tables)
    ))

(defn print-all-records 
  [recordVec cols counter] 
  (when-not (nil? recordVec) 
    (doall [
            (print-record (first recordVec) cols counter) 
            (print-all-records (next recordVec) cols (inc counter))
            ] )
    ))

(defn print-table 
  [t] 
  (doall [
          (printf "\nTable name-  %s\n" t)
          (println "==========================")
          (print-columns ((db t) :cols)) 
          (print-separator ((db t) :cols))
          (print-all-records ((db t) :data) ((db t) :cols) 1)
          ])
  )


(defn print-separator 
  [cols] 
  (if-not (nil? cols) 
    (doall [
            (printf "%10s" "--------" ) 
            (print-separator  (next cols))
            ])  
      (printf "\n"))
  )


(defn print-columns 
  [cols] 
  (if-not (nil? cols) 
    (doall [
            (printf "%10s" (first cols) ) 
            (print-columns  (next cols))
            ])  
      (printf "\n"))
  )



(defn print-record
  [r cols counter] 
  (let [value (r (first cols))] 
    (if-not (nil? cols) (doall [(printf "%d.%10s" counter value ) (print-records  r (next cols))])  
      (printf "\n")))
  )
