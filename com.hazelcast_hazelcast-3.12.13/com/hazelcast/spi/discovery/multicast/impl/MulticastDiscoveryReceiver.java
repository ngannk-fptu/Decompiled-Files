/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.discovery.multicast.impl;

import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.IOUtil;
import com.hazelcast.spi.discovery.multicast.impl.MulticastMemberInfo;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

public class MulticastDiscoveryReceiver {
    private static final int DATAGRAM_BUFFER_SIZE = 65536;
    private final MulticastSocket multicastSocket;
    private final DatagramPacket datagramPacketReceive = new DatagramPacket(new byte[65536], 65536);
    private final ILogger logger;

    public MulticastDiscoveryReceiver(MulticastSocket multicastSocket, ILogger logger) {
        this.multicastSocket = multicastSocket;
        this.logger = logger;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public MulticastMemberInfo receive() {
        block6: {
            MulticastMemberInfo multicastMemberInfo;
            ObjectInputStream in = null;
            ByteArrayInputStream bis = null;
            try {
                MulticastMemberInfo multicastMemberInfo2;
                this.multicastSocket.receive(this.datagramPacketReceive);
                byte[] data = this.datagramPacketReceive.getData();
                bis = new ByteArrayInputStream(data);
                in = new ObjectInputStream(bis);
                Object o = in.readObject();
                multicastMemberInfo = multicastMemberInfo2 = (MulticastMemberInfo)o;
                IOUtil.closeResource(bis);
            }
            catch (Exception e) {
                if (this.logger.isFinestEnabled()) {
                    this.logger.finest("Couldn't get member info from multicast channel " + e.getMessage());
                }
                break block6;
            }
            finally {
                IOUtil.closeResource(bis);
                IOUtil.closeResource(in);
            }
            IOUtil.closeResource(in);
            return multicastMemberInfo;
        }
        return null;
    }
}

