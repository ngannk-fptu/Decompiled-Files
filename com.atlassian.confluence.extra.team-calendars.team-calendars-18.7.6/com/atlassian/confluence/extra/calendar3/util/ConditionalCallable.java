/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.util;

import java.util.concurrent.Callable;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConditionalCallable<V>
implements Callable<V> {
    private static Logger logger = LoggerFactory.getLogger(ConditionalCallable.class);
    private final Supplier<Boolean> condition;
    private final Callable<V> callable;

    public ConditionalCallable(Supplier<Boolean> condition, Callable<V> callable) {
        this.condition = condition;
        this.callable = callable;
    }

    @Override
    public V call() throws Exception {
        if (this.condition == null || this.callable == null) {
            logger.debug("Skip ConditionalCallable because either `condition` or `callable` is null");
            return null;
        }
        if (!this.condition.get().booleanValue()) {
            logger.debug("Skip ConditionalCallable because `condition` does not match");
            return null;
        }
        logger.debug("Executing ConditionalCallable");
        V returnObj = this.callable.call();
        logger.debug("Executed ConditionalCallable");
        return returnObj;
    }
}

