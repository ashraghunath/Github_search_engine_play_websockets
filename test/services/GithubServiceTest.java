package services;

import com.typesafe.config.Config;
import models.*;

import org.eclipse.egit.github.core.*;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.GitHubRequest;
import org.eclipse.egit.github.core.service.CommitService;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.egit.github.core.service.UserService;
import org.fluentlenium.core.search.Search;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.OngoingStubbing;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Http;
import play.test.WithApplication;
import views.html.repository;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.Assert.assertNotNull;

import static org.junit.Assert.*;

import static org.mockito.Mockito.*;

/**
 * Test class for GithubService using mockito
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class GithubServiceTest extends WithApplication {


    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().build();
    }


    @Mock
    RepositoryService repositoryService;
    @Mock
    IssueService issueService;
    @Mock
    UserService userService;
    @Mock
    Config config;
    @Mock
    CommitService commitService;

    @Mock
    GitHubClient mockClient;


    @InjectMocks
    GithubService githubServiceMock;

    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);
    }

    /**
     * tests the service getRepositoryDetails
     *
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     * @author Ashwin Raghunath 40192120
     */
    @Test
    public void getRepositoryDetailsTest() throws IOException, ExecutionException, InterruptedException {
        when(repositoryService.getRepository(any(String.class),any(String.class))).thenReturn(repository());
        CompletionStage<RepositoryDetails> repositoryDetails = githubServiceMock.getRepositoryDetails("userName", "MockRepoName");
        assertNotNull(repositoryDetails);
        RepositoryDetails repositoryDetails1 = repositoryDetails.toCompletableFuture().get();
        assertEquals(repositoryDetails1.getRepository().getName(),"MockRepoName");
    }

    /**
     * mock object for testing getRepositoryDetails
     *
     * @return Repository object contains mock values
     * @author Ashwin Raghunath 40192120
     */
    private Repository repository()
    {
        Repository repository = new Repository();
        repository.setName("MockRepoName");
        return repository;
    }
    
    /**
     * tests the service getAllIssues

     * @throws IOException
     * @throws InterruptedException
     * @throws ExecutionException
     * @author Anushka Shetty 40192371
     */
    @Test
    public void getIssueWordLevelStatisticsTest() throws IOException, InterruptedException, ExecutionException {
    	when(issueService.getIssues(any(String.class), any(String.class), any())).thenReturn(issues());
    	CompletionStage<IssueWordStatistics> issueWordStatistics = githubServiceMock.getAllIssues("userName","MockRepoName");
    	assertNotNull(issueWordStatistics);
    	IssueWordStatistics issueWordStatisticsResult = issueWordStatistics.toCompletableFuture().get();
    	assertEquals(issueWordStatisticsResult.wordfrequency.size(),10);
    	assertEquals((int)issueWordStatisticsResult.getWordfrequency().get("null"),2);
    }

    /**
     * mock object for testing getAllIssues
     * @author Anushka Shetty 40192371
     * @return List<Issue> object contains mock values
     */
    private List<Issue> issues()
    { 	
    	List<Issue> issues = new ArrayList<Issue>();
    	Issue issue1 = new Issue();
    	issue1.setTitle("Null Pointer Exception");
    	Issue issue2 = new Issue();
    	issue2.setTitle("Null Reference");
    	Issue issue3 = new Issue();
    	issue3.setTitle("Index out of bound");
    	Issue issue4 = new Issue();
    	issue4.setTitle("Java Array");
    	issues.add(issue1);
    	issues.add(issue2);
    	issues.add(issue3);
    	issues.add(issue4);
    	return issues;
    }

    /**
     * tests the service getAllIssues
     * @author Sourav Uttam Sinha 40175660
     * @throws IOException
     * @throws InterruptedException
     * @throws ExecutionException
     */
    @Test
    public void getUserDetailsTest() throws IOException, ExecutionException, InterruptedException {
        when(userService.getUser(any(String.class))).thenReturn(user());
        CompletionStage<UserDetails> userDetails = githubServiceMock.getUserDetails("userName");
        assertNotNull(userDetails);
        UserDetails userDetails1 = userDetails.toCompletableFuture().get();
        assertEquals(userDetails1.getUser().getName(),"MockUserName");
    }

    /**
     * mock object for testing getRepositoryDetails
     *
     * @return User object contains mock values
     * @author Sourav Uttam Sinha 40175660
     */
    private User user()
    {
        User user = new User();
        user.setName("MockUserName");
        return user;
    }

    /**
     * Mock stream to override the githubClient.getStream() function
     * @return InputStream containg the topics
     * @author Trusha Patel 40192614
     */

    private InputStream topicInputStream() {
        String mockTopics = "{" +
                "\"names\": [" +
                "\"topic1\"," +
                "\"topic2\"," +
                "\"topic3\"" +
                "]" +
                "}";
        InputStream stream = new ByteArrayInputStream(mockTopics.getBytes(StandardCharsets.UTF_8));
        return stream;
    }

    /**
     * test for getRepositoriesByTopicTest function
     *
     * @throws IOException
     * @throws InterruptedException
     * @throws ExecutionException
     * @author Trusha Patel 40192614
     */
    @Test
    public void getReposByTopicTest() throws IOException, InterruptedException, ExecutionException {
        List<SearchRepository> searchRepos = searchRepos();
        when(mockClient.getStream(any())).thenReturn(topicInputStream());
        when(repositoryService.searchRepositories(anyMap())).thenReturn(searchRepos);
        CompletionStage<List<UserRepositoryTopics>> searchedReposDetails = githubServiceMock.getReposByTopics("mocktopic");
        assertNotNull(searchedReposDetails);
        List<UserRepositoryTopics> details = searchedReposDetails.toCompletableFuture().get();
        assertEquals(details.get(0).getName(),"repo1");
    }


    /**
     * mock object for testing getRepositoriesByTopics
     * @author Trusha Patel 40192614
     *
     */
    private List<SearchRepository> searchRepos() throws IOException {
        SearchRepository mock1 = mock(SearchRepository.class);
        when(mock1.getName()).thenReturn("repo1");
        when(mock1.getOwner()).thenReturn("owner1");
        when(mock1.getUrl()).thenReturn("https://github.com/mockuser/mockrepo");
        Calendar calendar = Calendar.getInstance();
        calendar.set(2012,10,12);
        when(mock1.getPushedAt()).thenReturn(calendar.getTime());
        List<SearchRepository> list = new ArrayList<>();
        list.add(mock1);
        return list;
    }

    /**
     *  Mocks the topics for the fake API call
     *
     * @throws IOException
     * @author Trusha Patel 40192614
     */

    @Test
    public void getTopicsTest() throws IOException{
        SearchRepository mocksearchRepository = mock(SearchRepository.class);
        GitHubRequest mockRequest = mock(GitHubRequest.class);
        when(mocksearchRepository.getUrl()).thenReturn("https://github.com/mockuser/mockrepo");
        when(mocksearchRepository.getName()).thenReturn("mockrepo");
        when(mocksearchRepository.getOwner()).thenReturn("mockuser");
        //The format of the return form is: https://github.com/CyC2018/CS-Notes
        when(mockClient.getStream(any())).thenReturn(topicInputStream());
        List<String> topics = githubServiceMock.getTopics(mocksearchRepository);
        List<String> expected = Arrays.asList("\"topic1\"", "\"topic2\"", "\"topic3\"");
        assertEquals(topics,expected);
    }

    /**
     * Test case for searchResults method
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     * * @author Ashwin Raghunath 40192120
     */
    @Test
    public void searchtest() throws IOException, ExecutionException, InterruptedException {
        List<SearchRepository> searchRepositoryList = searchRepositoryList();
        when(repositoryService.searchRepositories(anyString(),anyInt())).thenReturn(searchRepositoryList);
        when(mockClient.getStream(any())).thenReturn(topicInputStream());
        Http.RequestBuilder requestBuilder = new Http.RequestBuilder();
        CompletionStage<Map<String, List<UserRepositoryTopics>>> results = githubServiceMock.searchResults(requestBuilder.build(), "phrase");
        assertNotNull(results);
        Map<String, List<UserRepositoryTopics>> stringListMap = results.toCompletableFuture().get();
        assertEquals(1,stringListMap.size());

    }

    /**
     * Mock object for testing search
     *
     * @return List<SearchRepository>
     * @author Ashwin Raghunath 40192120
     */
    public List<SearchRepository> searchRepositoryList()
    {
        SearchRepository searchRepositoryMock1 = mock(SearchRepository.class);
        when(searchRepositoryMock1.getName()).thenReturn("name1");
        when(searchRepositoryMock1.getOwner()).thenReturn("owner1");
        when(searchRepositoryMock1.getUrl()).thenReturn("https://github.com/mockuser/mockrepo");
        Calendar calendar = Calendar.getInstance();
        calendar.set(2012,11,12);
        when(searchRepositoryMock1.getCreatedAt()).thenReturn(calendar.getTime());
        List<SearchRepository> list = new ArrayList<>();
        list.add(searchRepositoryMock1);
        return list;
    }



    /**
     * Test Case for Commits Page
     * @author Anmol Malhotra 40201452
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void commitTest() throws IOException, ExecutionException, InterruptedException {
        when(repositoryService.getRepository(anyString(), anyString())).thenReturn(repository());
        List<RepositoryCommit> repositoryCommit = new ArrayList<>();
        RepositoryCommit repoCommit1 = new RepositoryCommit();
        repoCommit1.setSha("commitId");
        User user1 = new User();
        user1.setLogin("Anmol");
        repoCommit1.setAuthor(user1);
        repositoryCommit.add(repoCommit1);
        when(commitService.getCommits(any())).thenReturn(repositoryCommit);
        when(mockClient.getStream(any())).thenReturn(commitStatsInputStream());
        CompletionStage<CommitDetails> result = githubServiceMock.getCommitsForRepository("anmol", "repo");
        CommitDetails finalResult = result.toCompletableFuture().get();
        Assert.assertTrue(result.toCompletableFuture().isDone());
    }

    private InputStream commitStatsInputStream() {
        String mockTopics = "{\" +\n" +
                "                \"\\\"addition\\\":0\" +\n" +
                "                \"\\\"deletion\\\":40\" +\n" +
                "                \"\\\"name\\\":\\\"Anmol\\\"\" +\n" +
                "                \"\\\"sha\\\":\\\"afdfafdfad\\\"\" +\n" +
                "                \"}\"";
        InputStream stream = new ByteArrayInputStream(mockTopics.getBytes(StandardCharsets.UTF_8));
        System.out.println(stream);
        return stream;
    }

}
