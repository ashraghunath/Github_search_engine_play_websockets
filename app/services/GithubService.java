package services;

import models.RepositoryDetails;
import models.IssueWordStatistics;

import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.PageIterator;
import org.eclipse.egit.github.core.service.CollaboratorService;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.RepositoryService;

import javax.inject.Inject;

import static java.util.concurrent.CompletableFuture.supplyAsync;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GithubService {

    private final RepositoryService repositoryService;
    private final CollaboratorService collaboratorService;
    private final IssueService issueService;
    private final GitHubClient gitHubClient;

    public GithubService() {
        gitHubClient = new GitHubClient();
        //gitHubClient.setCredentials();
        this.repositoryService=new RepositoryService(gitHubClient);
        this.collaboratorService=new CollaboratorService(gitHubClient);
        this.issueService=new IssueService(gitHubClient);
    }

    public CompletionStage<RepositoryDetails> getRepositoryDetails(String userName, String repositoryName){

        return CompletableFuture.supplyAsync( () -> {
            RepositoryDetails repositoryDetails = new RepositoryDetails();
            Repository repository=null;
            Map<String, String> params = new HashMap<String, String>();
            params.put(IssueService.FILTER_STATE, "all");
            try {
                repository = repositoryService.getRepository(userName, repositoryName);
                PageIterator<Issue> iterator = issueService.pageIssues(userName, repositoryName,
                        params, 1);
                List<Issue> issues = new ArrayList<>();
                while(iterator.hasNext() && issues.size()!=20)
                {
                    issues.add(iterator.next().iterator().next());
                }
                repositoryDetails.setRepository(repository);
                repositoryDetails.setIssues(issues);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return repositoryDetails;
        });

    }
    

	
	public CompletionStage<IssueWordStatistics> getAllIssues(String userName, String repositoryName) {
		 return CompletableFuture.supplyAsync( () -> {List<Issue> issues = new ArrayList<Issue>();
		 IssueWordStatistics issueWordStatistics = null;
		Map<String, String> parameters = new HashMap<>();
		parameters.put(IssueService.FILTER_STATE,IssueService.STATE_CLOSED);
		parameters.put(IssueService.FILTER_STATE,IssueService.STATE_OPEN);		
			try {
				issues = issueService.getIssues(userName, repositoryName,parameters);
				System.out.println("Size " + issues.size());
			    issueWordStatistics =	getWordLevelStatistics(issues);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return issueWordStatistics;
		 });
	}
	
public IssueWordStatistics  getWordLevelStatistics(final List<Issue> issues) {
		
		//return supplyAsync (()->{		
			//Converting Issue list into list of strings
			List<String> newList = new ArrayList<>(issues.size());
			for (Issue issue : issues) { 
				  newList.add(String.valueOf(issue.getTitle())); 
				}			
			//Splitting words
			List <String> list = Stream.of(newList.toString()).map(w -> w.split("\\s+")).flatMap(Arrays::stream)
		            .collect(Collectors.toList());
			
			//Mapping words with their frequency 
			Map<String, Integer> wordsCountMap = list.stream().map(eachWord -> eachWord)
					.collect(Collectors.toMap(w -> w.toLowerCase(), w -> 1, Integer::sum));
			
			//Sorting the result in descending order
			wordsCountMap = wordsCountMap.entrySet()
					.stream()
					.sorted(Map.Entry.<String, Integer> comparingByValue().reversed())
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2)-> e1, LinkedHashMap::new));
			return new IssueWordStatistics(wordsCountMap);
	//	});
}
	
	



}
