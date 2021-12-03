package services;

import Helper.SessionHelper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.typesafe.config.Config;
import models.*;
import org.eclipse.egit.github.core.*;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.GitHubRequest;
import org.eclipse.egit.github.core.service.*;
import org.json.JSONObject;
import play.mvc.Http;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static java.util.concurrent.CompletableFuture.supplyAsync;


/**
 * calls github api according to the service requested by the user for different pages
 */
public class GithubService {

	private RepositoryService repositoryService;
	private CollaboratorService collaboratorService;
	private IssueService issueService;
	private GitHubClient gitHubClient;
	private UserService userService;
	private List<SearchRepository> searchRepositoryList;
	private SessionHelper sessionHelper;
	private Config config;
	private CommitService commitService;
	private CommitStats commitStats;
	Map<String, List<UserRepositoryTopics>> searchSessionMap = new LinkedHashMap<>();

	@Inject
	public GithubService(Config config) {
		this.config = config;
		gitHubClient = new GitHubClient();
		gitHubClient.setOAuth2Token(config.getString("access.token"));
		this.repositoryService = new RepositoryService(gitHubClient);
		this.collaboratorService = new CollaboratorService(gitHubClient);
		this.issueService = new IssueService(gitHubClient);
		this.userService = new UserService(gitHubClient);
		this.sessionHelper = new SessionHelper();
		this.commitService = new CommitService(gitHubClient);
		this.commitStats = new CommitStats();
	}

