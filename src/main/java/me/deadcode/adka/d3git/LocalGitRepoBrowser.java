package me.deadcode.adka.d3git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LocalGitRepoBrowser extends GitRepoBrowser {

    public LocalGitRepoBrowser(String repositoryPath) {
        super(repositoryPath);
    }

    @Override
    public Map<String, List<CommitInfo>> getAllCommits() {
        Map<String, List<CommitInfo>> commitsByBranch = new HashMap<>();

        FileRepositoryBuilder builder = new FileRepositoryBuilder();

        try (Repository repository = builder.setGitDir(new File(getRepositoryPath() + "\\.git")).build();
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
            e.printStackTrace();
        }

        return commitsByBranch;
    }

    @Override
    public Map<String, List<CommitInfoDiff>> getAllChanges() {
        Map<String, List<CommitInfoDiff>> diffs = new HashMap<>();

        try {
            try (Repository repository = new FileRepositoryBuilder().setGitDir(new File(getRepositoryPath() + "\\.git")).build();
                 Git git = new Git(repository)) {

                for (Ref ref : git.branchList().call()) {
                    String[] branchNameParts = ref.getName().split("/");
                    String branch = branchNameParts[branchNameParts.length - 1];

                    List<CommitInfoDiff> branchDiffs = new ArrayList<>();

                    ProcessBuilder bob = new ProcessBuilder("git", "log", branch, "--pretty=format:\"%an%n%ae%n%at", "%H%n%s\"", "--shortstat")
                            .directory(new File(getRepositoryPath()));
                    Process p = bob.start();
                    p.waitFor();
                    /* output in the following format:
                       <author_name>            //TODO if no author_name: empty line or omitted?
                       <author_email>           //TODO ditto
                       <timestamp> <hash>
                       <message>
                        X files changed, Y insertions(+), Z deletions(-)
                    */

                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) { //TODO safe?
                            String authorName = line;
                            String authorEmail = reader.readLine();
                            String tsHash = reader.readLine();
                            String timestamp = tsHash.split(" ")[0];
                            String hash = tsHash.split(" ")[1];

                            String message = ""; //can be 0..* lines
                            Pattern diffLinePattern = Pattern.compile("^ ([0-9]+) file(s)? changed(, ([0-9]+) insertion(s)?\\(\\+\\))?(, ([0-9]+) deletion(s)?\\(\\-\\))?$");
                            line = reader.readLine();
                            Matcher diffLineMatcher = diffLinePattern.matcher(line);
                            while (!diffLineMatcher.matches()) {
                                message += "\n" + line;
                                line = reader.readLine();
                                diffLineMatcher = diffLinePattern.matcher(line);
                            }
                            //once it matches, the msg ends and we have the diff line
                            long changedFiles = Long.parseLong(diffLineMatcher.group(1));
                            long insertions = (diffLineMatcher.group(4) == null) ? 0 : Long.parseLong(diffLineMatcher.group(4));
                            long deletions = (diffLineMatcher.group(7) == null) ? 0 : Long.parseLong(diffLineMatcher.group(7)); //TODO named groups

                            CommitInfoDiff info = new CommitInfoDiff(); //TODO builder
                            info.setAuthorName(authorName);
                            info.setAuthorEmail(authorEmail);
                            info.setDate(Instant.ofEpochSecond(Integer.parseInt(timestamp)));
                            info.setHash(hash);
                            info.setMessage(message);
                            info.setFilesChanged(changedFiles);
                            info.setInsertions(insertions);
                            info.setDeletions(deletions);
                            branchDiffs.add(info);

                            reader.readLine(); //and an empty line at the end
                        }
                    }

                    diffs.put(branch, branchDiffs);
                }
            }
        } catch (InterruptedException | IOException | GitAPIException e) {
            e.printStackTrace();
        }

        return diffs;
    }

}
