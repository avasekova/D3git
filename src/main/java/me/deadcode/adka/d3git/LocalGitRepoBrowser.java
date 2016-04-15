package me.deadcode.adka.d3git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.*;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LocalGitRepoBrowser extends GitRepoBrowser {

    public LocalGitRepoBrowser(String repositoryPath) {
        super(repositoryPath);
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
                    List<String> lines = new ArrayList<>();
                    Process p = bob.start();

                    //read continually from the process output
                    // otherwise it may fill up and the process will hang forever with no indication of what's wrong
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                        //TODO consume the error outputstream in the same way, just to be sure
                        while (p.isAlive()) {
                            String l;
                            while ((l = reader.readLine()) != null) {
                                lines.add(l);
                            }
                        }
                    }
                    /* output in the following format:
                       <author_name>            //TODO if no author_name: empty line or omitted?
                       <author_email>           //TODO ditto
                       <timestamp> <hash>
                       <message>
                        X files changed, Y insertions(+), Z deletions(-)
                    */

                    Iterator<String> it = lines.iterator();
                    while (it.hasNext()) {
                        String l = it.next();
                        String authorName = l;
                        String authorEmail = it.next();
                        String tsHash = it.next();
                        String timestamp = tsHash.split(" ")[0];
                        String hash = tsHash.split(" ")[1];

                        String message = ""; //can be 0..* lines
                        Pattern diffLinePattern = Pattern.compile("^ ([0-9]+) file(s)? changed(, ([0-9]+) insertion(s)?\\(\\+\\))?(, ([0-9]+) deletion(s)?\\(\\-\\))?$");
                        l = it.next();
                        Matcher diffLineMatcher = diffLinePattern.matcher(l);
                        while (!diffLineMatcher.matches()) {
                            message += "\n" + l;
                            l = it.next();
                            diffLineMatcher = diffLinePattern.matcher(l);
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

                        if (it.hasNext()) {
                            it.next(); //and an empty line at the end
                        }
                    }

                    diffs.put(branch, branchDiffs);
                }
            }
        } catch (IOException | NoSuchElementException | GitAPIException e) {
            e.printStackTrace();
        }

        return diffs;
    }

}
