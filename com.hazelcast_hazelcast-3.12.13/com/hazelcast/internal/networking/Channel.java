/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.networking;

import com.hazelcast.internal.networking.ChannelCloseListener;
import com.hazelcast.internal.networking.ChannelOptions;
import com.hazelcast.internal.networking.InboundPipeline;
import com.hazelcast.internal.networking.OutboundFrame;
import com.hazelcast.internal.networking.OutboundPipeline;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.ConcurrentMap;

public interface Channel
extends Closeable {
    public ChannelOptions options();

    public ConcurrentMap attributeMap();

    public InboundPipeline inboundPipeline();

    public OutboundPipeline outboundPipeline();

    public Socket socket();

    public SocketAddress remoteSocketAddress();

    public SocketAddress localSocketAddress();

    public long lastReadTimeMillis();

    public long lastWriteTimeMillis();

    public void start();

    public void connect(InetSocketAddress var1, int var2) throws IOException;

    @Override
    public void close() throws IOException;

    public boolean isClosed();

    public void addCloseListener(ChannelCloseListener var1);

    public boolean isClientMode();

    public boolean write(OutboundFrame var1);

    public long bytesRead();

    public long bytesWritten();
}

