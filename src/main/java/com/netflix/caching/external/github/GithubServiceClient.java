package com.netflix.caching.external.github;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

@Log4j2
@Component
public class GithubServiceClient {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String GITHUB_API_TOKEN_KEY = "GITHUB_API_TOKEN";

    /**
     * This method calls the given URL and all linked pages(all paginated response is collapsed into single response)
     *
     * @param url : Origin server
     * @return Serialized response from the server
     */
    public String call(URL url) {
        boolean hasNextPage = true;
        int totalPageCount = 0;

        ArrayList<JsonNode> result = new ArrayList<>();

        try {
            while (hasNextPage) {
                final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("Authorization", "token " + getAuthToken());

                if (conn.getResponseCode() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
                }

                InputStream inputStream = conn.getInputStream();
                Map<String, List<String>> headers = conn.getHeaderFields();

                JsonNode jsonNode = OBJECT_MAPPER.readTree(inputStream);
                if (jsonNode.isArray()) {
                    // If the response is in array
                    result.addAll(flatResponse(jsonNode));
                } else {
                    // Handle non-array response
                    result.add(jsonNode);
                }

                Optional<String> nextPageUrl = getNextPageUrl(headers);
                if (!nextPageUrl.isPresent()) {
                    hasNextPage = false;
                } else {
                    url = new URL(nextPageUrl.get());
                    totalPageCount++;
                }

                conn.disconnect();
            }

            log.debug("Total page count is : {}", totalPageCount);
            if (result.size() == 1) {
                // Handle single element, don't serialize as an array
                return OBJECT_MAPPER.writeValueAsString(result.get(0));
            }

            return OBJECT_MAPPER.writeValueAsString(result);
        } catch (Exception e) {
            // We should add some exponential backoff retry strategy to handle transient failures.
            log.error("Error is calling the url {}", url, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Finds the next page URL from the give http response header. This method retuns Optional based on if next page
     * url is found or not.
     * <p>
     * Example header
     * Link: <https://api.github.com/search/code?q=addClass+user%3Amozilla&page=15>; rel="next",
     * <https://api.github.com/search/code?q=addClass+user%3Amozilla&page=34>; rel="last",
     * <https://api.github.com/search/code?q=addClass+user%3Amozilla&page=1>; rel="first",
     * <https://api.github.com/search/code?q=addClass+user%3Amozilla&page=13>; rel="prev"
     *
     * @param headers : Http response header
     * @return url : Optional<String>
     */
    private Optional<String> getNextPageUrl(final Map<String, List<String>> headers) {
        if (!headers.containsKey("Link") || headers.get("Link").size() == 0) {
            return Optional.empty();
        }

        String header = headers.get("Link").get(0);

        String[] parts = header.split(",");
        for (String part : parts) {
            if (part.indexOf("next") >= 0) {
                // This has next page extract the link.
                int startIndex = part.indexOf("<");
                int endIndex = part.indexOf(">");
                String url = part.substring(startIndex + 1, endIndex);
                log.debug("Next page URL is : {}", url);
                return Optional.of(url);
            }
        }

        log.debug("No next page found for given header : {}", headers);
        return Optional.empty();
    }

    private List<JsonNode> flatResponse(final JsonNode jsonNode) {
        final List<JsonNode> nodes = new ArrayList<>();

        if (jsonNode.isArray()) {
            Iterator<JsonNode> iterator = jsonNode.elements();
            while (iterator.hasNext()) {
                nodes.add(iterator.next());
            }
        }
        return nodes;
    }

    private String getAuthToken() {
        final String token = System.getenv(GITHUB_API_TOKEN_KEY);
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("GITHUB_API_TOKEN is not valid : token : {}" + token + "Please set the " +
                    "token");
        }
        return token;
    }
}
