/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.logging.ILogger
 *  com.hazelcast.spi.discovery.AbstractDiscoveryStrategy
 *  com.hazelcast.spi.discovery.DiscoveryNode
 */
package com.hazelcast.kubernetes;

import com.hazelcast.kubernetes.DnsEndpointResolver;
import com.hazelcast.kubernetes.KubernetesApiEndpointResolver;
import com.hazelcast.kubernetes.KubernetesClient;
import com.hazelcast.kubernetes.KubernetesConfig;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.discovery.AbstractDiscoveryStrategy;
import com.hazelcast.spi.discovery.DiscoveryNode;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class HazelcastKubernetesDiscoveryStrategy
extends AbstractDiscoveryStrategy {
    private final KubernetesClient client;
    private final EndpointResolver endpointResolver;
    private KubernetesConfig config;
    private final Map<String, Object> memberMetadata = new HashMap<String, Object>();

    HazelcastKubernetesDiscoveryStrategy(ILogger logger, Map<String, Comparable> properties) {
        super(logger, properties);
        this.config = new KubernetesConfig(properties);
        logger.info(this.config.toString());
        this.client = HazelcastKubernetesDiscoveryStrategy.buildKubernetesClient(this.config);
        this.endpointResolver = KubernetesConfig.DiscoveryMode.DNS_LOOKUP.equals((Object)this.config.getMode()) ? new DnsEndpointResolver(logger, this.config.getServiceDns(), this.config.getServicePort(), this.config.getServiceDnsTimeout()) : new KubernetesApiEndpointResolver(logger, this.config.getServiceName(), this.config.getServicePort(), this.config.getServiceLabelName(), this.config.getServiceLabelValue(), this.config.getPodLabelName(), this.config.getPodLabelValue(), this.config.isResolveNotReadyAddresses(), this.client);
        logger.info("Kubernetes Discovery activated with mode: " + this.config.getMode().name());
    }

    private static KubernetesClient buildKubernetesClient(KubernetesConfig config) {
        return new KubernetesClient(config.getNamespace(), config.getKubernetesMasterUrl(), config.getKubernetesApiToken(), config.getKubernetesCaCertificate(), config.getKubernetesApiRetries(), config.isUseNodeNameAsExternalAddress());
    }

    public void start() {
        this.endpointResolver.start();
    }

    public Map<String, Object> discoverLocalMetadata() {
        if (this.memberMetadata.isEmpty()) {
            this.memberMetadata.put("hazelcast.partition.group.zone", this.discoverZone());
            this.memberMetadata.put("hazelcast.partition.group.node", this.discoverNodeName());
        }
        return this.memberMetadata;
    }

    private String discoverZone() {
        if (KubernetesConfig.DiscoveryMode.KUBERNETES_API.equals((Object)this.config.getMode())) {
            try {
                String zone = this.client.zone(this.podName());
                if (zone != null) {
                    this.getLogger().info(String.format("Kubernetes plugin discovered availability zone: %s", zone));
                    return zone;
                }
            }
            catch (Exception e) {
                this.getLogger().finest((Throwable)e);
            }
            this.getLogger().warning("Cannot fetch the current zone, ZONE_AWARE feature is disabled");
        }
        return "unknown";
    }

    private String discoverNodeName() {
        if (KubernetesConfig.DiscoveryMode.KUBERNETES_API.equals((Object)this.config.getMode())) {
            try {
                String nodeName = this.client.nodeName(this.podName());
                if (nodeName != null) {
                    this.getLogger().info(String.format("Kubernetes plugin discovered node name: %s", nodeName));
                    return nodeName;
                }
            }
            catch (Exception e) {
                this.getLogger().finest((Throwable)e);
            }
            this.getLogger().warning("Cannot fetch name of the node, NODE_AWARE feature is disabled");
        }
        return "unknown";
    }

    private String podName() throws UnknownHostException {
        String podName = System.getenv("POD_NAME");
        if (podName == null) {
            podName = System.getenv("HOSTNAME");
        }
        if (podName == null) {
            podName = InetAddress.getLocalHost().getHostName();
        }
        return podName;
    }

    public Iterable<DiscoveryNode> discoverNodes() {
        return this.endpointResolver.resolve();
    }

    public void destroy() {
        this.endpointResolver.destroy();
    }

    static abstract class EndpointResolver {
        protected final ILogger logger;

        EndpointResolver(ILogger logger) {
            this.logger = logger;
        }

        abstract List<DiscoveryNode> resolve();

        void start() {
        }

        void destroy() {
        }

        protected InetAddress mapAddress(String address) {
            if (address == null) {
                return null;
            }
            try {
                return InetAddress.getByName(address);
            }
            catch (UnknownHostException e) {
                this.logger.warning("Address '" + address + "' could not be resolved");
                return null;
            }
        }
    }
}

