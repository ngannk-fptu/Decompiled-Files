/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.transaction;

import org.springframework.lang.Nullable;
import org.springframework.transaction.StaticTransactionDefinition;

public interface TransactionDefinition {
    public static final int PROPAGATION_REQUIRED = 0;
    public static final int PROPAGATION_SUPPORTS = 1;
    public static final int PROPAGATION_MANDATORY = 2;
    public static final int PROPAGATION_REQUIRES_NEW = 3;
    public static final int PROPAGATION_NOT_SUPPORTED = 4;
    public static final int PROPAGATION_NEVER = 5;
    public static final int PROPAGATION_NESTED = 6;
    public static final int ISOLATION_DEFAULT = -1;
    public static final int ISOLATION_READ_UNCOMMITTED = 1;
    public static final int ISOLATION_READ_COMMITTED = 2;
    public static final int ISOLATION_REPEATABLE_READ = 4;
    public static final int ISOLATION_SERIALIZABLE = 8;
    public static final int TIMEOUT_DEFAULT = -1;

    default public int getPropagationBehavior() {
        return 0;
    }

    default public int getIsolationLevel() {
        return -1;
    }

    default public int getTimeout() {
        return -1;
    }

    default public boolean isReadOnly() {
        return false;
    }

    @Nullable
    default public String getName() {
        return null;
    }

    public static TransactionDefinition withDefaults() {
        return StaticTransactionDefinition.INSTANCE;
    }
}

