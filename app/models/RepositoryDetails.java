package models;

import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the object to be displayed on the page - Repository profile
 * @author Ashwin Raghunath 40192120
 */
public class RepositoryDetails {
    private Repository repository;
    private List<User> collaborators;
    private List<Issue> issues;

    public RepositoryDetails() {
        this.collaborators=new ArrayList<>();
        this.issues=new ArrayList<>();
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
