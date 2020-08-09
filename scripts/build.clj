(ns build
  (:require
    [clojure.spec.test.alpha :as spec-test]
    [com.jeremyschoffen.mbt.alpha.core :as mbt-core]
    [com.jeremyschoffen.mbt.alpha.default :as mbt-defaults]))

(spec-test/instrument)




(def specific-conf (sorted-map
                     :maven/group-id 'fr.jeremyschoffen
                     :versioning/major :alpha
                     :versioning/scheme mbt-defaults/simple-scheme))


(def conf (mbt-defaults/make-conf specific-conf))


(comment
  (-> conf
      (assoc :project/version "0")
      mbt-defaults/build-jar!)

  (-> conf
      (assoc :project/version "0")
      mbt-defaults/install!))