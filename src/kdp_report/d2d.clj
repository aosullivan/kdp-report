(ns kdp-report.d2d
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [clojure.pprint :refer :all]))

; go to statements -> downloads and download each 'sales report'
; or just do raw sales data by month

(def filenames
  ["/Users/adrian.osullivan/Dropbox/kdp/2016-01-rawdata.csv"
   "/Users/adrian.osullivan/Dropbox/kdp/2016-02-rawdata.csv"
   "/Users/adrian.osullivan/Dropbox/kdp/2016-03-rawdata.csv"
   "/Users/adrian.osullivan/Dropbox/kdp/2016-04-rawdata.csv"
   "/Users/adrian.osullivan/Dropbox/kdp/2016-05-rawdata.csv"
   "/Users/adrian.osullivan/Dropbox/kdp/2016-06-rawdata.csv"])

(defrecord Row
  [StartDate	EndDate	Publisher	TitleID	ISBN	Title	PrimaryAuthor	Distributor	Vendor	Country	UnitsSold	UnitsReturned	NetUnitSales	ListPricePerUnit	CurrencyCode	OfferPricePerUnit	RoyaltyPercent	SalesChannelFeeTaxesPerUnit	SalesChannelRevenuePerUnit	ExtendedSalesChannelRevenue	SalesChannelShare	D2DShare	PublisherShare	PublisherShareUSDestimated	Verified])

(defn read-rows [filename]
  (->>
    (with-open [in-file (io/reader filename)]
      (doall (csv/read-csv in-file)))
    (rest)))

(defn books-ext [filename]
  (->>
    (for [row (read-rows filename)]
      (apply ->Row (flatten row)))
    (map #(into {} %))))

(defn books-all [filename]
  (map (fn [book] (select-keys book [:Title :UnitsSold :CurrencyCode :Country :Vendor]))
    (books-ext filename)))

(defn parse-int [s]
  (Integer/parseInt (re-find #"\A-?\d+" s)))

(defn expand-units-sold [books]
  (map (fn [book] (select-keys book [:Title :CurrencyCode :Country :Vendor]))
     (flatten
       (for [book books]
         (let [n (parse-int (:UnitsSold book))]
           (if (> n  1)  (repeat n book) book))))))

(def books
  (->>
    (map books-all filenames)
    (map expand-units-sold )
    (flatten)))

(group-by :Title books)

;todo
; expand units sold
; add country code to kdp
; merge the two reports - make the kdp a proper map with keys and leave group-by till the end


