/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util;

import com.hazelcast.util.EmptyStatement;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;

public final class OperatingSystemMXBeanSupport {
    static final String COM_HAZELCAST_FREE_PHYSICAL_MEMORY_SIZE_DISABLED = "hazelcast.os.free.physical.memory.disabled";
    public static volatile boolean GET_FREE_PHYSICAL_MEMORY_SIZE_DISABLED = Boolean.getBoolean("hazelcast.os.free.physical.memory.disabled");
    private static final OperatingSystemMXBean OPERATING_SYSTEM_MX_BEAN = ManagementFactory.getOperatingSystemMXBean();
    private static final double PERCENTAGE_MULTIPLIER = 100.0;

    private OperatingSystemMXBeanSupport() {
    }

    static void reload() {
        GET_FREE_PHYSICAL_MEMORY_SIZE_DISABLED = Boolean.getBoolean(COM_HAZELCAST_FREE_PHYSICAL_MEMORY_SIZE_DISABLED);
    }

    public static long readLongAttribute(String attributeName, long defaultValue) {
        try {
            String methodName = "get" + attributeName;
            if (GET_FREE_PHYSICAL_MEMORY_SIZE_DISABLED && methodName.equals("getFreePhysicalMemorySize")) {
                return defaultValue;
            }
            OperatingSystemMXBean systemMXBean = OPERATING_SYSTEM_MX_BEAN;
            Method method = systemMXBean.getClass().getMethod(methodName, new Class[0]);
            try {
                method.setAccessible(true);
            }
            catch (Exception e) {
                return defaultValue;
            }
            Object value = method.invoke((Object)systemMXBean, new Object[0]);
            if (value == null) {
                return defaultValue;
            }
            if (value instanceof Long) {
                return (Long)value;
            }
            if (value instanceof Double) {
                double v = (Double)value;
                return Math.round(v * 100.0);
            }
            if (value instanceof Number) {
                return ((Number)value).longValue();
            }
        }
        catch (RuntimeException re) {
            throw re;
        }
        catch (Exception ignored) {
            EmptyStatement.ignore(ignored);
        }
        return defaultValue;
    }

    public static double getSystemLoadAverage() {
        return OPERATING_SYSTEM_MX_BEAN.getSystemLoadAverage();
    }
}

