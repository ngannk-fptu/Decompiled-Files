/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.vcache.internal.RequestContext
 */
package com.atlassian.confluence.impl.vcache;

import com.atlassian.vcache.internal.RequestContext;
import java.util.function.Predicate;

public class CollectMetricsPredicate
implements Predicate<RequestContext> {
    private static final double LOGGING_FREQUENCY = Double.parseDouble(System.getProperty("confluence.vcache.metricsLogging.freq", "0"));

    @Override
    public boolean test(RequestContext requestContext) {
        return LOGGING_FREQUENCY == 1.0 || LOGGING_FREQUENCY != 0.0 && LOGGING_FREQUENCY >= Math.random();
    }
}

