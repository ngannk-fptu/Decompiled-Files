/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.ascii.memcache;

import com.hazelcast.internal.ascii.AbstractTextCommand;
import com.hazelcast.internal.ascii.TextCommandConstants;
import com.hazelcast.internal.ascii.memcache.Stats;
import com.hazelcast.nio.IOUtil;
import com.hazelcast.util.StringUtil;
import java.nio.ByteBuffer;

public class StatsCommand
extends AbstractTextCommand {
    static final byte[] STAT = StringUtil.stringToBytes("STAT ");
    static final byte[] UPTIME = StringUtil.stringToBytes("uptime ");
    static final byte[] BYTES = StringUtil.stringToBytes("bytes ");
    static final byte[] CMD_SET = StringUtil.stringToBytes("cmd_set ");
    static final byte[] CMD_GET = StringUtil.stringToBytes("cmd_get ");
    static final byte[] CMD_TOUCH = StringUtil.stringToBytes("cmd_touch ");
    static final byte[] THREADS = StringUtil.stringToBytes("threads ");
    static final byte[] WAITING_REQUESTS = StringUtil.stringToBytes("waiting_requests ");
    static final byte[] GET_HITS = StringUtil.stringToBytes("get_hits ");
    static final byte[] GET_MISSES = StringUtil.stringToBytes("get_misses ");
    static final byte[] DELETE_HITS = StringUtil.stringToBytes("delete_hits ");
    static final byte[] DELETE_MISSES = StringUtil.stringToBytes("delete_misses ");
    static final byte[] INCR_HITS = StringUtil.stringToBytes("incr_hits ");
    static final byte[] INCR_MISSES = StringUtil.stringToBytes("incr_misses ");
    static final byte[] DECR_HITS = StringUtil.stringToBytes("decr_hits ");
    static final byte[] DECR_MISSES = StringUtil.stringToBytes("decr_misses ");
    static final byte[] CURR_CONNECTIONS = StringUtil.stringToBytes("curr_connections ");
    static final byte[] TOTAL_CONNECTIONS = StringUtil.stringToBytes("total_connections ");
    private static final int CAPACITY = 1000;
    ByteBuffer response;

    public StatsCommand() {
        super(TextCommandConstants.TextCommandType.STATS);
    }

    @Override
    public boolean readFrom(ByteBuffer src) {
        return true;
    }

    public void setResponse(Stats stats) {
        this.response = ByteBuffer.allocate(1000);
        this.putInt(UPTIME, stats.getUptime());
        this.putInt(THREADS, stats.getThreads());
        this.putInt(WAITING_REQUESTS, stats.getWaitingRequests());
        this.putInt(CURR_CONNECTIONS, stats.getCurrConnections());
        this.putInt(TOTAL_CONNECTIONS, stats.getTotalConnections());
        this.putLong(BYTES, stats.getBytes());
        this.putLong(CMD_GET, stats.getCmdGet());
        this.putLong(CMD_SET, stats.getCmdSet());
        this.putLong(CMD_TOUCH, stats.getCmdTouch());
        this.putLong(GET_HITS, stats.getGetHits());
        this.putLong(GET_MISSES, stats.getGetMisses());
        this.putLong(DELETE_HITS, stats.getDeleteHits());
        this.putLong(DELETE_MISSES, stats.getDeleteMisses());
        this.putLong(INCR_HITS, stats.getIncrHits());
        this.putLong(INCR_MISSES, stats.getIncrMisses());
        this.putLong(DECR_HITS, stats.getDecrHits());
        this.putLong(DECR_MISSES, stats.getDecrMisses());
        this.response.put(TextCommandConstants.END);
        this.response.flip();
    }

    private void putInt(byte[] name, int value) {
        this.response.put(STAT);
        this.response.put(name);
        this.response.put(StringUtil.stringToBytes(String.valueOf(value)));
        this.response.put(TextCommandConstants.RETURN);
    }

    private void putLong(byte[] name, long value) {
        this.response.put(STAT);
        this.response.put(name);
        this.response.put(StringUtil.stringToBytes(String.valueOf(value)));
        this.response.put(TextCommandConstants.RETURN);
    }

    @Override
    public boolean writeTo(ByteBuffer dst) {
        if (this.response == null) {
            this.response = ByteBuffer.allocate(0);
        }
        IOUtil.copyToHeapBuffer(this.response, dst);
        return !this.response.hasRemaining();
    }

    @Override
    public String toString() {
        return "StatsCommand{}" + super.toString();
    }
}

