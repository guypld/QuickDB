
(ns tests.executeFile 
  (:use [core.core] [clojure.test] [core.printDB] [core.query] [utils.utils] )
  )

;values for "books" table
(def bookfields '("id" "name" "year"))
(def bookkeys '("id"))

;values for "person" table
(def personfields1 '("id" "name" "age"))
(def personKeys '("id" "age"))

;values for "movies" table
(def moviesFields '("number" "name" "year" "actor" "director"))
(def moviesKeys '("name" "year"))

;dummy keys for see exception
(def dummyKeys '("phone"))

;records for insert
;books
(def books-r1 {"id" 5 "name" "book" "year" 1999})
(def books-r2 {"id" 2 "name" "English" "year" 2002})
(def books-r3 {"id" 2 "name" "Hebrew" "year" 2013})
(def books-r4 {"id" 1 "name" "Hebrew" "year" 2013})

;person
(def person-r1 {"id" 80 "name" "Moshe" "age" 26})
(def person-r2 {"id" 2 "name" "Dror" "age" 20})
(def person-r3 {"id" 11 "name" "Miri" "age" 26})
(def person-r4 {"id" 22 "name" "David" "age" 31})
(def person-r5 {"id" 33 "name" "Eli" "age" 27})
(def person-r6 {"id" 44 "name" "Avraham" "age" 80})
 
;movies
(def movies-r1 {"number" 1 "name" "Super-Man" "year" 1990 "actor" "Avi" "director" "Pini"})
(def movies-r2 {"number" 2 "name" "Bat-Man" "year" 1990 "actor" "sivan" "director" "Pini"})
(def movies-r3 {"number" 3 "name" "Fire" "year" 2005 "actor" "Avi" "director" "Gabi"})
(def movies-r4 {"number" 4 "name" "Oliver-Twist" "year" 2013 "actor" "Yoav" "director" "Gabi"})
(def movies-r5 {"number" 5 "name" "30 years" "year" 1999 "actor" "Guy" "director" "Pini"})
(def movies-r6 {"number" 6 "name" "40 years" "year" 1880 "actor" "Ron" "director" "Gabi"})
(def movies-r7 {"number" 7 "name" "Clojure" "year" 2009 "actor" "Moshe" "director" "Pini"})


;create-table
(print-title "Create")
(execute ["create table" "books" bookfields dummyKeys]) ;fail- key doesn't wxist in fields
(execute ["create table" "books" bookfields bookkeys]) ;success
(execute ["create table" "books" personfields1 personKeys]) ;fail- table name exists
(execute ["create table" "person" personfields1 personKeys]) ;success
(execute ["create table" "movies" moviesFields moviesKeys]) ;success

;insert record
(print-title "Insert")
(insert-all "movies" [movies-r1 movies-r2 movies-r3 movies-r4 movies-r5 movies-r6 movies-r7])
(execute ["insert" "books" books-r2]) ;success
(execute ["insert" "ABCDE" books-r2]) ;failed- table not exsists
(execute ["insert" "books" books-r2]) ;failed- already exsits
(execute ["insert" "books" books-r1]) ;success
(execute ["insert" "person" books-r1]) ;faild- doesn't have all keys
(execute ["insert" "person" person-r1]) ;success
(execute ["insert" "person" person-r2]) ;success
(print-db db)

;drop table
(print-title "Drop")
(execute ["drop" "books"])
(print-db db)

;select
(print-title "Query 1")
(print-table-return (execute ["select" "books" ["id"]]))
(print-title "Query 2")
(print-table-return (execute [ "select" (execute ["select where" "movies" ["number" "actor" "year"]]) ["year" "actor"]]))

;insert
 (execute ["insert" "books" {"id" 80 "name" "Moshe" "year" 26 }])

;more select and where
(print-title "Query 3")
 (print-table-return (execute ["select where" "books" ["id"] {"id" >} {"id" 3}]))
 (print-title "Query 4")
 (print-table-return (execute ["select" "books" ["id"]]))
(print-db db) 

;update records
(print-title "Update")
(execute ["update" "books" books-r2 books-r3]) ;success
(execute ["update" "boo" books-r2 books-r3]) ;not exists table
(execute ["update" "books" books-r2 books-r1]) ; books-r2 not exsits
(execute ["update" "books" books-r3 books-r1]) ;not same keys
(execute ["update" "books" books-r2 books-r4]) ;not same keys
(print-db db) 

;delete records
(print-title "Delete")
(execute ["delete" "books" books-r2]) ;faild- not exists
(execute ["delete" "books" books-r3]) ;ok
(execute ["delete" "books" books-r1]) ;ok
(print-db db) 