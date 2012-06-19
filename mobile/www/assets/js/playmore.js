(function() {
  var initialize, reset;
  reset = function() {
    StopWatch.reset();
    Map.reset();
    $('#dashboard').slideUp();
    return $("#player")[0].currentTime = 0;
  };
  initialize = function() {
    var flipForm, toggleForm;
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
    $('#save').click(function() {
      return $.ajax({
        type: 'POST',
        dataType: 'jsonp',
        url: $('#save').attr('rel'),
        data: {
          'workout.id': '',
          'workout.title': $('#title').val(),
          'workout.description': $('#description').val(),
          'workout.duration': $('#clock').val(),
          'workout.distance': $('#distance').text()
        },
        error: function() {
          return alert('Posting failed, please try again.');
        },
        success: function(data) {
          var alert, msg;
          $('.alert-message').remove();
          msg = 'Your workout was successfully recorded.';
          alert = $('<div class="alert-message success fade in" data-alert="alert">');
          alert.html('<a class="close" href="#">&times;</a>' + msg);
          alert.insertBefore($('.span10'));
          return reset();
        }
      });
    });
    flipForm = function() {
      var flipper;
      flipper = $('#flipper');
      if (flipper.hasClass('flipped')) {
        $(this).removeClass('flipback');
        flipper.removeClass('flipped');
        $('.front.face').show();
        $('.face').css('-webkit-backface-visibility', 'hidden');
      } else {
        $(this).addClass('flipback');
        flipper.addClass('flipped');
        $('.content').height(400);
      }
      return setTimeout(toggleForm, 1000);
    };
    toggleForm = function() {
      if ($('#flipper').hasClass('flipped')) {
        $('.face').css('-webkit-backface-visibility', 'visible');
        return $('.front.face').hide();
      } else {
        $('.face').css('-webkit-backface-visibility', 'hidden');
        $('.front.face').show();
        return $('.content').height('auto');
      }
    };
    $('.flip').click(function(e) {
      return flipForm();
    });
    $('.title').keyup(function() {
      return $('.title').val($(this).val());
    });
    return $('.description').keyup(function() {
      return $('.description').val($(this).val());
    });
  };
  this.PlayMore = {
    initialize: initialize,
    reset: reset
  };
}).call(this);
