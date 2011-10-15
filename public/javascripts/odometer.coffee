# http://www.movable-type.co.uk/scripts/latlong.html
calculateDistance = (lat1, lon1, lat2, lon2) ->
  R = 6371 # km
  dLat = (lat2 - lat1).toRad()
  dLon = (lon2 - lon1).toRad()
  a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(lat1.toRad()) * Math.cos(lat2.toRad()) * Math.sin(dLon / 2) * Math.sin(dLon / 2)
  c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
  d = R * c
  d / 1.609344

startPos = null
lastPos = null
distance = 0

start = (config) ->
  log = config.log
  callback = config.callback
  map = config.map

  if navigator.geolocation
    navigator.geolocation.getCurrentPosition ((position) ->
      startPos = position
      lastPos = position
      $("#startLat").html(startPos.coords.latitude)
      $("#startLon").html(startPos.coords.longitude)
    ), (error) ->
      msg = 'Unable to locate position. '
      switch error.code
        when error.TIMEOUT then msg += 'Timeout.'
        when error.POSITION_UNAVAILABLE then msg += 'Position unavailable.'
        when error.PERMISSION_DENIED then msg += 'Please turn on location services.'
        when error.UNKNOWN_ERROR then msg += error.code
      alert msg
    
    navigator.geolocation.watchPosition (position) ->
      lat = position.coords.latitude
      lng = position.coords.longitude
      $("#currentLat").html(lat)
      $("#currentLon").html(lng)
      if (lastPos)
        distance += calculateDistance(lastPos.coords.latitude, lastPos.coords.longitude, lat, lng)
        $("#distance").html(distance.toFixed(2))
        callback(lastPos, position, map)

      lastPos = position

      if (log)
        time = new Date
        if ($('#log').length == 0)
          $('<div/>').attr('id', 'log').appendTo($('body'));
        hours = appendZero time.getHours()
        minutes = appendZero time.getMinutes()
        seconds = appendZero time.getHours()
        $('#log').append(time.toDateString() + ' ' + hours + ':' + minutes + ':' + seconds)
        $('#log').append(' | <strong>' + distance + '</strong> | ' + lat + '/' + lng + '<br/>')

Number::toRad = ->
  this * Math.PI / 180
  
appendZero = (time) ->
  time = time + "" # convert from int to string
  unless time.charAt(time.length - 2) == ""
    time = time.charAt(time.length - 2) + time.charAt(time.length - 1)
  else
    time = 0 + time.charAt(time.length - 1)

reset = ->
  $('#log').html('')
  $('#distance').html('0')

@Odometer = {
  start: start
  reset: reset
}