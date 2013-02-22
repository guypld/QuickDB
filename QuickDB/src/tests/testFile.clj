(ns tests.testFile 
  (:use [core.core])
  (:use [core.printDB] )
  )


;tests
(def bookfields '("id" "name" "year"))
(def bookkeys '("id"))
(def personfields1 '("id" "name" "age"))
(def personKeys '("id" "age"))
(def dummyKeys '("phone"))

(def r1 {"id" 5 "name" "book" "year" 1999})
(def r2 {"id" 2 "name" "english" "year" 2002})
(def r5 {"id" 80 "name" "Moshe" "age" 26})
(def r4 {"id" 123 "name" "Dror" "age" 20})

(create-table "books" bookfields dummyKeys)
(create-table "books" bookfields bookkeys)
(create-table "books" personfields1 personKeys)
(create-table "person" personfields1 personKeys)

(insert "books" r1)
(insert "books" r2)
(insert "books" r1)
(insert "person" r1)
(insert "person" r5)
(insert "person" r4)
(print-db db)