(ns core.query)

(defn findRecords 
  [fields tableData] 
  (if (nil? tableData) nil 
    (conj (findRecords fields (next tableData))  (creatRec fields (first tableData)) )
    ))

(defn select 
  [fields table]  
  (findRecords 'fields ((db 'table) :data)))


(defn creatRec 
  [fields record] 
  (if (nil? fields) nil 
    (merge {(first fields) (record (first fields)) } (creatRec (next fields) record))
    ))
