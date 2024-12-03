/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.instrumentation.operations.registry;

import com.atlassian.instrumentation.operations.OpCounter;
import com.atlassian.instrumentation.operations.OpSnapshot;
import com.atlassian.instrumentation.operations.registry.OpFinderFilter;
import com.atlassian.instrumentation.operations.registry.OpRegistry;
import com.atlassian.instrumentation.operations.registry.OpSnapshotComparator;
import com.atlassian.instrumentation.utils.dbc.Assertions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class OpFinder {
    public static final OpFinderFilter ALL = new OpFinderFilter(){

        @Override
        public boolean filter(OpSnapshot opSnapshot) {
            return true;
        }
    };
    private final OpRegistry opRegistry;

    public OpFinder(OpRegistry opRegistry) {
        Assertions.notNull("opRegistry", opRegistry);
        this.opRegistry = opRegistry;
    }

    public List find() {
        return this.find(ALL, OpSnapshotComparator.BY_DEFAULT);
    }

    public List find(Comparator comparator) {
        Assertions.notNull("comparator", comparator);
        return this.find(null, comparator);
    }

    public List<OpSnapshot> find(OpFinderFilter filter, Comparator comparator) {
        if (filter == null) {
            filter = ALL;
        }
        if (comparator == null) {
            comparator = OpSnapshotComparator.BY_DEFAULT;
        }
        ConcurrentHashMap<String, OpCounter> map = this.opRegistry.mapOfOpCounters;
        ArrayList<OpSnapshot> snapshotList = new ArrayList<OpSnapshot>(map.size());
        for (OpCounter opCounter : map.values()) {
            OpSnapshot opSnapshot = opCounter.snapshot();
            boolean add = filter.filter(opSnapshot);
            if (!add) continue;
            snapshotList.add(opSnapshot);
        }
        Collections.sort(snapshotList, comparator);
        return snapshotList;
    }
}

