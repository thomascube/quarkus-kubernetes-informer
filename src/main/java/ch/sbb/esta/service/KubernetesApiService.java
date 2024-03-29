package ch.sbb.esta.service;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.client.KubernetesClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class KubernetesApiService {

    @Inject KubernetesClient kubernetesClient;

    public ConfigMap getConfigMapByName(String name) {
        return kubernetesClient.configMaps().withName(name).get();
    }

    public KubernetesClient getKubernetesClient() {
        return kubernetesClient;
    }

}
