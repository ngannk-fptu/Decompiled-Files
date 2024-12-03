/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.util.persistence.hibernate.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BatchConfigParser {
    public static final int DEFAULT_QUERY_IN_BATCH_SIZE = 1000;
    private static final String PROPERTY_KEY = "crowd.query.in.batch.size";
    private static final Logger log = LoggerFactory.getLogger(BatchConfigParser.class);

    public int getCrowdQueryBatchSize() {
        Integer crowdQueryInBatchSize = Integer.getInteger(PROPERTY_KEY, 1000);
        if (crowdQueryInBatchSize < 0) {
            log.warn("Invalid value '{}' specified for property '{}', using default batch size: '{}'", new Object[]{crowdQueryInBatchSize, PROPERTY_KEY, 1000});
            return 1000;
        }
        return crowdQueryInBatchSize;
    }
}

