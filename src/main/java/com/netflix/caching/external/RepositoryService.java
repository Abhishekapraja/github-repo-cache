package com.netflix.caching.external;

/**
 * Interface for external repository service
 */
public interface RepositoryService {

    /**
     * Method returns the serialized repositories
     *
     * @return
     */
    String getRepositories();

    /**
     * Method returns the serialized org members
     *
     * @return
     */
    String getMembers();

    /**
     * Method returns the serialized org details
     *
     * @return
     */
    String getOrganizationDetails();

    /**
     * Method returns the serialized data for index page
     *
     * @return
     */
    String getServiceIndexPageDetails();
}
