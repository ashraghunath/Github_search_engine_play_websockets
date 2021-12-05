package controllers;

import Helper.SessionHelper;
import actors.SupervisorActor;
import play.cache.AsyncCacheApi;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.concurrent.HttpExecutionContext;
import play.libs.streams.ActorFlow;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.WebSocket;
import services.GithubService;

import java.util.concurrent.CompletionStage;

import static play.mvc.Results.ok;
import akka.actor.ActorSystem;
import akka.stream.Materializer;

import javax.inject.Inject;

/**
 * Handles all incoming http requests and maps it to a particular method
 */
public class GithubController {

	private final FormFactory formFactory;
	private final GithubService githubService;
	private final SessionHelper sessionHelper;
	private AsyncCacheApi cache;
	private final AssetsFinder assetsFinder;
	private HttpExecutionContext httpExecutionContext;
	@Inject
	private Materializer materializer;
	@Inject
	private ActorSystem actorSystem;

	@Inject
	public GithubController(HttpExecutionContext httpExecutionContext, AssetsFinder assetsFinder, FormFactory formFactory, GithubService githubService, SessionHelper sessionHelper, AsyncCacheApi cache, Materializer materializer, ActorSystem actorSystem) {
		this.formFactory = formFactory;
		this.githubService = githubService;
		this.sessionHelper = sessionHelper;
		this.cache=cache;
		this.actorSystem=actorSystem;
		this.materializer=materializer;
		this.assetsFinder=assetsFinder;
		this.httpExecutionContext=httpExecutionContext;
//		actorSystem.actorOf(TimeActor.props(), "timeActor");
	}
	
	/**
	 * Returns Result View for the Search page along with the session
	 * 
	 * @author Anushka Shetty 40192371
	 * @param request object
	 * @return Result View for the search page
	 */
	public Result index(Http.Request request) {
		if(sessionHelper.checkSessionExist(request))
		return ok(views.html.index.render(request, sessionHelper.getSearchResultsForCurrentSession(request, null, null)));
		else
	    return ok(views.html.index.render(request,null)).addingToSession(request, sessionHelper.getSessionKey(), sessionHelper.generateSessionValue());
	}

	/**
	 * Establishes socket connection with supervisor actor
	 * @return Websocket after initialization
	 * @author Ashwin Raghunath
	 */
	public WebSocket ws() {
		return WebSocket.Json.accept(request -> ActorFlow.actorRef(out -> SupervisorActor.props(out, githubService, cache), actorSystem, materializer));
	}

	/**
	 * Returns Result View for the Search page after search click
	 *
	 * @param request object
	 * @return Result View for the search page after click
	 * @author Anmol malhotra 40201452
	 */
	public CompletionStage<Result> search(Http.Request request) {
		DynamicForm form = formFactory.form().bindFromRequest(request);
		String phrase = form.get("phrase");
		CompletionStage<Result> resultCompletionStage = githubService
				.searchResults(request, phrase).thenApply(map -> ok(views.html.index.render(request,map)));
		return resultCompletionStage;
	}

	/**
	 * Returns the Repository details for the provided username and repository name
	 * 
	 * @author Ashwin Raghunath 40192120
	 * @param userName       the user who owns the repository.
	 * @param repositoryName the name of the repository to be searched for.
	 * @return represents the async response containing the
	 *         process stage of Result object for the repository details
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
	 * @return represents the async response containing the
	 *         process stage of Result object for the issue details
	 */
	public CompletionStage<Result> getIssues(String userName, String repositoryName) {
		CompletionStage<Result> resultCompletionStage = githubService.getAllIssues(userName, repositoryName)
				.thenApply(issues -> ok(views.html.issues.render(issues)));

		return resultCompletionStage;
	}

	/** Returns the Repositories that contains the given topic
	 * @author Trusha Patel
	 * @param topic_name of the repository
	 * @return represents the async response containing the process stage of Result
	 * 			object for the repository details matching the topic
	 */

	public CompletionStage<Result> getReposByTopics(String topic_name) {
		CompletionStage<Result> results = githubService.getReposByTopics(topic_name)
				.thenApplyAsync(repository -> ok(views.html.topics2.render(topic_name,
								repository.get("searchProfile"),assetsFinder)),
						httpExecutionContext.current());
		return results;

	}

	/** Returns the User Details for the provided user
	 * @author Sourav Uttam Sinha 40175660
	 * @param userName the user who owns the repository.
	 * @return represents the async response containing the process stage of Result object for the user details
	 */

	public CompletionStage<Result> getUserDetails(String userName) {

		CompletionStage<Result> results = cache
				.getOrElseUpdate((userName + ".getUserDetails"),
						() -> githubService.getUserDetails(userName)
								.thenApplyAsync(user -> ok(views.html.user.render(user))));
		return results;
	}

	/**
	 * Returns the repository commits' details for the given repository
	 *
	 * @param userName       owner of the repository
	 * @param repositoryName name of the repository
	 * @return represents the async response containing the process stage of Result object for the commits
	 * @param userName owner of the repository
	 * @param repositoryName name of the respository
	 * @return represents the async response containing the process stage of Result object for the commits
	 */

	public CompletionStage<Result> getRepositoryCommits(String userName, String repositoryName) {
		return githubService.getCommitsForRepository(userName, repositoryName)
				.thenApplyAsync(commits -> ok(views.html.commits.render(commits)));

	}
}
