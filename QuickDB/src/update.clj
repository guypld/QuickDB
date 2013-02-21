(ns finalProject.finalProject)


(def a "kjkjk")

(def db (ref {}))


(defn dropTable 
  [name] 
  (dosync
    (alter db dissoc name)))

;(defn updateTable [tableName newVec] (dosync (alter db assoc-in  tableName newVec)))

(defn checkValidation 
  [list1 list2 func msg] 
  (if (nil? list1) 
    true 
    (if (func (first list1) list2) 
      (checkValidation (next list1) list2 func msg) 
      (println msg)) ))

(defn checkAllKeysExists 
  [list1 list2 func msg] 
  (if (nil? list1) 
    true
    (if(= (second (first list1)) 1)
      (if (func (first list1) list2) 
        (checkValidation (next list1) list2 func msg) 
        (println msg)) 
      (checkValidation (next list1) list2 func msg))) )

(defn validKeys 
  [key keyList] 
  (let [x ((first key) keyList)] 
    (if (= x 1) 
      (not(nil? (second key))) 
      true)))

(defn validFileds 
  [field fieldsList] 
  (not (nil? (fieldsList (first field)))) )

(defn insert 
  [tableName newMap] 
  (dosync 
    (alter db assoc tableName 
           (conj (db tableName) newMap))))

(defn findKey 
  [key list] 
  (if (= key (first list)) 
    true (if(nil? list) 
           false (findKey key (next list)))))

(defn createTable 
  [arg keys] 
  (if (nil? arg) 
    nil 
    (merge {(first arg)  
            (if (findKey (first arg) keys) 1 0)} 
           (createTable (next arg) keys))))

(defn addTable 
  [tableName fields keys] 
  (if (nil? (db tableName))  
    (dosync (alter db assoc  tableName [(createTable fields keys)]))
    (printf("The table is already exists")) ))

(defn addStruct [tableName fields] (if (nil? (db tableName))  (defstruct tableName fields ) (printf("The table is already exists")) ))

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
