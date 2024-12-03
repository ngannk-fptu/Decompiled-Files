/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.core.instrument.MeterRegistry
 *  io.micrometer.core.instrument.Tag
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.hazelcast.micrometer;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import java.util.Set;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;
import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class JmxBinder {
    private static final Logger log = LoggerFactory.getLogger(JmxBinder.class);
    private final MBeanServer mbeanServer;
    private final MeterRegistry meterRegistry;

    JmxBinder(MBeanServer mbeanServer, MeterRegistry meterRegistry) {
        this.mbeanServer = mbeanServer;
        this.meterRegistry = meterRegistry;
    }

    JmxBinder bind(String typeName, String ... attributeNames) {
        ObjectName objectNamePattern = JmxBinder.getObjectNamePattern(typeName);
        Set<ObjectName> objectNames = this.mbeanServer.queryNames(objectNamePattern, null);
        if (objectNames.isEmpty()) {
            log.warn("No objects found for pattern {}", (Object)objectNamePattern);
        }
        for (ObjectName objectName : objectNames) {
            for (String attrName : attributeNames) {
                String meterName = String.format("%s.%s", typeName, attrName);
                log.info("Registering gauge for {} attribute {}", (Object)objectName, (Object)attrName);
                this.meterRegistry.gauge(meterName, JmxBinder.extractTags(objectName), (Object)attrName, this.attributeValue(objectName));
            }
        }
        return this;
    }

    private static ObjectName getObjectNamePattern(String typeName) {
        try {
            return new ObjectName(String.format("com.hazelcast:type=%s,*", typeName));
        }
        catch (MalformedObjectNameException e) {
            throw new RuntimeException(e);
        }
    }

    private static Set<Tag> extractTags(ObjectName objectName) {
        return objectName.getKeyPropertyList().entrySet().stream().map(entry -> Tag.of((String)((String)entry.getKey()), (String)((String)entry.getValue()))).collect(Collectors.toSet());
    }

    private ToDoubleFunction<String> attributeValue(ObjectName objectName) {
        return attrName -> {
            try {
                log.trace("Querying for value of attribute '{}' from {}", attrName, (Object)objectName);
                return ((Number)this.mbeanServer.getAttribute(objectName, (String)attrName)).doubleValue();
            }
            catch (JMException ex) {
                log.warn("Failed to get value of attribute '{}' from {}", new Object[]{attrName, objectName, ex});
                return Double.NaN;
            }
        };
    }
}

