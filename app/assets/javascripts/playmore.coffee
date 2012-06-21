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
      url: $('#save').attr('rel')
      data:
        'title': $('#title').val()
        'description': $('#description').val()
        'duration': $('#clock').val()
        'distance': $('#distance').text()
      error: (jqXHR, textStatus, errorThrown) ->
        alert('Posting failed, please try again.')
      success: (data, textStatus) ->
        $('.alert-message').remove()
        msg = 'Your workout was successfully recorded.'
        alert = $('<div class="alert alert-success fade in" data-alert="alert">')
        alert.html('<a class="close" data-dismiss="alert">&times;</a>' + msg);
        alert.insertBefore($('.span9'))
        $('#app form').get(0).reset()
        reset()

  #- Keep forms in sync
  $('.title').keyup ->
    $('.title').val $(this).val()
  $('.description').keyup ->
    $('.description').val $(this).val()

  $('a[data-toggle="tab"]').on('shown', (e) ->
    if ($(e.target).attr('id') == 'form')
      $('#workoutDuration').val $('#clock').val()
      $('#workoutDistance').val $('#distance').text()
  )

@PlayMore = {
  initialize: initialize
  reset: reset
}