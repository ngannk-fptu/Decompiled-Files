/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketOption;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import javax.management.ObjectName;

public class SocketProperties {
    protected int processorCache = 500;
    protected int eventCache = 500;
    protected boolean directBuffer = false;
    protected boolean directSslBuffer = false;
    protected Integer rxBufSize = null;
    protected Integer txBufSize = null;
    protected int appReadBufSize = 8192;
    protected int appWriteBufSize = 8192;
    protected int bufferPool = 500;
    protected int bufferPoolSize = 0x6400000;
    protected Boolean tcpNoDelay = Boolean.TRUE;
    protected Boolean soKeepAlive = null;
    protected Boolean ooBInline = null;
    protected Boolean soReuseAddress = null;
    protected Boolean soLingerOn = null;
    protected Integer soLingerTime = null;
    protected Integer soTimeout = 20000;
    protected Integer performanceConnectionTime = null;
    protected Integer performanceLatency = null;
    protected Integer performanceBandwidth = null;
    protected long timeoutInterval = 1000L;
    protected int unlockTimeout = 250;
    private ObjectName oname = null;

    public void setProperties(Socket socket) throws SocketException {
        if (this.rxBufSize != null) {
            socket.setReceiveBufferSize(this.rxBufSize);
        }
        if (this.txBufSize != null) {
            socket.setSendBufferSize(this.txBufSize);
        }
        if (this.ooBInline != null) {
            socket.setOOBInline(this.ooBInline);
        }
        if (this.soKeepAlive != null) {
            socket.setKeepAlive(this.soKeepAlive);
        }
        if (this.performanceConnectionTime != null && this.performanceLatency != null && this.performanceBandwidth != null) {
            socket.setPerformancePreferences(this.performanceConnectionTime, this.performanceLatency, this.performanceBandwidth);
        }
        if (this.soReuseAddress != null) {
            socket.setReuseAddress(this.soReuseAddress);
        }
        if (this.soLingerOn != null && this.soLingerTime != null) {
            socket.setSoLinger(this.soLingerOn, this.soLingerTime);
        }
        if (this.soTimeout != null && this.soTimeout >= 0) {
            socket.setSoTimeout(this.soTimeout);
        }
        if (this.tcpNoDelay != null) {
            try {
                socket.setTcpNoDelay(this.tcpNoDelay);
            }
            catch (SocketException socketException) {
                // empty catch block
            }
        }
    }

    public void setProperties(ServerSocket socket) throws SocketException {
        if (this.rxBufSize != null) {
            socket.setReceiveBufferSize(this.rxBufSize);
        }
        if (this.performanceConnectionTime != null && this.performanceLatency != null && this.performanceBandwidth != null) {
            socket.setPerformancePreferences(this.performanceConnectionTime, this.performanceLatency, this.performanceBandwidth);
        }
        if (this.soReuseAddress != null) {
            socket.setReuseAddress(this.soReuseAddress);
        }
        if (this.soTimeout != null && this.soTimeout >= 0) {
            socket.setSoTimeout(this.soTimeout);
        }
    }

    public void setProperties(AsynchronousSocketChannel socket) throws IOException {
        if (this.rxBufSize != null) {
            socket.setOption((SocketOption)StandardSocketOptions.SO_RCVBUF, this.rxBufSize);
        }
        if (this.txBufSize != null) {
            socket.setOption((SocketOption)StandardSocketOptions.SO_SNDBUF, this.txBufSize);
        }
        if (this.soKeepAlive != null) {
            socket.setOption((SocketOption)StandardSocketOptions.SO_KEEPALIVE, this.soKeepAlive);
        }
        if (this.soReuseAddress != null) {
            socket.setOption((SocketOption)StandardSocketOptions.SO_REUSEADDR, this.soReuseAddress);
        }
        if (this.soLingerOn != null && this.soLingerOn.booleanValue() && this.soLingerTime != null) {
            socket.setOption((SocketOption)StandardSocketOptions.SO_LINGER, this.soLingerTime);
        }
        if (this.tcpNoDelay != null) {
            socket.setOption((SocketOption)StandardSocketOptions.TCP_NODELAY, this.tcpNoDelay);
        }
    }

    public void setProperties(AsynchronousServerSocketChannel socket) throws IOException {
        if (this.rxBufSize != null) {
            socket.setOption((SocketOption)StandardSocketOptions.SO_RCVBUF, this.rxBufSize);
        }
        if (this.soReuseAddress != null) {
            socket.setOption((SocketOption)StandardSocketOptions.SO_REUSEADDR, this.soReuseAddress);
        }
    }

