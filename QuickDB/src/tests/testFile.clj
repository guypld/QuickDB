(ns tests.testFile (:use (core [core])))


;tests
(def a {"id" 9 "name" "dogs" "year" 2012})
(def b {"if" 9 "name" "dogs" "year" 2012})
(def c {"id" 9 "name" "dogs" "year" 2012 "if" 2012})

(insert :books a) ;true
(insert :books b) ;false
(insert :books c) ;false

(def bookCol ((db :books) :cols))
