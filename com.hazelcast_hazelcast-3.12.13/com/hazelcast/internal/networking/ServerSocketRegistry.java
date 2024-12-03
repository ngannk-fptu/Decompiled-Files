/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.networking;

import com.hazelcast.instance.EndpointQualifier;
import com.hazelcast.nio.IOUtil;
import java.nio.channels.ServerSocketChannel;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerSocketRegistry
implements Iterable<Pair> {
    private final boolean unifiedSocket;
    private final AtomicBoolean isOpen = new AtomicBoolean(true);
    private final Map<EndpointQualifier, ServerSocketChannel> serverSocketChannelMap;
    private final Set<Pair> entries = new HashSet<Pair>();

    public ServerSocketRegistry(Map<EndpointQualifier, ServerSocketChannel> map, boolean unifiedSocket) {
        this.unifiedSocket = unifiedSocket;
        this.serverSocketChannelMap = map == null ? Collections.emptyMap() : map;
        this.buildEntries();
    }

    public boolean isOpen() {
        return this.isOpen.get();
    }

    public boolean holdsUnifiedSocket() {
        return this.unifiedSocket;
    }

    private void buildEntries() {
        if (!this.serverSocketChannelMap.isEmpty()) {
            for (Map.Entry<EndpointQualifier, ServerSocketChannel> entry : this.serverSocketChannelMap.entrySet()) {
                this.entries.add(new Pair(entry.getValue(), entry.getKey()));
            }
        }
    }

    @Override
    public Iterator<Pair> iterator() {
        return this.entries.iterator();
    }

    public void destroy() {
        if (this.isOpen.compareAndSet(true, false)) {
            for (ServerSocketChannel channel : this.serverSocketChannelMap.values()) {
                IOUtil.closeResource(channel);
            }
        }
    }

    public String toString() {
        return "ServerSocketRegistry{" + this.serverSocketChannelMap + "}";
    }

    public static final class Pair {
        private final ServerSocketChannel channel;
        private final EndpointQualifier qualifier;

        private Pair(ServerSocketChannel channel, EndpointQualifier qualifier) {
            this.channel = channel;
            this.qualifier = qualifier;
        }

        public ServerSocketChannel getChannel() {
            return this.channel;
        }

        public EndpointQualifier getQualifier() {
            return this.qualifier;
        }
    }
}

