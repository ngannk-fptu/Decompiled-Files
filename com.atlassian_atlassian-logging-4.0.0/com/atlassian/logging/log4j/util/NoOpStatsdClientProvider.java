/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.timgroup.statsd.NoOpStatsDClient
 *  com.timgroup.statsd.StatsDClient
 */
package com.atlassian.logging.log4j.util;

import com.atlassian.logging.log4j.util.StatsdClientProvider;
import com.timgroup.statsd.NoOpStatsDClient;
import com.timgroup.statsd.StatsDClient;

public class NoOpStatsdClientProvider
implements StatsdClientProvider {
    @Override
    public StatsDClient getClient() {
        return new NoOpStatsDClient();
    }
}

