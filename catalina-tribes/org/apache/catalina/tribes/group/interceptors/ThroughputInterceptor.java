/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.tribes.group.interceptors;

import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.catalina.tribes.ChannelException;
import org.apache.catalina.tribes.ChannelMessage;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.tribes.group.ChannelInterceptorBase;
import org.apache.catalina.tribes.group.InterceptorPayload;
import org.apache.catalina.tribes.group.interceptors.ThroughputInterceptorMBean;
import org.apache.catalina.tribes.io.ChannelData;
import org.apache.catalina.tribes.io.XByteBuffer;
import org.apache.catalina.tribes.util.StringManager;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class ThroughputInterceptor
extends ChannelInterceptorBase
implements ThroughputInterceptorMBean {
    private static final Log log = LogFactory.getLog(ThroughputInterceptor.class);
    protected static final StringManager sm = StringManager.getManager(ThroughputInterceptor.class);
    double mbTx = 0.0;
    double mbAppTx = 0.0;
    double mbRx = 0.0;
    double timeTx = 0.0;
    double lastCnt = 0.0;
    final AtomicLong msgTxCnt = new AtomicLong(1L);
    final AtomicLong msgRxCnt = new AtomicLong(0L);
    final AtomicLong msgTxErr = new AtomicLong(0L);
    int interval = 10000;
    final AtomicInteger access = new AtomicInteger(0);
    long txStart = 0L;
    long rxStart = 0L;
    final DecimalFormat df = new DecimalFormat("#0.00");

    @Override
    public void sendMessage(Member[] destination, ChannelMessage msg, InterceptorPayload payload) throws ChannelException {
        if (this.access.addAndGet(1) == 1) {
            this.txStart = System.currentTimeMillis();
        }
        long bytes = XByteBuffer.getDataPackageLength(((ChannelData)msg).getDataPackageLength());
        try {
            super.sendMessage(destination, msg, payload);
        }
        catch (ChannelException x) {
            this.msgTxErr.addAndGet(1L);
            if (this.access.get() == 1) {
                this.access.addAndGet(-1);
            }
            throw x;
        }
        this.mbTx += (double)(bytes * (long)destination.length) / 1048576.0;
        this.mbAppTx += (double)bytes / 1048576.0;
        if (this.access.addAndGet(-1) == 0) {
            long stop = System.currentTimeMillis();
            this.timeTx += (double)(stop - this.txStart) / 1000.0;
            if ((double)this.msgTxCnt.get() / (double)this.interval >= this.lastCnt) {
                this.lastCnt += 1.0;
                this.report(this.timeTx);
            }
        }
        this.msgTxCnt.addAndGet(1L);
    }

    @Override
    public void messageReceived(ChannelMessage msg) {
        if (this.rxStart == 0L) {
            this.rxStart = System.currentTimeMillis();
        }
        long bytes = XByteBuffer.getDataPackageLength(((ChannelData)msg).getDataPackageLength());
        this.mbRx += (double)bytes / 1048576.0;
        this.msgRxCnt.addAndGet(1L);
        if (this.msgRxCnt.get() % (long)this.interval == 0L) {
            this.report(this.timeTx);
        }
        super.messageReceived(msg);
    }

    @Override
    public void report(double timeTx) {
        if (log.isInfoEnabled()) {
            log.info((Object)sm.getString("throughputInterceptor.report", this.msgTxCnt, this.df.format(this.mbTx), this.df.format(this.mbAppTx), this.df.format(timeTx), this.df.format(this.mbTx / timeTx), this.df.format(this.mbAppTx / timeTx), this.msgTxErr, this.msgRxCnt, this.df.format(this.mbRx / ((double)(System.currentTimeMillis() - this.rxStart) / 1000.0)), this.df.format(this.mbRx)));
        }
    }

    @Override
    public void setInterval(int interval) {
        this.interval = interval;
    }

    @Override
    public int getInterval() {
        return this.interval;
    }

    @Override
    public double getLastCnt() {
        return this.lastCnt;
    }

    @Override
    public double getMbAppTx() {
        return this.mbAppTx;
    }

    @Override
    public double getMbRx() {
        return this.mbRx;
    }

    @Override
    public double getMbTx() {
        return this.mbTx;
    }

    @Override
    public AtomicLong getMsgRxCnt() {
        return this.msgRxCnt;
    }

    @Override
    public AtomicLong getMsgTxCnt() {
        return this.msgTxCnt;
    }

    @Override
    public AtomicLong getMsgTxErr() {
        return this.msgTxErr;
    }

    @Override
    public long getRxStart() {
        return this.rxStart;
    }

    @Override
    public double getTimeTx() {
        return this.timeTx;
    }

    @Override
    public long getTxStart() {
        return this.txStart;
    }
}

