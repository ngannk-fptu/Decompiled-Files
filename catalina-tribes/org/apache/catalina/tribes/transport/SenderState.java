/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.tribes.transport;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.catalina.tribes.Member;

public class SenderState {
    public static final int READY = 0;
    public static final int SUSPECT = 1;
    public static final int FAILING = 2;
    protected static final ConcurrentMap<Member, SenderState> memberStates = new ConcurrentHashMap<Member, SenderState>();
    private volatile int state = 0;

    public static SenderState getSenderState(Member member) {
        return SenderState.getSenderState(member, true);
    }

    public static SenderState getSenderState(Member member, boolean create) {
        SenderState current;
        SenderState state = (SenderState)memberStates.get(member);
        if (state == null && create && (current = memberStates.putIfAbsent(member, state = new SenderState())) != null) {
            state = current;
        }
        return state;
    }

    public static void removeSenderState(Member member) {
        memberStates.remove(member);
    }

    private SenderState() {
        this(0);
    }

    private SenderState(int state) {
        this.state = state;
    }

    public boolean isSuspect() {
        return this.state == 1 || this.state == 2;
    }

    public void setSuspect() {
        this.state = 1;
    }

    public boolean isReady() {
        return this.state == 0;
    }

    public void setReady() {
        this.state = 0;
    }

    public boolean isFailing() {
        return this.state == 2;
    }

    public void setFailing() {
        this.state = 2;
    }
}

