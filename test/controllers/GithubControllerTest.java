package controllers;

import models.*;
import org.apache.http.HttpStatus;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.SearchRepository;
import org.eclipse.egit.github.core.User;
import org.junit.Test;

import static org.junit.Assert.*;
import static play.mvc.Results.ok;
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

import java.util.*;
import java.util.concurrent.*;

import play.cache.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;

/**
 * Test class for GithubController using mockito
 */
@RunWith(MockitoJUnitRunner.class)
public class GithubControllerTest extends WithApplication {

    @Mock
    GithubService githubService;
    @Mock
    AsyncCacheApi cache;

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

