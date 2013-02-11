
(def db (ref #{}))

(defstruct account :id :balance)

(dosync 
	(alter accounts conj(
		struct account "CLJ" 1000.00)))

