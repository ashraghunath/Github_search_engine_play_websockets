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

        public RepositoryDetails(JsonNode repositoryProfileResult) {
            this.repositoryDetails = repositoryProfileResult;
        }
    }


}
