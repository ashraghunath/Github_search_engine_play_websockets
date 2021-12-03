package actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.cache.AsyncCacheApi;
import services.GithubService;

import java.util.concurrent.CompletionStage;

public class TopicsActor extends AbstractActor {

    private ActorRef sessionActor;
    private AsyncCacheApi asyncCacheApi;
    private GithubService githubService;

    public TopicsActor(ActorRef sessionActor, GithubService githubService, AsyncCacheApi asyncCacheApi) {
        this.sessionActor = sessionActor;
        this.githubService = githubService;
        this.asyncCacheApi = asyncCacheApi;
    }

    public static Props props(ActorRef sessionActor, GithubService githubService, AsyncCacheApi asyncCacheApi) {
        return Props.create(TopicsActor.class, sessionActor, githubService , asyncCacheApi);
    }

    @Override
    public void preStart() {
        System.out.println("Topics actor created.");
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Messages.GetRepositoryfromTopic.class, topicsInfo -> {
                    onGetTopicsSearch(topicsInfo).thenAcceptAsync(this::processTopicsResult);
                })
                .build();
    }

    private CompletionStage<JsonNode> onGetTopicsSearch(Messages.GetRepositoryfromTopic topicsRequest) throws Exception {

        return asyncCacheApi.getOrElseUpdate(topicsRequest.topic_name ,
                        () -> githubService.getReposByTopics(topicsRequest.topic_name))
                .thenApplyAsync(
                        searchDetails -> {
                            asyncCacheApi.set(topicsRequest.topic_name,searchDetails,60*20);
                            ObjectMapper mapper = new ObjectMapper();
                            ObjectNode repositoryData = mapper.createObjectNode();
                            JsonNode repositoryJsonNode = mapper.convertValue(searchDetails, JsonNode.class);
                            repositoryData.put("responseType", "topicsDetails");
                            repositoryData.set("searchProfile", repositoryJsonNode);
                            System.out.println("Inside actor my json:" + repositoryData);
                            return repositoryData;
                        }
                );
    }

    private void processTopicsResult(JsonNode topicInfo) {
        sessionActor.tell(new Messages.TopicDetails(topicInfo), getSelf());
    }
}
