/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache;

public interface CacheOperationOutcomes {

    public static enum RemoveElementOutcome {
        SUCCESS,
        FAILURE;

    }

    public static enum PutIfAbsentOutcome {
        SUCCESS,
        FAILURE;

    }

    public static enum ReplaceTwoArgOutcome {
        SUCCESS,
        FAILURE;

    }

    public static enum ReplaceOneArgOutcome {
        SUCCESS,
        FAILURE;

    }

    public static enum NonStopOperationOutcomes {
        SUCCESS,
        FAILURE,
        REJOIN_TIMEOUT,
        TIMEOUT;

    }

    public static enum ClusterEventOutcomes {
        OFFLINE,
        ONLINE,
        REJOINED;

    }

    public static enum ExpiredOutcome {
        SUCCESS,
        FAILURE;

    }

    public static enum EvictionOutcome {
        SUCCESS;

    }

    public static enum SearchOutcome {
        SUCCESS,
        EXCEPTION;

    }

    public static enum RemoveAllOutcome {
        IGNORED,
        COMPLETED;

    }

    public static enum PutAllOutcome {
        IGNORED,
        COMPLETED;

    }

    public static enum GetAllOutcome {
        ALL_MISS,
        ALL_HIT,
        PARTIAL;

    }

    public static enum RemoveOutcome {
        SUCCESS;

    }

    public static enum PutOutcome {
        ADDED,
        UPDATED,
        IGNORED;

    }

    public static enum GetOutcome {
        HIT,
        MISS_EXPIRED,
        MISS_NOT_FOUND;

    }
}

