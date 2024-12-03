/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.cluster;

public class ReIndexingScopeThreadLocal {
    private static final ThreadLocal<ReIndexingScope> indexingScope = ThreadLocal.withInitial(() -> ReIndexingScope.CLUSTER_WIDE);

    public static ReIndexingScope currentScope() {
        return indexingScope.get();
    }

    public static void withScope(ReIndexingScope scope, Runnable runnable) {
        ReIndexingScope currentScope = ReIndexingScopeThreadLocal.currentScope();
        indexingScope.set(scope);
        try {
            runnable.run();
        }
        finally {
            indexingScope.set(currentScope);
        }
    }

    public static enum ReIndexingScope {
        CLUSTER_WIDE,
        LOCALLY;

    }
}

