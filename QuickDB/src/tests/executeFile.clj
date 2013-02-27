
(ns tests.executeFile 
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


;tests
(def bookfields '("id" "name" "year"))
(def bookkeys '("id"))
(def personfields1 '("id" "name" "age"))
(def personKeys '("id" "age"))
(def dummyKeys '("phone"))

(def r1 {"id" 5 "name" "book" "year" 1999})
(def r2 {"id" 2 "name" "English" "year" 2002})
(def r3 {"id" 2 "name" "Hebrew" "year" 2013})
(def r6 {"id" 1 "name" "Hebrew" "year" 2013})
(def r5 {"id" 80 "name" "Moshe" "age" 26})
(def r4 {"id" 2 "name" "Dror" "age" 20})
 
(execute ["create table" "books" bookfields dummyKeys]) ;fail- key doesn't wxist in fields
(execute ["create table" "books" bookfields bookkeys]) ;success
(execute ["create table" "books" personfields1 personKeys]) ;fail- table name exists
(execute ["create table" "person" personfields1 personKeys]) ;success

;(insert-all "books" [r1 r2 r1])
(execute ["insert" "books" r2]) ;success
(execute ["insert" "books" r2]) ;success
(execute ["insert" "books" r1]) ;success
(execute ["insert" "person" r1]) ;feild- doesn't have all keys
(execute ["insert" "person" r5]) ;success
(execute ["insert" "person" r4]) ;success
(print-db db)
(execute ["drop" "books"])
(print-db db)
(print-table-return (execute ["select" "books" ["id"]]))
 (execute ["insert" "books" {"id" 80 "name" "Moshe" "year" 26 }])
 (execute ["select where" "books" ["id"] {"id" >} {"id" 3}])
 (execute ["select" "books" ["id"]])
(print-db db) 
(execute ["update" "books" r2 r3]) ;success
(execute ["update" "boo" r2 r3]) ;not exists table
(execute ["update" "books" r2 r1]) ; r2 not exsits
(execute ["update" "books" r3 r1]) ;not same keys
(execute ["update" "books" r2 r6]) ;not same keys
(print-db db) 