# d3git

### Usage

`mvn tomcat7:run`

Requires `auth.properties` with a property named `oauth_token` (= the
OAuth access token generated for GitHub API) in `src/main/resources`.

Requires a running (for now, _local_) instance of Elasticsearch. (Works with 2.1.1.
Potential issues when using a different version due to the clash with the mvn
dependency version.)