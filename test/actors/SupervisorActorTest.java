package actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.IssueWordStatistics;
import models.RepositoryDetails;
import models.UserRepositoryTopics;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Repository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import play.cache.AsyncCacheApi;
import services.GithubService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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
     * Test case for IssueStatisticsActor flow in SupervisorActor
     */
    @Test
    public void supervisorActorTestForIssueWordLevelStats() {

        new TestKit(actorSystem) {
            {
                Mockito.when(githubServiceMock.getAllIssues(anyString(), anyString())).thenReturn(issueStatsCompletionStage());
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode issueStatData = mapper.createObjectNode();

                issueStatData.put("issueStatisticsPage", "");
                issueStatData.put("repositoryName", "KP_G05");
                issueStatData.put("userName", "anushkashetty96");

                //issueStatData.put("issueStatList", "{\"exception\":1,\"reference\":1,\"pointer\":1,\"null\":2,\"bound\":1,\"index\":1,\"out\":1}\n");
                final ActorRef supervisorActor = actorSystem.actorOf(
                        SupervisorActor.props(testProbe.getRef(), githubServiceMock,asyncCacheApi));
                supervisorActor.tell(issueStatData, testProbe.getRef());
                ObjectNode issueStatsJsonNode = testProbe.expectMsgClass(ObjectNode.class);
                JsonNode issueStatistics = issueStatsJsonNode.get("issueStatList");
                assertEquals("issueStatisticsPage",issueStatsJsonNode.get("responseType").asText());
                assertEquals(2,issueStatistics.get("wordfrequency").get("null").asInt());
                assertEquals("KP_G05",issueStatsJsonNode.get("respositoryName").asText());
            }
        };
    }

    /**
     * Mock IssueWordStatistics object
     * @return future of IssueWordStatistics
     */
    public CompletionStage<IssueWordStatistics> issueStatsCompletionStage()
    {
        return CompletableFuture.supplyAsync(() -> {

            Map<String,Integer> wordFrequency = new HashMap<>();
            wordFrequency.put("null",2);
            wordFrequency.put("pointer",1);
            wordFrequency.put("exception",1);
            wordFrequency.put("reference",1);
            wordFrequency.put("index",1);
            wordFrequency.put("out",1);
            wordFrequency.put("bound",1);
            IssueWordStatistics issueWordStatistics = new IssueWordStatistics(wordFrequency);
            return issueWordStatistics;

        });
    }
}
