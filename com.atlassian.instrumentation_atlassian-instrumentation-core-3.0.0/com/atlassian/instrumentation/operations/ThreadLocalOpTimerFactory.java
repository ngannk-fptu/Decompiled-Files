/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.instrumentation.operations;

import com.atlassian.instrumentation.operations.OpSnapshot;
import com.atlassian.instrumentation.operations.OpTimer;
import com.atlassian.instrumentation.operations.SimpleOpTimer;
import com.atlassian.instrumentation.operations.ThreadOpTimerFactory;
import com.atlassian.instrumentation.operations.registry.OpRegistry;
import com.atlassian.instrumentation.utils.dbc.Assertions;
import java.util.List;

public class ThreadLocalOpTimerFactory
implements ThreadOpTimerFactory {
    private static final ThreadLocal<OpRegistry> THREAD_LOCAL = new ThreadLocal<OpRegistry>(){

        @Override
        protected OpRegistry initialValue() {
            return new OpRegistry();
        }
    };

    @Override
    public OpTimer createOpTimer(String name, boolean captureCPUCost, final OpTimer.OnEndCallback endCallback) {
        Assertions.notNull("name", name);
        Assertions.notNull("endCallback", endCallback);
        return new SimpleOpTimer(name, captureCPUCost, new OpTimer.OnEndCallback(){

            @Override
            public void onEndCalled(OpSnapshot opSnapshot) {
                ThreadLocalOpTimerFactory.this.recordSnapshot(opSnapshot);
                endCallback.onEndCalled(opSnapshot);
            }
        });
    }

    private void recordSnapshot(OpSnapshot opSnapshot) {
        this.getOpRegistry().add(opSnapshot);
    }

    @Override
    public OpRegistry getOpRegistry() {
        return THREAD_LOCAL.get();
    }

    @Override
    public List<OpSnapshot> snapshotAndClear() {
        try {
            List<OpSnapshot> list = this.getOpRegistry().snapshotAndClear();
            return list;
        }
        finally {
            THREAD_LOCAL.remove();
        }
    }
}