	/**
	 * Returns the Repository details for the provided username and repository name
	 * 
	 * @author Ashwin Raghunath 40192120
	 * @param userName       the user who owns the repository.
	 * @param repositoryName the name of the repository to be searched for.
	 * @return represents the async response
	 *         containing the process stage of RepositoryDetails object
	 */
	public CompletionStage<RepositoryDetails> getRepositoryDetails(String userName, String repositoryName) {
		return CompletableFuture.supplyAsync(() -> {
			RepositoryDetails repositoryDetails = new RepositoryDetails();
			Repository repository = null;
			Map<String, String> params = new HashMap<String, String>();
			params.put(IssueService.FILTER_STATE, "all");
			try {
				System.out.println("Calling Github API to fetch repository details");
				repository = repositoryService.getRepository(userName, repositoryName);
				System.out.println("API call completed");
				List<Issue> issues = issueService.getIssues(userName, repositoryName, params).stream()
						.sorted(Comparator.comparing(Issue::getUpdatedAt).reversed()).limit(20)
						.collect(Collectors.toList());
				repositoryDetails.setRepository(repository);
				repositoryDetails.setIssues(issues);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return repositoryDetails;
		});

	}

	public CompletionStage<JsonNode> getRepositoryDetailsJsonNode(String username, String repositoryName) {
		return CompletableFuture.supplyAsync(() -> {

			Repository repository = null;
			Map<String, String> params = new HashMap<String, String>();
			params.put(IssueService.FILTER_STATE, "all");
			ObjectNode repositoryData = null;
			try {
				repository = repositoryService.getRepository(username, repositoryName);
				List<Issue> issues = issueService.getIssues(username, repositoryName, params).stream()
						.sorted(Comparator.comparing(Issue::getUpdatedAt).reversed()).limit(20)
						.collect(Collectors.toList());
				List<String> list = new ArrayList<>();
				ObjectMapper mapper = new ObjectMapper();
				repositoryData = mapper.createObjectNode();
				ArrayNode arrayNode = mapper.createArrayNode();
				list.forEach(arrayNode::add);

				JsonNode repositoryJsonNode = mapper.convertValue(repository, JsonNode.class);
				JsonNode issueJsonNode = mapper.convertValue(issues, JsonNode.class);
				repositoryData.set("repositoryProfile", repositoryJsonNode);
				repositoryData.set("issueList", issueJsonNode);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return repositoryData;
		});
	}

	/**
	 * Returns the Word level Statistics for all Issues for the provided username
	 * and repository name
	 * 
	 * @author Anushka Shetty 40192371
	 * @param userName       the user who owns the repository.
	 * @param repositoryName the name of the repository to be searched for.
	 * @return represents the async response
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
	 * @param issues List of all the issues for the given username and repository
	 *              name
	 * @return represents the async response
	 *         containing the process stage of IssueWordStatistics object
	 */
	public CompletableFuture<IssueWordStatistics> getWordLevelStatistics(final List<Issue> issues) {

		return supplyAsync(() -> {

			String[] listCommonWords = { "the", "a", "an", "are", "and", "not", "be", "for", "on", "to", "of", "in",
					"by", "is", "or" };
			Set<String> commonWords = new HashSet<>(Arrays.asList(listCommonWords));

			// Converting Issue list into list of strings
			List<String> newList = new ArrayList<>(issues.size());
			for (Issue issue : issues) {
				newList.add(String.valueOf(issue.getTitle()));
			}

			// Splitting words
			List<String> list = (newList).stream().map(w -> w.toLowerCase().trim().split("\\s+"))
					.flatMap(Arrays::stream).filter(q -> !commonWords.contains(q)).collect(Collectors.toList());
			long count = list.stream().distinct().count();
			// Mapping words with their frequency
			Map<String, Integer> wordsCountMap = list.stream().map(eachWord -> eachWord)
					.collect(Collectors.toMap(w -> w, w -> 1, Integer::sum));
			wordsCountMap.put("Total Word Count", (int) count);

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
	 * @param userName the user who owns the repository.
	 * @return represents the async response
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

	/**
	 * List of the search results of the phrase entered by the user
	 * 
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
			Map<String, List<UserRepositoryTopics>> searchMap = sessionHelper.getSearchResultsForCurrentSession(request,
					phrase, userRepositoryTopicsList);

			return searchMap;
		});
	}

	public CompletionStage<Map<String, List<UserRepositoryTopics>>> searchResultsUsingActors(String phrase) {
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

			searchSessionMap.put(phrase,userRepositoryTopicsList);
			return searchSessionMap;
		});
	}

	/**
	 * @author Trusha Patel
	 * @param topic_name The query topic
	 * @return  represents the async
	 *         response containing the process stage of SearchedRepositoryDetails
	 *         object
	 */

	public CompletionStage<JsonNode> getReposByTopics(String topic_name){
		return CompletableFuture.supplyAsync(() -> {
			SearchResults results = new SearchResults();
			Map<String, String> searchQuery = new HashMap<String, String>();
			searchQuery.put("topic", topic_name);
			List<SearchRepository> searchRepositoryList = null;
			ObjectMapper mapper = new ObjectMapper();
			ObjectNode repositoryData = mapper.createObjectNode();
			try {
				searchRepositoryList = repositoryService.searchRepositories(searchQuery).stream()
						.sorted(Comparator.comparing(SearchRepository::getPushedAt).reversed()).limit(10)
						.collect(Collectors.toList());
				List<UserRepositoryTopics> userRepositoryTopicsList = new ArrayList<>();
				for (SearchRepository searchRepository : searchRepositoryList)
				{
					UserRepositoryTopics userRepositoryTopics = new UserRepositoryTopics(searchRepository.getOwner(),
							searchRepository.getName());
					userRepositoryTopics.setTopics(getTopics(searchRepository));
					userRepositoryTopics.setPushedAt(searchRepository.getPushedAt());
					userRepositoryTopics.setDescription(searchRepository.getDescription());
					userRepositoryTopicsList.add(userRepositoryTopics);
					results.setKeyword(topic_name);
					results.setRepos(userRepositoryTopicsList);
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
			JsonNode repositoryJsonNode = mapper.convertValue(results, JsonNode.class);
			repositoryData.set("searchProfile", repositoryJsonNode);
			//System.out.println("trusha json service:" + repositoryData);

			return repositoryData;

		});

	}

	/**
	 * @param searchRepository A SearchRepository object to get the topics for
	 * @return List of the topics of the queried Repository
	 * @author Trusha Patel 40192614
	 */
	public List<String> getTopics(SearchRepository searchRepository) {
		GitHubRequest request = new GitHubRequest();
		List<String> topic_list = new ArrayList<>();
		try {
			String url = searchRepository.getUrl().split("//")[1].split("github.com")[1];
			request.setUri("/repos" + url + "/topics");
			String result = new BufferedReader(new InputStreamReader(gitHubClient.getStream(request))).lines()
					.collect(Collectors.joining("\n"));
			JSONObject jsonObject = new JSONObject(result);
			topic_list = Arrays.stream(jsonObject.get("names").toString().replace("[", "").replace("]", "").split(","))
					.collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (topic_list.isEmpty()) {
			topic_list.add("No Topics");
		}
		return topic_list;
	}


	/**
	 * @param userName       the user who owns the repository.
	 * @param repositoryName the name of the repository to be searched for.
	 * @return Commit Stats for the Repository with the Top Committers
	 * @author Anmol Malhotra 40201452
	 */
	public CompletionStage<CommitDetails> getCommitsForRepository(String userName, String repositoryName) {
		return supplyAsync(() -> {
			CommitDetails commitDetails = new CommitDetails();
			try {
				Repository repository = repositoryService.getRepository(userName, repositoryName);
				List<RepositoryCommit> listOfCommits = commitService.getCommits(repository).stream().limit(100).collect(Collectors.toList());

				LinkedHashMap<String, Integer> mapOfTop10UsersAndCommitCounts = sortHashMap(getMapOfTop10UsersAndCommitCounts(listOfCommits));

				List<String> listOfCommitIds = listOfCommits.stream().limit(100).map(RepositoryCommit::getSha).collect(Collectors.toList());
				List<Commits> listOfCommitStats = getListOfCommitStats(userName, repositoryName, listOfCommitIds);

				List<Commits> customListOfCommitsData = listOfCommitStats.parallelStream()
						.map(commitStat -> new Commits(commitStat.getName(), commitStat.getAdditions(), commitStat.getDeletions()))
						.collect(Collectors.toList());

				setMinMaxAndAverageAdditionDeletions(commitDetails, customListOfCommitsData);

				commitDetails.setTotalCommitsOnRepository(listOfCommits.size());
				commitDetails.setRepositoryName(repositoryName);
				commitDetails.setMapOfUserAndCommits(mapOfTop10UsersAndCommitCounts);

			} catch (IOException exception) {
				exception.printStackTrace();
			}
			return commitDetails;
		});
	}

	/**
	 * @param unsortedHashMap		unsorted hashMap for the Top Committers in the Repository
	 * @return Sorted Linked HashMap in reverse Order of the number of commits per user.
	 * @author Anmol Malhotra 40201452
	 */
	public LinkedHashMap<String, Integer> sortHashMap(Map<String, Integer> unsortedHashMap) {
		LinkedHashMap<String, Integer> sortedLinkedHashMap = new LinkedHashMap<>();

		unsortedHashMap.entrySet()
				.stream()
				.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
				.forEachOrdered(sortedPair -> sortedLinkedHashMap.put(sortedPair.getKey(), sortedPair.getValue()));
		return sortedLinkedHashMap;
	}

	/**
	 * @param listOfCommits	list of top 100 commits for the repository
	 * @return Unsorted Map of User and Commit count
	 * @author Anmol Malhotra 40201452
	 */
	private Map<String, Integer> getMapOfTop10UsersAndCommitCounts(List<RepositoryCommit> listOfCommits) {
		return listOfCommits
				.parallelStream()
				.collect(Collectors.toMap(commit -> commit.getAuthor().getLogin(), commit -> 1, Integer::sum));
	}

	/**
	 * @param commitDetails	Model for fetching Commit related data on the UI.
	 * @param customListOfCommitsData	custom-made list of Commits Data
	 * @author Anmol Malhotra 40201452
	 */
	private void setMinMaxAndAverageAdditionDeletions(CommitDetails commitDetails, List<Commits> customListOfCommitsData) {
		Optional<Commits> maxAdditionsCommit = (customListOfCommitsData.parallelStream().max(Comparator.comparing(Commits::getAdditions)));
		int maxAdditions = (maxAdditionsCommit.map(Commits::getAdditions).orElse(0));

		Optional<Commits> minAdditionsCommit = (customListOfCommitsData.parallelStream().min(Comparator.comparing(Commits::getAdditions)));
		int minAdditions = (maxAdditionsCommit.map(Commits::getAdditions).orElse(0));

		Optional<Commits> maxDeletionsCommit = (customListOfCommitsData.parallelStream().max(Comparator.comparing(Commits::getDeletions)));
		int maxDeletions = (maxAdditionsCommit.map(Commits::getDeletions).orElse(0));

		Optional<Commits> minDeletionsCommit = (customListOfCommitsData.parallelStream().max(Comparator.comparing(Commits::getDeletions)));
		int minDeletions = (maxAdditionsCommit.map(Commits::getDeletions).orElse(0));

		double averageAdditions = (customListOfCommitsData.parallelStream()
				.mapToDouble(Commits::getAdditions)
				.reduce(0, Double::sum)) / customListOfCommitsData.size();

		double averageDeletions = (customListOfCommitsData.parallelStream()
				.mapToDouble(Commits::getDeletions)
				.reduce(0, Double::sum)) / customListOfCommitsData.size();

		commitDetails.setMinimumDeletions(minDeletions);
		commitDetails.setMaximumDeletions(maxDeletions);
		commitDetails.setAverageDeletions((int) averageDeletions);
		commitDetails.setMinimumAdditions(minAdditions);
		commitDetails.setMaximumAdditions(maxAdditions);
		commitDetails.setAverageAdditions((int) averageAdditions);
	}

	/**
	 * @param userName       the user who owns the repository.
	 * @param repositoryName the name of the repository to be searched for.
	 * @param listOfCommitIds	list of all the commit ids for which Commit Data is required.
	 * @return Returns list of Commit Stats.
	 * @author Anmol Malhotra 40201452
	 */
	private List<Commits> getListOfCommitStats(String userName, String repositoryName, List<String> listOfCommitIds) {
		List<Commits> commitStatsList = new ArrayList<>();
		listOfCommitIds.forEach(commitId -> {
			try {
				commitStatsList.add(getCommitStatById(userName, repositoryName, commitId).get()	);
			} catch (IOException | InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		});
		return commitStatsList;
	}

	/**
	 * @param userName       the user who owns the repository.
	 * @param repositoryName the name of the repository to be searched for.
	 * @param commitId commit id for which Commit Data is required.
	 * @return Returns Commit Stats for the mentioned commit-id.
	 * @author Anmol Malhotra 40201452
	 */
	private CompletableFuture<Commits> getCommitStatById(String userName, String repositoryName, String commitId) throws IOException {
		return CompletableFuture.supplyAsync(() -> {
			GitHubRequest request = new GitHubRequest();
			request.setUri("/repos/" + userName + "/" + repositoryName + "/commits/" + commitId);
			String result = null;
			try {
				result = new BufferedReader(new InputStreamReader(gitHubClient.getStream(request)))
						.lines().collect(Collectors.joining("\n"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			JSONObject jsonResult = new JSONObject(result);

			Commits commits = new Commits();
			setCommitsData(commits, jsonResult);
			return commits;
		});
	}

	/**
	 * @param commits commit stats data for one commit
	 * @param jsonResult json result from the commit-stats data API
	 * @author Anmol Malhotra 40201452
	 */
	private void setCommitsData(Commits commits, JSONObject jsonResult) {
		commits.setName(jsonResult.getJSONObject("author").getString("login"));
		commits.setSha(jsonResult.getString("sha"));
		commits.setAdditions(jsonResult.getJSONObject("stats").getInt("additions"));
		commits.setDeletions(jsonResult.getJSONObject("stats").getInt("deletions"));
	}
}
