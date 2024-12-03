/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.store;

public interface StoreOperationOutcomes {

    public static enum RemoveOutcome {
        SUCCESS;

    }

    public static enum PutOutcome {
        ADDED,
        UPDATED;

    }

    public static enum GetOutcome {
        HIT,
        MISS;

    }
}

