# Created by Kåre Byberg © 21.01.2005. Please acknowledge if used on other domains than http://www.timpelen.com 
# Ported to CoffeeScript by Matt Raible. Also added hours support.
flagClock = 0
flagStop = 0
stopTime = 0
refresh = ""
clock = ""

start = (button, display) ->
  clock = display
  startDate = new Date()
  startTime = startDate.getTime()
  if flagClock == 0
    $(button).html("Stop")
    flagClock = 1
    counter startTime, display
  else
    $(button).html("Start")
    flagClock = 0
    flagStop = 1

counter = (startTime) ->
  currentTime = new Date()
  timeDiff = currentTime.getTime() - startTime
  timeDiff = timeDiff + stopTime  if flagStop == 1
  if flagClock == 1
    $(clock).val formatTime timeDiff, ""
    callback = -> counter startTime
    refresh = setTimeout callback, 10
  else
    window.clearTimeout refresh
    stopTime = timeDiff
    
formatTime = (rawTime, roundType) ->
  if roundType == "round"
    ds = Math.round(rawTime / 100) + ""
  else
    ds = Math.floor(rawTime / 100) + ""
  sec = Math.floor(rawTime / 1000)
  min = Math.floor(rawTime / 60000)
  hour = Math.floor(rawTime / 3600000)
  ds = ds.charAt(ds.length - 1)
  start() if hour >= 24
  sec = sec - 60 * min + ""
  sec = prependZeroCheck sec
  min = min - 60 * hour + ""
  min = prependZeroCheck min
  hour = prependZeroCheck hour
  hour + ":" + min + ":" + sec + "." + ds
  
prependZeroCheck = (time) ->
  time = time + "" # convert from int to string
  unless time.charAt(time.length - 2) == ""
    time = time.charAt(time.length - 2) + time.charAt(time.length - 1)
  else
    time = 0 + time.charAt(time.length - 1)

reset = ->
  flagStop = 0
  stopTime = 0
  window.clearTimeout refresh
  if flagClock == 1
    resetDate = new Date()
    resetTime = resetDate.getTime()
    counter resetTime
  else
    $(clock).val "00:00:00.0"

@StopWatch = {
  start: start
  reset: reset
  addZero: prependZeroCheck
}