(defproject com.lemondronor/droneklv "0.1.0-SNAPSHOT"
  :description "Process KLV metadata embedded in drone video."
  :url "https:///github.com/wiseman/droneklv"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[com.lemonodor/xio "0.2.2"]
                 [gloss "0.2.5"]
                 [org.clojure/clojure "1.6.0"]]
  :source-paths ["src/clj"]
  :java-source-paths ["src/java"]
  :profiles {:dev {:plugins [[lein-cloverage "1.0.2"]]}})
