package models;

import java.util.List;

public class SearchResults {
    private String keyword;
    private List<UserRepositoryTopics> repos;


    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public List<UserRepositoryTopics> getRepos() {
        return repos;
    }

    public void setRepos(List<UserRepositoryTopics> repos) {
        this.repos = repos;
    }



}
