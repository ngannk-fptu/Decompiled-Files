/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.eviction;

import com.hazelcast.internal.eviction.EvictionChecker;
import com.hazelcast.util.Preconditions;

public abstract class CompositeEvictionChecker
implements EvictionChecker {
    protected final EvictionChecker[] evictionCheckers;

    protected CompositeEvictionChecker(EvictionChecker ... evictionCheckers) {
        this.evictionCheckers = evictionCheckers;
    }

    public static CompositeEvictionChecker newCompositeEvictionChecker(CompositionOperator compositionOperator, EvictionChecker ... evictionCheckers) {
        Preconditions.isNotNull(compositionOperator, "composition");
        Preconditions.isNotNull(evictionCheckers, "evictionCheckers");
        if (evictionCheckers.length == 0) {
            throw new IllegalArgumentException("EvictionCheckers cannot be empty!");
        }
        switch (compositionOperator) {
            case AND: {
                return new CompositeEvictionCheckerWithAndComposition(evictionCheckers);
            }
            case OR: {
                return new CompositeEvictionCheckerWithOrComposition(evictionCheckers);
            }
        }
        throw new IllegalArgumentException("Invalid composition operator: " + (Object)((Object)compositionOperator));
    }

    private static final class CompositeEvictionCheckerWithOrComposition
    extends CompositeEvictionChecker {
        private CompositeEvictionCheckerWithOrComposition(EvictionChecker ... evictionCheckers) {
            super(evictionCheckers);
        }

        @Override
        public boolean isEvictionRequired() {
            for (EvictionChecker evictionChecker : this.evictionCheckers) {
                if (!evictionChecker.isEvictionRequired()) continue;
                return true;
            }
            return false;
        }
    }

    private static final class CompositeEvictionCheckerWithAndComposition
    extends CompositeEvictionChecker {
        private CompositeEvictionCheckerWithAndComposition(EvictionChecker ... evictionCheckers) {
            super(evictionCheckers);
        }

        @Override
        public boolean isEvictionRequired() {
            for (EvictionChecker evictionChecker : this.evictionCheckers) {
                if (evictionChecker.isEvictionRequired()) continue;
                return false;
            }
            return true;
        }
    }

    public static enum CompositionOperator {
        AND,
        OR;

    }
}

