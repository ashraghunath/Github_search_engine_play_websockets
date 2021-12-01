package actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.eclipse.egit.github.core.Issue;
import play.cache.AsyncCacheApi;
import services.GithubService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;

public class RepositoryDetailsActor extends AbstractActor {

    private ActorRef sessionActor;
    private AsyncCacheApi asyncCacheApi;
    private GithubService githubService;

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
                .match(Messages.GetRepositoryDetailsActor.class, repositoryProfileRequest -> {
                    onGetRepositoryProfile(repositoryProfileRequest).thenAcceptAsync(this::processRepositoryProfileResult);
                })
                .build();
    }

    private CompletionStage<JsonNode> onGetRepositoryProfile(Messages.GetRepositoryDetailsActor repositoryProfileRequest) throws Exception {

        return asyncCacheApi.getOrElseUpdate(repositoryProfileRequest.username + "." + repositoryProfileRequest.repositoryName,
                        () -> githubService.getRepositoryDetails(repositoryProfileRequest.username, repositoryProfileRequest.repositoryName))
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
                            repositoryData.set("repositoryProfile", repositoryJsonNode);
                            repositoryData.set("issueList", arrayNode);
                            return repositoryData;
                        }
                );
    }

    private void processRepositoryProfileResult(JsonNode repositoryProfileInfo) {
        sessionActor.tell(new Messages.RepositoryDetails(repositoryProfileInfo), getSelf());
    }
}
