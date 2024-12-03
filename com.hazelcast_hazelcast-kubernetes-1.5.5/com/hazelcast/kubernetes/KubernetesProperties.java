/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.config.properties.PropertyDefinition
 *  com.hazelcast.config.properties.PropertyTypeConverter
 *  com.hazelcast.config.properties.SimplePropertyDefinition
 *  com.hazelcast.core.TypeConverter
 */
package com.hazelcast.kubernetes;

import com.hazelcast.config.properties.PropertyDefinition;
import com.hazelcast.config.properties.PropertyTypeConverter;
import com.hazelcast.config.properties.SimplePropertyDefinition;
import com.hazelcast.core.TypeConverter;

public final class KubernetesProperties {
    public static final String KUBERNETES_SYSTEM_PREFIX = "hazelcast.kubernetes.";
    public static final PropertyDefinition SERVICE_DNS = KubernetesProperties.property("service-dns", (TypeConverter)PropertyTypeConverter.STRING);
    public static final PropertyDefinition SERVICE_DNS_TIMEOUT = KubernetesProperties.property("service-dns-timeout", (TypeConverter)PropertyTypeConverter.INTEGER);
    public static final PropertyDefinition SERVICE_NAME = KubernetesProperties.property("service-name", (TypeConverter)PropertyTypeConverter.STRING);
    public static final PropertyDefinition SERVICE_LABEL_NAME = KubernetesProperties.property("service-label-name", (TypeConverter)PropertyTypeConverter.STRING);
    public static final PropertyDefinition SERVICE_LABEL_VALUE = KubernetesProperties.property("service-label-value", (TypeConverter)PropertyTypeConverter.STRING);
    public static final PropertyDefinition NAMESPACE = KubernetesProperties.property("namespace", (TypeConverter)PropertyTypeConverter.STRING);
    public static final PropertyDefinition POD_LABEL_NAME = KubernetesProperties.property("pod-label-name", (TypeConverter)PropertyTypeConverter.STRING);
    public static final PropertyDefinition POD_LABEL_VALUE = KubernetesProperties.property("pod-label-value", (TypeConverter)PropertyTypeConverter.STRING);
    public static final PropertyDefinition RESOLVE_NOT_READY_ADDRESSES = KubernetesProperties.property("resolve-not-ready-addresses", (TypeConverter)PropertyTypeConverter.BOOLEAN);
    public static final PropertyDefinition USE_NODE_NAME_AS_EXTERNAL_ADDRESS = KubernetesProperties.property("use-node-name-as-external-address", (TypeConverter)PropertyTypeConverter.BOOLEAN);
    public static final PropertyDefinition KUBERNETES_API_RETIRES = KubernetesProperties.property("kubernetes-api-retries", (TypeConverter)PropertyTypeConverter.INTEGER);
    public static final PropertyDefinition KUBERNETES_MASTER_URL = KubernetesProperties.property("kubernetes-master", (TypeConverter)PropertyTypeConverter.STRING);
    public static final PropertyDefinition KUBERNETES_API_TOKEN = KubernetesProperties.property("api-token", (TypeConverter)PropertyTypeConverter.STRING);
    public static final PropertyDefinition KUBERNETES_CA_CERTIFICATE = KubernetesProperties.property("ca-certificate", (TypeConverter)PropertyTypeConverter.STRING);
    public static final PropertyDefinition SERVICE_PORT = KubernetesProperties.property("service-port", (TypeConverter)PropertyTypeConverter.INTEGER);

    private KubernetesProperties() {
    }

    private static PropertyDefinition property(String key, TypeConverter typeConverter) {
        return new SimplePropertyDefinition(key, true, typeConverter);
    }
}

