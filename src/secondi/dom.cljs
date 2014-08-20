(ns secondi.dom
  (:require [goog.style :as style]
            [goog.dom :as dom]
            [goog.dom.classes :as classes]
            [clojure.string :as string]))

(defn by-id [id]
  (.getElementById js/document id))

(defn by-tag-name [el tag]
  (prim-seq (dom/getElementsByTagNameAndClass tag nil el)))

(defn has-class
  "Return true if an element has a class."
  [element className]
  (classes/has element className))

(defn get-anchor
  "Returns anchor of the target if it exists otherwise the target's parent."
  [target]
  (if (= "a" (string/lower-case (.-tagName target)))
    target
    (dom/getParentElement target)))
