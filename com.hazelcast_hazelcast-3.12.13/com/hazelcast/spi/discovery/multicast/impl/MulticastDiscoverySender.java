/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.discovery.multicast.impl;

import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.discovery.DiscoveryNode;
import com.hazelcast.spi.discovery.multicast.impl.MulticastMemberInfo;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastDiscoverySender
implements Runnable {
    private static final int SLEEP_DURATION = 2000;
    private MulticastSocket multicastSocket;
    private MulticastMemberInfo multicastMemberInfo;
    private DatagramPacket datagramPacket;
    private ILogger logger;
    private String group;
    private int port;
    private volatile boolean stop;

    public MulticastDiscoverySender(DiscoveryNode discoveryNode, MulticastSocket multicastSocket, ILogger logger, String group, int port) throws IOException {
        this.multicastSocket = multicastSocket;
        this.logger = logger;
        this.group = group;
        this.port = port;
        if (discoveryNode != null) {
            Address address = discoveryNode.getPublicAddress();
            this.multicastMemberInfo = new MulticastMemberInfo(address.getHost(), address.getPort());
        }
        this.initDatagramPacket();
    }

    private void initDatagramPacket() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(this.multicastMemberInfo);
        byte[] yourBytes = bos.toByteArray();
        this.datagramPacket = new DatagramPacket(yourBytes, yourBytes.length, InetAddress.getByName(this.group), this.port);
    }

    @Override
    public void run() {
        while (!this.stop) {
            try {
                this.send();
            }
            catch (IOException e) {
                this.logger.finest(e.getMessage());
            }
            this.sleepUnlessStopped();
        }
    }

    private void sleepUnlessStopped() {
        if (this.stop) {
            return;
        }
        try {
            Thread.sleep(2000L);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            this.logger.finest("Thread sleeping interrupted. This may due to graceful shutdown.");
        }
    }

    void send() throws IOException {
        this.multicastSocket.send(this.datagramPacket);
    }

    public void stop() {
        this.stop = true;
    }
}

