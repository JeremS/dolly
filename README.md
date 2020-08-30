


# Dolly

Utilities for cloning vars in Clojure and Clojurescript.



## Installation
Deps coords:
```clojure
#:fr.jeremyschoffen{dolly #:mvn{:version "0"}}
```
Lein coords:
```clojure
[fr.jeremyschoffen/dolly "0"]
```

## Usage

```Clojure
(defn foo
  "Some foo doc"
  [])

;; We can clone foo in the same ns or not
(def-clone bar foo)

;; The docstring (and other var metadata) is cloned
(clojure.repl/doc foo)
;=> "Some foo doc"


;; We can clone macros
(defmacro baz
  "Some doc for baz."
  [])

;; And changed some metadata for the clone.
(def-clone ^{:doc "Some other doc"}cloned-baz baz)

(clojure.repl/doc cloned-baz)
;=> "Some other doc"
```

## Inspirations
This project is inspired by [https://github.com/aleph-io/potemkin](potemkin's') import var system.
However dolly's `def-clone` doesn't link aliased vars to propagate change when reloaded the original is redefined.
I don't think that's possible in Clojurescript and this projects aims to work with it.

## License

Copyright © 2020 Jeremy Schoffen.

Distributed under the Eclipse Public License v 2.0.
