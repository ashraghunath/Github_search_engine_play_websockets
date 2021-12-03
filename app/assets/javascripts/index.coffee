$ ->
  # Requests a web socket from the server for two-way fully duplex communication
  ws = new WebSocket $("#gitterific-home").data("ws-url")
  # On receiving a message, checks the response type and renders data accordingly
  ws.onmessage = (event) ->
    message = JSON.parse event.data
    switch message.responseType
      when "repositoryDetails"
        $("#search-page").hide()
        $("#search-page-result").hide()
        $("#topic-page-result").hide()
        ComposeRepositoryDetailsHtml(message)
        $("#repository-details").show()
      when "searchResults"
        $("#search-page").show()
        ComposeSearchPageHtml(message)
        $("#search-page-result").show()
        $("#repository-details").hide()
        $("#topic-page-result").hide()
      when "topicsDetails"
        $("#search-page").hide()
        $("#search-page-result").hide()
        $("#repository-details").hide()
        ComposeTopicSearchHtml(message)
        $("#topic-page-result").show()

  $("#searchForm").submit (event) ->
      event.preventDefault()
      phrase = $("#phrase").val()
      if phrase == ""
        alert "search cant be empty"
        return false
      else
        ws.send(JSON.stringify({searchPage: phrase}))
        $("#phrase").val("")
        return

  $("#search-page-result").on "click", "a.repository-details", (event) ->
    event.preventDefault()
    ws.send(JSON.stringify({repositoryDetails: $(this).text(), username: $(this).attr("username")}))
    return
  $("#search-page-result").on "click", "a.topic-link", (event) ->
    event.preventDefault()
    ws.send(JSON.stringify({topicsDetails: $(this).text()}))
    return
  $("#topic-page-result").on "click", "a.topic-link", (event) ->
    event.preventDefault()
    ws.send(JSON.stringify({topicsDetails: $(this).text()}))
    return
  $("#topic-page-result").on "click", "a.repository-details", (event) ->
    event.preventDefault()
    ws.send(JSON.stringify({repositoryDetails: $(this).text(), username: $(this).attr("username")}))
    return

ComposeSearchPageHtml =  (message) ->
  $("#search-page-result").empty()
  keys = message.searchMap.keys
  for key,value of message.searchMap
    if(typeof value == "object")
        $('#search-page-result').append "<br/><br/><b>" + "Search term :  " +key + "<br/><br/>"
        searchTable = $("<table>").prop("class", "table").prop("border","1")
        searchTable.append "<thead><tr><th>User</th><th>Repository</th><th>Topics</th></thead><tbody>"
        getSearchDetails value, searchTable
        $("#search-page-result").append(searchTable)


getSearchDetails = (objectValue, searchTable ) ->
        for key,value of objectValue
            searchData = $("<tr>")
            if(typeof value == "object")
                getSearchRepoValues value , searchData
            searchTable.append(searchData)


getSearchRepoValues = (objectValue, searchData ) ->
        for key,value of objectValue
            if(key=="owner")
                userLink = $("<a>").text(value).attr("class", "user-details")
                owner = $("<td>").append(userLink).append("</td>")
            else if(key=="name")
                repositoryLink =  $("<a>").text(value).attr("class", "repository-details").attr("username",objectValue['owner'])
                repository = $("<td>").append(repositoryLink).append("</td>")
            else if(key=="topics")
                topicsData =  $("<td>")
                for element,val of value
                    topicLink =  $("<a>").text(val).attr("class","topic-link")
                    topicsData.append(topicLink).append("</td>")
        searchData.append(owner).append(repository).append(topicsData).append("</tr>")

ComposeTopicSearchHtml = (message) ->
    $("#mainBanner").empty()
    $("#mainBanner").removeAttr("style")
    $("#mainBanner").append($("<h1>").text("Topics Search"))
    $("#mainBanner").attr("style","margin-left: 450px;")
    $("#topic-page-result").empty()
    topicName = message.searchProfile.searchProfile.keyword
    $("#topic-page-result").append($("<p>").text("sdadsa"))
    $("#mainBanner").append($("<h3>").text("Repository from topic"+ topicName))
    searchTable = $("<table>").prop("class", "table").prop("border","1")
    searchTable.append "<thead><tr><th>Repository</th><th>User</th><th>Topics</th></thead><tbody>"
    for repository in message.searchProfile.searchProfile.repos
        searchData = $("<tr>")
        repositoryLink = $("<a>").text(repository.name).attr("class", "repository-details").attr("username",repository.name)
        repository_user = $("<td>").append(repositoryLink).append("</td>")
        searchData.append(repository_user)
        userProfileLink = $("<a>").text(repository.owner).attr("class", "user-details")
        userData  = $("<td>").append(userProfileLink).append("</td>")
        searchData.append(userProfileLink)
        topicList =$("<p>").text("topics:")
        for topic in repository.topics
            topicLink = $("<a>").text(topic).attr("href", "/getReposByTopics/"+topic).attr("class","topic-link")
            topicList.append(topicLink)
        topicData = $("<td>").append(topicList).append("</td>")
        searchData.append(topicData)
        searchData.append($("</tr>"))
        searchTable.append(searchData)
    searchTable.append($("</tbody>"))
    $("#topic-page-result").append(searchTable)

