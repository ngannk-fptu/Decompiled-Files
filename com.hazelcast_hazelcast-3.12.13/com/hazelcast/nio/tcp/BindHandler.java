/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.nio.tcp;

import com.hazelcast.instance.EndpointQualifier;
import com.hazelcast.instance.ProtocolType;
import com.hazelcast.internal.cluster.impl.BindMessage;
import com.hazelcast.internal.cluster.impl.ExtendedBindMessage;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ConnectionType;
import com.hazelcast.nio.IOService;
import com.hazelcast.nio.Packet;
import com.hazelcast.nio.tcp.BindRequest;
import com.hazelcast.nio.tcp.TcpIpConnection;
import com.hazelcast.nio.tcp.TcpIpEndpointManager;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

final class BindHandler {
    private final TcpIpEndpointManager tcpIpEndpointManager;
    private final IOService ioService;
    private final ILogger logger;
    private final boolean spoofingChecks;
    private final boolean unifiedEndpointManager;
    private final Set<ProtocolType> supportedProtocolTypes;

    BindHandler(TcpIpEndpointManager tcpIpEndpointManager, IOService ioService, ILogger logger, boolean spoofingChecks, Set<ProtocolType> supportedProtocolTypes) {
        this.tcpIpEndpointManager = tcpIpEndpointManager;
        this.ioService = ioService;
        this.logger = logger;
        this.spoofingChecks = spoofingChecks;
        this.supportedProtocolTypes = supportedProtocolTypes;
        this.unifiedEndpointManager = tcpIpEndpointManager.getEndpointQualifier() == null;
    }

    public void process(Packet packet) {
        Object bind = this.ioService.getSerializationService().toObject(packet);
        TcpIpConnection connection = (TcpIpConnection)packet.getConn();
        if (connection.setBinding()) {
            if (bind instanceof ExtendedBindMessage) {
                ExtendedBindMessage extendedBindMessage = (ExtendedBindMessage)bind;
                this.bind(connection, extendedBindMessage);
            } else {
                BindMessage bindMessage = (BindMessage)bind;
                this.bind(connection, bindMessage.getLocalAddress(), bindMessage.getTargetAddress(), bindMessage.shouldReply());
            }
        } else if (this.logger.isFinestEnabled()) {
            this.logger.finest("Connection " + connection + " is already bound, ignoring incoming " + bind);
        }
    }

    private synchronized boolean bind(TcpIpConnection connection, ExtendedBindMessage bindMessage) {
        if (this.logger.isFinestEnabled()) {
            this.logger.finest("Extended binding " + connection + ", complete message is " + bindMessage);
        }
        Map<ProtocolType, Collection<Address>> remoteAddressesPerProtocolType = bindMessage.getLocalAddresses();
        ArrayList<Address> allAliases = new ArrayList<Address>();
        for (Map.Entry<ProtocolType, Collection<Address>> remoteAddresses : remoteAddressesPerProtocolType.entrySet()) {
            if (!this.supportedProtocolTypes.contains((Object)remoteAddresses.getKey())) continue;
            allAliases.addAll(remoteAddresses.getValue());
        }
        assert (this.tcpIpEndpointManager.getEndpointQualifier() != EndpointQualifier.MEMBER || connection.getType() == ConnectionType.MEMBER) : "When handling MEMBER connections, connection type must be already set";
        boolean isMemberConnection = connection.getType() == ConnectionType.MEMBER && (this.tcpIpEndpointManager.getEndpointQualifier() == EndpointQualifier.MEMBER || this.unifiedEndpointManager);
        boolean mustRegisterRemoteSocketAddress = !bindMessage.isReply();
        Address remoteEndpoint = null;
        if (isMemberConnection) {
            if (mustRegisterRemoteSocketAddress) {
                allAliases.add(new Address(connection.getRemoteSocketAddress()));
            }
        } else {
            remoteEndpoint = new Address(connection.getRemoteSocketAddress());
        }
        return this.bind0(connection, remoteEndpoint, allAliases, bindMessage.isReply());
    }

    private synchronized void bind(TcpIpConnection connection, Address remoteEndPoint, Address localEndpoint, boolean reply) {
        if (this.logger.isFinestEnabled()) {
            this.logger.finest("Binding " + connection + " to " + remoteEndPoint + ", reply is " + reply);
        }
        Address thisAddress = this.ioService.getThisAddress();
        if (!(!this.spoofingChecks || this.ensureValidBindSource(connection, remoteEndPoint) && this.ensureBindNotFromSelf(connection, remoteEndPoint, thisAddress))) {
            return;
        }
        if (!this.ensureValidBindTarget(connection, remoteEndPoint, localEndpoint, thisAddress)) {
            return;
        }
        this.bind0(connection, remoteEndPoint, null, reply);
    }

