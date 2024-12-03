/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.logging.ILogger
 *  com.hazelcast.nio.Address
 *  com.hazelcast.spi.discovery.DiscoveryNode
 *  com.hazelcast.spi.discovery.SimpleDiscoveryNode
 */
package com.hazelcast.kubernetes;

import com.hazelcast.kubernetes.HazelcastKubernetesDiscoveryStrategy;
import com.hazelcast.kubernetes.KubernetesClient;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.discovery.DiscoveryNode;
import com.hazelcast.spi.discovery.SimpleDiscoveryNode;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

class KubernetesApiEndpointResolver
extends HazelcastKubernetesDiscoveryStrategy.EndpointResolver {
    private final String serviceName;
    private final String serviceLabel;
    private final String serviceLabelValue;
    private final String podLabel;
    private final String podLabelValue;
    private final Boolean resolveNotReadyAddresses;
    private final int port;
    private final KubernetesClient client;

    KubernetesApiEndpointResolver(ILogger logger, String serviceName, int port, String serviceLabel, String serviceLabelValue, String podLabel, String podLabelValue, Boolean resolveNotReadyAddresses, KubernetesClient client) {
        super(logger);
        this.serviceName = serviceName;
        this.port = port;
        this.serviceLabel = serviceLabel;
        this.serviceLabelValue = serviceLabelValue;
        this.podLabel = podLabel;
        this.podLabelValue = podLabelValue;
        this.resolveNotReadyAddresses = resolveNotReadyAddresses;
        this.client = client;
    }

    @Override
    List<DiscoveryNode> resolve() {
        if (this.serviceName != null && !this.serviceName.isEmpty()) {
            this.logger.fine("Using service name to discover nodes.");
            return this.getSimpleDiscoveryNodes(this.client.endpointsByName(this.serviceName));
        }
        if (this.serviceLabel != null && !this.serviceLabel.isEmpty()) {
            this.logger.fine("Using service label to discover nodes.");
            return this.getSimpleDiscoveryNodes(this.client.endpointsByServiceLabel(this.serviceLabel, this.serviceLabelValue));
        }
        if (this.podLabel != null && !this.podLabel.isEmpty()) {
            this.logger.fine("Using pod label to discover nodes.");
            return this.getSimpleDiscoveryNodes(this.client.endpointsByPodLabel(this.podLabel, this.podLabelValue));
        }
        return this.getSimpleDiscoveryNodes(this.client.endpoints());
    }

    private List<DiscoveryNode> getSimpleDiscoveryNodes(List<KubernetesClient.Endpoint> endpoints) {
        ArrayList<DiscoveryNode> discoveredNodes = new ArrayList<DiscoveryNode>();
        for (KubernetesClient.Endpoint address : endpoints) {
            this.addAddress(discoveredNodes, address);
        }
        return discoveredNodes;
    }

    private void addAddress(List<DiscoveryNode> discoveredNodes, KubernetesClient.Endpoint endpoint) {
        if (Boolean.TRUE.equals(this.resolveNotReadyAddresses) || endpoint.isReady()) {
            Address privateAddress = this.createAddress(endpoint.getPrivateAddress());
            Address publicAddress = this.createAddress(endpoint.getPublicAddress());
            discoveredNodes.add((DiscoveryNode)new SimpleDiscoveryNode(privateAddress, publicAddress, endpoint.getAdditionalProperties()));
            if (this.logger.isFinestEnabled()) {
                this.logger.finest(String.format("Found node service with addresses (private, public): %s, %s ", privateAddress, publicAddress));
            }
        }
    }

    private Address createAddress(KubernetesClient.EndpointAddress address) {
        if (address == null) {
            return null;
        }
        String ip = address.getIp();
        InetAddress inetAddress = this.mapAddress(ip);
        int port = this.port(address);
        return new Address(inetAddress, port);
    }

    private int port(KubernetesClient.EndpointAddress address) {
        if (this.port > 0) {
            return this.port;
        }
        if (address.getPort() != null) {
            return address.getPort();
        }
        return 5701;
    }
}

