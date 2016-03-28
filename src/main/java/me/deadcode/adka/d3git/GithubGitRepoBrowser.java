package me.deadcode.adka.d3git;


import org.eclipse.egit.github.core.RepositoryBranch;
import org.eclipse.egit.github.core.RepositoryCommit;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.CommitService;
import org.eclipse.egit.github.core.service.RepositoryService;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class GithubGitRepoBrowser extends GitRepoBrowser {

    private static final String AUTH_PROPERTIES = "src/main/resources/auth.properties";
    private static final String OAUTH_TOKEN = "oauth_token";

    private GitHubClient gitHubClient;
    private RepositoryId repo;

    public GithubGitRepoBrowser(String repositoryPath) {
        super(repositoryPath);

        Properties authProp = new Properties();
        try {
            authProp.load(new FileReader(AUTH_PROPERTIES));

            this.gitHubClient = new GitHubClient();
            gitHubClient.setOAuth2Token(authProp.get(OAUTH_TOKEN).toString());

            repo = RepositoryId.createFromId(repositoryPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, List<CommitInfoDiff>> getAllChanges() {
        Map<String, List<CommitInfoDiff>> commitsByBranch = new HashMap<>();

        try {RepositoryService repositoryService = new RepositoryService(gitHubClient);
            List<RepositoryBranch> branches = repositoryService.getBranches(repo);

            CommitService service = new CommitService(gitHubClient);

            for (RepositoryBranch br : branches) {
                List<CommitInfoDiff> commits = new ArrayList<>();

                service.getCommits(repo, br.getName(), null).forEach(c -> {

                    RepositoryCommit commitWithStats = c;
                    try {
                        //for some reason, c.getStats() is null, but when asked explicitly for this commit, the service retrieves the stats as well
                        commitWithStats = service.getCommit(repo, c.getSha());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    commits.add(
                            new CommitInfoDiff(
                                    c.getCommit().getAuthor().getDate().toInstant(),
                                    c.getAuthor() == null ? "<dummyName>" : c.getAuthor().getName(), //still not sure why getAuthor returns null sometimes :/
                                    c.getAuthor() == null ? "<dummyEmail>" : c.getAuthor().getEmail(), //TODO maybe commitWithStats.getAuthor is safer?
                                    c.getSha(),
                                    c.getCommit().getMessage(),
                                    0,   //TODO get the number of affected files
                                    commitWithStats.getStats() == null ? -1 : commitWithStats.getStats().getAdditions(),
                                    commitWithStats.getStats() == null ? -1 : commitWithStats.getStats().getDeletions()));
                });

                commitsByBranch.put(br.getName(), commits);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return commitsByBranch;
    }
}
