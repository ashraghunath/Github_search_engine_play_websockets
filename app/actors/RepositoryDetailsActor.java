package actors;

import akka.actor.AbstractActor;
import akka.actor.AbstractActorWithTimers;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.eclipse.egit.github.core.Issue;
import play.cache.AsyncCacheApi;
import scala.concurrent.duration.Duration;
import services.GithubService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

public class RepositoryDetailsActor extends AbstractActorWithTimers {

    private ActorRef sessionActor;
    private AsyncCacheApi asyncCacheApi;
    private GithubService githubService;
    private String username;
    private String repositoryName;

    public RepositoryDetailsActor(ActorRef sessionActor, GithubService githubService, AsyncCacheApi asyncCacheApi) {
        this.sessionActor = sessionActor;
        this.githubService = githubService;
        this.asyncCacheApi = asyncCacheApi;
    }

    public static Props props(ActorRef sessionActor, GithubService githubService, AsyncCacheApi asyncCacheApi) {
        return Props.create(RepositoryDetailsActor.class, sessionActor, githubService , asyncCacheApi);
    }

    @Override
    public void preStart() {
        System.out.println("RepositoryDetails actor created.");
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Messages.GetRepositoryDetailsActor.class, repositoryDetailsRequest -> {
                    getRepositoryDetails(repositoryDetailsRequest).thenAcceptAsync(this::processRepositoryDetails);
                    getTimers().startPeriodicTimer("repositoryDetails",
                            new Messages.GetRepositoryDetailsActor(this.username,this.repositoryName),
                            Duration.create(5, TimeUnit.SECONDS));
                })
                .build();
    }

    private CompletionStage<JsonNode> getRepositoryDetails(Messages.GetRepositoryDetailsActor repositoryDetailRequest) throws Exception {
        return githubService.getRepositoryDetails(repositoryDetailRequest.username, repositoryDetailRequest.repositoryName)
                .thenApplyAsync(
                        repositoryDetails -> {
                            ObjectMapper mapper = new ObjectMapper();
                            ObjectNode repositoryData = mapper.createObjectNode();
                            JsonNode repositoryJsonNode = mapper.convertValue(repositoryDetails.getRepository(), JsonNode.class);
                            List<Issue> issues = repositoryDetails.getIssues();
                            List<String> issueNames = new ArrayList<>();
                            for (Issue issue : issues) {
                                issueNames.add(issue.getTitle());
                            }
                            ArrayNode arrayNode = mapper.createArrayNode();
                            issueNames.forEach(arrayNode::add);
                            repositoryData.put("responseType", "repositoryDetails");
                            repositoryData.set("repositoryDetails", repositoryJsonNode);
                            JsonNode issuesJsonNode = mapper.convertValue(repositoryDetails.getIssues(), JsonNode.class);
                            repositoryData.set("issueListNode", issuesJsonNode);

                            return repositoryData;
                        }
                );
    }

    private void processRepositoryDetails(JsonNode repositoryDetails) {
        sessionActor.tell(new Messages.RepositoryDetails(repositoryDetails), getSelf());
    }
}
