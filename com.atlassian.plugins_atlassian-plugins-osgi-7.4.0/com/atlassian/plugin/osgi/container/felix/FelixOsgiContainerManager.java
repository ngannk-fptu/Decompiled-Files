/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ReferenceMode
 *  com.atlassian.plugin.event.PluginEventListener
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.event.events.PluginFrameworkShutdownEvent
 *  com.atlassian.plugin.event.events.PluginFrameworkStartingEvent
 *  com.atlassian.plugin.event.events.PluginFrameworkWarmRestartingEvent
 *  com.atlassian.plugin.event.events.PluginUninstalledEvent
 *  com.atlassian.plugin.event.events.PluginUpgradedEvent
 *  com.atlassian.plugin.instrumentation.PluginSystemInstrumentation
 *  com.atlassian.plugin.util.ContextClassLoaderSwitchingUtil
 *  com.atlassian.plugin.util.FileUtils
 *  com.atlassian.plugin.util.PluginUtils
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Preconditions
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.felix.framework.Felix
 *  org.apache.felix.framework.Logger
 *  org.apache.felix.framework.util.StringMap
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleActivator
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.BundleEvent
 *  org.osgi.framework.BundleException
 *  org.osgi.framework.BundleListener
 *  org.osgi.framework.FrameworkEvent
 *  org.osgi.framework.FrameworkListener
 *  org.osgi.framework.ServiceReference
 *  org.osgi.framework.ServiceRegistration
 *  org.osgi.framework.hooks.resolver.ResolverHookFactory
 *  org.osgi.framework.hooks.weaving.WeavingHook
 *  org.osgi.service.packageadmin.PackageAdmin
 *  org.osgi.util.tracker.ServiceTracker
 *  org.osgi.util.tracker.ServiceTrackerCustomizer
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.osgi.container.felix;

