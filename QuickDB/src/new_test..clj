
(def db (ref #{}))

(defstruct account :id :balance)

; The use of refs and transactions offers a safe multi threads procs
(dosync 
	(alter accounts conj(
		struct account "CLJ" 1000.00)))

; if function is a predicate it should end with ?
(string? "hello")
; string? keyword? symbol?


; we can just call (greeting) -> "hello default"
(defn greeting
	"dsdsdsd"
	( [] (greeting "default"))
	( [username] (str "hello " username)))

; function with varible arity (this can be useful in recursive)
(defn date [person-1 person-2 & chaperones]
	(println person-1 "and" person-2
		"went out with" (count chaperones) "chaperones." ))

;Anonymous functions
(filter (fn [w] (> (count w) 2)) (re-split #"\W+" "A fine day"))
--> ("fine" "day")
;SAME: [% is the 1st param]
(filter #(> (count %) 2) (re-split #"\W+" "A fine day it is"))

; making funcitons (by anon):
(defn make-greeter [greeting-prefix]
	(fn [username] (str greeting-prefix ", " username)))
(def aloha-greeting (make-greeter "Aloha"))
(aloha-greeting "world")
--> "Aloha, world"

; distructring
(let [[x y] [1 2 3]]
	[x y])
--> [1 2]

;if
(defn is-small? [number]
	(if (< number 100) "yes" "no" ))