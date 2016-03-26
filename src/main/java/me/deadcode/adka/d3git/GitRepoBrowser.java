package me.deadcode.adka.d3git;

import java.util.List;
import java.util.Map;

public interface GitRepoBrowser {

    Map<String, List<CommitInfo>> getAllCommits(String repositoryPath);
    Map<String, List<CommitInfoDiff>> getAllChanges(String repositoryPath);
}
