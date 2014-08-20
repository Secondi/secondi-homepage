(ns secondi.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [secondi.page :as page]
            [secondi.menu :as menu]
            [secondi.dom :refer [get-anchor has-class by-id]]
            [secondi.reactive :refer [listen]]
            [cljs.core.async :refer [<! >! put! chan]]
            [secretary.core :as secretary :include-macros true :refer [defroute]])
  (:import [goog.history Html5History]
           [goog Uri]
           [goog.history EventType])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(enable-console-print!)
(println "hello Secondi")

(defonce app-state (atom {:view :home
                          :areas [(page/navigate-page "Video" "A whole lot of video")
                                  (page/navigate-page "About Us" "hello this is about us")
                                  (page/navigate-page "Rainbow Series" "you like rainbows?")
                                  (page/navigate-page "Sign Up" "you should sign up to the mailing list")
                                  (page/navigate-page "Blog" "blog with me")
                                  (page/navigate-page "Music" "I like music, we like music, you like too?")]}))

(defn secondi-app [app owner]
  (let [view (:view app)
        component (condp = view
                    :home (:areas app)
                    :video (:areas app)
                    :about-us (:areas app)
                    :rainbow-series (:areas app)
                    :sign-up (:areas app)
                    :blog (:areas app)
                    :music (:areas app))]
    (om/component
     (dom/div nil
              (om/build menu/menu-view app)
              (apply dom/div nil
                     (om/build-all page/page-view component))))))

(om/root secondi-app app-state
         {:target (. js/document (getElementById "page"))})

;; ROUTING

(defroute "/" []
  (swap! app-state assoc :view :home)
  (js/window.scrollTo 0 0))
(defroute "/:path" [path]
  (swap! app-state assoc :view (keyword path))
  (js/window.scrollTo 0 0))

(def history (Html5History.))
(.setUseFragment history false)
(.setPathPrefix history "")
(.setEnabled history true)

(js/history.pushState #js {} "Secondi Homepage" (.substring js/window.location.hash 1))

(secretary/dispatch! (.-pathname js/window.location))

(defn pdclick
  "Prevents the default action on the provided event if it is an internal link."
  [e]
  (let [anchor (get-anchor (.-target e))]
    (when (not (has-class anchor "external"))
      (.preventDefault e))))

(def click-c (listen js/window :click pdclick))
(def navigation-c (listen history :navigate))

(go (while true
      (let [[v c] (alts! [
                          click-c
                          navigation-c])]
        (condp = c
          click-c (let [anchor (get-anchor (.-target v))
                        path (.getPath (.parse Uri (.-href anchor)))
                        title (.-title anchor)
                        internal (not (has-class anchor "external"))
                        matches-path? (secretary/locate-route path)]
                    (if (and matches-path? (has-class anchor "link") internal)
                      (. history (setToken path title))))
          navigation-c (secretary/dispatch! (.-token v))))))
