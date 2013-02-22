(ns utils.utils)

;util func- get element and collection and 
;return true if the collection contains the element
(defn in? 
  "true if seq contains elm"
  [seq elm]  
  (some #(= elm %) seq))