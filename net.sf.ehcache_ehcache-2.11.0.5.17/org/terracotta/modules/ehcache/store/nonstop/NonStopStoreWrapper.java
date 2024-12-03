/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.terracotta.toolkit.Toolkit
 *  org.terracotta.toolkit.ToolkitFeatureType
 *  org.terracotta.toolkit.feature.NonStopFeature
 *  org.terracotta.toolkit.nonstop.NonStopConfiguration
 *  org.terracotta.toolkit.nonstop.NonStopException
 *  org.terracotta.toolkit.nonstop.NonStopToolkitInstantiationException
 *  org.terracotta.toolkit.rejoin.RejoinException
 */
package org.terracotta.modules.ehcache.store.nonstop;

import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheOperationOutcomes;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.Status;
import net.sf.ehcache.concurrent.CacheLockProvider;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.InvalidConfigurationException;
import net.sf.ehcache.config.NonstopConfiguration;
import net.sf.ehcache.config.TimeoutBehaviorConfiguration;
import net.sf.ehcache.constructs.nonstop.NonStopCacheException;
import net.sf.ehcache.event.CacheEventListener;
import net.sf.ehcache.search.Attribute;
import net.sf.ehcache.search.Results;
import net.sf.ehcache.search.SearchException;
import net.sf.ehcache.statistics.StatisticBuilder;
import net.sf.ehcache.store.ElementValueComparator;
import net.sf.ehcache.store.Policy;
import net.sf.ehcache.store.StoreListener;
import net.sf.ehcache.store.StoreQuery;
import net.sf.ehcache.store.TerracottaStore;
import net.sf.ehcache.terracotta.TerracottaNotRunningException;
import net.sf.ehcache.writer.CacheWriterManager;
import net.sf.ehcache.writer.writebehind.NonStopWriteBehind;
import net.sf.ehcache.writer.writebehind.WriteBehind;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terracotta.modules.ehcache.ClusteredCacheInternalContext;
import org.terracotta.modules.ehcache.ToolkitInstanceFactory;
import org.terracotta.modules.ehcache.concurrency.NonStopCacheLockProvider;
import org.terracotta.modules.ehcache.store.TerracottaStoreInitializationService;
import org.terracotta.modules.ehcache.store.ToolkitNonStopExceptionOnTimeoutConfiguration;
import org.terracotta.modules.ehcache.store.nonstop.ExceptionOnTimeoutStore;
import org.terracotta.modules.ehcache.store.nonstop.LocalReadsAndExceptionOnWritesTimeoutStore;
import org.terracotta.modules.ehcache.store.nonstop.LocalReadsOnTimeoutStore;
import org.terracotta.modules.ehcache.store.nonstop.NoOpOnTimeoutStore;
import org.terracotta.modules.ehcache.store.nonstop.NonStopSubTypeProxyUtil;
import org.terracotta.modules.ehcache.store.nonstop.RejoinWithoutNonStopStore;
import org.terracotta.modules.ehcache.store.nonstop.ToolkitNonstopDisableConfig;
import org.terracotta.statistics.StatisticsManager;
import org.terracotta.statistics.observer.OperationObserver;
import org.terracotta.toolkit.Toolkit;
import org.terracotta.toolkit.ToolkitFeatureType;
import org.terracotta.toolkit.feature.NonStopFeature;
import org.terracotta.toolkit.nonstop.NonStopConfiguration;
import org.terracotta.toolkit.nonstop.NonStopException;
import org.terracotta.toolkit.nonstop.NonStopToolkitInstantiationException;
import org.terracotta.toolkit.rejoin.RejoinException;

