package models;

import java.util.List;

import org.eclipse.egit.github.core.SearchRepository;

/**
 * Class to represent the repositories containing the topics - Topic Match Repositories
 * @author Trusha Patel
 * @version 1.0.0
 *
 */

public class SearchedRepositoryDetails {
	private List<SearchRepository> searched;
	
	public void setRepos(List<SearchRepository> searched) {
		this.searched = searched;
	}
	
	public List<SearchRepository> getRepo() {
		return this.searched;
	}
	
	
	
}
