(ns secondi.menu
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [secondi.page :as page]))


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

(defn menu-view [app owner]
  (reify
    om/IInitState
    (init-state [_]
                {})
    om/IWillMount
    (will-mount [_]
                (js/console.log (om/get-state owner :hello)))
    om/IRenderState
    (render-state [this state]
                  (dom/div #js {:className "menuOccupy"}
                           (dom/div #js {:className "menuWrapper"}
                                    (apply dom/ul #js {:className "menu"}
                                           (om/build-all menu-item-view (nav-items (:areas app)))))))))
