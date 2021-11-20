package services;

import com.typesafe.config.Config;
import models.*;
import org.eclipse.egit.github.core.client.GitHubRequest;
import play.mvc.Http;

import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Repository;

import org.eclipse.egit.github.core.SearchRepository;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.PageIterator;
import org.eclipse.egit.github.core.service.CollaboratorService;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.egit.github.core.service.UserService;
import Helper.SessionHelper;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import java.util.stream.Collectors;
import org.json.*;

import javax.inject.Inject;
import java.util.stream.Stream;

public class GithubService {

	private RepositoryService repositoryService;
	private CollaboratorService collaboratorService;
	private IssueService issueService;
	private GitHubClient gitHubClient;
	private UserService userService;
	private List<SearchRepository> searchRepositoryList;
	private SessionHelper sessionHelper;
	private Config config;
	Map<Optional<String>, Map<String, List<UserRepositoryTopics>>> searchSessionMap = new LinkedHashMap<>();

	@Inject
	public GithubService(Config config) {
		this.config=config;
		gitHubClient = new GitHubClient();
		gitHubClient.setOAuth2Token(config.getString("access.token"));
		this.repositoryService = new RepositoryService(gitHubClient);
		this.collaboratorService = new CollaboratorService(gitHubClient);
		this.issueService = new IssueService(gitHubClient);
		this.userService = new UserService(gitHubClient);
		this.sessionHelper = new SessionHelper();
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
				List<Issue> issues = issueService.getIssues(userName, repositoryName, params).stream().sorted(Comparator.comparing(Issue::getUpdatedAt).reversed()).limit(20)
						.collect(Collectors.toList());
				repositoryDetails.setRepository(repository);
				repositoryDetails.setIssues(issues);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return repositoryDetails;
		});

	}

	/**
	 * Returns the Word level Statistics for all Issues for the provided username
	 * and repository name
	 * 
	 * @author Anushka Shetty 40192371
	 * @param userName       the user who owns the repository.
	 * @param repositoryName the name of the repository to be searched for.
	 * @return CompletionStage<IssueWordStatistics> represents the async response
	 *         containing the process stage of IssueWordStatistics object
	 */

	public CompletionStage<IssueWordStatistics> getAllIssues(String userName, String repositoryName) {
		return CompletableFuture.supplyAsync(() -> {
			List<Issue> issues = new ArrayList<Issue>();
			Map<String, String> parameters = new HashMap<>();
			parameters.put(IssueService.FILTER_STATE, IssueService.STATE_CLOSED);
			parameters.put(IssueService.FILTER_STATE, IssueService.STATE_OPEN);

			try {
				issues = issueService.getIssues(userName, repositoryName, parameters);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return issues;
		}).thenComposeAsync(issues -> getWordLevelStatistics(issues));
	}

	/**
	 * Returns the Word level Statistics for all Issues for the provided username
	 * and repository name
	 * 
	 * @author Anushka Shetty 40192371
	 * @param List<Issue> List of all the issues for the given username and
	 *                    repository name
	 * @return CompletableFuture<IssueWordStatistics> represents the async response
	 *         containing the process stage of IssueWordStatistics object
	 */
	public CompletableFuture<IssueWordStatistics> getWordLevelStatistics(final List<Issue> issues) {

		return supplyAsync(() -> {

			String[] listCommonWords = { "the", "a", "an", "are", "and", "not", "be", "for", "on", "to", "of" };
			Set<String> commonWords = new HashSet<>(Arrays.asList(listCommonWords));

			// Converting Issue list into list of strings
			List<String> newList = new ArrayList<>(issues.size());
			for (Issue issue : issues) {
				newList.add(String.valueOf(issue.getTitle()));
			}

			// Splitting words
			List<String> list = (newList).stream().map(w -> w.trim().split("\\s+")).flatMap(Arrays::stream)
					.filter(q -> !commonWords.contains(q)).collect(Collectors.toList());
			// Mapping words with their frequency
			Map<String, Integer> wordsCountMap = list.stream().map(eachWord -> eachWord)
					.collect(Collectors.toMap(w -> w.toLowerCase(), w -> 1, Integer::sum));

			// Sorting the result in descending order
			wordsCountMap = wordsCountMap.entrySet().stream()
					.sorted(Map.Entry.<String, Integer>comparingByValue().reversed()).collect(Collectors
							.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
			return new IssueWordStatistics(wordsCountMap);
		});
	}

	/**
	 * Returns the User details for the provided username
	 *
	 * @author Sourav Uttam Sinha 40175660
	 * @param userName       the user who owns the repository.
	 * @return CompletionStage<RepositoryDetails> represents the async response
	 *         containing the process stage of RepositoryDetails object
	 */

	public CompletionStage<UserDetails> getUserDetails(String userName) {
		return CompletableFuture.supplyAsync(() -> {
			UserDetails userDetails = new UserDetails();
			User user = null;
			List<Repository> repositories = null;
			Map<String, String> params = new HashMap<String, String>();
			params.put(RepositoryService.TYPE_ALL, "all");
			try {
				user = userService.getUser(userName);
				repositories = repositoryService.getRepositories(userName).stream().limit(10)
						.collect(Collectors.toList());
				// userDetails.setUser(user);

			} catch (IOException e) {
				e.printStackTrace();
			}
			userDetails.setRepository(repositories);
			userDetails.setUser(user);
			return userDetails;
		});
	}

	/** List of the search results of the phrase entered by the user
	 * @author Ashwin Raghunath 40192120
	 * @param request incoming request value from search
	 * @param phrase
	 * @return map of the results after searching for repositories
	 */
	public CompletionStage<Map<String, List<UserRepositoryTopics>>> searchResults(Http.Request request, String phrase) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				searchRepositoryList = repositoryService.searchRepositories(phrase, 0).stream()
						.sorted(Comparator.comparing(SearchRepository::getPushedAt).reversed()).limit(10)
						.collect(Collectors.toList());
			} catch (IOException e) {
				e.printStackTrace();
			}
			List<UserRepositoryTopics> userRepositoryTopicsList = new ArrayList<>();
			for (SearchRepository searchRepository : searchRepositoryList) {
				UserRepositoryTopics userRepositoryTopics = new UserRepositoryTopics(searchRepository.getOwner(),
						searchRepository.getName());
				userRepositoryTopics.setTopics(getTopics(searchRepository));
				userRepositoryTopicsList.add(userRepositoryTopics);
			}
			Map<String, List<UserRepositoryTopics>> searchMap = sessionHelper
					.getSearchResultsForCurrentSession(request, phrase, userRepositoryTopicsList);

			return searchMap;
		});
	}

	/**
	 * @author Trusha Patel
	 * @param  topic_name The query topic
	 * @return CompletionStage<SearchedRepositoryDetails> represents the async
	 *         response containing the process stage of SearchedRepositoryDetails
	 *         object
	 */

	public CompletionStage<SearchedRepositoryDetails> getReposByTopics(String topic_name){
		return CompletableFuture.supplyAsync(() -> {
			Map<String, String> searchQuery = new HashMap<String, String>();
			SearchedRepositoryDetails searchResDetails = new SearchedRepositoryDetails();
			searchQuery.put("topic", topic_name);
			List<SearchRepository> searchRes = null;
			try {
				searchRes = repositoryService.searchRepositories(searchQuery).stream()
						.sorted(Comparator.comparing(SearchRepository::getPushedAt).reversed()).limit(10)
						.collect(Collectors.toList());
				searchResDetails.setRepos(searchRes);
			} catch (IOException e) {
				e.printStackTrace();
			}
			searchResDetails.setTopic(topic_name);
			return searchResDetails;
		});

	}
	/**
	 * @author Trusha Patel 40192614
	 * @param searchRepository A SearchRepository object to get the topics for
	 * @return List of the topics of the queried Repository
	 */
	public List<String> getTopics(SearchRepository searchRepository){
		GitHubRequest request = new GitHubRequest();
		List<String> topic_list = new ArrayList<>();
		try {

			String url =  searchRepository.getUrl().split("//")[1].split("github.com")[1];
			request.setUri("/repos"+ url + "/topics");
			String result = new BufferedReader(new InputStreamReader(gitHubClient.getStream(request)))
					.lines().collect(Collectors.joining("\n"));
			JSONObject jsonObject = new JSONObject(result);
			topic_list = Arrays.stream(jsonObject.get("names").toString().replace("[", "").replace("]", "").split(",")).collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return topic_list;
	}
}
