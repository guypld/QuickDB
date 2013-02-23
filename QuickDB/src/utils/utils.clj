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