import com.atlassian.plugin.ReferenceMode;
import com.atlassian.plugin.event.PluginEventListener;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.event.events.PluginFrameworkShutdownEvent;
import com.atlassian.plugin.event.events.PluginFrameworkStartingEvent;
import com.atlassian.plugin.event.events.PluginFrameworkWarmRestartingEvent;
import com.atlassian.plugin.event.events.PluginUninstalledEvent;
import com.atlassian.plugin.event.events.PluginUpgradedEvent;
import com.atlassian.plugin.instrumentation.PluginSystemInstrumentation;
import com.atlassian.plugin.osgi.container.OsgiContainerException;
import com.atlassian.plugin.osgi.container.OsgiContainerManager;
import com.atlassian.plugin.osgi.container.OsgiContainerStartedEvent;
import com.atlassian.plugin.osgi.container.OsgiContainerStoppedEvent;
import com.atlassian.plugin.osgi.container.OsgiPersistentCache;
import com.atlassian.plugin.osgi.container.PackageScannerConfiguration;
import com.atlassian.plugin.osgi.container.felix.ExportsBuilder;
import com.atlassian.plugin.osgi.container.felix.FelixLoggerBridge;
import com.atlassian.plugin.osgi.container.felix.PluginKeyWeaver;
import com.atlassian.plugin.osgi.hook.dmz.DmzResolverHookFactory;
import com.atlassian.plugin.osgi.hook.rest.RestVersionResolverHookFactory;
import com.atlassian.plugin.osgi.hostcomponents.HostComponentProvider;
import com.atlassian.plugin.osgi.hostcomponents.HostComponentRegistration;
import com.atlassian.plugin.osgi.hostcomponents.impl.DefaultComponentRegistrar;
import com.atlassian.plugin.osgi.util.OsgiHeaderUtil;
import com.atlassian.plugin.util.ContextClassLoaderSwitchingUtil;
import com.atlassian.plugin.util.FileUtils;
import com.atlassian.plugin.util.PluginUtils;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.framework.Felix;
import org.apache.felix.framework.util.StringMap;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.hooks.resolver.ResolverHookFactory;
import org.osgi.framework.hooks.weaving.WeavingHook;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FelixOsgiContainerManager
implements OsgiContainerManager {
    public static final int REFRESH_TIMEOUT = 10;
    private static final Logger log = LoggerFactory.getLogger(FelixOsgiContainerManager.class);
    public static final String ATLASSIAN_BOOTDELEGATION = "atlassian.org.osgi.framework.bootdelegation";
    public static final String ATLASSIAN_BOOTDELEGATION_EXTRA = "atlassian.org.osgi.framework.bootdelegation.extra";
    public static final String ATLASSIAN_DISABLE_REFERENCE_PROTOCOL = "atlassian.felix.disable.reference.protocol";
    private final OsgiPersistentCache persistentCache;
    private Function<DefaultComponentRegistrar, BundleRegistration> bundleRegistrationFactory;
    private final PackageScannerConfiguration packageScannerConfig;
    private final HostComponentProvider hostComponentProvider;
    private final List<ServiceTracker> trackers;
    private final ExportsBuilder exportsBuilder;
    private final ThreadFactory threadFactory = runnable -> {
        Thread thread = new Thread(runnable, "Felix:Startup");
        thread.setDaemon(true);
        return thread;
    };
    private BundleRegistration registration = null;
    private Felix felix = null;
    private boolean felixRunning = false;
    private boolean disableMultipleBundleVersions = true;
    private org.apache.felix.framework.Logger felixLogger;
    private final PluginEventManager pluginEventManager;

    public FelixOsgiContainerManager(URL frameworkBundlesZip, OsgiPersistentCache persistentCache, PackageScannerConfiguration packageScannerConfig, HostComponentProvider provider, PluginEventManager eventManager) {
        this((DefaultComponentRegistrar registrar) -> new BundleRegistration(frameworkBundlesZip, persistentCache.getFrameworkBundleCache(), (DefaultComponentRegistrar)registrar, packageScannerConfig), persistentCache, packageScannerConfig, provider, eventManager);
    }

    public FelixOsgiContainerManager(File frameworkBundlesDirectory, OsgiPersistentCache persistentCache, PackageScannerConfiguration packageScannerConfig, HostComponentProvider provider, PluginEventManager eventManager) {
        this((DefaultComponentRegistrar registrar) -> new BundleRegistration(frameworkBundlesDirectory, (DefaultComponentRegistrar)registrar, packageScannerConfig), persistentCache, packageScannerConfig, provider, eventManager);
    }

    private FelixOsgiContainerManager(Function<DefaultComponentRegistrar, BundleRegistration> bundleRegistrationFactory, OsgiPersistentCache persistentCache, PackageScannerConfiguration packageScannerConfig, HostComponentProvider provider, PluginEventManager eventManager) {
        Preconditions.checkNotNull(bundleRegistrationFactory, (Object)"The bundle registration factory must not be null");
        Preconditions.checkNotNull((Object)persistentCache, (Object)"The framework bundles directory must not be null");
        Preconditions.checkNotNull((Object)packageScannerConfig, (Object)"The package scanner configuration must not be null");
        Preconditions.checkNotNull((Object)eventManager, (Object)"The plugin event manager must not be null");
        this.bundleRegistrationFactory = bundleRegistrationFactory;
        this.packageScannerConfig = packageScannerConfig;
        this.persistentCache = persistentCache;
        this.hostComponentProvider = provider;
        this.trackers = Collections.synchronizedList(new ArrayList());
        this.pluginEventManager = eventManager;
        eventManager.register((Object)this);
        this.felixLogger = new FelixLoggerBridge(log);
        this.exportsBuilder = new ExportsBuilder();
    }

    public void setFelixLogger(org.apache.felix.framework.Logger logger) {
        this.felixLogger = logger;
    }

    public void setDisableMultipleBundleVersions(boolean val) {
        this.disableMultipleBundleVersions = val;
    }

    public void clearExportCache() {
        this.exportsBuilder.clearExportCache();
    }

    @PluginEventListener
    public void onStart(PluginFrameworkStartingEvent event) {
        this.start();
    }

    @PluginEventListener
    public void onShutdown(PluginFrameworkShutdownEvent event) {
        this.stop();
    }

    @PluginEventListener
    public void onPluginUpgrade(PluginUpgradedEvent event) {
        this.registration.refreshPackages();
    }

    @PluginEventListener
    public void onPluginUninstallation(PluginUninstalledEvent event) {
        this.registration.refreshPackages();
    }

    @PluginEventListener
    public void onPluginFrameworkWarmRestarting(PluginFrameworkWarmRestartingEvent event) {
        this.registration.loadHostComponents(this.collectHostComponents(this.hostComponentProvider));
    }

    @Override
    public void start() {
        String extraBootDelegation;
        if (this.isRunning()) {
            return;
        }
        DefaultComponentRegistrar registrar = this.collectHostComponents(this.hostComponentProvider);
        StringMap configMap = new StringMap();
        configMap.put((Object)"org.osgi.framework.system.packages.extra", (Object)this.exportsBuilder.getExports(registrar.getRegistry(), this.packageScannerConfig));
        configMap.put((Object)"felix.cache.rootdir", (Object)this.persistentCache.getOsgiBundleCache().getAbsolutePath());
        configMap.put((Object)"felix.log.level", (Object)String.valueOf(this.felixLogger.getLogLevel()));
        configMap.put((Object)"felix.log.logger", (Object)this.felixLogger);
        String bootDelegation = System.getProperty(ATLASSIAN_BOOTDELEGATION);
        if (bootDelegation == null || bootDelegation.trim().length() == 0) {
            bootDelegation = "weblogic,weblogic.*,META-INF.services,jdk.*,com.yourkit,com.yourkit.*,com.chronon,com.chronon.*,org.jboss.byteman,org.jboss.byteman.*,com.jprofiler,com.jprofiler.*,org.apache.xerces,org.apache.xerces.*,org.apache.xalan,org.apache.xalan.*,org.apache.xml.serializer,sun.*,com.sun.xml.bind.v2,com.sun.xml.internal.bind.v2,com.icl.saxon,com_cenqua_clover,com.cenqua.clover,com.cenqua.clover.*,com.atlassian.clover,com.atlassian.clover.*";
        }
        if (0 != (extraBootDelegation = System.getProperty(ATLASSIAN_BOOTDELEGATION_EXTRA, "").trim()).length()) {
            bootDelegation = bootDelegation + "," + extraBootDelegation;
        }
        configMap.put((Object)"org.osgi.framework.bootdelegation", (Object)bootDelegation);
        configMap.put((Object)"felix.bootdelegation.implicit", (Object)"false");
        configMap.put((Object)"org.osgi.framework.bundle.parent", (Object)"framework");
        if (log.isDebugEnabled()) {
            log.debug("Felix configuration: {}", (Object)configMap);
        }
        this.validateConfiguration(configMap);
        try {
            this.registration = this.bundleRegistrationFactory.apply(registrar);
            ArrayList<BundleRegistration> list = new ArrayList<BundleRegistration>();
            list.add(this.registration);
            configMap.put((Object)"felix.systembundle.activators", list);
            this.felix = new Felix((Map)configMap);
            Runnable start = () -> {
                try {
                    Thread.currentThread().setContextClassLoader(null);
                    this.felix.start();
                    this.felixRunning = true;
                }
                catch (BundleException e) {
                    throw new OsgiContainerException("Unable to start felix", e);
                }
            };
            Thread t = this.threadFactory.newThread(start);
            t.start();
            t.join(600000L);
        }
        catch (Exception ex) {
            throw new OsgiContainerException("Unable to start OSGi container", ex);
        }
        this.pluginEventManager.broadcast((Object)new OsgiContainerStartedEvent(this));
    }

    private void validateConfiguration(StringMap configMap) {
        String systemExports = (String)configMap.get((Object)"org.osgi.framework.system.packages.extra");
        String cacheKeySource = StringUtils.join((Object[])new Object[]{this.getRuntimeEnvironment(), systemExports}, (char)',');
        this.validateCaches(cacheKeySource);
        this.detectIncorrectOsgiVersion();
        this.detectXercesOverride(systemExports);
    }

    void detectXercesOverride(String systemExports) {
        int pos = systemExports.indexOf("org.apache.xerces.util");
        if (!(pos <= -1 || pos != 0 && systemExports.charAt(pos - 1) != ',' || (pos += "org.apache.xerces.util".length()) < systemExports.length() && ';' == systemExports.charAt(pos))) {
            throw new OsgiContainerException("Detected an incompatible version of Apache Xerces on the classpath. If using Tomcat, you may have an old version of Xerces in $TOMCAT_HOME/common/lib/endorsed that will need to be removed.");
        }
    }

    private void validateCaches(String cacheKeySource) {
        log.debug("Using Felix bundle cacheKey source: {}", (Object)cacheKeySource);
        this.persistentCache.validate(cacheKeySource);
        log.debug("Using Felix bundle cache directory: {}", (Object)this.persistentCache.getOsgiBundleCache().getAbsolutePath());
    }

    private void detectIncorrectOsgiVersion() {
        try {
            Bundle.class.getMethod("getBundleContext", new Class[0]);
        }
        catch (NoSuchMethodException e) {
            throw new OsgiContainerException("Detected older version (4.0 or earlier) of OSGi. If using WebSphere 6.1, please enable application-first (parent-last) classloading and the 'Single classloader for application' WAR classloader policy.");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void stop() {
        if (this.felixRunning) {
            ArrayList<ServiceTracker> trackersToStop;
            List<ServiceTracker> list = this.trackers;
            synchronized (list) {
                trackersToStop = new ArrayList<ServiceTracker>(this.trackers);
            }
            for (ServiceTracker tracker : trackersToStop) {
                tracker.close();
            }
            FrameworkListener listener = event -> {
                if (event.getType() == 512) {
                    log.error("Timeout waiting for OSGi to shutdown");
                    this.threadDump();
                } else if (event.getType() == 64) {
                    log.info("OSGi shutdown successful");
                }
            };
            try {
                this.felix.getBundleContext().addFrameworkListener(listener);
                this.felix.stop();
                this.felix.waitForStop(TimeUnit.SECONDS.toMillis(60L));
            }
            catch (InterruptedException e) {
                log.warn("Interrupting Felix shutdown", (Throwable)e);
            }
            catch (BundleException ex) {
                log.error("An error occurred while stopping the Felix OSGi Container. ", (Throwable)ex);
            }
        }
        this.felixRunning = false;
        this.felix = null;
        this.pluginEventManager.broadcast((Object)new OsgiContainerStoppedEvent(this));
    }

    private void threadDump() {
        StringBuilder sb = new StringBuilder();
        String nl = System.getProperty("line.separator");
        for (Map.Entry<Thread, StackTraceElement[]> entry : Thread.getAllStackTraces().entrySet()) {
            Thread key = entry.getKey();
            StackTraceElement[] trace = entry.getValue();
            sb.append(key).append(nl);
            for (StackTraceElement aTrace : trace) {
                sb.append(" ").append(aTrace).append(nl);
            }
        }
        log.debug("Thread dump: {}{}", (Object)nl, (Object)sb);
    }

    @Override
    public Bundle[] getBundles() {
        if (this.isRunning()) {
            return this.registration.getBundles();
        }
        throw new IllegalStateException("Cannot retrieve the bundles if the Felix container isn't running. Check earlier in the logs for the possible cause as to why Felix didn't start correctly.");
    }

    @Override
    public ServiceReference[] getRegisteredServices() {
        return this.felix.getRegisteredServices();
    }

    @Override
    public ServiceTracker getServiceTracker(String interfaceClassName) {
        return this.getServiceTracker(interfaceClassName, null);
    }

    @Override
    public ServiceTracker getServiceTracker(String interfaceClassName, ServiceTrackerCustomizer serviceTrackerCustomizer) {
        if (!this.isRunning()) {
            throw new IllegalStateException("Unable to create a tracker when osgi is not running");
        }
        ServiceTracker tracker = this.registration.getServiceTracker(interfaceClassName, this.trackers, serviceTrackerCustomizer);
        tracker.open();
        this.trackers.add(tracker);
        return tracker;
    }

    @Override
    public void addBundleListener(BundleListener listener) {
        this.felix.getBundleContext().addBundleListener(listener);
    }

    @Override
    public void removeBundleListener(BundleListener listener) {
        BundleContext context;
        Felix felix = this.felix;
        if (felix != null && (context = felix.getBundleContext()) != null) {
            context.removeBundleListener(listener);
        }
    }

    @Override
    public Bundle installBundle(File file, ReferenceMode referenceMode) {
        try {
            return this.registration.install(file, this.disableMultipleBundleVersions, referenceMode.allowsReference());
        }
        catch (BundleException e) {
            throw new OsgiContainerException("Unable to install bundle", e);
        }
    }

    DefaultComponentRegistrar collectHostComponents(HostComponentProvider provider) {
        DefaultComponentRegistrar registrar = new DefaultComponentRegistrar();
        if (provider != null) {
            provider.provide(registrar);
        }
        return registrar;
    }

    @Override
    public boolean isRunning() {
        return this.felixRunning;
    }

    @Override
    public List<HostComponentRegistration> getHostComponentRegistrations() {
        return this.registration.getHostComponentRegistrations();
    }

    @VisibleForTesting
    String getRuntimeEnvironment() {
        return String.format("java.version=%s,plugin.enable.timeout=%d", System.getProperty("java.version"), PluginUtils.getDefaultEnablingWaitPeriod());
    }

    static class BundleRegistration
    implements BundleActivator,
    BundleListener,
    FrameworkListener {
        private static final boolean IS_PLUGIN_PROFILING_DISABLED = Boolean.getBoolean("atlassian.plugins.profiling.disabled");
        private final URL frameworkBundlesUrl;
        private final File frameworkBundlesDir;
        private DefaultComponentRegistrar registrar;
        private ClassLoader initializedClassLoader;
        private BundleContext bundleContext;
        private PackageAdmin packageAdmin;
        private List<ServiceRegistration> hostServicesReferences;
        private List<HostComponentRegistration> hostComponentRegistrations;
        private Optional<ServiceRegistration> instrumentationServiceReference = Optional.empty();
        private final PackageScannerConfiguration packageScannerConfig;

        public BundleRegistration(File frameworkBundlesDir, DefaultComponentRegistrar registrar, PackageScannerConfiguration packageScannerConfig) {
            this(null, frameworkBundlesDir, registrar, packageScannerConfig);
        }

        public BundleRegistration(URL frameworkBundlesUrl, File frameworkBundlesDir, DefaultComponentRegistrar registrar, PackageScannerConfiguration packageScannerConfig) {
            this.frameworkBundlesUrl = frameworkBundlesUrl;
            this.frameworkBundlesDir = frameworkBundlesDir;
            this.registrar = registrar;
            this.initializedClassLoader = Thread.currentThread().getContextClassLoader();
            this.packageScannerConfig = packageScannerConfig;
        }

        public void start(BundleContext context) throws Exception {
            this.bundleContext = context;
            ServiceReference ref = context.getServiceReference(PackageAdmin.class.getName());
            this.packageAdmin = (PackageAdmin)context.getService(ref);
            context.addBundleListener((BundleListener)this);
            context.addFrameworkListener((FrameworkListener)this);
            if (!IS_PLUGIN_PROFILING_DISABLED) {
                PluginKeyWeaver weaver = new PluginKeyWeaver();
                context.registerService(WeavingHook.class, (Object)weaver, null);
            }
            this.registerDmzResolverHook();
            this.registerRestResolverHook();
            this.loadHostComponents(this.registrar);
            if (null != this.frameworkBundlesUrl) {
                FileUtils.conditionallyExtractZipFile((URL)this.frameworkBundlesUrl, (File)this.frameworkBundlesDir);
            }
            this.installFrameworkBundles();
            this.instrumentationServiceReference.ifPresent(ServiceRegistration::unregister);
            this.instrumentationServiceReference = Optional.empty();
            Optional instrumentRegistry = PluginSystemInstrumentation.instance().getInstrumentRegistry();
            this.instrumentationServiceReference = instrumentRegistry.isPresent() ? Optional.of(context.registerService("com.atlassian.instrumentation.InstrumentRegistry", instrumentRegistry.get(), null)) : Optional.empty();
        }

        private void registerDmzResolverHook() {
            log.info("Register DmzResolverHookFactory.");
            log.info("Application bundled internal plugins: {}", this.packageScannerConfig.getApplicationBundledInternalPlugins());
            log.info("OSGI public packages: {}", this.packageScannerConfig.getOsgiPublicPackages());
            log.info("OSGI public packages excludes: {}", this.packageScannerConfig.getOsgiPublicPackagesExcludes());
            this.registrar.register(ResolverHookFactory.class).forInstance(new DmzResolverHookFactory(this.packageScannerConfig)).withName("DmzResolverHookFactory");
        }

        private void registerRestResolverHook() {
            log.info("Register REST v2 Hook Factory.");
            this.registrar.register(ResolverHookFactory.class).forInstance(new RestVersionResolverHookFactory()).withName("AtlassianRestDependencyHookFactory");
        }

        public void stop(BundleContext ctx) {
            ctx.removeBundleListener((BundleListener)this);
            ctx.removeFrameworkListener((FrameworkListener)this);
            if (this.hostServicesReferences != null) {
                for (ServiceRegistration ref : this.hostServicesReferences) {
                    ref.unregister();
                }
            }
            this.instrumentationServiceReference.ifPresent(ServiceRegistration::unregister);
            this.instrumentationServiceReference = Optional.empty();
            this.bundleContext = null;
            this.packageAdmin = null;
            this.hostServicesReferences = null;
            this.hostComponentRegistrations = null;
            this.registrar = null;
            this.initializedClassLoader = null;
        }

        public void bundleChanged(BundleEvent evt) {
            switch (evt.getType()) {
                case 1: {
                    log.info("Installed bundle {} ({})", (Object)evt.getBundle().getSymbolicName(), (Object)evt.getBundle().getBundleId());
                    break;
                }
                case 32: {
                    log.info("Resolved bundle {} ({})", (Object)evt.getBundle().getSymbolicName(), (Object)evt.getBundle().getBundleId());
                    break;
                }
                case 64: {
                    log.info("Unresolved bundle {} ({})", (Object)evt.getBundle().getSymbolicName(), (Object)evt.getBundle().getBundleId());
                    break;
                }
                case 2: {
                    log.info("Started bundle {} ({})", (Object)evt.getBundle().getSymbolicName(), (Object)evt.getBundle().getBundleId());
                    break;
                }
                case 4: {
                    log.info("Stopped bundle {} ({})", (Object)evt.getBundle().getSymbolicName(), (Object)evt.getBundle().getBundleId());
                    break;
                }
                case 16: {
                    log.info("Uninstalled bundle {} ({})", (Object)evt.getBundle().getSymbolicName(), (Object)evt.getBundle().getBundleId());
                    break;
                }
            }
        }

        public Bundle install(File path, boolean uninstallOtherVersions) throws BundleException {
            return this.install(path, uninstallOtherVersions, false);
        }

        public Bundle install(File path, boolean uninstallOtherVersions, boolean allowReference) throws BundleException {
            boolean bundleUninstalled = false;
            if (uninstallOtherVersions) {
                String pluginKey = OsgiHeaderUtil.getPluginKey(path);
                if (null == pluginKey) {
                    throw new BundleException("No plugin key in (possibly malformed) bundle jar '" + path + "'");
                }
                for (Bundle oldBundle : this.bundleContext.getBundles()) {
                    if (!pluginKey.equals(OsgiHeaderUtil.getPluginKey(oldBundle))) continue;
                    log.info("Uninstalling existing version {}", oldBundle.getHeaders().get("Bundle-Version"));
                    oldBundle.uninstall();
                    bundleUninstalled = true;
                }
            }
            String location = path.toURI().toString();
            if (allowReference && !Boolean.getBoolean(FelixOsgiContainerManager.ATLASSIAN_DISABLE_REFERENCE_PROTOCOL) && location.startsWith("file:")) {
                location = "reference:" + location;
            }
            Bundle bundle = this.bundleContext.installBundle(location);
            if (bundleUninstalled) {
                this.refreshPackages();
            }
            return bundle;
        }

        public Bundle[] getBundles() {
            return this.bundleContext.getBundles();
        }

        public ServiceTracker getServiceTracker(String clazz, Collection<ServiceTracker> trackedTrackers) {
            return this.getServiceTracker(clazz, trackedTrackers, null);
        }

        public ServiceTracker getServiceTracker(String clazz, final Collection<ServiceTracker> trackedTrackers, ServiceTrackerCustomizer customizer) {
            return new ServiceTracker(this.bundleContext, clazz, customizer){

                public void close() {
                    super.close();
                    trackedTrackers.remove((Object)this);
                }
            };
        }

        public List<HostComponentRegistration> getHostComponentRegistrations() {
            return this.hostComponentRegistrations;
        }

        void loadHostComponents(DefaultComponentRegistrar registrar) {
            if (this.hostServicesReferences != null) {
                for (ServiceRegistration reg : this.hostServicesReferences) {
                    reg.unregister();
                }
            }
            ContextClassLoaderSwitchingUtil.runInContext((ClassLoader)this.initializedClassLoader, () -> {
                this.hostServicesReferences = registrar.writeRegistry(this.bundleContext);
                this.hostComponentRegistrations = registrar.getRegistry();
            });
        }

        private void installFrameworkBundles() throws BundleException {
            File[] bundleFiles;
            File[] fileArray = bundleFiles = this.frameworkBundlesDir == null ? null : this.frameworkBundlesDir.listFiles((file, s) -> s.endsWith(".jar"));
            if (bundleFiles == null) {
                throw new BundleException("Directory with framework bundle jars could not be read: " + this.frameworkBundlesDir);
            }
            ArrayList<Bundle> bundles = new ArrayList<Bundle>();
            for (File bundleFile : bundleFiles) {
                bundles.add(this.install(bundleFile, false, false));
            }
            this.packageAdmin.resolveBundles(null);
            for (Bundle bundle : bundles) {
                if (bundle.getHeaders().get("Fragment-Host") != null) continue;
                bundle.start();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void refreshPackages() {
            CountDownLatch latch = new CountDownLatch(1);
            FrameworkListener refreshListener = event -> {
                if (event.getType() == 4) {
                    log.info("Packages refreshed");
                    latch.countDown();
                }
            };
            this.bundleContext.addFrameworkListener(refreshListener);
            try {
                this.packageAdmin.refreshPackages(null);
                boolean refreshed = false;
                try {
                    refreshed = latch.await(10L, TimeUnit.SECONDS);
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                }
                if (!refreshed) {
                    log.warn("Timeout exceeded waiting for package refresh");
                }
            }
            finally {
                this.bundleContext.removeFrameworkListener(refreshListener);
            }
        }

        public void frameworkEvent(FrameworkEvent event) {
            String bundleBits = "";
            if (event.getBundle() != null) {
                bundleBits = " in bundle " + event.getBundle().getSymbolicName();
            }
            switch (event.getType()) {
                case 2: {
                    log.error("Framework error{}", (Object)bundleBits, (Object)event.getThrowable());
                    break;
                }
                case 16: {
                    log.warn("Framework warning{}", (Object)bundleBits, (Object)event.getThrowable());
                    break;
                }
                case 32: {
                    log.info("Framework info{}", (Object)bundleBits, (Object)event.getThrowable());
                    break;
                }
            }
        }
    }
}

