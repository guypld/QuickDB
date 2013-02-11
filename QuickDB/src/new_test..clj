
(def db (ref #{}))

(defstruct account :id :balance)

; The use of refs and transactions offers a safe multi threads procs
(dosync 
	(alter accounts conj(
		struct account "CLJ" 1000.00)))

