# github-repo-cache
Service to cache github repository to local servers. This service does local caching of already configured paths and proxies others to Origin server

Prerequisite
 * Need maven to build the project [Please install if you don't have]
 * Need java 8 to run the service [Please install if you don't have]


Steps to build and run the service.

1. mvn package
2. Set the GITHUB_API_TOKEN env variable
3. java -jar -Dserver.port=<port> target/RepositoryCachingService-1.0-SNAPSHOT.jar

