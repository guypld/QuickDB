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
(def db-test-5(ref {"books" {:keys ["id"], :cols ["id" "name" "year"], :data []}}))

(def table-name1 "books")
(def table-name2 "workers")

;create large db to tested on
(defn test-create-large-db []
  (execute ["create table" table-name1 '("id" "name" "year") '("id")])
  (execute ["insert" table-name1 {"id" 1 "name" "book1" "year" 1999}])
  (execute ["insert" table-name1 {"id" 2 "name" "book2" "year" 2000}])
  (execute ["insert" table-name1 {"id" 3 "name" "book3" "year" 2001}])
  (execute ["insert" table-name1 {"id" 4 "name" "book4" "year" 2002}])
  (execute ["insert" table-name1 {"id" 5 "name" "book5" "year" 2003}])
  (execute ["insert" table-name1 {"id" 5 "name" "book6" "year" 2004}])
  (execute ["create table" table-name2 '("id" "name" "last" "sallery") '("id" "name")])
  (execute ["insert" table-name2 {"id" 200012 "name" "ron" "last" "yano" "sallery" 60}])
  (execute ["insert" table-name2 {"id" 400013 "name" "guy" "last" "peled" "sallery" 55}])
  (execute ["insert" table-name2 {"id" 400014 "name" "mosh" "last" "mosh" "sallery" 55}]))

(deftest test-creation (is (= @db-test-1 @db)))
(deftest test-insert-1 (is (= @db-test-2 @db)))
(deftest test-insert-2 (is (= @db-test-3 @db)))
(deftest test-after-drop (is (= @db-test-4 @db)))
(deftest test-after-del (is (= @db-test-5 @db)))

(deftest test-insert
  (execute ["create table" table-name1 '("id" "name" "year") '("id")])
  (test-creation)
  (execute ["insert" table-name1 {"id" 5 "name" "book" "year" 1999}])
  (test-insert-1)
  (execute ["insert" table-name1 {"id" 2 "name" "english" "year" 2002}])
  (test-insert-2))

(deftest test-insert-delete
  (execute ["create table" table-name1 '("id" "name" "year") '("id")])
  (test-creation)
  (execute ["insert" table-name1 {"id" 5 "name" "book" "year" 1999}])
  (test-insert-1)
  (execute ["insert" table-name1 {"id" 2 "name" "english" "year" 2002}])
  (test-insert-2)
  (del-record table-name1 {"id" 2 "name" "english" "year" 2002})
  ;(execute ["delete" table-name1 {"id" 2 "name" "english" "year" 2002}])
  (test-after-del))

(deftest test-select
  (test-create-large-db)
  (def tmp (ref (execute ["select" table-name2 ["id" "sallery"]])))
  (def tmp-select (ref {:data [{"sallery" 55, "id" 400014}
                               {"sallery" 55, "id" 400013}
                               {"sallery" 60, "id" 200012}]
                        :keys []
                        :cols ["id" "sallery"]}))
  (is (= @tmp-select @tmp))
  )
  
;drop , delete , insert , select , create table , select where"

;test list of all test cases
(deftest test-list
  ;test insertion of 2 recoreds
  (test-insert)
  (drop-table table-name1)
  (test-after-drop)
  ;test insertion of 2 recoreds and delete one
  (test-insert-delete)
  (drop-table table-name1)
  ;test select
  (test-select)
  (drop-table table-name2)
  (drop-table table-name1)
  (test-after-drop)
  )