public class NonStopStoreWrapper
implements TerracottaStore {
    private static final Logger LOGGER = LoggerFactory.getLogger(NonStopStoreWrapper.class);
    private static final long TIME_TO_WAIT_FOR_ASYNC_STORE_INIT = Long.getLong("com.tc.non.stop.async.store.init", TimeUnit.MINUTES.toMillis(5L));
    private static final Set<String> LOCAL_METHODS = new HashSet<String>();
    private static final long REJOIN_RETRY_INTERVAL = TimeUnit.SECONDS.toMillis(10L);
    private static final Set<String> METHODS_TO_SKIP;
    private volatile TerracottaStore delegate;
    private final NonStopFeature nonStop;
    private final ToolkitNonStopExceptionOnTimeoutConfiguration toolkitNonStopConfiguration;
    private final NonstopConfiguration ehcacheNonStopConfiguration;
    private volatile TerracottaStore localReadDelegate;
    private final BulkOpsToolkitNonStopConfiguration bulkOpsToolkitNonStopConfiguration;
    private final ClusteredCacheInternalContext clusteredCacheInternalContext;
    private final TerracottaStoreInitializationService initializationService;
    private final Ehcache cache;
    private WriteBehind writeBehind;
    private volatile Throwable exceptionDuringInitialization = null;
    private final OperationObserver<CacheOperationOutcomes.NonStopOperationOutcomes> nonstopObserver = ((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)StatisticBuilder.operation(CacheOperationOutcomes.NonStopOperationOutcomes.class).named("nonstop")).of(this)).tag(new String[]{"cache"})).build();
    private CacheEventListener cacheEventListener;

    public NonStopStoreWrapper(Callable<TerracottaStore> clusteredStoreCreator, ToolkitInstanceFactory toolkitInstanceFactory, Ehcache cache, TerracottaStoreInitializationService initializationService) {
        this.cache = cache;
        this.initializationService = initializationService;
        this.nonStop = (NonStopFeature)toolkitInstanceFactory.getToolkit().getFeature(ToolkitFeatureType.NONSTOP);
        this.ehcacheNonStopConfiguration = cache.getCacheConfiguration().getTerracottaConfiguration().getNonstopConfiguration();
        this.toolkitNonStopConfiguration = new ToolkitNonStopExceptionOnTimeoutConfiguration(this.ehcacheNonStopConfiguration);
        this.bulkOpsToolkitNonStopConfiguration = new BulkOpsToolkitNonStopConfiguration(this.ehcacheNonStopConfiguration);
        Toolkit toolkit = toolkitInstanceFactory.getToolkit();
        CacheLockProvider cacheLockProvider = this.createCacheLockProvider(toolkit, toolkitInstanceFactory);
        this.clusteredCacheInternalContext = new ClusteredCacheInternalContext(toolkit, cacheLockProvider);
        if (this.ehcacheNonStopConfiguration != null && this.ehcacheNonStopConfiguration.isEnabled()) {
            this.createStoreAsynchronously(toolkit, clusteredStoreCreator);
        } else {
            this.createStore(clusteredStoreCreator);
        }
        StatisticsManager.associate(this).withParent(cache);
    }

    private void createStore(Callable<TerracottaStore> clusteredStoreCreator) {
        try {
            while (true) {
                try {
                    this.doInit(clusteredStoreCreator);
                    return;
                }
                catch (RejoinException e) {
                    Thread.sleep(REJOIN_RETRY_INTERVAL);
                    continue;
                }
                break;
            }
        }
        catch (Throwable t) {
            String message = "Error while creating store inline ";
            this.handleException(message, t);
            return;
        }
    }

    private CacheLockProvider createCacheLockProvider(Toolkit toolkit, ToolkitInstanceFactory toolkitInstanceFactory) {
        return new NonStopCacheLockProvider((NonStopFeature)toolkit.getFeature(ToolkitFeatureType.NONSTOP), this.ehcacheNonStopConfiguration, toolkitInstanceFactory);
    }

    private void createStoreAsynchronously(Toolkit toolkit, Callable<TerracottaStore> clusteredStoreCreator) {
        this.initializationService.initialize(this.createInitRunnable(clusteredStoreCreator), this.ehcacheNonStopConfiguration);
        if (this.exceptionDuringInitialization != null) {
            throw new NonStopToolkitInstantiationException(this.exceptionDuringInitialization);
        }
    }

    private Runnable createInitRunnable(final Callable<TerracottaStore> clusteredStoreCreator) {
        Runnable initRunnable = new Runnable(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void run() {
                try {
                    long startTime = System.currentTimeMillis();
                    while (true) {
                        NonStopStoreWrapper.this.nonStop.start((NonStopConfiguration)new ToolkitNonstopDisableConfig());
                        try {
                            NonStopStoreWrapper.this.doInit(clusteredStoreCreator);
                            NonStopStoreWrapper nonStopStoreWrapper = NonStopStoreWrapper.this;
                            synchronized (nonStopStoreWrapper) {
                                NonStopStoreWrapper.this.notifyAll();
                            }
                            return;
                        }
                        catch (RejoinException e) {
                            if (startTime + TIME_TO_WAIT_FOR_ASYNC_STORE_INIT >= System.currentTimeMillis()) continue;
                            throw new RuntimeException("Unable to create clusteredStore in time", e);
                        }
                        finally {
                            NonStopStoreWrapper.this.nonStop.finish();
                            continue;
                        }
                        break;
                    }
                }
                catch (Throwable t) {
                    LOGGER.warn("Error while creating store asynchronously for Cache: " + NonStopStoreWrapper.this.cache.getName(), t);
                    NonStopStoreWrapper.this.exceptionDuringInitialization = t;
                    NonStopStoreWrapper nonStopStoreWrapper = NonStopStoreWrapper.this;
                    synchronized (nonStopStoreWrapper) {
                        NonStopStoreWrapper.this.notifyAll();
                    }
                    return;
                }
            }
        };
        return initRunnable;
    }

    @Override
    public Object getInternalContext() {
        return this.clusteredCacheInternalContext;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public WriteBehind createWriteBehind() {
        if (this.ehcacheNonStopConfiguration != null && this.ehcacheNonStopConfiguration.isEnabled()) {
            NonStopStoreWrapper nonStopStoreWrapper = this;
            synchronized (nonStopStoreWrapper) {
                if (this.writeBehind != null) {
                    throw new IllegalStateException();
                }
                this.writeBehind = new NonStopWriteBehind();
                if (this.delegate != null) {
                    ((NonStopWriteBehind)this.writeBehind).init(this.cache.getCacheManager().createTerracottaWriteBehind(this.cache));
                }
                return this.writeBehind;
            }
        }
        this.writeBehind = this.cache.getCacheManager().createTerracottaWriteBehind(this.cache);
        return this.writeBehind;
    }

    @Override
    public boolean bufferFull() {
        return false;
    }

    private TerracottaStore createTerracottaStore(Callable<TerracottaStore> clusteredStoreCreator) {
        try {
            return clusteredStoreCreator.call();
        }
        catch (InvalidConfigurationException e) {
            throw e;
        }
        catch (RejoinException e) {
            throw e;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void doInit(Callable<TerracottaStore> clusteredStoreCreator) {
        TerracottaStore delegateTemp = this.createTerracottaStore(clusteredStoreCreator);
        if (this.clusteredCacheInternalContext.getCacheLockProvider() instanceof NonStopCacheLockProvider) {
            ((NonStopCacheLockProvider)this.clusteredCacheInternalContext.getCacheLockProvider()).init((CacheLockProvider)delegateTemp.getInternalContext());
        }
        this.cacheEventListener = this.cache.getCacheManager().createTerracottaEventReplicator(this.cache);
        NonStopStoreWrapper nonStopStoreWrapper = this;
        synchronized (nonStopStoreWrapper) {
            if (this.delegate == null) {
                this.delegate = delegateTemp;
                StatisticsManager.associate(this).withChild(delegateTemp);
                if (this.writeBehind != null && this.writeBehind instanceof NonStopWriteBehind) {
                    ((NonStopWriteBehind)this.writeBehind).init(this.cache.getCacheManager().createTerracottaWriteBehind(this.cache));
                }
            }
        }
        LOGGER.debug("Initialization Completed for Cache : {}", (Object)this.cache.getName());
    }

    @Override
    public Object getMBean() {
        return null;
    }

    @Override
    public void removeStoreListener(StoreListener arg0) {
        if (this.delegate != null) {
            this.delegate.removeStoreListener(arg0);
        }
    }

    @Override
    public void dispose() {
        this.cacheEventListener.dispose();
        if (this.delegate != null) {
            this.delegate.dispose();
        }
    }

    @Override
    public void waitUntilClusterCoherent() throws UnsupportedOperationException, TerracottaNotRunningException, InterruptedException {
        this.nonStop.start((NonStopConfiguration)new ToolkitNonstopDisableConfig());
        try {
            this.waitForInit(Long.MAX_VALUE);
            while (true) {
                try {
                    this.delegate.waitUntilClusterCoherent();
                    return;
                }
                catch (RejoinException e) {
                    try {
                        Thread.sleep(REJOIN_RETRY_INTERVAL);
                        continue;
                    }
                    catch (NonStopToolkitInstantiationException e2) {
                        this.handleNonStopToolkitInstantiationException(e2);
                    }
                }
                break;
            }
        }
        finally {
            this.nonStop.finish();
        }
    }

    private void throwNonStopExceptionWhenClusterNotInit() throws NonStopException {
        if (this.delegate == null && this.ehcacheNonStopConfiguration != null && this.ehcacheNonStopConfiguration.isEnabled()) {
            if (this.ehcacheNonStopConfiguration.isImmediateTimeout()) {
                if (this.exceptionDuringInitialization != null) {
                    throw new NonStopToolkitInstantiationException(this.exceptionDuringInitialization);
                }
                throw new NonStopException("Cluster not up OR still in the process of connecting ");
            }
            long timeout = this.ehcacheNonStopConfiguration.getTimeoutMillis();
            this.waitForInit(timeout);
        }
    }

    private void handleException(String message, Throwable t) {
        if (t.getClass().getSimpleName().equals("TCNotRunningException")) {
            throw new TerracottaNotRunningException("Clustered Cache is probably shutdown or Terracotta backend is down.", t);
        }
        if (t instanceof CacheException) {
            throw (CacheException)t;
        }
        throw new CacheException(message + t.getMessage(), t);
    }

    private void handleNonStopToolkitInstantiationException(NonStopToolkitInstantiationException e) {
        switch (this.ehcacheNonStopConfiguration.getTimeoutBehavior().getTimeoutBehaviorType()) {
            case EXCEPTION: {
                this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.FAILURE);
                throw new NonStopCacheException("Error while initializing cache", e);
            }
        }
        LOGGER.error("Error while initializing cache", (Throwable)e);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void waitForInit(long timeout) {
        NonStopStoreWrapper nonStopStoreWrapper = this;
        synchronized (nonStopStoreWrapper) {
            while (this.delegate == null) {
                try {
                    if (this.exceptionDuringInitialization != null) {
                        throw new NonStopToolkitInstantiationException(this.exceptionDuringInitialization);
                    }
                    this.wait(timeout);
                }
                catch (InterruptedException e) {
                    throw new NonStopException("Cluster not up OR still in the process of connecting ");
                }
            }
        }
    }

    private TerracottaStore getTimeoutBehavior(boolean rejoin) {
        if (this.ehcacheNonStopConfiguration == null) {
            throw new AssertionError((Object)"Ehcache NonStopConfig cannot be null");
        }
        if (this.ehcacheNonStopConfiguration.isEnabled()) {
            TimeoutBehaviorConfiguration behaviorConfiguration = this.ehcacheNonStopConfiguration.getTimeoutBehavior();
            switch (behaviorConfiguration.getTimeoutBehaviorType()) {
                case EXCEPTION: {
                    return ExceptionOnTimeoutStore.getInstance();
                }
                case LOCAL_READS: {
                    if (this.localReadDelegate == null) {
                        if (this.delegate == null) {
                            return NoOpOnTimeoutStore.getInstance();
                        }
                        this.localReadDelegate = new LocalReadsOnTimeoutStore(this.delegate);
                    }
                    return this.localReadDelegate;
                }
                case LOCAL_READS_AND_EXCEPTION_ON_WRITES: {
                    if (this.localReadDelegate == null) {
                        if (this.delegate == null) {
                            return new LocalReadsAndExceptionOnWritesTimeoutStore();
                        }
                        this.localReadDelegate = new LocalReadsAndExceptionOnWritesTimeoutStore(this.delegate);
                    }
                    return this.localReadDelegate;
                }
                case NOOP: {
                    return NoOpOnTimeoutStore.getInstance();
                }
            }
            return ExceptionOnTimeoutStore.getInstance();
        }
        if (rejoin) {
            return RejoinWithoutNonStopStore.getInstance();
        }
        return ExceptionOnTimeoutStore.getInstance();
    }

    private static void validateMethodNamesExist(Class klazz, Set<String> methodToCheck) {
        for (String methodName : methodToCheck) {
            if (!NonStopStoreWrapper.exist(klazz, methodName)) {
                throw new AssertionError((Object)("Method " + methodName + " does not exist in class " + klazz.getName()));
            }
        }
    }

    private static boolean exist(Class klazz, String method) {
        Method[] methods;
        for (Method m : methods = klazz.getMethods()) {
            if (!m.getName().equals(method)) continue;
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        PrintStream out = System.out;
        Class[] classes = new Class[]{TerracottaStore.class};
        HashSet<String> bulkMethods = new HashSet<String>();
        bulkMethods.add("setNodeCoherent");
        bulkMethods.add("putAll");
        bulkMethods.add("getAllQuiet");
        bulkMethods.add("getAll");
        bulkMethods.add("removeAll");
        bulkMethods.add("quickClear");
        bulkMethods.add("quickSize");
        bulkMethods.add("getSize");
        bulkMethods.add("getTerracottaClusteredSize");
        NonStopStoreWrapper.validateMethodNamesExist(TerracottaStore.class, bulkMethods);
        for (Class c : classes) {
            for (Method m : c.getMethods()) {
                int i;
                if (METHODS_TO_SKIP.contains(m.toGenericString())) continue;
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
                out.println(" // THIS IS GENERATED CODE -- DO NOT HAND MODIFY!");
                out.println(" // " + m.toGenericString());
                if (LOCAL_METHODS.contains(m.getName())) {
                    out.println(" if (delegate != null) {");
                    if (m.getReturnType() != Void.TYPE) {
                        out.print(m.getReturnType().getSimpleName() + " _ret = ");
                    }
                    if (NonStopSubTypeProxyUtil.isNonStopSubtype(m.getReturnType())) {
                        out.print("NonStopSubTypeProxyUtil.newNonStopSubTypeProxy(" + m.getReturnType().getSimpleName() + ".class , ");
                    }
                    out.print("this.delegate." + m.getName() + "(");
                    for (i = 0; i < params.length; ++i) {
                        out.print("arg" + i);
                        if (i >= params.length - 1) continue;
                        out.print(", ");
                    }
                    if (NonStopSubTypeProxyUtil.isNonStopSubtype(m.getReturnType())) {
                        out.println(")");
                    }
                    out.println(");");
                    if (m.getReturnType() != Void.TYPE) {
                        out.println("return _ret;");
                    }
                    out.println("    } else {");
                    if (m.getReturnType() != Void.TYPE) {
                        out.print("return ");
                    }
                    out.print("NoOpOnTimeoutStore.getInstance()." + m.getName() + "(");
                    for (i = 0; i < params.length; ++i) {
                        out.print("arg" + i);
                        if (i >= params.length - 1) continue;
                        out.print(", ");
                    }
                    out.println(");");
                    out.println(" }");
                    out.println(" }");
                    continue;
                }
                if (bulkMethods.contains(m.getName())) {
                    out.println("      nonStop.start(bulkOpsToolkitNonStopConfiguration);");
                } else {
                    out.println("      nonStop.start(toolkitNonStopConfiguration);");
                }
                out.println("      try {");
                out.println("      throwNonStopExceptionWhenClusterNotInit();");
                out.print("        ");
                if (m.getReturnType() != Void.TYPE) {
                    out.print(m.getReturnType().getSimpleName() + " _ret = ");
                }
                if (NonStopSubTypeProxyUtil.isNonStopSubtype(m.getReturnType())) {
                    out.print("NonStopSubTypeProxyUtil.newNonStopSubTypeProxy(" + m.getReturnType().getSimpleName() + ".class , ");
                }
                out.print("this.delegate." + m.getName() + "(");
                for (i = 0; i < params.length; ++i) {
                    out.print("arg" + i);
                    if (i >= params.length - 1) continue;
                    out.print(", ");
                }
                if (NonStopSubTypeProxyUtil.isNonStopSubtype(m.getReturnType())) {
                    out.println(")");
                }
                out.println(");");
                out.println("nonstopObserver.end(NonStopOperationOutcomes.SUCCESS);");
                if (m.getReturnType() != Void.TYPE) {
                    out.println("return _ret;");
                }
                out.println("      } catch (NonStopToolkitInstantiationException e) {");
                System.out.println("handleNonStopToolkitInstantiationException(e);");
                if (m.getReturnType() != Void.TYPE) {
                    out.print("return ");
                }
                out.print("getTimeoutBehavior(false)." + m.getName() + "(");
                for (i = 0; i < params.length; ++i) {
                    out.print("arg" + i);
                    if (i >= params.length - 1) continue;
                    out.print(", ");
                }
                out.println(");");
                out.println("      } catch (NonStopException e) {");
                out.println("nonstopObserver.end(NonStopOperationOutcomes.TIMEOUT);");
                if (m.getReturnType() != Void.TYPE) {
                    out.print("return ");
                }
                out.print("getTimeoutBehavior(false)." + m.getName() + "(");
                for (i = 0; i < params.length; ++i) {
                    out.print("arg" + i);
                    if (i >= params.length - 1) continue;
                    out.print(", ");
                }
                out.println(");");
                out.println("      } catch (RejoinException e) {");
                out.println("nonstopObserver.end(NonStopOperationOutcomes.REJOIN_TIMEOUT);");
                if (m.getReturnType() != Void.TYPE) {
                    out.print("return ");
                }
                out.print("getTimeoutBehavior(true)." + m.getName() + "(");
                for (i = 0; i < params.length; ++i) {
                    out.print("arg" + i);
                    if (i >= params.length - 1) continue;
                    out.print(", ");
                }
                out.println(");");
                out.println("      } finally {");
                out.println("        nonStop.finish();");
                out.println("      }");
                out.println("}");
                out.println("");
            }
        }
    }

    @Override
    public Element unsafeGet(Object arg0) {
        if (this.delegate != null) {
            Element _ret = this.delegate.unsafeGet(arg0);
            return _ret;
        }
        return NoOpOnTimeoutStore.getInstance().unsafeGet(arg0);
    }

    @Override
    public void quickClear() {
        this.nonStop.start((NonStopConfiguration)this.bulkOpsToolkitNonStopConfiguration);
        try {
            this.throwNonStopExceptionWhenClusterNotInit();
            this.delegate.quickClear();
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.SUCCESS);
        }
        catch (NonStopToolkitInstantiationException e) {
            this.handleNonStopToolkitInstantiationException(e);
            this.getTimeoutBehavior(false).quickClear();
        }
        catch (NonStopException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.TIMEOUT);
            this.getTimeoutBehavior(false).quickClear();
        }
        catch (RejoinException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.REJOIN_TIMEOUT);
            this.getTimeoutBehavior(true).quickClear();
        }
        finally {
            this.nonStop.finish();
        }
    }

    @Override
    public int quickSize() {
        this.nonStop.start((NonStopConfiguration)this.bulkOpsToolkitNonStopConfiguration);
        try {
            this.throwNonStopExceptionWhenClusterNotInit();
            int _ret = this.delegate.quickSize();
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.SUCCESS);
            int n = _ret;
            return n;
        }
        catch (NonStopToolkitInstantiationException e) {
            this.handleNonStopToolkitInstantiationException(e);
            int n = this.getTimeoutBehavior(false).quickSize();
            return n;
        }
        catch (NonStopException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.TIMEOUT);
            int n = this.getTimeoutBehavior(false).quickSize();
            return n;
        }
        catch (RejoinException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.REJOIN_TIMEOUT);
            int n = this.getTimeoutBehavior(true).quickSize();
            return n;
        }
        finally {
            this.nonStop.finish();
        }
    }

    @Override
    public Set getLocalKeys() {
        if (this.delegate != null) {
            Set _ret = NonStopSubTypeProxyUtil.newNonStopSubTypeProxy(Set.class, this.delegate.getLocalKeys());
            return _ret;
        }
        return NoOpOnTimeoutStore.getInstance().getLocalKeys();
    }

    @Override
    public CacheConfiguration.TransactionalMode getTransactionalMode() {
        this.nonStop.start((NonStopConfiguration)this.toolkitNonStopConfiguration);
        try {
            this.throwNonStopExceptionWhenClusterNotInit();
            CacheConfiguration.TransactionalMode _ret = this.delegate.getTransactionalMode();
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.SUCCESS);
            CacheConfiguration.TransactionalMode transactionalMode = _ret;
            return transactionalMode;
        }
        catch (NonStopToolkitInstantiationException e) {
            this.handleNonStopToolkitInstantiationException(e);
            CacheConfiguration.TransactionalMode transactionalMode = this.getTimeoutBehavior(false).getTransactionalMode();
            return transactionalMode;
        }
        catch (NonStopException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.TIMEOUT);
            CacheConfiguration.TransactionalMode transactionalMode = this.getTimeoutBehavior(false).getTransactionalMode();
            return transactionalMode;
        }
        catch (RejoinException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.REJOIN_TIMEOUT);
            CacheConfiguration.TransactionalMode transactionalMode = this.getTimeoutBehavior(true).getTransactionalMode();
            return transactionalMode;
        }
        finally {
            this.nonStop.finish();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Element remove(Object arg0) {
        this.nonStop.start((NonStopConfiguration)this.toolkitNonStopConfiguration);
        try {
            this.throwNonStopExceptionWhenClusterNotInit();
            Element _ret = this.delegate.remove(arg0);
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.SUCCESS);
            Element element = _ret;
            return element;
        }
        catch (NonStopToolkitInstantiationException e) {
            this.handleNonStopToolkitInstantiationException(e);
            Element element = this.getTimeoutBehavior(false).remove(arg0);
            return element;
        }
        catch (NonStopException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.TIMEOUT);
            Element element = this.getTimeoutBehavior(false).remove(arg0);
            return element;
        }
        catch (RejoinException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.REJOIN_TIMEOUT);
            Element element = this.getTimeoutBehavior(true).remove(arg0);
            return element;
        }
        finally {
            this.nonStop.finish();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Element get(Object arg0) {
        this.nonStop.start((NonStopConfiguration)this.toolkitNonStopConfiguration);
        try {
            this.throwNonStopExceptionWhenClusterNotInit();
            Element _ret = this.delegate.get(arg0);
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.SUCCESS);
            Element element = _ret;
            return element;
        }
        catch (NonStopToolkitInstantiationException e) {
            this.handleNonStopToolkitInstantiationException(e);
            Element element = this.getTimeoutBehavior(false).get(arg0);
            return element;
        }
        catch (NonStopException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.TIMEOUT);
            Element element = this.getTimeoutBehavior(false).get(arg0);
            return element;
        }
        catch (RejoinException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.REJOIN_TIMEOUT);
            Element element = this.getTimeoutBehavior(true).get(arg0);
            return element;
        }
        finally {
            this.nonStop.finish();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean put(Element arg0) throws CacheException {
        this.nonStop.start((NonStopConfiguration)this.toolkitNonStopConfiguration);
        try {
            this.throwNonStopExceptionWhenClusterNotInit();
            boolean _ret = this.delegate.put(arg0);
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.SUCCESS);
            boolean bl = _ret;
            return bl;
        }
        catch (NonStopToolkitInstantiationException e) {
            this.handleNonStopToolkitInstantiationException(e);
            boolean bl = this.getTimeoutBehavior(false).put(arg0);
            return bl;
        }
        catch (NonStopException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.TIMEOUT);
            boolean bl = this.getTimeoutBehavior(false).put(arg0);
            return bl;
        }
        catch (RejoinException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.REJOIN_TIMEOUT);
            boolean bl = this.getTimeoutBehavior(true).put(arg0);
            return bl;
        }
        finally {
            this.nonStop.finish();
        }
    }

    public void putAll(Collection arg0) throws CacheException {
        this.nonStop.start((NonStopConfiguration)this.bulkOpsToolkitNonStopConfiguration);
        try {
            this.throwNonStopExceptionWhenClusterNotInit();
            this.delegate.putAll(arg0);
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.SUCCESS);
        }
        catch (NonStopToolkitInstantiationException e) {
            this.handleNonStopToolkitInstantiationException(e);
            this.getTimeoutBehavior(false).putAll(arg0);
        }
        catch (NonStopException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.TIMEOUT);
            this.getTimeoutBehavior(false).putAll(arg0);
        }
        catch (RejoinException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.REJOIN_TIMEOUT);
            this.getTimeoutBehavior(true).putAll(arg0);
        }
        finally {
            this.nonStop.finish();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Element putIfAbsent(Element arg0) throws NullPointerException {
        this.nonStop.start((NonStopConfiguration)this.toolkitNonStopConfiguration);
        try {
            this.throwNonStopExceptionWhenClusterNotInit();
            Element _ret = this.delegate.putIfAbsent(arg0);
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.SUCCESS);
            Element element = _ret;
            return element;
        }
        catch (NonStopToolkitInstantiationException e) {
            this.handleNonStopToolkitInstantiationException(e);
            Element element = this.getTimeoutBehavior(false).putIfAbsent(arg0);
            return element;
        }
        catch (NonStopException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.TIMEOUT);
            Element element = this.getTimeoutBehavior(false).putIfAbsent(arg0);
            return element;
        }
        catch (RejoinException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.REJOIN_TIMEOUT);
            Element element = this.getTimeoutBehavior(true).putIfAbsent(arg0);
            return element;
        }
        finally {
            this.nonStop.finish();
        }
    }

    @Override
    public void flush() throws IOException {
        this.nonStop.start((NonStopConfiguration)this.toolkitNonStopConfiguration);
        try {
            this.throwNonStopExceptionWhenClusterNotInit();
            this.delegate.flush();
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.SUCCESS);
        }
        catch (NonStopToolkitInstantiationException e) {
            this.handleNonStopToolkitInstantiationException(e);
            this.getTimeoutBehavior(false).flush();
        }
        catch (NonStopException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.TIMEOUT);
            this.getTimeoutBehavior(false).flush();
        }
        catch (RejoinException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.REJOIN_TIMEOUT);
            this.getTimeoutBehavior(true).flush();
        }
        finally {
            this.nonStop.finish();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean containsKey(Object arg0) {
        this.nonStop.start((NonStopConfiguration)this.toolkitNonStopConfiguration);
        try {
            this.throwNonStopExceptionWhenClusterNotInit();
            boolean _ret = this.delegate.containsKey(arg0);
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.SUCCESS);
            boolean bl = _ret;
            return bl;
        }
        catch (NonStopToolkitInstantiationException e) {
            this.handleNonStopToolkitInstantiationException(e);
            boolean bl = this.getTimeoutBehavior(false).containsKey(arg0);
            return bl;
        }
        catch (NonStopException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.TIMEOUT);
            boolean bl = this.getTimeoutBehavior(false).containsKey(arg0);
            return bl;
        }
        catch (RejoinException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.REJOIN_TIMEOUT);
            boolean bl = this.getTimeoutBehavior(true).containsKey(arg0);
            return bl;
        }
        finally {
            this.nonStop.finish();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean replace(Element arg0, Element arg1, ElementValueComparator arg2) throws NullPointerException, IllegalArgumentException {
        this.nonStop.start((NonStopConfiguration)this.toolkitNonStopConfiguration);
        try {
            this.throwNonStopExceptionWhenClusterNotInit();
            boolean _ret = this.delegate.replace(arg0, arg1, arg2);
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.SUCCESS);
            boolean bl = _ret;
            return bl;
        }
        catch (NonStopToolkitInstantiationException e) {
            this.handleNonStopToolkitInstantiationException(e);
            boolean bl = this.getTimeoutBehavior(false).replace(arg0, arg1, arg2);
            return bl;
        }
        catch (NonStopException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.TIMEOUT);
            boolean bl = this.getTimeoutBehavior(false).replace(arg0, arg1, arg2);
            return bl;
        }
        catch (RejoinException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.REJOIN_TIMEOUT);
            boolean bl = this.getTimeoutBehavior(true).replace(arg0, arg1, arg2);
            return bl;
        }
        finally {
            this.nonStop.finish();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Element replace(Element arg0) throws NullPointerException {
        this.nonStop.start((NonStopConfiguration)this.toolkitNonStopConfiguration);
        try {
            this.throwNonStopExceptionWhenClusterNotInit();
            Element _ret = this.delegate.replace(arg0);
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.SUCCESS);
            Element element = _ret;
            return element;
        }
        catch (NonStopToolkitInstantiationException e) {
            this.handleNonStopToolkitInstantiationException(e);
            Element element = this.getTimeoutBehavior(false).replace(arg0);
            return element;
        }
        catch (NonStopException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.TIMEOUT);
            Element element = this.getTimeoutBehavior(false).replace(arg0);
            return element;
        }
        catch (RejoinException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.REJOIN_TIMEOUT);
            Element element = this.getTimeoutBehavior(true).replace(arg0);
            return element;
        }
        finally {
            this.nonStop.finish();
        }
    }

    @Override
    public int getSize() {
        this.nonStop.start((NonStopConfiguration)this.bulkOpsToolkitNonStopConfiguration);
        try {
            this.throwNonStopExceptionWhenClusterNotInit();
            int _ret = this.delegate.getSize();
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.SUCCESS);
            int n = _ret;
            return n;
        }
        catch (NonStopToolkitInstantiationException e) {
            this.handleNonStopToolkitInstantiationException(e);
            int n = this.getTimeoutBehavior(false).getSize();
            return n;
        }
        catch (NonStopException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.TIMEOUT);
            int n = this.getTimeoutBehavior(false).getSize();
            return n;
        }
        catch (RejoinException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.REJOIN_TIMEOUT);
            int n = this.getTimeoutBehavior(true).getSize();
            return n;
        }
        finally {
            this.nonStop.finish();
        }
    }

    @Override
    public void removeAll() throws CacheException {
        this.nonStop.start((NonStopConfiguration)this.bulkOpsToolkitNonStopConfiguration);
        try {
            this.throwNonStopExceptionWhenClusterNotInit();
            this.delegate.removeAll();
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.SUCCESS);
        }
        catch (NonStopToolkitInstantiationException e) {
            this.handleNonStopToolkitInstantiationException(e);
            this.getTimeoutBehavior(false).removeAll();
        }
        catch (NonStopException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.TIMEOUT);
            this.getTimeoutBehavior(false).removeAll();
        }
        catch (RejoinException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.REJOIN_TIMEOUT);
            this.getTimeoutBehavior(true).removeAll();
        }
        finally {
            this.nonStop.finish();
        }
    }

    public void removeAll(Collection arg0) {
        this.nonStop.start((NonStopConfiguration)this.bulkOpsToolkitNonStopConfiguration);
        try {
            this.throwNonStopExceptionWhenClusterNotInit();
            this.delegate.removeAll(arg0);
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.SUCCESS);
        }
        catch (NonStopToolkitInstantiationException e) {
            this.handleNonStopToolkitInstantiationException(e);
            this.getTimeoutBehavior(false).removeAll(arg0);
        }
        catch (NonStopException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.TIMEOUT);
            this.getTimeoutBehavior(false).removeAll(arg0);
        }
        catch (RejoinException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.REJOIN_TIMEOUT);
            this.getTimeoutBehavior(true).removeAll(arg0);
        }
        finally {
            this.nonStop.finish();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Element removeElement(Element arg0, ElementValueComparator arg1) throws NullPointerException {
        this.nonStop.start((NonStopConfiguration)this.toolkitNonStopConfiguration);
        try {
            this.throwNonStopExceptionWhenClusterNotInit();
            Element _ret = this.delegate.removeElement(arg0, arg1);
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.SUCCESS);
            Element element = _ret;
            return element;
        }
        catch (NonStopToolkitInstantiationException e) {
            this.handleNonStopToolkitInstantiationException(e);
            Element element = this.getTimeoutBehavior(false).removeElement(arg0, arg1);
            return element;
        }
        catch (NonStopException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.TIMEOUT);
            Element element = this.getTimeoutBehavior(false).removeElement(arg0, arg1);
            return element;
        }
        catch (RejoinException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.REJOIN_TIMEOUT);
            Element element = this.getTimeoutBehavior(true).removeElement(arg0, arg1);
            return element;
        }
        finally {
            this.nonStop.finish();
        }
    }

    @Override
    public List getKeys() {
        this.nonStop.start((NonStopConfiguration)this.toolkitNonStopConfiguration);
        try {
            this.throwNonStopExceptionWhenClusterNotInit();
            List _ret = NonStopSubTypeProxyUtil.newNonStopSubTypeProxy(List.class, this.delegate.getKeys());
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.SUCCESS);
            List list = _ret;
            return list;
        }
        catch (NonStopToolkitInstantiationException e) {
            this.handleNonStopToolkitInstantiationException(e);
            List list = this.getTimeoutBehavior(false).getKeys();
            return list;
        }
        catch (NonStopException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.TIMEOUT);
            List list = this.getTimeoutBehavior(false).getKeys();
            return list;
        }
        catch (RejoinException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.REJOIN_TIMEOUT);
            List list = this.getTimeoutBehavior(true).getKeys();
            return list;
        }
        finally {
            this.nonStop.finish();
        }
    }

    @Override
    public boolean containsKeyInMemory(Object arg0) {
        if (this.delegate != null) {
            boolean _ret = this.delegate.containsKeyInMemory(arg0);
            return _ret;
        }
        return NoOpOnTimeoutStore.getInstance().containsKeyInMemory(arg0);
    }

    @Override
    public boolean containsKeyOffHeap(Object arg0) {
        if (this.delegate != null) {
            boolean _ret = this.delegate.containsKeyOffHeap(arg0);
            return _ret;
        }
        return NoOpOnTimeoutStore.getInstance().containsKeyOffHeap(arg0);
    }

    @Override
    public long getInMemorySizeInBytes() {
        if (this.delegate != null) {
            long _ret = this.delegate.getInMemorySizeInBytes();
            return _ret;
        }
        return NoOpOnTimeoutStore.getInstance().getInMemorySizeInBytes();
    }

    @Override
    public int getInMemorySize() {
        if (this.delegate != null) {
            int _ret = this.delegate.getInMemorySize();
            return _ret;
        }
        return NoOpOnTimeoutStore.getInstance().getInMemorySize();
    }

    @Override
    public long getOffHeapSizeInBytes() {
        if (this.delegate != null) {
            long _ret = this.delegate.getOffHeapSizeInBytes();
            return _ret;
        }
        return NoOpOnTimeoutStore.getInstance().getOffHeapSizeInBytes();
    }

    @Override
    public int getOffHeapSize() {
        if (this.delegate != null) {
            int _ret = this.delegate.getOffHeapSize();
            return _ret;
        }
        return NoOpOnTimeoutStore.getInstance().getOffHeapSize();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Map getAllQuiet(Collection arg0) {
        this.nonStop.start((NonStopConfiguration)this.bulkOpsToolkitNonStopConfiguration);
        try {
            this.throwNonStopExceptionWhenClusterNotInit();
            Map<Object, Element> _ret = NonStopSubTypeProxyUtil.newNonStopSubTypeProxy(Map.class, this.delegate.getAllQuiet(arg0));
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.SUCCESS);
            Map<Object, Element> map = _ret;
            return map;
        }
        catch (NonStopToolkitInstantiationException e) {
            this.handleNonStopToolkitInstantiationException(e);
            Map<Object, Element> map = this.getTimeoutBehavior(false).getAllQuiet(arg0);
            return map;
        }
        catch (NonStopException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.TIMEOUT);
            Map<Object, Element> map = this.getTimeoutBehavior(false).getAllQuiet(arg0);
            return map;
        }
        catch (RejoinException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.REJOIN_TIMEOUT);
            Map<Object, Element> map = this.getTimeoutBehavior(true).getAllQuiet(arg0);
            return map;
        }
        finally {
            this.nonStop.finish();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Map getAll(Collection arg0) {
        this.nonStop.start((NonStopConfiguration)this.bulkOpsToolkitNonStopConfiguration);
        try {
            this.throwNonStopExceptionWhenClusterNotInit();
            Map<Object, Element> _ret = NonStopSubTypeProxyUtil.newNonStopSubTypeProxy(Map.class, this.delegate.getAll(arg0));
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.SUCCESS);
            Map<Object, Element> map = _ret;
            return map;
        }
        catch (NonStopToolkitInstantiationException e) {
            this.handleNonStopToolkitInstantiationException(e);
            Map<Object, Element> map = this.getTimeoutBehavior(false).getAll(arg0);
            return map;
        }
        catch (NonStopException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.TIMEOUT);
            Map<Object, Element> map = this.getTimeoutBehavior(false).getAll(arg0);
            return map;
        }
        catch (RejoinException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.REJOIN_TIMEOUT);
            Map<Object, Element> map = this.getTimeoutBehavior(true).getAll(arg0);
            return map;
        }
        finally {
            this.nonStop.finish();
        }
    }

    @Override
    public void setNodeCoherent(boolean arg0) throws UnsupportedOperationException, TerracottaNotRunningException {
        this.nonStop.start((NonStopConfiguration)this.bulkOpsToolkitNonStopConfiguration);
        try {
            this.throwNonStopExceptionWhenClusterNotInit();
            this.delegate.setNodeCoherent(arg0);
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.SUCCESS);
        }
        catch (NonStopToolkitInstantiationException e) {
            this.handleNonStopToolkitInstantiationException(e);
            this.getTimeoutBehavior(false).setNodeCoherent(arg0);
        }
        catch (NonStopException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.TIMEOUT);
            this.getTimeoutBehavior(false).setNodeCoherent(arg0);
        }
        catch (RejoinException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.REJOIN_TIMEOUT);
            this.getTimeoutBehavior(true).setNodeCoherent(arg0);
        }
        finally {
            this.nonStop.finish();
        }
    }

    public void setAttributeExtractors(Map arg0) {
        this.nonStop.start((NonStopConfiguration)this.toolkitNonStopConfiguration);
        try {
            this.throwNonStopExceptionWhenClusterNotInit();
            this.delegate.setAttributeExtractors(arg0);
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.SUCCESS);
        }
        catch (NonStopToolkitInstantiationException e) {
            this.handleNonStopToolkitInstantiationException(e);
            this.getTimeoutBehavior(false).setAttributeExtractors(arg0);
        }
        catch (NonStopException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.TIMEOUT);
            this.getTimeoutBehavior(false).setAttributeExtractors(arg0);
        }
        catch (RejoinException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.REJOIN_TIMEOUT);
            this.getTimeoutBehavior(true).setAttributeExtractors(arg0);
        }
        finally {
            this.nonStop.finish();
        }
    }

    @Override
    public boolean hasAbortedSizeOf() {
        this.nonStop.start((NonStopConfiguration)this.toolkitNonStopConfiguration);
        try {
            this.throwNonStopExceptionWhenClusterNotInit();
            boolean _ret = this.delegate.hasAbortedSizeOf();
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.SUCCESS);
            boolean bl = _ret;
            return bl;
        }
        catch (NonStopToolkitInstantiationException e) {
            this.handleNonStopToolkitInstantiationException(e);
            boolean bl = this.getTimeoutBehavior(false).hasAbortedSizeOf();
            return bl;
        }
        catch (NonStopException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.TIMEOUT);
            boolean bl = this.getTimeoutBehavior(false).hasAbortedSizeOf();
            return bl;
        }
        catch (RejoinException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.REJOIN_TIMEOUT);
            boolean bl = this.getTimeoutBehavior(true).hasAbortedSizeOf();
            return bl;
        }
        finally {
            this.nonStop.finish();
        }
    }

    @Override
    public int getOnDiskSize() {
        this.nonStop.start((NonStopConfiguration)this.toolkitNonStopConfiguration);
        try {
            this.throwNonStopExceptionWhenClusterNotInit();
            int _ret = this.delegate.getOnDiskSize();
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.SUCCESS);
            int n = _ret;
            return n;
        }
        catch (NonStopToolkitInstantiationException e) {
            this.handleNonStopToolkitInstantiationException(e);
            int n = this.getTimeoutBehavior(false).getOnDiskSize();
            return n;
        }
        catch (NonStopException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.TIMEOUT);
            int n = this.getTimeoutBehavior(false).getOnDiskSize();
            return n;
        }
        catch (RejoinException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.REJOIN_TIMEOUT);
            int n = this.getTimeoutBehavior(true).getOnDiskSize();
            return n;
        }
        finally {
            this.nonStop.finish();
        }
    }

    @Override
    public void setInMemoryEvictionPolicy(Policy arg0) {
        this.nonStop.start((NonStopConfiguration)this.toolkitNonStopConfiguration);
        try {
            this.throwNonStopExceptionWhenClusterNotInit();
            this.delegate.setInMemoryEvictionPolicy(arg0);
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.SUCCESS);
        }
        catch (NonStopToolkitInstantiationException e) {
            this.handleNonStopToolkitInstantiationException(e);
            this.getTimeoutBehavior(false).setInMemoryEvictionPolicy(arg0);
        }
        catch (NonStopException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.TIMEOUT);
            this.getTimeoutBehavior(false).setInMemoryEvictionPolicy(arg0);
        }
        catch (RejoinException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.REJOIN_TIMEOUT);
            this.getTimeoutBehavior(true).setInMemoryEvictionPolicy(arg0);
        }
        finally {
            this.nonStop.finish();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean putWithWriter(Element arg0, CacheWriterManager arg1) throws CacheException {
        this.nonStop.start((NonStopConfiguration)this.toolkitNonStopConfiguration);
        try {
            this.throwNonStopExceptionWhenClusterNotInit();
            boolean _ret = this.delegate.putWithWriter(arg0, arg1);
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.SUCCESS);
            boolean bl = _ret;
            return bl;
        }
        catch (NonStopToolkitInstantiationException e) {
            this.handleNonStopToolkitInstantiationException(e);
            boolean bl = this.getTimeoutBehavior(false).putWithWriter(arg0, arg1);
            return bl;
        }
        catch (NonStopException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.TIMEOUT);
            boolean bl = this.getTimeoutBehavior(false).putWithWriter(arg0, arg1);
            return bl;
        }
        catch (RejoinException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.REJOIN_TIMEOUT);
            boolean bl = this.getTimeoutBehavior(true).putWithWriter(arg0, arg1);
            return bl;
        }
        finally {
            this.nonStop.finish();
        }
    }

    @Override
    public void recalculateSize(Object arg0) {
        this.nonStop.start((NonStopConfiguration)this.toolkitNonStopConfiguration);
        try {
            this.throwNonStopExceptionWhenClusterNotInit();
            this.delegate.recalculateSize(arg0);
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.SUCCESS);
        }
        catch (NonStopToolkitInstantiationException e) {
            this.handleNonStopToolkitInstantiationException(e);
            this.getTimeoutBehavior(false).recalculateSize(arg0);
        }
        catch (NonStopException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.TIMEOUT);
            this.getTimeoutBehavior(false).recalculateSize(arg0);
        }
        catch (RejoinException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.REJOIN_TIMEOUT);
            this.getTimeoutBehavior(true).recalculateSize(arg0);
        }
        finally {
            this.nonStop.finish();
        }
    }

    @Override
    public boolean isCacheCoherent() {
        this.nonStop.start((NonStopConfiguration)this.toolkitNonStopConfiguration);
        try {
            this.throwNonStopExceptionWhenClusterNotInit();
            boolean _ret = this.delegate.isCacheCoherent();
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.SUCCESS);
            boolean bl = _ret;
            return bl;
        }
        catch (NonStopToolkitInstantiationException e) {
            this.handleNonStopToolkitInstantiationException(e);
            boolean bl = this.getTimeoutBehavior(false).isCacheCoherent();
            return bl;
        }
        catch (NonStopException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.TIMEOUT);
            boolean bl = this.getTimeoutBehavior(false).isCacheCoherent();
            return bl;
        }
        catch (RejoinException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.REJOIN_TIMEOUT);
            boolean bl = this.getTimeoutBehavior(true).isCacheCoherent();
            return bl;
        }
        finally {
            this.nonStop.finish();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public long getOnDiskSizeInBytes() {
        this.nonStop.start((NonStopConfiguration)this.toolkitNonStopConfiguration);
        try {
            this.throwNonStopExceptionWhenClusterNotInit();
            long _ret = this.delegate.getOnDiskSizeInBytes();
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.SUCCESS);
            long l = _ret;
            return l;
        }
        catch (NonStopToolkitInstantiationException e) {
            this.handleNonStopToolkitInstantiationException(e);
            long l = this.getTimeoutBehavior(false).getOnDiskSizeInBytes();
            return l;
        }
        catch (NonStopException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.TIMEOUT);
            long l = this.getTimeoutBehavior(false).getOnDiskSizeInBytes();
            return l;
        }
        catch (RejoinException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.REJOIN_TIMEOUT);
            long l = this.getTimeoutBehavior(true).getOnDiskSizeInBytes();
            return l;
        }
        finally {
            this.nonStop.finish();
        }
    }

    @Override
    public int getTerracottaClusteredSize() {
        this.nonStop.start((NonStopConfiguration)this.bulkOpsToolkitNonStopConfiguration);
        try {
            this.throwNonStopExceptionWhenClusterNotInit();
            int _ret = this.delegate.getTerracottaClusteredSize();
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.SUCCESS);
            int n = _ret;
            return n;
        }
        catch (NonStopToolkitInstantiationException e) {
            this.handleNonStopToolkitInstantiationException(e);
            int n = this.getTimeoutBehavior(false).getTerracottaClusteredSize();
            return n;
        }
        catch (NonStopException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.TIMEOUT);
            int n = this.getTimeoutBehavior(false).getTerracottaClusteredSize();
            return n;
        }
        catch (RejoinException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.REJOIN_TIMEOUT);
            int n = this.getTimeoutBehavior(true).getTerracottaClusteredSize();
            return n;
        }
        finally {
            this.nonStop.finish();
        }
    }

    @Override
    public void expireElements() {
        this.nonStop.start((NonStopConfiguration)this.toolkitNonStopConfiguration);
        try {
            this.throwNonStopExceptionWhenClusterNotInit();
            this.delegate.expireElements();
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.SUCCESS);
        }
        catch (NonStopToolkitInstantiationException e) {
            this.handleNonStopToolkitInstantiationException(e);
            this.getTimeoutBehavior(false).expireElements();
        }
        catch (NonStopException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.TIMEOUT);
            this.getTimeoutBehavior(false).expireElements();
        }
        catch (RejoinException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.REJOIN_TIMEOUT);
            this.getTimeoutBehavior(true).expireElements();
        }
        finally {
            this.nonStop.finish();
        }
    }

    @Override
    public boolean isNodeCoherent() throws TerracottaNotRunningException {
        this.nonStop.start((NonStopConfiguration)this.toolkitNonStopConfiguration);
        try {
            this.throwNonStopExceptionWhenClusterNotInit();
            boolean _ret = this.delegate.isNodeCoherent();
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.SUCCESS);
            boolean bl = _ret;
            return bl;
        }
        catch (NonStopToolkitInstantiationException e) {
            this.handleNonStopToolkitInstantiationException(e);
            boolean bl = this.getTimeoutBehavior(false).isNodeCoherent();
            return bl;
        }
        catch (NonStopException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.TIMEOUT);
            boolean bl = this.getTimeoutBehavior(false).isNodeCoherent();
            return bl;
        }
        catch (RejoinException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.REJOIN_TIMEOUT);
            boolean bl = this.getTimeoutBehavior(true).isNodeCoherent();
            return bl;
        }
        finally {
            this.nonStop.finish();
        }
    }

    @Override
    public void addStoreListener(StoreListener arg0) {
        this.nonStop.start((NonStopConfiguration)this.toolkitNonStopConfiguration);
        try {
            this.throwNonStopExceptionWhenClusterNotInit();
            this.delegate.addStoreListener(arg0);
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.SUCCESS);
        }
        catch (NonStopToolkitInstantiationException e) {
            this.handleNonStopToolkitInstantiationException(e);
            this.getTimeoutBehavior(false).addStoreListener(arg0);
        }
        catch (NonStopException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.TIMEOUT);
            this.getTimeoutBehavior(false).addStoreListener(arg0);
        }
        catch (RejoinException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.REJOIN_TIMEOUT);
            this.getTimeoutBehavior(true).addStoreListener(arg0);
        }
        finally {
            this.nonStop.finish();
        }
    }

    @Override
    public boolean isClusterCoherent() throws TerracottaNotRunningException {
        this.nonStop.start((NonStopConfiguration)this.toolkitNonStopConfiguration);
        try {
            this.throwNonStopExceptionWhenClusterNotInit();
            boolean _ret = this.delegate.isClusterCoherent();
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.SUCCESS);
            boolean bl = _ret;
            return bl;
        }
        catch (NonStopToolkitInstantiationException e) {
            this.handleNonStopToolkitInstantiationException(e);
            boolean bl = this.getTimeoutBehavior(false).isClusterCoherent();
            return bl;
        }
        catch (NonStopException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.TIMEOUT);
            boolean bl = this.getTimeoutBehavior(false).isClusterCoherent();
            return bl;
        }
        catch (RejoinException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.REJOIN_TIMEOUT);
            boolean bl = this.getTimeoutBehavior(true).isClusterCoherent();
            return bl;
        }
        finally {
            this.nonStop.finish();
        }
    }

    @Override
    public Policy getInMemoryEvictionPolicy() {
        this.nonStop.start((NonStopConfiguration)this.toolkitNonStopConfiguration);
        try {
            this.throwNonStopExceptionWhenClusterNotInit();
            Policy _ret = this.delegate.getInMemoryEvictionPolicy();
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.SUCCESS);
            Policy policy = _ret;
            return policy;
        }
        catch (NonStopToolkitInstantiationException e) {
            this.handleNonStopToolkitInstantiationException(e);
            Policy policy = this.getTimeoutBehavior(false).getInMemoryEvictionPolicy();
            return policy;
        }
        catch (NonStopException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.TIMEOUT);
            Policy policy = this.getTimeoutBehavior(false).getInMemoryEvictionPolicy();
            return policy;
        }
        catch (RejoinException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.REJOIN_TIMEOUT);
            Policy policy = this.getTimeoutBehavior(true).getInMemoryEvictionPolicy();
            return policy;
        }
        finally {
            this.nonStop.finish();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Element removeWithWriter(Object arg0, CacheWriterManager arg1) throws CacheException {
        this.nonStop.start((NonStopConfiguration)this.toolkitNonStopConfiguration);
        try {
            this.throwNonStopExceptionWhenClusterNotInit();
            Element _ret = this.delegate.removeWithWriter(arg0, arg1);
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.SUCCESS);
            Element element = _ret;
            return element;
        }
        catch (NonStopToolkitInstantiationException e) {
            this.handleNonStopToolkitInstantiationException(e);
            Element element = this.getTimeoutBehavior(false).removeWithWriter(arg0, arg1);
            return element;
        }
        catch (NonStopException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.TIMEOUT);
            Element element = this.getTimeoutBehavior(false).removeWithWriter(arg0, arg1);
            return element;
        }
        catch (RejoinException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.REJOIN_TIMEOUT);
            Element element = this.getTimeoutBehavior(true).removeWithWriter(arg0, arg1);
            return element;
        }
        finally {
            this.nonStop.finish();
        }
    }

    @Override
    public Set<Attribute> getSearchAttributes() {
        this.nonStop.start((NonStopConfiguration)this.toolkitNonStopConfiguration);
        try {
            this.throwNonStopExceptionWhenClusterNotInit();
            Set<Attribute> _ret = this.delegate.getSearchAttributes();
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.SUCCESS);
            Set<Attribute> set = _ret;
            return set;
        }
        catch (NonStopToolkitInstantiationException e) {
            this.handleNonStopToolkitInstantiationException(e);
            Set<Attribute> set = this.getTimeoutBehavior(false).getSearchAttributes();
            return set;
        }
        catch (NonStopException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.TIMEOUT);
            Set<Attribute> set = this.getTimeoutBehavior(false).getSearchAttributes();
            return set;
        }
        catch (RejoinException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.REJOIN_TIMEOUT);
            Set<Attribute> set = this.getTimeoutBehavior(true).getSearchAttributes();
            return set;
        }
        finally {
            this.nonStop.finish();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Attribute getSearchAttribute(String arg0) {
        this.nonStop.start((NonStopConfiguration)this.toolkitNonStopConfiguration);
        try {
            this.throwNonStopExceptionWhenClusterNotInit();
            Attribute _ret = this.delegate.getSearchAttribute(arg0);
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.SUCCESS);
            Attribute attribute = _ret;
            return attribute;
        }
        catch (NonStopToolkitInstantiationException e) {
            this.handleNonStopToolkitInstantiationException(e);
            Attribute attribute = this.getTimeoutBehavior(false).getSearchAttribute(arg0);
            return attribute;
        }
        catch (NonStopException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.TIMEOUT);
            Attribute attribute = this.getTimeoutBehavior(false).getSearchAttribute(arg0);
            return attribute;
        }
        catch (RejoinException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.REJOIN_TIMEOUT);
            Attribute attribute = this.getTimeoutBehavior(true).getSearchAttribute(arg0);
            return attribute;
        }
        finally {
            this.nonStop.finish();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean containsKeyOnDisk(Object arg0) {
        this.nonStop.start((NonStopConfiguration)this.toolkitNonStopConfiguration);
        try {
            this.throwNonStopExceptionWhenClusterNotInit();
            boolean _ret = this.delegate.containsKeyOnDisk(arg0);
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.SUCCESS);
            boolean bl = _ret;
            return bl;
        }
        catch (NonStopToolkitInstantiationException e) {
            this.handleNonStopToolkitInstantiationException(e);
            boolean bl = this.getTimeoutBehavior(false).containsKeyOnDisk(arg0);
            return bl;
        }
        catch (NonStopException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.TIMEOUT);
            boolean bl = this.getTimeoutBehavior(false).containsKeyOnDisk(arg0);
            return bl;
        }
        catch (RejoinException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.REJOIN_TIMEOUT);
            boolean bl = this.getTimeoutBehavior(true).containsKeyOnDisk(arg0);
            return bl;
        }
        finally {
            this.nonStop.finish();
        }
    }

    @Override
    public Results executeQuery(StoreQuery arg0) throws SearchException {
        try {
            this.throwNonStopExceptionWhenClusterNotInit();
            Results _ret = NonStopSubTypeProxyUtil.newNonStopSubTypeProxy(Results.class, this.delegate.executeQuery(arg0));
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.SUCCESS);
            return _ret;
        }
        catch (NonStopToolkitInstantiationException e) {
            this.handleNonStopToolkitInstantiationException(e);
            return this.getTimeoutBehavior(false).executeQuery(arg0);
        }
        catch (NonStopException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.TIMEOUT);
            return this.getTimeoutBehavior(false).executeQuery(arg0);
        }
        catch (RejoinException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.REJOIN_TIMEOUT);
            return this.getTimeoutBehavior(true).executeQuery(arg0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Element getQuiet(Object arg0) {
        this.nonStop.start((NonStopConfiguration)this.toolkitNonStopConfiguration);
        try {
            this.throwNonStopExceptionWhenClusterNotInit();
            Element _ret = this.delegate.getQuiet(arg0);
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.SUCCESS);
            Element element = _ret;
            return element;
        }
        catch (NonStopToolkitInstantiationException e) {
            this.handleNonStopToolkitInstantiationException(e);
            Element element = this.getTimeoutBehavior(false).getQuiet(arg0);
            return element;
        }
        catch (NonStopException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.TIMEOUT);
            Element element = this.getTimeoutBehavior(false).getQuiet(arg0);
            return element;
        }
        catch (RejoinException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.REJOIN_TIMEOUT);
            Element element = this.getTimeoutBehavior(true).getQuiet(arg0);
            return element;
        }
        finally {
            this.nonStop.finish();
        }
    }

    @Override
    public Status getStatus() {
        this.nonStop.start((NonStopConfiguration)this.toolkitNonStopConfiguration);
        try {
            this.throwNonStopExceptionWhenClusterNotInit();
            Status _ret = this.delegate.getStatus();
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.SUCCESS);
            Status status = _ret;
            return status;
        }
        catch (NonStopToolkitInstantiationException e) {
            this.handleNonStopToolkitInstantiationException(e);
            Status status = this.getTimeoutBehavior(false).getStatus();
            return status;
        }
        catch (NonStopException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.TIMEOUT);
            Status status = this.getTimeoutBehavior(false).getStatus();
            return status;
        }
        catch (RejoinException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.REJOIN_TIMEOUT);
            Status status = this.getTimeoutBehavior(true).getStatus();
            return status;
        }
        finally {
            this.nonStop.finish();
        }
    }

    @Override
    public void notifyCacheEventListenersChanged() {
        this.nonStop.start((NonStopConfiguration)this.toolkitNonStopConfiguration);
        try {
            this.throwNonStopExceptionWhenClusterNotInit();
            this.delegate.notifyCacheEventListenersChanged();
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.SUCCESS);
        }
        catch (NonStopToolkitInstantiationException e) {
            this.handleNonStopToolkitInstantiationException(e);
            this.getTimeoutBehavior(false).notifyCacheEventListenersChanged();
        }
        catch (NonStopException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.TIMEOUT);
            this.getTimeoutBehavior(false).notifyCacheEventListenersChanged();
        }
        catch (RejoinException e) {
            this.nonstopObserver.end(CacheOperationOutcomes.NonStopOperationOutcomes.REJOIN_TIMEOUT);
            this.getTimeoutBehavior(true).notifyCacheEventListenersChanged();
        }
        finally {
            this.nonStop.finish();
        }
    }

    static {
        LOCAL_METHODS.add("unsafeGet");
        LOCAL_METHODS.add("containsKeyInMemory");
        LOCAL_METHODS.add("containsKeyOffHeap");
        LOCAL_METHODS.add("getInMemorySizeInBytes");
        LOCAL_METHODS.add("getInMemorySize");
        LOCAL_METHODS.add("getOffHeapSizeInBytes");
        LOCAL_METHODS.add("getOffHeapSize");
        LOCAL_METHODS.add("getLocalKeys");
        METHODS_TO_SKIP = new HashSet<String>();
        METHODS_TO_SKIP.add("public abstract net.sf.ehcache.writer.writebehind.WriteBehind net.sf.ehcache.store.TerracottaStore.createWriteBehind()");
        METHODS_TO_SKIP.add("public abstract java.lang.Object net.sf.ehcache.store.Store.getInternalContext()");
        METHODS_TO_SKIP.add("public abstract boolean net.sf.ehcache.store.Store.bufferFull()");
        METHODS_TO_SKIP.add("public abstract java.lang.Object net.sf.ehcache.store.Store.getMBean()");
        METHODS_TO_SKIP.add("public abstract void net.sf.ehcache.store.Store.dispose()");
        METHODS_TO_SKIP.add("public abstract void net.sf.ehcache.store.Store.removeStoreListener(net.sf.ehcache.store.StoreListener)");
        METHODS_TO_SKIP.add("public abstract void net.sf.ehcache.store.Store.waitUntilClusterCoherent() throws java.lang.UnsupportedOperationException,net.sf.ehcache.terracotta.TerracottaNotRunningException,java.lang.InterruptedException");
    }

    private static class BulkOpsToolkitNonStopConfiguration
    extends ToolkitNonStopExceptionOnTimeoutConfiguration {
        public BulkOpsToolkitNonStopConfiguration(NonstopConfiguration ehcacheNonStopConfig) {
            super(ehcacheNonStopConfig);
        }

        @Override
        public long getTimeoutMillis() {
            return (long)this.ehcacheNonStopConfig.getBulkOpsTimeoutMultiplyFactor() * this.ehcacheNonStopConfig.getTimeoutMillis();
        }
    }
}

