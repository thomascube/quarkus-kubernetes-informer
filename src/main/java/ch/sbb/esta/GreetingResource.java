package ch.sbb.esta;

import ch.sbb.esta.service.KubernetesApiService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/hello")
public class GreetingResource {

    @Inject KubernetesApiService kubernetesApi;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        var cm = kubernetesApi.getConfigMapByName(KubernetesApiService.DEFAULT_CONFIGMAP);
        return "Hello from RESTEasy Reactive.\nConfigMap %s:\n%s"
                .formatted(KubernetesApiService.DEFAULT_CONFIGMAP, cm.getData().toString());
    }
}
