package actors;

import static java.util.concurrent.CompletableFuture.supplyAsync;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.service.IssueService;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import models.IssueWordStatistics;
import play.cache.AsyncCacheApi;
import services.GithubService;

public class IssueStatisticsActor extends AbstractActor {

	private ActorRef sessionActor;
	private GithubService githubService;
	private AsyncCacheApi asyncCacheApi;

	public IssueStatisticsActor(ActorRef sessionActor, GithubService githubService,AsyncCacheApi asyncCacheApi) {
		this.sessionActor = sessionActor;
		this.githubService = githubService;
		this.asyncCacheApi = asyncCacheApi;
	}

	@Override
	public Receive createReceive() {
		// TODO Auto-generated method stub
		return receiveBuilder().match(Messages.GetIssueStatisticsActor.class, issueWordLevelStats -> {
			onGetIssueStatistics(issueWordLevelStats).thenAcceptAsync(this::processIssueStatisticsResult);
		}).build();
	}

	public static Props props(ActorRef  supervisorActor, GithubService gitHubAPI, AsyncCacheApi asyncCacheApi) {
		return Props.create(IssueStatisticsActor.class, supervisorActor, gitHubAPI, asyncCacheApi);
	}


	@Override
	public void preStart() {
		System.out.println("Issue Statistics actor created.");
	}

	private CompletionStage<JsonNode> onGetIssueStatistics(Messages.GetIssueStatisticsActor issueWordLevelStats)
			throws Exception {


		return githubService.getAllIssues(issueWordLevelStats.username, issueWordLevelStats.repositoryName)
				.thenApplyAsync(
						issueStats -> {
							ObjectMapper mapper = new ObjectMapper();
							ObjectNode issueStatData = mapper.createObjectNode();
							JsonNode issueStatJsonNode = mapper.convertValue(issueStats, JsonNode.class);


							issueStatData.put("responseType", "issueStatisticsPage");
							issueStatData.set("issueStatList", issueStatJsonNode);
							issueStatData.put("respositoryName",issueWordLevelStats.repositoryName);
							return issueStatData;
						}
				);
	}

	private void processIssueStatisticsResult(JsonNode issueWordLevelStats) {
		sessionActor.tell(new Messages.IssueStatistics(issueWordLevelStats), getSelf());
	}

}
