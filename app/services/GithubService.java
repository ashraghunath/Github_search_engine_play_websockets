package services;

import models.RepositoryDetails;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Repository;

import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.PageIterator;
import org.eclipse.egit.github.core.service.CollaboratorService;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.RepositoryService;


import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class GithubService {

    private final RepositoryService repositoryService;
    private final CollaboratorService collaboratorService;
    private final IssueService issueService;
    private final GitHubClient gitHubClient;

    public GithubService() {
        gitHubClient = new GitHubClient();
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


}
