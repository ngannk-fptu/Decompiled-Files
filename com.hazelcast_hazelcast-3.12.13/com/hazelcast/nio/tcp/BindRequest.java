/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio.tcp;

import com.hazelcast.instance.EndpointQualifier;
import com.hazelcast.instance.ProtocolType;
import com.hazelcast.internal.cluster.impl.BindMessage;
import com.hazelcast.internal.cluster.impl.ExtendedBindMessage;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.IOService;
import com.hazelcast.nio.Packet;
import com.hazelcast.nio.tcp.TcpIpConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BindRequest {
    private final ILogger logger;
    private final IOService ioService;
    private final TcpIpConnection connection;
    private final Address remoteEndPoint;
    private final boolean reply;

    BindRequest(ILogger logger, IOService ioService, TcpIpConnection connection, Address remoteEndPoint, boolean reply) {
        this.logger = logger;
        this.ioService = ioService;
        this.connection = connection;
        this.remoteEndPoint = remoteEndPoint;
        this.reply = reply;
    }

    public void send() {
        this.connection.setEndPoint(this.remoteEndPoint);
        this.ioService.onSuccessfulConnection(this.remoteEndPoint);
        if (this.logger.isFinestEnabled()) {
            this.logger.finest("Sending bind packet to " + this.remoteEndPoint);
        }
        ExtendedBindMessage bind = new ExtendedBindMessage(1, this.getConfiguredLocalAddresses(), this.remoteEndPoint, this.reply);
        byte[] bytes = this.ioService.getSerializationService().toBytes(bind);
        Packet packet = new Packet(bytes).setPacketType(Packet.Type.EXTENDED_BIND);
        this.connection.write(packet);
        BindMessage oldbind = new BindMessage(this.ioService.getThisAddress(), this.remoteEndPoint, this.reply);
        bytes = this.ioService.getSerializationService().toBytes(oldbind);
        packet = new Packet(bytes).setPacketType(Packet.Type.BIND);
        this.connection.write(packet);
    }

    Map<ProtocolType, Collection<Address>> getConfiguredLocalAddresses() {
        HashMap<ProtocolType, Collection<Address>> addressMap = new HashMap<ProtocolType, Collection<Address>>();
        Map<EndpointQualifier, Address> addressesPerEndpointQualifier = this.ioService.getThisAddresses();
        for (Map.Entry<EndpointQualifier, Address> addressEntry : addressesPerEndpointQualifier.entrySet()) {
            ArrayList<Address> addresses = (ArrayList<Address>)addressMap.get((Object)addressEntry.getKey().getType());
            if (addresses == null) {
                addresses = new ArrayList<Address>();
                addressMap.put(addressEntry.getKey().getType(), addresses);
            }
            addresses.add(addressEntry.getValue());
        }
        return addressMap;
    }
}

