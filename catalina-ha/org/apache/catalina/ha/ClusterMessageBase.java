/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.catalina.tribes.Member
 */
package org.apache.catalina.ha;

import org.apache.catalina.ha.ClusterMessage;
import org.apache.catalina.tribes.Member;

public abstract class ClusterMessageBase
implements ClusterMessage {
    private static final long serialVersionUID = 1L;
    private long timestamp;
    protected transient Member address;

    @Override
    public Member getAddress() {
        return this.address;
    }

    @Override
    public long getTimestamp() {
        return this.timestamp;
    }

    @Override
    public void setAddress(Member member) {
        this.address = member;
    }

    @Override
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

