/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.config.InvalidConfigurationException
 *  com.hazelcast.config.properties.PropertyDefinition
 *  com.hazelcast.nio.IOUtil
 *  com.hazelcast.util.StringUtil
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.kubernetes;

import com.hazelcast.config.InvalidConfigurationException;
import com.hazelcast.config.properties.PropertyDefinition;
import com.hazelcast.kubernetes.KubernetesProperties;
import com.hazelcast.nio.IOUtil;
import com.hazelcast.util.StringUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

final class KubernetesConfig {
    private static final String DEFAULT_MASTER_URL = "https://kubernetes.default.svc";
    private static final int DEFAULT_SERVICE_DNS_TIMEOUT_SECONDS = 5;
    private static final int DEFAULT_KUBERNETES_API_RETRIES = 3;
    private final String serviceDns;
    private final int serviceDnsTimeout;
    private final String serviceName;
    private final String serviceLabelName;
    private final String serviceLabelValue;
    private final String namespace;
    private final String podLabelName;
    private final String podLabelValue;
    private final boolean resolveNotReadyAddresses;
    private final boolean useNodeNameAsExternalAddress;
    private final int kubernetesApiRetries;
    private final String kubernetesMasterUrl;
    private final String kubernetesApiToken;
    private final String kubernetesCaCertificate;
    private final int servicePort;

    KubernetesConfig(Map<String, Comparable> properties) {
        this.serviceDns = (String)this.getOrNull(properties, "hazelcast.kubernetes.", KubernetesProperties.SERVICE_DNS);
        this.serviceDnsTimeout = this.getOrDefault(properties, "hazelcast.kubernetes.", KubernetesProperties.SERVICE_DNS_TIMEOUT, 5);
        this.serviceName = (String)this.getOrNull(properties, "hazelcast.kubernetes.", KubernetesProperties.SERVICE_NAME);
        this.serviceLabelName = (String)this.getOrNull(properties, "hazelcast.kubernetes.", KubernetesProperties.SERVICE_LABEL_NAME);
        this.serviceLabelValue = this.getOrDefault(properties, "hazelcast.kubernetes.", KubernetesProperties.SERVICE_LABEL_VALUE, "true");
        this.namespace = this.getOrDefault(properties, "hazelcast.kubernetes.", KubernetesProperties.NAMESPACE, this.getNamespaceOrDefault());
        this.podLabelName = (String)this.getOrNull(properties, "hazelcast.kubernetes.", KubernetesProperties.POD_LABEL_NAME);
        this.podLabelValue = (String)this.getOrNull(properties, "hazelcast.kubernetes.", KubernetesProperties.POD_LABEL_VALUE);
        this.resolveNotReadyAddresses = this.getOrDefault(properties, "hazelcast.kubernetes.", KubernetesProperties.RESOLVE_NOT_READY_ADDRESSES, false);
        this.useNodeNameAsExternalAddress = this.getOrDefault(properties, "hazelcast.kubernetes.", KubernetesProperties.USE_NODE_NAME_AS_EXTERNAL_ADDRESS, false);
        this.kubernetesApiRetries = this.getOrDefault(properties, "hazelcast.kubernetes.", KubernetesProperties.KUBERNETES_API_RETIRES, 3);
        this.kubernetesMasterUrl = this.getOrDefault(properties, "hazelcast.kubernetes.", KubernetesProperties.KUBERNETES_MASTER_URL, DEFAULT_MASTER_URL);
        this.kubernetesApiToken = this.getApiToken(properties);
        this.kubernetesCaCertificate = this.caCertificate(properties);
        this.servicePort = this.getOrDefault(properties, "hazelcast.kubernetes.", KubernetesProperties.SERVICE_PORT, 0);
        this.validateConfig();
    }

    private String getNamespaceOrDefault() {
        String namespace = System.getenv("KUBERNETES_NAMESPACE");
        if (namespace == null && (namespace = System.getenv("OPENSHIFT_BUILD_NAMESPACE")) == null) {
            namespace = "default";
        }
        return namespace;
    }

