/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.instrumentation.operations.registry;

import com.atlassian.instrumentation.operations.OpCounter;
import com.atlassian.instrumentation.operations.OpSnapshot;
import com.atlassian.instrumentation.operations.OpTimer;
import com.atlassian.instrumentation.operations.SimpleOpTimer;
import com.atlassian.instrumentation.utils.dbc.Assertions;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class OpRegistry {
    final ConcurrentHashMap<String, OpCounter> mapOfOpCounters = new ConcurrentHashMap();

    public OpCounter put(OpCounter opCounter) {
        Assertions.notNull("opCounter", opCounter);
        return this.mapOfOpCounters.put(opCounter.getName(), opCounter);
    }

    public OpCounter get(String opName) {
        Assertions.notNull("opName", opName);
        return this.mapOfOpCounters.get(opName);
    }

    public OpTimer pullTimer(String name) {
        final OpRegistry that = this;
        return new SimpleOpTimer(name){

            public void onEndCalled(OpSnapshot opSnapshot) {
                that.add(opSnapshot);
            }
        };
    }

    public OpCounter pull(String opName) {
        Assertions.notNull("opName", opName);
        OpCounter possiblyNeeded = new OpCounter(opName);
        OpCounter prevValue = this.mapOfOpCounters.putIfAbsent(opName, possiblyNeeded);
        if (prevValue == null) {
            prevValue = possiblyNeeded;
        }
        return prevValue;
    }

    public OpCounter add(OpCounter srcOpCounter) {
        Assertions.notNull("srcOpCounter", srcOpCounter);
        return this.add(srcOpCounter.snapshot());
    }

    public OpCounter add(OpSnapshot srcOpSnapshot) {
        Assertions.notNull("srcOpSnapshot", srcOpSnapshot);
        OpCounter targetOpCounter = this.pull(srcOpSnapshot.getName());
        targetOpCounter.add(srcOpSnapshot);
        return targetOpCounter;
    }

    public void add(OpRegistry srcOpRegistry) {
        List<OpSnapshot> copy = srcOpRegistry.snapshot();
        for (OpSnapshot opSnapshot : copy) {
            this.add(opSnapshot);
        }
    }

    public void add(List<OpSnapshot> opSnapshotList) {
        Assertions.notNull("opSnapshotList", opSnapshotList);
        for (OpSnapshot opSnapshot : opSnapshotList) {
            this.add(opSnapshot);
        }
    }

    public List<OpSnapshot> snapshot() {
        ArrayList<OpSnapshot> snapshotList = new ArrayList<OpSnapshot>(this.mapOfOpCounters.size());
        for (OpCounter opCounter : this.mapOfOpCounters.values()) {
            snapshotList.add(opCounter.snapshot());
        }
        return snapshotList;
    }

    public List<OpSnapshot> snapshotAndClear() {
        List<OpSnapshot> snapshot = this.snapshot();
        this.mapOfOpCounters.clear();
        return snapshot;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString()).append(" ").append(this.mapOfOpCounters.toString());
        return sb.toString();
    }
}

