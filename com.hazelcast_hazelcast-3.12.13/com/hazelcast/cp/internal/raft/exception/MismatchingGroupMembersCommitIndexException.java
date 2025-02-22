/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.raft.exception;

import com.hazelcast.core.Endpoint;
import com.hazelcast.cp.exception.CPSubsystemException;
import java.util.Collection;

public class MismatchingGroupMembersCommitIndexException
extends CPSubsystemException {
    private static final long serialVersionUID = -109570074579015635L;
    private final long commitIndex;
    private final Collection<Endpoint> members;

    public MismatchingGroupMembersCommitIndexException(long commitIndex, Collection<Endpoint> members) {
        super("commit index: " + commitIndex + " members: " + members, (Endpoint)null);
        this.commitIndex = commitIndex;
        this.members = members;
    }

    public long getCommitIndex() {
        return this.commitIndex;
    }

    public Collection<Endpoint> getMembers() {
        return this.members;
    }
}

