(ns kdp-report.main
  (:require [kdp-report.kdp :refer :all]
            [kdp-report.d2d :refer :all]))

(group-by :Title
  (concat kindle-books d2d-books))