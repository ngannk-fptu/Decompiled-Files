/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.util;

import com.hazelcast.internal.memory.impl.UnsafeUtil;
import com.hazelcast.logging.Logger;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.management.ManagementFactory;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeDataSupport;

public final class JVMUtil {
    public static final int REFERENCE_COST_IN_BYTES = JVMUtil.is32bitJVM() || JVMUtil.isCompressedOops() ? 4 : 8;

    private JVMUtil() {
    }

    static boolean is32bitJVM() {
        String architecture = System.getProperty("sun.arch.data.model");
        return architecture != null && architecture.equals("32");
    }

    static boolean isCompressedOops() {
        Boolean enabled = JVMUtil.isHotSpotCompressedOopsOrNull();
        if (enabled != null) {
            return enabled;
        }
        enabled = JVMUtil.isObjectLayoutCompressedOopsOrNull();
        if (enabled != null) {
            return enabled;
        }
        Logger.getLogger(JVMUtil.class).info("Could not determine memory cost of reference; setting to default of 4 bytes.");
        return true;
    }

    @SuppressFBWarnings(value={"NP_BOOLEAN_RETURN_NULL"})
    static Boolean isHotSpotCompressedOopsOrNull() {
        try {
            MBeanServer server = ManagementFactory.getPlatformMBeanServer();
            ObjectName mbean = new ObjectName("com.sun.management:type=HotSpotDiagnostic");
            Object[] objects = new Object[]{"UseCompressedOops"};
            String[] strings = new String[]{"java.lang.String"};
            String operation = "getVMOption";
            CompositeDataSupport compressedOopsValue = (CompositeDataSupport)server.invoke(mbean, operation, objects, strings);
            return Boolean.valueOf(compressedOopsValue.get("value").toString());
        }
        catch (Exception e) {
            Logger.getLogger(JVMUtil.class).fine("Failed to read HotSpot specific configuration: " + e.getMessage());
            return null;
        }
    }

    @SuppressFBWarnings(value={"NP_BOOLEAN_RETURN_NULL"})
    static Boolean isObjectLayoutCompressedOopsOrNull() {
        if (!UnsafeUtil.UNSAFE_AVAILABLE) {
            return null;
        }
        Integer referenceSize = ReferenceSizeEstimator.getReferenceSizeOrNull();
        if (referenceSize == null) {
            return null;
        }
        return referenceSize.intValue() != UnsafeUtil.UNSAFE.addressSize();
    }

    private static final class ReferenceSizeEstimator {
        public Object firstField;
        public Object secondField;

        private ReferenceSizeEstimator() {
        }

        static Integer getReferenceSizeOrNull() {
            Integer referenceSize = null;
            try {
                long firstFieldOffset = UnsafeUtil.UNSAFE.objectFieldOffset(ReferenceSizeEstimator.class.getField("firstField"));
                long secondFieldOffset = UnsafeUtil.UNSAFE.objectFieldOffset(ReferenceSizeEstimator.class.getField("secondField"));
                referenceSize = (int)Math.abs(secondFieldOffset - firstFieldOffset);
            }
            catch (Exception e) {
                Logger.getLogger(JVMUtil.class).fine("Could not determine cost of reference using field offsets: " + e.getMessage());
            }
            return referenceSize;
        }
    }
}