    public boolean getDirectBuffer() {
        return this.directBuffer;
    }

    public boolean getDirectSslBuffer() {
        return this.directSslBuffer;
    }

    public boolean getOoBInline() {
        return this.ooBInline;
    }

    public int getPerformanceBandwidth() {
        return this.performanceBandwidth;
    }

    public int getPerformanceConnectionTime() {
        return this.performanceConnectionTime;
    }

    public int getPerformanceLatency() {
        return this.performanceLatency;
    }

    public int getRxBufSize() {
        return this.rxBufSize;
    }

    public boolean getSoKeepAlive() {
        return this.soKeepAlive;
    }

    public boolean getSoLingerOn() {
        return this.soLingerOn;
    }

    public int getSoLingerTime() {
        return this.soLingerTime;
    }

    public boolean getSoReuseAddress() {
        return this.soReuseAddress;
    }

    public int getSoTimeout() {
        return this.soTimeout;
    }

    public boolean getTcpNoDelay() {
        return this.tcpNoDelay;
    }

    public int getTxBufSize() {
        return this.txBufSize;
    }

    public int getBufferPool() {
        return this.bufferPool;
    }

    public int getBufferPoolSize() {
        return this.bufferPoolSize;
    }

    public int getEventCache() {
        return this.eventCache;
    }

    public int getAppReadBufSize() {
        return this.appReadBufSize;
    }

    public int getAppWriteBufSize() {
        return this.appWriteBufSize;
    }

    public int getProcessorCache() {
        return this.processorCache;
    }

    public long getTimeoutInterval() {
        return this.timeoutInterval;
    }

    public int getDirectBufferPool() {
        return this.bufferPool;
    }

    public void setPerformanceConnectionTime(int performanceConnectionTime) {
        this.performanceConnectionTime = performanceConnectionTime;
    }

    public void setTxBufSize(int txBufSize) {
        this.txBufSize = txBufSize;
    }

    public void setTcpNoDelay(boolean tcpNoDelay) {
        this.tcpNoDelay = tcpNoDelay;
    }

    public void setSoTimeout(int soTimeout) {
        this.soTimeout = soTimeout;
    }

    public void setSoReuseAddress(boolean soReuseAddress) {
        this.soReuseAddress = soReuseAddress;
    }

    public void setSoLingerTime(int soLingerTime) {
        this.soLingerTime = soLingerTime;
    }

    public void setSoKeepAlive(boolean soKeepAlive) {
        this.soKeepAlive = soKeepAlive;
    }

    public void setRxBufSize(int rxBufSize) {
        this.rxBufSize = rxBufSize;
    }

    public void setPerformanceLatency(int performanceLatency) {
        this.performanceLatency = performanceLatency;
    }

    public void setPerformanceBandwidth(int performanceBandwidth) {
        this.performanceBandwidth = performanceBandwidth;
    }

    public void setOoBInline(boolean ooBInline) {
        this.ooBInline = ooBInline;
    }

    public void setDirectBuffer(boolean directBuffer) {
        this.directBuffer = directBuffer;
    }

    public void setDirectSslBuffer(boolean directSslBuffer) {
        this.directSslBuffer = directSslBuffer;
    }

    public void setSoLingerOn(boolean soLingerOn) {
        this.soLingerOn = soLingerOn;
    }

    public void setBufferPool(int bufferPool) {
        this.bufferPool = bufferPool;
    }

    public void setBufferPoolSize(int bufferPoolSize) {
        this.bufferPoolSize = bufferPoolSize;
    }

    public void setEventCache(int eventCache) {
        this.eventCache = eventCache;
    }

    public void setAppReadBufSize(int appReadBufSize) {
        this.appReadBufSize = appReadBufSize;
    }

    public void setAppWriteBufSize(int appWriteBufSize) {
        this.appWriteBufSize = appWriteBufSize;
    }

    public void setProcessorCache(int processorCache) {
        this.processorCache = processorCache;
    }

    public void setTimeoutInterval(long timeoutInterval) {
        this.timeoutInterval = timeoutInterval;
    }

    public void setDirectBufferPool(int directBufferPool) {
        this.bufferPool = directBufferPool;
    }

    public int getUnlockTimeout() {
        return this.unlockTimeout;
    }

    public void setUnlockTimeout(int unlockTimeout) {
        this.unlockTimeout = unlockTimeout;
    }

    void setObjectName(ObjectName oname) {
        this.oname = oname;
    }

    ObjectName getObjectName() {
        return this.oname;
    }
}

