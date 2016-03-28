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
        numInsertionsPerDay.putAll(fillGaps(numInsertionsPerDay));

        return numInsertionsPerDay;
    }


    /*  [
          {"key":"2016-01-01"," value":90},
          {"key":"250"," y-coordinate":"50"}
          ...
        ]
    */
    public static String mapToJSON(Map<LocalDate, Long> map) {
        StringBuilder builder = new StringBuilder("[");

        boolean comma = false;
        for (Map.Entry<LocalDate, Long> entry : map.entrySet()) {
            if (comma) {
                builder.append(",");
            } else {
                comma = true;
            }

            builder.append("{\"key\":\"").append(entry.getKey()).append("\",\"value\":").append(entry.getValue()).append("}");
        }

        builder.append("]");

        return builder.toString();
    }

    private static Map<LocalDate, Long> fillGaps(Map<LocalDate, Long> numsPerDay) {
        Map<LocalDate, Long> newEntries = new HashMap<>();
        if (numsPerDay.size() > 1) {
            Iterator<LocalDate> iterator = numsPerDay.keySet().iterator();
            LocalDate currentEntry = iterator.next();
            LocalDate expectedNext = currentEntry.plusDays(1);
            LocalDate nextEntry = iterator.next();

            while (true) { //'while (iterator.hasNext())' is not enough, still needs to fill in everything until the last entry
                while (! nextEntry.equals(expectedNext)) { //there is a gap
                    newEntries.put(expectedNext, 0L);
                    currentEntry = expectedNext;
                    expectedNext = currentEntry.plusDays(1);
                }

                if (iterator.hasNext()) {
                    currentEntry = nextEntry;
                    expectedNext = currentEntry.plusDays(1);
                    nextEntry = iterator.next();
                } else {
                    break;
                }
            }
        }

        return newEntries;
    }

//    public static void main(String[] args) { //num Commits
//        GitRepoBrowser git = new LocalGitRepoBrowser();
//        Map<String, List<CommitInfo>> commits = git.getAllCommits("..\\DragonsFX");
//
////        GitRepoBrowser git = new GithubGitRepoBrowser();
////        Map<String, List<CommitInfo>> commits = git.getAllCommits("avasekova/adml");
//
//        //TODO double-check all this - timezones etc.
//        //number of commits per day;       TODO pbbly move to GitRepoBrowser
//        Map<LocalDate, Integer> numCommitsPerDay = new TreeMap<>();
//        for (CommitInfo commit : commits.get("master")) {
//            LocalDate date = commit.getDate().atZone(ZoneId.systemDefault()).toLocalDate();
//            if (numCommitsPerDay.containsKey(date)) {
//                numCommitsPerDay.put(date, numCommitsPerDay.get(date) + 1);
//            } else {
//                numCommitsPerDay.put(date, 1);
//            }
//        }
//
//        //fill in the gaps
//        numCommitsPerDay.putAll(fillGaps(numCommitsPerDay));
//
//
//        //for now, output to csv so we can directly parse it with the D3 things we already have
//        outputToCsv(numCommitsPerDay);
//    }
}
