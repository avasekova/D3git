# d3git

### Usage

`mvn tomcat7:run`

Requires `auth.properties` with a property named `oauth_token` (= the
OAuth access token generated for GitHub API) in `src/main/resources`.

Requires a running (for now, _local_) instance of Elasticsearch. Also, you might
want to add `http.cors.enabled: true`, `https.cors.allow-credentials: true` and
`http.cors.allow-origin: "*"` to `elasticsearch.yml`.

(Works with Elasticsearch 2.1.1. Potential issues when using a different
version due to the clash with the `mvn` dependency version.)