(ns rules
  "Clojure Implementation of data transformation using Excel Sheets"
  (:require [dk.ative.docjure.spreadsheet :refer :all]))

;; Defining an atom
(def resultdata (atom {}))
(def lookupdata (atom {}))

;; Reading the data of document spreadsheet
(defn read-documents []
  (->>
    (load-workbook "Member - CE Problem-1 Sample.xlsx")
    (select-sheet "Erp Documents")
    (select-columns {:A :bill-number :B :document-number :C :assignment-number :D :customer-code :E :customer-name
                     :F :type :G :doc-date :H :due-date :I :amount})))

;; Reading the data of lookup spreadsheet
(defn read-lookup []
  (->>
    (load-workbook "Member - CE Problem-1 Sample.xlsx")
    (select-sheet "Erp Lookup")
    (select-columns {:A :bill-number :B :document-number :C :assignment-number :D :customer-code :E :customer-name
                     :F :type :G :field1 :H :field2 :I :field3 :J :field4})))

;;;;;;;;;;;;;;;;;;;;
;;Helper functions
;;;;;;;;;;;;;;;;;;;;

(defn insert-lookup-data [ref_id row]
  (swap! lookupdata assoc (keyword ref_id) {:bill-number (:bill-number row) :document-number (:document-number row) :assignment-number (:assignment-number row)
                                            :customer-code (:customer-code row) :customer-name (:customer-name row) :type (:type row)
                                            :field1 (:field1 row) :field2 (:field2 row) :field3 (:field3 row) :field4 (:field4 row)}))

(defn insert-result-data [ref_id document_id row]
  (let [lookup-data ((keyword ref_id) @lookupdata)
        doc-type (if (some? (:field1 lookup-data))
                   (cond (clojure.string/includes? (:field1 lookup-data) "debitmemo") "DEBIT_MEMO"
                         (clojure.string/includes? (:field1 lookup-data) "debitnote") "DEBIT_NOTE"
                         :else "INVOICE")
                   "INVOICE")
        doc-number (cond (some? (:document-number row)) (:document-number row)
                         (some? (:bill-number row)) (:bill-number row)
                         (some? (:assignment-number row)) (:assignment-number row))]
    (swap! resultdata assoc (keyword ref_id) {:doc_id document_id  :document-number doc-number :type (:type row)
                                          :doc-type doc-type :customer-code (:customer-code row) :customer-name (:customer-name row)
                                          :doc-date (:doc-date row) :due-date (:due-date row) :amount (:amount row)
                                          :balance (:amount row) :field1 (:field1 lookup-data) :field2 (:field2 lookup-data)
                                          :field3 (:field3 lookup-data) :field4 (:field4 lookup-data)})))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Data transformation functions
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Function process and inserts lookup data into atom
(defn lookup-data []
  (doseq [row (rest (read-lookup))]
    (let [ref_id (str (:bill-number row) "-" (:document-number row) "-" (:assignment-number row) "-" (:customer-code row))]
      (insert-lookup-data ref_id row))))

(lookup-data)

;; Function process and inserts final data into atom
(defn read-data []
  (doseq [row (read-documents)]
    (let [ref_id (str (:bill-number row) "-" (:document-number row) "-" (:assignment-number row) "-" (:customer-code row))
          document_id (str (:bill-number row) "-" (:document-number row) "-" (:assignment-number row) "-" (:customer-code row) "-" (:type row))]
      (cond (= (:type row) "Debit")
            (if (contains? @resultdata (keyword ref_id))
              (do (swap! resultdata update-in [(keyword ref_id) :amount] + (:amount row))
                  (swap! resultdata update-in [(keyword ref_id) :balance] + (:amount row)))
              (insert-result-data ref_id document_id row))
            (= (:type row) "Credit")
            (when (contains? @resultdata (keyword ref_id))
              (swap! resultdata update-in [(keyword ref_id) :balance] - (:amount row)))))))

(read-data)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Converting the final data into Excel
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Processing the vales of final result data to convert in Excel
(def outcome (map #(vector (:doc_id %) (:document-number %) (:type %) (:doc-type %) (:customer-code %)
                           (:customer-name %) (:doc-date %) (:due-date %) (:amount %) (:balance %)
                           (:field1 %) (:field2 %) (:field3 %) (:field4 %))
                  (vals @resultdata)))


;;; Create a spreadsheet and save it
(let [wb (create-workbook "Result Sheet" (cons ["Doc ID" "Doc Number" "Type" "Doc Type" "Customer Code" "Customer Name"
                                                "Doc Date" "Due Date" "Amount" "Balance" "Field1" "Field2" "Field3"  "Field4"]
                                           outcome))]
  (save-workbook! "result.xlsx" wb))