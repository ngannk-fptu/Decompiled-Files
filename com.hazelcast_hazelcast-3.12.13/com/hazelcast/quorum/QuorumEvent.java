/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.quorum;

import com.hazelcast.core.Member;
import java.util.Collection;
import java.util.EventObject;

public class QuorumEvent
extends EventObject {
    private final int threshold;
    private final Collection<Member> currentMembers;
    private final boolean presence;

    public QuorumEvent(Object source, int threshold, Collection<Member> currentMembers, boolean presence) {
        super(source);
        this.threshold = threshold;
        this.currentMembers = currentMembers;
        this.presence = presence;
    }

    public int getThreshold() {
        return this.threshold;
    }

    public Collection<Member> getCurrentMembers() {
        return this.currentMembers;
    }

    public boolean isPresent() {
        return this.presence;
    }

    @Override
    public String toString() {
        return "QuorumEvent{threshold=" + this.threshold + ", currentMembers=" + this.currentMembers + ", presence=" + this.presence + '}';
    }
}

