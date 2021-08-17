package com.netflix.caching;

import com.netflix.caching.manager.ServiceCacheManager;
import com.netflix.caching.model.ViewResponse;
import com.netflix.caching.model.ViewType;
import com.netflix.caching.pojo.Repository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Log4j2
public class ServiceController {

    @Autowired private ServiceCacheManager cacheManager;

    @GetMapping("/local/orgs/Netflix/members")
    public String getMembers() {
        return cacheManager.getMembersFromCache();
    }

    @GetMapping("/local/orgs/Netflix/repos")
    public String getRepos() {
        return cacheManager.getRepoFromCache();
    }

    @GetMapping("/local/orgs/Netflix")
    public String getNetflixRoot() {
        return cacheManager.getNetflixRootFromCache();
    }

    @GetMapping("/local")
    public String getRoot() {
        return cacheManager.getRootFromCache();
    }

    @GetMapping("/view/top/{n}/{view}")
    public List<ViewResponse> getTopStars(@PathVariable final String n, @PathVariable final String view) {
        final ViewType viewType = ViewType.valueOfLabel(view);
        switch (viewType) {
            case FORKS:
                return buildForksViewResponse(cacheManager.getRepositorySortedByForks(), Integer.valueOf(n));
            case STARS:
                return buildStarsViewResponse(cacheManager.getRepositorySortedByStars(), Integer.valueOf(n));
            case OPEN_ISSUES:
                return buildOpenIssuesViewResponse(cacheManager.getRepositorySortedByIssues(), Integer.valueOf(n));
            case LAST_UPDATED:
                return buildLastUpdatedViewResponse(cacheManager.getRepositorySortedByLastUpdated(), Integer.valueOf(n));
        }
        return new ArrayList<>();
    }

    @GetMapping("/local/healthcheck")
    public String healthCheck() {
        if (cacheManager.checkHealth()) {
            return "healthy";
        } else {
            throw new RuntimeException("Service is not healthy");
        }
    }

    private List<ViewResponse> buildStarsViewResponse(final List<Repository> repositories, final int limit) {
        return repositories.stream()
                .limit(limit)
                .map(repo -> new ViewResponse<Integer>(repo.getFullName(), repo.getStars()))
                .collect(Collectors.toList());
    }

    private List<ViewResponse> buildForksViewResponse(final List<Repository> repositories, final int limit) {
        return repositories.stream()
                .limit(limit)
                .map(repo -> new ViewResponse<Integer>(repo.getFullName(), repo.getForks()))
                .collect(Collectors.toList());
    }

    private List<ViewResponse> buildOpenIssuesViewResponse(final List<Repository> repositories, final int limit) {
        return repositories.stream()
                .limit(limit)
                .map(repo -> new ViewResponse<Integer>(repo.getFullName(), repo.getOpenIssues()))
                .collect(Collectors.toList());
    }

    private List<ViewResponse> buildLastUpdatedViewResponse(final List<Repository> repositories, final int limit) {
        return repositories.stream()
                .limit(limit)
                .map(repo -> new ViewResponse<Instant>(repo.getFullName(), repo.getLastUpdated()))
                .collect(Collectors.toList());
    }
}
