/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.constructs.classloader;

import java.beans.PropertyChangeListener;
import java.io.PrintStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.Status;
import net.sf.ehcache.bootstrap.BootstrapCacheLoader;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.event.RegisteredEventListeners;
import net.sf.ehcache.exceptionhandler.CacheExceptionHandler;
import net.sf.ehcache.extension.CacheExtension;
import net.sf.ehcache.loader.CacheLoader;
import net.sf.ehcache.search.Attribute;
import net.sf.ehcache.search.Query;
import net.sf.ehcache.search.attribute.DynamicAttributesExtractor;
import net.sf.ehcache.statistics.StatisticsGateway;
import net.sf.ehcache.terracotta.TerracottaNotRunningException;
import net.sf.ehcache.transaction.manager.TransactionManagerLookup;
import net.sf.ehcache.writer.CacheWriter;
import net.sf.ehcache.writer.CacheWriterManager;
import org.terracotta.context.annotations.ContextChild;

public class ClassLoaderAwareCache
implements Ehcache {
    protected final ClassLoader classLoader;
    @ContextChild
    protected final Ehcache cache;

    public ClassLoaderAwareCache(Ehcache cache, ClassLoader classLoader) {
        this.cache = cache;
        this.classLoader = classLoader;
    }

    public static void main(String[] args) {
        PrintStream out = System.out;
        for (Method m : Ehcache.class.getMethods()) {
            int i;
            out.println("/**");
            out.println("* {@inheritDoc}");
            out.println("*/");
            out.print("public " + m.getReturnType().getSimpleName() + " " + m.getName() + "(");
            Class<?>[] params = m.getParameterTypes();
            for (int i2 = 0; i2 < params.length; ++i2) {
                out.print(params[i2].getSimpleName() + " arg" + i2);
                if (i2 >= params.length - 1) continue;
                out.print(", ");
            }
            out.print(")");
            Class<?>[] exceptions = m.getExceptionTypes();
            if (exceptions.length > 0) {
                out.print(" throws ");
            }
            for (i = 0; i < exceptions.length; ++i) {
                out.print(exceptions[i].getSimpleName());
                if (i >= exceptions.length - 1) continue;
                out.print(", ");
            }
            out.println(" {");
            out.println("    // THIS IS GENERATED CODE -- DO NOT HAND MODIFY!");
            out.println("    Thread t = Thread.currentThread();");
            out.println("    ClassLoader prev = t.getContextClassLoader();");
            out.println("    t.setContextClassLoader(this.classLoader);");
            out.println("    try {");
            out.print("        ");
            if (m.getReturnType() != Void.TYPE) {
                out.print("return ");
            }
            out.print("this.cache." + m.getName() + "(");
            for (i = 0; i < params.length; ++i) {
                out.print("arg" + i);
                if (i >= params.length - 1) continue;
                out.print(", ");
            }
            out.println(");");
            out.println("    } finally {");
            out.println("        t.setContextClassLoader(prev);");
            out.println("    }");
            out.println("}");
            out.println("");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void putQuiet(Element arg0) throws IllegalArgumentException, IllegalStateException, CacheException {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            this.cache.putQuiet(arg0);
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void putWithWriter(Element arg0) throws IllegalArgumentException, IllegalStateException, CacheException {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            this.cache.putWithWriter(arg0);
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Map getAll(Collection arg0) throws IllegalStateException, CacheException, NullPointerException {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            Map<Object, Element> map = this.cache.getAll(arg0);
            return map;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Element getQuiet(Serializable arg0) throws IllegalStateException, CacheException {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            Element element = this.cache.getQuiet(arg0);
            return element;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Element getQuiet(Object arg0) throws IllegalStateException, CacheException {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            Element element = this.cache.getQuiet(arg0);
            return element;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List getKeysWithExpiryCheck() throws IllegalStateException, CacheException {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            List list = this.cache.getKeysWithExpiryCheck();
            return list;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List getKeysNoDuplicateCheck() throws IllegalStateException {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            List list = this.cache.getKeysNoDuplicateCheck();
            return list;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean removeQuiet(Serializable arg0) throws IllegalStateException {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            boolean bl = this.cache.removeQuiet(arg0);
            return bl;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean removeQuiet(Object arg0) throws IllegalStateException {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            boolean bl = this.cache.removeQuiet(arg0);
            return bl;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean removeWithWriter(Object arg0) throws IllegalStateException, CacheException {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            boolean bl = this.cache.removeWithWriter(arg0);
            return bl;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    @Deprecated
    public long calculateInMemorySize() throws IllegalStateException, CacheException {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            long l = this.cache.calculateInMemorySize();
            return l;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    @Deprecated
    public long calculateOffHeapSize() throws IllegalStateException, CacheException {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            long l = this.cache.calculateOffHeapSize();
            return l;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    @Deprecated
    public long calculateOnDiskSize() throws IllegalStateException, CacheException {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            long l = this.cache.calculateOnDiskSize();
            return l;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean hasAbortedSizeOf() {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            boolean bl = this.cache.hasAbortedSizeOf();
            return bl;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    @Deprecated
    public long getMemoryStoreSize() throws IllegalStateException {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            long l = this.cache.getMemoryStoreSize();
            return l;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    @Deprecated
    public long getOffHeapStoreSize() throws IllegalStateException {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            long l = this.cache.getOffHeapStoreSize();
            return l;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    @Deprecated
    public int getDiskStoreSize() throws IllegalStateException {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            int n = this.cache.getDiskStoreSize();
            return n;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isExpired(Element arg0) throws IllegalStateException, NullPointerException {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            boolean bl = this.cache.isExpired(arg0);
            return bl;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public RegisteredEventListeners getCacheEventNotificationService() {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            RegisteredEventListeners registeredEventListeners = this.cache.getCacheEventNotificationService();
            return registeredEventListeners;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isElementInMemory(Object arg0) {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            boolean bl = this.cache.isElementInMemory(arg0);
            return bl;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isElementInMemory(Serializable arg0) {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            boolean bl = this.cache.isElementInMemory(arg0);
            return bl;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isElementOnDisk(Object arg0) {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            boolean bl = this.cache.isElementOnDisk(arg0);
            return bl;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isElementOnDisk(Serializable arg0) {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            boolean bl = this.cache.isElementOnDisk(arg0);
            return bl;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getGuid() {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            String string = this.cache.getGuid();
            return string;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CacheManager getCacheManager() {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            CacheManager cacheManager = this.cache.getCacheManager();
            return cacheManager;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    @Override
    public void evictExpiredElements() {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            this.cache.evictExpiredElements();
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isKeyInCache(Object arg0) {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            boolean bl = this.cache.isKeyInCache(arg0);
            return bl;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isValueInCache(Object arg0) {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            boolean bl = this.cache.isValueInCache(arg0);
            return bl;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public StatisticsGateway getStatistics() throws IllegalStateException {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            StatisticsGateway statisticsGateway = this.cache.getStatistics();
            return statisticsGateway;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setCacheManager(CacheManager arg0) {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            this.cache.setCacheManager(arg0);
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public BootstrapCacheLoader getBootstrapCacheLoader() {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            BootstrapCacheLoader bootstrapCacheLoader = this.cache.getBootstrapCacheLoader();
            return bootstrapCacheLoader;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setBootstrapCacheLoader(BootstrapCacheLoader arg0) throws CacheException {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            this.cache.setBootstrapCacheLoader(arg0);
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    @Override
    public void initialise() {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            this.cache.initialise();
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    @Override
    public void bootstrap() {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            this.cache.bootstrap();
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CacheConfiguration getCacheConfiguration() {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            CacheConfiguration cacheConfiguration = this.cache.getCacheConfiguration();
            return cacheConfiguration;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void registerCacheExtension(CacheExtension arg0) {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            this.cache.registerCacheExtension(arg0);
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unregisterCacheExtension(CacheExtension arg0) {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            this.cache.unregisterCacheExtension(arg0);
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List getRegisteredCacheExtensions() {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            List<CacheExtension> list = this.cache.getRegisteredCacheExtensions();
            return list;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setCacheExceptionHandler(CacheExceptionHandler arg0) {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            this.cache.setCacheExceptionHandler(arg0);
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CacheExceptionHandler getCacheExceptionHandler() {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            CacheExceptionHandler cacheExceptionHandler = this.cache.getCacheExceptionHandler();
            return cacheExceptionHandler;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void registerCacheLoader(CacheLoader arg0) {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            this.cache.registerCacheLoader(arg0);
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unregisterCacheLoader(CacheLoader arg0) {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            this.cache.unregisterCacheLoader(arg0);
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List getRegisteredCacheLoaders() {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            List<CacheLoader> list = this.cache.getRegisteredCacheLoaders();
            return list;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void registerCacheWriter(CacheWriter arg0) {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            this.cache.registerCacheWriter(arg0);
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void registerDynamicAttributesExtractor(DynamicAttributesExtractor extractor) {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            this.cache.registerDynamicAttributesExtractor(extractor);
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    @Override
    public void unregisterCacheWriter() {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            this.cache.unregisterCacheWriter();
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CacheWriter getRegisteredCacheWriter() {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            CacheWriter cacheWriter = this.cache.getRegisteredCacheWriter();
            return cacheWriter;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Element getWithLoader(Object arg0, CacheLoader arg1, Object arg2) throws CacheException {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            Element element = this.cache.getWithLoader(arg0, arg1, arg2);
            return element;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Map getAllWithLoader(Collection arg0, Object arg1) throws CacheException {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            Map map = this.cache.getAllWithLoader(arg0, arg1);
            return map;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isDisabled() {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            boolean bl = this.cache.isDisabled();
            return bl;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setDisabled(boolean arg0) {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            this.cache.setDisabled(arg0);
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object getInternalContext() {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            Object object = this.cache.getInternalContext();
            return object;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    @Override
    public void disableDynamicFeatures() {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            this.cache.disableDynamicFeatures();
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CacheWriterManager getWriterManager() {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            CacheWriterManager cacheWriterManager = this.cache.getWriterManager();
            return cacheWriterManager;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isClusterCoherent() throws TerracottaNotRunningException {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            boolean bl = this.cache.isClusterCoherent();
            return bl;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isNodeCoherent() throws TerracottaNotRunningException {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            boolean bl = this.cache.isNodeCoherent();
            return bl;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setNodeCoherent(boolean arg0) throws UnsupportedOperationException, TerracottaNotRunningException {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            this.cache.setNodeCoherent(arg0);
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    @Override
    public void waitUntilClusterCoherent() throws UnsupportedOperationException, TerracottaNotRunningException {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            this.cache.waitUntilClusterCoherent();
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setTransactionManagerLookup(TransactionManagerLookup arg0) {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            this.cache.setTransactionManagerLookup(arg0);
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Attribute getSearchAttribute(String arg0) throws CacheException {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            Attribute attribute = this.cache.getSearchAttribute(arg0);
            return attribute;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Set<Attribute> getSearchAttributes() throws CacheException {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            Set<Attribute> set = this.cache.getSearchAttributes();
            return set;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Query createQuery() {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            Query query = this.cache.createQuery();
            return query;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSearchable() {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            boolean bl = this.cache.isSearchable();
            return bl;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void acquireReadLockOnKey(Object arg0) {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            this.cache.acquireReadLockOnKey(arg0);
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void acquireWriteLockOnKey(Object arg0) {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            this.cache.acquireWriteLockOnKey(arg0);
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean tryReadLockOnKey(Object arg0, long arg1) throws InterruptedException {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            boolean bl = this.cache.tryReadLockOnKey(arg0, arg1);
            return bl;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean tryWriteLockOnKey(Object arg0, long arg1) throws InterruptedException {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            boolean bl = this.cache.tryWriteLockOnKey(arg0, arg1);
            return bl;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void releaseReadLockOnKey(Object arg0) {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            this.cache.releaseReadLockOnKey(arg0);
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void releaseWriteLockOnKey(Object arg0) {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            this.cache.releaseWriteLockOnKey(arg0);
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isReadLockedByCurrentThread(Object arg0) throws UnsupportedOperationException {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            boolean bl = this.cache.isReadLockedByCurrentThread(arg0);
            return bl;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isWriteLockedByCurrentThread(Object arg0) throws UnsupportedOperationException {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            boolean bl = this.cache.isWriteLockedByCurrentThread(arg0);
            return bl;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isClusterBulkLoadEnabled() throws UnsupportedOperationException, TerracottaNotRunningException {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            boolean bl = this.cache.isClusterBulkLoadEnabled();
            return bl;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isNodeBulkLoadEnabled() throws UnsupportedOperationException, TerracottaNotRunningException {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            boolean bl = this.cache.isNodeBulkLoadEnabled();
            return bl;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setNodeBulkLoadEnabled(boolean arg0) throws UnsupportedOperationException, TerracottaNotRunningException {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            this.cache.setNodeBulkLoadEnabled(arg0);
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    @Override
    public void waitUntilClusterBulkLoadComplete() throws UnsupportedOperationException, TerracottaNotRunningException {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            this.cache.waitUntilClusterBulkLoadComplete();
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void loadAll(Collection arg0, Object arg1) throws CacheException {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            this.cache.loadAll(arg0, arg1);
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String toString() {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            String string = this.cache.toString();
            return string;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Element get(Object arg0) throws IllegalStateException, CacheException {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            Element element = this.cache.get(arg0);
            return element;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Element get(Serializable arg0) throws IllegalStateException, CacheException {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            Element element = this.cache.get(arg0);
            return element;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void put(Element arg0) throws IllegalArgumentException, IllegalStateException, CacheException {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            this.cache.put(arg0);
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void put(Element arg0, boolean arg1) throws IllegalArgumentException, IllegalStateException, CacheException {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            this.cache.put(arg0, arg1);
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            Object object = this.cache.clone();
            return object;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getName() {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            String string = this.cache.getName();
            return string;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Element replace(Element arg0) throws NullPointerException {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            Element element = this.cache.replace(arg0);
            return element;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean replace(Element arg0, Element arg1) throws NullPointerException, IllegalArgumentException {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            boolean bl = this.cache.replace(arg0, arg1);
            return bl;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void putAll(Collection arg0) throws IllegalArgumentException, IllegalStateException, CacheException {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            this.cache.putAll(arg0);
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean remove(Serializable arg0, boolean arg1) throws IllegalStateException {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            boolean bl = this.cache.remove(arg0, arg1);
            return bl;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean remove(Object arg0) throws IllegalStateException {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            boolean bl = this.cache.remove(arg0);
            return bl;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean remove(Serializable arg0) throws IllegalStateException {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            boolean bl = this.cache.remove(arg0);
            return bl;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean remove(Object arg0, boolean arg1) throws IllegalStateException {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            boolean bl = this.cache.remove(arg0, arg1);
            return bl;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void load(Object arg0) throws CacheException {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            this.cache.load(arg0);
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setName(String arg0) {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            this.cache.setName(arg0);
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    @Override
    public void flush() throws IllegalStateException, CacheException {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            this.cache.flush();
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int getSize() throws IllegalStateException, CacheException {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            int n = this.cache.getSize();
            return n;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean removeElement(Element arg0) throws NullPointerException {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            boolean bl = this.cache.removeElement(arg0);
            return bl;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeAll(boolean arg0) throws IllegalStateException, CacheException {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            this.cache.removeAll(arg0);
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    @Override
    public void removeAll() throws IllegalStateException, CacheException {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            this.cache.removeAll();
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeAll(Collection arg0, boolean arg1) throws IllegalStateException, NullPointerException {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            this.cache.removeAll(arg0, arg1);
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeAll(Collection arg0) throws IllegalStateException, NullPointerException {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            this.cache.removeAll(arg0);
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Element putIfAbsent(Element arg0) throws NullPointerException {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            Element element = this.cache.putIfAbsent(arg0);
            return element;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Element putIfAbsent(Element arg0, boolean arg1) throws NullPointerException {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            Element element = this.cache.putIfAbsent(arg0, arg1);
            return element;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addPropertyChangeListener(PropertyChangeListener arg0) {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            this.cache.addPropertyChangeListener(arg0);
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removePropertyChangeListener(PropertyChangeListener arg0) {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            this.cache.removePropertyChangeListener(arg0);
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    @Override
    public void dispose() throws IllegalStateException {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            this.cache.dispose();
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List getKeys() throws IllegalStateException, CacheException {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            List list = Collections.unmodifiableList(new ClassLoaderAwareList(this.cache.getKeys()));
            return list;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Status getStatus() {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            Status status = this.cache.getStatus();
            return status;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    private class ClassLoaderAwareIterator
    implements Iterator {
        private final Iterator delegate;

        public ClassLoaderAwareIterator(Iterator delegate) {
            this.delegate = delegate;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean hasNext() {
            Thread t = Thread.currentThread();
            ClassLoader prev = t.getContextClassLoader();
            t.setContextClassLoader(ClassLoaderAwareCache.this.classLoader);
            try {
                boolean bl = this.delegate.hasNext();
                return bl;
            }
            finally {
                t.setContextClassLoader(prev);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public Object next() {
            Thread t = Thread.currentThread();
            ClassLoader prev = t.getContextClassLoader();
            t.setContextClassLoader(ClassLoaderAwareCache.this.classLoader);
            try {
                Object e = this.delegate.next();
                return e;
            }
            finally {
                t.setContextClassLoader(prev);
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove not supported for this Iterator");
        }
    }

    private class ClassLoaderAwareList
    extends AbstractList {
        private final List delegate;

        public ClassLoaderAwareList(List delegate) {
            this.delegate = delegate;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Object get(int index) {
            Thread t = Thread.currentThread();
            ClassLoader prev = t.getContextClassLoader();
            t.setContextClassLoader(ClassLoaderAwareCache.this.classLoader);
            try {
                Object e = this.delegate.get(index);
                return e;
            }
            finally {
                t.setContextClassLoader(prev);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int size() {
            Thread t = Thread.currentThread();
            ClassLoader prev = t.getContextClassLoader();
            t.setContextClassLoader(ClassLoaderAwareCache.this.classLoader);
            try {
                int n = this.delegate.size();
                return n;
            }
            finally {
                t.setContextClassLoader(prev);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Iterator iterator() {
            Thread t = Thread.currentThread();
            ClassLoader prev = t.getContextClassLoader();
            t.setContextClassLoader(ClassLoaderAwareCache.this.classLoader);
            try {
                ClassLoaderAwareIterator classLoaderAwareIterator = new ClassLoaderAwareIterator(this.delegate.iterator());
                return classLoaderAwareIterator;
            }
            finally {
                t.setContextClassLoader(prev);
            }
        }
    }
}

