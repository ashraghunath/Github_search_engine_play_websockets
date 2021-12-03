package actors;

import akka.actor.AbstractActor;
import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.fasterxml.jackson.databind.JsonNode;
import play.cache.AsyncCacheApi;
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
    private ActorRef repositoryDetailsActor = null;
    private ActorRef issueStatActor = null;
    private ActorRef searchPageActor = null;

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
                .match(Messages.RepositoryDetails.class, repositoryDetails -> wsOut.tell(repositoryDetails.repositoryDetails, self()))
                .match(Messages.SearchResult.class, searchResult -> wsOut.tell(searchResult.searchResult, self()))
                .matchAny(other -> log.error("Received unknown message type: " + other.getClass()))
                .build();
    }


    private void processRequest(JsonNode receivedJson) {
    	log.info(receivedJson.asText());
        if(receivedJson.has("searchPage")) {

            if(searchPageActor==null)
            {
                log.info("Creating a search page actor.");
                searchPageActor = getContext().actorOf(SearchPageActor.props(self(), githubService, asyncCacheApi));
            }
            String phrase = receivedJson.get("searchPage").asText();
            searchPageActor.tell(new Messages.SearchPageActor(phrase), getSelf());

        } else if(receivedJson.has("repositoryDetails")) {
            String repositoryName = receivedJson.get("repositoryDetails").asText();
            String username = receivedJson.get("username").asText();
            if(repositoryDetailsActor == null) {
                log.info("Creating a repository profile actor.");
                repositoryDetailsActor = getContext().actorOf(RepositoryDetailsActor.props(self(), githubService, asyncCacheApi));
            }
            repositoryDetailsActor.tell(new Messages.GetRepositoryDetailsActor(username, repositoryName), getSelf());
        }
    }
}
