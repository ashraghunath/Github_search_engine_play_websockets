package services;

import models.IssueWordStatistics;
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
import static java.util.concurrent.CompletableFuture.supplyAsync;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GithubService {

	private RepositoryService repositoryService;
	private CollaboratorService collaboratorService;
	private IssueService issueService;
	private GitHubClient gitHubClient;

	public GithubService() {
		gitHubClient = new GitHubClient();
		this.repositoryService = new RepositoryService(gitHubClient);
		this.collaboratorService = new CollaboratorService(gitHubClient);
		this.issueService = new IssueService(gitHubClient);
	}

	/**
	 * Returns the Repository details for the provided username and repository name
	 * 
	 * @author Ashwin Raghunath 40192120
	 * @param userName       the user who owns the repository.
	 * @param repositoryName the name of the repository to be searched for.
	 * @return CompletionStage<RepositoryDetails> represents the async response
	 *         containing the process stage of RepositoryDetails object
	 */
	public CompletionStage<RepositoryDetails> getRepositoryDetails(String userName, String repositoryName) {

		return CompletableFuture.supplyAsync(() -> {
			RepositoryDetails repositoryDetails = new RepositoryDetails();
			Repository repository = null;
			Map<String, String> params = new HashMap<String, String>();
			params.put(IssueService.FILTER_STATE, "all");
			try {
				repository = repositoryService.getRepository(userName, repositoryName);
				PageIterator<Issue> iterator = issueService.pageIssues(userName, repositoryName, params, 1);
				List<Issue> issues = new ArrayList<>();
				while (iterator != null && iterator.hasNext() && issues.size() != 20) {
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
		return CompletableFuture.supplyAsync(() -> {
			List<Issue> issues = new ArrayList<Issue>();
			Map<String, String> parameters = new HashMap<>();
			parameters.put(IssueService.FILTER_STATE, IssueService.STATE_CLOSED);
			parameters.put(IssueService.FILTER_STATE, IssueService.STATE_OPEN);

			try {
				issues = issueService.getIssues(userName, repositoryName, parameters);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return issues;
		}).thenComposeAsync(issues -> getWordLevelStatistics(issues));
	}

	public CompletableFuture<IssueWordStatistics> getWordLevelStatistics(final List<Issue> issues) {

		 return supplyAsync (()->{

			 String[] listCommonWords = {"the", "a", "an", "are", "and","not", "be","for","on", "to"};
			 Set<String> commonWords = new HashSet<>(Arrays.asList(listCommonWords));  

		// Converting Issue list into list of strings
		List<String> newList = new ArrayList<>(issues.size());
		for (Issue issue : issues) {
			newList.add(String.valueOf(issue.getTitle()));
		}

		// Splitting words
		List<String> list = Stream.of(newList.toString()).map(w -> w.split("\\s+")).flatMap(Arrays::stream).filter(q -> !commonWords.contains(q))
				.collect(Collectors.toList());

		// Mapping words with their frequency
		Map<String, Integer> wordsCountMap = list.stream().map(eachWord -> eachWord)
				.collect(Collectors.toMap(w -> w.toLowerCase(), w -> 1, Integer::sum));

		// Sorting the result in descending order
		wordsCountMap = wordsCountMap.entrySet().stream()
				.sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		return new IssueWordStatistics(wordsCountMap);
		 });
	}

}
