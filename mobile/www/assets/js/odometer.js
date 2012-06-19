(function() {
  var appendZero, calculateDistance, callback, distance, geolocationOptions, lastPos, log, map, reset, showDistance, start, startPos, watchId;
  calculateDistance = function(lat1, lon1, lat2, lon2) {
    var R, a, c, d, dLat, dLon;
    R = 6371;
    dLat = (lat2 - lat1).toRad();
    dLon = (lon2 - lon1).toRad();
    a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(lat1.toRad()) * Math.cos(lat2.toRad()) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
    c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    d = R * c;
    return d / 1.609344;
  };
  startPos = null;
  lastPos = null;
  distance = 0;
  geolocationOptions = {
    timeout: 10000,
    enableHighAccuracy: true
  };
  callback = null;
  map = null;
  log = null;
  watchId = null;
  start = function(config) {
    log = config.log;
    callback = config.callback;
    map = config.map;
    if (Modernizr.geolocation) {
      if (!config.position) {
        navigator.geolocation.getCurrentPosition((function(position) {
          startPos = position;
          lastPos = position;
          $("#startLat").html(startPos.coords.latitude);
          return $("#startLon").html(startPos.coords.longitude);
        }), null, geolocationOptions);
      } else {
        startPos = config.position;
        lastPos = config.position;
      }
      return watchId = navigator.geolocation.watchPosition(showDistance, null, geolocationOptions);
    }
  };
  showDistance = function(position) {
    var hours, lat, lng, minutes, seconds, time;
    lat = position.coords.latitude;
    lng = position.coords.longitude;
    $("#currentLat").html(lat);
    $("#currentLon").html(lng);
    if (lastPos) {
      distance += calculateDistance(lastPos.coords.latitude, lastPos.coords.longitude, lat, lng);
      $("#distance").html(distance.toFixed(2));
      callback(lastPos, position, map);
    }
    lastPos = position;
    if (log) {
      time = new Date;
      if ($('#log').length === 0) {
        $('<div/>').attr('id', 'log').appendTo($('body'));
      }
      hours = appendZero(time.getHours());
      minutes = appendZero(time.getMinutes());
      seconds = appendZero(time.getHours());
      $('#log').append(time.toDateString() + ' ' + hours + ':' + minutes + ':' + seconds);
      return $('#log').append(' | <strong>' + distance + '</strong> | ' + lat + '/' + lng + '<br/>');
    }
  };
  Number.prototype.toRad = function() {
    return this * Math.PI / 180;
  };
  appendZero = function(time) {
    time = time + "";
    if (time.charAt(time.length - 2) !== "") {
      return time = time.charAt(time.length - 2) + time.charAt(time.length - 1);
    } else {
      return time = 0 + time.charAt(time.length - 1);
    }
  };
  reset = function() {
    startPos = null;
    lastPos = null;
    distance = 0;
    $('#log').empty();
    $('#distance').html('0');
    if (watchId) {
      return navigator.geolocation.clearWatch(watchId);
    }
  };
  this.Odometer = {
    start: start,
    reset: reset
  };
}).call(this);
