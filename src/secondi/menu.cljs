(ns secondi.menu
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [secondi.pages.generic :as generic]
            [secondi.scroll :refer [when-scrolling]]
            [clojure.string :as string]))


;; om component
;; ----------------------------------------------------------------------------

(defn menu-square [name]
  (dom/div #js {:className "nav-square"}
           (dom/div #js {:className "nav-name"} name)))

(defn nav-name [page]
  (if (satisfies? generic/ICustomPage (.-value page))
    (get-in page [:navigation-page :page :name])
    (get-in page [:page :name])))

(defn menu-item-view [item owner]
  (om/component
   (dom/li nil
           (dom/a #js {:className "link" :href (generic/create-slug (.-value item))}
                  (->> item
                       (nav-name)
                       (menu-square)
                       (dom/div #js {:className "wrapper"}))))))

(defn nav-items [areas]
  (filter #(satisfies? generic/IPageNavigation (.-value %)) areas))


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
   (dom/div #js {:className "menuOccupy"} nil)))
