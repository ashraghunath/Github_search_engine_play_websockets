package actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.RepositoryDetails;
import models.SearchResults;
import models.UserRepositoryTopics;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.SearchRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import play.cache.AsyncCacheApi;
import services.GithubService;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

/**
 * Test class for SupervisorActor
 * @author Ashwin Raghunath
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class SupervisorActorTest {

    static ActorSystem actorSystem;
    private static TestKit testProbe;
    private static GithubService githubServiceMock;
    private static AsyncCacheApi asyncCacheApi;

    @Before
    public  void setup() {
        actorSystem = ActorSystem.create();
        testProbe = new TestKit(actorSystem);
        githubServiceMock = Mockito.mock(GithubService.class);
        asyncCacheApi = Mockito.mock(AsyncCacheApi.class);
    }

    /**
     * Test case for RepositoryDetailsActor flow in SupervisorActor
     */
    @Test
    public void supervisorActorTestForRepositoryDetailsFlow() {

        new TestKit(actorSystem) {
            {
                Mockito.when(githubServiceMock.getRepositoryDetails(anyString(), anyString())).thenReturn(repositoryDetailsCompletionStage());
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode repositoryData = mapper.createObjectNode();
                repositoryData.put("repositoryDetails", "KP_G05");
                repositoryData.put("username", "ashraghunath");
                final ActorRef supervisorActor = actorSystem.actorOf(
                        SupervisorActor.props(testProbe.getRef(), githubServiceMock,asyncCacheApi));
                supervisorActor.tell(repositoryData, testProbe.getRef());
                ObjectNode repositoryDetailsJsonNode = testProbe.expectMsgClass(ObjectNode.class);
                assertEquals("repositoryDetails",repositoryDetailsJsonNode.get("responseType").asText());
                assertEquals("KP_G05",repositoryDetailsJsonNode.get("repositoryDetails").get("name").asText());
            }
        };
    }

    /**
     * Test case for SearchPageActor flow in SupervisorActor
     */
    @Test
    public void supervisorActorTestForSearchPageFlow() {

        new TestKit(actorSystem) {
            {
                Mockito.when(asyncCacheApi.getOrElseUpdate(anyString(),any())).thenReturn(completionStageSearch());
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode repositoryData = mapper.createObjectNode();
                repositoryData.put("searchPage", "JAVA AI DL");
                final ActorRef supervisorActor = actorSystem.actorOf(
                        SupervisorActor.props(testProbe.getRef(), githubServiceMock,asyncCacheApi));
                supervisorActor.tell(repositoryData, testProbe.getRef());
                ObjectNode searchPageJsonNode = testProbe.expectMsgClass(ObjectNode.class);
                assertEquals("searchResults",searchPageJsonNode.get("responseType").asText());
                assertEquals("owner",searchPageJsonNode.get("searchMap").get("JAVA AI DL").get(0).get("owner").asText());
            }
        };
    }


    /**
     * Mock RepositoryDetails object
     * @return future of RepositoryDetails
     */
    public CompletionStage<RepositoryDetails> repositoryDetailsCompletionStage()
    {
        return CompletableFuture.supplyAsync(() -> {

            Repository repository = new Repository();
            repository.setName("KP_G05");
            Issue issue = new Issue();
            issue.setTitle("title");
            List<Issue> issues = Arrays.asList(issue);
            RepositoryDetails repositoryDetails = new RepositoryDetails();
            repositoryDetails.setRepository(repository);
            repositoryDetails.setIssues(issues);
            return repositoryDetails;

        });
    }

    /**
     * Mock searchResult object
     * @return future of Object that gets return on calling search function in github service
     */
    public CompletionStage<Object> completionStageSearch()
    {
        return CompletableFuture.supplyAsync(() -> {
            UserRepositoryTopics userRepositoryTopics = new UserRepositoryTopics("owner","name");
            Map<String,List<UserRepositoryTopics>> map = new HashMap<>();
            map.put("JAVA AI DL", Arrays.asList(userRepositoryTopics));
            return map;
        });
    }

    /**
     * Test case for RepositoryDetailsActor flow in SupervisorActor
     */
    @Test
    public void supervisorActorTestForTopicsSearchFlow() {

        new TestKit(actorSystem) {
            {
                Mockito.when(asyncCacheApi.getOrElseUpdate(anyString(),any())).thenReturn(topicsSearchCompletionStage());
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode topicData = mapper.createObjectNode();
                topicData.put("topicsDetails", "java");
                final ActorRef supervisor = actorSystem.actorOf(
                        SupervisorActor.props(testProbe.getRef(), githubServiceMock, asyncCacheApi));
                supervisor.tell(topicData, testProbe.getRef());
                ObjectNode topicsInfoNode = testProbe.expectMsgClass(ObjectNode.class);
                //System.out.println("Topics INfo: "+ topicsInfoNode);
                assertEquals("topicsDetails", topicsInfoNode.get("responseType").asText());
                List<String> owners = new ArrayList<>();
                for(JsonNode repository:topicsInfoNode.get("searchProfile").get("repos"))
                {
                    owners.add(repository.get("owner").asText());
                }
                assertEquals(Arrays.asList("owner1","owner2"),owners);
            }
        };
    }

    public CompletionStage<Object> topicsSearchCompletionStage()
    {
        return CompletableFuture.supplyAsync(() -> {
            SearchResults searchResults = new SearchResults();
            List<UserRepositoryTopics> searchItem = new ArrayList<>();
            UserRepositoryTopics userRepositoryTopics1 = new UserRepositoryTopics("owner1","name1");
            userRepositoryTopics1.setTopics(Arrays.asList("topic1","topic2"));
            UserRepositoryTopics userRepositoryTopics2 = new UserRepositoryTopics("owner2","name2");
            userRepositoryTopics2.setTopics(Arrays.asList("topic3","topic4"));
            searchItem.add(userRepositoryTopics1);
            searchItem.add(userRepositoryTopics2);
            searchResults.setKeyword("java");
            searchResults.setRepos(searchItem);
            return searchResults;
        });
    }
}
