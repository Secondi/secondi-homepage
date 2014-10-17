(ns secondi.components.bandcamp
  (:require [clojure.string :as string]
            [cljs.core.async :refer [<! put! chan map<]]
            [goog.net.Jsonp :as Jsonp]
            [goog.Uri :as Uri])
  (:import [goog.net Jsonp]
           [goog Uri])
  (:require-macros [cljs.core.async.macros :refer [go]]))


;; sound types
;; ----------------------------------------------------------------------------

(defprotocol ITrack
  (prepare-track [this])
  (play-track [this])
  (stop-track [this])
  (current-position [this])
  (set-position [this]))

(defrecord BandcampTrack [name soundmanager]
  ITrack
  (prepare-track [this] nil)
  (play-track [this] nil)
  (stop-track [this] nil)
  (current-position [this] nil)
  (set-position [this] nil))

(defn music-track [name _]
  (->BandcampTrack name nil))

(defprotocol IPlaylist
  (play-next [this] [this next-track]))

(defrecord Playlist [name album-cover track-collection]
  IPlaylist
  (play-next [this] nil))

(defn playlist [{:keys [title album-cover track-collection] :as info}]
  (->Playlist title (if album-cover album-cover (:large_art_url info)) track-collection))

;; sound types
;; ----------------------------------------------------------------------------


(def secondi-id "1269523251")
(def dev-key "vatnthrunginnyskrathettr")
(def api-url "http://api.bandcamp.com/api/")
(def band-url (str api-url "band/"))
(def album-url (str api-url "album/"))

(defn url-kv [k v]
  (str k "=" v))

(defn url-query [v]
  (string/join "&" (map (fn [item]
                          (url-kv (-> item (get 0) name)
                                  (item 1)))(seq v))))

(defn discography-url [api-key band-id]
  (str band-url "3/discography?" (url-query {:key api-key
                                             :band_id band-id})))
(defn tracklist-url [api-key album-id]
  (str album-url "2/info?" (url-query {:key api-key
                                       :album_id album-id})))

(defn handler [response]
  (js/console.log response))

(defn jsonp
  "
  Submit Jsonp to uri
  on response, push to channel
  return channel that will have response pushed onto
  "
  [uri]
  (let [out (chan)
        req (Jsonp. uri)]
    (.send req nil (fn [res] (put! out res)))
    out))

(defn tracklist [album]
  (->> album
       :album_id
       (tracklist-url dev-key)
       jsonp))

(defn init-bandcamp []
  (let [d-uri (discography-url dev-key secondi-id)
        d-c (jsonp (Uri. d-uri))]
    (go (while true
          (let [albums (-> (<! d-c) (js->clj :keywordize-keys true) :discography)
                tracklists-c (map tracklist albums)]
            (js/console.log (first (map< #(js->clj (<! %) :keywordize-keys true) (seq tracklists-c))))
            )))))

(init-bandcamp)
