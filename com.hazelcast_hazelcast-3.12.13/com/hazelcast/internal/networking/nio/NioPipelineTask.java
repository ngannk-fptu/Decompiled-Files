/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.networking.nio;

import com.hazelcast.internal.networking.nio.NioPipeline;

abstract class NioPipelineTask
implements Runnable {
    private final NioPipeline pipeline;

    NioPipelineTask(NioPipeline pipeline) {
        this.pipeline = pipeline;
    }

    @Override
    public final void run() {
        if (this.pipeline.owner() == Thread.currentThread()) {
            try {
                this.run0();
            }
            catch (Exception e) {
                this.pipeline.onError(e);
            }
        } else {
            this.pipeline.addTaskAndWakeup(this);
        }
    }

    protected abstract void run0() throws Exception;
}

