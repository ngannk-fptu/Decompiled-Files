/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLStreamHandler;
import java.security.AccessControlException;
import java.security.Permission;
import java.security.PrivilegedActionException;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.felix.framework.BundleContextImpl;
import org.apache.felix.framework.BundleImpl;
import org.apache.felix.framework.BundleProtectionDomain;
import org.apache.felix.framework.BundleRevisionDependencies;
import org.apache.felix.framework.BundleRevisionImpl;
import org.apache.felix.framework.BundleWiringImpl;
import org.apache.felix.framework.EntryFilterEnumeration;
import org.apache.felix.framework.EventDispatcher;
import org.apache.felix.framework.ExportedPackageImpl;
import org.apache.felix.framework.ExtensionManager;
import org.apache.felix.framework.FrameworkStartLevelImpl;
import org.apache.felix.framework.FrameworkWiringImpl;
import org.apache.felix.framework.HookRegistry;
import org.apache.felix.framework.Logger;
import org.apache.felix.framework.ServiceRegistry;
import org.apache.felix.framework.StatefulResolver;
import org.apache.felix.framework.URLHandlersActivator;
import org.apache.felix.framework.URLHandlersBundleStreamHandler;
import org.apache.felix.framework.VersionConverter;
import org.apache.felix.framework.cache.BundleArchive;
import org.apache.felix.framework.cache.BundleCache;
import org.apache.felix.framework.capabilityset.CapabilitySet;
import org.apache.felix.framework.capabilityset.SimpleFilter;
import org.apache.felix.framework.ext.SecurityProvider;
import org.apache.felix.framework.util.ListenerInfo;
import org.apache.felix.framework.util.MapToDictionary;
import org.apache.felix.framework.util.SecureAction;
import org.apache.felix.framework.util.ShrinkableCollection;
import org.apache.felix.framework.util.StringMap;
import org.apache.felix.framework.util.ThreadGate;
import org.apache.felix.framework.util.Util;
import org.apache.felix.framework.util.manifestparser.NativeLibraryClause;
import org.apache.felix.framework.wiring.BundleRequirementImpl;
import org.osgi.framework.AdminPermission;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.BundleReference;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.PackagePermission;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceException;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServicePermission;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.Version;
import org.osgi.framework.connect.ModuleConnector;
import org.osgi.framework.hooks.bundle.FindHook;
import org.osgi.framework.hooks.service.ListenerHook;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.startlevel.FrameworkStartLevel;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleRevisions;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.framework.wiring.FrameworkWiring;
import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.service.condition.Condition;
import org.osgi.service.packageadmin.ExportedPackage;
import org.osgi.service.resolver.ResolutionException;

