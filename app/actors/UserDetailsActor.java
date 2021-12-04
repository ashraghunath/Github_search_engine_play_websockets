package actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.UserDetails;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.User;
import play.cache.AsyncCacheApi;
import services.GithubService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;

public class UserDetailsActor extends AbstractActor{

    private ActorRef sessionActor;
    private AsyncCacheApi asyncCacheApi;
    private GithubService githubService;

    public UserDetailsActor(ActorRef sessionActor, GithubService githubService, AsyncCacheApi asyncCacheApi) {
        this.sessionActor = sessionActor;
        this.githubService = githubService;
        this.asyncCacheApi = asyncCacheApi;
    }

    public static Props props(ActorRef sessionActor, GithubService githubService, AsyncCacheApi asyncCacheApi) {
        return Props.create(UserDetailsActor.class, sessionActor, githubService , asyncCacheApi);
    }

    @Override
    public void preStart() {
        System.out.println("UserDetails actor created.");
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Messages.GetUserDetailsActor.class, userProfileRequest -> {
                    getUserDetails(userProfileRequest).thenAcceptAsync(this::processUserProfileResult);
                })
                .build();
    }

    private CompletionStage<JsonNode> getUserDetails(Messages.GetUserDetailsActor userProfileRequest) throws Exception {

        return asyncCacheApi.getOrElseUpdate(userProfileRequest.username + ".",
                () -> githubService.getUserDetails(userProfileRequest.username))
                .thenApplyAsync(
                        userDetails -> {
                            ObjectMapper mapper = new ObjectMapper();
                            ObjectNode userData = mapper.createObjectNode();
                            JsonNode userJsonNode = mapper.convertValue(userDetails.getUser(), JsonNode.class);
                            List<Repository> repositories = userDetails.getRepository();
                            List<String> repositoryNames = new ArrayList<>();
                            for (Repository repository : repositories) {
                                repositoryNames.add(repository.getName());
                            }
                            ArrayNode arrayNode = mapper.createArrayNode();
                            repositoryNames.forEach(arrayNode::add);
                            userData.put("responseType", "userDetails");
                            userData.set("userDetails", userJsonNode);
                            userData.set("repositoryList", arrayNode);
                            return userData;
                        }
                );
    }

    private void processUserProfileResult(JsonNode userProfileInfo) {
        sessionActor.tell(new Messages.UserDetails(userProfileInfo), getSelf());
    }

}
