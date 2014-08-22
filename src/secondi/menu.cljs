(ns secondi.menu
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [secondi.page :as page]
            [secondi.scroll :refer [when-scrolling]]
            [clojure.string :as string]))


;; om component
;; ----------------------------------------------------------------------------

(defn menu-square [name]
  (dom/div #js {:className "nav-square"}
           (dom/div #js {:className "nav-name"} name)))

(defn menu-item-view [item owner]
  (om/component
   (dom/li nil
           (dom/a #js {:className "link" :href (page/create-slug (.-value item))}
                  (dom/div #js {:className "wrapper"}
                           (menu-square (get-in item [:page :name])))))))

(defn nav-items [areas]
  (filter #(satisfies? page/IPageNavigation (.-value %)) areas))


(defn menu-class [state]
  (clj->js (merge {:className (string/join " " ["menuWrapper"
                                   (if (> 300 (:scrollY state)) "full" "minimized")])})))

(defn menu-view [app owner]
  (reify
    om/IInitState
    (init-state [_]
                {})
    om/IWillMount
    (will-mount [_]
                (when-scrolling #(om/set-state! owner :scrollY %)))
    om/IRenderState
    (render-state [this state]
                  (dom/div (menu-class state)
                           (apply dom/ul #js {:className "menu"}
                                  (om/build-all menu-item-view (nav-items (:areas app))))))))

(defn menu-wrapper [app owner]
  (om/component
   (dom/div #js {:className "menuOccupy"}
            (om/build menu-view app))))
