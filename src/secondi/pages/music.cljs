(ns secondi.pages.music
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [secondi.pages.generic :as generic]))



;; music page view
(defn music-view [page owner]
  (om/component
   (dom/div #js {:className "sectionWrapper general-page"}
            (dom/div #js {:className "content"} "THIS IS THE MUSIC PAGE"))))
