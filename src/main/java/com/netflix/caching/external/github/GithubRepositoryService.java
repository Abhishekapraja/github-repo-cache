package com.netflix.caching.external.github;

import com.netflix.caching.external.RepositoryService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;

@Component
@Log4j2
public class GithubRepositoryService implements RepositoryService {
    private static final String REPOSITORY_URL = "https://api.github.com/orgs/Netflix/repos";
    private static final String NETFLIX_ORG_DETAILS_URL = "https://api.github.com/orgs/Netflix";
    private static final String GITHUB_ROOT_URL = "https://api.github.com/";
    private static final String NETFLIX_MEMBERS_URL = "https://api.github.com/orgs/Netflix/members";

    @Autowired
    private GithubServiceClient client;

    @Override
    public String getRepositories() {
        try {
            log.debug("Getting repositories from Origin Server");
            return client.call(new URL(REPOSITORY_URL));
        } catch (Exception e) {
            log.error("Error in getting repositories from Origin Server");
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getMembers() {
        try {
            log.debug("Getting members from Origin Server");
            return client.call(new URL(NETFLIX_MEMBERS_URL));
        } catch (Exception e) {
            log.error("Error in getting members from Origin Server");
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getOrganizationDetails() {
        try {
            log.debug("Getting org details from Origin Server");
            return client.call(new URL(NETFLIX_ORG_DETAILS_URL));
        } catch (Exception e) {
            log.error("Error in org details from Origin Server");
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getServiceIndexPageDetails() {
        try {
            log.debug("Getting github root from Origin Server");
            return client.call(new URL(GITHUB_ROOT_URL));
        } catch (Exception e) {
            log.error("Error in getting github root from Origin Server");
            throw new RuntimeException(e);
        }
    }
}
