package ch.sbb.esta.service;

import io.fabric8.kubernetes.api.model.ConfigMap;

public record ConfigMapModifiedEvent(ConfigMap oldCm, ConfigMap newCm) {

}
