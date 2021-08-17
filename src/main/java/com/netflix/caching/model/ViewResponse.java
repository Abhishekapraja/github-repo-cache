package com.netflix.caching.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;

/**
 * Views response class from Service
 */
@JsonSerialize(using = ViewResponseSerializer.class)
@Getter
public class ViewResponse<T> {
    private final String repoName;
    private final T data;

    public ViewResponse(final String repoName, final T data) {
        this.repoName = repoName;
        this.data = data;
    }
}
