package me.deadcode.adka.d3git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.patch.FileHeader;
import org.eclipse.jgit.patch.HunkHeader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.*;

public class LocalGitRepoBrowser implements GitRepoBrowser {

    @Override
    public Map<String, List<CommitInfo>> getAllCommits(String repositoryPath) {
        Map<String, List<CommitInfo>> commitsByBranch = new HashMap<>();

        FileRepositoryBuilder builder = new FileRepositoryBuilder();

        try (Repository repository = builder.setGitDir(new File(repositoryPath)).build();
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


                //----prints files diff, but not all additions and deletions and I really don't know how to get them
                Iterable<RevCommit> ccc = git.log().add(ref.getObjectId()).call();
                Iterator<RevCommit> it = ccc.iterator();
                RevCommit newestCommit = null;
                RevCommit onestepOlderCommit = it.next(); //TODO size > 1

                while (it.hasNext()) {
                    newestCommit = onestepOlderCommit;
                    onestepOlderCommit = it.next();

                    // prepare the two iterators to compute the diff between
                    try (ObjectReader reader = repository.newObjectReader()) {
                        ObjectId oldHead = repository.resolve(onestepOlderCommit.getName() + "^{tree}");
                        ObjectId head = repository.resolve(newestCommit.getName() + "^{tree}");
                        System.out.println("\nPrinting diff between tree: " + oldHead + " and " + head);

                        CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
                        oldTreeIter.reset(reader, oldHead);
                        CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
                        newTreeIter.reset(reader, head);

                        // Use a DiffFormatter to compare new and old tree and return a list of changes
                        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                            DiffFormatter diffFormatter = new DiffFormatter(out);
                            diffFormatter.setRepository(repository);
                            diffFormatter.setContext(0);
                            List<DiffEntry> diffs = diffFormatter.scan(oldTreeIter, newTreeIter);


                            for (DiffEntry entry : diffs) {
                                System.out.println("Entry: " + entry);
                                FileHeader fileHeader = diffFormatter.toFileHeader(entry);
                                List<? extends HunkHeader> hunks = fileHeader.getHunks();
                                for (HunkHeader hunk : hunks) {
                                    System.out.println(hunk + ": added: " + (hunk.getNewLineCount()) +
                                            ", deleted: " + hunk.getOldImage().getLinesDeleted());
                                }
                            }
                        }

                    }
                }
                //TODO "diff" of initial commit
                // git.diff().setNewTree(newTreeIter).setOldTree(null).call();, means diff to HEAD
                git.log().add(onestepOlderCommit).call().forEach(i -> System.out.println(new CommitInfo(Instant.ofEpochSecond(i.getCommitTime()),
                        i.getAuthorIdent().getName(),
                        i.getAuthorIdent().getEmailAddress(),
                        i.getName(),
                        i.getFullMessage().trim())));


            }

        } catch (GitAPIException | IOException e) {
            //TODO log
            e.printStackTrace();
        }

        return commitsByBranch;
    }
}
