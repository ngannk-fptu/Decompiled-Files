/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.ehcache.sizeof.impl;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum JvmInformation {
    UNKNOWN_32_BIT(null){

        @Override
        public String getJvmDescription() {
            return "Unrecognized 32-Bit JVM";
        }

        @Override
        public int getPointerSize() {
            return 4;
        }

        @Override
        public int getJavaPointerSize() {
            return 4;
        }

        @Override
        public int getObjectAlignment() {
            return 8;
        }

        @Override
        public int getFieldOffsetAdjustment() {
            return 0;
        }

        @Override
        public int getAgentSizeOfAdjustment() {
            return 0;
        }

        @Override
        public boolean supportsAgentSizeOf() {
            return true;
        }

        @Override
        public boolean supportsUnsafeSizeOf() {
            return true;
        }

        @Override
        public boolean supportsReflectionSizeOf() {
            return true;
        }
    }
    ,
    UNKNOWN_64_BIT(UNKNOWN_32_BIT){

        @Override
        public int getPointerSize() {
            return 8;
        }

        @Override
        public int getJavaPointerSize() {
            return 8;
        }

        @Override
        public String getJvmDescription() {
            return "Unrecognized 64-Bit JVM";
        }
    }
    ,
    HOTSPOT_32_BIT(UNKNOWN_32_BIT){

        @Override
        public String getJvmDescription() {
            return "32-Bit HotSpot JVM";
        }
    }
    ,
    HOTSPOT_32_BIT_WITH_CONCURRENT_MARK_AND_SWEEP(UNKNOWN_32_BIT){

        @Override
        public int getMinimumObjectSize() {
            return 16;
        }

        @Override
        public String getJvmDescription() {
            return "32-Bit HotSpot JVM with Concurrent Mark-and-Sweep GC";
        }
    }
    ,
    HOTSPOT_64_BIT(UNKNOWN_64_BIT){

        @Override
        public String getJvmDescription() {
            return "64-Bit HotSpot JVM";
        }
    }
    ,
    HOTSPOT_64_BIT_WITH_CONCURRENT_MARK_AND_SWEEP(HOTSPOT_64_BIT){

        @Override
        public int getMinimumObjectSize() {
            return 24;
        }

        @Override
        public String getJvmDescription() {
            return "64-Bit HotSpot JVM with Concurrent Mark-and-Sweep GC";
        }
    }
    ,
    HOTSPOT_64_BIT_WITH_COMPRESSED_OOPS(HOTSPOT_64_BIT){

        @Override
        public int getJavaPointerSize() {
            return 4;
        }

        @Override
        public String getJvmDescription() {
            return "64-Bit HotSpot JVM with Compressed OOPs";
        }
    }
    ,
    HOTSPOT_64_BIT_WITH_COMPRESSED_OOPS_AND_CONCURRENT_MARK_AND_SWEEP(HOTSPOT_64_BIT_WITH_COMPRESSED_OOPS){

        @Override
        public int getMinimumObjectSize() {
            return 24;
        }

        @Override
        public String getJvmDescription() {
            return "64-Bit HotSpot JVM with Compressed OOPs and Concurrent Mark-and-Sweep GC";
        }
    }
    ,
    OPENJDK_32_BIT(HOTSPOT_32_BIT){

        @Override
        public String getJvmDescription() {
            return "32-Bit OpenJDK JVM";
        }
    }
    ,
    OPENJDK_32_BIT_WITH_CONCURRENT_MARK_AND_SWEEP(HOTSPOT_32_BIT){

        @Override
        public int getMinimumObjectSize() {
            return 16;
        }

        @Override
        public String getJvmDescription() {
            return "32-Bit OpenJDK JVM with Concurrent Mark-and-Sweep GC";
        }
    }
    ,
    OPENJDK_64_BIT(HOTSPOT_64_BIT){

        @Override
        public String getJvmDescription() {
            return "64-Bit OpenJDK JVM";
        }
    }
    ,
    OPENJDK_64_BIT_WITH_CONCURRENT_MARK_AND_SWEEP(OPENJDK_64_BIT){

        @Override
        public int getMinimumObjectSize() {
            return 24;
        }

        @Override
        public String getJvmDescription() {
            return "64-Bit OpenJDK JVM with Concurrent Mark-and-Sweep GC";
        }
    }
    ,
    OPENJDK_64_BIT_WITH_COMPRESSED_OOPS(OPENJDK_64_BIT){

        @Override
        public int getJavaPointerSize() {
            return 4;
        }

        @Override
        public String getJvmDescription() {
            return "64-Bit OpenJDK JVM with Compressed OOPs";
        }
    }
    ,
    OPENJDK_64_BIT_WITH_COMPRESSED_OOPS_AND_CONCURRENT_MARK_AND_SWEEP(OPENJDK_64_BIT_WITH_COMPRESSED_OOPS){

        @Override
        public int getMinimumObjectSize() {
            return 24;
        }

        @Override
        public String getJvmDescription() {
            return "64-Bit OpenJDK JVM with Compressed OOPs and Concurrent Mark-and-Sweep GC";
        }
    }
    ,
    IBM_32_BIT(UNKNOWN_32_BIT){

        @Override
        public String getJvmDescription() {
            return "IBM 32-Bit JVM";
        }

        @Override
        public int getObjectHeaderSize() {
            return 16;
        }

        @Override
        public boolean supportsReflectionSizeOf() {
            return false;
        }
    }
    ,
    IBM_64_BIT(UNKNOWN_64_BIT){

        @Override
        public int getObjectHeaderSize() {
            return 24;
        }

        @Override
        public boolean supportsReflectionSizeOf() {
            return false;
        }

        @Override
        public String getJvmDescription() {
            return "IBM 64-Bit JVM (with no reference compression)";
        }
    }
    ,
    IBM_64_BIT_WITH_COMPRESSED_REFS(IBM_32_BIT){

        @Override
        public int getObjectHeaderSize() {
            return 16;
        }

        @Override
        public String getJvmDescription() {
            return "IBM 64-Bit JVM with Compressed References";
        }
    };

    public static final JvmInformation CURRENT_JVM_INFORMATION;
    private static final Logger LOGGER;
    private static final long TWENTY_FIVE_GB = 0x640000000L;
    private static final long FIFTY_SEVEN_GB = 0xE40000000L;
    private JvmInformation parent;

    private JvmInformation(JvmInformation parent) {
        this.parent = parent;
    }

    public int getPointerSize() {
        return this.parent.getPointerSize();
    }

    public int getJavaPointerSize() {
        return this.parent.getJavaPointerSize();
    }

    public int getMinimumObjectSize() {
        return this.getObjectAlignment();
    }

    public int getObjectAlignment() {
        return this.parent.getObjectAlignment();
    }

    public int getObjectHeaderSize() {
        return this.getPointerSize() + this.getJavaPointerSize();
    }

    public int getFieldOffsetAdjustment() {
        return this.parent.getFieldOffsetAdjustment();
    }

    public int getAgentSizeOfAdjustment() {
        return this.parent.getAgentSizeOfAdjustment();
    }

    public boolean supportsAgentSizeOf() {
        return this.parent.supportsAgentSizeOf();
    }

    public boolean supportsUnsafeSizeOf() {
        return this.parent.supportsUnsafeSizeOf();
    }

    public boolean supportsReflectionSizeOf() {
        return this.parent.supportsReflectionSizeOf();
    }

    public abstract String getJvmDescription();

    private static JvmInformation getJvmInformation() {
        JvmInformation jif = JvmInformation.detectHotSpot();
        if (jif == null) {
            jif = JvmInformation.detectOpenJDK();
        }
        if (jif == null) {
            jif = JvmInformation.detectIBM();
        }
        if (jif == null && JvmInformation.is64Bit()) {
            jif = UNKNOWN_64_BIT;
        } else if (jif == null) {
            jif = UNKNOWN_32_BIT;
        }
        return jif;
    }

    private static JvmInformation detectHotSpot() {
        JvmInformation jif = null;
        if (JvmInformation.isHotspot()) {
            jif = JvmInformation.is64Bit() ? (JvmInformation.isHotspotCompressedOops() && JvmInformation.isHotspotConcurrentMarkSweepGC() ? HOTSPOT_64_BIT_WITH_COMPRESSED_OOPS_AND_CONCURRENT_MARK_AND_SWEEP : (JvmInformation.isHotspotCompressedOops() ? HOTSPOT_64_BIT_WITH_COMPRESSED_OOPS : (JvmInformation.isHotspotConcurrentMarkSweepGC() ? HOTSPOT_64_BIT_WITH_CONCURRENT_MARK_AND_SWEEP : HOTSPOT_64_BIT))) : (JvmInformation.isHotspotConcurrentMarkSweepGC() ? HOTSPOT_32_BIT_WITH_CONCURRENT_MARK_AND_SWEEP : HOTSPOT_32_BIT);
        }
        return jif;
    }

    private static JvmInformation detectOpenJDK() {
        JvmInformation jif = null;
        if (JvmInformation.isOpenJDK()) {
            jif = JvmInformation.is64Bit() ? (JvmInformation.isHotspotCompressedOops() && JvmInformation.isHotspotConcurrentMarkSweepGC() ? OPENJDK_64_BIT_WITH_COMPRESSED_OOPS_AND_CONCURRENT_MARK_AND_SWEEP : (JvmInformation.isHotspotCompressedOops() ? OPENJDK_64_BIT_WITH_COMPRESSED_OOPS : (JvmInformation.isHotspotConcurrentMarkSweepGC() ? OPENJDK_64_BIT_WITH_CONCURRENT_MARK_AND_SWEEP : OPENJDK_64_BIT))) : (JvmInformation.isHotspotConcurrentMarkSweepGC() ? OPENJDK_32_BIT_WITH_CONCURRENT_MARK_AND_SWEEP : OPENJDK_32_BIT);
        }
        return jif;
    }

    private static JvmInformation detectIBM() {
        JvmInformation jif = null;
        if (JvmInformation.isIBM()) {
            jif = JvmInformation.is64Bit() ? (JvmInformation.isIBMCompressedRefs() ? IBM_64_BIT_WITH_COMPRESSED_REFS : IBM_64_BIT) : IBM_32_BIT;
        }
        return jif;
    }

    private static boolean isJRockit64GBCompression() {
        if (JvmInformation.getJRockitVmArgs().contains("-XXcompressedRefs:enable=false")) {
            return false;
        }
        if (JvmInformation.getJRockitVmArgs().contains("-XXcompressedRefs:size=4GB") || JvmInformation.getJRockitVmArgs().contains("-XXcompressedRefs:size=32GB")) {
            return false;
        }
        if (JvmInformation.getJRockitVmArgs().contains("-XXcompressedRefs:size=64GB")) {
            return true;
        }
        long maxMemory = Runtime.getRuntime().maxMemory();
        return maxMemory > 0x640000000L && maxMemory <= 0xE40000000L && JvmInformation.getJRockitVmArgs().contains("-XXcompressedRefs:enable=true");
    }

    public static boolean isJRockit() {
        return System.getProperty("jrockit.version") != null || System.getProperty("java.vm.name", "").toLowerCase().contains("jrockit");
    }

    public static boolean isOSX() {
        String vendor = System.getProperty("java.vm.vendor");
        return vendor != null && vendor.startsWith("Apple");
    }

    public static boolean isHotspot() {
        return System.getProperty("java.vm.name", "").toLowerCase().contains("hotspot");
    }

    public static boolean isOpenJDK() {
        return System.getProperty("java.vm.name", "").toLowerCase().contains("openjdk");
    }

    public static boolean isIBM() {
        return System.getProperty("java.vm.name", "").contains("IBM") && System.getProperty("java.vm.vendor").contains("IBM");
    }

    private static boolean isIBMCompressedRefs() {
        return System.getProperty("com.ibm.oti.vm.bootstrap.library.path", "").contains("compressedrefs");
    }

    private static boolean isHotspotCompressedOops() {
        String value = JvmInformation.getHotSpotVmOptionValue("UseCompressedOops");
        if (value == null) {
            return false;
        }
        return Boolean.valueOf(value);
    }

    private static String getHotSpotVmOptionValue(String name) {
        try {
            MBeanServer server = ManagementFactory.getPlatformMBeanServer();
            ObjectName beanName = ObjectName.getInstance("com.sun.management:type=HotSpotDiagnostic");
            Object vmOption = server.invoke(beanName, "getVMOption", new Object[]{name}, new String[]{"java.lang.String"});
            return (String)((CompositeData)vmOption).get("value");
        }
        catch (Throwable t) {
            return null;
        }
    }

    private static String getPlatformMBeanAttribute(String beanName, String attrName) {
        try {
            MBeanServer server = ManagementFactory.getPlatformMBeanServer();
            ObjectName name = ObjectName.getInstance(beanName);
            Object attr = server.getAttribute(name, attrName);
            if (attr != null) {
                return attr.toString();
            }
            return null;
        }
        catch (Throwable t) {
            return null;
        }
    }

    private static String getJRockitVmArgs() {
        return JvmInformation.getPlatformMBeanAttribute("oracle.jrockit.management:type=PerfCounters", "java.rt.vmArgs");
    }

    private static boolean isHotspotConcurrentMarkSweepGC() {
        for (GarbageCollectorMXBean bean : ManagementFactory.getGarbageCollectorMXBeans()) {
            if (!"ConcurrentMarkSweep".equals(bean.getName())) continue;
            return true;
        }
        return false;
    }

    private static boolean is64Bit() {
        String systemProp = System.getProperty("com.ibm.vm.bitmode");
        if (systemProp != null) {
            return systemProp.equals("64");
        }
        systemProp = System.getProperty("sun.arch.data.model");
        if (systemProp != null) {
            return systemProp.equals("64");
        }
        systemProp = System.getProperty("java.vm.version");
        if (systemProp != null) {
            return systemProp.contains("_64");
        }
        return false;
    }

    static {
        LOGGER = LoggerFactory.getLogger(JvmInformation.class);
        CURRENT_JVM_INFORMATION = JvmInformation.getJvmInformation();
        LOGGER.info("Detected JVM data model settings of: " + CURRENT_JVM_INFORMATION.getJvmDescription());
    }
}

