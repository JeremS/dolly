(ns build
  (:require
    [fr.jeremyschoffen.mbt2.core :as mbt]
    [fr.jeremyschoffen.mbt2.git :as git]
    [fr.jeremyschoffen.mbt2.prose :as p]))


(def lib-name 'io.github.jerems/dolly)

(defn generate-readme! []
  (spit "README.md" (p/generate-md-doc "README.md.prose" {:lib-name lib-name})))

(p/eval-doc "README.md.prose" {:lib-name lib-name})

(defn generate-docs! []
  ;; generate stuff
  (generate-readme!)
  (git/add-all!)
  (git/commit! :commit-msg "Generated docs."))



(defn release! []
  (mbt/assert-clean-repo)
  (mbt/tag-release!)
  (generate-docs!))


(comment
  (-> *e ex-data)
  (generate-readme!)
  (release!))
