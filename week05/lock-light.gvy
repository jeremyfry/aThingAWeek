/**
 *  Turn On Light When I unlock Door
 *
 *  Copyright 2015 Jeremy Fry
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
definition(
    name: "Turn On Light When I unlock Door",
    namespace: "jfry",
    author: "Jeremy Fry",
    description: "Turns on a light and turns it off after a set time (if it was off to begin with)",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")



preferences {
	section("When lock is unlocked...") {
		input "lock1", "capability.lock"
	}
    section("Turn on a switch...") {
		input "switch1", "capability.switch"
	}
    section("Turn off when?"){
		input "minutesLater", "number"
	}
    section("After this time of day") {
		input "sunset", "time", title: "Time?"
	}
    section("Before this time of day") {
		input "sunrise", "time", title: "Time?"
	}
}

private correctTime() {
	def t0 = now()
    def timeZone = location.timeZone ?: timeZone(timeOfDay)
    def startTime = timeToday(sunset, timeZone)
    def endTime = timeToday(sunrise, timeZone)
	if (t0 >= startTime.time || t0 <= endTime.time) {
		true
	} else {
		false
	}
}

def subscribeToEvents(){
	log.debug "Settings: ${settings}"
    subscribe(lock1, "lock", lockHandler)
}

def lockHandler(evt) {
	log.debug "doorLocked($evt.name: $evt.value)"
    if(evt.value == "unlocked"){
	    if(switch1.currentState("switch").value == "off" && correctTime()){
        	switch1.on()
            def delay = minutesLater * 60
            runIn(delay, turnOffLight)
        }
    }
}

def turnOffLight(){
	switch1.off()
}

def installed() {
	log.debug "Installed"
	subscribeToEvents()
}

def updated() {
	log.debug "Updated"
	unsubscribe()
    subscribeToEvents()
}
