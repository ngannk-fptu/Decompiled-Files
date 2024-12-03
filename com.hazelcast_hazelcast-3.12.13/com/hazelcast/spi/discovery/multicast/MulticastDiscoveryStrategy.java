/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.discovery.multicast;

import com.hazelcast.config.properties.ValidationException;
import com.hazelcast.config.properties.ValueValidator;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.discovery.AbstractDiscoveryStrategy;
import com.hazelcast.spi.discovery.DiscoveryNode;
import com.hazelcast.spi.discovery.SimpleDiscoveryNode;
import com.hazelcast.spi.discovery.multicast.MulticastProperties;
import com.hazelcast.spi.discovery.multicast.impl.MulticastDiscoveryReceiver;
import com.hazelcast.spi.discovery.multicast.impl.MulticastDiscoverySender;
import com.hazelcast.spi.discovery.multicast.impl.MulticastMemberInfo;
import com.hazelcast.spi.partitiongroup.PartitionGroupStrategy;
import com.hazelcast.util.ExceptionUtil;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MulticastDiscoveryStrategy
extends AbstractDiscoveryStrategy {
    private static final int DATA_OUTPUT_BUFFER_SIZE = 65536;
    private static final int DEFAULT_MULTICAST_PORT = 54327;
    private static final int SOCKET_TIME_TO_LIVE = 255;
    private static final int SOCKET_TIMEOUT = 3000;
    private static final String DEFAULT_MULTICAST_GROUP = "224.2.2.3";
    private DiscoveryNode discoveryNode;
    private MulticastSocket multicastSocket;
    private Thread thread;
    private MulticastDiscoveryReceiver multicastDiscoveryReceiver;
    private MulticastDiscoverySender multicastDiscoverySender;
    private ILogger logger;
    private boolean isClient;

    public MulticastDiscoveryStrategy(DiscoveryNode discoveryNode, ILogger logger, Map<String, Comparable> properties) {
        super(logger, properties);
        this.discoveryNode = discoveryNode;
        this.logger = logger;
    }

    private void initializeMulticastSocket() {
        try {
            InetAddress inetAddress;
            int port = this.getOrDefault(MulticastProperties.PORT, 54327);
            PortValueValidator validator = new PortValueValidator();
            validator.validate(port);
            String group = this.getOrDefault(MulticastProperties.GROUP, DEFAULT_MULTICAST_GROUP);
            this.multicastSocket = new MulticastSocket(null);
            this.multicastSocket.bind(new InetSocketAddress(port));
            if (this.discoveryNode != null && !(inetAddress = this.discoveryNode.getPrivateAddress().getInetAddress()).isLoopbackAddress()) {
                this.multicastSocket.setInterface(inetAddress);
            }
            this.multicastSocket.setReuseAddress(true);
            this.multicastSocket.setTimeToLive(255);
            this.multicastSocket.setReceiveBufferSize(65536);
            this.multicastSocket.setSendBufferSize(65536);
            this.multicastSocket.setSoTimeout(3000);
            this.multicastSocket.joinGroup(InetAddress.getByName(group));
            this.multicastDiscoverySender = new MulticastDiscoverySender(this.discoveryNode, this.multicastSocket, this.logger, group, port);
            this.multicastDiscoveryReceiver = new MulticastDiscoveryReceiver(this.multicastSocket, this.logger);
            if (this.discoveryNode == null) {
                this.isClient = true;
            }
        }
        catch (Exception e) {
            this.logger.finest(e.getMessage());
            ExceptionUtil.rethrow(e);
        }
    }

    @Override
    public void start() {
        this.initializeMulticastSocket();
        if (!this.isClient) {
            this.thread = new Thread(this.multicastDiscoverySender);
            this.thread.start();
        }
    }

    @Override
    public Iterable<DiscoveryNode> discoverNodes() {
        MulticastMemberInfo multicastMemberInfo = this.multicastDiscoveryReceiver.receive();
        if (multicastMemberInfo == null) {
            return null;
        }
        ArrayList<DiscoveryNode> arrayList = new ArrayList<DiscoveryNode>();
        try {
            SimpleDiscoveryNode discoveryNode = new SimpleDiscoveryNode(new Address(multicastMemberInfo.getHost(), multicastMemberInfo.getPort()));
            arrayList.add(discoveryNode);
        }
        catch (UnknownHostException e) {
            this.logger.finest(e.getMessage());
        }
        return arrayList;
    }

    @Override
    public void destroy() {
        this.multicastDiscoverySender.stop();
        if (this.thread != null) {
            this.thread.interrupt();
        }
    }

    @Override
    public PartitionGroupStrategy getPartitionGroupStrategy() {
        return null;
    }

    @Override
    public Map<String, Object> discoverLocalMetadata() {
        return new HashMap<String, Object>();
    }

    private static class PortValueValidator
    implements ValueValidator<Integer> {
        private static final int MIN_PORT = 0;
        private static final int MAX_PORT = 65535;

        private PortValueValidator() {
        }

        @Override
        public void validate(Integer value) throws ValidationException {
            if (value < 0) {
                throw new ValidationException("hz-port number must be greater 0");
            }
            if (value > 65535) {
                throw new ValidationException("hz-port number must be less or equal to 65535");
            }
        }
    }
}