    private String getApiToken(Map<String, Comparable> properties) {
        String apiToken = (String)this.getOrNull(properties, "hazelcast.kubernetes.", KubernetesProperties.KUBERNETES_API_TOKEN);
        if (apiToken == null) {
            apiToken = KubernetesConfig.readAccountToken();
        }
        return apiToken;
    }

    private String caCertificate(Map<String, Comparable> properties) {
        String caCertificate = (String)this.getOrNull(properties, "hazelcast.kubernetes.", KubernetesProperties.KUBERNETES_CA_CERTIFICATE);
        if (caCertificate == null) {
            caCertificate = KubernetesConfig.readCaCertificate();
        }
        return caCertificate;
    }

    @SuppressFBWarnings(value={"DMI_HARDCODED_ABSOLUTE_FILENAME"})
    private static String readAccountToken() {
        return KubernetesConfig.readFileContents("/var/run/secrets/kubernetes.io/serviceaccount/token");
    }

    @SuppressFBWarnings(value={"DMI_HARDCODED_ABSOLUTE_FILENAME"})
    private static String readCaCertificate() {
        return KubernetesConfig.readFileContents("/var/run/secrets/kubernetes.io/serviceaccount/ca.crt");
    }

    static String readFileContents(String tokenFile) {
        String string;
        FileInputStream is = null;
        try {
            File file = new File(tokenFile);
            byte[] data = new byte[(int)file.length()];
            is = new FileInputStream(file);
            ((InputStream)is).read(data);
            string = new String(data, "UTF-8");
        }
        catch (IOException e) {
            try {
                throw new RuntimeException("Could not get token file", e);
            }
            catch (Throwable throwable) {
                IOUtil.closeResource(is);
                throw throwable;
            }
        }
        IOUtil.closeResource((Closeable)is);
        return string;
    }

    private <T extends Comparable> T getOrNull(Map<String, Comparable> properties, String prefix, PropertyDefinition property) {
        return this.getOrDefault(properties, prefix, property, null);
    }

    private <T extends Comparable> T getOrDefault(Map<String, Comparable> properties, String prefix, PropertyDefinition property, T defaultValue) {
        if (property == null) {
            return defaultValue;
        }
        Comparable value = this.readProperty(prefix, property);
        if (value == null) {
            value = properties.get(property.key());
        }
        if (value == null) {
            return defaultValue;
        }
        return (T)value;
    }

    private Comparable readProperty(String prefix, PropertyDefinition property) {
        if (prefix != null) {
            String p = this.getProperty(prefix, property);
            String v = System.getProperty(p);
            if (StringUtil.isNullOrEmpty((String)v) && StringUtil.isNullOrEmpty((String)(v = System.getenv(p)))) {
                v = System.getenv(this.cIdentifierLike(p));
            }
            if (!StringUtil.isNullOrEmpty((String)v)) {
                return property.typeConverter().convert((Comparable)((Object)v));
            }
        }
        return null;
    }

    private String cIdentifierLike(String property) {
        property = property.toUpperCase();
        property = property.replace(".", "_");
        return property.replace("-", "_");
    }

    private String getProperty(String prefix, PropertyDefinition property) {
        StringBuilder sb = new StringBuilder(prefix);
        if (prefix.charAt(prefix.length() - 1) != '.') {
            sb.append('.');
        }
        return sb.append(property.key()).toString();
    }

