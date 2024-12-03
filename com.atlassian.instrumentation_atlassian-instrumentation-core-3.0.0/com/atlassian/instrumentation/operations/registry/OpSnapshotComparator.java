/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.instrumentation.operations.registry;

import com.atlassian.instrumentation.operations.OpSnapshot;
import com.atlassian.instrumentation.utils.dbc.Assertions;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

public abstract class OpSnapshotComparator
implements Comparator<OpSnapshot> {
    public static final OpSnapshotComparator BY_NAME = new OpSnapshotComparator(){

        @Override
        public int compareThese(OpSnapshot opSnapshot1, OpSnapshot opSnapshot2) {
            return this.sortByName(opSnapshot1, opSnapshot2);
        }
    };
    public static final OpSnapshotComparator BY_TIME_TAKEN = new OpSnapshotComparator(){

        @Override
        public int compareThese(OpSnapshot opSnapshot1, OpSnapshot opSnapshot2) {
            return this.sortByTimeTaken(opSnapshot1, opSnapshot2);
        }
    };
    public static final OpSnapshotComparator BY_INVOCATION_COUNT = new OpSnapshotComparator(){

        @Override
        public int compareThese(OpSnapshot opSnapshot1, OpSnapshot opSnapshot2) {
            return this.sortByInvocation(opSnapshot1, opSnapshot2);
        }
    };
    public static final OpSnapshotComparator BY_RESULT_SET_SIZE = new OpSnapshotComparator(){

        @Override
        public int compareThese(OpSnapshot opSnapshot1, OpSnapshot opSnapshot2) {
            return this.sortByResultSetSize(opSnapshot1, opSnapshot2);
        }
    };
    public static final OpSnapshotComparator BY_DEFAULT = BY_TIME_TAKEN;

    @Override
    public int compare(OpSnapshot opSnapshot1, OpSnapshot opSnapshot2) {
        Assertions.notNull("opSnapshot1", opSnapshot1);
        Assertions.notNull("opSnapshot2", opSnapshot2);
        return this.compareThese(opSnapshot1, opSnapshot2);
    }

    protected abstract int compareThese(OpSnapshot var1, OpSnapshot var2);

    protected int sortByName(OpSnapshot opSnapshot1, OpSnapshot opSnapshot2) {
        double rc = opSnapshot1.getName().compareTo(opSnapshot2.getName());
        if (rc == 0.0) {
            rc = opSnapshot1.compareTo(opSnapshot2);
        }
        return rc > 0.0 ? 1 : (rc < 0.0 ? -1 : 0);
    }

    protected int sortByTimeTaken(OpSnapshot opSnapshot1, OpSnapshot opSnapshot2) {
        double rc = opSnapshot2.getElapsedTotalTime(TimeUnit.NANOSECONDS) - opSnapshot1.getElapsedTotalTime(TimeUnit.NANOSECONDS);
        if (rc == 0.0 && (rc = (double)(opSnapshot2.getInvocationCount() - opSnapshot1.getInvocationCount())) == 0.0 && (rc = (double)(opSnapshot2.getResultSetSize() - opSnapshot1.getResultSetSize())) == 0.0) {
            rc = opSnapshot1.getName().compareTo(opSnapshot2.getName());
        }
        return rc > 0.0 ? 1 : (rc < 0.0 ? -1 : 0);
    }

    protected int sortByInvocation(OpSnapshot opSnapshot1, OpSnapshot opSnapshot2) {
        double rc = opSnapshot2.getInvocationCount() - opSnapshot1.getInvocationCount();
        if (rc == 0.0 && (rc = (double)(opSnapshot2.getElapsedTotalTime(TimeUnit.NANOSECONDS) - opSnapshot1.getElapsedTotalTime(TimeUnit.NANOSECONDS))) == 0.0 && (rc = (double)(opSnapshot2.getResultSetSize() - opSnapshot1.getResultSetSize())) == 0.0) {
            rc = opSnapshot1.getName().compareTo(opSnapshot2.getName());
        }
        return rc > 0.0 ? 1 : (rc < 0.0 ? -1 : 0);
    }

    protected int sortByResultSetSize(OpSnapshot opSnapshot1, OpSnapshot opSnapshot2) {
        double rc = opSnapshot2.getResultSetSize() - opSnapshot1.getResultSetSize();
        if (rc == 0.0 && (rc = (double)(opSnapshot2.getInvocationCount() - opSnapshot1.getInvocationCount())) == 0.0 && (rc = (double)(opSnapshot2.getElapsedTotalTime(TimeUnit.NANOSECONDS) - opSnapshot1.getElapsedTotalTime(TimeUnit.NANOSECONDS))) == 0.0) {
            rc = opSnapshot1.getName().compareTo(opSnapshot2.getName());
        }
        return rc > 0.0 ? 1 : (rc < 0.0 ? -1 : 0);
    }
}