public class Felix
extends BundleImpl
implements Framework {
    static final SecureAction m_secureAction = new SecureAction();
    private final ExtensionManager m_extensionManager;
    private final FrameworkWiringImpl m_fwkWiring;
    private final FrameworkStartLevelImpl m_fwkStartLevel;
    private final Logger m_logger;
    private final Map<String, Object> m_configMap;
    private final Map<String, Object> m_configMutableMap;
    private final StatefulResolver m_resolver;
    private final ReentrantLock m_bundleLock = new ReentrantLock(true);
    private final java.util.concurrent.locks.Condition m_bundleLockCondition = this.m_bundleLock.newCondition();
    private final List<Thread> m_globalLockWaitersList = new ArrayList<Thread>();
    private Thread m_globalLockThread = null;
    private int m_globalLockCount = 0;
    private final Map<String, String> m_installRequestMap = new HashMap<String, String>();
    private final Object[] m_installRequestLock_Priority1 = new Object[0];
    private volatile Map[] m_installedBundles;
    private static final int LOCATION_MAP_IDX = 0;
    private static final int IDENTIFIER_MAP_IDX = 1;
    private volatile List<BundleImpl> m_uninstalledBundles;
    private final BundleRevisionDependencies m_dependencies = new BundleRevisionDependencies();
    private volatile int m_activeStartLevel = 0;
    private volatile int m_targetStartLevel = 0;
    private final SortedSet<StartLevelTuple> m_startLevelBundles = new TreeSet<StartLevelTuple>();
    private BundleCache m_cache = null;
    List m_activatorList = null;
    private long m_nextId = 1L;
    private final Object m_nextIdLock = new Object[0];
    private final ServiceRegistry m_registry;
    private final EventDispatcher m_dispatcher;
    private final URLStreamHandler m_bundleStreamHandler;
    private final String[] m_bootPkgs;
    private final boolean[] m_bootPkgWildcards;
    private volatile ThreadGate m_shutdownGate = null;
    private SecurityManager m_securityManager = null;
    private volatile boolean m_securityDefaultPolicy;
    private final ModuleConnector m_connectFramework;
    private final Map<Class, Boolean> m_systemBundleClassCache = new WeakHashMap<Class, Boolean>();
    private volatile SecurityProvider m_securityProvider;
    private volatile URLHandlersActivator m_urlHandlersActivator;

    public Felix(Map configMap) {
        this(configMap, null);
    }

    public Felix(Map configMap, ModuleConnector connectFramework) {
        this.m_configMutableMap = new StringMap();
        if (configMap != null) {
            for (Map.Entry entry : configMap.entrySet()) {
                this.m_configMutableMap.put(entry.getKey().toString(), entry.getValue());
            }
        }
        this.m_activatorList = (List)this.m_configMutableMap.remove("felix.systembundle.activators");
        this.m_activatorList = this.m_activatorList == null ? new ArrayList() : new ArrayList(this.m_activatorList);
        this.m_configMap = this.createUnmodifiableMap(this.m_configMutableMap);
        this.m_logger = this.m_configMutableMap.get("felix.log.logger") != null ? (Logger)this.m_configMutableMap.get("felix.log.logger") : new Logger();
        try {
            this.m_logger.setLogLevel(Integer.parseInt((String)this.m_configMutableMap.get("felix.log.level")));
        }
        catch (NumberFormatException i) {
            // empty catch block
        }
        this.initializeFrameworkProperties();
        String s = this.m_configMap == null ? null : (String)this.m_configMap.get("org.osgi.framework.bootdelegation");
        s = s == null ? "java.*" : s + ",java.*";
        StringTokenizer st = new StringTokenizer(s, " ,");
        this.m_bootPkgs = new String[st.countTokens()];
        this.m_bootPkgWildcards = new boolean[this.m_bootPkgs.length];
        for (int i = 0; i < this.m_bootPkgs.length; ++i) {
            s = st.nextToken();
            if (s.equals("*") || s.endsWith(".*")) {
                this.m_bootPkgWildcards[i] = true;
                s = s.substring(0, s.length() - 1);
            }
            this.m_bootPkgs[i] = s;
        }
        NativeLibraryClause.initializeNativeAliases(this.m_configMap);
        this.m_securityDefaultPolicy = "true".equals(this.getProperty("felix.security.defaultpolicy"));
        this.m_bundleStreamHandler = new URLHandlersBundleStreamHandler(this, m_secureAction);
        this.m_registry = new ServiceRegistry(this.m_logger, new ServiceRegistry.ServiceRegistryCallbacks(){

            public void serviceChanged(ServiceEvent event, Dictionary oldProps) {
                Felix.this.fireServiceEvent(event, oldProps);
            }
        });
        this.m_resolver = new StatefulResolver(this, this.m_registry);
        this.m_extensionManager = new ExtensionManager(this.m_logger, this.m_configMap, this);
        try {
            this.addRevision(this.m_extensionManager.getRevision());
        }
        catch (Exception ex) {
            throw new RuntimeException("Exception creating system bundle revision", ex);
        }
        this.m_dispatcher = new EventDispatcher(this.m_logger, this.m_registry);
        this.m_fwkWiring = new FrameworkWiringImpl(this, this.m_registry);
        this.m_fwkStartLevel = new FrameworkStartLevelImpl(this, this.m_registry);
        this.m_connectFramework = connectFramework;
    }

    Logger getLogger() {
        return this.m_logger;
    }

    Map<String, Object> getConfig() {
        return this.m_configMap;
    }

    StatefulResolver getResolver() {
        return this.m_resolver;
    }

    BundleRevisionDependencies getDependencies() {
        return this.m_dependencies;
    }

    URLStreamHandler getBundleStreamHandler() {
        return this.m_bundleStreamHandler;
    }

    String[] getBootPackages() {
        return this.m_bootPkgs;
    }

    boolean[] getBootPackageWildcards() {
        return this.m_bootPkgWildcards;
    }

    private Map createUnmodifiableMap(Map mutableMap) {
        Map result = Collections.unmodifiableMap(mutableMap);
        try {
            result.keySet().iterator();
        }
        catch (NoClassDefFoundError ex) {
            return mutableMap;
        }
        return result;
    }

    @Override
    void close() {
    }

    @Override
    Felix getFramework() {
        return this;
    }

    @Override
    public <A> A adapt(Class<A> type) {
        this.checkAdapt(type);
        if (type == Framework.class || type == Felix.class) {
            return (A)this;
        }
        if (type == FrameworkWiring.class || type == FrameworkWiringImpl.class) {
            return (A)this.m_fwkWiring;
        }
        if (type == FrameworkStartLevel.class || type == FrameworkStartLevelImpl.class) {
            return (A)this.m_fwkStartLevel;
        }
        return super.adapt(type);
    }

    @Override
    public long getBundleId() {
        return 0L;
    }

    @Override
    public long getLastModified() {
        return 0L;
    }

    @Override
    void setLastModified(long l) {
    }

    @Override
    String _getLocation() {
        return "System Bundle";
    }

    @Override
    public int getPersistentState() {
        return 32;
    }

    @Override
    public void setPersistentStateInactive() {
    }

    @Override
    public void setPersistentStateActive() {
    }

    @Override
    public void setPersistentStateUninstalled() {
    }

    @Override
    int getStartLevel(int defaultLevel) {
        return 0;
    }

    @Override
    void setStartLevel(int level) {
        throw new IllegalArgumentException("Cannot set the system bundle's start level.");
    }

    @Override
    public boolean hasPermission(Object obj) {
        return true;
    }

    @Override
    public void init() throws BundleException {
        this.init(null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void init(FrameworkListener ... listeners) throws BundleException {
        block52: {
            this.acquireBundleLock(this, 46);
            try {
                if (this.getState() != 2 && this.getState() != 4) break block52;
                Object security = (FrameworkListener[])this.m_configMap.get("org.osgi.framework.security");
                if (security != null) {
                    if (System.getSecurityManager() != null) {
                        throw new SecurityException("SecurityManager already installed");
                    }
                    if ("osgi".equalsIgnoreCase((String)(security = ((String)security).trim())) || ((String)security).length() == 0) {
                        this.m_securityManager = new SecurityManager();
                        System.setSecurityManager(this.m_securityManager);
                    } else {
                        try {
                            this.m_securityManager = (SecurityManager)Class.forName((String)security).newInstance();
                            System.setSecurityManager(this.m_securityManager);
                        }
                        catch (Throwable t) {
                            SecurityException se = new SecurityException("Unable to install custom SecurityManager: " + (String)security);
                            se.initCause(t);
                            throw se;
                        }
                    }
                }
                this.m_configMutableMap.put("org.osgi.framework.uuid", Util.randomUUID("true".equalsIgnoreCase(this._getProperty("felix.uuid.secure"))));
                this.m_dispatcher.startDispatching();
                this.m_cache = (BundleCache)this.m_configMutableMap.get("felix.bundlecache.impl");
                if (this.m_cache == null) {
                    try {
                        this.m_cache = new BundleCache(this.m_logger, this.m_configMap);
                    }
                    catch (Exception ex) {
                        this.m_logger.log(1, "Error creating bundle cache.", ex);
                        throw new BundleException("Error creating bundle cache.", ex);
                    }
                }
                if (this.getState() == 2) {
                    String clean = (String)this.m_configMap.get("org.osgi.framework.storage.clean");
                    if (clean != null && clean.equalsIgnoreCase("onFirstInit")) {
                        try {
                            this.m_cache.delete();
                        }
                        catch (Exception ex) {
                            throw new BundleException("Unable to flush bundle cache.", ex);
                        }
                    }
                    if (this.m_connectFramework != null) {
                        this.m_connectFramework.initialize(this.m_cache.getCacheDir(), this.m_configMap);
                    }
                }
                Map[] maps = new Map[]{new HashMap(1), new TreeMap()};
                this.m_uninstalledBundles = new ArrayList<BundleImpl>(0);
                maps[0].put(this._getLocation(), this);
                maps[1].put(new Long(0L), this);
                this.m_installedBundles = maps;
                try {
                    this.getResolver().removeRevision(this.m_extensionManager.getRevision());
                    this.m_extensionManager.removeExtensionBundles();
                    this.m_extensionManager.updateRevision(this, this.m_configMap);
                    if (!this.m_configMutableMap.containsKey("org.osgi.framework.system.packages")) {
                        this.m_configMutableMap.put("org.osgi.framework.system.packages", this.m_extensionManager.getRevision().getHeaders().get("Export-Package"));
                    }
                    this.getResolver().addRevision(this.m_extensionManager.getRevision());
                }
                catch (Exception ex) {
                    throw new BundleException("Exception creating system bundle revision", ex);
                }
                try {
                    this.m_resolver.resolve(Collections.singleton(this.adapt(BundleRevision.class)), Collections.EMPTY_SET);
                }
                catch (ResolutionException ex) {
                    throw new BundleException("Unresolved constraint in System Bundle:" + ex.getUnresolvedRequirements());
                }
                BundleArchive[] archives = null;
                try {
                    archives = this.m_cache.getArchives(this.m_connectFramework);
                }
                catch (Exception ex) {
                    this.m_logger.log(1, "Unable to list saved bundles.", ex);
                    archives = null;
                }
                this.setActivator(new SystemBundleActivator());
                this.setBundleContext(new BundleContextImpl(this.m_logger, this, this));
                boolean javaVersionChanged = this.handleJavaVersionChange();
                for (int i = 0; archives != null && i < archives.length; ++i) {
                    try {
                        this.m_nextId = Math.max(this.m_nextId, archives[i].getId() + 1L);
                        if (archives[i].getPersistentState() == 1) {
                            archives[i].closeAndDelete();
                            continue;
                        }
                        this.reloadBundle(archives[i], javaVersionChanged);
                        continue;
                    }
                    catch (Exception ex) {
                        this.fireFrameworkEvent(2, this, ex);
                        try {
                            this.m_logger.log(1, "Unable to re-install " + archives[i].getLocation(), ex);
                            continue;
                        }
                        catch (Exception ex2) {
                            this.m_logger.log(1, "Unable to re-install cached bundle.", ex);
                        }
                    }
                }
                for (Bundle extension : this.m_extensionManager.resolveExtensionBundles(this)) {
                    this.m_extensionManager.startExtensionBundle(this, (BundleImpl)extension);
                }
                if (this.m_connectFramework != null) {
                    this.m_connectFramework.newBundleActivator().ifPresent(this.m_activatorList::add);
                }
                this.m_nextId = Math.max(this.m_nextId, this.loadNextId());
                this.setBundleStateAndNotify(this, 8);
                this.m_shutdownGate = new ThreadGate();
                if (listeners != null) {
                    for (FrameworkListener fl : listeners) {
                        this.addFrameworkListener(this, fl);
                    }
                }
                this.m_resolver.start();
                this.m_fwkWiring.start();
                this.m_fwkStartLevel.start();
                try {
                    m_secureAction.startActivator(this.getActivator(), this._getBundleContext());
                }
                catch (Throwable ex) {
                    this.m_logger.log(1, "Unable to start system bundle.", ex);
                    throw new RuntimeException("Unable to start system bundle.");
                }
                SecurityProvider sp = this.getFramework().getSecurityProvider();
                if (sp != null && System.getSecurityManager() != null) {
                    boolean locked = this.acquireGlobalLock();
                    if (!locked) {
                        throw new BundleException("Unable to acquire the global lock to check the bundle.");
                    }
                    try {
                        for (Object bundle : this.m_installedBundles[1].values()) {
                            try {
                                if (bundle == this) continue;
                                this.setBundleProtectionDomain(((BundleImpl)bundle).adapt(BundleRevisionImpl.class));
                            }
                            catch (Exception ex) {
                                ((BundleImpl)bundle).close();
                                maps = new Map[]{new HashMap(this.m_installedBundles[0]), new TreeMap(this.m_installedBundles[1])};
                                maps[0].remove(((BundleImpl)bundle)._getLocation());
                                maps[1].remove(new Long(((BundleImpl)bundle).getBundleId()));
                                this.m_installedBundles = maps;
                                this.m_logger.log(1, "Bundle in cache doesn't pass security check anymore.", ex);
                            }
                        }
                    }
                    finally {
                        this.releaseGlobalLock();
                    }
                }
                this.m_extensionManager.startPendingExtensionBundles(this);
                this.m_fwkWiring.refreshBundles(null, new FrameworkListener[0]);
                Map<Class, Boolean> map = this.m_systemBundleClassCache;
                synchronized (map) {
                    this.m_systemBundleClassCache.clear();
                }
            }
            catch (Throwable t) {
                this.stopBundle(this, false);
                if (this.m_cache != null) {
                    this.m_cache.release();
                    this.m_cache = null;
                }
                this.__setState(2);
                throw t;
            }
            finally {
                this.releaseBundleLock(this);
                if (listeners != null) {
                    for (FrameworkListener fl : listeners) {
                        this.removeFrameworkListener(this, fl);
                    }
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean handleJavaVersionChange() {
        File dataFile = this.getDataFile(this, "last.java.version");
        int currentVersion = 8;
        try {
            currentVersion = Version.parseVersion(this._getProperty("java.specification.version")).getMajor();
        }
        catch (Exception ignore) {
            this.getLogger().log(this, 2, "Unable to parse current java version", (Throwable)ignore);
        }
        if (currentVersion < 8) {
            currentVersion = 8;
        }
        int lastVersion = 8;
        if (dataFile.isFile()) {
            BufferedReader input = null;
            try {
                input = new BufferedReader(new InputStreamReader(m_secureAction.getInputStream(dataFile), "UTF-8"));
                lastVersion = Version.parseVersion(input.readLine()).getMajor();
            }
            catch (Exception ignore) {
                this.getLogger().log(this, 2, "Unable to parse last java version", (Throwable)ignore);
            }
            finally {
                if (input != null) {
                    try {
                        input.close();
                    }
                    catch (Exception ignore) {}
                }
            }
        }
        if (lastVersion < 8) {
            lastVersion = 8;
        }
        PrintWriter output = null;
        try {
            output = new PrintWriter(new OutputStreamWriter(m_secureAction.getOutputStream(this.getDataFile(this, "last.java.version")), "UTF-8"));
            output.println(Integer.toString(currentVersion));
            output.flush();
        }
        catch (Exception ignore) {
            this.getLogger().log(this, 2, "Unable to persist current java version", (Throwable)ignore);
        }
        finally {
            if (output != null) {
                try {
                    output.close();
                }
                catch (Exception exception) {}
            }
        }
        return currentVersion != lastVersion;
    }

    void setBundleProtectionDomain(BundleRevisionImpl revisionImpl) throws Exception {
        Certificate[] certificates = null;
        SecurityProvider sp = this.getFramework().getSecurityProvider();
        if (sp != null && System.getSecurityManager() != null) {
            BundleImpl bundleImpl = revisionImpl.getBundle();
            sp.checkBundle(bundleImpl);
            Map signers = (Map)sp.getSignerMatcher(bundleImpl, 2);
            certificates = signers.keySet().toArray(new Certificate[signers.size()]);
        }
        revisionImpl.setProtectionDomain(new BundleProtectionDomain(revisionImpl, certificates));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void start() throws BundleException {
        block7: {
            int startLevel = 1;
            this.acquireBundleLock(this, 46);
            try {
                if (this.getState() == 2 || this.getState() == 4) {
                    this.init();
                }
                if (this.getState() != 8) break block7;
                String s = (String)this.m_configMap.get("org.osgi.framework.startlevel.beginning");
                if (s != null) {
                    try {
                        startLevel = Integer.parseInt(s);
                    }
                    catch (NumberFormatException ex) {
                        startLevel = 1;
                    }
                }
                this.m_fwkStartLevel.setStartLevelAndWait(startLevel);
                this.setBundleStateAndNotify(this, 32);
            }
            finally {
                this.releaseBundleLock(this);
            }
        }
        this.fireBundleEvent(2, this);
        this.fireFrameworkEvent(1, this, null);
    }

    @Override
    public void start(int options) throws BundleException {
        this.start();
    }

    @Override
    public void stop() throws BundleException {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new AdminPermission(this, "execute"));
        }
        if ((this.getState() & 6) == 0) {
            new Thread("FelixShutdown"){

                @Override
                public void run() {
                    try {
                        Felix.this.stopBundle(Felix.this, true);
                    }
                    catch (BundleException ex) {
                        Felix.this.m_logger.log(1, "Exception trying to stop framework.", ex);
                    }
                }
            }.start();
        }
    }

    @Override
    public void stop(int options) throws BundleException {
        this.stop();
    }

    @Override
    public FrameworkEvent waitForStop(long timeout) throws InterruptedException {
        if (timeout < 0L) {
            throw new IllegalArgumentException("Timeout cannot be negative.");
        }
        ThreadGate gate = this.m_shutdownGate;
        boolean open = false;
        if (gate != null) {
            open = gate.await(timeout);
        }
        FrameworkEvent event = open && gate.getMessage() != null ? (FrameworkEvent)gate.getMessage() : (!open && gate != null ? new FrameworkEvent(512, this, null) : new FrameworkEvent(64, this, null));
        return event;
    }

    @Override
    public void uninstall() throws BundleException {
        throw new BundleException("Cannot uninstall the system bundle.");
    }

    @Override
    public void update() throws BundleException {
        this.update(null);
    }

    @Override
    public void update(InputStream is) throws BundleException {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new AdminPermission(this, "execute"));
        }
        try {
            if (is != null) {
                is.close();
            }
        }
        catch (IOException ex) {
            this.m_logger.log(2, "Exception closing input stream.", ex);
        }
        new Thread(new Runnable(){

            @Override
            public void run() {
                try {
                    Felix.this.acquireBundleLock(Felix.this, 40);
                    Felix.this.m_shutdownGate.setMessage(new FrameworkEvent(128, Felix.this, null));
                    int oldState = Felix.this.getState();
                    try {
                        Felix.this.stop();
                    }
                    catch (BundleException ex) {
                        Felix.this.m_logger.log(2, "Exception stopping framework.", ex);
                    }
                    finally {
                        Felix.this.releaseBundleLock(Felix.this);
                    }
                    try {
                        Felix.this.waitForStop(0L);
                    }
                    catch (InterruptedException ex) {
                        Felix.this.m_logger.log(2, "Did not wait for framework to stop.", ex);
                    }
                    try {
                        switch (oldState) {
                            case 8: {
                                Felix.this.init();
                                break;
                            }
                            case 32: {
                                Felix.this.start();
                            }
                        }
                    }
                    catch (BundleException ex) {
                        Felix.this.m_logger.log(2, "Exception restarting framework.", ex);
                    }
                }
                catch (Exception ex) {
                    Felix.this.m_logger.log(2, "Cannot update an inactive framework.");
                }
            }
        }).start();
    }

    private void stopRefresh() throws BundleException {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new AdminPermission(this, "execute"));
        }
        new Thread(new Runnable(){

            @Override
            public void run() {
                try {
                    Felix.this.acquireBundleLock(Felix.this, 40);
                    Felix.this.m_shutdownGate.setMessage(new FrameworkEvent(1024, Felix.this, null));
                    int oldState = Felix.this.getState();
                    try {
                        Felix.this.stop();
                    }
                    catch (BundleException ex) {
                        Felix.this.m_logger.log(2, "Exception stopping framework.", ex);
                    }
                    finally {
                        Felix.this.releaseBundleLock(Felix.this);
                    }
                }
                catch (Exception ex) {
                    Felix.this.m_logger.log(2, "Cannot update an inactive framework.");
                }
            }
        }).start();
    }

    @Override
    public String toString() {
        return this.getSymbolicName() + " [" + this.getBundleId() + "]";
    }

    int getActiveStartLevel() {
        return this.m_activeStartLevel;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void setActiveStartLevel(int requestedLevel, FrameworkListener[] listeners) {
        this.m_targetStartLevel = requestedLevel;
        if (this.m_targetStartLevel != this.m_activeStartLevel) {
            boolean bundlesRemaining;
            boolean locked = this.acquireGlobalLock();
            if (!locked) {
                throw new IllegalStateException("Unable to acquire global lock to create bundle snapshot.");
            }
            try {
                SortedSet<StartLevelTuple> sortedSet = this.m_startLevelBundles;
                synchronized (sortedSet) {
                    Bundle[] bundles;
                    for (Bundle sortedSet2 : bundles = this.getBundles()) {
                        this.m_startLevelBundles.add(new StartLevelTuple((BundleImpl)sortedSet2, ((BundleImpl)sortedSet2).getStartLevel(this.getInitialBundleStartLevel())));
                    }
                    bundlesRemaining = !this.m_startLevelBundles.isEmpty();
                }
            }
            finally {
                this.releaseGlobalLock();
            }
            boolean isLowering = this.m_targetStartLevel < this.m_activeStartLevel;
            int low = isLowering ? this.m_targetStartLevel + 1 : this.m_activeStartLevel + 1;
            int high = isLowering ? this.m_activeStartLevel : this.m_targetStartLevel;
            int n = this.m_activeStartLevel = isLowering ? high : low;
            while (bundlesRemaining) {
                StartLevelTuple tuple;
                SortedSet<StartLevelTuple> sortedSet = this.m_startLevelBundles;
                synchronized (sortedSet) {
                    tuple = isLowering ? this.m_startLevelBundles.last() : this.m_startLevelBundles.first();
                    if (tuple.m_level >= low && tuple.m_level <= high) {
                        this.m_activeStartLevel = tuple.m_level;
                    }
                }
                if (tuple.m_bundle.getBundleId() != 0L) {
                    try {
                        this.acquireBundleLock(tuple.m_bundle, 62);
                    }
                    catch (IllegalStateException illegalStateException) {
                        if (tuple.m_bundle.getState() != 1) {
                            this.fireFrameworkEvent(2, tuple.m_bundle, illegalStateException);
                            this.m_logger.log(tuple.m_bundle, 1, "Error locking " + tuple.m_bundle._getLocation(), (Throwable)illegalStateException);
                            continue;
                        }
                        SortedSet<StartLevelTuple> sortedSet3 = this.m_startLevelBundles;
                        synchronized (sortedSet3) {
                            this.m_startLevelBundles.remove(tuple);
                            bundlesRemaining = !this.m_startLevelBundles.isEmpty();
                            continue;
                        }
                    }
                    try {
                        if (!(isLowering || tuple.m_bundle.getPersistentState() != 32 && tuple.m_bundle.getPersistentState() != 8 || tuple.m_level != this.m_activeStartLevel)) {
                            try {
                                int n3 = 1;
                                n3 = tuple.m_bundle.getPersistentState() == 8 ? n3 | 2 : n3;
                                this.startBundle(tuple.m_bundle, n3);
                            }
                            catch (Throwable throwable) {
                                this.fireFrameworkEvent(2, tuple.m_bundle, throwable);
                                this.m_logger.log(tuple.m_bundle, 1, "Error starting " + tuple.m_bundle._getLocation(), throwable);
                            }
                        } else if (isLowering && (tuple.m_bundle.getState() == 32 || tuple.m_bundle.getState() == 8) && tuple.m_level == this.m_activeStartLevel) {
                            try {
                                this.stopBundle(tuple.m_bundle, false);
                            }
                            catch (Throwable throwable) {
                                this.fireFrameworkEvent(2, tuple.m_bundle, throwable);
                                this.m_logger.log(tuple.m_bundle, 1, "Error stopping " + tuple.m_bundle._getLocation(), throwable);
                            }
                        }
                    }
                    finally {
                        this.releaseBundleLock(tuple.m_bundle);
                    }
                }
                SortedSet<StartLevelTuple> sortedSet4 = this.m_startLevelBundles;
                synchronized (sortedSet4) {
                    this.m_startLevelBundles.remove(tuple);
                    bundlesRemaining = !this.m_startLevelBundles.isEmpty();
                }
            }
            this.m_activeStartLevel = this.m_targetStartLevel;
        }
        if (this.getState() == 32) {
            this.fireFrameworkEvent(8, this, null);
            if (listeners != null) {
                FrameworkEvent event = new FrameworkEvent(8, this, null);
                for (FrameworkListener l : listeners) {
                    try {
                        l.frameworkEvent(event);
                    }
                    catch (Throwable th) {
                        this.m_logger.log(1, "Framework listener delivery error.", th);
                    }
                }
            }
        }
    }

    int getInitialBundleStartLevel() {
        String s = (String)this.m_configMap.get("felix.startlevel.bundle");
        if (s != null) {
            try {
                int i = Integer.parseInt(s);
                return i > 0 ? i : 1;
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        return 1;
    }

    void setInitialBundleStartLevel(int startLevel) {
        if (startLevel <= 0) {
            throw new IllegalArgumentException("Initial start level must be greater than zero.");
        }
        this.m_configMutableMap.put("felix.startlevel.bundle", Integer.toString(startLevel));
    }

    int getBundleStartLevel(Bundle bundle) {
        if (bundle.getState() == 1) {
            throw new IllegalArgumentException("Bundle is uninstalled.");
        }
        return ((BundleImpl)bundle).getStartLevel(this.getInitialBundleStartLevel());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void setBundleStartLevel(Bundle bundle, int startLevel) {
        Throwable rethrow;
        block12: {
            BundleImpl impl = (BundleImpl)bundle;
            try {
                this.acquireBundleLock(impl, 62);
            }
            catch (IllegalStateException ex) {
                this.fireFrameworkEvent(2, impl, ex);
                this.m_logger.log(impl, 1, "Error locking " + impl._getLocation(), (Throwable)ex);
                return;
            }
            rethrow = null;
            try {
                if (startLevel >= 1) {
                    try {
                        if ((impl.getPersistentState() == 32 || impl.getPersistentState() == 8) && startLevel <= this.m_activeStartLevel) {
                            int options = 1;
                            options = impl.getPersistentState() == 8 ? options | 2 : options;
                            this.startBundle(impl, options);
                        } else if ((impl.getState() == 32 || impl.getState() == 8) && startLevel > this.m_activeStartLevel) {
                            this.stopBundle(impl, false);
                        }
                        break block12;
                    }
                    catch (Throwable th) {
                        rethrow = th;
                        this.m_logger.log(impl, 1, "Error starting/stopping bundle.", th);
                    }
                    break block12;
                }
                this.m_logger.log(impl, 2, "Bundle start level must be greater than zero.");
            }
            finally {
                this.releaseBundleLock(impl);
            }
        }
        if (rethrow != null) {
            this.fireFrameworkEvent(2, bundle, rethrow);
        }
    }

    boolean isBundlePersistentlyStarted(Bundle bundle) {
        if (bundle.getState() == 1) {
            throw new IllegalArgumentException("Bundle is uninstalled.");
        }
        return ((BundleImpl)bundle).getPersistentState() == 32 || ((BundleImpl)bundle).getPersistentState() == 8;
    }

    boolean isBundleActivationPolicyUsed(Bundle bundle) {
        if (bundle.getState() == 1) {
            throw new IllegalArgumentException("Bundle is uninstalled.");
        }
        return ((BundleImpl)bundle).isDeclaredActivationPolicyUsed();
    }

    Dictionary getBundleHeaders(BundleImpl bundle, String locale) {
        return new MapToDictionary(bundle.getCurrentLocalizedHeader(locale));
    }

    URL getBundleResource(BundleImpl bundle, String name) {
        if (bundle.getState() == 1) {
            throw new IllegalStateException("The bundle is uninstalled.");
        }
        if (Util.isFragment(bundle.adapt(BundleRevision.class))) {
            return null;
        }
        if (bundle.getState() == 2) {
            try {
                this.resolveBundleRevision(bundle.adapt(BundleRevision.class));
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        if (bundle.adapt(BundleRevision.class).getWiring() == null) {
            return ((BundleRevisionImpl)bundle.adapt(BundleRevision.class)).getResourceLocal(name);
        }
        return ((BundleWiringImpl)bundle.adapt(BundleRevision.class).getWiring()).getResourceByDelegation(name);
    }

    Enumeration getBundleResources(BundleImpl bundle, String name) {
        if (bundle.getState() == 1) {
            throw new IllegalStateException("The bundle is uninstalled.");
        }
        if (Util.isFragment(bundle.adapt(BundleRevision.class))) {
            return null;
        }
        if (bundle.getState() == 2) {
            try {
                this.resolveBundleRevision(bundle.adapt(BundleRevision.class));
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        if (bundle.adapt(BundleRevision.class).getWiring() == null) {
            return ((BundleRevisionImpl)bundle.adapt(BundleRevision.class)).getResourcesLocal(name);
        }
        return ((BundleWiringImpl)bundle.adapt(BundleRevision.class).getWiring()).getResourcesByDelegation(name);
    }

    URL getBundleEntry(BundleImpl bundle, String name) {
        EntryFilterEnumeration enumeration;
        if (bundle.getState() == 1) {
            throw new IllegalStateException("The bundle is uninstalled.");
        }
        URL url = ((BundleRevisionImpl)bundle.adapt(BundleRevision.class)).getEntry(name);
        if (url == null && name.endsWith("/") && !name.equals("/") && (enumeration = new EntryFilterEnumeration(bundle.adapt(BundleRevision.class), false, name, "*", true, true)).hasMoreElements()) {
            URL entryURL = (URL)enumeration.nextElement();
            try {
                url = new URL(entryURL, name.charAt(0) == '/' ? name : "/" + name);
            }
            catch (MalformedURLException ex) {
                url = null;
            }
        }
        return url;
    }

    Enumeration getBundleEntryPaths(BundleImpl bundle, String path) {
        if (bundle.getState() == 1) {
            throw new IllegalStateException("The bundle is uninstalled.");
        }
        EntryFilterEnumeration enumeration = new EntryFilterEnumeration(bundle.adapt(BundleRevision.class), false, path, "*", false, false);
        return !enumeration.hasMoreElements() ? null : enumeration;
    }

    Enumeration findBundleEntries(BundleImpl bundle, String path, String filePattern, boolean recurse) {
        if (bundle.getState() == 1) {
            throw new IllegalStateException("The bundle is uninstalled.");
        }
        if (!Util.isFragment(bundle.adapt(BundleRevision.class)) && bundle.getState() == 2) {
            try {
                this.resolveBundleRevision(bundle.adapt(BundleRevision.class));
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return this.findBundleEntries(bundle.adapt(BundleRevision.class), path, filePattern, recurse);
    }

    Enumeration findBundleEntries(BundleRevision revision, String path, String filePattern, boolean recurse) {
        EntryFilterEnumeration enumeration = new EntryFilterEnumeration(revision, !(revision instanceof ExtensionManager.ExtensionManagerRevision), path, filePattern, recurse, true);
        return !enumeration.hasMoreElements() ? null : enumeration;
    }

    ServiceReference[] getBundleRegisteredServices(BundleImpl bundle) {
        if (bundle.getState() == 1) {
            throw new IllegalStateException("The bundle is uninstalled.");
        }
        ServiceReference[] refs = this.m_registry.getRegisteredServices(bundle);
        return refs;
    }

    ServiceReference[] getBundleServicesInUse(Bundle bundle) {
        ServiceReference[] refs = this.m_registry.getServicesInUse(bundle);
        return refs;
    }

    boolean bundleHasPermission(BundleImpl bundle, Object obj) {
        if (bundle.getState() == 1) {
            throw new IllegalStateException("The bundle is uninstalled.");
        }
        if (System.getSecurityManager() != null) {
            try {
                return obj instanceof Permission ? this.impliesBundlePermission((BundleProtectionDomain)bundle.getProtectionDomain(), (Permission)obj, true) : false;
            }
            catch (Exception ex) {
                this.m_logger.log(bundle, 2, "Exception while evaluating the permission.", (Throwable)ex);
                return false;
            }
        }
        return true;
    }

    Class loadBundleClass(BundleImpl bundle, String name) throws ClassNotFoundException {
        if (bundle.getState() == 1) {
            throw new IllegalStateException("Bundle is uninstalled");
        }
        if (Util.isFragment(bundle.adapt(BundleRevision.class))) {
            throw new ClassNotFoundException("Fragments cannot load classes.");
        }
        if (bundle.getState() == 2) {
            try {
                this.resolveBundleRevision(bundle.adapt(BundleRevision.class));
            }
            catch (BundleException ex) {
                this.fireFrameworkEvent(2, bundle, ex);
                throw new ClassNotFoundException(name, ex);
            }
        }
        if (name != null && name.length() > 0 && name.charAt(0) == '[') {
            return Class.forName(name, false, ((BundleWiringImpl)bundle.adapt(BundleWiring.class)).getClassLoader());
        }
        return ((BundleWiringImpl)bundle.adapt(BundleWiring.class)).getClassLoader().loadClass(name);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    void startBundle(BundleImpl bundle, int options) throws BundleException {
        int eventType;
        boolean isTransient = (options & 1) != 0;
        try {
            this.acquireBundleLock(bundle, 62);
        }
        catch (IllegalStateException ex) {
            if (bundle.getState() != 1) throw new BundleException("Bundle " + bundle + " cannot be started: " + ex.getMessage());
            throw new IllegalStateException("Cannot start an uninstalled bundle.");
        }
        boolean wasDeferred = bundle.isDeclaredActivationPolicyUsed() && ((BundleRevisionImpl)bundle.adapt(BundleRevision.class)).getDeclaredActivationPolicy() == 1;
        bundle.setDeclaredActivationPolicyUsed((options & 2) != 0);
        BundleException rethrow = null;
        try {
            if (bundle.isExtension()) {
                return;
            }
            if (Util.isFragment(bundle.adapt(BundleRevision.class))) {
                throw new BundleException("Fragment bundles can not be started.");
            }
            if (!isTransient) {
                if ((options & 2) != 0) {
                    bundle.setPersistentStateStarting();
                } else {
                    bundle.setPersistentStateActive();
                }
            }
            int bundleLevel = bundle.getStartLevel(this.getInitialBundleStartLevel());
            if (isTransient && bundleLevel > this.m_activeStartLevel) {
                throw new BundleException("Cannot start bundle " + bundle + " because its start level is " + bundleLevel + ", which is greater than the framework's start level of " + this.m_activeStartLevel + ".", 10);
            }
            if (bundleLevel > this.m_targetStartLevel) {
                return;
            }
            if (!Thread.currentThread().getName().equals("FelixStartLevel")) {
                SortedSet<StartLevelTuple> sortedSet = this.m_startLevelBundles;
                synchronized (sortedSet) {
                    if (isTransient && bundleLevel > this.m_activeStartLevel) {
                        throw new BundleException("Cannot start bundle " + bundle + " because its start level is " + bundleLevel + ", which is greater than the framework's start level of " + this.m_activeStartLevel + ".", 10);
                    }
                    if (!this.m_startLevelBundles.isEmpty() && bundleLevel >= this.m_activeStartLevel) {
                        boolean found = false;
                        for (StartLevelTuple tuple : this.m_startLevelBundles) {
                            if (tuple.m_bundle != bundle) continue;
                            found = true;
                        }
                        if (!found) {
                            this.m_startLevelBundles.add(new StartLevelTuple(bundle, bundleLevel));
                        }
                        if (!isTransient) {
                            return;
                        }
                    }
                }
            }
            switch (bundle.getState()) {
                case 1: {
                    throw new IllegalStateException("Cannot start an uninstalled bundle.");
                }
                case 8: {
                    if (wasDeferred) break;
                    throw new BundleException("Bundle " + bundle + " cannot be started, since it is starting.");
                }
                case 16: {
                    throw new BundleException("Bundle " + bundle + " cannot be started, since it is stopping.");
                }
                case 32: {
                    return;
                }
                case 2: {
                    this.resolveBundleRevision(bundle.adapt(BundleRevision.class));
                }
                case 4: {
                    bundle.setBundleContext(new BundleContextImpl(this.m_logger, this, bundle));
                    this.setBundleStateAndNotify(bundle, 8);
                    break;
                }
            }
            if (!bundle.isDeclaredActivationPolicyUsed() || ((BundleRevisionImpl)bundle.adapt(BundleRevision.class)).getDeclaredActivationPolicy() != 1 || ((BundleWiringImpl.BundleClassLoader)bundle.adapt(BundleWiring.class).getClassLoader()).isActivationTriggered()) {
                eventType = 2;
                try {
                    this.activateBundle(bundle, false);
                }
                catch (BundleException ex) {
                    rethrow = ex;
                }
            } else {
                eventType = 512;
            }
        }
        finally {
            this.releaseBundleLock(bundle);
        }
        if (rethrow == null) {
            this.fireBundleEvent(eventType, bundle);
            return;
        }
        this.fireBundleEvent(4, bundle);
        throw rethrow;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void activateBundle(BundleImpl bundle, boolean fireEvent) throws BundleException {
        try {
            this.acquireBundleLock(bundle, 40);
        }
        catch (IllegalStateException ex) {
            throw new IllegalStateException("Activation only occurs for bundles in STARTING state.");
        }
        try {
            if (bundle.getState() == 32 || bundle.getStartLevel(this.getInitialBundleStartLevel()) > this.m_targetStartLevel) {
                return;
            }
            Throwable rethrow = null;
            try {
                bundle.setActivator(this.createBundleActivator(bundle));
            }
            catch (Throwable th) {
                rethrow = th;
            }
            try {
                this.fireBundleEvent(128, bundle);
                if (rethrow != null) {
                    throw rethrow;
                }
                if (bundle.getActivator() != null) {
                    m_secureAction.startActivator(bundle.getActivator(), bundle._getBundleContext());
                }
                this.setBundleStateAndNotify(bundle, 32);
            }
            catch (Throwable th2) {
                Exception th2;
                this.fireBundleEvent(256, bundle);
                this.setBundleStateAndNotify(bundle, 4);
                bundle.setActivator(null);
                BundleContextImpl bci = (BundleContextImpl)bundle._getBundleContext();
                bci.invalidate();
                bundle.setBundleContext(null);
                this.m_registry.unregisterServices(bundle);
                this.m_registry.ungetServices(bundle);
                this.m_dispatcher.removeListeners(bci);
                if (th2 instanceof BundleException) {
                    throw (BundleException)th2;
                }
                if (System.getSecurityManager() != null && th2 instanceof PrivilegedActionException) {
                    th2 = ((PrivilegedActionException)th2).getException();
                }
                throw new BundleException("Activator start error in bundle " + bundle + ".", 5, th2);
            }
        }
        finally {
            this.releaseBundleLock(bundle);
        }
        if (fireEvent) {
            this.fireBundleEvent(2, bundle);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void updateBundle(BundleImpl bundle, InputStream is) throws BundleException {
        try {
            this.acquireBundleLock(bundle, 62);
        }
        catch (IllegalStateException ex) {
            if (bundle.getState() == 1) {
                throw new IllegalStateException("Cannot update an uninstalled bundle.");
            }
            throw new BundleException("Bundle " + bundle + " cannot be update: " + ex.getMessage());
        }
        try {
            if (bundle.getState() == 8 && (!bundle.isDeclaredActivationPolicyUsed() || ((BundleRevisionImpl)bundle.adapt(BundleRevision.class)).getDeclaredActivationPolicy() != 1) || bundle.getState() == 16) {
                throw new IllegalStateException("Bundle " + bundle + " cannot be update, since it is either STARTING or STOPPING.");
            }
            Throwable rethrow = null;
            int oldState = bundle.getState();
            String updateLocation = (String)((BundleRevisionImpl)bundle.adapt(BundleRevision.class)).getHeaders().get("Bundle-UpdateLocation");
            if (updateLocation == null) {
                updateLocation = bundle._getLocation();
            }
            if (oldState == 32) {
                this.stopBundle(bundle, false);
            }
            try {
                boolean locked = this.acquireGlobalLock();
                if (!locked) {
                    throw new BundleException("Cannot acquire global lock to update the bundle.");
                }
                try {
                    boolean wasExtension = bundle.isExtension();
                    bundle.revise(updateLocation, is);
                    try {
                        SecurityManager sm = System.getSecurityManager();
                        if (sm != null) {
                            sm.checkPermission(new AdminPermission(bundle, "lifecycle"));
                        }
                        if (!wasExtension && bundle.isExtension()) {
                            this.m_extensionManager.addExtensionBundle(bundle);
                        } else if (wasExtension) {
                            this.setBundleStateAndNotify(bundle, 2);
                        }
                    }
                    catch (Throwable ex) {
                        try {
                            bundle.rollbackRevise();
                        }
                        catch (Exception busted) {
                            this.m_logger.log(bundle, 1, "Unable to rollback.", (Throwable)busted);
                        }
                        throw ex;
                    }
                }
                finally {
                    this.releaseGlobalLock();
                }
            }
            catch (Throwable ex) {
                this.m_logger.log(bundle, 1, "Unable to update the bundle.", ex);
                rethrow = ex;
            }
            if (rethrow == null) {
                bundle.setLastModified(System.currentTimeMillis());
                if (!bundle.isExtension()) {
                    this.setBundleStateAndNotify(bundle, 2);
                }
                for (Bundle extension : this.m_extensionManager.resolveExtensionBundles(this)) {
                    this.m_extensionManager.startExtensionBundle(this, (BundleImpl)extension);
                }
                this.fireBundleEvent(64, bundle);
                this.fireBundleEvent(8, bundle);
                boolean locked = this.acquireGlobalLock();
                if (locked) {
                    try {
                        if (!this.m_dependencies.hasDependents(bundle) && !bundle.isExtension()) {
                            try {
                                ArrayList<Bundle> list = new ArrayList<Bundle>(1);
                                list.add(bundle);
                                this.refreshPackages(list, null);
                            }
                            catch (Exception ex) {
                                this.m_logger.log(bundle, 1, "Unable to immediately purge the bundle revisions.", (Throwable)ex);
                            }
                        }
                    }
                    finally {
                        this.releaseGlobalLock();
                    }
                }
            }
            if (oldState == 32 && Util.isFragment(bundle.adapt(BundleRevision.class))) {
                bundle.setPersistentStateInactive();
                this.m_logger.log(bundle, 2, "Previously active bundle was updated to a fragment, resetting state to inactive: " + bundle);
            } else if (oldState == 32) {
                this.startBundle(bundle, 1);
            }
            if (rethrow != null) {
                if (rethrow instanceof AccessControlException) {
                    throw (AccessControlException)rethrow;
                }
                if (rethrow instanceof BundleException) {
                    throw (BundleException)rethrow;
                }
                throw new BundleException("Update of bundle " + bundle + " failed.", rethrow);
            }
        }
        finally {
            try {
                if (is != null) {
                    is.close();
                }
            }
            catch (Exception ex) {
                this.m_logger.log(bundle, 1, "Unable to close input stream.", (Throwable)ex);
            }
            this.releaseBundleLock(bundle);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void stopBundle(BundleImpl bundle, boolean record) throws BundleException {
        try {
            this.acquireBundleLock(bundle, 62);
        }
        catch (IllegalStateException ex) {
            if (bundle.getState() == 1) {
                throw new IllegalStateException("Cannot stop an uninstalled bundle.");
            }
            throw new BundleException("Bundle " + bundle + " cannot be stopped: " + ex.getMessage());
        }
        try {
            Throwable rethrow = null;
            if (record) {
                bundle.setPersistentStateInactive();
            }
            if (!this.isBundlePersistentlyStarted(bundle)) {
                bundle.setDeclaredActivationPolicyUsed(false);
            }
            if (Util.isFragment(bundle.adapt(BundleRevision.class))) {
                throw new BundleException("Fragment bundles can not be stopped: " + bundle);
            }
            boolean wasActive = false;
            switch (bundle.getState()) {
                case 1: {
                    throw new IllegalStateException("Cannot stop an uninstalled bundle.");
                }
                case 8: {
                    if (!bundle.isDeclaredActivationPolicyUsed() || ((BundleRevisionImpl)bundle.adapt(BundleRevision.class)).getDeclaredActivationPolicy() == 1) break;
                    throw new BundleException("Stopping a starting or stopping bundle is currently not supported.");
                }
                case 16: {
                    throw new BundleException("Stopping a starting or stopping bundle is currently not supported.");
                }
                case 2: 
                case 4: {
                    return;
                }
                case 32: {
                    wasActive = true;
                }
            }
            this.setBundleStateAndNotify(bundle, 16);
            this.fireBundleEvent(256, bundle);
            if (wasActive || bundle.getBundleId() == 0L) {
                try {
                    if (bundle.getActivator() != null) {
                        m_secureAction.stopActivator(bundle.getActivator(), bundle._getBundleContext());
                    }
                }
                catch (Throwable th) {
                    this.m_logger.log(bundle, 1, "Error stopping bundle.", th);
                    rethrow = th;
                }
            }
            if (bundle.getBundleId() != 0L) {
                bundle.setActivator(null);
                BundleContextImpl bci = (BundleContextImpl)bundle._getBundleContext();
                bci.invalidate();
                bundle.setBundleContext(null);
                this.m_registry.unregisterServices(bundle);
                this.m_registry.ungetServices(bundle);
                this.m_dispatcher.removeListeners(bci);
                this.setBundleStateAndNotify(bundle, 4);
            }
            if (rethrow != null) {
                if (rethrow instanceof BundleException) {
                    throw (BundleException)rethrow;
                }
                if (System.getSecurityManager() != null && rethrow instanceof PrivilegedActionException) {
                    rethrow = ((PrivilegedActionException)rethrow).getException();
                }
                throw new BundleException("Activator stop error in bundle " + bundle + ".", rethrow);
            }
        }
        finally {
            this.releaseBundleLock(bundle);
        }
        this.fireBundleEvent(4, bundle);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void uninstallBundle(BundleImpl bundle) throws BundleException {
        try {
            this.acquireBundleLock(bundle, 62);
        }
        catch (IllegalStateException ex) {
            if (bundle.getState() == 1) {
                throw new IllegalStateException("Cannot uninstall an uninstalled bundle.");
            }
            throw new BundleException("Bundle " + bundle + " cannot be uninstalled: " + ex.getMessage());
        }
        try {
            if (bundle.getState() == 8 && (!bundle.isDeclaredActivationPolicyUsed() || ((BundleRevisionImpl)bundle.adapt(BundleRevision.class)).getDeclaredActivationPolicy() != 1) || bundle.getState() == 16) {
                throw new IllegalStateException("Bundle " + bundle + " cannot be uninstalled, since it is either STARTING or STOPPING.");
            }
            if (!bundle.isExtension() && bundle.getState() == 32) {
                try {
                    this.stopBundle(bundle, true);
                }
                catch (BundleException ex) {
                    this.fireFrameworkEvent(2, bundle, ex);
                }
            }
            BundleImpl target = null;
            boolean locked = this.acquireGlobalLock();
            if (!locked) {
                throw new IllegalStateException("Unable to acquire global lock to remove bundle.");
            }
            try {
                Map[] maps = new Map[]{new HashMap(this.m_installedBundles[0]), new TreeMap(this.m_installedBundles[1])};
                target = (BundleImpl)maps[0].remove(bundle._getLocation());
                if (target != null) {
                    maps[1].remove(new Long(target.getBundleId()));
                    this.m_installedBundles = maps;
                    bundle.setPersistentStateUninstalled();
                    this.rememberUninstalledBundle(bundle);
                }
            }
            finally {
                this.releaseGlobalLock();
            }
            if (target == null) {
                this.m_logger.log(bundle, 1, "Unable to remove bundle from installed map!");
            }
            this.setBundleStateAndNotify(bundle, 2);
            this.fireBundleEvent(64, bundle);
            this.setBundleStateAndNotify(bundle, 1);
            bundle.setLastModified(System.currentTimeMillis());
        }
        finally {
            this.releaseBundleLock(bundle);
        }
        this.fireBundleEvent(16, bundle);
        boolean locked = this.acquireGlobalLock();
        if (locked) {
            Set<Bundle> refreshCandidates = this.addUninstalled(bundle, new LinkedHashSet<Bundle>());
            try {
                HashSet<Bundle> dependent = new HashSet<Bundle>();
                for (Bundle b : refreshCandidates) {
                    this.populateDependentGraph(b, dependent);
                }
                if (refreshCandidates.containsAll(dependent)) {
                    try {
                        this.refreshPackages(refreshCandidates, null);
                    }
                    catch (Exception ex) {
                        this.m_logger.log(this, 1, "Unable to immediately garbage collect bundles.", (Throwable)ex);
                    }
                } else {
                    boolean progress;
                    do {
                        progress = false;
                        Iterator<Bundle> iter = refreshCandidates.iterator();
                        while (iter.hasNext()) {
                            Bundle b = iter.next();
                            if (this.m_dependencies.hasDependents(b)) continue;
                            iter.remove();
                            try {
                                List<Bundle> list = Collections.singletonList(b);
                                this.refreshPackages(list, null);
                                progress = true;
                            }
                            catch (Exception ex) {
                                this.m_logger.log(b, 1, "Unable to immediately garbage collect the bundle.", (Throwable)ex);
                            }
                        }
                    } while (progress);
                }
            }
            finally {
                this.releaseGlobalLock();
            }
        }
    }

    private Set<Bundle> addUninstalled(Bundle bundle, Set<Bundle> refreshCandidates) {
        refreshCandidates.add(bundle);
        BundleRevisions bundleRevisions = bundle.adapt(BundleRevisions.class);
        if (bundleRevisions != null) {
            for (BundleRevision br : bundleRevisions.getRevisions()) {
                BundleWiring bw = br.getWiring();
                if (bw == null) continue;
                for (BundleWire wire : bw.getRequiredWires(null)) {
                    Bundle b = wire.getProvider().getBundle();
                    if (b.getState() != 1 || refreshCandidates.contains(b)) continue;
                    refreshCandidates = this.addUninstalled(b, refreshCandidates);
                }
            }
        }
        Set<Bundle> dependent = this.populateDependentGraph(bundle, new HashSet<Bundle>());
        for (Bundle b : dependent) {
            if (b.getState() != 1 || refreshCandidates.contains(b)) continue;
            refreshCandidates = this.addUninstalled(b, refreshCandidates);
        }
        return refreshCandidates;
    }

    String getProperty(String key) {
        Object val = this.m_configMap.get(key);
        return !(val instanceof String) ? System.getProperty(key) : (String)val;
    }

    String _getProperty(String key) {
        Object val = this.m_configMap.get(key);
        return !(val instanceof String) ? m_secureAction.getSystemProperty(key, null) : (String)val;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Bundle reloadBundle(BundleArchive ba, boolean updateMulti) throws BundleException {
        BundleImpl bundle = null;
        try {
            if (ba.isRemovalPending()) {
                ba.purge();
            }
        }
        catch (Exception ex) {
            this.m_logger.log(1, "Could not purge bundle.", ex);
        }
        try {
            boolean locked = this.acquireGlobalLock();
            if (!locked) {
                throw new BundleException("Unable to acquire the global lock to install the bundle.");
            }
            try {
                bundle = new BundleImpl(this, null, ba);
                if (updateMulti) {
                    try {
                        if ("true".equals(bundle.adapt(BundleRevisionImpl.class).getHeaders().get("Multi-Release"))) {
                            ba.setLastModified(System.currentTimeMillis());
                        }
                    }
                    catch (Exception ex) {
                        this.getLogger().log(this, 2, "Unable to update multi-release bundle last modified", (Throwable)ex);
                    }
                }
                if (bundle.isExtension()) {
                    this.m_extensionManager.addExtensionBundle(bundle);
                }
                Map[] maps = new Map[]{new HashMap(this.m_installedBundles[0]), new TreeMap(this.m_installedBundles[1])};
                maps[0].put(bundle._getLocation(), bundle);
                maps[1].put(new Long(bundle.getBundleId()), bundle);
                this.m_installedBundles = maps;
            }
            finally {
                this.releaseGlobalLock();
            }
        }
        catch (Throwable ex) {
            if (ex instanceof BundleException) {
                throw (BundleException)ex;
            }
            if (ex instanceof AccessControlException) {
                throw (AccessControlException)ex;
            }
            throw new BundleException("Could not create bundle object.", ex);
        }
        return bundle;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    Bundle installBundle(Bundle origin, String location, InputStream is) throws BundleException {
        BundleImpl existing;
        BundleImpl bundle;
        block47: {
            BundleArchive ba = null;
            bundle = null;
            this.acquireInstallLock(location);
            try {
                boolean locked;
                if (this.getState() == 16 || this.getState() == 1) {
                    throw new BundleException("The framework has been shutdown.");
                }
                existing = (BundleImpl)this.getBundle(location);
                if (existing != null) break block47;
                long id = this.getNextId();
                try {
                    ba = this.m_cache.create(id, this.getInitialBundleStartLevel(), location, is, this.m_connectFramework);
                }
                catch (Exception ex) {
                    throw new BundleException("Unable to cache bundle: " + location, ex);
                }
                finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                    }
                    catch (IOException ex) {
                        this.m_logger.log(1, "Unable to close input stream.", ex);
                    }
                }
                try {
                    locked = this.acquireGlobalLock();
                    if (!locked) {
                        throw new BundleException("Unable to acquire the global lock to install the bundle.");
                    }
                    try {
                        bundle = new BundleImpl(this, origin, ba);
                    }
                    finally {
                        this.releaseGlobalLock();
                    }
                    if (!bundle.isExtension()) {
                        SecurityManager sm = System.getSecurityManager();
                        if (sm != null) {
                            sm.checkPermission(new AdminPermission(bundle, "lifecycle"));
                        }
                    } else {
                        this.m_extensionManager.addExtensionBundle(bundle);
                    }
                }
                catch (Throwable ex) {
                    try {
                        if (bundle != null) {
                            bundle.closeAndDelete();
                        } else if (ba != null) {
                            ba.closeAndDelete();
                        }
                    }
                    catch (Exception ex1) {
                        this.m_logger.log(bundle, 1, "Could not remove from cache.", (Throwable)ex1);
                    }
                    if (ex instanceof BundleException) {
                        throw (BundleException)ex;
                    }
                    if (ex instanceof AccessControlException) {
                        throw (AccessControlException)ex;
                    }
                    throw new BundleException("Could not create bundle object.", ex);
                }
                locked = this.acquireGlobalLock();
                if (!locked) {
                    throw new IllegalStateException("Unable to acquire global lock to add bundle.");
                }
                try {
                    Map[] maps = new Map[]{new HashMap(this.m_installedBundles[0]), new TreeMap(this.m_installedBundles[1])};
                    maps[0].put(location, bundle);
                    maps[1].put(new Long(bundle.getBundleId()), bundle);
                    this.m_installedBundles = maps;
                }
                finally {
                    this.releaseGlobalLock();
                }
                for (Bundle extension : this.m_extensionManager.resolveExtensionBundles(this)) {
                    this.m_extensionManager.startExtensionBundle(this, (BundleImpl)extension);
                }
            }
            finally {
                this.releaseInstallLock(location);
                try {
                    if (is != null) {
                        is.close();
                    }
                }
                catch (IOException ex) {
                    this.m_logger.log(bundle, 1, "Unable to close input stream.", (Throwable)ex);
                }
            }
        }
        if (existing != null) {
            Set<ServiceReference<FindHook>> hooks = this.getHookRegistry().getHooks(FindHook.class);
            if (!hooks.isEmpty()) {
                Collection<Bundle> bundles = new ArrayList<BundleImpl>(1);
                bundles.add(existing);
                bundles = new ShrinkableCollection(bundles);
                for (ServiceReference<FindHook> hook : hooks) {
                    FindHook fh = this.getService(this, hook, false);
                    if (fh == null) continue;
                    try {
                        m_secureAction.invokeBundleFindHook(fh, ((BundleImpl)origin)._getBundleContext(), bundles);
                    }
                    catch (Throwable th) {
                        this.m_logger.doLog(hook.getBundle(), hook, 2, "Problem invoking bundle hook.", th);
                    }
                }
                if (origin != this && bundles.isEmpty()) {
                    throw new BundleException("Bundle installation rejected by hook.", 12);
                }
            }
        } else {
            this.fireBundleEvent(1, bundle, origin);
        }
        return existing != null ? existing : bundle;
    }

    Bundle getBundle(String location) {
        return (Bundle)this.m_installedBundles[0].get(location);
    }

    Bundle getBundle(BundleContext bc, long id) {
        Set<ServiceReference<FindHook>> hooks;
        BundleImpl bundle = (BundleImpl)this.m_installedBundles[1].get(new Long(id));
        if (bundle != null) {
            List<BundleImpl> uninstalledBundles = this.m_uninstalledBundles;
            for (int i = 0; bundle == null && uninstalledBundles != null && i < uninstalledBundles.size(); ++i) {
                if (uninstalledBundles.get(i).getBundleId() != id) continue;
                bundle = uninstalledBundles.get(i);
            }
        }
        if (!(hooks = this.getHookRegistry().getHooks(FindHook.class)).isEmpty() && bundle != null) {
            Collection<Bundle> bundles = new ArrayList<BundleImpl>(1);
            bundles.add(bundle);
            bundles = new ShrinkableCollection(bundles);
            for (ServiceReference<FindHook> hook : hooks) {
                FindHook fh = this.getService(this, hook, false);
                if (fh == null) continue;
                try {
                    m_secureAction.invokeBundleFindHook(fh, bc, bundles);
                }
                catch (Throwable th) {
                    this.m_logger.doLog(hook.getBundle(), hook, 2, "Problem invoking bundle hook.", th);
                }
            }
            if (bc.getBundle() != this) {
                bundle = bundles.isEmpty() ? null : bundle;
            }
        }
        return bundle;
    }

    Bundle getBundle(long id) {
        BundleImpl bundle = (BundleImpl)this.m_installedBundles[1].get(new Long(id));
        if (bundle != null) {
            return bundle;
        }
        List<BundleImpl> uninstalledBundles = this.m_uninstalledBundles;
        for (int i = 0; uninstalledBundles != null && i < uninstalledBundles.size(); ++i) {
            if (uninstalledBundles.get(i).getBundleId() != id) continue;
            return uninstalledBundles.get(i);
        }
        return null;
    }

    Bundle[] getBundles(BundleContext bc) {
        Set<ServiceReference<FindHook>> hooks;
        Collection bundles = this.m_installedBundles[1].values();
        if (!bundles.isEmpty() && !(hooks = this.getHookRegistry().getHooks(FindHook.class)).isEmpty()) {
            ShrinkableCollection<Bundle> shrunkBundles = new ShrinkableCollection<Bundle>(new ArrayList(bundles));
            for (ServiceReference<FindHook> hook : hooks) {
                FindHook fh = this.getService(this, hook, false);
                if (fh == null) continue;
                try {
                    m_secureAction.invokeBundleFindHook(fh, bc, shrunkBundles);
                }
                catch (Throwable th) {
                    this.m_logger.doLog(hook.getBundle(), hook, 2, "Problem invoking bundle hook.", th);
                }
            }
            if (bc.getBundle() != this) {
                bundles = shrunkBundles;
            }
        }
        return bundles.toArray(new Bundle[bundles.size()]);
    }

    Bundle[] getBundles() {
        Collection bundles = this.m_installedBundles[1].values();
        return bundles.toArray(new Bundle[bundles.size()]);
    }

    void addBundleListener(BundleImpl bundle, BundleListener l) {
        this.m_dispatcher.addListener(bundle._getBundleContext(), BundleListener.class, l, null);
    }

    void removeBundleListener(BundleImpl bundle, BundleListener l) {
        this.m_dispatcher.removeListener(bundle._getBundleContext(), BundleListener.class, l);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void addServiceListener(BundleImpl bundle, ServiceListener l, String f) throws InvalidSyntaxException {
        ListenerHook lh;
        Filter newFilter = f == null ? null : FrameworkUtil.createFilter(f);
        Filter oldFilter = this.m_dispatcher.addListener(bundle._getBundleContext(), ServiceListener.class, l, newFilter);
        Set<ServiceReference<ListenerHook>> listenerHooks = this.getHookRegistry().getHooks(ListenerHook.class);
        if (oldFilter != null) {
            Set<ListenerHook.ListenerInfo> removed = Collections.singleton(new ListenerInfo(bundle, bundle._getBundleContext(), ServiceListener.class, l, oldFilter, null, true));
            for (ServiceReference<ListenerHook> sr : listenerHooks) {
                lh = this.getService(this, sr, false);
                if (lh == null) continue;
                try {
                    m_secureAction.invokeServiceListenerHookRemoved(lh, removed);
                }
                catch (Throwable th) {
                    this.m_logger.log(sr, 2, "Problem invoking service registry hook", th);
                }
                finally {
                    this.m_registry.ungetService(this, sr, null);
                }
            }
        }
        Set<ListenerHook.ListenerInfo> added = Collections.singleton(new ListenerInfo(bundle, bundle._getBundleContext(), ServiceListener.class, l, newFilter, null, false));
        for (ServiceReference<ListenerHook> sr : listenerHooks) {
            lh = this.getService(this, sr, false);
            if (lh == null) continue;
            try {
                m_secureAction.invokeServiceListenerHookAdded(lh, added);
            }
            catch (Throwable th) {
                this.m_logger.log(sr, 2, "Problem invoking service registry hook", th);
            }
            finally {
                this.m_registry.ungetService(this, sr, null);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void removeServiceListener(BundleImpl bundle, ServiceListener l) {
        ListenerHook.ListenerInfo listener = this.m_dispatcher.removeListener(bundle._getBundleContext(), ServiceListener.class, l);
        if (listener != null) {
            Set<ServiceReference<ListenerHook>> listenerHooks = this.getHookRegistry().getHooks(ListenerHook.class);
            Set<ListenerHook.ListenerInfo> removed = Collections.singleton(listener);
            for (ServiceReference<ListenerHook> sr : listenerHooks) {
                ListenerHook lh = this.getService(this, sr, false);
                if (lh == null) continue;
                try {
                    m_secureAction.invokeServiceListenerHookRemoved(lh, removed);
                }
                catch (Throwable th) {
                    this.m_logger.log(sr, 2, "Problem invoking service registry hook", th);
                }
                finally {
                    this.m_registry.ungetService(this, sr, null);
                }
            }
        }
    }

    void addFrameworkListener(BundleImpl bundle, FrameworkListener l) {
        this.m_dispatcher.addListener(bundle._getBundleContext(), FrameworkListener.class, l, null);
    }

    void removeFrameworkListener(BundleImpl bundle, FrameworkListener l) {
        this.m_dispatcher.removeListener(bundle._getBundleContext(), FrameworkListener.class, l);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    ServiceRegistration registerService(BundleContextImpl context, String[] classNames, Object svcObj, Dictionary dict) {
        ListenerHook lh;
        if (classNames == null) {
            throw new NullPointerException("Service class names cannot be null.");
        }
        if (svcObj == null) {
            throw new IllegalArgumentException("Service object cannot be null.");
        }
        ServiceRegistration<?> reg = null;
        if (!(svcObj instanceof ServiceFactory)) {
            for (int i = 0; i < classNames.length; ++i) {
                Class clazz = Util.loadClassUsingClass(svcObj.getClass(), classNames[i], m_secureAction);
                if (clazz == null) {
                    if (Util.checkImplementsWithName(svcObj.getClass(), classNames[i])) continue;
                    throw new IllegalArgumentException("Cannot cast service: " + classNames[i]);
                }
                if (clazz.isAssignableFrom(svcObj.getClass())) continue;
                throw new IllegalArgumentException("Service object is not an instance of \"" + classNames[i] + "\".");
            }
        }
        reg = this.m_registry.registerService(context.getBundle(), classNames, svcObj, dict);
        if (HookRegistry.isHook(classNames, ListenerHook.class, svcObj) && (lh = (ListenerHook)this.getService(this, reg.getReference(), false)) != null) {
            try {
                m_secureAction.invokeServiceListenerHookAdded(lh, this.m_dispatcher.getAllServiceListeners());
            }
            catch (Throwable th) {
                this.m_logger.log(reg.getReference(), 2, "Problem invoking service registry hook", th);
            }
            finally {
                this.ungetService(this, reg.getReference(), null);
            }
        }
        this.fireServiceEvent(new ServiceEvent(1, reg.getReference()), null);
        return reg;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    ServiceReference[] getServiceReferences(BundleImpl bundle, String className, String expr, boolean checkAssignable) throws InvalidSyntaxException {
        SimpleFilter filter = null;
        if (expr != null) {
            try {
                filter = SimpleFilter.parse(expr);
            }
            catch (Exception ex) {
                throw new InvalidSyntaxException(ex.getMessage(), expr);
            }
        }
        ArrayList refList = this.m_registry.getServiceReferences(className, filter);
        if (checkAssignable) {
            Iterator<Capability> refIter = refList.iterator();
            while (refIter.hasNext()) {
                ServiceReference ref = (ServiceReference)((Object)refIter.next());
                if (Util.isServiceAssignable(bundle, ref)) continue;
                refIter.remove();
            }
        }
        ArrayList resRefList = bundle == this ? new ArrayList(refList) : refList;
        Set<ServiceReference<org.osgi.framework.hooks.service.FindHook>> findHooks = this.getHookRegistry().getHooks(org.osgi.framework.hooks.service.FindHook.class);
        for (ServiceReference<org.osgi.framework.hooks.service.FindHook> sr : findHooks) {
            org.osgi.framework.hooks.service.FindHook fh = this.getService(this, sr, false);
            if (fh == null) continue;
            try {
                m_secureAction.invokeServiceFindHook(fh, bundle._getBundleContext(), className, expr, !checkAssignable, new ShrinkableCollection(refList));
            }
            catch (Throwable th) {
                this.m_logger.log(sr, 2, "Problem invoking service registry hook", th);
            }
            finally {
                this.m_registry.ungetService(this, sr, null);
            }
        }
        if (resRefList.size() > 0) {
            return resRefList.toArray(new ServiceReference[resRefList.size()]);
        }
        return null;
    }

    ServiceReference[] getAllowedServiceReferences(BundleImpl bundle, String className, String expr, boolean checkAssignable) throws InvalidSyntaxException {
        ServiceReference[] refs = this.getServiceReferences(bundle, className, expr, checkAssignable);
        SecurityManager sm = System.getSecurityManager();
        if (sm == null || refs == null) {
            return refs;
        }
        ArrayList<ServiceReference> result = new ArrayList<ServiceReference>();
        for (int i = 0; i < refs.length; ++i) {
            try {
                sm.checkPermission(new ServicePermission(refs[i], "get"));
                result.add(refs[i]);
                continue;
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        if (result.isEmpty()) {
            return null;
        }
        return result.toArray(new ServiceReference[result.size()]);
    }

    <S> S getService(Bundle bundle, ServiceReference<S> ref, boolean isServiceObjetcs) {
        try {
            return this.m_registry.getService(bundle, ref, isServiceObjetcs);
        }
        catch (ServiceException ex) {
            this.fireFrameworkEvent(2, bundle, ex);
            return null;
        }
    }

    boolean ungetService(Bundle bundle, ServiceReference ref, Object srvObj) {
        return this.m_registry.ungetService(bundle, ref, srvObj);
    }

    File getDataFile(BundleImpl bundle, String s) {
        if (bundle.getState() == 1) {
            throw new IllegalStateException("Bundle has been uninstalled");
        }
        if (Util.isFragment(this.adapt(BundleRevision.class))) {
            return null;
        }
        try {
            if (bundle == this) {
                return this.m_cache.getSystemBundleDataFile(s);
            }
            return bundle.getArchive().getDataFile(s);
        }
        catch (Exception ex) {
            this.m_logger.log(bundle, 1, ex.getMessage());
            return null;
        }
    }

    HookRegistry getHookRegistry() {
        return this.m_registry.getHookRegistry();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    Bundle getBundle(Class clazz) {
        ClassLoader classLoader = m_secureAction.getClassLoader(clazz);
        if (classLoader instanceof BundleReference) {
            BundleReference br = (BundleReference)((Object)classLoader);
            return br.getBundle() instanceof BundleImpl && ((BundleImpl)br.getBundle()).getFramework() == this ? br.getBundle() : null;
        }
        if (!clazz.getName().startsWith("java.")) {
            Boolean fromSystemBundle;
            Map<Class, Boolean> map = this.m_systemBundleClassCache;
            synchronized (map) {
                fromSystemBundle = this.m_systemBundleClassCache.get(clazz);
            }
            if (fromSystemBundle == null) {
                Class sbClass = null;
                try {
                    sbClass = ((BundleWiringImpl)this.m_extensionManager.getRevision().getWiring()).getClassByDelegation(clazz.getName());
                }
                catch (ClassNotFoundException classNotFoundException) {
                    // empty catch block
                }
                Map<Class, Boolean> map2 = this.m_systemBundleClassCache;
                synchronized (map2) {
                    fromSystemBundle = sbClass == clazz ? Boolean.TRUE : Boolean.FALSE;
                    this.m_systemBundleClassCache.put(clazz, fromSystemBundle);
                }
            }
            return fromSystemBundle != false ? this : null;
        }
        return null;
    }

    ExportedPackage[] getExportedPackages(String pkgName) {
        Map<String, Object> attrs = Collections.singletonMap("osgi.wiring.package", pkgName);
        BundleRequirementImpl req = new BundleRequirementImpl(null, "osgi.wiring.package", Collections.EMPTY_MAP, attrs);
        List<BundleCapability> exports = this.m_resolver.findProviders(req, false);
        Iterator<BundleCapability> it = exports.iterator();
        while (it.hasNext()) {
            if (it.next().getRevision().getWiring() != null) continue;
            it.remove();
        }
        if (exports != null) {
            ArrayList<ExportedPackageImpl> pkgs = new ArrayList<ExportedPackageImpl>();
            Iterator<BundleCapability> it2 = exports.iterator();
            while (it2.hasNext()) {
                Bundle bundle = it2.next().getRevision().getBundle();
                List<BundleRevision> originRevisions = bundle.adapt(BundleRevisions.class).getRevisions();
                for (int i = originRevisions.size() - 1; i >= 0; --i) {
                    BundleRevision originBr = originRevisions.get(i);
                    List<BundleRevision> revisions = Collections.singletonList(originBr);
                    if ((originBr.getTypes() & 1) != 0) {
                        revisions = new ArrayList<BundleRevision>();
                        for (BundleWire bw : originBr.getWiring().getRequiredWires("osgi.wiring.host")) {
                            revisions.add(bw.getProviderWiring().getRevision());
                        }
                    }
                    for (BundleRevision br : revisions) {
                        List<BundleCapability> caps = br.getWiring() == null ? br.getDeclaredCapabilities(null) : br.getWiring().getCapabilities(null);
                        for (BundleCapability cap : caps) {
                            if (!cap.getNamespace().equals(req.getNamespace()) || !CapabilitySet.matches(cap, req.getFilter())) continue;
                            pkgs.add(new ExportedPackageImpl(this, (BundleImpl)br.getBundle(), br, cap));
                        }
                    }
                }
            }
            return pkgs.isEmpty() ? null : pkgs.toArray(new ExportedPackage[pkgs.size()]);
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    ExportedPackage[] getExportedPackages(Bundle b) {
        ArrayList list = new ArrayList();
        if (b != null) {
            BundleImpl bundle = (BundleImpl)b;
            this.getExportedPackages(bundle, list);
        } else {
            boolean locked = this.acquireGlobalLock();
            if (!locked) {
                throw new IllegalStateException("Unable to acquire global lock to retrieve exported packages.");
            }
            try {
                for (int bundleIdx = 0; this.m_uninstalledBundles != null && bundleIdx < this.m_uninstalledBundles.size(); ++bundleIdx) {
                    BundleImpl bundle = this.m_uninstalledBundles.get(bundleIdx);
                    this.getExportedPackages(bundle, list);
                }
                Bundle[] bundles = this.getBundles();
                for (int bundleIdx = 0; bundleIdx < bundles.length; ++bundleIdx) {
                    BundleImpl bundle = (BundleImpl)bundles[bundleIdx];
                    this.getExportedPackages(bundle, list);
                }
            }
            finally {
                this.releaseGlobalLock();
            }
        }
        return list.isEmpty() ? null : list.toArray(new ExportedPackage[list.size()]);
    }

    private void getExportedPackages(Bundle bundle, List list) {
        for (BundleRevision br : bundle.adapt(BundleRevisions.class).getRevisions()) {
            List<BundleCapability> caps = br.getWiring() == null ? br.getDeclaredCapabilities(null) : br.getWiring().getCapabilities(null);
            if (caps == null || caps.size() <= 0) continue;
            for (BundleCapability cap : caps) {
                if (!cap.getNamespace().equals("osgi.wiring.package")) continue;
                String pkgName = (String)cap.getAttributes().get("osgi.wiring.package");
                list.add(new ExportedPackageImpl(this, (BundleImpl)bundle, br, cap));
            }
        }
    }

    Set<Bundle> getImportingBundles(BundleImpl exporter, BundleCapability cap) {
        return this.m_dependencies.getImportingBundles(exporter, cap);
    }

    Set<Bundle> getRequiringBundles(BundleImpl bundle) {
        return this.m_dependencies.getRequiringBundles(bundle);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    boolean resolveBundles(Collection<Bundle> targets) {
        boolean locked = this.acquireGlobalLock();
        if (!locked) {
            this.m_logger.log(2, "Unable to acquire global lock to perform resolve.", null);
            return false;
        }
        try {
            boolean result;
            block12: {
                Collection<Bundle> originalTargets = targets;
                if (targets == null) {
                    targets = this.m_installedBundles[0].values();
                }
                result = true;
                if (!targets.isEmpty()) {
                    HashSet<BundleRevision> revisions = new HashSet<BundleRevision>(targets.size());
                    for (Bundle b : targets) {
                        if (b.getState() == 1) continue;
                        revisions.add(b.adapt(BundleRevision.class));
                    }
                    if (originalTargets != null && originalTargets.size() != revisions.size()) {
                        result = false;
                    }
                    try {
                        this.m_resolver.resolve(Collections.EMPTY_SET, revisions);
                        if (!result) break block12;
                        for (BundleRevision br : revisions) {
                            if (br.getWiring() != null) continue;
                            result = false;
                            break;
                        }
                    }
                    catch (ResolutionException ex) {
                        result = false;
                    }
                    catch (BundleException ex) {
                        result = false;
                    }
                }
            }
            boolean bl = result;
            return bl;
        }
        finally {
            this.releaseGlobalLock();
        }
    }

    private void resolveBundleRevision(BundleRevision revision) throws BundleException {
        try {
            this.m_resolver.resolve(Collections.singleton(revision), Collections.EMPTY_SET);
        }
        catch (ResolutionException ex) {
            throw new BundleException(ex.getMessage() + " Unresolved requirements: " + ex.getUnresolvedRequirements(), 4);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void refreshPackages(Collection<Bundle> targets, FrameworkListener[] listeners) {
        block26: {
            boolean locked = this.acquireGlobalLock();
            if (!locked) {
                throw new IllegalStateException("Unable to acquire global lock for refresh.");
            }
            Collection<Bundle> newTargets = targets;
            if (newTargets == null) {
                ArrayList<Bundle> list = new ArrayList<Bundle>();
                for (int i = 0; this.m_uninstalledBundles != null && i < this.m_uninstalledBundles.size(); ++i) {
                    list.add(this.m_uninstalledBundles.get(i));
                }
                for (BundleImpl bundle : this.m_installedBundles[0].values()) {
                    if (!bundle.isRemovalPending()) continue;
                    list.add(bundle);
                }
                if (!list.isEmpty()) {
                    newTargets = list;
                }
            }
            HashSet<Bundle> bundles = null;
            if (newTargets != null) {
                bundles = new HashSet<Bundle>();
                for (Bundle target : newTargets) {
                    if (target == null) continue;
                    bundles.add(target);
                    this.populateDependentGraph((BundleImpl)target, bundles);
                }
            }
            try {
                boolean restart = false;
                boolean extensionBundle = false;
                Felix systemBundle = this;
                if (bundles == null) break block26;
                for (Bundle bundle : bundles) {
                    if (systemBundle == bundle) {
                        restart = true;
                        continue;
                    }
                    if (!((BundleImpl)bundle).isExtension()) continue;
                    restart = true;
                    extensionBundle = true;
                    break;
                }
                if (!restart) {
                    ArrayList<RefreshHelper> helpers = new ArrayList<RefreshHelper>(bundles.size());
                    for (Bundle b : bundles) {
                        this.forgetUninstalledBundle((BundleImpl)b);
                        helpers.add(new RefreshHelper(b));
                    }
                    for (RefreshHelper helper : helpers) {
                        if (helper == null) continue;
                        helper.stop();
                    }
                    for (RefreshHelper helper : helpers) {
                        if (helper == null) continue;
                        helper.refreshOrRemove();
                    }
                    for (RefreshHelper helper : helpers) {
                        if (helper == null) continue;
                        helper.restart();
                    }
                    break block26;
                }
                if (!extensionBundle) {
                    try {
                        this.update();
                    }
                    catch (BundleException ex) {
                        this.m_logger.log(1, "Framework restart error.", ex);
                    }
                    break block26;
                }
                try {
                    this.stopRefresh();
                }
                catch (BundleException ex) {
                    this.m_logger.log(1, "Framework stop error.", ex);
                }
            }
            finally {
                this.releaseGlobalLock();
            }
        }
        this.fireFrameworkEvent(4, this, null);
        if (listeners != null) {
            FrameworkEvent event = new FrameworkEvent(4, this, null);
            for (FrameworkListener frameworkListener : listeners) {
                try {
                    frameworkListener.frameworkEvent(event);
                }
                catch (Throwable th) {
                    this.m_logger.log(1, "Framework listener delivery error.", th);
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    Collection<Bundle> getDependencyClosure(Collection<Bundle> targets) {
        boolean locked = this.acquireGlobalLock();
        if (!locked) {
            throw new IllegalStateException("Unable to acquire global lock for refresh.");
        }
        try {
            HashSet<Bundle> bundles = Collections.EMPTY_SET;
            if (targets != null) {
                bundles = new HashSet<Bundle>();
                for (Bundle target : targets) {
                    bundles.add(target);
                    this.populateDependentGraph((BundleImpl)target, bundles);
                }
            }
            HashSet<Bundle> hashSet = bundles;
            return hashSet;
        }
        finally {
            this.releaseGlobalLock();
        }
    }

    private Set<Bundle> populateDependentGraph(Bundle exporter, Set<Bundle> set) {
        Set<Bundle> dependents = this.m_dependencies.getDependentBundles(exporter);
        if (dependents != null) {
            for (Bundle b : dependents) {
                if (set.contains(b)) continue;
                set.add(b);
                set = this.populateDependentGraph((BundleImpl)b, set);
            }
        }
        return set;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    Collection<Bundle> getRemovalPendingBundles() {
        boolean locked = this.acquireGlobalLock();
        if (!locked) {
            throw new IllegalStateException("Unable to acquire global lock for refresh.");
        }
        try {
            ArrayList<Object> bundles = new ArrayList<Object>();
            if (this.m_uninstalledBundles != null) {
                for (Bundle bundle : this.m_uninstalledBundles) {
                    bundles.add(bundle);
                }
            }
            for (Bundle b : this.getBundles()) {
                if (!((BundleImpl)b).isRemovalPending()) continue;
                bundles.add(b);
            }
            ArrayList<Object> arrayList = bundles;
            return arrayList;
        }
        finally {
            this.releaseGlobalLock();
        }
    }

    SecurityProvider getSecurityProvider() {
        return this.m_securityProvider;
    }

    void setSecurityProvider(SecurityProvider securityProvider) {
        this.m_securityProvider = securityProvider;
    }

    Object getSignerMatcher(BundleImpl bundle, int signersType) {
        if (bundle != this && this.m_securityProvider != null) {
            return this.m_securityProvider.getSignerMatcher(bundle, signersType);
        }
        return new HashMap();
    }

    boolean impliesBundlePermission(BundleProtectionDomain bundleProtectionDomain, Permission permission, boolean direct) {
        if (direct && permission instanceof PackagePermission && bundleProtectionDomain.impliesWoven(permission)) {
            return true;
        }
        if (this.m_securityProvider != null) {
            return this.m_securityProvider.hasBundlePermission(bundleProtectionDomain, permission, direct);
        }
        BundleImpl source = bundleProtectionDomain.getBundle();
        return this.m_securityDefaultPolicy && (source == null || source.getBundleId() != 0L) ? bundleProtectionDomain.superImplies(permission) : true;
    }

    private BundleActivator createBundleActivator(Bundle impl) throws Exception {
        BundleActivator activator = null;
        Map<String, Object> headerMap = ((BundleRevisionImpl)impl.adapt(BundleRevision.class)).getHeaders();
        String className = (String)headerMap.get("Bundle-Activator");
        if (className != null) {
            Class clazz;
            className = className.trim();
            try {
                clazz = ((BundleWiringImpl)impl.adapt(BundleRevision.class).getWiring()).getClassByDelegation(className);
            }
            catch (ClassNotFoundException ex) {
                throw new BundleException("Not found: " + className, ex);
            }
            activator = (BundleActivator)clazz.newInstance();
        }
        return activator;
    }

    private void refreshBundle(BundleImpl bundle) throws Exception {
        try {
            this.acquireBundleLock(bundle, 6);
        }
        catch (IllegalStateException ex) {
            throw new BundleException("Bundle state has changed unexpectedly during refresh.");
        }
        try {
            boolean fire = bundle.getState() != 2;
            this.m_dependencies.removeDependencies(bundle);
            bundle.refresh();
            if (fire) {
                this.setBundleStateAndNotify(bundle, 2);
                this.fireBundleEvent(64, bundle);
            }
        }
        catch (Exception ex) {
            this.fireFrameworkEvent(2, bundle, ex);
        }
        finally {
            this.releaseBundleLock(bundle);
        }
    }

    void fireFrameworkEvent(int type, Bundle bundle, Throwable throwable) {
        this.m_dispatcher.fireFrameworkEvent(new FrameworkEvent(type, bundle, throwable));
    }

    void fireBundleEvent(int type, Bundle bundle) {
        this.m_dispatcher.fireBundleEvent(new BundleEvent(type, bundle), this);
    }

    void fireBundleEvent(int type, Bundle bundle, Bundle origin) {
        this.m_dispatcher.fireBundleEvent(new BundleEvent(type, bundle, origin), this);
    }

    private void fireServiceEvent(ServiceEvent event, Dictionary oldProps) {
        this.m_dispatcher.fireServiceEvent(event, oldProps, this);
    }

    private void initializeFrameworkProperties() {
        this.m_configMutableMap.put("org.osgi.framework.version", "1.9");
        this.m_configMutableMap.put("org.osgi.framework.vendor", "Apache Software Foundation");
        this.m_configMutableMap.put("org.osgi.supports.framework.extension", ExtensionManager.m_extenderFramework != null ? "true" : "false");
        this.m_configMutableMap.put("org.osgi.supports.framework.fragment", "true");
        this.m_configMutableMap.put("org.osgi.supports.framework.requirebundle", "true");
        this.m_configMutableMap.put("org.osgi.supports.bootclasspath.extension", ExtensionManager.m_extenderBoot != null ? "true" : "false");
        String s = null;
        if (!this.m_configMutableMap.containsKey("org.osgi.framework.os.name")) {
            s = NativeLibraryClause.normalizeOSName(System.getProperty("os.name"));
            this.m_configMutableMap.put("org.osgi.framework.os.name", s);
        }
        if (!this.m_configMutableMap.containsKey("org.osgi.framework.processor")) {
            s = NativeLibraryClause.normalizeProcessor(System.getProperty("os.arch"));
            this.m_configMutableMap.put("org.osgi.framework.processor", s);
        }
        if (!this.m_configMutableMap.containsKey("org.osgi.framework.os.version")) {
            this.m_configMutableMap.put("org.osgi.framework.os.version", VersionConverter.toOsgiVersion(System.getProperty("os.version")).toString());
        }
        if (!this.m_configMutableMap.containsKey("org.osgi.framework.language")) {
            this.m_configMutableMap.put("org.osgi.framework.language", System.getProperty("user.language"));
        }
        this.m_configMutableMap.put("felix.version", Felix.getFrameworkVersion().toString());
        Properties defaultProperties = Util.loadDefaultProperties(this.m_logger);
        Util.initializeJPMSEE(this._getProperty("java.specification.version"), defaultProperties, this.m_logger);
        this.loadFromDefaultIfNotDefined(defaultProperties, "org.osgi.framework.executionenvironment");
        this.loadPrefixFromDefaultIfNotDefined(this.m_configMutableMap, defaultProperties, "felix.native.osname.alias");
        this.loadPrefixFromDefaultIfNotDefined(this.m_configMutableMap, defaultProperties, "felix.native.processor.alias");
    }

    private void loadFromDefaultIfNotDefined(Properties defaultProperties, String propertyName) {
        String s;
        if (!this.getConfig().containsKey(propertyName) && (s = Util.getPropertyWithSubs(defaultProperties, propertyName)) != null) {
            this.m_configMutableMap.put(propertyName, s);
        }
    }

    private void loadPrefixFromDefaultIfNotDefined(Map configMap, Properties defaultProperties, String prefix) {
        Map<String, String> defaultPropsWithPrefix = Util.getPropertiesWithPrefix(defaultProperties, prefix);
        for (String currentDefaultProperty : defaultPropsWithPrefix.keySet()) {
            if (configMap.containsKey(currentDefaultProperty)) continue;
            configMap.put(currentDefaultProperty, defaultPropsWithPrefix.get(currentDefaultProperty));
        }
    }

    private static Version getFrameworkVersion() {
        Properties props = new Properties();
        InputStream in = Felix.class.getResourceAsStream("Felix.properties");
        if (in != null) {
            try {
                props.load(in);
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
            finally {
                try {
                    in.close();
                }
                catch (IOException iOException) {}
            }
        }
        return VersionConverter.toOsgiVersion(props.getProperty("felix.version", "0.0.0"));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private long loadNextId() {
        Object object = this.m_nextIdLock;
        synchronized (object) {
            InputStream is = null;
            BufferedReader br = null;
            try {
                File file = this.m_cache.getSystemBundleDataFile("bundle.id");
                if (!m_secureAction.isFile(file)) return -1L;
                is = m_secureAction.getInputStream(file);
                br = new BufferedReader(new InputStreamReader(is));
                long l = Long.parseLong(br.readLine());
                return l;
            }
            catch (FileNotFoundException ex) {
            }
            catch (Exception ex) {
                this.m_logger.log(2, "Unable to initialize next bundle identifier from persistent storage.", ex);
            }
            finally {
                try {
                    if (br != null) {
                        br.close();
                    }
                    if (is != null) {
                        is.close();
                    }
                }
                catch (Exception ex) {
                    this.m_logger.log(2, "Unable to close next bundle identifier file.", ex);
                }
            }
            return -1L;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private long getNextId() {
        Object object = this.m_nextIdLock;
        synchronized (object) {
            long id = this.m_nextId++;
            OutputStream os = null;
            BufferedWriter bw = null;
            try {
                File file = this.m_cache.getSystemBundleDataFile("bundle.id");
                os = m_secureAction.getOutputStream(file);
                bw = new BufferedWriter(new OutputStreamWriter(os));
                String s = Long.toString(this.m_nextId);
                bw.write(s, 0, s.length());
            }
            catch (Exception ex) {
                this.m_logger.log(2, "Unable to save next bundle identifier to persistent storage.", ex);
            }
            finally {
                try {
                    if (bw != null) {
                        bw.close();
                    }
                    if (os != null) {
                        os.close();
                    }
                }
                catch (Exception ex) {
                    this.m_logger.log(2, "Unable to close next bundle identifier file.", ex);
                }
            }
            return id;
        }
    }

    public boolean hasConnectFramework() {
        return this.m_connectFramework != null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void rememberUninstalledBundle(BundleImpl bundle) {
        boolean locked = this.acquireGlobalLock();
        if (!locked) {
            throw new IllegalStateException("Unable to acquire global lock to record uninstalled bundle.");
        }
        try {
            for (int i = 0; this.m_uninstalledBundles != null && i < this.m_uninstalledBundles.size(); ++i) {
                if (this.m_uninstalledBundles.get(i) != bundle) continue;
                return;
            }
            ArrayList<BundleImpl> uninstalledBundles = new ArrayList<BundleImpl>(this.m_uninstalledBundles);
            uninstalledBundles.add(bundle);
            this.m_uninstalledBundles = uninstalledBundles;
        }
        finally {
            this.releaseGlobalLock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void forgetUninstalledBundle(BundleImpl bundle) {
        boolean locked = this.acquireGlobalLock();
        if (!locked) {
            throw new IllegalStateException("Unable to acquire global lock to release uninstalled bundle.");
        }
        try {
            if (this.m_uninstalledBundles == null) {
                return;
            }
            ArrayList<BundleImpl> uninstalledBundles = new ArrayList<BundleImpl>(this.m_uninstalledBundles);
            uninstalledBundles.remove(bundle);
            this.m_uninstalledBundles = uninstalledBundles;
        }
        finally {
            this.releaseGlobalLock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void acquireInstallLock(String location) throws BundleException {
        Object[] objectArray = this.m_installRequestLock_Priority1;
        synchronized (this.m_installRequestLock_Priority1) {
            while (this.m_installRequestMap.get(location) != null) {
                try {
                    this.m_installRequestLock_Priority1.wait();
                }
                catch (InterruptedException ex) {
                    throw new BundleException("Unable to install, thread interrupted.");
                }
            }
            this.m_installRequestMap.put(location, location);
            // ** MonitorExit[var2_2] (shouldn't be in output)
            return;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void releaseInstallLock(String location) {
        Object[] objectArray = this.m_installRequestLock_Priority1;
        synchronized (this.m_installRequestLock_Priority1) {
            this.m_installRequestMap.remove(location);
            this.m_installRequestLock_Priority1.notifyAll();
            // ** MonitorExit[var2_2] (shouldn't be in output)
            return;
        }
    }

    void setBundleStateAndNotify(BundleImpl bundle, int state) {
        this.m_bundleLock.lock();
        try {
            bundle.__setState(state);
            this.m_bundleLockCondition.signalAll();
        }
        finally {
            this.m_bundleLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void acquireBundleLock(BundleImpl bundle, int desiredStates) throws IllegalStateException {
        this.m_bundleLock.lock();
        try {
            while (!bundle.isLockable() || this.m_globalLockThread != null && this.m_globalLockThread != Thread.currentThread() && bundle.getLockingThread() != Thread.currentThread()) {
                if ((desiredStates & bundle.getState()) == 0) {
                    throw new IllegalStateException("Bundle in unexpected state.");
                }
                if (this.m_globalLockThread == Thread.currentThread() && bundle.getLockingThread() != null && this.m_globalLockWaitersList.contains(bundle.getLockingThread())) {
                    bundle.getLockingThread().interrupt();
                }
                try {
                    this.m_bundleLockCondition.await();
                }
                catch (InterruptedException ex) {
                    throw new IllegalStateException("Unable to acquire bundle lock, thread interrupted.");
                }
            }
            if ((desiredStates & bundle.getState()) == 0) {
                throw new IllegalStateException("Bundle in unexpected state.");
            }
            bundle.lock();
        }
        finally {
            this.m_bundleLock.unlock();
        }
    }

    void releaseBundleLock(BundleImpl bundle) {
        this.m_bundleLock.lock();
        try {
            bundle.unlock();
            if (bundle.getLockingThread() == null) {
                this.m_bundleLockCondition.signalAll();
            }
        }
        finally {
            this.m_bundleLock.unlock();
        }
    }

    boolean acquireGlobalLock() {
        this.m_bundleLock.lock();
        try {
            boolean interrupted = false;
            while (!interrupted && this.m_globalLockThread != null && this.m_globalLockThread != Thread.currentThread()) {
                this.m_globalLockWaitersList.add(Thread.currentThread());
                this.m_bundleLockCondition.signalAll();
                try {
                    this.m_bundleLockCondition.await();
                }
                catch (InterruptedException ex) {
                    interrupted = true;
                }
                this.m_globalLockWaitersList.remove(Thread.currentThread());
            }
            if (!interrupted) {
                ++this.m_globalLockCount;
                this.m_globalLockThread = Thread.currentThread();
            }
            boolean bl = !interrupted;
            return bl;
        }
        finally {
            this.m_bundleLock.unlock();
        }
    }

    void releaseGlobalLock() {
        block5: {
            this.m_bundleLock.lock();
            try {
                if (this.m_globalLockThread == Thread.currentThread()) {
                    --this.m_globalLockCount;
                    if (this.m_globalLockCount == 0) {
                        this.m_globalLockThread = null;
                        this.m_bundleLockCondition.signalAll();
                    }
                    break block5;
                }
                throw new IllegalStateException("The current thread doesn't own the global lock.");
            }
            finally {
                this.m_bundleLock.unlock();
            }
        }
    }

    void setURLHandlersActivator(URLHandlersActivator urlHandlersActivator) {
        this.m_urlHandlersActivator = urlHandlersActivator;
    }

    Object getStreamHandlerService(String protocol) {
        return this.m_urlHandlersActivator.getStreamHandlerService(protocol);
    }

    Object getContentHandlerService(String mimeType) {
        return this.m_urlHandlersActivator.getContentHandlerService(mimeType);
    }

    Collection<BundleCapability> findProviders(Requirement requirement) {
        return this.m_resolver.findProvidersInternal(null, requirement, true, false);
    }

    private static class StartLevelTuple
    implements Comparable<StartLevelTuple> {
        private final BundleImpl m_bundle;
        private int m_level;

        StartLevelTuple(BundleImpl bundle, int level) {
            this.m_bundle = bundle;
            this.m_level = level;
        }

        @Override
        public int compareTo(StartLevelTuple t) {
            int result = 1;
            if (this.m_level < t.m_level) {
                result = -1;
            } else if (this.m_level > t.m_level) {
                result = 1;
            } else if (this.m_bundle.getBundleId() < t.m_bundle.getBundleId()) {
                result = -1;
            } else if (this.m_bundle.getBundleId() == t.m_bundle.getBundleId()) {
                result = 0;
            }
            return result;
        }
    }

    private class RefreshHelper {
        private BundleImpl m_bundle = null;
        private int m_oldState = 2;

        public RefreshHelper(Bundle bundle) {
            this.m_bundle = (BundleImpl)bundle;
        }

        public void stop() {
            Felix.this.acquireBundleLock(this.m_bundle, 63);
            try {
                this.m_oldState = this.m_bundle.getState();
                if (this.m_oldState != 1 && !Util.isFragment(this.m_bundle.adapt(BundleRevision.class))) {
                    Felix.this.stopBundle(this.m_bundle, false);
                }
            }
            catch (Throwable ex) {
                Felix.this.fireFrameworkEvent(2, this.m_bundle, ex);
            }
            finally {
                Felix.this.releaseBundleLock(this.m_bundle);
            }
        }

        public void refreshOrRemove() {
            try {
                if (this.m_bundle.getState() == 1) {
                    Felix.this.m_dependencies.removeDependencies(this.m_bundle);
                    this.m_bundle.closeAndDelete();
                    this.m_bundle = null;
                } else {
                    Felix.this.refreshBundle(this.m_bundle);
                }
            }
            catch (Throwable ex) {
                Felix.this.fireFrameworkEvent(2, this.m_bundle, ex);
            }
        }

        public void restart() {
            if (this.m_bundle != null && this.m_oldState == 32) {
                try {
                    int options = 1;
                    options = this.m_bundle.getPersistentState() == 8 ? options | 2 : options;
                    Felix.this.startBundle(this.m_bundle, options);
                }
                catch (Throwable ex) {
                    Felix.this.fireFrameworkEvent(2, this.m_bundle, ex);
                }
            }
        }
    }

    class SystemBundleActivator
    implements BundleActivator {
        private volatile ServiceRegistration<Condition> m_reg;

        SystemBundleActivator() {
        }

        @Override
        public void start(BundleContext context) throws Exception {
            Felix.this.m_activatorList.add(0, new URLHandlersActivator(Felix.this.m_configMap, Felix.this));
            this.m_reg = context.registerService(Condition.class, Condition.INSTANCE, FrameworkUtil.asDictionary(Collections.singletonMap("osgi.condition.id", "true")));
            Iterator iter = Felix.this.m_activatorList.iterator();
            while (iter.hasNext()) {
                try {
                    m_secureAction.startActivator((BundleActivator)iter.next(), context);
                }
                catch (Throwable throwable) {
                    throwable.printStackTrace();
                    iter.remove();
                    Felix.this.fireFrameworkEvent(2, context.getBundle(), new BundleException("Unable to start Bundle", throwable));
                    Felix.this.m_logger.log(2, "Exception starting a system bundle activator.", throwable);
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void stop(BundleContext context) {
            int i;
            Felix.this.acquireBundleLock(Felix.this, 16);
            Felix.this.releaseBundleLock(Felix.this);
            Felix.this.m_fwkStartLevel.setStartLevelAndWait(0);
            Felix.this.m_fwkWiring.stop();
            Felix.this.m_fwkStartLevel.stop();
            Felix.this.m_resolver.stop();
            Felix.this.m_dispatcher.stopDispatching();
            Bundle[] bundles = Felix.this.getBundles();
            for (i = 0; i < bundles.length; ++i) {
                BundleImpl bundle = (BundleImpl)bundles[i];
                if (!bundle.isRemovalPending()) continue;
                try {
                    Felix.this.refreshBundle(bundle);
                    continue;
                }
                catch (Exception ex) {
                    Felix.this.fireFrameworkEvent(2, bundle, ex);
                    Felix.this.m_logger.log(bundle, 1, "Unable to purge bundle " + bundle._getLocation(), (Throwable)ex);
                }
            }
            for (i = 0; Felix.this.m_uninstalledBundles != null && i < Felix.this.m_uninstalledBundles.size(); ++i) {
                try {
                    ((BundleImpl)Felix.this.m_uninstalledBundles.get(i)).closeAndDelete();
                    continue;
                }
                catch (Exception ex) {
                    Felix.this.m_logger.log((Bundle)Felix.this.m_uninstalledBundles.get(i), 1, "Unable to remove " + ((BundleImpl)Felix.this.m_uninstalledBundles.get(i))._getLocation(), (Throwable)ex);
                }
            }
            bundles = Felix.this.getBundles();
            for (i = 0; i < bundles.length; ++i) {
                ((BundleImpl)bundles[i]).close();
            }
            Felix.this.m_extensionManager.stopExtensionBundles(Felix.this);
            for (i = 0; i < Felix.this.m_activatorList.size(); ++i) {
                try {
                    m_secureAction.stopActivator((BundleActivator)Felix.this.m_activatorList.get(i), Felix.this._getBundleContext());
                    continue;
                }
                catch (Throwable throwable) {
                    Felix.this.fireFrameworkEvent(2, context.getBundle(), new BundleException("Unable to stop Bundle", throwable));
                    Felix.this.m_logger.log(2, "Exception stopping a system bundle activator.", throwable);
                }
            }
            this.m_reg.unregister();
            Felix.this.m_activatorList.clear();
            if (Felix.this.m_securityManager != null) {
                System.setSecurityManager(null);
                Felix.this.m_securityManager = null;
            }
            Felix.this.m_dependencies.removeDependents(Felix.this.adapt(BundleRevision.class));
            Felix.this.m_cache.release();
            Felix.this.m_cache = null;
            Felix.this.acquireBundleLock(Felix.this, 16);
            try {
                ((BundleContextImpl)Felix.this._getBundleContext()).invalidate();
                Felix.this.setBundleContext(null);
                Felix.this.setBundleStateAndNotify(Felix.this, 4);
                Felix.this.m_shutdownGate.open();
                Felix.this.m_shutdownGate = null;
            }
            finally {
                Felix.this.releaseBundleLock(Felix.this);
            }
        }
    }
}

