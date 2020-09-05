(ns re-replace)

(defn Example []
  (def web-info (slurp "http://www.clojure.org"))
  (def pat (re-pattern "\\d+"))
  (def newweb-info (clojure.string/replace web-info #"[aeiou]" "###"))
  (def reweb-info (clojure.string/replace web-info pat "???"))
  (println web-info)
  (println newweb-info)
  (println reweb-info))
(Example)
