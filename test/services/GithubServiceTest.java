package services;

import models.IssueWordStatistics;
import models.RepositoryDetails;

import models.UserDetails;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.egit.github.core.service.UserService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
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
}
