package ch.sbb.esta;

import ch.sbb.esta.service.KubernetesApiService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/configmap")
public class ConfigMapResource {

    @Inject KubernetesApiService kubernetesApi;

    @GET
    @Path("{name}")
    @Produces(MediaType.TEXT_PLAIN)
    public String get(@PathParam("name") String name) {
        var cm = kubernetesApi.getConfigMapByName(name);
        if (cm != null) {
            return "ConfigMap %s:\n%s".formatted(name, cm.getData().toString());
        }
        throw new RuntimeException("ConfigMap with name %s could not be fetched".formatted(name));
    }
}
