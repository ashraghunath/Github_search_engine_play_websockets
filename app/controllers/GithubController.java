package controllers;

import Helper.SessionHelper;
import play.cache.AsyncCacheApi;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Http;
import play.mvc.Result;
import services.GithubService;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

import static play.mvc.Results.ok;


/**
 * Handles all incoming http requests and maps it to a particular method
 */
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
	
	/**
	 * Returns Result View for the Search page along with the session
	 *
	 * @param HTTP request object
	 * @return Result View for the search page
	 * @author Ashwin Raghunath 40192120
	 */
	
	public Result index(Http.Request request) {
		
		if(sessionHelper.checkSessionExist(request))
		return ok(views.html.index.render(sessionHelper.getSearchResultsForCurrentSession(request, null, null)));
		else
	    return ok(views.html.index.render(null)).addingToSession(request, sessionHelper.getSessionKey(), sessionHelper.generateSessionValue());
	}

	/**
	 * Returns Result View for the Search page after search click
	 * @param request object
	 * @return Result View for the search page after the click
	 * @author Anmol malhotra 40201452
	 */
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
	 * @param userName       the user who owns the repository.
	 * @param repositoryName the name of the repository to be searched for.
	 * @return CompletionStage<Result> represents the async response containing the
	 *         process stage of Result object of the repository details like name and owner
	 * @author Ashwin Raghunath 40192120
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
	 *
	 * @param userName       the user who owns the repository.
	 * @param repositoryName the name of the repository to be searched for.
	 * @return CompletionStage<Result> represents the async response containing the
	 *         process stage of Result object with issue details
	 *  @author Anushka Shetty 401923
	 */
	public CompletionStage<Result> getIssues(String userName, String repositoryName) {
		CompletionStage<Result> resultCompletionStage = githubService.getAllIssues(userName, repositoryName)
				.thenApply(issues -> ok(views.html.issues.render(issues)));

		return resultCompletionStage;
	}

	/** Returns the Repositories that contains the given topic
	 *
	 * @param topic_name of the repository
	 * @return CompletionStage<Result> represents the async response containing the process stage of Result object with repositories
	 * 			details matching the queried topics
	 * @author Trusha Patel 40162614
	 */


	public CompletionStage<Result> getReposByTopics(String topic_name) {
		CompletionStage<Result> results = githubService.getReposByTopics(topic_name)
								.thenApply(repository -> ok(views.html.topics.render(repository)));
		return results;

	}

	/** Returns the User Details for the provided user
	 *
	 * @param userName the user who owns the repository.
	 * @return CompletionStage<Result> represents the async response containing the process stage of Result object for the user details
	 *  @author Sourav Uttam Sinha 40175660
	 */

	public CompletionStage<Result> getUserDetails(String userName) {

		CompletionStage<Result> results = cache
				.getOrElseUpdate((userName + ".getUserDetails"),
						() -> githubService.getUserDetails(userName)
								.thenApplyAsync(user -> ok(views.html.user.render(user))));
		return results;
	}

	/**
	 *
	 * @param userName the owner of the repository
	 * @param repositoryName the name of the repository
	 * @return CompletionStage<Result> represents the async response containing the process stage of Result object for the repository commit details
	 * @author Anmol Malhotra
	 */

	public CompletionStage<Result> getRepositoryCommits(String userName, String repositoryName) {
		return githubService.getCommitsForRepository(userName, repositoryName)
				.thenApplyAsync(commits -> ok(views.html.commits.render(commits)));
	}

}
