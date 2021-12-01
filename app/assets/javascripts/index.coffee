$ ->
  # Requests a web socket from the server for two-way fully duplex communication
  ws = new WebSocket $("#gitterific-home").data("ws-url")
  # On receiving a message, checks the response type and renders data accordingly
  ws.onmessage = (event) ->
    message = JSON.parse event.data
    switch message.responseType
      when "repositoryDetails"
        $("#search-page").hide()
        ComposeRepositoryDetailsHtml(message)
        $("#repository-details").show()

  $("#search-page").on "click", "a.repository-details", (event) ->
    event.preventDefault()
    ws.send(JSON.stringify({repositoryDetails: $(this).text(), username: $(this).attr("username")}))
    return

ComposeRepositoryDetailsHtml = (message) ->
  $("#mainBanner").empty()
  $("#mainBanner").removeAttr("style")
  $("#mainBanner").append("<h1>").text("Repository Details")
  $("#mainBanner").attr("style","margin-left: 450px;")
  $("#repository-details").empty()

  repositoryName = message.repositoryProfile.name
  username = message.repositoryProfile.owner.login
  dlList = $("<dl>").prop("class","row")
  $('#repository-details').append(dlList)
  for key,value of message.repositoryProfile
    if(typeof value == "object")
      getRepositoryDetails value, repositoryName, dlList
    else if(key == "openIssues" || key == "createdAt" || key == "updatedAt" || key == "pushedAt"  || key == "watchers" || key == "description" || key == "name" || key == "hasWiki" || key == "hasIssues" || key == "hasDownloads" || key == "masterBranch" || key == "forks" || key == "size" )
      dt = $("<dt>").prop("class", "col-sm-3").text(key)
      dd = $("<dd>").prop("class", "col-sm-9").text(value)
      dlList.append(dt).append(dd)
      $('#repository-details').append(dlList)

  $('#repository-details').append "<br><b><h3>Issues of repository :  "+repositoryName+"</h3></b>"
  if message.issueList.length > 0
      issuesTable = $("<table>").prop("class", "table").prop("border","1")
      issuesTable.append "<thead><tr><th>Sl no.</th><th>Issues</th></thead><tbody>"
      $("#repository-details").append(issuesTable)

      for key,value of message.issueList

            issueData = $('<tr>')

            index = parseInt(key) + 1
            issueSlNo = $("<td>").text(index).append("</td>")
            issueTitle = $("<td>").text(value).append("</td>")

            issueData.append(issueSlNo).append(issueTitle)
            issuesTable.append(issueData)
            issuesTable.append("</tbody>")
      $("#repository-details").append(issuesTable).append("</tbody><br>")
  else
      $("#repository-details").append("No issues found")


getRepositoryDetails = (objectValue, repositoryName, dlList ) ->
        for key,value of objectValue
            if(key == "login" || key == "htmlUrl" )
                  userProfileLink = $("<a>").text(value).attr("href", "/user-profile/" + repositoryName)
                  userProfileLink.addClass("user-profile-link")
                  dt = $("<dt>").prop("class", "col-sm-3").text(key)
                  dd = $("<dd>").prop("class", "col-sm-9").append(userProfileLink)
                  dlList.append(dt).append(dd)
                  $('#repository-details').append(dlList)
            else if(key == "openIssues" || key == "updatedAt" || key == "pushedAt"  || key == "watchers" || key == "description" || key == "hasWiki" || key == "hasIssues" || key == "hasDownloads" || key == "masterBranch" || key == "forks" || key == "size" )
                   dt = $("<dt>").prop("class", "col-sm-3").text(key)
                   dd = $("<dd>").prop("class", "col-sm-9").text(value)
                   dlList.append(dt).append(dd)
                   $('#repository-details').append(dlList)

