/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.raft.impl.state;

import com.hazelcast.core.Endpoint;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;

public class RaftGroupMembers {
    private final long index;
    private final Collection<Endpoint> members;
    private final Collection<Endpoint> remoteMembers;

    RaftGroupMembers(long index, Collection<Endpoint> endpoints, Endpoint localEndpoint) {
        this.index = index;
        this.members = Collections.unmodifiableSet(new LinkedHashSet<Endpoint>(endpoints));
        LinkedHashSet<Endpoint> remoteMembers = new LinkedHashSet<Endpoint>(endpoints);
        remoteMembers.remove(localEndpoint);
        this.remoteMembers = Collections.unmodifiableSet(remoteMembers);
    }

    public long index() {
        return this.index;
    }

    public Collection<Endpoint> members() {
        return this.members;
    }

    public Collection<Endpoint> remoteMembers() {
        return this.remoteMembers;
    }

    public int memberCount() {
        return this.members.size();
    }

    public int majority() {
        return this.members.size() / 2 + 1;
    }

    public boolean isKnownMember(Endpoint endpoint) {
        return this.members.contains(endpoint);
    }

    public String toString() {
        return "RaftGroupMembers{index=" + this.index + ", members=" + this.members + '}';
    }
}

