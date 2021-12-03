package actors;

import com.fasterxml.jackson.databind.JsonNode;

public class Messages {

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


}
