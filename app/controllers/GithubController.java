package controllers;

import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Http;
import play.mvc.Result;
import services.GithubService;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

import static play.libs.Json.toJson;
import static play.mvc.Results.ok;

public class GithubController {

	private final FormFactory formFactory;
	private final GithubService githubService;

	@Inject
	public GithubController(FormFactory formFactory, GithubService githubService) {
		this.formFactory = formFactory;
		this.githubService = githubService;
	}

	public Result index(Http.Request request) {
		UUID uuid = UUID.randomUUID();
		String sessionKey = uuid.toString();
		return ok(views.html.index.render(null)).addingToSession(request, "key", sessionKey);
	}

	public Result search(Http.Request request) {
		DynamicForm form = formFactory.form().bindFromRequest(request);
		String phrase = form.get("phrase");
		CompletionStage<Result> resultCompletionStage = githubService
				.searchResults(request.session().get("key"), phrase).thenApply(map -> ok(views.html.index.render(map)));
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
		CompletionStage<Result> resultCompletionStage = githubService.getRepositoryDetails(userName, repositoryName)
				.thenApply(repository -> ok(views.html.repository.render(repository)));
		return resultCompletionStage;
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

}
