package com.netflix.caching.pojo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Log4j2
public class RepositoryHelper {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());

    public static List<Repository> getRepositoriesSortedByStars(final String serializedRepositories) {
        final List<Repository> repositories = readTree(serializedRepositories);
        sortRepoByStar(repositories);
        return repositories;
    }

    public static List<Repository> getRepositoriesSortedByForks(final String serializedRepositories) {
        final List<Repository> repositories = readTree(serializedRepositories);
        sortRepoByFork(repositories);
        return repositories;
    }

    public static List<Repository> getRepositoriesSortedByIssues(final String serializedRepositories) {
        final List<Repository> repositories = readTree(serializedRepositories);
        sortRepoByIssues(repositories);
        return repositories;
    }

    public static List<Repository> getRepositoriesSortedByLastUpdated(final String serializedRepositories) {
        final List<Repository> repositories = readTree(serializedRepositories);
        sortRepoByLastUpdatedDate(repositories);
        return repositories;
    }

    private static List<Repository> readTree(String serializedRepositories) {
        JsonNode node;
        try {
            node = OBJECT_MAPPER.readTree(serializedRepositories);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        List<Repository> repositories = deserializeRepositories(node);
        return repositories;
    }

    private static void sortRepoByIssues(List<Repository> repositories) {
        Collections.sort(repositories, (a, b) -> b.getOpenIssues() - a.getOpenIssues());
    }

    private static void sortRepoByLastUpdatedDate(List<Repository> repositories) {
        Collections.sort(repositories, (a, b) -> b.getLastUpdated().compareTo(a.getLastUpdated()));
    }

    private static void sortRepoByFork(List<Repository> repositories) {
        Collections.sort(repositories, (a, b) -> b.getForks() - a.getForks());
    }

    private static void sortRepoByStar(List<Repository> repositories) {
        Collections.sort(repositories, (a, b) -> b.getStars() - a.getStars());
    }

    private static List<Repository> deserializeRepositories(final JsonNode jsonNode) {
        final List<Repository> deserializeRepos = new ArrayList<>();

        if (jsonNode.isArray()) {
            Iterator<JsonNode> iterator = jsonNode.elements();
            while (iterator.hasNext()) {
                JsonNode node = iterator.next();
                try {
                    final Repository repo = OBJECT_MAPPER.treeToValue(node, Repository.class);
                    deserializeRepos.add(repo);
                } catch (Exception e) {
                    log.error("Error in deserializing Repositories");
                    throw new RuntimeException(e);
                }
            }
        }
        return deserializeRepos;
    }
}
