package me.deadcode.adka.d3git;


import org.eclipse.egit.github.core.RepositoryBranch;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.CommitService;
import org.eclipse.egit.github.core.service.RepositoryService;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class GithubGitRepoBrowser implements GitRepoBrowser {


    private static final String AUTH_PROPERTIES = "src/main/resources/me/deadcode/adka/d3git/auth.properties";
    private static final String OAUTH_TOKEN = "oauth_token";

    @Override
    public Map<String, List<CommitInfo>> getAllCommits(String repositoryPath) {
        Map<String, List<CommitInfo>> commitsByBranch = new HashMap<>();

        try {
            Properties authProp = new Properties();
            authProp.load(new FileReader(AUTH_PROPERTIES));

            GitHubClient gitHubClient = new GitHubClient();
            gitHubClient.setOAuth2Token(authProp.get(OAUTH_TOKEN).toString());

            RepositoryId repo = RepositoryId.createFromId(repositoryPath);

            RepositoryService repositoryService = new RepositoryService(gitHubClient);
            List<RepositoryBranch> branches = repositoryService.getBranches(repo);

            CommitService service = new CommitService(gitHubClient);

            for (RepositoryBranch br : branches) {
                List<CommitInfo> commits = new ArrayList<>();

                service.getCommits(repo, br.getName(), null).forEach(c -> { commits.add(
                        new CommitInfo(
                                c.getCommit().getAuthor().getDate().toInstant(),
                                c.getAuthor() == null ? "<dummyName>" : c.getAuthor().getName(), //still not sure why getAuthor returns null sometimes :/
                                c.getAuthor() == null ? "<dummyEmail>" : c.getAuthor().getEmail(),
                                c.getSha(),
                                c.getCommit().getMessage()));
                });

                commitsByBranch.put(br.getName(), commits);
            }



        } catch (IOException e) {
            e.printStackTrace();
        }


        return commitsByBranch;
    }

    @Override
    public Map<String, List<CommitInfoDiff>> getAllChanges(String repositoryPath) {
        throw new UnsupportedOperationException();
    }
}
