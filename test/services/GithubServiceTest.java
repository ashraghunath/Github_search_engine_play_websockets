package services;

import models.*;

import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.SearchRepository;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.egit.github.core.service.UserService;
import org.fluentlenium.core.search.Search;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.test.WithApplication;
import views.html.repository;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

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


    @InjectMocks
    GithubService githubServiceMock;

    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);
    }

    /**
     * tests the service getRepositoryDetails
     * @author Ashwin Raghunath 40192120
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
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
     * @author Ashwin Raghunath 40192120
     * @return Repository object contains mock values
     */
    private Repository repository()
    {
        Repository repository = new Repository();
        repository.setName("MockRepoName");
        return repository;
    }

    
    /**
     * tests the service getAllIssues
     * @author Anushka Shetty 40192371
     * @throws IOException
     * @throws InterruptedException
     * @throws ExecutionException
     */
    @Test
    public void getIssueWordLevelStatisticsTest() throws IOException, InterruptedException, ExecutionException {
    	when(issueService.getIssues(any(String.class), any(String.class), any())).thenReturn(issues());
    	CompletionStage<IssueWordStatistics> issueWordStatistics = githubServiceMock.getAllIssues("userName","MockRepoName");
    	assertNotNull(issueWordStatistics);
    	IssueWordStatistics issueWordStatisticsResult = issueWordStatistics.toCompletableFuture().get();
    	assertEquals(issueWordStatisticsResult.wordfrequency.size(),9);
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
     * @author Sourav Uttam Sinha 40175660
     * @return User object contains mock values
     */
    private User user()
    {
        User user = new User();
        user.setName("MockUserName");
        return user;
    }
    @Test
    public void getRepositoriesByTopicTest() throws IOException, InterruptedException, ExecutionException {
        List<SearchRepository> sr = searchRepos();
        System.out.println("Whats is this:"+ sr);
        when(repositoryService.searchRepositories(anyMap())).thenReturn(sr);
        CompletionStage<SearchedRepositoryDetails> searchedReposDetails = githubServiceMock.getRepositoriesByTopics("mocktopic");
        assertNotNull(searchedReposDetails);
        SearchedRepositoryDetails details = searchedReposDetails.toCompletableFuture().get();
        List<String> actual = new ArrayList<>();
        for (SearchRepository repo:details.getRepo()) {
            actual.add(repo.getName());
            System.out.println("repo:"+ repo.getName());
        }
        List<String> expected = Arrays.asList("repo2", "repo1");
        assertEquals(expected,actual);
    }

    private List<SearchRepository> searchRepos() {
        List<SearchRepository> searchItem = new ArrayList<>();
        Calendar my_cal = Calendar.getInstance();
        SearchRepository searchMock1 = mock(SearchRepository.class);
        my_cal.set(2010,3,22);
        when(searchMock1.getCreatedAt()).thenReturn(my_cal.getTime());
        //when(searchMock1.getOwner()).thenReturn("user1");
        when(searchMock1.getName()).thenReturn("repo1");
        SearchRepository searchMock2 = mock(SearchRepository.class);
        my_cal.set(2011,3,22);
        when(searchMock2.getCreatedAt()).thenReturn(my_cal.getTime());
        //when(searchMock2.getOwner()).thenReturn("user2");
        when(searchMock2.getName()).thenReturn("repo2");
        searchItem.add(searchMock1);
        searchItem.add(searchMock2);
        return searchItem.stream().sorted(Comparator.comparing(SearchRepository::getCreatedAt)).collect(Collectors.toList());
    }

}
