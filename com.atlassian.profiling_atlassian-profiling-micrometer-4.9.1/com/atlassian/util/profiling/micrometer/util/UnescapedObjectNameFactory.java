/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.codahale.metrics.jmx.ObjectNameFactory
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.util.profiling.micrometer.util;

import com.codahale.metrics.jmx.ObjectNameFactory;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnescapedObjectNameFactory
implements ObjectNameFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(UnescapedObjectNameFactory.class);

    public ObjectName createName(String type, String domain, String name) {
        try {
            return new ObjectName(domain + ":" + name);
        }
        catch (MalformedObjectNameException e) {
            LOGGER.warn("Unable to register {} {}", new Object[]{type, name, e});
            throw new RuntimeException(e);
        }
    }
}

