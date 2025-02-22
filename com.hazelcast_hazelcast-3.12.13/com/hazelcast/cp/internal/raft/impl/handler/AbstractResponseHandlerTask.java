/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.raft.impl.handler;

import com.hazelcast.core.Endpoint;
import com.hazelcast.cp.internal.raft.impl.RaftNodeImpl;
import com.hazelcast.cp.internal.raft.impl.task.RaftNodeStatusAwareTask;

public abstract class AbstractResponseHandlerTask
extends RaftNodeStatusAwareTask {
    AbstractResponseHandlerTask(RaftNodeImpl raftNode) {
        super(raftNode);
    }

    @Override
    protected final void innerRun() {
        Endpoint sender = this.sender();
        if (!this.raftNode.state().isKnownMember(sender)) {
            this.logger.warning("Won't run, since " + sender + " is unknown to us");
            return;
        }
        this.handleResponse();
    }

    protected abstract void handleResponse();

    protected abstract Endpoint sender();
}

