(ns fr.jeremyschoffen.dolly.docs.core
  (:require
    [fr.jeremyschoffen.textp.alpha.doc.core :as tp-doc]
    [fr.jeremyschoffen.mbt.alpha.utils :as u]))


(u/pseudo-nss
  project)



(def readme-path "fr/jeremyschoffen/dolly/docs/pages/README.md.tp")


(defn make-readme! [{wd ::project/working-dir
                     coords ::project/maven-coords}]
  (spit (u/safer-path wd "README.md")
        (tp-doc/make-document readme-path {:project/maven-coords coords})))