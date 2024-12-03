/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.core;

import com.hazelcast.core.Cluster;
import com.hazelcast.core.Member;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.EventObject;
import java.util.Set;

@SuppressFBWarnings(value={"SE_BAD_FIELD"})
public class InitialMembershipEvent
extends EventObject {
    private static final long serialVersionUID = -2010865371829087371L;
    private final Set<Member> members;

    public InitialMembershipEvent(Cluster cluster, Set<Member> members) {
        super(cluster);
        this.members = members;
    }

    public Set<Member> getMembers() {
        return this.members;
    }

    public Cluster getCluster() {
        return (Cluster)this.getSource();
    }

    @Override
    public String toString() {
        return "MembershipInitializeEvent {" + this.members + "}";
    }
}