ComposeRepositoryDetailsHtml = (message) ->
  $("#mainBanner").empty()
  $("#mainBanner").removeAttr("style")
  $("#mainBanner").append("<h1>").text("Repository Details")
  $("#mainBanner").attr("style","margin-left: 450px;")
  $("#repository-details").empty()


  repositoryName = message.repositoryDetails.name
  username = message.repositoryDetails.owner.login

  div = $("<div>").addClass("container-fluid")
  ul = $("<ul>").addClass("nav navbar-nav navbar-right").attr("id","repo-page-hyperlinks")
  issuesSpan = $('<span>').addClass("glyphicon glyphicon-stats").append("</span>")
  issuesSpan2 = $('<span>').addClass("glyphicon glyphicon-stats").append("</span>")
  issuesStats = $("<a>").attr("username",username).attr("repositoryName",repositoryName).append("</a>").text(" Issues Statistics ").append(issuesSpan)
  li =  $("<li>").append(issuesStats).append("</li>")
  commitStats = $("<a>").attr("username",username).attr("repositoryName",repositoryName).append("</a>").text(" Commit Statistics ").append(issuesSpan2)
  li2 =  $("<li>").append(commitStats).append("</li>")
  ul.append(li).append(li2).append("</ul>")
  div.append(ul).append("</div>")
  $("#reponavbar").append(div)

  dlList = $("<dl>").prop("class","row")
  $('#repository-details').append(dlList)
  for key,value of message.repositoryDetails
    if(typeof value == "object")
      getRepositoryDetails value, repositoryName, dlList
    else if(key == "openIssues" || key == "createdAt" || key == "updatedAt" || key == "pushedAt"  || key == "watchers" || key == "description" || key == "name" || key == "hasWiki" || key == "hasIssues" || key == "hasDownloads" || key == "masterBranch" || key == "forks" || key == "size" )
      if(value!=null)
          h4key = $("<h4>").text(key).append("</h4>")
          dt = $("<dt>").prop("class", "col-sm-3").append(h4key)
          h4value = $("<h4>").text(value).append("</h4>")
          dd = $("<dd>").prop("class", "col-sm-9").append(h4value)
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
            if(key == "login")
                  if(value!=null)
                      userProfileLink = $("<a>").text(value).attr("href", "/user-profile/" + repositoryName)
                      userProfileLink.addClass("user-profile-link")
                      h4key = $("<h4>").text(key).append("</h4>")
                      dt = $("<dt>").prop("class", "col-sm-3").append(h4key)
                      h4value = $("<h4>").append(userProfileLink).append("</h4>")
                      dd = $("<dd>").prop("class", "col-sm-9").append(h4value)
                      dlList.append(dt).append(dd)
                      $('#repository-details').append(dlList)
            else if(key == "htmlUrl")
                  if(value!=null)
                      userProfileLink = $("<a>").text(value+"/"+repositoryName).attr("href", value+"/"+ repositoryName)
                      userProfileLink.addClass("user-profile-link")
                      h4key = $("<h4>").text(key).append("</h4>")
                      dt = $("<dt>").prop("class", "col-sm-3").append(h4key)
                      h4value = $("<h4>").append(userProfileLink).append("</h4>")
                      dd = $("<dd>").prop("class", "col-sm-9").append(h4value)
                      dlList.append(dt).append(dd)
                      $('#repository-details').append(dlList)
            else if(key == "openIssues" || key == "updatedAt" || key == "pushedAt"  || key == "watchers" || key == "description" || key == "hasWiki" || key == "hasIssues" || key == "hasDownloads" || key == "masterBranch" || key == "forks" || key == "size" )
                   if(value!=null)
                       dt = $("<dt>").prop("class", "col-sm-3").text(key)
                       dd = $("<dd>").prop("class", "col-sm-9").text(value)
                       dlList.append(dt).append(dd)
                       $('#repository-details').append(dlList)

