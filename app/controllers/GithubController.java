package controllers;

import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Http;
import play.mvc.Result;
import services.GithubService;

import javax.inject.Inject;
import Helper.SessionHelper;

import java.util.concurrent.CompletionStage;
import play.cache.*;
import views.html.repository;

import static play.mvc.Results.ok;

public class GithubController {

	private final FormFactory formFactory;
	private final GithubService githubService;
	private final SessionHelper sessionHelper;
	private AsyncCacheApi cache;

	@Inject
	public GithubController(FormFactory formFactory, GithubService githubService, SessionHelper sessionHelper, AsyncCacheApi cache) {
		this.formFactory = formFactory;
		this.githubService = githubService;
		this.sessionHelper = sessionHelper;
		this.cache=cache;
	}

	public Result index(Http.Request request) {
		
		if(sessionHelper.checkSessionExist(request))
		return ok(views.html.index.render(sessionHelper.getSearchResultsForCurrentSession(request, null, null)));
		else
	    return ok(views.html.index.render(null)).addingToSession(request, sessionHelper.getSessionKey(), sessionHelper.generateSessionValue());
	}

	public CompletionStage<Result> search(Http.Request request) {
		DynamicForm form = formFactory.form().bindFromRequest(request);
		String phrase = form.get("phrase");
		CompletionStage<Result> resultCompletionStage = githubService
				.searchResults(request, phrase).thenApply(map -> ok(views.html.index.render(map)));
		return resultCompletionStage;
	}

	/**
	 * Returns the Repository details for the provided username and repository name
	 * 
	 * @author Ashwin Raghunath 40192120
	 * @param userName       the user who owns the repository.
	 * @param repositoryName the name of the repository to be searched for.
	 * @return CompletionStage<Result> represents the async response containing the
	 *         process stage of Result object
	 */
	public CompletionStage<Result> getRepositoryDetails(String userName, String repositoryName) {

		CompletionStage<Result> results = cache
				.getOrElseUpdate((userName +"."+repositoryName),
						() -> githubService.getRepositoryDetails(userName, repositoryName)
				.thenApplyAsync(repository -> ok(views.html.repository.render(repository))));
		return results;
	}

	/**
	 * Returns the Repository Issues for the provided username and repository name
	 * 
	 * @author Anushka Shetty 401923
	 * @param userName       the user who owns the repository.
	 * @param repositoryName the name of the repository to be searched for.
	 * @return CompletionStage<Result> represents the async response containing the
	 *         process stage of Result object
	 */
	public CompletionStage<Result> getIssues(String userName, String repositoryName) {
		CompletionStage<Result> resultCompletionStage = githubService.getAllIssues(userName, repositoryName)
				.thenApply(issues -> ok(views.html.issues.render(issues)));

		return resultCompletionStage;
	}

	public CompletionStage<Result> getUserDetails(String userName){
		CompletionStage<Result> result = githubService.getUserDetails(userName)
				.thenApply(user -> ok(views.html.user.render(user)));
		return result;
	}
	/** Returns the Repositories that contains the given topic
	 * @author Trusha Patel
	 * @param topic_name of the repository
	 * @return CompletionStage<Result> represents the async response containing the process stage of Result object
	 */

	public CompletionStage<Result> getReposByTopics(String topic_name) {
		CompletionStage<Result> resultCompletionStage = githubService.getRepositoriesByTopics(topic_name)
				.thenApply(repositories -> ok(views.html.topics.render(repositories)));

		return resultCompletionStage;
	}

}
