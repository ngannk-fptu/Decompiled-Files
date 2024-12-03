/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.core.HazelcastInstance
 *  com.hazelcast.core.HazelcastInstanceAware
 *  com.hazelcast.instance.HazelcastInstanceImpl
 *  com.hazelcast.internal.serialization.InternalSerializationService
 *  com.hazelcast.internal.serialization.impl.ObjectDataInputStream
 *  com.hazelcast.internal.serialization.impl.ObjectDataOutputStream
 *  com.hazelcast.nio.MemberSocketInterceptor
 *  com.hazelcast.nio.ObjectDataInput
 *  com.hazelcast.nio.ObjectDataOutput
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.cluster.hazelcast.interceptor;

import com.atlassian.confluence.impl.cluster.hazelcast.interceptor.authenticator.ClusterJoinManager;
import com.atlassian.confluence.impl.cluster.hazelcast.interceptor.authenticator.ClusterJoinMode;
import com.atlassian.confluence.impl.cluster.hazelcast.interceptor.authenticator.ClusterJoinRequest;
import com.atlassian.confluence.impl.cluster.hazelcast.interceptor.authenticator.ParanoidObjectDataInputStream;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.instance.HazelcastInstanceImpl;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.internal.serialization.impl.ObjectDataInputStream;
import com.hazelcast.internal.serialization.impl.ObjectDataOutputStream;
import com.hazelcast.nio.MemberSocketInterceptor;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Properties;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ClusterJoinSocketInterceptor
implements MemberSocketInterceptor,
HazelcastInstanceAware {
    private static final Logger log = LoggerFactory.getLogger(ClusterJoinSocketInterceptor.class);
    private ClusterJoinManager clusterJoinManager;
    private HazelcastInstance hazelcast;

    public void init(Properties properties) {
        this.clusterJoinManager = (ClusterJoinManager)properties.get(ClusterJoinManager.class.getName());
    }

    public void onAccept(Socket socket) throws IOException {
        this.clusterJoinManager.accept(new SocketClusterJoinRequest(socket, this.hazelcast, ClusterJoinMode.ACCEPT));
    }

    public void onConnect(Socket socket) throws IOException {
        this.clusterJoinManager.connect(new SocketClusterJoinRequest(socket, this.hazelcast, ClusterJoinMode.CONNECT));
    }

    public void setHazelcastInstance(HazelcastInstance hazelcast) {
        this.hazelcast = hazelcast;
    }

    static final class SocketClusterJoinRequest
    implements ClusterJoinRequest {
        private final ClusterJoinMode joinMode;
        private final HazelcastInstance hazelcast;
        private final ObjectDataInputStream in;
        private final ObjectDataOutputStream out;
        private final String localAddress;
        private final String remoteAddress;
        private final int localPort;
        private final int remotePort;

        SocketClusterJoinRequest(Socket socket, HazelcastInstance hazelcast, ClusterJoinMode joinMode) throws IOException {
            this.joinMode = joinMode;
            this.hazelcast = hazelcast;
            InternalSerializationService serializationService = ((HazelcastInstanceImpl)hazelcast).getSerializationService();
            this.in = new ParanoidObjectDataInputStream(socket.getInputStream(), serializationService);
            this.out = new ObjectDataOutputStream(socket.getOutputStream(), serializationService);
            SocketAddress localSocket = socket.getLocalSocketAddress();
            if (localSocket instanceof InetSocketAddress) {
                InetSocketAddress localInetSocket = (InetSocketAddress)localSocket;
                this.localAddress = localInetSocket.getAddress().getHostAddress();
                this.localPort = localInetSocket.getPort();
            } else {
                log.warn("Local socket address not an InetSocketAddress: {}", (Object)localSocket);
                this.localAddress = localSocket.toString();
                this.localPort = localSocket.hashCode();
            }
            SocketAddress remoteSocket = socket.getRemoteSocketAddress();
            if (remoteSocket instanceof InetSocketAddress) {
                InetSocketAddress remoteInetSocket = (InetSocketAddress)remoteSocket;
                this.remoteAddress = remoteInetSocket.getAddress().getHostAddress();
                this.remotePort = remoteInetSocket.getPort();
            } else {
                log.warn("Remote socket address not an InetSocketAddress: {}", (Object)remoteSocket);
                this.remoteAddress = remoteSocket.toString();
                this.remotePort = remoteSocket.hashCode();
            }
        }

        @Override
        @Nonnull
        public HazelcastInstance getHazelcast() {
            return this.hazelcast;
        }

        @Override
        @Nonnull
        public ClusterJoinMode getJoinMode() {
            return this.joinMode;
        }

        @Override
        @Nonnull
        public String getLocalAddress() {
            return this.localAddress;
        }

        @Override
        public int getLocalPort() {
            return this.localPort;
        }

        @Override
        @Nonnull
        public String getRemoteAddress() {
            return this.remoteAddress;
        }

        @Override
        public int getRemotePort() {
            return this.remotePort;
        }

        @Override
        @Nonnull
        public ObjectDataInput in() {
            return this.in;
        }

        @Override
        @Nonnull
        public ObjectDataOutput out() {
            return this.out;
        }

        public String toString() {
            return this.joinMode + "(" + this.localAddress + ":" + this.localPort + (this.joinMode == ClusterJoinMode.ACCEPT ? " <- " : " -> ") + this.remoteAddress + ":" + this.remotePort + ")";
        }
    }
}

