/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster.impl;

import com.hazelcast.config.Config;
import com.hazelcast.config.ConfigAccessor;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.MulticastConfig;
import com.hazelcast.instance.Node;
import com.hazelcast.instance.OutOfMemoryErrorDispatcher;
import com.hazelcast.internal.cluster.impl.JoinMessage;
import com.hazelcast.internal.cluster.impl.JoinMessageTrustChecker;
import com.hazelcast.internal.cluster.impl.MulticastListener;
import com.hazelcast.internal.cluster.impl.NodeMulticastListener;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.BufferObjectDataInput;
import com.hazelcast.nio.BufferObjectDataOutput;
import com.hazelcast.nio.NodeIOService;
import com.hazelcast.nio.serialization.HazelcastSerializationException;
import com.hazelcast.util.ByteArrayProcessor;
import com.hazelcast.util.EmptyStatement;
import java.io.EOFException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public final class MulticastService
implements Runnable {
    private static final int SEND_OUTPUT_SIZE = 1024;
    private static final int DATAGRAM_BUFFER_SIZE = 65536;
    private static final int SOCKET_BUFFER_SIZE = 65536;
    private static final int SOCKET_TIMEOUT = 1000;
    private static final int SHUTDOWN_TIMEOUT_SECONDS = 5;
    private static final int JOIN_SERIALIZATION_ERROR_SUPPRESSION_MILLIS = 60000;
    private final List<MulticastListener> listeners = new CopyOnWriteArrayList<MulticastListener>();
    private final Object sendLock = new Object();
    private final CountDownLatch stopLatch = new CountDownLatch(1);
    private final ILogger logger;
    private final Node node;
    private final MulticastSocket multicastSocket;
    private final BufferObjectDataOutput sendOutput;
    private final DatagramPacket datagramPacketSend;
    private final DatagramPacket datagramPacketReceive;
    private final JoinMessageTrustChecker joinMessageTrustChecker;
    private final ByteArrayProcessor inputProcessor;
    private final ByteArrayProcessor outputProcessor;
    private long lastLoggedJoinSerializationFailure;
    private volatile boolean running = true;

    private MulticastService(Node node, MulticastSocket multicastSocket) throws Exception {
        this.logger = node.getLogger(MulticastService.class.getName());
        this.node = node;
        this.multicastSocket = multicastSocket;
        NodeIOService nodeIOService = new NodeIOService(node, node.nodeEngine);
        this.inputProcessor = node.getNodeExtension().createMulticastInputProcessor(nodeIOService);
        this.outputProcessor = node.getNodeExtension().createMulticastOutputProcessor(nodeIOService);
        this.sendOutput = node.getSerializationService().createObjectDataOutput(1024);
        Config config = node.getConfig();
        MulticastConfig multicastConfig = ConfigAccessor.getActiveMemberNetworkConfig(config).getJoin().getMulticastConfig();
        this.datagramPacketSend = new DatagramPacket(new byte[0], 0, InetAddress.getByName(multicastConfig.getMulticastGroup()), multicastConfig.getMulticastPort());
        this.datagramPacketReceive = new DatagramPacket(new byte[65536], 65536);
        Set<String> trustedInterfaces = multicastConfig.getTrustedInterfaces();
        ILogger logger = node.getLogger(JoinMessageTrustChecker.class);
        this.joinMessageTrustChecker = new JoinMessageTrustChecker(trustedInterfaces, logger);
    }

    public static MulticastService createMulticastService(Address bindAddress, Node node, Config config, ILogger logger) {
        JoinConfig join = ConfigAccessor.getActiveMemberNetworkConfig(config).getJoin();
        MulticastConfig multicastConfig = join.getMulticastConfig();
        if (!multicastConfig.isEnabled()) {
            return null;
        }
        MulticastService mcService = null;
        try {
            MulticastSocket multicastSocket = new MulticastSocket(null);
            multicastSocket.setReuseAddress(true);
            multicastSocket.bind(new InetSocketAddress(multicastConfig.getMulticastPort()));
            multicastSocket.setTimeToLive(multicastConfig.getMulticastTimeToLive());
            try {
                if (!bindAddress.getInetAddress().isLoopbackAddress()) {
                    multicastSocket.setInterface(bindAddress.getInetAddress());
                } else if (multicastConfig.isLoopbackModeEnabled()) {
                    multicastSocket.setLoopbackMode(true);
                    multicastSocket.setInterface(bindAddress.getInetAddress());
                } else {
                    logger.warning("Hazelcast is bound to " + bindAddress.getHost() + " and loop-back mode is disabled in the configuration. This could cause multicast auto-discovery issues and render it unable to work. Check your network connectivity, try to enable the loopback mode and/or force -Djava.net.preferIPv4Stack=true on your JVM.");
                }
            }
            catch (Exception e) {
                logger.warning(e);
            }
            multicastSocket.setReceiveBufferSize(65536);
            multicastSocket.setSendBufferSize(65536);
            String multicastGroup = System.getProperty("hazelcast.multicast.group");
            if (multicastGroup == null) {
                multicastGroup = multicastConfig.getMulticastGroup();
            }
            multicastConfig.setMulticastGroup(multicastGroup);
            multicastSocket.joinGroup(InetAddress.getByName(multicastGroup));
            multicastSocket.setSoTimeout(1000);
            mcService = new MulticastService(node, multicastSocket);
            mcService.addMulticastListener(new NodeMulticastListener(node));
        }
        catch (Exception e) {
            logger.severe(e);
        }
        return mcService;
    }

    public void addMulticastListener(MulticastListener multicastListener) {
        this.listeners.add(multicastListener);
    }

    public void removeMulticastListener(MulticastListener multicastListener) {
        this.listeners.remove(multicastListener);
    }

    public void stop() {
        try {
            if (!this.running && this.multicastSocket.isClosed()) {
                return;
            }
            try {
                this.multicastSocket.close();
            }
            catch (Throwable ignored) {
                EmptyStatement.ignore(ignored);
            }
            this.running = false;
            if (!this.stopLatch.await(5L, TimeUnit.SECONDS)) {
                this.logger.warning("Failed to shutdown MulticastService in 5 seconds!");
            }
        }
        catch (Throwable e) {
            this.logger.warning(e);
        }
    }

    private void cleanup() {
        this.running = false;
        try {
            this.sendOutput.close();
            this.datagramPacketReceive.setData(new byte[0]);
            this.datagramPacketSend.setData(new byte[0]);
        }
        catch (Throwable ignored) {
            EmptyStatement.ignore(ignored);
        }
        this.stopLatch.countDown();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public void run() {
        try {
            while (this.running) {
                try {
                    JoinMessage joinMessage = this.receive();
                    if (joinMessage == null || !this.joinMessageTrustChecker.isTrusted(joinMessage)) continue;
                    for (MulticastListener multicastListener : this.listeners) {
                        try {
                            multicastListener.onMessage(joinMessage);
                        }
                        catch (Exception e) {
                            this.logger.warning(e);
                        }
                    }
                }
                catch (OutOfMemoryError e) {
                    OutOfMemoryErrorDispatcher.onOutOfMemory(e);
                }
                catch (Exception e) {
                    this.logger.warning(e);
                }
            }
            return;
        }
        finally {
            this.cleanup();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private JoinMessage receive() {
        block15: {
            JoinMessage joinMessage;
            byte packetVersion;
            try {
                this.multicastSocket.receive(this.datagramPacketReceive);
            }
            catch (IOException ignore) {
                return null;
            }
            byte[] data = this.datagramPacketReceive.getData();
            int offset = this.datagramPacketReceive.getOffset();
            int length = this.datagramPacketReceive.getLength();
            byte[] processed = this.inputProcessor != null ? this.inputProcessor.process(data, offset, length) : data;
            BufferObjectDataInput input = this.node.getSerializationService().createObjectDataInput(processed);
            if (this.inputProcessor == null) {
                input.position(offset);
            }
            if ((packetVersion = input.readByte()) != 4) {
                this.logger.warning("Received a JoinRequest with a different packet version, or encrypted. Verify that the sender Node, doesn't have symmetric-encryption on. This -> 4, Incoming -> " + packetVersion + ", Sender -> " + this.datagramPacketReceive.getAddress());
                return null;
            }
            try {
                joinMessage = (JoinMessage)input.readObject();
            }
            catch (Throwable throwable) {
                try {
                    try {
                        input.close();
                        throw throwable;
                    }
                    catch (Exception e) {
                        if (e instanceof EOFException || e instanceof HazelcastSerializationException) {
                            long now = System.currentTimeMillis();
                            if (now - this.lastLoggedJoinSerializationFailure > 60000L) {
                                this.lastLoggedJoinSerializationFailure = now;
                                this.logger.warning("Received a JoinRequest with an incompatible binary-format. An old version of Hazelcast may be using the same multicast discovery port. Are you running multiple Hazelcast clusters on this host? (This message will be suppressed for 60 seconds). ");
                            }
                            break block15;
                        }
                        if (e instanceof GeneralSecurityException) {
                            this.logger.warning("Received a JoinRequest with an incompatible encoding. Symmetric-encryption is enabled on this node, the remote node either doesn't have it on, or it uses different cipher.(This message will be suppressed for 60 seconds). ");
                            break block15;
                        }
                        throw e;
                    }
                }
                catch (Exception e) {
                    this.logger.warning(e);
                }
            }
            input.close();
            return joinMessage;
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void send(JoinMessage joinMessage) {
        if (!this.running) {
            return;
        }
        BufferObjectDataOutput out = this.sendOutput;
        Object object = this.sendLock;
        synchronized (object) {
            try {
                out.writeByte(4);
                out.writeObject(joinMessage);
                byte[] processed = this.outputProcessor != null ? this.outputProcessor.process(out.toByteArray()) : out.toByteArray();
                this.datagramPacketSend.setData(processed);
                this.multicastSocket.send(this.datagramPacketSend);
                out.clear();
            }
            catch (IOException e) {
                this.logger.warning("Sending multicast datagram failed. Exception message saying the operation is not permitted usually means the underlying OS is not able to send packets at a given pace. It can be caused by starting several hazelcast members in parallel when the members send their join message nearly at the same time.", e);
            }
        }
    }
}

