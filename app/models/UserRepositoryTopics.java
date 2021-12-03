package models;

import org.eclipse.egit.github.core.SearchRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserRepositoryTopics extends SearchRepository {

    List<String> topics;
    String owner;
    String name;
    Date pushedAt;
    String description;

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



    public Date getPushedAt() {
        return this.pushedAt;
    }

    public void setPushedAt(Date pushedAt) {
        System.out.println("Child pushed at");
        this.pushedAt = pushedAt;
        System.out.println(this.pushedAt);
    }
    public void setDescription(String description) {
        this.description = description;
    }
}