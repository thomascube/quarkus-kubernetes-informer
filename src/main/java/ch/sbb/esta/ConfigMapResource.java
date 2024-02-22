package ch.sbb.esta;

import ch.sbb.esta.service.HttpFetchService;
import ch.sbb.esta.service.KubernetesApiService;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path("/configmap")
public class ConfigMapResource {

    @Inject KubernetesApiService kubernetesApi;
    @Inject HttpFetchService httpFetchService;

    @GET
    @Path("{name}")
    @Produces(MediaType.TEXT_PLAIN)
    public String get(@PathParam("name") String name) {
        // simple synchronous fetch with KubernetesClient
        return getConfigMap(name);
    }

    @GET
    @Path("chained/{name}")
    @Produces(MediaType.TEXT_PLAIN)
    public Uni<String> getChained(@PathParam("name") String name) {
        // chained, asynchronous fetch with Vert.x Webclient and KubernetesClient
        // this is expected to fail due to https://github.com/quarkusio/quarkus/issues/38133
        return httpFetchService.getChoke()
                .invoke(choke -> log.info("Got random choke: {}", choke))
                .map(Unchecked.function(choke -> {
                    String configMap = getConfigMap(name);
                    return "Choke: %s\n---\n%s".formatted(choke, configMap);
                }));
    }

    @GET
    @Path("sync/{name}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getSync(@PathParam("name") String name) {
        // synchronous fetch with Vert.x Webclient and KubernetesClient
        String choke = httpFetchService.getChoke().await().atMost(Duration.ofSeconds(10));
        String configMap = getConfigMap(name);
        return "Choke: %s\n---\n%s".formatted(choke, configMap);
    }

    private String getConfigMap(String name) {
        var cm = kubernetesApi.getConfigMapByName(name);
        if (cm != null) {
            return "ConfigMap %s:\n%s".formatted(name, cm.getData().toString());
        }
        throw new RuntimeException("ConfigMap with name %s could not be fetched".formatted(name));
    }
}
