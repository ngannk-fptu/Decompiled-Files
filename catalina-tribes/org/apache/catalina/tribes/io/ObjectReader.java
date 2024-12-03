/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.tribes.io;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import org.apache.catalina.tribes.ChannelMessage;
import org.apache.catalina.tribes.io.ChannelData;
import org.apache.catalina.tribes.io.XByteBuffer;
import org.apache.catalina.tribes.util.StringManager;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class ObjectReader {
    private static final Log log = LogFactory.getLog(ObjectReader.class);
    protected static final StringManager sm = StringManager.getManager(ObjectReader.class);
    private XByteBuffer buffer;
    protected long lastAccess = System.currentTimeMillis();
    protected boolean accessed = false;
    private volatile boolean cancelled;

    public ObjectReader(int packetSize) {
        this.buffer = new XByteBuffer(packetSize, true);
    }

    public ObjectReader(SocketChannel channel) {
        this(channel.socket());
    }

    public ObjectReader(Socket socket) {
        try {
            this.buffer = new XByteBuffer(socket.getReceiveBufferSize(), true);
        }
        catch (IOException x) {
            log.warn((Object)sm.getString("objectReader.retrieveFailed.socketReceiverBufferSize", Integer.toString(65536)));
            this.buffer = new XByteBuffer(65536, true);
        }
    }

    public synchronized void access() {
        this.accessed = true;
        this.lastAccess = System.currentTimeMillis();
    }

    public synchronized void finish() {
        this.accessed = false;
        this.lastAccess = System.currentTimeMillis();
    }

    public synchronized boolean isAccessed() {
        return this.accessed;
    }

    public int append(ByteBuffer data, int len, boolean count) {
        this.buffer.append(data, len);
        int pkgCnt = -1;
        if (count) {
            pkgCnt = this.buffer.countPackages();
        }
        return pkgCnt;
    }

    public int append(byte[] data, int off, int len, boolean count) {
        this.buffer.append(data, off, len);
        int pkgCnt = -1;
        if (count) {
            pkgCnt = this.buffer.countPackages();
        }
        return pkgCnt;
    }

    public ChannelMessage[] execute() {
        int pkgCnt = this.buffer.countPackages();
        ChannelMessage[] result = new ChannelMessage[pkgCnt];
        for (int i = 0; i < pkgCnt; ++i) {
            ChannelData data = this.buffer.extractPackage(true);
            result[i] = data;
        }
        return result;
    }

    public int bufferSize() {
        return this.buffer.getLength();
    }

    public boolean hasPackage() {
        return this.buffer.countPackages(true) > 0;
    }

    public int count() {
        return this.buffer.countPackages();
    }

    public void close() {
        this.buffer = null;
    }

    public synchronized long getLastAccess() {
        return this.lastAccess;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public synchronized void setLastAccess(long lastAccess) {
        this.lastAccess = lastAccess;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}

