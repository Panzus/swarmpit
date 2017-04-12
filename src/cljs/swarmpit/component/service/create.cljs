(ns swarmpit.component.service.create
  (:require [swarmpit.material :as material]
            [swarmpit.component.service.form-settings :as settings]
            [swarmpit.component.service.form-ports :as ports]
            [swarmpit.component.service.form-variables :as variables]
            [rum.core :as rum]))

(enable-console-print!)

(defonce step-index (atom 0))

(def steps ["General settings" "Ports" "Environment variables"])

(defmulti form-item identity)

(defmethod form-item 0 [_] (settings/form false))

(defmethod form-item 1 [_] (ports/form))

(defmethod form-item 2 [_] (variables/form))

(defn- step-previous
  [index]
  (if (< 0 index)
    (reset! step-index (dec index))))

(defn- step-next
  [index]
  (if (> (count steps) index)
    (reset! step-index (inc index))))

(defn- step-items []
  (map-indexed
    (fn [index item]
      (material/step
        #js {:key index}
        (material/step-button
          #js {:disableTouchRipple true
               :style              #js {:backgroundColor "transparent"}
               :onClick            (fn [] (reset! step-index index))}
          item)))
    steps))

(rum/defc form < rum/reactive []
  (let [index (rum/react step-index)]
    [:div
     (material/theme
       (material/stepper
         #js {:activeStep index
              :linear     false
              :style      #js {:background "rgb(245, 245, 245)"
                               :height     "60px"}
              :children   (clj->js (step-items))}))
     (form-item index)
     [:div.form-panel.form-buttons
      [:div.form-panel-left
       (material/theme
         (material/raised-button
           #js {:label      "Previous"
                :disabled   (= 0 index)
                :onTouchTap (fn [] (step-previous index))
                :style      #js {:marginRight "12px"}}))
       (material/theme
         (material/raised-button
           #js {:label      "Next"
                :disabled   (= (- (count steps) 1) index)
                :onTouchTap (fn [] (step-next index))}))]
      [:div.form-panel-right
       (material/theme
         (material/raised-button
           #js {:label   "Create"
                :primary true}))]]]))

(defn- init-settings-state
  []
  (reset! settings/state {:image        nil
                          :serviceName  ""
                          :mode         "replicated"
                          :replicas     1
                          :autoredeploy false}))

(defn- init-ports-state
  []
  (reset! ports/state []))

(defn- init-variables-state
  []
  (reset! variables/state []))

(defn- init-state
  []
  (init-settings-state)
  (init-ports-state)
  (init-variables-state))

(defn mount!
  []
  (init-state)
  (rum/mount (form) (.getElementById js/document "content")))