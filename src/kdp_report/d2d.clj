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

(def rows
  (->>
    (with-open [in-file (io/reader "/Users/adrian.osullivan/Dropbox/kdp/2016-01-rawdata.csv")]
      (doall (csv/read-csv in-file)))
    (rest)))

(def books
  (->>
    (for [row rows]
      (apply ->Row (flatten row)))
    (map #(into {} %))))



(:Title  (second books) )

