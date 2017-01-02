(ns kdp-report.kdp
  (:require [dk.ative.docjure.spreadsheet :refer :all]
            [clojure.pprint :refer :all] ))

;download 'prior months royalties one by one'

(def filenames
  ["/Users/adrian.osullivan/Dropbox/kdp/kdp-report-1-2016.xls"
   "/Users/adrian.osullivan/Dropbox/kdp/kdp-report-2-2016.xls"
   "/Users/adrian.osullivan/Dropbox/kdp/kdp-report-3-2016.xls"
   "/Users/adrian.osullivan/Dropbox/kdp/kdp-report-4-2016.xls"
   "/Users/adrian.osullivan/Dropbox/kdp/kdp-report-5-2016.xls"
   "/Users/adrian.osullivan/Dropbox/kdp/kdp-report-6-2016.xls"
   "/Users/adrian.osullivan/Dropbox/kdp/kdp-report-7-2016.xls"
   "/Users/adrian.osullivan/Dropbox/kdp/kdp-reports-08-2016-1479749593121-efe96ea153478e0814a09f54e3184f79.xlsx"
   "/Users/adrian.osullivan/Dropbox/kdp/kdp-reports-09-2016-1479749586926-f88c1bff260960ac54d1c5e30e59c834.xlsx"
   "/Users/adrian.osullivan/Dropbox/kdp/kdp-reports-10-2016-1479748641878-49ecf7cf74852ad18001bc12d9788161.xlsx"
   "/Users/adrian.osullivan/Dropbox/kdp/kdp-reports-11-2016-1482242511043-699037402df131d60bfb9ef6023b6d9c.xlsx"])

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

(defn is-royalty-row? [rrow]
  (boolean (re-find #"Total Royalty" (rrow :row-desc))))

(defn is-header-row? [rrow]
  (= "Title" (rrow :row-desc)))

(defn is-nosales-row? [rrow]
  (= "There were no sales during this period" (rrow :row-desc)))

(defn is-report-row? [rrow]
  (boolean
    (re-find #"Sales report for the period" (rrow :row-desc))))

(defn is-currency-row? [rrow]
  (nil? (rrow :row-desc)))

(defn ws [filename]
  (->> (load-workbook filename)
       (select-sheet
         (if (.endsWith filename ".xls") "Reports" "eBook Royalty Report"))))

(defn ws-seq [filename]
  (->>
    (ws filename)
    (select-columns
      {:A :row-desc, :C :asin,  :D :units, :G :royalty, :I :list-price, :M :royalty-paid})
    (remove nil?)
    (filter #(some? (%1 :row-desc)))
    (remove is-report-row?)
    (remove is-header-row?)
    (remove is-royalty-row?)
    (remove is-currency-row?)
    (drop-last 6)))

(def section?
  (comp #(contains? currencies %) :row-desc))

(defn trans-rows [filename]
  (map flatten
     (partition 2
       (partition-by section? (ws-seq filename)))))

(defn replace-first-with-currency [row]
  (assoc row 0 (currencies (:row-desc (first row)))) )

(defn curr-books-map [filename]
  (->> (map #(into [] %) (trans-rows filename))
       (map replace-first-with-currency)))

(defn get-all [filename]
  (->>
    (for [row (curr-books-map filename)
          book (rest row)]
          [(first row) (book :row-desc)])
    (flatten)
    (partition 2)
    (remove #(= "" (second %)))
    (remove #(= "There were no sales during this period" (second %)))))
    ;(filter #(= "Rain of Clarity" (second %))

(def all-kdp (partition 2
                (flatten
                  (map get-all filenames))))

(group-by second all-kdp)



