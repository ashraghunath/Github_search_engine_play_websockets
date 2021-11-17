package Helper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import models.UserRepositoryTopics;
import play.mvc.Http;

public class SessionHelper {
	private static final Map<String, Map<String, List<UserRepositoryTopics>>> searchSessionMap = new LinkedHashMap<>();
	private static final String SESSION_KEY = "sessionKey";

	public String getSessionKey() {
		return SESSION_KEY;
	}

	/**
	 * @author Anushka Shetty 40192371
	 * @return String representing Unique User Identification to differentiate each
	 *         session
	 */
	public String generateSessionValue() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}

	/**
	 * @author Anushka Shetty 40192371
	 * @param HTTP Request object
	 * @return boolean representing whether there is an ongoing session or not
	 */
	public boolean checkSessionExist(Http.Request request) {
		return request.session().get(SESSION_KEY).orElse(null) != null;
	}

	/**
	 * @author Anushka Shetty 40192371
	 * @param HTTP Request object
	 * @return String containing value of the sessionKey
	 */
	public String getSessionValue(Http.Request request) {
		return request.session().get(SESSION_KEY).orElse(null);
	}

	/**
	 * @author Anushka Shetty 40192371
	 * @param HTTP Request object, searchTerm String, searchResults List
	 * @return Map<searchTerm,List<UserRepositoryTopics> containing key,value pair
	 *         of the searchTerm against the searchResults for the current session
	 */
	public Map<String, List<UserRepositoryTopics>> getSearchResultsForCurrentSession(Http.Request request,
			String searchTerm, List<UserRepositoryTopics> searchResults) {
		String sessionValue = getSessionValue(request);
		Map<String, List<UserRepositoryTopics>> searchMap = new LinkedHashMap<>();
		Map<String, List<UserRepositoryTopics>> phraseList = searchSessionMap.get(sessionValue) != null
				? searchSessionMap.get(sessionValue)
				: new LinkedHashMap<>();
		if (searchTerm != null && searchResults != null) {
			phraseList.put(searchTerm, searchResults);
			searchSessionMap.put(sessionValue, phraseList);
		}
		if(searchSessionMap.containsKey(sessionValue))
			searchMap.putAll(searchSessionMap.get(sessionValue));
		return searchMap;
	}
}
