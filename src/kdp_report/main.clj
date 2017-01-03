(ns kdp-report.main
  (:require [kdp-report.kdp :refer :all]
            [kdp-report.d2d :refer :all]
            [clojure.pprint :refer :all] ))

(def all-books
  (concat kindle-books d2d-books))

(defn books-grouped-by [keys]
  (sort-by #(vec (map % keys))
    (for [book (group-by #(select-keys % keys) all-books)]
      (assoc (first book) :UnitsSold (count (second book))))))

(print-table (books-grouped-by [:Title :Country :Vendor]))

