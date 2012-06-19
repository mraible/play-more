(function() {
  var addAddressToMap, drawMap, geocoder, geolocationError, geolocationOptions, initialize, latlng, map, mapCenter, reset, showMap, start, timeoutId, tripCoordinates;
  map = null;
  mapCenter = null;
  geocoder = null;
  latlng = null;
  geolocationOptions = {
    timeout: 10000,
    enableHighAccuracy: true
  };
  timeoutId = null;
  initialize = function() {
    if (Modernizr.geolocation) {
      return navigator.geolocation.getCurrentPosition(showMap, geolocationError, geolocationOptions);
    }
  };
  showMap = function(position) {
    var latitude, longitude, mapOptions;
    latitude = position.coords.latitude;
    longitude = position.coords.longitude;
    mapOptions = {
      zoom: 15,
      mapTypeId: google.maps.MapTypeId.ROADMAP
    };
    map = new google.maps.Map(document.getElementById("map"), mapOptions);
    latlng = new google.maps.LatLng(latitude, longitude);
    map.setCenter(latlng);
    Odometer.start({
      position: position,
      callback: drawMap,
      map: map
    });
    geocoder = new google.maps.Geocoder();
    return geocoder.geocode({
      'latLng': latlng
    }, addAddressToMap);
  };
  addAddressToMap = function(results, status) {
    var marker;
    if (status === google.maps.GeocoderStatus.OK) {
      if (results[1]) {
        marker = new google.maps.Marker({
          position: latlng,
          map: map
        });
        return $('#location').html('Your location: ' + results[0].formatted_address);
      }
    } else {
      return alert("Sorry, we were unable to geocode that address.");
    }
  };
  tripCoordinates = [];
  drawMap = function(lastPos, newPos, map) {
    var tripPath;
    tripCoordinates.push(new google.maps.LatLng(lastPos.coords.latitude, lastPos.coords.longitude));
    tripCoordinates.push(new google.maps.LatLng(newPos.coords.latitude, newPos.coords.longitude));
    tripPath = new google.maps.Polyline({
      path: tripCoordinates,
      strokeColor: "#FF0000",
      strokeOpacity: 1.0,
      strokeWeight: 2
    });
    return tripPath.setMap(map);
  };
  geolocationError = function(error) {
    var alert, msg;
    msg = 'Unable to locate position. ';
    switch (error.code) {
      case error.TIMEOUT:
        msg += 'Timeout.';
        break;
      case error.POSITION_UNAVAILABLE:
        msg += 'Position unavailable.';
        break;
      case error.PERMISSION_DENIED:
        msg += 'Please turn on location services.';
        break;
      case error.UNKNOWN_ERROR:
        msg += error.code;
    }
    $('.alert-message').remove();
    alert = $('<div class="alert-message error fade in" data-alert="alert">');
    alert.html('<a class="close" href="#">&times;</a>' + msg);
    return alert.insertBefore($('.span10'));
  };
  start = function() {
    return timeoutId = setTimeout(initialize, 500);
  };
  reset = function() {
    tripCoordinates = [];
    $('#location').empty();
    Odometer.reset();
    if (timeoutId) {
      return clearTimeout(timeoutId);
    }
  };
  this.Map = {
    start: start,
    reset: reset
  };
}).call(this);
