/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.catalina.tribes.Member
 */
package org.apache.catalina.ha.deploy;

import org.apache.catalina.ha.ClusterMessage;
import org.apache.catalina.tribes.Member;

public class UndeployMessage
implements ClusterMessage {
    private static final long serialVersionUID = 2L;
    private Member address;
    private long timestamp;
    private String uniqueId;
    private final String contextName;

    public UndeployMessage(Member address, long timestamp, String uniqueId, String contextName) {
        this.address = address;
        this.timestamp = timestamp;
        this.uniqueId = uniqueId;
        this.contextName = contextName;
    }

    @Override
    public Member getAddress() {
        return this.address;
    }

    @Override
    public void setAddress(Member address) {
        this.address = address;
    }

    @Override
    public long getTimestamp() {
        return this.timestamp;
    }

    @Override
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String getUniqueId() {
        return this.uniqueId;
    }

    public String getContextName() {
        return this.contextName;
    }
}

