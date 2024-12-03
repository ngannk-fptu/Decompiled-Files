/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.AwsConfig;
import com.hazelcast.config.AzureConfig;
import com.hazelcast.config.DiscoveryConfig;
import com.hazelcast.config.DiscoveryStrategyConfig;
import com.hazelcast.config.EurekaConfig;
import com.hazelcast.config.GcpConfig;
import com.hazelcast.config.InvalidConfigurationException;
import com.hazelcast.config.KubernetesConfig;
import com.hazelcast.config.MulticastConfig;
import com.hazelcast.config.TcpIpConfig;
import com.hazelcast.util.Preconditions;
import java.util.Collection;

public class JoinConfig {
    private MulticastConfig multicastConfig = new MulticastConfig();
    private TcpIpConfig tcpIpConfig = new TcpIpConfig();
    private AwsConfig awsConfig = new AwsConfig();
    private GcpConfig gcpConfig = new GcpConfig();
    private AzureConfig azureConfig = new AzureConfig();
    private KubernetesConfig kubernetesConfig = new KubernetesConfig();
    private EurekaConfig eurekaConfig = new EurekaConfig();
    private DiscoveryConfig discoveryConfig = new DiscoveryConfig();

    public MulticastConfig getMulticastConfig() {
        return this.multicastConfig;
    }

    public JoinConfig setMulticastConfig(MulticastConfig multicastConfig) {
        this.multicastConfig = Preconditions.isNotNull(multicastConfig, "multicastConfig");
        return this;
    }

    public TcpIpConfig getTcpIpConfig() {
        return this.tcpIpConfig;
    }

    public JoinConfig setTcpIpConfig(TcpIpConfig tcpIpConfig) {
        this.tcpIpConfig = Preconditions.isNotNull(tcpIpConfig, "tcpIpConfig");
        return this;
    }

    public AwsConfig getAwsConfig() {
        return this.awsConfig;
    }

    public JoinConfig setAwsConfig(AwsConfig awsConfig) {
        this.awsConfig = Preconditions.isNotNull(awsConfig, "awsConfig");
        return this;
    }

    public GcpConfig getGcpConfig() {
        return this.gcpConfig;
    }

    public JoinConfig setGcpConfig(GcpConfig gcpConfig) {
        this.gcpConfig = Preconditions.isNotNull(gcpConfig, "gcpConfig");
        return this;
    }

    public AzureConfig getAzureConfig() {
        return this.azureConfig;
    }

    public JoinConfig setAzureConfig(AzureConfig azureConfig) {
        this.azureConfig = Preconditions.isNotNull(azureConfig, "azureConfig");
        return this;
    }

    public KubernetesConfig getKubernetesConfig() {
        return this.kubernetesConfig;
    }

    public JoinConfig setKubernetesConfig(KubernetesConfig kubernetesConfig) {
        this.kubernetesConfig = Preconditions.isNotNull(kubernetesConfig, "kubernetesConfig");
        return this;
    }

    public EurekaConfig getEurekaConfig() {
        return this.eurekaConfig;
    }

    public JoinConfig setEurekaConfig(EurekaConfig eurekaConfig) {
        this.eurekaConfig = Preconditions.isNotNull(eurekaConfig, "eurekaConfig");
        return this;
    }

    public DiscoveryConfig getDiscoveryConfig() {
        return this.discoveryConfig;
    }

    public JoinConfig setDiscoveryConfig(DiscoveryConfig discoveryConfig) {
        this.discoveryConfig = Preconditions.isNotNull(discoveryConfig, "discoveryProvidersConfig");
        return this;
    }

    public void verify() {
        Collection<DiscoveryStrategyConfig> discoveryStrategyConfigs;
        int countEnabled = 0;
        if (this.getTcpIpConfig().isEnabled()) {
            ++countEnabled;
        }
        if (this.getMulticastConfig().isEnabled()) {
            ++countEnabled;
        }
        if (this.getAwsConfig().isEnabled()) {
            ++countEnabled;
        }
        if (this.getGcpConfig().isEnabled()) {
            ++countEnabled;
        }
        if (this.getAzureConfig().isEnabled()) {
            ++countEnabled;
        }
        if (this.getKubernetesConfig().isEnabled()) {
            ++countEnabled;
        }
        if (this.getEurekaConfig().isEnabled()) {
            ++countEnabled;
        }
        if ((countEnabled += (discoveryStrategyConfigs = this.discoveryConfig.getDiscoveryStrategyConfigs()).size()) > 1) {
            throw new InvalidConfigurationException("Multiple join configuration cannot be enabled at the same time. Enable only one of: TCP/IP, Multicast, AWS, GCP, Azure, Kubernetes, Eureka or Discovery Strategy");
        }
    }

    public String toString() {
        return "JoinConfig{multicastConfig=" + this.multicastConfig + ", tcpIpConfig=" + this.tcpIpConfig + ", awsConfig=" + this.awsConfig + ", gcpConfig=" + this.gcpConfig + ", azureConfig=" + this.azureConfig + ", kubernetesConfig=" + this.kubernetesConfig + ", eurekaConfig=" + this.eurekaConfig + ", discoveryConfig=" + this.discoveryConfig + '}';
    }
}

