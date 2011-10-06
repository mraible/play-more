calculateDistance = (lat1, lon1, lat2, lon2) ->
  R = 6371
  dLat = (lat2 - lat1).toRad()
  dLon = (lon2 - lon1).toRad()
  a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(lat1.toRad()) * Math.cos(lat2.toRad()) * Math.sin(dLon / 2) * Math.sin(dLon / 2)
  c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
  d = R * c
  d

window.onload = ->
  startPos = ""
  if navigator.geolocation
    navigator.geolocation.getCurrentPosition ((position) ->
      startPos = position
      $("#startLat").html(startPos.coords.latitude)
      $("#startLon").html(startPos.coords.longitude)
    ), (error) ->
      alert "Error occurred. Error code: " + error.code
    
    navigator.geolocation.watchPosition (position) ->
      $("#currentLat").html(position.coords.latitude)
      $("#currentLon").html(position.coords.longitude)
      $("#distance").html(calculateDistance(startPos.coords.latitude, startPos.coords.longitude, position.coords.latitude, position.coords.longitude))

Number::toRad = ->
  this * Math.PI / 180