# Geolocation with HTML 5 and Google Maps API based on example from maxheapsize: 
# http://maxheapsize.com/2009/04/11/getting-the-browsers-geolocation-with-html-5/
# This script is by Merge Database and Design, http://merged.ca/ -- if you use some, all, or any of this code, please offer a return link.

map = null
mapCenter = null
geocoder = null
latlng = null

initialize = ->	
  if navigator.geolocation
    navigator.geolocation.getCurrentPosition showMap

showMap = (position) ->
  latitude = position.coords.latitude
  longitude = position.coords.longitude
  mapOptions = {
    zoom: 15,
    mapTypeId: google.maps.MapTypeId.ROADMAP
  }
  map = new google.maps.Map(document.getElementById("map"), mapOptions)
  latlng = new google.maps.LatLng(latitude, longitude)
  map.setCenter(latlng)

  geocoder = new google.maps.Geocoder()
  geocoder.geocode({'latLng': latlng}, addAddressToMap)

addAddressToMap = (results, status) ->
  if (status == google.maps.GeocoderStatus.OK) 
    if (results[1]) 
      marker = new google.maps.Marker({
          position: latlng,
          map: map
      })
      $('#location').html('Your location: ' + results[0].formatted_address)
  else 
    alert "Sorry, we were unable to geocode that address."

start = ->
  setTimeout initialize, 500

reset = ->
  alert 'reset'
  
@Map = {
  start: start
  reset: reset
  getMap: map
}