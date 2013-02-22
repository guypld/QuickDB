(ns core.query)


(def db(ref {:books {:keys ["id"] :cols ["id" "name" "year"] :data [{"id" 5 "name" "book" "year" 1999}
                                                                    {"id" 2 "name" "english" "year" 2002}
                                                                    ]}
             :person {:keys ["name"] :cols ["id" "name" "age"] :data [{"id" 80 "name" "Moshe" "age" 26}
                                                                    {"id" 123 "name" "Dror" "year" 20}
                                                                    ]}}))


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

(def a {"id" 9 "name" "dogs" "year" 2012})
(def f '("id" "year"))


(def record1t1 {:name "Moshe" :id 1234 :address "tt" :phone 33 :age 26 :weight 80 :high 170})
(def record2t1 {:name "Alon" :id 4312 :address "tt" :phone 33 :age 26 :weight 80 :high 170})
(def record1t2 {:name "Miri" :salary 1234 :start 11 :end 33})
(def record2t2 {:name "Tomer" :salary 1114 :start 11 :end 33})