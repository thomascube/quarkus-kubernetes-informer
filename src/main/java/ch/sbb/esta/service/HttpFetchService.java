package ch.sbb.esta.service;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.predicate.ResponsePredicate;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class HttpFetchService {

    public static final int MAX_POOL_SIZE = 10;
    public static final int KEEP_ALIVE_TIMEOUT = 15;

    @Inject
    Vertx vertx;

    private WebClient getWebClient() {
        return WebClient.create(vertx,
                new WebClientOptions().setDefaultHost("api.chucknorris.io")
                        .setDefaultPort(443)
                        .setSsl(true)
                        .setMaxPoolSize(MAX_POOL_SIZE)
                        .setHttp2MaxPoolSize(MAX_POOL_SIZE)
                        .setKeepAliveTimeout(KEEP_ALIVE_TIMEOUT)
                        .setHttp2KeepAliveTimeout(KEEP_ALIVE_TIMEOUT)
                        .setTrustAll(true));
    }

    public Uni<String> getChoke() {
        return getWebClient().get("/jokes/random")
                .expect(ResponsePredicate.status(Response.Status.OK.getStatusCode()))
                .send()
                .map(response -> response.bodyAsJsonObject().mapTo(ChokeModel.class))
                .map(ChokeModel::value);
    }

    public record ChokeModel (String id, String icon_url, String url, String value) {
    }

}
