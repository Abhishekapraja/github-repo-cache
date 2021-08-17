# github-repo-cache
Service to cache github repository to local servers. This service does local caching of already configured paths and proxies others to Origin server

Prerequisite
 * Need maven to build the project [Please install if you don't have](https://maven.apache.org/install.html). Choose based on your platform. 
 * Need java 8. Choose based on your platform.


Steps to build and run the service.

1. Pull the repository `git clone https://github.com/Abhishekapraja/github-repo-cache.git`
2. `cd github-repo-cache`
1. mvn package
2. Set the GITHUB_API_TOKEN env variable
3. java -jar -Dserver.port=<port> target/RepositoryCachingService-1.0-SNAPSHOT.jar
4. Now your service should be up on the above configured port.

