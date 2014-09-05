(ns secondi.pages.music
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [secondi.pages.generic :as generic]
            [clojure.string :as string]
            [cljs.core.async :refer [<! >! put! chan]])
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

;; sound types
;; ----------------------------------------------------------------------------

(defprotocol ITrack
  (prepare-track [this])
  (play-track [this])
  (stop-track [this])
  (current-position [this])
  (set-position [this]))

(defrecord SoundcloudTrack [name soundmanager]
  ITrack
  (prepare-track [this] nil)
  (play-track [this] nil)
  (stop-track [this] nil)
  (current-position [this] nil)
  (set-position [this] nil))

(defn music-track [name _]
  (->SoundcloudTrack name nil))

(defprotocol IPlaylist
  (play-next [this] [this next-track]))

(defrecord Playlist [name album-cover track-collection]
  IPlaylist
  (play-next [this] nil))

(defn playlist [name album-cover track-collection]
  (->Playlist name album-cover track-collection))

;; page extension
;; ----------------------------------------------------------------------------


(defrecord MusicPage [navigation-page]
  generic/IPageNavigation
  (create-slug [this]
               (generic/make-slug (get-in this [:navigation-page :page :name]))))

(defn music-page [name body-description]
  (->MusicPage (generic/navigate-page name body-description)))


;; primitive test data
;; ----------------------------------------------------------------------------

(def img-1 "http://www.csettepiu7.it/dfiles/portate_image/carne.jpg")
(def img-2 "http://www.anticatrattoriabellaria.it/upload/thumb500/1301264205.jpg")
(def img-3 "http://3.bp.blogspot.com/-IXpItzpZr3w/T6dyViU3DkI/AAAAAAAAGHA/_r9xVqMkMPM/s1600/IMG_1686.JPG")

(def temp-playlists [(playlist "this is" img-1 [(music-track "hello" 1)
                                                (music-track "there" 1)
                                                (music-track "i'm" 1)
                                                (music-track "dummy" 1)
                                                (music-track "data" 1)])
                     (playlist "secondi food" img-2 [(music-track "watch me" 1)
                                                     (music-track "play" 1)
                                                     (music-track "i'll" 1)
                                                     (music-track "choose" 1)
                                                     (music-track "anti-mage" 1)])
                     (playlist "from italy, apparently" img-3 [(music-track "care" 1)
                                                               (music-track "i'm" 1)
                                                               (music-track "tummy" 1)
                                                               (music-track "laughter" 1)])])

;; track component
;; ----------------------------------------------------------------------------

(def playlist-corners
  (dom/div nil
           (dom/div #js {:id "corner-topleft"} nil)
           (dom/div #js {:id "corner-topright"} nil)
           (dom/div #js {:id "corner-bottomleft"} nil)
           (dom/div #js {:id "corner-bottomright"} nil)))

(def play-symbol "►")
(def pause-symbol "||")

(defn play-button [play-state]
  (condp = play-state
    :play pause-symbol
    :pause play-symbol
    :stop play-symbol))

(defn track-view [track owner]
  (reify
    om/IInitState
    (init-state [_]
                {:playing :stop})
    om/IRenderState
    (render-state [_ state]
                  (dom/div #js {:className "track"
                                :onClick #(js/console.log (str "you've clicked: " (:name track)))}
                           (dom/div #js {:className "play-button"}
                                    (dom/span nil (play-button (:playing state))))
                           (dom/span #js {:className "track-text"} (str "Track: " (:name track)))))))

(defn playlist-view [playlist owner]
  (reify
    om/IRenderState
    (render-state [_ state]
                  (dom/div #js {:id "playlist-wrapper"}
                           playlist-corners
                           (dom/div #js {:id "playlist"}
                                    (println playlist)
                                    (dom/h2 nil (-> playlist :name string/upper-case))
                                    (apply dom/div nil
                                           (om/build-all track-view (:track-collection playlist))))))))

;; album view component
;; ----------------------------------------------------------------------------

(defn background-img [src]
  (str "url(" src ")"))

(def album-pointer
  (dom/div #js {:className "album-pointer"} nil))

(defn set-active! [album state owner]
  (put! (:ta-chan state) (om/value album))
  (om/set-state! owner :active? true))

(defn album-view [album owner]
  (reify
    om/IInitState
    (init-state [_]
                {:active? false})
    om/IRenderState
    (render-state [_ state]
                  (dom/div #js {:className "album"
                                :onClick #(set-active! album state owner)
                                :style #js {:background (background-img (:album-cover album))}}
                           (when (= true (:active? state)) album-pointer)))))

(defn current-album [albums]
  (first albums))

;(fn [xs] (vec (remove #(= contact %) xs))))
(defn select-album! [album owner]
  (js/console.log (om/get-props owner))
  ;(om/transact! owner (fn [xs] xs))
  )

(defn albumlist-view [app-state owner]
  (reify
    om/IInitState
    (init-state [_]
                {:ta-chan (chan)})
    om/IWillMount
    (will-mount [_]
                (let [ta-chan (om/get-state owner :ta-chan)]
                  (go (while true
                           (select-album! (<! ta-chan) owner)))))
    om/IRenderState
    (render-state [_ state]
                  (dom/div nil
                           (apply dom/div #js {:id "albums"}
                                  (om/build-all album-view (:albums state) {:init-state {:ta-chan (:ta-chan state)}}))
                           (om/build playlist-view (current-album (:albums state)))))))

;; music page wrapper
;; ----------------------------------------------------------------------------

(extend-type MusicPage
  generic/ICustomPage
  (custom-page
   [this]
   (fn [app-state owner]
     (reify
       om/IInitState
       (init-state [_]
                   {:sections temp-playlists})
       om/IRenderState
       (render-state [_ state]
                     (dom/div #js {:className "sectionWrapper music-page"}
                              (dom/div #js {:className "content"}
                                       (om/build albumlist-view app-state {:init-state {:albums (:sections state)}}))))))))
