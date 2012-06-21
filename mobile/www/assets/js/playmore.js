(function() {
  var initialize, reset;
  reset = function() {
    StopWatch.reset();
    Map.reset();
    $('#dashboard').slideUp();
    return $("#player")[0].currentTime = 0;
  };
  initialize = function() {
    $("#start,#reset").removeAttr("disabled");
    $('#start').click(function() {
      if ($(this).text() === 'Stop') {
        $('#actions').slideDown();
        $('.content').height(700);
        $('#start').width('45%');
        $('#reset').show();
        $('#title').focus();
      } else {
        $('#actions').slideUp();
        $('#actions').hide();
        $('.content').height(500);
        $('#start').width('100%');
        $('#reset').hide();
      }
      StopWatch.start(this, $('#clock'));
      $('#dashboard').slideDown();
      return Map.start();
    });
    $('#reset').click(function() {
      reset();
      return $('.content').height('auto');
    });
    $('#no-music').click(function() {
      if ($("#no-music").is(":checked")) {
        return $("#player")[0].pause();
      } else {
        return $("#player")[0].play();
      }
    });
    $.ajaxSetup({
      headers: {"X-Requested-With":"XMLHttpRequest"}
    });
    $('#save').click(function() {
      return $.ajax({
        type: 'POST',
        url: $('#save').attr('rel'),
        data: {
          'title': $('#title').val(),
          'description': $('#description').val(),
          'duration': $('#clock').val(),
          'distance': $('#distance').text()
        },
        error: function(jqXHR, textStatus, errorThrown) {
          return alert('Posting failed, please try again.');
        },
        success: function(data, textStatus) {
          var alert, msg;
          $('.alert').remove();
          msg = 'Your workout was successfully recorded.';
          alert = $('<div class="alert alert-success fade in" data-alert="alert">');
          alert.html('<a class="close" data-dismiss="alert">&times;</a>' + msg);
          alert.insertBefore($('#display'));
          $('.content form').get(0).reset();
          return reset();
        }
      });
    });
    $('.title').keyup(function() {
      return $('.title').val($(this).val());
    });
    $('.description').keyup(function() {
      return $('.description').val($(this).val());
    });
    return $('a[data-toggle="tab"]').on('shown', function(e) {
      if ($(e.target).attr('id') === 'form') {
        $('#workoutDuration').val($('#clock').val());
        return $('#workoutDistance').val($('#distance').text());
      }
    });
  };
  this.PlayMore = {
    initialize: initialize,
    reset: reset
  };
}).call(this);
