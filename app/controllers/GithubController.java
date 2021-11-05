package controllers;

import org.eclipse.egit.github.core.Repository;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.*;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletionStage;


import services.GithubService;
import views.html.index;

import static play.libs.Json.toJson;
import static play.libs.Scala.asScala;
import static play.mvc.Results.ok;

public class GithubController {
	

	 private final FormFactory formFactory;
	 private final GithubService githubService;

	 @Inject
	 public GithubController(FormFactory formFactory, GithubService githubService)
	 {
		 this.formFactory=formFactory;
		 this.githubService=githubService;
	 }

	public Result index() {
		return ok(views.html.index.render());
	}

	public Result search(Http.Request request)
	{
		DynamicForm form = formFactory.form().bindFromRequest(request);
		String phrase = form.get("phrase");
		return ok(views.html.index.render());
	}

	public Result getSearchResults()
	{
		List<String> strings = Arrays.asList("value1", "value2", "value3");
		return ok(toJson(strings));
	}

	public CompletionStage<Result> getRepositoryDetails(String userName, String repositoryName)
	{
		CompletionStage<Result> resultCompletionStage = githubService
				.getRepositoryDetails(userName, repositoryName)
				.thenApply(repository -> ok(views.html.repository.render(repository)));
		return resultCompletionStage;
	}
	
	public CompletionStage<Result> getIssues(String userName, String repositoryName)
	{
		CompletionStage<Result> resultCompletionStage = githubService
				.getAllIssues(userName, repositoryName)
				.thenApply(issues -> ok(views.html.issues.render(issues)));
		
		return resultCompletionStage;
	}

}


