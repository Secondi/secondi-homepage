(ns secondi.vimeo
  (:require [cljs.core.async :refer [<!]]
            [ajax.core :refer [GET POST]]
            [cognitect.transit :as t])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defonce app-state (atom {}))

(defn convert-json [json]
  (let [r (t/reader :json)]
    (-> (t/read r json)
        (clj->js) ;convert back to js as the keywords don't get picked up the first time...
        (js->clj :keywordize-keys true))))

(defn handler [response]
  (reset! app-state (convert-json response)))

(defn error-handler [{:keys [status status-text]}]
  (.log js/console (str "something bad happened: " status " " status-text)))

(def authorize "/oauth/authorize/client")
(def api_url "https://api.vimeo.com")
(def token "be481b1c706148d60b228284f3ab740f")
(def user-videos-uri "/users/19719781/videos")

(def response (GET (str api_url user-videos-uri) {:headers {:authorization (str "bearer " token)
                                                            :Content-Type "application/json"}
                                                  :format :json
                                                  :params {:filter "embeddable"
                                                           :filter_embeddable true}
                                                  :handler handler
                                                  :error-handler error-handler}))
(defn get-video-picture [pictures]
  (nth pictures 0))

(defn extract-videos [response]
  (for [video (:data response)]
    (get-video-picture (:pictures video))))

(extract-videos @app-state)
(keys (nth (:data @app-state) 0))
(:uri (nth (:data @app-state) 0))


;https://api.vimeo.com/users/19719781/videos
