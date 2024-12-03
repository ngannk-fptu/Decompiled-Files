/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.operation;

import com.hazelcast.internal.management.ThreadDumpGenerator;
import com.hazelcast.spi.AbstractLocalOperation;

public class ThreadDumpOperation
extends AbstractLocalOperation {
    private boolean dumpDeadlocks;
    private String result;

    public ThreadDumpOperation(boolean dumpDeadlocks) {
        this.dumpDeadlocks = dumpDeadlocks;
    }

    @Override
    public void run() throws Exception {
        this.result = this.dumpDeadlocks ? ThreadDumpGenerator.dumpDeadlocks() : ThreadDumpGenerator.dumpAllThreads();
    }

    @Override
    public Object getResponse() {
        return this.result;
    }
}

