/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.tribes.transport;

import java.io.IOException;

public interface DataSender {
    public void connect() throws IOException;

    public void disconnect();

    public boolean isConnected();

    public void setRxBufSize(int var1);

    public void setTxBufSize(int var1);

    public boolean keepalive();

    public void setTimeout(long var1);

    public void setKeepAliveCount(int var1);

    public void setKeepAliveTime(long var1);

    public int getRequestCount();

    public long getConnectTime();
}

