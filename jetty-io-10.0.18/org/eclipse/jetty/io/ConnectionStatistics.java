/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.util.annotation.ManagedAttribute
 *  org.eclipse.jetty.util.annotation.ManagedObject
 *  org.eclipse.jetty.util.annotation.ManagedOperation
 *  org.eclipse.jetty.util.component.AbstractLifeCycle
 *  org.eclipse.jetty.util.component.Dumpable
 *  org.eclipse.jetty.util.statistic.CounterStatistic
 *  org.eclipse.jetty.util.statistic.RateCounter
 *  org.eclipse.jetty.util.statistic.SampleStatistic
 */
package org.eclipse.jetty.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.annotation.ManagedOperation;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.util.component.Dumpable;
import org.eclipse.jetty.util.statistic.CounterStatistic;
import org.eclipse.jetty.util.statistic.RateCounter;
import org.eclipse.jetty.util.statistic.SampleStatistic;

@ManagedObject(value="Tracks statistics on connections")
public class ConnectionStatistics
extends AbstractLifeCycle
implements Connection.Listener,
Dumpable {
    private final Stats _stats = new Stats("total");
    private final Map<String, Stats> _statsMap = new ConcurrentHashMap<String, Stats>();

    @ManagedOperation(value="Resets the statistics", impact="ACTION")
    public void reset() {
        this._stats.reset();
        this._statsMap.clear();
    }

    protected void doStart() throws Exception {
        this.reset();
    }

    @Override
    public void onOpened(Connection connection) {
        if (!this.isStarted()) {
            return;
        }
        this.onTotalOpened(connection);
        this.onConnectionOpened(connection);
    }

    protected void onTotalOpened(Connection connection) {
        this._stats.incrementCount();
    }

    protected void onConnectionOpened(Connection connection) {
        this._statsMap.computeIfAbsent(connection.getClass().getName(), Stats::new).incrementCount();
    }

    @Override
    public void onClosed(Connection connection) {
        if (!this.isStarted()) {
            return;
        }
        this.onTotalClosed(connection);
        this.onConnectionClosed(connection);
    }

    protected void onTotalClosed(Connection connection) {
        this.onClosed(this._stats, connection);
    }

    protected void onConnectionClosed(Connection connection) {
        Stats stats = this._statsMap.get(connection.getClass().getName());
        if (stats != null) {
            this.onClosed(stats, connection);
        }
    }

    private void onClosed(Stats stats, Connection connection) {
        long messagesOut;
        long messagesIn;
        long bytesOut;
        stats.decrementCount();
        stats.recordDuration(System.currentTimeMillis() - connection.getCreatedTimeStamp());
        long bytesIn = connection.getBytesIn();
        if (bytesIn > 0L) {
            stats.recordBytesIn(bytesIn);
        }
        if ((bytesOut = connection.getBytesOut()) > 0L) {
            stats.recordBytesOut(bytesOut);
        }
        if ((messagesIn = connection.getMessagesIn()) > 0L) {
            stats.recordMessagesIn(messagesIn);
        }
        if ((messagesOut = connection.getMessagesOut()) > 0L) {
            stats.recordMessagesOut(messagesOut);
        }
    }

    @ManagedAttribute(value="Total number of bytes received by tracked connections")
    public long getReceivedBytes() {
        return this._stats.getReceivedBytes();
    }

    @ManagedAttribute(value="Total number of bytes received per second since the last invocation of this method")
    public long getReceivedBytesRate() {
        return this._stats.getReceivedBytesRate();
    }

    @ManagedAttribute(value="Total number of bytes sent by tracked connections")
    public long getSentBytes() {
        return this._stats.getSentBytes();
    }

    @ManagedAttribute(value="Total number of bytes sent per second since the last invocation of this method")
    public long getSentBytesRate() {
        return this._stats.getSentBytesRate();
    }

    @ManagedAttribute(value="The max duration of a connection in ms")
    public long getConnectionDurationMax() {
        return this._stats.getConnectionDurationMax();
    }

    @ManagedAttribute(value="The mean duration of a connection in ms")
    public double getConnectionDurationMean() {
        return this._stats.getConnectionDurationMean();
    }

    @ManagedAttribute(value="The standard deviation of the duration of a connection")
    public double getConnectionDurationStdDev() {
        return this._stats.getConnectionDurationStdDev();
    }

    @ManagedAttribute(value="The total number of connections opened")
    public long getConnectionsTotal() {
        return this._stats.getConnectionsTotal();
    }

    @ManagedAttribute(value="The current number of open connections")
    public long getConnections() {
        return this._stats.getConnections();
    }

    @ManagedAttribute(value="The max number of open connections")
    public long getConnectionsMax() {
        return this._stats.getConnectionsMax();
    }

    @ManagedAttribute(value="The total number of messages received")
    public long getReceivedMessages() {
        return this._stats.getReceivedMessages();
    }

    @ManagedAttribute(value="Total number of messages received per second since the last invocation of this method")
    public long getReceivedMessagesRate() {
        return this._stats.getReceivedMessagesRate();
    }

    @ManagedAttribute(value="The total number of messages sent")
    public long getSentMessages() {
        return this._stats.getSentMessages();
    }

    @ManagedAttribute(value="Total number of messages sent per second since the last invocation of this method")
    public long getSentMessagesRate() {
        return this._stats.getSentMessagesRate();
    }

    public Map<String, Stats> getConnectionStatisticsGroups() {
        return this._statsMap;
    }

    public void dump(Appendable out, String indent) throws IOException {
        ArrayList<Stats> children = new ArrayList<Stats>();
        children.add(this._stats);
        children.addAll(this._statsMap.values());
        Dumpable.dumpObjects((Appendable)out, (String)indent, (Object)this, (Object[])children.toArray());
    }

    public String toString() {
        return String.format("%s@%x", this.getClass().getSimpleName(), this.hashCode());
    }

    public static class Stats
    implements Dumpable {
        private final CounterStatistic _connections = new CounterStatistic();
        private final SampleStatistic _connectionsDuration = new SampleStatistic();
        private final LongAdder _bytesIn = new LongAdder();
        private final RateCounter _bytesInRate = new RateCounter();
        private final LongAdder _bytesOut = new LongAdder();
        private final RateCounter _bytesOutRate = new RateCounter();
        private final LongAdder _messagesIn = new LongAdder();
        private final RateCounter _messagesInRate = new RateCounter();
        private final LongAdder _messagesOut = new LongAdder();
        private final RateCounter _messagesOutRate = new RateCounter();
        private final String _name;

        public Stats(String name) {
            this._name = name;
        }

        public void reset() {
            this._connections.reset();
            this._connectionsDuration.reset();
            this._bytesIn.reset();
            this._bytesInRate.reset();
            this._bytesOut.reset();
            this._bytesOutRate.reset();
            this._messagesIn.reset();
            this._messagesInRate.reset();
            this._messagesOut.reset();
            this._messagesOutRate.reset();
        }

        public String getName() {
            return this._name;
        }

        public long getReceivedBytes() {
            return this._bytesIn.sum();
        }

        public long getReceivedBytesRate() {
            long rate = this._bytesInRate.getRate();
            this._bytesInRate.reset();
            return rate;
        }

        public long getSentBytes() {
            return this._bytesOut.sum();
        }

        public long getSentBytesRate() {
            long rate = this._bytesOutRate.getRate();
            this._bytesOutRate.reset();
            return rate;
        }

        public long getConnectionDurationMax() {
            return this._connectionsDuration.getMax();
        }

        public double getConnectionDurationMean() {
            return this._connectionsDuration.getMean();
        }

        public double getConnectionDurationStdDev() {
            return this._connectionsDuration.getStdDev();
        }

        public long getConnectionsTotal() {
            return this._connections.getTotal();
        }

        public long getConnections() {
            return this._connections.getCurrent();
        }

        public long getConnectionsMax() {
            return this._connections.getMax();
        }

        public long getReceivedMessages() {
            return this._messagesIn.sum();
        }

        public long getReceivedMessagesRate() {
            long rate = this._messagesInRate.getRate();
            this._messagesInRate.reset();
            return rate;
        }

        public long getSentMessages() {
            return this._messagesOut.sum();
        }

        public long getSentMessagesRate() {
            long rate = this._messagesOutRate.getRate();
            this._messagesOutRate.reset();
            return rate;
        }

        public void incrementCount() {
            this._connections.increment();
        }

        public void decrementCount() {
            this._connections.decrement();
        }

        public void recordDuration(long duration) {
            this._connectionsDuration.record(duration);
        }

        public void recordBytesIn(long bytesIn) {
            this._bytesIn.add(bytesIn);
            this._bytesInRate.add(bytesIn);
        }

        public void recordBytesOut(long bytesOut) {
            this._bytesOut.add(bytesOut);
            this._bytesOutRate.add(bytesOut);
        }

        public void recordMessagesIn(long messagesIn) {
            this._messagesIn.add(messagesIn);
            this._messagesInRate.add(messagesIn);
        }

        public void recordMessagesOut(long messagesOut) {
            this._messagesOut.add(messagesOut);
            this._messagesOutRate.add(messagesOut);
        }

        public void dump(Appendable out, String indent) throws IOException {
            Dumpable.dumpObjects((Appendable)out, (String)indent, (Object)this, (Object[])new Object[]{String.format("connections=%s", this._connections), String.format("durations=%s", this._connectionsDuration), String.format("bytes in/out=%s/%s", this.getReceivedBytes(), this.getSentBytes()), String.format("messages in/out=%s/%s", this.getReceivedMessages(), this.getSentMessages())});
        }

        public String toString() {
            return String.format("%s[%s]", this.getClass().getSimpleName(), this.getName());
        }
    }
}

