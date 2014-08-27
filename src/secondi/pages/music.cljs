(ns secondi.pages.music
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [secondi.pages.generic :as generic]))

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

(defn track-view [track owner]
  (reify
    om/IRenderState
    (render-state [_ state]
                  (dom/div #js {:className "track"
                                :onClick #(js/console.log (str "you've clicked: " (:name track)))}
                           (dom/p nil (:name track))))))

(defn playlist-view [playlist owner]
  (reify
    om/IRenderState
    (render-state [_ state]
                  (apply dom/div nil
                         (om/build-all track-view (:track-collection playlist))))))

;; album view component
;; ----------------------------------------------------------------------------

(defn background-img [src]
  (str "url(" src ")"))

(defn album-view [album owner]
  (reify
    om/IRenderState
    (render-state [_ state]
                  (dom/div #js {:className "album"
                                :onClick #(js/console.log (str "you have clicked: " (:name album)))
                                :style #js {:background (background-img (:album-cover album))}}))))

(defn albumlist-view [albums owner]
  (reify
    om/IInitState
    (init-state [_]
                {:current-album (first albums)})
    om/IRenderState
    (render-state [_ state]
                  (dom/div nil
                           (apply dom/div #js {:id "albums"}
                                  (om/build-all album-view albums))
                           (dom/div #js {:id "playlist"}
                                    (om/build playlist-view (:current-album state)))))))

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
                                       (om/build albumlist-view (:sections state)))))))))
