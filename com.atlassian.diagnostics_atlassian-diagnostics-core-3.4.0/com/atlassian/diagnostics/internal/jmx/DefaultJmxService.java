/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.diagnostics.internal.jmx;

import com.atlassian.diagnostics.internal.jmx.JmxService;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.ThreadMXBean;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.management.InstanceNotFoundException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.Query;
import javax.management.ReflectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultJmxService
implements JmxService {
    private static final Logger logger = LoggerFactory.getLogger(DefaultJmxService.class);

    @Override
    public ThreadMXBean getThreadMXBean() {
        return ManagementFactory.getThreadMXBean();
    }

    @Override
    @Nonnull
    public List<MemoryPoolMXBean> getMemoryPoolMXBeans() {
        return ManagementFactory.getMemoryPoolMXBeans();
    }

    @Override
    @Nonnull
    public List<GarbageCollectorMXBean> getGarbageCollectorMXBeans() {
        return ManagementFactory.getGarbageCollectorMXBeans();
    }

    @Override
    public boolean hasObjectName(@Nonnull String instanceOfQuery) {
        return this.getObjectName(instanceOfQuery) != null;
    }

    @Override
    @Nullable
    public <T> T getJmxAttribute(@Nonnull String instanceOfQuery, @Nonnull String attributeName) {
        List<T> attributes = this.getJmxAttributes(instanceOfQuery, new String[]{attributeName});
        return !attributes.isEmpty() ? (T)attributes.get(0) : null;
    }

    @Override
    @Nonnull
    public <T> List<T> getJmxAttributes(@Nonnull String instanceOfQuery, @Nonnull String[] attributeNames) {
        ObjectName objectName = this.getObjectName(instanceOfQuery);
        if (objectName != null) {
            try {
                return ManagementFactory.getPlatformMBeanServer().getAttributes(objectName, attributeNames).asList().stream().map(attribute -> attribute.getValue()).collect(Collectors.toList());
            }
            catch (ClassCastException | InstanceNotFoundException | ReflectionException e) {
                logger.debug("Failed to get jmxAttributes", (Throwable)e);
            }
        }
        return Collections.emptyList();
    }

    private ObjectName getObjectName(String instanceOfQuery) {
        Set<ObjectInstance> jmxObjectInstances = ManagementFactory.getPlatformMBeanServer().queryMBeans(null, Query.isInstanceOf(Query.value(instanceOfQuery)));
        return jmxObjectInstances.isEmpty() ? null : jmxObjectInstances.iterator().next().getObjectName();
    }
}

