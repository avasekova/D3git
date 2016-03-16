package me.deadcode.adka.d3git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocalGitRepoBrowser implements GitRepoBrowser {

    @Override
    public Map<String, List<CommitInfo>> getAllCommits(File gitDirectory) {
        Map<String, List<CommitInfo>> commitsByBranch = new HashMap<>();

        FileRepositoryBuilder builder = new FileRepositoryBuilder();

        try (Repository repository = builder.setGitDir(gitDirectory).build();
             Git git = new Git(repository)) {

            for (Ref ref : git.branchList().call()) {
                List<CommitInfo> commits = new ArrayList<>();

                git.log()
                        .add(ref.getObjectId()) //start graph traversal from this branch
                        .call()
                        .forEach(i -> commits.add(new CommitInfo(Instant.ofEpochSecond(i.getCommitTime()),
                                i.getAuthorIdent().getName(),
                                i.getAuthorIdent().getEmailAddress(),
                                i.getName(),
                                i.getFullMessage().trim())));

                String[] branchNameParts = ref.getName().split("/");
                commitsByBranch.put(branchNameParts[branchNameParts.length - 1], commits);
            }
        } catch (GitAPIException | IOException e) {
            //TODO log
        }

        return commitsByBranch;
    }
}
