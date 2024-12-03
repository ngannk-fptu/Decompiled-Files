/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.tribes.transport.nio;

public interface PooledParallelSenderMBean {
    public int getRxBufSize();

    public int getTxBufSize();

    public int getUdpRxBufSize();

    public int getUdpTxBufSize();

    public boolean getDirectBuffer();

    public int getKeepAliveCount();

    public long getKeepAliveTime();

    public long getTimeout();

    public int getMaxRetryAttempts();

    public boolean getOoBInline();

    public boolean getSoKeepAlive();

    public boolean getSoLingerOn();

    public int getSoLingerTime();

    public boolean getSoReuseAddress();

    public int getSoTrafficClass();

    public boolean getTcpNoDelay();

    public boolean getThrowOnFailedAck();

    public int getPoolSize();

    public long getMaxWait();

    public boolean isConnected();

    public int getInPoolSize();

    public int getInUsePoolSize();
}

