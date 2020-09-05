(ns regex-test)
(use 'regex)
(use 're-replace)
(use 'clojure.test)

(deftest regextest1
  (is (= ["123" "345" "678"] (regex/details "123-345-678"))))

(deftest regextest2
  (is(not= ["128" "345" "678"] (regex/details "123-345-678"))))

(deftest regextest3
  (is(= nil (re-replace/Example))))


