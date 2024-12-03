/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.security.serialfilter;

import com.atlassian.security.serialfilter.DeserializationFilter;
import java.util.function.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeserializationFilterConfigurator {
    private static final Logger log = LoggerFactory.getLogger(DeserializationFilterConfigurator.class);

    public boolean configure(Predicate<Class<?>> blocklistFilter) {
        DeserializationFilter filter = new DeserializationFilter(blocklistFilter);
        filter.register();
        log.info("Global serial filter set to JDK 11 DeserializationFilter");
        return true;
    }
}