    private void validateConfig() {
        if (!(StringUtil.isNullOrEmptyAfterTrim((String)this.serviceDns) || StringUtil.isNullOrEmptyAfterTrim((String)this.serviceName) && StringUtil.isNullOrEmptyAfterTrim((String)this.serviceLabelName) && StringUtil.isNullOrEmptyAfterTrim((String)this.podLabelName))) {
            throw new InvalidConfigurationException(String.format("Properties '%s' and ('%s' or '%s' or %s) cannot be defined at the same time", KubernetesProperties.SERVICE_DNS.key(), KubernetesProperties.SERVICE_NAME.key(), KubernetesProperties.SERVICE_LABEL_NAME.key(), KubernetesProperties.POD_LABEL_NAME.key()));
        }
        if (!StringUtil.isNullOrEmptyAfterTrim((String)this.serviceName) && !StringUtil.isNullOrEmptyAfterTrim((String)this.serviceLabelName)) {
            throw new InvalidConfigurationException(String.format("Properties '%s' and '%s' cannot be defined at the same time", KubernetesProperties.SERVICE_NAME.key(), KubernetesProperties.SERVICE_LABEL_NAME.key()));
        }
        if (!StringUtil.isNullOrEmptyAfterTrim((String)this.serviceName) && !StringUtil.isNullOrEmptyAfterTrim((String)this.podLabelName)) {
            throw new InvalidConfigurationException(String.format("Properties '%s' and '%s' cannot be defined at the same time", KubernetesProperties.SERVICE_NAME.key(), KubernetesProperties.POD_LABEL_NAME.key()));
        }
        if (!StringUtil.isNullOrEmptyAfterTrim((String)this.serviceLabelName) && !StringUtil.isNullOrEmptyAfterTrim((String)this.podLabelName)) {
            throw new InvalidConfigurationException(String.format("Properties '%s' and '%s' cannot be defined at the same time", KubernetesProperties.SERVICE_LABEL_NAME.key(), KubernetesProperties.POD_LABEL_NAME.key()));
        }
        if (this.serviceDnsTimeout < 0) {
            throw new InvalidConfigurationException(String.format("Property '%s' cannot be a negative number", KubernetesProperties.SERVICE_DNS_TIMEOUT.key()));
        }
        if (this.kubernetesApiRetries < 0) {
            throw new InvalidConfigurationException(String.format("Property '%s' cannot be a negative number", KubernetesProperties.KUBERNETES_API_RETIRES.key()));
        }
        if (this.servicePort < 0) {
            throw new InvalidConfigurationException(String.format("Property '%s' cannot be a negative number", KubernetesProperties.SERVICE_PORT.key()));
        }
    }

    DiscoveryMode getMode() {
        if (!StringUtil.isNullOrEmptyAfterTrim((String)this.serviceDns)) {
            return DiscoveryMode.DNS_LOOKUP;
        }
        return DiscoveryMode.KUBERNETES_API;
    }

    String getServiceDns() {
        return this.serviceDns;
    }

    int getServiceDnsTimeout() {
        return this.serviceDnsTimeout;
    }

    String getServiceName() {
        return this.serviceName;
    }

    String getServiceLabelName() {
        return this.serviceLabelName;
    }

    String getServiceLabelValue() {
        return this.serviceLabelValue;
    }

    String getNamespace() {
        return this.namespace;
    }

    public String getPodLabelName() {
        return this.podLabelName;
    }

    public String getPodLabelValue() {
        return this.podLabelValue;
    }

    boolean isResolveNotReadyAddresses() {
        return this.resolveNotReadyAddresses;
    }

    boolean isUseNodeNameAsExternalAddress() {
        return this.useNodeNameAsExternalAddress;
    }

    int getKubernetesApiRetries() {
        return this.kubernetesApiRetries;
    }

    String getKubernetesMasterUrl() {
        return this.kubernetesMasterUrl;
    }

    String getKubernetesApiToken() {
        return this.kubernetesApiToken;
    }

    String getKubernetesCaCertificate() {
        return this.kubernetesCaCertificate;
    }

    int getServicePort() {
        return this.servicePort;
    }

    public String toString() {
        return "Kubernetes Discovery properties: { service-dns: " + this.serviceDns + ", service-dns-timeout: " + this.serviceDnsTimeout + ", service-name: " + this.serviceName + ", service-port: " + this.servicePort + ", service-label: " + this.serviceLabelName + ", service-label-value: " + this.serviceLabelValue + ", namespace: " + this.namespace + ", pod-label: " + this.podLabelName + ", pod-label-value: " + this.podLabelValue + ", resolve-not-ready-addresses: " + this.resolveNotReadyAddresses + ", use-node-name-as-external-address: " + this.useNodeNameAsExternalAddress + ", kubernetes-api-retries: " + this.kubernetesApiRetries + ", kubernetes-master: " + this.kubernetesMasterUrl + "}";
    }

    static enum DiscoveryMode {
        DNS_LOOKUP,
        KUBERNETES_API;

    }
}

