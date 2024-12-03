/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.slf4j.helpers.NOPLoggerFactory
 *  org.slf4j.spi.LocationAwareLogger
 */
package io.micrometer.core.util.internal.logging;

import io.micrometer.core.util.internal.logging.InternalLogger;
import io.micrometer.core.util.internal.logging.InternalLoggerFactory;
import io.micrometer.core.util.internal.logging.LocationAwareSlf4JLogger;
import io.micrometer.core.util.internal.logging.Slf4JLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.NOPLoggerFactory;
import org.slf4j.spi.LocationAwareLogger;

@Deprecated
public class Slf4JLoggerFactory
extends InternalLoggerFactory {
    public static final InternalLoggerFactory INSTANCE = new Slf4JLoggerFactory();

    private Slf4JLoggerFactory() {
        if (LoggerFactory.getILoggerFactory() instanceof NOPLoggerFactory) {
            throw new NoClassDefFoundError("NOPLoggerFactory not supported");
        }
    }

    @Override
    public InternalLogger newInstance(String name) {
        return Slf4JLoggerFactory.wrapLogger(LoggerFactory.getLogger((String)name));
    }

    static InternalLogger wrapLogger(Logger logger) {
        return logger instanceof LocationAwareLogger ? new LocationAwareSlf4JLogger((LocationAwareLogger)logger) : new Slf4JLogger(logger);
    }
}

