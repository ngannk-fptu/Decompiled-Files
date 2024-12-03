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
public class MembershipEvent
extends EventObject {
    public static final int MEMBER_ADDED = 1;
    public static final int MEMBER_REMOVED = 2;
    public static final int MEMBER_ATTRIBUTE_CHANGED = 5;
    private static final long serialVersionUID = -2010865371829087371L;
    private final Member member;
    private final int eventType;
    private final Set<Member> members;

    public MembershipEvent(Cluster cluster, Member member, int eventType, Set<Member> members) {
        super(cluster);
        this.member = member;
        this.eventType = eventType;
        this.members = members;
    }

    public Set<Member> getMembers() {
        return this.members;
    }

    public Cluster getCluster() {
        return (Cluster)this.getSource();
    }

    public int getEventType() {
        return this.eventType;
    }

    public Member getMember() {
        return this.member;
    }

    @Override
    public String toString() {
        String type;
        switch (this.eventType) {
            case 1: {
                type = "added";
                break;
            }
            case 2: {
                type = "removed";
                break;
            }
            case 5: {
                type = "attributed_changes";
                break;
            }
            default: {
                throw new IllegalStateException();
            }
        }
        return String.format("MembershipEvent {member=%s,type=%s}", this.member, type);
    }
}

