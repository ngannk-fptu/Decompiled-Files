/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.tribes.group.interceptors;

import java.util.concurrent.atomic.AtomicLong;

public interface ThroughputInterceptorMBean {
    public int getOptionFlag();

    public int getInterval();

    public void setInterval(int var1);

    public double getLastCnt();

    public double getMbAppTx();

    public double getMbRx();

    public double getMbTx();

    public AtomicLong getMsgRxCnt();

    public AtomicLong getMsgTxCnt();

    public AtomicLong getMsgTxErr();

    public long getRxStart();

    public double getTimeTx();

    public long getTxStart();

    public void report(double var1);
}

