package controllers;

import models.IssueWordStatistics;
import models.RepositoryDetails;
import org.apache.http.HttpStatus;
import org.eclipse.egit.github.core.Repository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import play.test.WithApplication;
import services.GithubService;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;

@RunWith(MockitoJUnitRunner.class)
public class GithubControllerTest extends WithApplication {

    @Mock
    GithubService githubService;

    @InjectMocks
    GithubController githubController;


    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().build();
    }

    @Test
    public void indexTest() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/");

        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    /** Unit test for resting the endpoint /getRepositoryDetails/:userName/:repositoryName
     * @author Ashwin Raghunath 40192120
     */
    @Test
    public void getRepositoryDetailsTest()
    {
        running(provideApplication(), () -> {
            when(githubService.getRepositoryDetails(anyString(),anyString())).thenReturn(repositoryDetails());
            CompletionStage<Result> repositoryDetails = githubController.getRepositoryDetails("play", "play");
            try {
                Result result = repositoryDetails.toCompletableFuture().get();
                assertEquals(HttpStatus.SC_OK,result.status());
                assertTrue(contentAsString(result).contains("MockRepoName"));
                assertEquals("text/html",result.contentType().get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

    /** mock object for testing getRepositoryDetailsTest
     * @author Ashwin Raghunath 40192120
     * @return CompletionStage<RepositoryDetails> represents the async response containing the process stage of RepositoryDetails object
     */
    private CompletionStage<RepositoryDetails> repositoryDetails(){
        return CompletableFuture.supplyAsync( () -> {
            RepositoryDetails repositoryDetails = new RepositoryDetails();
            Repository repository = new Repository();
            repository.setName("MockRepoName");
            repositoryDetails.setRepository(repository);
            return repositoryDetails;
        });
    }
    
    
    /** Unit test for resting the endpoint /getRepositoryIssues/:userName/:repositoryName   
     * @author Anushka Shetty 40192371
     */
    @Test
    public void getRepositoryIssuesTest()
    {
        running(provideApplication(), () -> {
            when(githubService.getAllIssues(anyString(),anyString())).thenReturn(issueStatistics());
            CompletionStage<Result> issueStatistics = githubController.getIssues("play", "play");
            try {
                Result result = issueStatistics.toCompletableFuture().get();
                assertEquals(HttpStatus.SC_OK,result.status());
                assertEquals("text/html",result.contentType().get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
    }
    
    /** mock object for testing getRepositoryIssuesTest
     * @author Anushka Shetty 40192371
     * @return CompletionStage<IssueWordStatistics> represents the async response containing the process stage of IssueWordStatistics object
     */
    
    private CompletionStage<IssueWordStatistics> issueStatistics(){
        return CompletableFuture.supplyAsync( () -> {
        	Map<String, Integer> mapItem = new HashMap<String, Integer>();        	
        	mapItem.put("null",2);
        	mapItem.put("reference",1);
        	mapItem.put("exception",1);
        	mapItem.put("pointer",1);
        	mapItem.put("java",1);
        	mapItem.put("array",1);
        	mapItem.put("bound",1);
        	mapItem.put("index",1);	
        	mapItem.put("out",1);        
        	IssueWordStatistics issueWordStatistics = new IssueWordStatistics(mapItem);
            return issueWordStatistics;
        });
    }


}
