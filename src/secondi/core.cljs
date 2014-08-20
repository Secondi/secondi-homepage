(ns secondi.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [secondi.page :as page]
            [secondi.menu :as menu]))

(enable-console-print!)
(println "hello Secondi")

(defonce app-state (atom {:areas [(page/generic-page "Video" "A whole lot of video")
                                  (page/generic-page "About Us" "hello this is about us")
                                  (page/generic-page "Rainbow Series" "you like rainbows?")
                                  (page/generic-page "Sign Up" "you should sign up to the mailing list")
                                  (page/generic-page "Blog" "blog with me")
                                  (page/generic-page "Music" "I like music, we like music, you like too?")]}))

(defn secondi-app [app owner]
  (om/component
   (dom/div nil
            (om/build menu/menu-view app)
            (apply dom/div nil
                   (om/build-all page/page-view (:areas app))))))

(om/root secondi-app app-state
         {:target (. js/document (getElementById "page"))})
