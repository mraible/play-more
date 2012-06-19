(function() {
  var clock, counter, flagClock, flagStop, formatTime, prependZeroCheck, refresh, reset, start, stopTime;
  flagClock = 0;
  flagStop = 0;
  stopTime = 0;
  refresh = null;
  clock = null;
  start = function(button, display) {
    var startDate, startTime;
    clock = display;
    startDate = new Date();
    startTime = startDate.getTime();
    if (flagClock === 0) {
      $(button).html("Stop");
      flagClock = 1;
      counter(startTime, display);
      if (!$("#no-music").is(":checked")) {
        return $("#player")[0].play();
      }
    } else {
      $(button).html("Start");
      flagClock = 0;
      flagStop = 1;
      return $("#player")[0].pause();
    }
  };
  counter = function(startTime) {
    var callback, currentTime, timeDiff;
    currentTime = new Date();
    timeDiff = currentTime.getTime() - startTime;
    if (flagStop === 1) {
      timeDiff = timeDiff + stopTime;
    }
    if (flagClock === 1) {
      $(clock).val(formatTime(timeDiff, ""));
      callback = function() {
        return counter(startTime);
      };
      return refresh = setTimeout(callback, 10);
    } else {
      window.clearTimeout(refresh);
      return stopTime = timeDiff;
    }
  };
  formatTime = function(rawTime, roundType) {
    var ds, hour, min, sec;
    if (roundType === "round") {
      ds = Math.round(rawTime / 100) + "";
    } else {
      ds = Math.floor(rawTime / 100) + "";
    }
    sec = Math.floor(rawTime / 1000);
    min = Math.floor(rawTime / 60000);
    hour = Math.floor(rawTime / 3600000);
    ds = ds.charAt(ds.length - 1);
    if (hour >= 24) {
      start();
    }
    sec = sec - 60 * min + "";
    sec = prependZeroCheck(sec);
    min = min - 60 * hour + "";
    min = prependZeroCheck(min);
    hour = prependZeroCheck(hour);
    return hour + ":" + min + ":" + sec + "." + ds;
  };
  prependZeroCheck = function(time) {
    time = time + "";
    if (time.charAt(time.length - 2) !== "") {
      return time = time.charAt(time.length - 2) + time.charAt(time.length - 1);
    } else {
      return time = 0 + time.charAt(time.length - 1);
    }
  };
  reset = function() {
    var resetDate, resetTime;
    flagStop = 0;
    stopTime = 0;
    window.clearTimeout(refresh);
    if (flagClock === 1) {
      resetDate = new Date();
      resetTime = resetDate.getTime();
      return counter(resetTime);
    } else {
      return $(clock).val("00:00:00.0");
    }
  };
  this.StopWatch = {
    start: start,
    reset: reset,
    addZero: prependZeroCheck
  };
}).call(this);
