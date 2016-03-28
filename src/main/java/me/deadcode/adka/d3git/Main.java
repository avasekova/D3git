package me.deadcode.adka.d3git;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class Main {

    public static Map<LocalDate, Long> loadGithubRepo(String repositoryPath) {
        return loadRepo(new GithubGitRepoBrowser(repositoryPath));
    }

    public static Map<LocalDate, Long> loadLocalRepo(String repositoryPath) {
        return loadRepo(new LocalGitRepoBrowser(repositoryPath));
    }

    private static Map<LocalDate, Long> loadRepo(GitRepoBrowser git) {
        Map<String, List<CommitInfoDiff>> commits = git.getAllChanges();

        //number of commits per day;       TODO pbbly move to GitRepoBrowser
        Map<LocalDate, Long> numInsertionsPerDay = new TreeMap<>();
        for (CommitInfoDiff commit : commits.get("master")) {
            LocalDate date = commit.getDate().atZone(ZoneId.systemDefault()).toLocalDate();
            if (numInsertionsPerDay.containsKey(date)) {
                numInsertionsPerDay.put(date, numInsertionsPerDay.get(date) + commit.getInsertions());
            } else {
                numInsertionsPerDay.put(date, commit.getInsertions());
            }
        }

        //fill in the gaps
        numInsertionsPerDay.putAll(Util.fillGaps(numInsertionsPerDay));

        return numInsertionsPerDay;
    }
}
