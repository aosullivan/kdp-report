(ns kdp-report.core
  (:require [dk.ative.docjure.spreadsheet :refer :all]))


(def filename "/Users/adrian.osullivan/Dropbox/kdp/kdp-report-1-2016.xls")

;date - determined by the filename
;country - tricky

(def currencies {"Amazon Kindle US Store" "USD"
                 "Amazon Kindle UK Store" "GBP"
                 "Amazon Kindle DE Store" "EUR (Germany)"
                 "Amazon Kindle FR Store" "EUR (France)"
                 "Amazon Kindle Japan Store" "JPY"
                 "Amazon Kindle CA Store" "CAD"
                 "Amazon Kindle IT Store" "EUR (Italy)"
                 "Amazon Kindle ES Store" "EUR (Spain)"
                 "Amazon Kindle IN Store" "INR"
                 "Amazon Kindle AU Store" "AUD"
                 "Amazon Kindle NL Store" "EUR (Netherlands)"
                 "Amazon Kindle BR Store" "BRL"
                 "Amazon Kindle MX Store" "MXN"})





;the code...
(defn is-royalty-row? [rrow] (boolean (re-find #"Total Royalty" (rrow :row-desc))))

(defn is-header-row? [rrow] (= "Title" (rrow :row-desc)))

(defn is-nosales-row? [rrow] (= "There were no sales during this period" (rrow :row-desc)))

(defn is-report-row? [rrow] (boolean (re-find #"Sales report for the period" (rrow :row-desc))))

(def ws (->> (load-workbook filename)
             (select-sheet "Reports")))

(def ws-seq
  (drop-last 6
    (remove is-report-row?
      (remove is-header-row?
        (remove is-royalty-row?
          (filter #(some? (%1 :row-desc)) ;filter out if nothing in first col
            (select-columns
              {:A :row-desc, :C :asin,  :D :units, :G :royalty, :I :list-price, :M :royalty-paid} ws)))))))


(def section?
  (comp #(contains? currencies %) :row-desc))

(def trans-rows
  (map flatten
       (partition 2
                  (partition-by  section? ws-seq))))

(defn replace-first-with-currency [row]
  (assoc row 0 (currencies (:row-desc (first row)))) )


(->> (map #(into [] %) trans-rows)
     (map replace-first-with-currency))

(+ 1 2)

