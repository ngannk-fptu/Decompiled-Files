/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.impl.hibernate.bulk;

import com.atlassian.confluence.impl.hibernate.bulk.BulkExecutionContext;
import java.util.List;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface BulkAction<CONTEXT extends BulkExecutionContext, TARGET> {
    public static final String LOCK_PREFIX = BulkAction.class.getSimpleName();

    public @NonNull Result<CONTEXT, TARGET> process(CONTEXT var1, TARGET var2);

    public static class Result<CONTEXT extends BulkExecutionContext, TARGET> {
        private final CONTEXT context;
        private final List<TARGET> nextBatch;
        private final boolean actioned;

        public Result(CONTEXT context, List<TARGET> nextBatch, boolean actioned) {
            this.context = context;
            this.nextBatch = nextBatch;
            this.actioned = actioned;
        }

        public List<TARGET> getNextBatch() {
            return this.nextBatch;
        }

        public CONTEXT getContext() {
            return this.context;
        }

        public boolean isActioned() {
            return this.actioned;
        }
    }
}

