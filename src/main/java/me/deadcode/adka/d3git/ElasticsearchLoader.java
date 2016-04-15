package me.deadcode.adka.d3git;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

public class ElasticsearchLoader {

    private static final String INDEX = "d3gitindex";

    public static void loadGithubRepo(String repositoryPath) {
        loadRepo(new GithubGitRepoBrowser(repositoryPath));
    }

    public static void loadLocalRepo(String repositoryPath) {
        loadRepo(new LocalGitRepoBrowser(repositoryPath));
    }

    private static void loadRepo(GitRepoBrowser git) {
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

            //refresh indices (for some reason the querying does not work unless this is done)
            client.admin()
                    .indices()
                    .prepareRefresh()
                    .execute()
                    .actionGet();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
