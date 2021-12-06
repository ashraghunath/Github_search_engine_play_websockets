package actors;

import akka.actor.AbstractActor;
import akka.actor.AbstractActorWithTimers;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.cache.AsyncCacheApi;
import play.mvc.Http;
import scala.concurrent.duration.FiniteDuration;
import services.GithubService;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

/**
 * Actor to fetch the list of repositories for a given phrase on the main search page
 * @author Ashwin Raghunath
 */

public class SearchPageActor extends AbstractActorWithTimers {

    private ActorRef sessionActor;
    private GithubService githubService;
    private AsyncCacheApi asyncCacheApi;


    public SearchPageActor(ActorRef sessionActor, GithubService githubService, AsyncCacheApi asyncCacheApi) {
        this.sessionActor = sessionActor;
        this.githubService = githubService;
        this.asyncCacheApi=asyncCacheApi;

    }

    public static Props props(ActorRef sessionActor, GithubService githubService, AsyncCacheApi asyncCacheApi) {
        return Props.create(SearchPageActor.class, sessionActor, githubService , asyncCacheApi);
    }

    @Override
    public void preStart() {
        System.out.println("SearchPageActor actor created.");
    }


    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Messages.SearchPageActor.class, searchPageActorQuery -> {
                    onGetSearch(searchPageActorQuery).thenAcceptAsync(this::processSearchResult);
                })
                .build();

    }

    private CompletionStage<JsonNode> onGetSearch(Messages.SearchPageActor searchPageActor) throws Exception {
        return asyncCacheApi.getOrElseUpdate(searchPageActor.phrase,
                        () -> githubService.searchResultsUsingActors(searchPageActor.phrase))
                .thenApplyAsync(
                        searchResults -> {
                            ObjectMapper mapper = new ObjectMapper();
                            ObjectNode searchData = mapper.createObjectNode();
                            searchData.put("responseType", "searchResults");
                            JsonNode searchMapJsonNode = mapper.convertValue(searchResults, JsonNode.class);
                            searchData.set("searchMap",searchMapJsonNode);
                            return searchData;
                        }
                );
    }
    private void processSearchResult(JsonNode searchResult) {
        sessionActor.tell(new Messages.SearchResult(searchResult), getSelf());
    }
}
