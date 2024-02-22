package ch.sbb.esta.service;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.quarkus.vertx.ConsumeEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class ConfigMapListener {

    @Inject KubernetesApiService kubernetesApi;

    @ConsumeEvent("CM_ADDED")
    public void configMapAdded(ConfigMap cm) {
        log.info("New ConfigMap received: {}", cm.getMetadata().getName());
    }

    @ConsumeEvent(value = "CM_MODIFIED", blocking = true)
    public void configMapModified(ConfigMapModifiedEvent event) {
        log.info("ConfigMap changed: {}", event.newCm().getMetadata().getName());
        try {
            // fetch an arbitrary resource from the Kubernetes API
            // this call will block the thread and eventually fail with a timeout exception
            var cm = kubernetesApi.getConfigMapByName(event.newCm().getMetadata().getName());
            log.info("Fetched ConfigMap {}", cm.getMetadata().getName());
        } catch (Throwable e) {
            log.error("Failed fetching ConfigMap", e);
        }
    }
}
