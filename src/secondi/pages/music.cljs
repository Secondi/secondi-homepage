(ns secondi.pages.music
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [secondi.pages.generic :as generic]))

;; types
;; ----------------------------------------------------------------------------

(defrecord MusicPage [navigation-page]
  generic/IPageNavigation
  (create-slug [this]
               (generic/make-slug (get-in this [:navigation-page :page :name]))))

(defn music-page [name body-description]
  (->MusicPage (generic/navigate-page name body-description)))


;; om component
;; ----------------------------------------------------------------------------

(extend-type MusicPage
  generic/ICustomPage
  (custom-page
   [this]
   (fn [app-state owner]
     (reify
       om/IRender
       (render [_]
               (dom/div #js {:className "sectionWrapper general-page"}
                        (dom/div #js {:className "content"} "THIS IS THE MUSIC PAGE")))))))
