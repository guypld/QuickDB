
(ns tests.testFile 
  (:use [core.core] [clojure.test] [core.printDB] [core.query] )
  )

; READ !!
; http://nakkaya.com/2009/11/18/unit-testing-in-clojure/ 
;(deftest 

;db with data for test
;(def db(ref {:books {:keys ["id"] :cols ["id" "name" "year"] :data [{"id" 5 "name" "book" "year" 1999}
 ;                                                                   {"id" 2 "name" "english" "year" 2002}
  ;                                                                  ]}
   ;          :person {:keys ["name"] :cols ["id" "name" "age"] :data [{"id" 80 "name" "Moshe" "age" 26}
    ;                                                                {"id" 123 "name" "Dror" "year" 20}
     ;                                                               ]}}))

(def db-test-1(ref {"books" {:keys ["id"] :cols ["id" "name" "year"] :data []}}))
(def db-test-2(ref {"books" {:keys ["id"] :cols ["id" "name" "year"] :data [{"id" 5 "name" "book" "year" 1999}]}}))
(def db-test-3(ref {"books" {:keys ["id"] :cols ["id" "name" "year"] :data [{"id" 5 "name" "book" "year" 1999}
                                                                            {"id" 2 "name" "english" "year" 2002}]}}))
(def db-test-4(ref {}))

(defn test-create-large-db []
  
  )

(deftest test-creation (is (= @db-test-1 @db)))
(deftest test-insert-1 (is (= @db-test-2 @db)))
(deftest test-insert-2 (is (= @db-test-3 @db)))
(deftest test-after-drop (is (= @db-test-4 @db)))
(deftest test-after-del (is (= @db-test-2 @db)))

(def table-name "books")

(deftest test-insert
  (execute ["create table" table-name '("id" "name" "year") '("id")])
  (test-creation)
  (execute ["insert" table-name {"id" 5 "name" "book" "year" 1999}])
  (test-insert-1)
  (execute ["insert" table-name {"id" 2 "name" "english" "year" 2002}])
  (test-insert-2))

(deftest test-insert-delete
  (execute ["create table" table-name '("id" "name" "year") '("id")])
  (test-creation)
  (execute ["insert" table-name {"id" 5 "name" "book" "year" 1999}])
  (test-insert-1)
  (execute ["insert" table-name {"id" 2 "name" "english" "year" 2002}])
  (test-insert-2)
  (execute ["delete" table-name {"id" 2 "name" "english" "year" 2002}])
  (test-after-del)
  )

;drop , delete , insert , select , create table , select where"

;test list of all test cases
(deftest test-list
  ;test insertion of 2 recoreds
  (test-insert)
  (execute ["drop" table-name])
  (test-after-drop)
  
  ;test insertion of 2 recoreds and delete one
  (test-insert-delete)
  (execute ["drop" table-name])
  
  
  
  )


