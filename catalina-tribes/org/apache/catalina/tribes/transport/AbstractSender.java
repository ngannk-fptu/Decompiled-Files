/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.tribes.transport;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.tribes.transport.DataSender;

public abstract class AbstractSender
implements DataSender {
    private volatile boolean connected = false;
    private int rxBufSize = 25188;
    private int txBufSize = 65536;
    private int udpRxBufSize = 25188;
    private int udpTxBufSize = 65536;
    private boolean directBuffer = false;
    private int keepAliveCount = -1;
    private int requestCount = 0;
    private long connectTime;
    private long keepAliveTime = -1L;
    private long timeout = 3000L;
    private Member destination;
    private InetAddress address;
    private int port;
    private int maxRetryAttempts = 1;
    private int attempt;
    private boolean tcpNoDelay = true;
    private boolean soKeepAlive = false;
    private boolean ooBInline = true;
    private boolean soReuseAddress = true;
    private boolean soLingerOn = false;
    private int soLingerTime = 3;
    private int soTrafficClass = 28;
    private boolean throwOnFailedAck = true;
    private boolean udpBased = false;
    private int udpPort = -1;

    public static void transferProperties(AbstractSender from, AbstractSender to) {
        to.rxBufSize = from.rxBufSize;
        to.txBufSize = from.txBufSize;
        to.directBuffer = from.directBuffer;
        to.keepAliveCount = from.keepAliveCount;
        to.keepAliveTime = from.keepAliveTime;
        to.timeout = from.timeout;
        to.destination = from.destination;
        to.address = from.address;
        to.port = from.port;
        to.maxRetryAttempts = from.maxRetryAttempts;
        to.tcpNoDelay = from.tcpNoDelay;
        to.soKeepAlive = from.soKeepAlive;
        to.ooBInline = from.ooBInline;
        to.soReuseAddress = from.soReuseAddress;
        to.soLingerOn = from.soLingerOn;
        to.soLingerTime = from.soLingerTime;
        to.soTrafficClass = from.soTrafficClass;
        to.throwOnFailedAck = from.throwOnFailedAck;
        to.udpBased = from.udpBased;
        to.udpPort = from.udpPort;
    }

    @Override
    public abstract void connect() throws IOException;

    @Override
    public abstract void disconnect();

    @Override
    public boolean keepalive() {
        boolean disconnect = false;
        if (this.isUdpBased()) {
            disconnect = true;
        } else if (this.keepAliveCount >= 0 && this.requestCount > this.keepAliveCount) {
            disconnect = true;
        } else if (this.keepAliveTime >= 0L && System.currentTimeMillis() - this.connectTime > this.keepAliveTime) {
            disconnect = true;
        }
        if (disconnect) {
            this.disconnect();
        }
        return disconnect;
    }

    protected void setConnected(boolean connected) {
        this.connected = connected;
    }

    @Override
    public boolean isConnected() {
        return this.connected;
    }

    @Override
    public long getConnectTime() {
        return this.connectTime;
    }

    public Member getDestination() {
        return this.destination;
    }

    public int getKeepAliveCount() {
        return this.keepAliveCount;
    }

    public long getKeepAliveTime() {
        return this.keepAliveTime;
    }

    @Override
    public int getRequestCount() {
        return this.requestCount;
    }

    public int getRxBufSize() {
        return this.rxBufSize;
    }

    public long getTimeout() {
        return this.timeout;
    }

    public int getTxBufSize() {
        return this.txBufSize;
    }

    public InetAddress getAddress() {
        return this.address;
    }

    public int getPort() {
        return this.port;
    }

    public int getMaxRetryAttempts() {
        return this.maxRetryAttempts;
    }

    public void setDirectBuffer(boolean directBuffer) {
        this.directBuffer = directBuffer;
    }

    public boolean getDirectBuffer() {
        return this.directBuffer;
    }

    public int getAttempt() {
        return this.attempt;
    }

    public boolean getTcpNoDelay() {
        return this.tcpNoDelay;
    }

    public boolean getSoKeepAlive() {
        return this.soKeepAlive;
    }

    public boolean getOoBInline() {
        return this.ooBInline;
    }

    public boolean getSoReuseAddress() {
        return this.soReuseAddress;
    }

    public boolean getSoLingerOn() {
        return this.soLingerOn;
    }

    public int getSoLingerTime() {
        return this.soLingerTime;
    }

    public int getSoTrafficClass() {
        return this.soTrafficClass;
    }

    public boolean getThrowOnFailedAck() {
        return this.throwOnFailedAck;
    }

    @Override
    public void setKeepAliveCount(int keepAliveCount) {
        this.keepAliveCount = keepAliveCount;
    }

    @Override
    public void setKeepAliveTime(long keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }

    public void setRequestCount(int requestCount) {
        this.requestCount = requestCount;
    }

    @Override
    public void setRxBufSize(int rxBufSize) {
        this.rxBufSize = rxBufSize;
    }

    @Override
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    @Override
    public void setTxBufSize(int txBufSize) {
        this.txBufSize = txBufSize;
    }

    public void setConnectTime(long connectTime) {
        this.connectTime = connectTime;
    }

    public void setMaxRetryAttempts(int maxRetryAttempts) {
        this.maxRetryAttempts = maxRetryAttempts;
    }

    public void setAttempt(int attempt) {
        this.attempt = attempt;
    }

    public void setTcpNoDelay(boolean tcpNoDelay) {
        this.tcpNoDelay = tcpNoDelay;
    }

    public void setSoKeepAlive(boolean soKeepAlive) {
        this.soKeepAlive = soKeepAlive;
    }

    public void setOoBInline(boolean ooBInline) {
        this.ooBInline = ooBInline;
    }

    public void setSoReuseAddress(boolean soReuseAddress) {
        this.soReuseAddress = soReuseAddress;
    }

    public void setSoLingerOn(boolean soLingerOn) {
        this.soLingerOn = soLingerOn;
    }

    public void setSoLingerTime(int soLingerTime) {
        this.soLingerTime = soLingerTime;
    }

    public void setSoTrafficClass(int soTrafficClass) {
        this.soTrafficClass = soTrafficClass;
    }

    public void setThrowOnFailedAck(boolean throwOnFailedAck) {
        this.throwOnFailedAck = throwOnFailedAck;
    }

    public void setDestination(Member destination) throws UnknownHostException {
        this.destination = destination;
        this.address = InetAddress.getByAddress(destination.getHost());
        this.port = destination.getPort();
        this.udpPort = destination.getUdpPort();
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }

    public boolean isUdpBased() {
        return this.udpBased;
    }

    public void setUdpBased(boolean udpBased) {
        this.udpBased = udpBased;
    }

    public int getUdpPort() {
        return this.udpPort;
    }

    public void setUdpPort(int udpPort) {
        this.udpPort = udpPort;
    }

    public int getUdpRxBufSize() {
        return this.udpRxBufSize;
    }

    public void setUdpRxBufSize(int udpRxBufSize) {
        this.udpRxBufSize = udpRxBufSize;
    }

    public int getUdpTxBufSize() {
        return this.udpTxBufSize;
    }

    public void setUdpTxBufSize(int udpTxBufSize) {
        this.udpTxBufSize = udpTxBufSize;
    }
}

