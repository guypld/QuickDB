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
