package me.deadcode.adka.d3git;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        GitRepoBrowser git = new LocalGitRepoBrowser();
        Map<String, List<CommitInfo>> commits = git.getAllCommits(new File("..\\adml\\.git"));

        //TODO double-check all this - timezones etc.
        //number of commits per day;       TODO pbbly move to GitRepoBrowser
        Map<LocalDate, Integer> numCommitsPerDay = new TreeMap<>();
        for (CommitInfo commit : commits.get("master")) {
            LocalDate date = commit.getDate().atZone(ZoneId.systemDefault()).toLocalDate();
            if (numCommitsPerDay.containsKey(date)) {
                numCommitsPerDay.put(date, numCommitsPerDay.get(date) + 1);
            } else {
                numCommitsPerDay.put(date, 1);
            }
        }

        //fill in the gaps
        Map<LocalDate, Integer> newEntries = new HashMap<>();
        if (numCommitsPerDay.size() > 1) {
            Iterator<LocalDate> iterator = numCommitsPerDay.keySet().iterator();
            LocalDate currentEntry = iterator.next();
            LocalDate expectedNext = currentEntry.plusDays(1);
            LocalDate nextEntry = iterator.next();

            while (true) { //'while (iterator.hasNext())' is not enough, still needs to fill in everything until the last entry
                while (! nextEntry.equals(expectedNext)) { //there is a gap
                    newEntries.put(expectedNext, 0);
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

        numCommitsPerDay.putAll(newEntries);


        //for now, output to csv so we can directly parse it with the D3 things we already have
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/me/deadcode/adka/d3git/data_commits.csv"))) {
            writer.append("name,value");
            writer.newLine();

            for (Map.Entry<LocalDate, Integer> entry : numCommitsPerDay.entrySet()) {
                writer.append(entry.getKey().toString()).append(",").append(entry.getValue() + "");
                writer.newLine();
            }

            writer.flush();
        } catch (IOException e) {
            //TODO log
        }
    }
}