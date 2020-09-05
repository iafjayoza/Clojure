(ns map_example)

(def jo {:name "Jay Oza", :address {:zip 365560}})
(get-in jo [:address :zip])
(assoc-in jo [:address :zip] 223840)
(update-in jo [:address :zip] inc)
(get jo :name)
(get-in jo [:address :hobby] "Skatting")