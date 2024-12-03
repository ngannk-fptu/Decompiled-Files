/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.amazonaws.metrics;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.jmx.spi.SdkMBeanRegistry;
import com.amazonaws.metrics.MetricCollector;
import com.amazonaws.metrics.MetricType;
import com.amazonaws.metrics.RequestMetricCollector;
import com.amazonaws.metrics.ServiceMetricCollector;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.regions.Regions;
import com.amazonaws.util.AWSRequestMetrics;
import com.amazonaws.util.AWSServiceMetrics;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public enum AwsSdkMetrics {

    private static final Log log = LogFactory.getLog(AwsSdkMetrics.class);
    public static final String DEFAULT_METRIC_NAMESPACE = "AWSSDK/Java";
    private static final String MBEAN_OBJECT_NAME = "com.amazonaws.management:type=" + AwsSdkMetrics.class.getSimpleName();
    private static volatile String registeredAdminMbeanName;
    public static final String USE_SINGLE_METRIC_NAMESPACE = "useSingleMetricNamespace";
    public static final String EXCLUDE_MACHINE_METRICS = "excludeMachineMetrics";
    public static final String INCLUDE_PER_HOST_METRICS = "includePerHostMetrics";
    public static final String AWS_CREDENTIAL_PROPERTIES_FILE = "credentialFile";
    @Deprecated
    public static final String AWS_CREDENTAIL_PROPERTIES_FILE = "credentialFile";
    public static final String CLOUDWATCH_REGION = "cloudwatchRegion";
    public static final String METRIC_QUEUE_SIZE = "metricQueueSize";
    public static final String QUEUE_POLL_TIMEOUT_MILLI = "getQueuePollTimeoutMilli";
    public static final String METRIC_NAME_SPACE = "metricNameSpace";
    public static final String JVM_METRIC_NAME = "jvmMetricName";
    public static final String HOST_METRIC_NAME = "hostMetricName";
    private static final String DEFAULT_METRIC_COLLECTOR_FACTORY = "com.amazonaws.metrics.internal.cloudwatch.DefaultMetricCollectorFactory";
    private static final String ENABLE_HTTP_SOCKET_READ_METRIC = "enableHttpSocketReadMetric";
    private static final boolean defaultMetricsEnabled;
    private static volatile AWSCredentialsProvider credentialProvider;
    private static volatile boolean machineMetricsExcluded;
    private static volatile boolean perHostMetricsIncluded;
    private static volatile boolean httpSocketReadMetricEnabled;
    private static volatile Region region;
    private static volatile Integer metricQueueSize;
    private static volatile Long queuePollTimeoutMilli;
    private static volatile String metricNameSpace;
    private static volatile String credentialFile;
    private static volatile String jvmMetricName;
    private static volatile String hostMetricName;
    private static volatile boolean singleMetricNamespace;
    private static final MetricRegistry registry;
    private static volatile MetricCollector mc;
    private static boolean dirtyEnabling;

    public static boolean isMetricAdminMBeanRegistered() {
        SdkMBeanRegistry registry = SdkMBeanRegistry.Factory.getMBeanRegistry();
        return registeredAdminMbeanName != null && registry.isMBeanRegistered(registeredAdminMbeanName);
    }

    public static String getRegisteredAdminMbeanName() {
        return registeredAdminMbeanName;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean registerMetricAdminMBean() {
        SdkMBeanRegistry registry = SdkMBeanRegistry.Factory.getMBeanRegistry();
        Class<AwsSdkMetrics> clazz = AwsSdkMetrics.class;
        synchronized (AwsSdkMetrics.class) {
            if (registeredAdminMbeanName != null) {
                // ** MonitorExit[var1_1] (shouldn't be in output)
                return false;
            }
            boolean registered = registry.registerMetricAdminMBean(MBEAN_OBJECT_NAME);
            if (registered) {
                registeredAdminMbeanName = MBEAN_OBJECT_NAME;
            } else {
                String mbeanName = MBEAN_OBJECT_NAME;
                int count = 0;
                while (registry.isMBeanRegistered(mbeanName)) {
                    mbeanName = MBEAN_OBJECT_NAME + "/" + ++count;
                }
                registered = registry.registerMetricAdminMBean(mbeanName);
                if (registered) {
                    registeredAdminMbeanName = mbeanName;
                }
            }
            if (registered) {
                log.debug((Object)("Admin mbean registered under " + registeredAdminMbeanName));
            }
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return registered;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean unregisterMetricAdminMBean() {
        SdkMBeanRegistry registry = SdkMBeanRegistry.Factory.getMBeanRegistry();
        Class<AwsSdkMetrics> clazz = AwsSdkMetrics.class;
        synchronized (AwsSdkMetrics.class) {
            if (registeredAdminMbeanName == null) {
                // ** MonitorExit[var1_1] (shouldn't be in output)
                return true;
            }
            boolean success = registry.unregisterMBean(registeredAdminMbeanName);
            if (success) {
                registeredAdminMbeanName = null;
            }
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return success;
        }
    }

    public static <T extends RequestMetricCollector> T getRequestMetricCollector() {
        if (mc == null && AwsSdkMetrics.isDefaultMetricsEnabled()) {
            AwsSdkMetrics.enableDefaultMetrics();
        }
        RequestMetricCollector t = mc == null ? RequestMetricCollector.NONE : mc.getRequestMetricCollector();
        return (T)t;
    }

    public static <T extends ServiceMetricCollector> T getServiceMetricCollector() {
        if (mc == null && AwsSdkMetrics.isDefaultMetricsEnabled()) {
            AwsSdkMetrics.enableDefaultMetrics();
        }
        ServiceMetricCollector t = mc == null ? ServiceMetricCollector.NONE : mc.getServiceMetricCollector();
        return (T)t;
    }

    static MetricCollector getInternalMetricCollector() {
        return mc;
    }

    public static <T extends MetricCollector> T getMetricCollector() {
        if (mc == null && AwsSdkMetrics.isDefaultMetricsEnabled()) {
            AwsSdkMetrics.enableDefaultMetrics();
        }
        MetricCollector t = mc == null ? MetricCollector.NONE : mc;
        return (T)t;
    }

    public static synchronized void setMetricCollector(MetricCollector mc) {
        MetricCollector old = AwsSdkMetrics.mc;
        AwsSdkMetrics.mc = mc;
        if (old != null) {
            old.stop();
        }
    }

    public static void setMachineMetricsExcluded(boolean excludeMachineMetrics) {
        machineMetricsExcluded = excludeMachineMetrics;
    }

    public static void setPerHostMetricsIncluded(boolean includePerHostMetrics) {
        perHostMetricsIncluded = includePerHostMetrics;
    }

    public static void enableHttpSocketReadMetric() {
        httpSocketReadMetricEnabled = true;
    }

    public static boolean isDefaultMetricsEnabled() {
        return defaultMetricsEnabled;
    }

    public static boolean isSingleMetricNamespace() {
        return singleMetricNamespace;
    }

    public static void setSingleMetricNamespace(boolean singleMetricNamespace) {
        AwsSdkMetrics.singleMetricNamespace = singleMetricNamespace;
    }

    public static boolean isMetricsEnabled() {
        MetricCollector mc = AwsSdkMetrics.mc;
        return mc != null && mc.isEnabled();
    }

    public static boolean isMachineMetricExcluded() {
        return machineMetricsExcluded;
    }

    public static boolean isPerHostMetricIncluded() {
        return perHostMetricsIncluded;
    }

    public static boolean isPerHostMetricEnabled() {
        if (perHostMetricsIncluded) {
            return true;
        }
        String host = hostMetricName;
        host = host == null ? "" : host.trim();
        return host.length() > 0;
    }

    public static boolean isHttpSocketReadMetricEnabled() {
        return httpSocketReadMetricEnabled;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static synchronized boolean enableDefaultMetrics() {
        if (mc == null || !mc.isEnabled()) {
            if (dirtyEnabling) {
                throw new IllegalStateException("Reentrancy is not allowed");
            }
            dirtyEnabling = true;
            try {
                Class<?> c = Class.forName(DEFAULT_METRIC_COLLECTOR_FACTORY);
                MetricCollector.Factory f = (MetricCollector.Factory)c.newInstance();
                MetricCollector instance = f.getInstance();
                if (instance != null) {
                    AwsSdkMetrics.setMetricCollector(instance);
                    boolean bl = true;
                    return bl;
                }
            }
            catch (Exception e) {
                LogFactory.getLog(AwsSdkMetrics.class).warn((Object)"Failed to enable the default metrics", (Throwable)e);
            }
            finally {
                dirtyEnabling = false;
            }
        }
        return false;
    }

    public static void disableMetrics() {
        AwsSdkMetrics.setMetricCollector(MetricCollector.NONE);
    }

    public static boolean add(MetricType type) {
        return type == null ? false : registry.addMetricType(type);
    }

    public static <T extends MetricType> boolean addAll(Collection<T> types) {
        return types == null || types.size() == 0 ? false : registry.addMetricTypes(types);
    }

    public static <T extends MetricType> void set(Collection<T> types) {
        registry.setMetricTypes(types);
    }

    public static boolean remove(MetricType type) {
        return type == null ? false : registry.removeMetricType(type);
    }

    public static Set<MetricType> getPredefinedMetrics() {
        return registry.predefinedMetrics();
    }

    public static AWSCredentialsProvider getCredentialProvider() {
        StackTraceElement[] e = Thread.currentThread().getStackTrace();
        for (int i = 0; i < e.length; ++i) {
            if (!e[i].getClassName().equals(DEFAULT_METRIC_COLLECTOR_FACTORY)) continue;
            return credentialProvider;
        }
        SecurityException ex = new SecurityException();
        LogFactory.getLog(AwsSdkMetrics.class).warn((Object)"Illegal attempt to access the credential provider", (Throwable)ex);
        throw ex;
    }

    public static synchronized void setCredentialProvider(AWSCredentialsProvider provider) {
        credentialProvider = provider;
    }

    public static Regions getRegion() throws IllegalArgumentException {
        return Regions.fromName(region.getName());
    }

    public static String getRegionName() {
        return region == null ? null : region.getName();
    }

    public static void setRegion(Regions region) {
        AwsSdkMetrics.region = RegionUtils.getRegion(region.getName());
    }

    public static void setRegion(String region) {
        AwsSdkMetrics.region = RegionUtils.getRegion(region);
    }

    @Deprecated
    public static String getCredentailFile() {
        return credentialFile;
    }

    public static String getCredentialFile() {
        return credentialFile;
    }

    public static void setCredentialFile(String filepath) throws FileNotFoundException, IOException {
        AwsSdkMetrics.setCredentialFile0(filepath);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void setCredentialFile0(String filepath) throws FileNotFoundException, IOException {
        final PropertiesCredentials cred = new PropertiesCredentials(new File(filepath));
        Class<AwsSdkMetrics> clazz = AwsSdkMetrics.class;
        synchronized (AwsSdkMetrics.class) {
            credentialProvider = new AWSCredentialsProvider(){

                @Override
                public void refresh() {
                }

                @Override
                public AWSCredentials getCredentials() {
                    return cred;
                }
            };
            credentialFile = filepath;
            // ** MonitorExit[var2_2] (shouldn't be in output)
            return;
        }
    }

    public static Integer getMetricQueueSize() {
        return metricQueueSize;
    }

    public static void setMetricQueueSize(Integer size) {
        metricQueueSize = size;
    }

    public static Long getQueuePollTimeoutMilli() {
        return queuePollTimeoutMilli;
    }

    public static void setQueuePollTimeoutMilli(Long timeoutMilli) {
        queuePollTimeoutMilli = timeoutMilli;
    }

    public static String getMetricNameSpace() {
        return metricNameSpace;
    }

    public static void setMetricNameSpace(String metricNameSpace) {
        if (metricNameSpace == null || metricNameSpace.trim().length() == 0) {
            throw new IllegalArgumentException();
        }
        AwsSdkMetrics.metricNameSpace = metricNameSpace;
    }

    public static String getJvmMetricName() {
        return jvmMetricName;
    }

    public static void setJvmMetricName(String jvmMetricName) {
        AwsSdkMetrics.jvmMetricName = jvmMetricName;
    }

    public static String getHostMetricName() {
        return hostMetricName;
    }

    public static void setHostMetricName(String hostMetricName) {
        AwsSdkMetrics.hostMetricName = hostMetricName;
    }

    static {
        metricNameSpace = DEFAULT_METRIC_NAMESPACE;
        String defaultMetrics = System.getProperty("com.amazonaws.sdk.enableDefaultMetrics");
        boolean bl = defaultMetricsEnabled = defaultMetrics != null;
        if (defaultMetricsEnabled) {
            String[] values = defaultMetrics.split(",");
            boolean excludeMachineMetrics = false;
            boolean includePerHostMetrics = false;
            boolean useSingleMetricNamespace = false;
            boolean enableHttpSocketReadMetric = false;
            for (String s : values) {
                String part = s.trim();
                if (!excludeMachineMetrics && EXCLUDE_MACHINE_METRICS.equals(part)) {
                    excludeMachineMetrics = true;
                    continue;
                }
                if (!includePerHostMetrics && INCLUDE_PER_HOST_METRICS.equals(part)) {
                    includePerHostMetrics = true;
                    continue;
                }
                if (!useSingleMetricNamespace && USE_SINGLE_METRIC_NAMESPACE.equals(part)) {
                    useSingleMetricNamespace = true;
                    continue;
                }
                if (!enableHttpSocketReadMetric && ENABLE_HTTP_SOCKET_READ_METRIC.equals(part)) {
                    enableHttpSocketReadMetric = true;
                    continue;
                }
                String[] pair = part.split("=");
                if (pair.length != 2) continue;
                String key = pair[0].trim();
                String value = pair[1].trim();
                try {
                    Number i;
                    if ("credentialFile".equals(key)) {
                        AwsSdkMetrics.setCredentialFile0(value);
                        continue;
                    }
                    if (CLOUDWATCH_REGION.equals(key)) {
                        region = RegionUtils.getRegion(value);
                        continue;
                    }
                    if (METRIC_QUEUE_SIZE.equals(key)) {
                        i = Integer.valueOf(value);
                        if ((Integer)i < 1) {
                            throw new IllegalArgumentException("metricQueueSize must be at least 1");
                        }
                        metricQueueSize = i;
                        continue;
                    }
                    if (QUEUE_POLL_TIMEOUT_MILLI.equals(key)) {
                        i = Long.valueOf(value);
                        if (((Long)i).intValue() < 1000) {
                            throw new IllegalArgumentException("getQueuePollTimeoutMilli must be at least 1000");
                        }
                        queuePollTimeoutMilli = i;
                        continue;
                    }
                    if (METRIC_NAME_SPACE.equals(key)) {
                        metricNameSpace = value;
                        continue;
                    }
                    if (JVM_METRIC_NAME.equals(key)) {
                        jvmMetricName = value;
                        continue;
                    }
                    if (HOST_METRIC_NAME.equals(key)) {
                        hostMetricName = value;
                        continue;
                    }
                    LogFactory.getLog(AwsSdkMetrics.class).debug((Object)("Ignoring unrecognized parameter: " + part));
                }
                catch (Exception e) {
                    LogFactory.getLog(AwsSdkMetrics.class).debug((Object)"Ignoring failure", (Throwable)e);
                }
            }
            machineMetricsExcluded = excludeMachineMetrics;
            perHostMetricsIncluded = includePerHostMetrics;
            singleMetricNamespace = useSingleMetricNamespace;
            httpSocketReadMetricEnabled = enableHttpSocketReadMetric;
        }
        registry = new MetricRegistry();
        try {
            AwsSdkMetrics.registerMetricAdminMBean();
        }
        catch (Exception ex) {
            LogFactory.getLog(AwsSdkMetrics.class).warn((Object)"", (Throwable)ex);
        }
    }

    private static class MetricRegistry {
        private final Set<MetricType> metricTypes = new HashSet<MetricType>();
        private volatile Set<MetricType> readOnly;

        MetricRegistry() {
            this.metricTypes.add(AWSRequestMetrics.Field.ClientExecuteTime);
            this.metricTypes.add(AWSRequestMetrics.Field.Exception);
            this.metricTypes.add(AWSRequestMetrics.Field.ThrottleException);
            this.metricTypes.add(AWSRequestMetrics.Field.HttpClientRetryCount);
            this.metricTypes.add(AWSRequestMetrics.Field.HttpRequestTime);
            this.metricTypes.add(AWSRequestMetrics.Field.RequestCount);
            this.metricTypes.add(AWSRequestMetrics.Field.RetryCount);
            this.metricTypes.add(AWSRequestMetrics.Field.RetryCapacityConsumed);
            this.metricTypes.add(AWSRequestMetrics.Field.ThrottledRetryCount);
            this.metricTypes.add(AWSRequestMetrics.Field.HttpClientSendRequestTime);
            this.metricTypes.add(AWSRequestMetrics.Field.HttpClientReceiveResponseTime);
            this.metricTypes.add(AWSRequestMetrics.Field.HttpSocketReadTime);
            this.metricTypes.add(AWSRequestMetrics.Field.HttpClientPoolAvailableCount);
            this.metricTypes.add(AWSRequestMetrics.Field.HttpClientPoolLeasedCount);
            this.metricTypes.add(AWSRequestMetrics.Field.HttpClientPoolPendingCount);
            this.metricTypes.add(AWSServiceMetrics.HttpClientGetConnectionTime);
            this.syncReadOnly();
        }

        private void syncReadOnly() {
            this.readOnly = Collections.unmodifiableSet(new HashSet<MetricType>(this.metricTypes));
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public boolean addMetricType(MetricType type) {
            Set<MetricType> set = this.metricTypes;
            synchronized (set) {
                boolean added = this.metricTypes.add(type);
                if (added) {
                    this.syncReadOnly();
                }
                return added;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public <T extends MetricType> boolean addMetricTypes(Collection<T> types) {
            Set<MetricType> set = this.metricTypes;
            synchronized (set) {
                boolean added = this.metricTypes.addAll(types);
                if (added) {
                    this.syncReadOnly();
                }
                return added;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public <T extends MetricType> void setMetricTypes(Collection<T> types) {
            Set<MetricType> set = this.metricTypes;
            synchronized (set) {
                if (types == null || types.size() == 0) {
                    if (this.metricTypes.size() == 0) {
                        return;
                    }
                    if (types == null) {
                        types = Collections.emptyList();
                    }
                }
                this.metricTypes.clear();
                if (!this.addMetricTypes(types)) {
                    this.syncReadOnly();
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public boolean removeMetricType(MetricType type) {
            Set<MetricType> set = this.metricTypes;
            synchronized (set) {
                boolean removed = this.metricTypes.remove(type);
                if (removed) {
                    this.syncReadOnly();
                }
                return removed;
            }
        }

        public Set<MetricType> predefinedMetrics() {
            return this.readOnly;
        }
    }
}

