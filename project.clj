(defproject secondi-homepage "0.1.0-SNAPSHOT"
  :description "Homepage for Secondi"
  :url "http://secondi.co.nz"

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2234"]
                 [org.clojure/core.async "0.1.303.0-886421-alpha"]
                 [om "0.7.0"]
                 [secretary "1.2.0"]
                 [cljs-ajax "0.2.6"]]


  :plugins [[lein-cljsbuild "1.0.3"]]

  :source-paths ["src"]
  :resource-paths ["assets"
                   "bower_components"]

  :cljsbuild {:builds {:dev {:id "secondi-dev"
                             :source-paths ["src"]
                             :compiler {:output-to "assets/js/secondi-homepage.js"
                                        :output-dir "assets/js/out"
                                        :optimizations :none
                                        :source-map true}}
                       :main {:id "release"
                              :source-paths ["src"]
                              :compiler {:output-to "release/main.js"
                                         :optimizations :advanced
                                         :pretty-print false
                                         :preamble ["react/react.min.js"]
                                         :externs ["react/externs/react.js"]}}}})
