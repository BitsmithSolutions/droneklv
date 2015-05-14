(ns com.lemondronor.droneklv-test
  (:require [clojure.test :refer :all]
            [com.lemondronor.droneklv :as droneklv]))


(defn b [bytes]
  (byte-array (droneklv/ints->bytes bytes)))


(defn a= [a b eps]
  (let [d (Math/abs (- a b))]
    (if (< d eps)
      true
      (do
        (binding [*out* *err*]
          (println "Difference is too great:" d))
        false))))


;; These tests are taken from the examples section 8 in the the ST
;; 0601.8 spec, "Conversions and Mappings between Metadata Types".

(deftest local-set-tag-parsing
  (testing "unix timestamp"
    (let [[offset tag] (droneklv/parse-local-set-tag
                        (b [0x02 0x08 0x00 0x04 0x59 0xF4 0xA6 0xAA 0x4A 0xA8]))]
      (is (= 10 offset))
      (is (= [:unix-timestamp 1224807209913000N] tag))
      (is (= #inst "2008-10-24T00:13:29.913-00:00"
             (java.util.Date. (long (/ (second tag) 1000.0)))))))
  (testing "mission ID"
    (is (= [11 [:mission-id "MISSION01"]]
           (droneklv/parse-local-set-tag
            (b [0x03 0x09 0x4D 0x49 0x53 0x53 0x49 0x4F 0x4E 0x30 0x31])))))
  (testing "platform tail number"
    (is (= [8 [:platform-tail-number "AF-101"]]
           (droneklv/parse-local-set-tag
            (b [0x04 0x06 0x41 0x46 0x2D 0x31 0x30 0x31])))))
  (testing "platform heading"
    (let [[off [tag angle]] (droneklv/parse-local-set-tag
                             (b [0x05 0x02 0x71 0xC2]))]
      (is (= 4 off))
      (is (= :platform-heading tag))
      (is (a= 159.9744 angle 1e-4))))
  (testing "platform pitch"
    (let [[off [tag angle]] (droneklv/parse-local-set-tag
                             (b [0x06 0x02 0xFD 0x3D]))]
      (is (= 4 off))
      (is (= :platform-pitch tag))
      ;; Spec has -0.4315251
      (is (a= -0.4315317 angle 1e-7))))
  (testing "platform true airspeed"
    (let [[off [tag spd]] (droneklv/parse-local-set-tag
                             (b [0x08 0x01 0x93]))]
      (is (= 3 off))
      (is (= :platform-true-airspeed tag))
      (is (= 147 spd))))
  (testing "platform indicated airspeed"
    (let [[off [tag spd]] (droneklv/parse-local-set-tag
                             (b [0x09 0x01 0x9F]))]
      (is (= 3 off))
      (is (= :platform-indicated-airspeed tag))
      (is (= 159 spd))))
  (testing "platform designation"
    (let [[off [tag desig]] (droneklv/parse-local-set-tag
                             (b [0x0A 0x05 0x4D 0x51 0x31 0x2D 0x42]))]
      (is (= 7 off))
      (is (= :platform-designation tag))
      (is (= "MQ1-B" desig))))
  (testing "image source sensor"
    (let [[off [tag src]] (droneklv/parse-local-set-tag
                             (b [0x0B 0x02 0x45 0x4F]))]
      (is (= 4 off))
      (is (= :image-source-sensor tag))
      (is (= "EO" src))))
  (testing "image coordinate system"
    (let [[off [tag sys]] (droneklv/parse-local-set-tag
                           (b [0x0C 0x06 0x57 0x47 0x53 0x2D 0x38 0x34]))]
      (is (= 8 off))
      (is (= :image-coordinate-system tag))
      (is (= "WGS-84" sys))))
  (testing "sensor latitude"
    (let [[off [tag lat]] (droneklv/parse-local-set-tag
                           (b [0x0D 0x04 0x55 0x95 0xB6 0x6D]))]
      (is (= 6 off))
      (is (= :sensor-lat tag))
      (is (a= 60.1768229669783 lat 1e-13))))
  (testing "sensor longitude"
    (let [[off [tag lon]] (droneklv/parse-local-set-tag
                           (b [0x0E 0x04 0x5B 0x53 0x60 0xC4]))]
      (is (= 6 off))
      (is (= :sensor-lon tag))
      (is (a= 128.426759042045 lon 1e-12))))
  (testing "sensor true altitude"
    (let [[off [tag alt]] (droneklv/parse-local-set-tag
                           (b [0x0F 0x02 0xC2 0x21]))]
      (is (= 4 off))
      (is (= :sensor-true-alt tag))
      (is (a= 14190.72 alt 1e-2))))
  (testing "sensor horizontal fov"
    (let [[off [tag fov]] (droneklv/parse-local-set-tag
                           (b [0x10 0x02 0xCD 0x9C]))]
      (is (= 4 off))
      (is (= :sensor-horizontal-fov tag))
      (is (a= 144.5713 fov 1e-4))))
  (testing "sensor vertical fov"
    (let [[off [tag fov]] (droneklv/parse-local-set-tag
                           (b [0x11 0x02 0xD9 0x17]))]
      (is (= 4 off))
      (is (= :sensor-vertical-fov tag))
      (is (a= 152.6436 fov 1e-4))))
  (testing "sensor relative azimuth"
    (let [[off [tag angle]] (droneklv/parse-local-set-tag
                             (b [0x12 0x04 0x72 0x4A 0x0A 0x20]))]
      (is (= 6 off))
      (is (= :sensor-relative-azimuth tag))
      ;; Spec has 160.719211474396.
      (is (a= 160.7192114369756 angle 1e-12))))
  )
