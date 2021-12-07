package actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import services.GithubService;

import java.util.concurrent.CompletionStage;

public class CommitStatisticsActor extends AbstractActor {

    private ActorRef supervisorActor;
    private GithubService githubService;

    public CommitStatisticsActor(ActorRef supervisorActor, GithubService githubService) {
        this.supervisorActor = supervisorActor;
        this.githubService = githubService;
    }

    public static Props props(ActorRef supervisorActor, GithubService githubService) {
        System.out.println("in commit actor props");
        return Props.create(CommitStatisticsActor.class, supervisorActor, githubService);
    }

    @Override
    public void preStart() {
        System.out.println("Commit Statistics Actor created");
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(Messages.GetCommitStatisticsActor.class, commitStats -> getCommitStatistics(commitStats).thenAcceptAsync(this::processCommitStatisticsResult)).build();
    }

    private CompletionStage<JsonNode> getCommitStatistics(Messages.GetCommitStatisticsActor commitStatsActor) {
        System.out.println("in method getCommitStatistics in Actor");
        return githubService.getCommitStatisticsForRepository(commitStatsActor.username, commitStatsActor.repositoryName)
                .thenApplyAsync(commitStatsData -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    ObjectNode commitStatObjectNode = objectMapper.createObjectNode();
                    JsonNode commitStatsJsonNode = objectMapper.convertValue(commitStatsData, JsonNode.class);

                    commitStatObjectNode.put("responseType", "commitStatisticsPage");
                    commitStatObjectNode.set("commitStatsList", commitStatsJsonNode);
                    System.out.println("repo name is: " + commitStatsActor.repositoryName);
                    commitStatObjectNode.put("repositoryName", commitStatsActor.repositoryName);
                    return commitStatObjectNode;
                });
    }

    private void processCommitStatisticsResult(JsonNode commitStatistics) {
        supervisorActor.tell(new Messages.IssueStatistics(commitStatistics), getSelf());
    }
}
