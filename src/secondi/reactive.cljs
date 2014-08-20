(ns secondi.reactive
  (:refer-clojure :exclude [map filter remove distinct concat take-while])
  (:require [goog.events :as events]
            [goog.events.EventType]
            [goog.history.EventType]
            [goog.net.Jsonp]
            [goog.Uri]
            [goog.dom :as gdom]
            [cljs.core.async :refer [>! <! chan put! close! timeout sliding-buffer]])
  (:require-macros [cljs.core.async.macros :refer [go alt!]])
  (:import goog.events.EventType))

(defn atom? [x]
  (instance? Atom x))

(def keyword->event-type
  {:keyup goog.events.EventType.KEYUP
   :keydown goog.events.EventType.KEYDOWN
   :keypress goog.events.EventType.KEYPRESS
   :click goog.events.EventType.CLICK
   :dblclick goog.events.EventType.DBLCLICK
   :mousedown goog.events.EventType.MOUSEDOWN
   :mouseup goog.events.EventType.MOUSEUP
   :mouseover goog.events.EventType.MOUSEOVER
   :mouseout goog.events.EventType.MOUSEOUT
   :mousemove goog.events.EventType.MOUSEMOVE
   :focus goog.events.EventType.FOCUS
   :blur goog.events.EventType.BLUR
   :scroll goog.events.EventType.SCROLL
   :resize goog.events.EventType.RESIZE
   :navigate goog.history.EventType.NAVIGATE})

(defn listen
  ([el type] (listen el type nil))
  ([el type f] (listen el type f (chan (sliding-buffer 1))))
  ([el type f out]
   (events/listen el (keyword->event-type type)
                  (fn [e] (when f (f e)) (put! out e)))
   out))
