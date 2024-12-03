/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.analytics.event.transport;

import com.atlassian.analytics.EventMessage;
import com.atlassian.analytics.event.serialization.EventSerializer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDPSender {
    private DatagramSocket clientSocket;
    private EventSerializer serializer = new EventSerializer();

    public UDPSender() throws SocketException {
        this.clientSocket = new DatagramSocket();
    }

    public void send(EventMessage message, InetAddress address, int port) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        this.serializer.serialize(message, baos);
        byte[] data = baos.toByteArray();
        this.clientSocket.send(new DatagramPacket(data, data.length, address, port));
    }

    public void shutdown() {
        this.clientSocket.close();
    }
}

