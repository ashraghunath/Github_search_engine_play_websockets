package actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import play.cache.AsyncCacheApi;

import com.fasterxml.jackson.databind.JsonNode;
import services.GithubService;

import java.util.HashMap;
import java.util.Map;


public class SupervisorActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    private final ActorRef wsOut;
    private GithubService githubService;
    private final AsyncCacheApi asyncCacheApi;

    final Map<String, ActorRef> queryToSearchActor = new HashMap<String, ActorRef>();
    private ActorRef userProfileActor = null;
    private ActorRef repositoryProfileActor = null;
    private ActorRef issueStatisticsActor = null;

    public SupervisorActor(final ActorRef wsOut, GithubService githubService, AsyncCacheApi asyncCacheApi) {
        this.wsOut =  wsOut;
        this.githubService = githubService;
        this.asyncCacheApi = asyncCacheApi;
    }


    public static Props props(final ActorRef wsout, GithubService githubService, AsyncCacheApi asyncCacheApi) {
        return Props.create(SupervisorActor.class, wsout, githubService, asyncCacheApi);
    }


    @Override
    public void preStart() {
        System.out.println("Supervisor actor created.");
    }


    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(JsonNode.class, this::processRequest)
                .match(Messages.RepositoryDetails.class, repositoryProfileInfo -> wsOut.tell(repositoryProfileInfo.repositoryDetails, self()))
                .matchAny(other -> log.error("Received unknown message type: " + other.getClass()))
                .build();
    }


    private void processRequest(JsonNode receivedJson) {
    	log.info(receivedJson.asText());
        if(receivedJson.has("search_query")) {

        } else if(receivedJson.has("repositoryDetails")) {
            String repositoryName = receivedJson.get("repositoryDetails").asText();
            String username = receivedJson.get("username").asText();
            if(repositoryProfileActor == null) {
                log.info("Creating a repository profile actor.");
                repositoryProfileActor = getContext().actorOf(RepositoryDetailsActor.props(self(), githubService, asyncCacheApi));
            }
            repositoryProfileActor.tell(new Messages.GetRepositoryDetailsActor(username, repositoryName), getSelf());
        }else if(receivedJson.has("issueStatisticsPage")) {
            String repositoryName = receivedJson.get("repositoryName").asText();
            String userName = receivedJson.get("userName").asText();
            if(issueStatisticsActor == null) {
                log.info("Creating a issue statistics actor.");
                issueStatisticsActor = getContext().actorOf(IssueStatisticsActor.props(self(), githubService, asyncCacheApi));
            }
            issueStatisticsActor.tell(new Messages.GetIssueStatisticsActor(userName, repositoryName), getSelf());
        }
    }
}
