package models;

import org.eclipse.egit.github.core.SearchRepository;

import java.util.ArrayList;
import java.util.List;

public class UserRepositoryTopics extends SearchRepository {

    List<String> topics;

    public UserRepositoryTopics(String owner, String name) {
        super(owner, name);
        topics = new ArrayList<>();
    }

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }
}
