reset = ->
  StopWatch.reset()
  Map.reset()
  $('#dashboard').slideUp()
  $("#player")[0].currentTime = 0

initialize = ->
  $("#start,#reset").removeAttr "disabled"

  $('#start').click ->
    if ($(this).text() == 'Stop')
      $('#actions').slideDown()
      $('.content').height(700)
      $('#start').width('45%')
      $('#reset').show()
      $('#title').focus()
    else
      $('#actions').slideUp()
      $('#actions').hide()
      $('.content').height(500)
      $('#start').width('100%')
      $('#reset').hide()
    StopWatch.start this, $('#clock')
    $('#dashboard').slideDown()
    Map.start()

  $('#reset').click ->
    reset()
    $('.content').height('auto')

  $('#no-music').click ->
    if $("#no-music").is(":checked")
      $("#player")[0].pause()
    else
      $("#player")[0].play()

  $('#save').click ->
    $.ajax
      type: 'POST'
      dataType: 'jsonp'
      url: $('#save').attr('rel')
      data:
        'workout.id': ''
        'workout.title': $('#title').val()
        'workout.description': $('#description').val()
        'workout.duration': $('#clock').val()
        'workout.distance': $('#distance').text()
      error: ->
        alert('Posting failed, please try again.')
      success: (data) ->
        $('.alert-message').remove()
        msg = 'Your workout was successfully recorded.'
        alert = $('<div class="alert-message success fade in" data-alert="alert">')
        alert.html('<a class="close" href="#">&times;</a>' + msg);
        alert.insertBefore($('.span10'))
        reset()

  flipForm = ->
    flipper = $('#flipper')
    if (flipper.hasClass('flipped'))
      $(this).removeClass('flipback')
      flipper.removeClass('flipped')
      $('.front.face').show()
      $('.face').css('-webkit-backface-visibility', 'hidden');
    else
      $(this).addClass('flipback')
      flipper.addClass('flipped')
      $('.content').height(400)
    setTimeout toggleForm, 1000

  toggleForm = ->
    if ($('#flipper').hasClass('flipped'))
      $('.face').css('-webkit-backface-visibility', 'visible');
      $('.front.face').hide()
    else
      $('.face').css('-webkit-backface-visibility', 'hidden');
      $('.front.face').show()
      $('.content').height('auto')

  $('.flip').click (e) ->
    flipForm()

  #- Keep forms in sync
  $('.title').keyup ->
    $('.title').val $(this).val()
  $('.description').keyup ->
    $('.description').val $(this).val()

@PlayMore = {
  initialize: initialize
  reset: reset
}