/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map;

import com.hazelcast.core.HazelcastException;

public class QueryResultSizeExceededException
extends HazelcastException {
    public QueryResultSizeExceededException(String message) {
        super(message);
    }

    public QueryResultSizeExceededException() {
        super("This exception has been thrown to prevent an OOME on this Hazelcast instance. An OOME might occur when a query collects large data sets from the whole cluster, e.g. by calling IMap.values(), IMap.keySet() or IMap.entrySet(). See GroupProperty.QUERY_RESULT_SIZE_LIMIT for further details.");
    }

    public QueryResultSizeExceededException(int maxResultLimit, String optionalMessage) {
        super(String.format("This exception has been thrown to prevent an OOME on this Hazelcast instance. An OOME might occur when a query collects large data sets from the whole cluster, e.g. by calling IMap.values(), IMap.keySet() or IMap.entrySet(). See GroupProperty.QUERY_RESULT_SIZE_LIMIT for further details. The configured query result size limit is %d items.%s", maxResultLimit, optionalMessage));
    }
}

