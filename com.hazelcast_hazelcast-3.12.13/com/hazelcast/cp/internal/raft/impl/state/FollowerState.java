/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.raft.impl.state;

import com.hazelcast.util.Clock;

public class FollowerState {
    private static final int MAX_BACKOFF_ROUND = 20;
    private long matchIndex;
    private long nextIndex;
    private int backoffRound;
    private int nextBackoffPower;
    private long appendRequestAckTimestamp;

    FollowerState(long matchIndex, long nextIndex) {
        this.matchIndex = matchIndex;
        this.nextIndex = nextIndex;
        this.appendRequestAckTimestamp = Clock.currentTimeMillis();
    }

    public long matchIndex() {
        return this.matchIndex;
    }

    public void matchIndex(long matchIndex) {
        this.matchIndex = matchIndex;
    }

    public long nextIndex() {
        return this.nextIndex;
    }

    public void nextIndex(long nextIndex) {
        this.nextIndex = nextIndex;
    }

    public boolean isAppendRequestBackoffSet() {
        return this.backoffRound > 0;
    }

    public void setAppendRequestBackoff() {
        this.backoffRound = this.nextBackoffRound();
        ++this.nextBackoffPower;
    }

    private int nextBackoffRound() {
        return Math.min(1 << this.nextBackoffPower, 20);
    }

    public void setMaxAppendRequestBackoff() {
        this.backoffRound = 20;
    }

    public boolean completeAppendRequestBackoffRound() {
        return --this.backoffRound == 0;
    }

    public void appendRequestAckReceived() {
        this.backoffRound = 0;
        this.nextBackoffPower = 0;
        this.appendRequestAckTimestamp = Clock.currentTimeMillis();
    }

    public long appendRequestAckTimestamp() {
        return this.appendRequestAckTimestamp;
    }

    public String toString() {
        return "FollowerState{matchIndex=" + this.matchIndex + ", nextIndex=" + this.nextIndex + ", backoffRound=" + this.backoffRound + ", nextBackoffPower=" + this.nextBackoffPower + ", appendRequestAckTime=" + this.appendRequestAckTimestamp + '}';
    }
}

