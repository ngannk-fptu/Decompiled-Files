/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.common.properties;

import java.time.Duration;

public final class SpringLdapPoolDefaults {
    public static final int MAX_TOTAL = -1;
    public static final int MAX_TOTAL_PER_KEY = -1;
    public static final int MAX_IDLE_PER_KEY = -1;
    public static final int MIN_IDLE_PER_KEY = 0;
    public static final long MAX_WAIT = -1000L;
    public static final boolean BLOCK_WHEN_EXHAUSTED = true;
    public static final boolean TEST_ON_CREATE = false;
    public static final boolean TEST_ON_BORROW = true;
    public static final boolean TEST_ON_RETURN = false;
    public static final boolean TEST_WHILE_IDLE = false;
    public static final long EVICTION_RUN_INTERVAL_MILLIS = Duration.ofMinutes(5L).toMillis();
    public static final int TESTS_PER_EVICTION_RUN = 3;
    public static final long MIN_EVICTABLE_TIME_MILLIS = Duration.ofMinutes(5L).toMillis();
    public static final long SOFT_MIN_EVICTABLE_TIME_MILLIS = -1L;
    public static final String EVICTION_POLICY_CLASS = "org.apache.commons.pool2.impl.DefaultEvictionPolicy";
    public static final boolean FAIRNESS = false;
    public static final boolean JMX_ENABLE = true;
    public static final String JMX_NAME_PREFIX = "pool";
    public static final boolean LIFO = true;

    private SpringLdapPoolDefaults() {
    }
}

