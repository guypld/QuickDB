(ns ns-test)

(def db {})

(defn dropTable [name] (def db (dissoc db name)))

(defn updateTable [tableName newVec] (def db (assoc-in db [tableName] newVec)))

(defn insert [tableName newMap] (updateTable tableName (conj (db tableName) newMap)))

(defn findKey [key list] (if (= key (first list)) true (if(nil? list) false (findKey key (next list)))))

(defn createTable [arg keys] (if (nil? arg) nil (merge {(first arg)  (if (findKey (first arg) keys) 1 0)} (createTable (next arg) keys))))
(defn addTable [tableName fields keys] (if (nil? (db tableName))  (def db (assoc db tableName [(createTable fields keys)])) (printf("The table is already exists")) ))

(def tableField1 (list :id :name :address :phone :age :weight :high))
(def tableField2 (list :name :salary :start :end))

(def keys1 (list :id :age))
(def keys2 (list :name :end))

(addTable :person tableField1 keys1)
(addTable :employee tableField2 keys2)

(def record1t1 {:name "Moshe" :id 1234 :address "tt" :phone 33 :age 26 :weight 80 :high 170})
(def record2t1 {:name "Alon" :id 4312 :address "tt" :phone 33 :age 26 :weight 80 :high 170})
(def record3t1 {:name "Ron" :id 4321 :address "tt" :phone 33 :age 26 :weight 80 :high 170})
(def record4t1 {:name "guy" :id 5532 :address "tt" :phone 33 :age 26 :weight 80 :high 170})

(insert :person record1t1)
(insert :person record2t1)
(insert :person record3t1)
(insert :person record4t1)

(def record1t2 {:name "Miri" :salary 1234 :start 11 :end 33})
(def record2t2 {:name "Tomer" :salary 1114 :start 11 :end 33})
(def record3t2 {:name "Eden" :salary 224 :start 13 :end 33})
(def record4t2 {:name "Roni" :salary 994 :start 10 :end 33})

(insert :employee record1t2)
(insert :employee record2t2)
(insert :employee record3t2)
(insert :employee record4t2)
