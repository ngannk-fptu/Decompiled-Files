/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.slf4j.MDC
 *  org.slf4j.spi.LocationAwareLogger
 */
package org.jboss.logging;

import java.util.Collections;
import java.util.Map;
import org.jboss.logging.AbstractLoggerProvider;
import org.jboss.logging.Logger;
import org.jboss.logging.LoggerProvider;
import org.jboss.logging.Slf4jLocationAwareLogger;
import org.jboss.logging.Slf4jLogger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.spi.LocationAwareLogger;

final class Slf4jLoggerProvider
extends AbstractLoggerProvider
implements LoggerProvider {
    Slf4jLoggerProvider() {
    }

    @Override
    public Logger getLogger(String name) {
        org.slf4j.Logger l = LoggerFactory.getLogger((String)name);
        try {
            return new Slf4jLocationAwareLogger(name, (LocationAwareLogger)l);
        }
        catch (Throwable throwable) {
            return new Slf4jLogger(name, l);
        }
    }

    @Override
    public void clearMdc() {
        MDC.clear();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object putMdc(String key, Object value) {
        try {
            String string = MDC.get((String)key);
            return string;
        }
        finally {
            if (value == null) {
                MDC.remove((String)key);
            } else {
                MDC.put((String)key, (String)String.valueOf(value));
            }
        }
    }

    @Override
    public Object getMdc(String key) {
        return MDC.get((String)key);
    }

    @Override
    public void removeMdc(String key) {
        MDC.remove((String)key);
    }

    @Override
    public Map<String, Object> getMdcMap() {
        Map map = MDC.getCopyOfContextMap();
        return map == null ? Collections.emptyMap() : map;
    }
}

