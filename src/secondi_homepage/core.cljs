(ns secondi.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

(enable-console-print!)
(println "hello Secondi")

(defonce app-state (atom 0))

(defn secondi-app [app owner]
  (om/component
   (dom/div nil nil)))

(om/root secondi-app app-state
         {:target (. js/document (getElementById "page"))})
