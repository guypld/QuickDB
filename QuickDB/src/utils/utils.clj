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

;util func- used by select function.
(defn is-match?
  "Check if some Col(represented by map) is matching to some Target Col.
   This pure finction gets Source Col, and Target Col, and map of functions,
   each funtion to each Col."
  [source-map target-map cond-list]
  (if (nil? cond-list)
    true  ; if condition list empty - return true
    (if (and ; cheak if all maps share same keys
          (is-map-matched? source-map target-map)
	        (is-map-matched? source-map cond-list))
      (let [res true] 
;???      
      (doseq ; run over all keys and evaluate it 
       [keyval cond-list] 
       ; (f l1 l2)
        (let [k (key keyval)] ;k is the curr key from cond-list
         
           ( (cond-list k) (source-map k) (target-map k) )
           
       )))

     ;else
     (println "Not all maps is same")
    )
  )
) 
  
