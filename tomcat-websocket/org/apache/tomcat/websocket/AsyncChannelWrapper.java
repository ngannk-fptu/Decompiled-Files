/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.websocket;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLException;

public interface AsyncChannelWrapper {
    public Future<Integer> read(ByteBuffer var1);

    public <B, A extends B> void read(ByteBuffer var1, A var2, CompletionHandler<Integer, B> var3);

    public Future<Integer> write(ByteBuffer var1);

    public <B, A extends B> void write(ByteBuffer[] var1, int var2, int var3, long var4, TimeUnit var6, A var7, CompletionHandler<Long, B> var8);

    public void close();

    public Future<Void> handshake() throws SSLException;

    public SocketAddress getLocalAddress() throws IOException;
}

