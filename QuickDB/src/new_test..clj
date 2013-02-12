
(def db (ref #{}))

(defstruct account :id :balance)

; The use of refs and transactions offers a safe multi threads procs
(dosync 
	(alter accounts conj(
		struct account "CLJ" 1000.00)))

; if function is a predicate it should end with ?
(string? "hello")
; string? keyword? symbol?