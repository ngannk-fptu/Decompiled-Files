/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.Enumeration;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import org.apfloat.ApfloatConfigurationException;
import org.apfloat.ApfloatMath;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.ConcurrentWeakHashMap;
import org.apfloat.spi.BuilderFactory;
import org.apfloat.spi.FilenameGenerator;
import org.apfloat.spi.Util;

public class ApfloatContext
implements Cloneable {
    public static final String BUILDER_FACTORY = "builderFactory";
    public static final String DEFAULT_RADIX = "defaultRadix";
    public static final String MAX_MEMORY_BLOCK_SIZE = "maxMemoryBlockSize";
    public static final String CACHE_L1_SIZE = "cacheL1Size";
    public static final String CACHE_L2_SIZE = "cacheL2Size";
    public static final String CACHE_BURST = "cacheBurst";
    public static final String MEMORY_THRESHOLD = "memoryThreshold";
    @Deprecated
    public static final String MEMORY_TRESHOLD = "memoryTreshold";
    public static final String SHARED_MEMORY_TRESHOLD = "sharedMemoryTreshold";
    public static final String BLOCK_SIZE = "blockSize";
    public static final String NUMBER_OF_PROCESSORS = "numberOfProcessors";
    public static final String FILE_PATH = "filePath";
    public static final String FILE_INITIAL_VALUE = "fileInitialValue";
    public static final String FILE_SUFFIX = "fileSuffix";
    public static final String CLEANUP_AT_EXIT = "cleanupAtExit";
    private static ApfloatContext globalContext;
    private static Map<Thread, ApfloatContext> threadContexts;
    private static Properties defaultProperties;
    private static ExecutorService defaultExecutorService;
    private volatile BuilderFactory builderFactory;
    private volatile FilenameGenerator filenameGenerator;
    private volatile int defaultRadix;
    private volatile long maxMemoryBlockSize;
    private volatile int cacheL1Size;
    private volatile int cacheL2Size;
    private volatile int cacheBurst;
    private volatile long memoryThreshold;
    private volatile long sharedMemoryTreshold;
    private volatile int blockSize;
    private volatile int numberOfProcessors;
    private volatile CleanupThread cleanupThread;
    private volatile Properties properties;
    private volatile Object sharedMemoryLock = new Object();
    private volatile ExecutorService executorService = defaultExecutorService;
    private volatile ConcurrentHashMap<String, Object> attributes = new ConcurrentHashMap();

    public ApfloatContext(Properties properties) throws ApfloatConfigurationException {
        this.properties = (Properties)defaultProperties.clone();
        this.properties.putAll((Map<?, ?>)properties);
        this.setProperties(this.properties);
    }

    public static ApfloatContext getContext() {
        ApfloatContext ctx = ApfloatContext.getThreadContext();
        if (ctx == null) {
            ctx = ApfloatContext.getGlobalContext();
        }
        return ctx;
    }

    public static ApfloatContext getGlobalContext() {
        return globalContext;
    }

    public static ApfloatContext getThreadContext() {
        if (threadContexts.isEmpty()) {
            return null;
        }
        return ApfloatContext.getThreadContext(Thread.currentThread());
    }

    public static ApfloatContext getThreadContext(Thread thread) {
        return threadContexts.get(thread);
    }

    public static void setThreadContext(ApfloatContext threadContext) {
        ApfloatContext.setThreadContext(threadContext, Thread.currentThread());
    }

    public static void setThreadContext(ApfloatContext threadContext, Thread thread) {
        threadContexts.put(thread, threadContext);
    }

    public static void removeThreadContext() {
        ApfloatContext.removeThreadContext(Thread.currentThread());
    }

    public static void removeThreadContext(Thread thread) {
        threadContexts.remove(thread);
    }

    public static void clearThreadContexts() {
        threadContexts.clear();
    }

    public BuilderFactory getBuilderFactory() {
        return this.builderFactory;
    }

    public void setBuilderFactory(BuilderFactory builderFactory) {
        this.properties.setProperty(BUILDER_FACTORY, builderFactory.getClass().getName());
        this.builderFactory = builderFactory;
        if (this.cleanupThread != null) {
            this.cleanupThread.setBuilderFactory(builderFactory);
        }
    }

    public FilenameGenerator getFilenameGenerator() {
        return this.filenameGenerator;
    }

    public void setFilenameGenerator(FilenameGenerator filenameGenerator) {
        this.properties.setProperty(FILE_PATH, filenameGenerator.getPath());
        this.properties.setProperty(FILE_INITIAL_VALUE, String.valueOf(filenameGenerator.getInitialValue()));
        this.properties.setProperty(FILE_SUFFIX, filenameGenerator.getSuffix());
        this.filenameGenerator = filenameGenerator;
    }

    public int getDefaultRadix() {
        return this.defaultRadix;
    }

    public void setDefaultRadix(int radix) {
        radix = Math.min(Math.max(radix, 2), 36);
        this.properties.setProperty(DEFAULT_RADIX, String.valueOf(radix));
        this.defaultRadix = radix;
    }

    public long getMaxMemoryBlockSize() {
        return this.maxMemoryBlockSize;
    }

    public void setMaxMemoryBlockSize(long maxMemoryBlockSize) {
        maxMemoryBlockSize = Util.round23down(Math.max(maxMemoryBlockSize, 65536L));
        this.properties.setProperty(MAX_MEMORY_BLOCK_SIZE, String.valueOf(maxMemoryBlockSize));
        this.maxMemoryBlockSize = maxMemoryBlockSize;
    }

    public int getCacheL1Size() {
        return this.cacheL1Size;
    }

    public void setCacheL1Size(int cacheL1Size) {
        cacheL1Size = Util.round2down(Math.max(cacheL1Size, 512));
        this.properties.setProperty(CACHE_L1_SIZE, String.valueOf(cacheL1Size));
        this.cacheL1Size = cacheL1Size;
    }

    public int getCacheL2Size() {
        return this.cacheL2Size;
    }

    public void setCacheL2Size(int cacheL2Size) {
        cacheL2Size = Util.round2down(Math.max(cacheL2Size, 2048));
        this.properties.setProperty(CACHE_L2_SIZE, String.valueOf(cacheL2Size));
        this.cacheL2Size = cacheL2Size;
    }

    public int getCacheBurst() {
        return this.cacheBurst;
    }

    public void setCacheBurst(int cacheBurst) {
        cacheBurst = Util.round2down(Math.max(cacheBurst, 8));
        this.properties.setProperty(CACHE_BURST, String.valueOf(cacheBurst));
        this.cacheBurst = cacheBurst;
    }

    @Deprecated
    public int getMemoryTreshold() {
        return (int)Math.min(Integer.MAX_VALUE, this.getMemoryThreshold());
    }

    @Deprecated
    public void setMemoryTreshold(int memoryThreshold) {
        this.setMemoryThreshold(memoryThreshold);
    }

    public long getMemoryThreshold() {
        return this.memoryThreshold;
    }

    public void setMemoryThreshold(long memoryThreshold) {
        memoryThreshold = Math.max(memoryThreshold, 128L);
        this.properties.setProperty(MEMORY_TRESHOLD, String.valueOf(memoryThreshold));
        this.properties.setProperty(MEMORY_THRESHOLD, String.valueOf(memoryThreshold));
        this.memoryThreshold = memoryThreshold;
    }

    public long getSharedMemoryTreshold() {
        return this.sharedMemoryTreshold;
    }

    public void setSharedMemoryTreshold(long sharedMemoryTreshold) {
        sharedMemoryTreshold = Math.max(sharedMemoryTreshold, 128L);
        this.properties.setProperty(SHARED_MEMORY_TRESHOLD, String.valueOf(sharedMemoryTreshold));
        this.sharedMemoryTreshold = sharedMemoryTreshold;
    }

    public int getBlockSize() {
        return this.blockSize;
    }

    public void setBlockSize(int blockSize) {
        blockSize = Util.round2down(Math.max(blockSize, 128));
        this.properties.setProperty(BLOCK_SIZE, String.valueOf(blockSize));
        this.blockSize = blockSize;
    }

    public int getNumberOfProcessors() {
        return this.numberOfProcessors;
    }

    public void setNumberOfProcessors(int numberOfProcessors) {
        numberOfProcessors = Math.max(numberOfProcessors, 1);
        this.properties.setProperty(NUMBER_OF_PROCESSORS, String.valueOf(numberOfProcessors));
        this.numberOfProcessors = numberOfProcessors;
    }

    public boolean getCleanupAtExit() {
        return this.cleanupThread != null;
    }

    public void setCleanupAtExit(boolean cleanupAtExit) {
        this.properties.setProperty(CLEANUP_AT_EXIT, String.valueOf(cleanupAtExit));
        if (cleanupAtExit && this.cleanupThread == null) {
            this.cleanupThread = new CleanupThread();
            this.cleanupThread.setBuilderFactory(this.builderFactory);
            Runtime.getRuntime().addShutdownHook(this.cleanupThread);
        } else if (!cleanupAtExit && this.cleanupThread != null) {
            Runtime.getRuntime().removeShutdownHook(this.cleanupThread);
            this.cleanupThread = null;
        }
    }

    public String getProperty(String propertyName) {
        return this.properties.getProperty(propertyName);
    }

    public String getProperty(String propertyName, String defaultValue) {
        return this.properties.getProperty(propertyName, defaultValue);
    }

    public void setProperty(String propertyName, String propertyValue) throws ApfloatConfigurationException {
        try {
            if (propertyName.equals(BUILDER_FACTORY)) {
                this.setBuilderFactory((BuilderFactory)Class.forName(propertyValue).getDeclaredConstructor(new Class[0]).newInstance(new Object[0]));
            } else if (propertyName.equals(DEFAULT_RADIX)) {
                this.setDefaultRadix(Integer.parseInt(propertyValue));
            } else if (propertyName.equals(MAX_MEMORY_BLOCK_SIZE)) {
                this.setMaxMemoryBlockSize(Long.parseLong(propertyValue));
            } else if (propertyName.equals(CACHE_L1_SIZE)) {
                this.setCacheL1Size(Integer.parseInt(propertyValue));
            } else if (propertyName.equals(CACHE_L2_SIZE)) {
                this.setCacheL2Size(Integer.parseInt(propertyValue));
            } else if (propertyName.equals(CACHE_BURST)) {
                this.setCacheBurst(Integer.parseInt(propertyValue));
            } else if (propertyName.equals(MEMORY_TRESHOLD) || propertyName.equals(MEMORY_THRESHOLD)) {
                this.setMemoryThreshold(Long.parseLong(propertyValue));
            } else if (propertyName.equals(SHARED_MEMORY_TRESHOLD)) {
                this.setSharedMemoryTreshold(Long.parseLong(propertyValue));
            } else if (propertyName.equals(BLOCK_SIZE)) {
                this.setBlockSize(Integer.parseInt(propertyValue));
            } else if (propertyName.equals(NUMBER_OF_PROCESSORS)) {
                this.setNumberOfProcessors(Integer.parseInt(propertyValue));
            } else if (propertyName.equals(FILE_PATH)) {
                this.setFilenameGenerator(new FilenameGenerator(propertyValue, this.getProperty(FILE_INITIAL_VALUE), this.getProperty(FILE_SUFFIX)));
            } else if (propertyName.equals(FILE_INITIAL_VALUE)) {
                this.setFilenameGenerator(new FilenameGenerator(this.getProperty(FILE_PATH), propertyValue, this.getProperty(FILE_SUFFIX)));
            } else if (propertyName.equals(FILE_SUFFIX)) {
                this.setFilenameGenerator(new FilenameGenerator(this.getProperty(FILE_PATH), this.getProperty(FILE_INITIAL_VALUE), propertyValue));
            } else if (propertyName.equals(CLEANUP_AT_EXIT)) {
                this.setCleanupAtExit(Boolean.parseBoolean(propertyValue));
            } else {
                this.properties.setProperty(propertyName, propertyValue);
            }
        }
        catch (Exception e) {
            throw new ApfloatConfigurationException("Error setting property \"" + propertyName + "\" to value \"" + propertyValue + '\"', e);
        }
    }

    public Properties getProperties() {
        return (Properties)this.properties.clone();
    }

    public Object getSharedMemoryLock() {
        return this.sharedMemoryLock;
    }

    public void setSharedMemoryLock(Object lock) {
        this.sharedMemoryLock = lock;
    }

    public ExecutorService getExecutorService() {
        return this.executorService;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public void wait(Future<?> future) {
        this.getBuilderFactory().getExecutionBuilder().createExecution().wait(future);
    }

    public Object getAttribute(String name) {
        return this.attributes.get(name);
    }

    public Object setAttribute(String name, Object value) {
        return this.attributes.put(name, value);
    }

    public Object removeAttribute(String name) {
        return this.attributes.remove(name);
    }

    public Enumeration<String> getAttributeNames() {
        return this.attributes.keys();
    }

    public static Properties loadProperties() throws ApfloatRuntimeException {
        Properties properties = new Properties();
        try {
            ResourceBundle resourceBundle = ResourceBundle.getBundle("apfloat");
            Enumeration<String> keys = resourceBundle.getKeys();
            while (keys.hasMoreElements()) {
                String key = keys.nextElement();
                properties.setProperty(key, resourceBundle.getString(key));
            }
        }
        catch (MissingResourceException missingResourceException) {
            // empty catch block
        }
        return properties;
    }

    public static ExecutorService getDefaultExecutorService() {
        ForkJoinPool forkJoinPool;
        int numberOfThreads = Math.max(1, ApfloatContext.getContext().getNumberOfProcessors() - 1);
        try {
            forkJoinPool = new ForkJoinPool(numberOfThreads);
        }
        catch (SecurityException se) {
            forkJoinPool = ForkJoinPool.commonPool();
        }
        return forkJoinPool;
    }

    public void setProperties(Properties properties) throws ApfloatConfigurationException {
        for (String key : properties.stringPropertyNames()) {
            this.setProperty(key, properties.getProperty(key));
        }
    }

    public Object clone() {
        try {
            ApfloatContext ctx = (ApfloatContext)super.clone();
            ctx.properties = (Properties)ctx.properties.clone();
            ctx.attributes = new ConcurrentHashMap<String, Object>(ctx.attributes);
            return ctx;
        }
        catch (CloneNotSupportedException cnse) {
            throw new InternalError();
        }
    }

    private static Properties loadSystemOverrides(Properties properties) {
        for (String propertyName : properties.stringPropertyNames()) {
            String propertyValue = properties.getProperty(propertyName);
            try {
                propertyValue = System.getProperty("apfloat." + propertyName, propertyValue);
            }
            catch (SecurityException securityException) {
                // empty catch block
            }
            properties.setProperty(propertyName, propertyValue);
        }
        return properties;
    }

    static {
        long totalMemory;
        threadContexts = new ConcurrentWeakHashMap<Thread, ApfloatContext>();
        defaultProperties = new Properties();
        try {
            MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
            MemoryUsage memoryUsage = memoryBean.getHeapMemoryUsage();
            totalMemory = Math.max(memoryUsage.getCommitted(), memoryUsage.getMax());
        }
        catch (NoClassDefFoundError ncdfe) {
            totalMemory = Runtime.getRuntime().maxMemory();
        }
        long maxMemoryBlockSize = Util.round23down(totalMemory / 5L * 4L);
        int numberOfProcessors = Runtime.getRuntime().availableProcessors();
        long memoryThreshold = Math.max(maxMemoryBlockSize >> 10, 65536L);
        int blockSize = Util.round2down((int)Math.min(memoryThreshold, Integer.MAX_VALUE));
        defaultProperties.setProperty(BUILDER_FACTORY, "org.apfloat.internal.LongBuilderFactory");
        defaultProperties.setProperty(DEFAULT_RADIX, "10");
        defaultProperties.setProperty(MAX_MEMORY_BLOCK_SIZE, String.valueOf(maxMemoryBlockSize));
        defaultProperties.setProperty(CACHE_L1_SIZE, "8192");
        defaultProperties.setProperty(CACHE_L2_SIZE, "262144");
        defaultProperties.setProperty(CACHE_BURST, "32");
        defaultProperties.setProperty(MEMORY_THRESHOLD, String.valueOf(memoryThreshold));
        defaultProperties.setProperty(SHARED_MEMORY_TRESHOLD, String.valueOf(maxMemoryBlockSize / (long)numberOfProcessors / 32L));
        defaultProperties.setProperty(BLOCK_SIZE, String.valueOf(blockSize));
        defaultProperties.setProperty(NUMBER_OF_PROCESSORS, String.valueOf(numberOfProcessors));
        defaultProperties.setProperty(FILE_PATH, "");
        defaultProperties.setProperty(FILE_INITIAL_VALUE, "0");
        defaultProperties.setProperty(FILE_SUFFIX, ".ap");
        defaultProperties.setProperty(CLEANUP_AT_EXIT, "true");
        ApfloatContext.loadSystemOverrides(defaultProperties);
        globalContext = new ApfloatContext(ApfloatContext.loadSystemOverrides(ApfloatContext.loadProperties()));
        defaultExecutorService = ApfloatContext.getDefaultExecutorService();
        globalContext.setExecutorService(defaultExecutorService);
    }

    private static class CleanupThread
    extends Thread {
        private BuilderFactory builderFactory;

        public CleanupThread() {
            super("apfloat shutdown clean-up thread");
        }

        @Override
        public void run() {
            ApfloatMath.cleanUp();
            System.gc();
            System.gc();
            System.runFinalization();
            this.builderFactory.shutdown();
        }

        public void setBuilderFactory(BuilderFactory builderFactory) {
            this.builderFactory = builderFactory;
        }
    }
}

