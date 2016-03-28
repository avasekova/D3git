package me.deadcode.adka.d3git;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ElasticsearchLoader {

    private static final String INDEX = "d3gitindex";

    public static Map<LocalDate, Long> loadGithubRepo(String repositoryPath) {
        return loadRepo(new GithubGitRepoBrowser(repositoryPath));
    }

    public static Map<LocalDate, Long> loadLocalRepo(String repositoryPath) {
        return loadRepo(new LocalGitRepoBrowser(repositoryPath));
    }

    private static Map<LocalDate, Long> loadRepo(GitRepoBrowser git) {
        Map<String, List<CommitInfoDiff>> commits = git.getAllChanges();

        try (Client client = TransportClient.builder().build()
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9300))) { //!!! not 9200

            //delete if exists
            boolean indexExists = client.admin().indices().prepareExists(INDEX).execute().actionGet().isExists();
            if (indexExists) {
                client.admin().indices().prepareDelete(INDEX).execute().actionGet();
            }
            //then create
            //TODO specify mapping for type "commit"? at least the date. might help later
            client.admin().indices().prepareCreate(INDEX).execute().actionGet();


            //index all commits from master
            for (CommitInfoDiff commit : commits.get("master")) {
                client.prepareIndex(INDEX, "commit")
                        .setSource(commit.toJSONString())
                        .get();
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }


        //TODO later remove and get the data from elasticsearch
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
