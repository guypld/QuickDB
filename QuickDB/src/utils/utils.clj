(ns utils.utils)

;util func- get element and collection and 
;return true if the collection contains the element
(defn in? 
  "true if seq contains elm"
  [seq elm]  
  (some #(= elm %) seq))

;util func- check if two maps conatains the same keys
(defn is-map-matched? 
  "Check if 2 maps share the same keys."
  [m1 m2]
  (every? true? 
          (map = (keys m1) (keys m2)))
 )

(defn do-func-with-all 
  [data coll func] 
  ( when-not (nil? coll) 
    (do 
      (func data (first coll))
      (do-func-with-all data (next coll) func)) 
    ))

;used by where function
(defn match-all-keys
  "Gets Source Rec & Target Rec and check if i matched
   accordinf to a ConditionList that is a map that contains
   keys from the Source Rec.
   Ex: (def s {:id 10})
       (def t {:id 5 })
       (def c {:id > })
   ( match-all-keys (keys c) c t s )
   will check if :id in `s` is bigger than 5"
     [k-list cond-list source-rec target-rec]
     (let [k (first k-list)]
       (if-not (nil? k)     ;as long as we have keys to check
         (and 
           ( (cond-list k) (source-rec k) (target-rec k) ) ; check if key match
           (recur (rest k-list) cond-list source-rec target-rec) ) ;call with next key
         true ))) ;return true if no keys left


;used by select
(defn where
  "Iterate over a table (Record by Record), 
   and return a Table Format with every record that match the condition list.
   The Condition List is a map with KEYS from the table 
   and a Bool Function for each key.
   Example: (def cond-list   {id: =  , :age > })
            (def target-rec {id: 11 , :age 53}) "
  [table cond-list target-rec]
  ( if (check-record-validation (keys cond-list) (table :cols) valid-cols?)
    ( let [records (table :data) ;Get All Table Records 
           new-records (ref []) ]      ;this vec will save matched records
      (do
        (doseq [rec records]       ;loop over all table and collect all matched records
          (if (match-all-keys (keys cond-list) cond-list rec target-rec)
            (dosync (alter new-records conj rec))))
        {:keys (table :keys) ; RETUEN the Table (TODO:Check!!)
         :cols (table :fields)  
         :data new-records}
        )
      )
    )
  )



(defn index-of 
  [e coll] 
  (first (keep-indexed #(if (= e %2) %1) coll)))