    @SuppressFBWarnings(value={"RV_RETURN_VALUE_OF_PUTIFABSENT_IGNORED"})
    private synchronized boolean bind0(TcpIpConnection connection, Address remoteEndpoint, Collection<Address> remoteAddressAliases, boolean reply) {
        Address remoteAddress = new Address(connection.getRemoteSocketAddress());
        if (this.tcpIpEndpointManager.connectionsInProgress.contains(remoteAddress)) {
            remoteEndpoint = remoteAddress;
        }
        if (remoteEndpoint == null) {
            if (remoteAddressAliases == null) {
                throw new IllegalStateException("Remote endpoint and remote address aliases cannot be both null");
            }
            remoteEndpoint = remoteAddressAliases.iterator().next();
        }
        connection.setEndPoint(remoteEndpoint);
        this.ioService.onSuccessfulConnection(remoteEndpoint);
        if (reply) {
            BindRequest bindRequest = new BindRequest(this.logger, this.ioService, connection, remoteEndpoint, false);
            bindRequest.send();
        }
        if (this.checkAlreadyConnected(connection, remoteEndpoint)) {
            return false;
        }
        if (this.logger.isLoggable(Level.FINEST)) {
            this.logger.finest("Registering connection " + connection + " to address " + remoteEndpoint);
        }
        boolean returnValue = this.tcpIpEndpointManager.registerConnection(remoteEndpoint, connection);
        if (remoteAddressAliases != null && returnValue) {
            for (Address remoteAddressAlias : remoteAddressAliases) {
                if (this.logger.isLoggable(Level.FINEST)) {
                    this.logger.finest("Registering connection " + connection + " to address alias " + remoteAddressAlias);
                }
                this.tcpIpEndpointManager.connectionsMap.putIfAbsent(remoteAddressAlias, connection);
            }
        }
        return returnValue;
    }

    private boolean ensureValidBindSource(TcpIpConnection connection, Address remoteEndPoint) {
        try {
            InetAddress originalRemoteAddr = connection.getRemoteSocketAddress().getAddress();
            InetAddress presentedRemoteAddr = remoteEndPoint.getInetAddress();
            if (!originalRemoteAddr.equals(presentedRemoteAddr)) {
                String msg = "Wrong bind request from " + originalRemoteAddr + ", identified as " + presentedRemoteAddr;
                this.logger.warning(msg);
                connection.close(msg, null);
                return false;
            }
        }
        catch (UnknownHostException e) {
            String msg = e.getMessage();
            this.logger.warning(msg);
            connection.close(msg, e);
            return false;
        }
        return true;
    }

    private boolean ensureBindNotFromSelf(TcpIpConnection connection, Address remoteEndPoint, Address thisAddress) {
        if (thisAddress.equals(remoteEndPoint)) {
            String msg = "Wrong bind request. Remote endpoint is same to this endpoint.";
            this.logger.warning(msg);
            connection.close(msg, null);
            return false;
        }
        return true;
    }

    private boolean ensureValidBindTarget(TcpIpConnection connection, Address remoteEndPoint, Address localEndpoint, Address thisAddress) {
        if (this.ioService.isSocketBindAny() && !connection.isClient() && !thisAddress.equals(localEndpoint)) {
            String msg = "Wrong bind request from " + remoteEndPoint + "! This node is not the requested endpoint: " + localEndpoint;
            this.logger.warning(msg);
            connection.close(msg, null);
            return false;
        }
        return true;
    }

    private boolean checkAlreadyConnected(TcpIpConnection connection, Address remoteEndPoint) {
        TcpIpConnection existingConnection = this.tcpIpEndpointManager.getConnection(remoteEndPoint);
        if (existingConnection != null && existingConnection.isAlive()) {
            if (existingConnection != connection) {
                if (this.logger.isFinestEnabled()) {
                    this.logger.finest(existingConnection + " is already bound to " + remoteEndPoint + ", new one is " + connection);
                }
                this.tcpIpEndpointManager.activeConnections.add(connection);
            }
            return true;
        }
        return false;
    }
}

