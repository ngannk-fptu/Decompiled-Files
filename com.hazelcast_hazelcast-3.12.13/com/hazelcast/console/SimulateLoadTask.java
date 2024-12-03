/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.console;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.nio.serialization.BinaryInterface;
import java.io.Serializable;
import java.util.concurrent.Callable;

@BinaryInterface
public final class SimulateLoadTask
implements Callable,
Serializable,
HazelcastInstanceAware {
    private static final long serialVersionUID = 1L;
    private static final long ONE_THOUSAND = 1000L;
    private final int delay;
    private final int taskId;
    private final String latchId;
    private transient HazelcastInstance hz;

    public SimulateLoadTask(int delay, int taskId, String latchId) {
        this.delay = delay;
        this.taskId = taskId;
        this.latchId = latchId;
    }

    @Override
    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        this.hz = hazelcastInstance;
    }

    public Object call() throws Exception {
        try {
            Thread.sleep((long)this.delay * 1000L);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
        this.hz.getCountDownLatch(this.latchId).countDown();
        System.out.println("Finished task: " + this.taskId);
        return null;
    }
}

