package ch.sbb.esta.service;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.client.informers.ResourceEventHandler;
import io.fabric8.kubernetes.client.informers.SharedIndexInformer;
import io.fabric8.kubernetes.client.informers.SharedInformerFactory;
import io.quarkus.runtime.LaunchMode;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.vertx.mutiny.core.eventbus.EventBus;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class ConfigMapEventWatcher {

    @Inject KubernetesApiService kubernetesApi;
    @Inject EventBus eventBus;

    private SharedInformerFactory sharedInformerFactory;
    private SharedIndexInformer<ConfigMap> configMapInformer;

    @PostConstruct
    public void postConstruct() {
        sharedInformerFactory = kubernetesApi.getKubernetesClient().informers();
    }

    public void setupInformerOnStart(@Observes StartupEvent event) {
        if (!LaunchMode.current().equals(LaunchMode.TEST)) {
            setupInformer(null);
        }
    }

    public void stopWatching(@Observes ShutdownEvent shutdownEvent) {
        if (configMapInformer != null) {
            log.info("Closing ConfigMap Informer caused by shutdown!");
            configMapInformer.close();
        }
        sharedInformerFactory.stopAllRegisteredInformers();
    }

    public void setupInformer(Object msg) {
        log.info("Starting ConfigMap Informer...");

        configMapInformer = kubernetesApi.getKubernetesClient().configMaps().inform(new ResourceEventHandler<>() {
            @Override
            public void onAdd(ConfigMap cm) {
                log.debug("Kubernetes Event: ConfigMap ADDED {}", cm.getMetadata().getName());
                eventBus.publish("CM_ADDED", cm);
            }

            @Override
            public void onUpdate(ConfigMap oldCm, ConfigMap newCm) {
                log.debug("Kubernetes Event: ConfigMap UPDATED {}", oldCm.getMetadata().getName());
                eventBus.publish("CM_MODIFIED", new ConfigMapModifiedEvent(oldCm, newCm));
            }

            @Override
            public void onDelete(ConfigMap cm, boolean deletedFinalStateUnknown) {
                log.debug("Kubernetes Event: ConfigMap DELETED {}", cm.getMetadata().getName());
                eventBus.publish("CM_DELETED", cm);
            }
        });

        configMapInformer.stopped().thenAccept((v) -> {
            log.info("ConfigMap Informer has stopped");
        });
    }

}
