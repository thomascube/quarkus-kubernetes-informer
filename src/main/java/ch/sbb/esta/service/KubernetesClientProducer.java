package ch.sbb.esta.service;

import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Singleton
public class KubernetesClientProducer {

    @ConfigProperty(name = "quarkus.kubernetes-client.namespace")
    String namespace;

    @Produces
    public KubernetesClient kubernetesClient() {
        final var configBuilder = new ConfigBuilder().withNamespace(namespace);
        return new KubernetesClientBuilder()
                .withConfig(configBuilder.build())
                .build();
    }

}
