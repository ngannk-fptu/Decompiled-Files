/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.raft.impl.task;

import com.hazelcast.cp.internal.raft.impl.RaftNodeImpl;
import com.hazelcast.logging.ILogger;

public abstract class RaftNodeStatusAwareTask
implements Runnable {
    protected final RaftNodeImpl raftNode;
    protected final ILogger logger;

    protected RaftNodeStatusAwareTask(RaftNodeImpl raftNode) {
        this.raftNode = raftNode;
        this.logger = raftNode.getLogger(this.getClass());
    }

    @Override
    public final void run() {
        if (this.raftNode.isTerminatedOrSteppedDown()) {
            this.logger.fine("Won't run, since raft node is terminated");
            return;
        }
        try {
            this.innerRun();
        }
        catch (Throwable e) {
            this.logger.severe(e);
        }
    }

    protected abstract void innerRun();
}

