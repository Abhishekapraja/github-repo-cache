package com.netflix.caching.manager;

import com.netflix.caching.external.RepositoryService;
import com.netflix.caching.pojo.Repository;
import com.netflix.caching.pojo.RepositoryHelper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Log4j2
public class ServiceCacheManager {
    private static final String REPO_KEY = "netflix-repos";
    private static final String MEMBERS_KEY = "netflix-members";
    private static final String NETFLIX_ROOT_KEY = "netflix-root";
    private static final String ROOT_KEY = "root";
    private static final String NETFLIX_REPO_SORTED_BY_STARS_KEY = "netflix-sorted_stars";
    private static final String NETFLIX_REPO_SORTED_BY_FORKS_KEY = "netflix-sorted_forks";
    private static final String NETFLIX_REPO_SORTED_BY_ISSUES_KEY = "netflix-sorted_issues";
    private static final String NETFLIX_REPO_SORTED_BY_LAST_UPDATED_KEY = "netflix-sorted_last_updated";

    private static final Duration CACHE_STALE_ALERT = Duration.ofMinutes(30);

    // HashMap to cache the Origin server response in serialized format(String)
    private static ConcurrentHashMap<String, String> serviceCache;

    private static ConcurrentHashMap<String, List<Repository>> viewsCache;
    private Instant serviceCacheLastUpdated;
    private Instant viewsCacheLastUpdated;

    @Autowired private RepositoryService repositoryService;

    public ServiceCacheManager() {
        serviceCache = new ConcurrentHashMap<>();
        viewsCache = new ConcurrentHashMap<>();
    }

    public String getRepoFromCache() {
        return serviceCache.get(REPO_KEY);
    }

    public String getMembersFromCache() {
        return serviceCache.get(MEMBERS_KEY);
    }

    public String getNetflixRootFromCache() {
        return serviceCache.get(NETFLIX_ROOT_KEY);
    }

    public String getRootFromCache() {
        return serviceCache.get(ROOT_KEY);
    }

    public List<Repository> getRepositorySortedByStars() {
        return viewsCache.get(NETFLIX_REPO_SORTED_BY_STARS_KEY);
    }

    public List<Repository> getRepositorySortedByForks() {
        return viewsCache.get(NETFLIX_REPO_SORTED_BY_FORKS_KEY);
    }

    public List<Repository> getRepositorySortedByIssues() {
        return viewsCache.get(NETFLIX_REPO_SORTED_BY_ISSUES_KEY);
    }

    public List<Repository> getRepositorySortedByLastUpdated() {
        return viewsCache.get(NETFLIX_REPO_SORTED_BY_LAST_UPDATED_KEY);
    }

    public void setRepositoryCache() {
        try {
            String serializedValue = repositoryService.getRepositories();
            serviceCache.put(REPO_KEY, serializedValue);
            log.info("Updated repository cache");
            serviceCacheLastUpdated = Instant.now();
        } catch (Exception exception) {
            // Any downstream error should be caught here
            log.error("Error in updating Repo cache ", exception);
        }

        /*
         * Update Views:
         *
         * This is design choice between storage vs latency. Provided we have available storage to keep all
         * sorted views will give better latency for clients as we don't have to sort on every call.
         *
         */
        updateViews();
    }

    public void setMembersCache() {
        try {
            String serializedValue = repositoryService.getMembers();
            serviceCache.put(MEMBERS_KEY, serializedValue);
            log.info("Updated members cache");
        } catch (Exception exception) {
            // Any downstream error should be caught here
            log.error("Error in updating members cache ", exception);
        }
    }

    public void setOrganizationDetailsCache() {
        try {
            String serializedValue = repositoryService.getOrganizationDetails();
            serviceCache.put(NETFLIX_ROOT_KEY, serializedValue);
            log.info("Updated Organization Details cache");
        } catch (Exception exception) {
            // Any downstream error should be caught here
            log.error("Error in updating Org details cache ", exception);
        }
    }

    public void setServiceIndexPageDetails() {
        try {
            String serializedValue = repositoryService.getServiceIndexPageDetails();
            serviceCache.put(ROOT_KEY, serializedValue);
            log.info("Updated service root cache");
        } catch (Exception exception) {
            // Any downstream error should be caught here
            log.error("Error in updating root cache ", exception);
        }
    }

    private void updateViews() {
        final String serializedRepositories = getRepoFromCache();

        try {
            //Set Stars cache
            List<Repository> repositoriesSortedByStars = RepositoryHelper.getRepositoriesSortedByStars(serializedRepositories);
            viewsCache.put(NETFLIX_REPO_SORTED_BY_STARS_KEY, repositoriesSortedByStars);

            //Set Forks cache
            List<Repository> repositoriesSortedByForks = RepositoryHelper.getRepositoriesSortedByForks(serializedRepositories);
            viewsCache.put(NETFLIX_REPO_SORTED_BY_FORKS_KEY, repositoriesSortedByForks);

            // Set Issues cache
            List<Repository> repositoriesSortedByIssues = RepositoryHelper.getRepositoriesSortedByIssues(serializedRepositories);
            viewsCache.put(NETFLIX_REPO_SORTED_BY_ISSUES_KEY, repositoriesSortedByIssues);

            // Set last updated cache
            List<Repository> repositoriesSortedByLastUpdated = RepositoryHelper.getRepositoriesSortedByLastUpdated(serializedRepositories);
            viewsCache.put(NETFLIX_REPO_SORTED_BY_LAST_UPDATED_KEY, repositoriesSortedByLastUpdated);

            viewsCacheLastUpdated = Instant.now();
        } catch (Exception exception) {
            // Any downstream error should be caught here.
            log.error("Error in updating Views ", exception);
        }
    }

    /**
     * Returns the health of the system
     *
     * @return
     */
    public boolean checkHealth() {
        final Duration repoUpdatedSince = Duration.between(Instant.now(), serviceCacheLastUpdated);
        final Duration viewsUpdatedSince = Duration.between(Instant.now(), serviceCacheLastUpdated);

        return viewsCache.size() > 0
                && serviceCache.size() > 0
                && repoUpdatedSince.compareTo(CACHE_STALE_ALERT) < 0
                && viewsUpdatedSince.compareTo(CACHE_STALE_ALERT) < 0;
    }
}
