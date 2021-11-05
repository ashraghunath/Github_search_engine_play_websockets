package models;

import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.User;

import java.util.ArrayList;
import java.util.List;

public class RepositoryDetails {
    private Repository repository;
    private List<User> collaborators;
    private List<Issue> issues;

    public RepositoryDetails() {
        this.collaborators=new ArrayList<>();
        this.issues=new ArrayList<>();
    }

    public RepositoryDetails(Repository repository, List<User> collaborators, List<Issue> issues) {
        this.repository = repository;
        this.collaborators = collaborators;
        this.issues = issues;
    }


    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public List<User> getCollaborators() {
        return collaborators;
    }

    public void setCollaborators(List<User> collaborators) {
        this.collaborators = collaborators;
    }

    public List<Issue> getIssues() {
        return issues;
    }

    public void setIssues(List<Issue> issues) {
        this.issues = issues;
    }
}
