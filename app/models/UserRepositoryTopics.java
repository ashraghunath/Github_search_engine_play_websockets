package models;

import org.eclipse.egit.github.core.SearchRepository;

import java.util.ArrayList;
import java.util.List;

public class UserRepositoryTopics extends SearchRepository {

    List<String> topics;
    private String repoName;
    private String ownerName;

    public UserRepositoryTopics(String owner, String name) {
        super(owner, name);
        topics = new ArrayList<>();
    }

    public String getOwnerName(){
        return this.getOwner();
    }

    public String getRepoName(){
        return this.getName();
    }

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }
}
