/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.timgroup.statsd.StatsDClient
 */
package com.atlassian.logging.log4j.util;

import com.timgroup.statsd.StatsDClient;

public interface StatsdClientProvider {
    public StatsDClient getClient();
}

