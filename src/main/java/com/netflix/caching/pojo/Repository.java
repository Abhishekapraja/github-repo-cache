package com.netflix.caching.pojo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.Instant;

/**
 * Repository class for internal handling of subset of repository information
 */
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Repository {
    private final String fullName;
    private final int forks;
    private final int openIssues;
    private final int stars;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    private final Instant lastUpdated;

    @JsonCreator
    public Repository(@JsonProperty("full_name") String fullName,
                      @JsonProperty("stargazers_count") int starCount,
                      @JsonProperty("forks_count") int forks,
                      @JsonProperty("open_issues_count") int issues,
                      @JsonProperty("updated_at") Instant lastUpdated) {
        this.fullName = fullName;
        this.forks = forks;
        this.openIssues = issues;
        this.stars = starCount;
        this.lastUpdated = lastUpdated;
    }
}
