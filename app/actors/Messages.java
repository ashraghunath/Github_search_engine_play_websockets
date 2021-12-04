package actors;

import com.fasterxml.jackson.databind.JsonNode;

public class Messages {
    public static final class TrackSearch {
        public final String searchQuery;
        public final String requestType;

        /**
         * @param searchQuery Search query to be tracked by <code>SearchActor</code>
         * @param requestType Indicates whether the request is a periodic search query sent by the search actor itself or a request sent from client side
         */
        public TrackSearch(String searchQuery, String requestType) {
            this.searchQuery = searchQuery;
            this.requestType = requestType;
        }
    }

    public static final class GetRepositoryDetailsActor {
        public final String repositoryName;
        public final String username;

        public GetRepositoryDetailsActor(String username, String repositoryName) {
            this.username = username;
            this.repositoryName = repositoryName;
        }
    }

    public static final class RepositoryDetails {
        public final JsonNode repositoryDetails;

        public RepositoryDetails(JsonNode repositoryDetails) {
            this.repositoryDetails = repositoryDetails;
        }
    }


    public static final class SearchPageActor{
        public final String phrase;

        public SearchPageActor(String phrase) {
            this.phrase = phrase;
        }
    }

    public static final class SearchResult {
        public final JsonNode searchResult;
        public SearchResult(JsonNode searchResult) {
            this.searchResult = searchResult;
        }
    }

    public static final class GetRepositoryfromTopic {
        public final String topic_name;

        public GetRepositoryfromTopic(String topic_name) {
            this.topic_name = topic_name;
        }
    }

    public static final class TopicDetails {
        public final JsonNode topicDetails;

        public TopicDetails(JsonNode topicSearchResult) {
            this.topicDetails = topicSearchResult;
        }
    }

    public static final class GetUserDetailsActor {
        public final String username;

        public GetUserDetailsActor(String username) {
            this.username = username;
        }
    }

    public static final class UserDetails {
        public final JsonNode userDetails;

        public UserDetails(JsonNode userProfileResult) {
            this.userDetails = userProfileResult;
        }
    }


}
