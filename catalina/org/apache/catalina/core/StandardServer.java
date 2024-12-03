/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.ExceptionUtils
 *  org.apache.tomcat.util.buf.StringCache
 *  org.apache.tomcat.util.modeler.Registry
 *  org.apache.tomcat.util.res.StringManager
 *  org.apache.tomcat.util.threads.ScheduledThreadPoolExecutor
 *  org.apache.tomcat.util.threads.TaskThreadFactory
 */
package org.apache.catalina.core;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Server;
import org.apache.catalina.Service;
import org.apache.catalina.core.NamingContextListener;
import org.apache.catalina.core.StandardService;
import org.apache.catalina.deploy.NamingResourcesImpl;
import org.apache.catalina.mbeans.MBeanFactory;
import org.apache.catalina.startup.Catalina;
import org.apache.catalina.util.ExtensionValidator;
import org.apache.catalina.util.LifecycleMBeanBase;
import org.apache.catalina.util.ServerInfo;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.buf.StringCache;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.util.threads.TaskThreadFactory;

public final class StandardServer
extends LifecycleMBeanBase
implements Server {
    private static final Log log = LogFactory.getLog(StandardServer.class);
    private static final StringManager sm = StringManager.getManager(StandardServer.class);
    private javax.naming.Context globalNamingContext = null;
    private NamingResourcesImpl globalNamingResources = null;
    private final NamingContextListener namingContextListener;
    private int port = 8005;
    private int portOffset = 0;
    private String address = "localhost";
    private Random random = null;
    private Service[] services = new Service[0];
    private final Object servicesLock = new Object();
    private String shutdown = "SHUTDOWN";
    final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private volatile boolean stopAwait = false;
    private Catalina catalina = null;
    private ClassLoader parentClassLoader = null;
    private volatile Thread awaitThread = null;
    private volatile ServerSocket awaitSocket = null;
    private File catalinaHome = null;
    private File catalinaBase = null;
    private final Object namingToken = new Object();
    private int utilityThreads = 2;
    private boolean utilityThreadsAsDaemon = false;
    private ScheduledThreadPoolExecutor utilityExecutor = null;
    private final Object utilityExecutorLock = new Object();
    private ScheduledExecutorService utilityExecutorWrapper = null;
    private ScheduledFuture<?> periodicLifecycleEventFuture = null;
    private ScheduledFuture<?> monitorFuture;
    private int periodicEventDelay = 10;
    private ObjectName onameStringCache;
    private ObjectName onameMBeanFactory;

    public StandardServer() {
        this.globalNamingResources = new NamingResourcesImpl();
        this.globalNamingResources.setContainer(this);
        if (this.isUseNaming()) {
            this.namingContextListener = new NamingContextListener();
            this.addLifecycleListener(this.namingContextListener);
        } else {
            this.namingContextListener = null;
        }
    }

    @Override
    public Object getNamingToken() {
        return this.namingToken;
    }

    @Override
    public javax.naming.Context getGlobalNamingContext() {
        return this.globalNamingContext;
    }

    public void setGlobalNamingContext(javax.naming.Context globalNamingContext) {
        this.globalNamingContext = globalNamingContext;
    }

    @Override
    public NamingResourcesImpl getGlobalNamingResources() {
        return this.globalNamingResources;
    }

    @Override
    public void setGlobalNamingResources(NamingResourcesImpl globalNamingResources) {
        NamingResourcesImpl oldGlobalNamingResources = this.globalNamingResources;
        this.globalNamingResources = globalNamingResources;
        this.globalNamingResources.setContainer(this);
        this.support.firePropertyChange("globalNamingResources", oldGlobalNamingResources, this.globalNamingResources);
    }

    public String getServerInfo() {
        return ServerInfo.getServerInfo();
    }

    public String getServerBuilt() {
        return ServerInfo.getServerBuilt();
    }

    public String getServerNumber() {
        return ServerInfo.getServerNumber();
    }

    @Override
    public int getPort() {
        return this.port;
    }

    @Override
    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public int getPortOffset() {
        return this.portOffset;
    }

    @Override
    public void setPortOffset(int portOffset) {
        if (portOffset < 0) {
            throw new IllegalArgumentException(sm.getString("standardServer.portOffset.invalid", new Object[]{portOffset}));
        }
        this.portOffset = portOffset;
    }

    @Override
    public int getPortWithOffset() {
        int port = this.getPort();
        if (port > 0) {
            return port + this.getPortOffset();
        }
        return port;
    }

    @Override
    public String getAddress() {
        return this.address;
    }

    @Override
    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String getShutdown() {
        return this.shutdown;
    }

    @Override
    public void setShutdown(String shutdown) {
        this.shutdown = shutdown;
    }

    @Override
    public Catalina getCatalina() {
        return this.catalina;
    }

    @Override
    public void setCatalina(Catalina catalina) {
        this.catalina = catalina;
    }

    @Override
    public int getUtilityThreads() {
        return this.utilityThreads;
    }

    private static int getUtilityThreadsInternal(int utilityThreads) {
        int result = utilityThreads;
        if (result <= 0 && (result = Runtime.getRuntime().availableProcessors() + result) < 2) {
            result = 2;
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setUtilityThreads(int utilityThreads) {
        int oldUtilityThreads = this.utilityThreads;
        if (StandardServer.getUtilityThreadsInternal(utilityThreads) < StandardServer.getUtilityThreadsInternal(oldUtilityThreads)) {
            return;
        }
        this.utilityThreads = utilityThreads;
        Object object = this.utilityExecutorLock;
        synchronized (object) {
            if (oldUtilityThreads != utilityThreads && this.utilityExecutor != null) {
                this.reconfigureUtilityExecutor(StandardServer.getUtilityThreadsInternal(utilityThreads));
            }
        }
    }

    private void reconfigureUtilityExecutor(int threads) {
        if (this.utilityExecutor != null) {
            this.utilityExecutor.setCorePoolSize(threads);
        } else {
            ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(threads, (ThreadFactory)new TaskThreadFactory("Catalina-utility-", this.utilityThreadsAsDaemon, 1));
            scheduledThreadPoolExecutor.setKeepAliveTime(10L, TimeUnit.SECONDS);
            scheduledThreadPoolExecutor.setRemoveOnCancelPolicy(true);
            scheduledThreadPoolExecutor.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
            this.utilityExecutor = scheduledThreadPoolExecutor;
            this.utilityExecutorWrapper = new org.apache.tomcat.util.threads.ScheduledThreadPoolExecutor((ScheduledExecutorService)this.utilityExecutor);
        }
    }

    public boolean getUtilityThreadsAsDaemon() {
        return this.utilityThreadsAsDaemon;
    }

    public void setUtilityThreadsAsDaemon(boolean utilityThreadsAsDaemon) {
        this.utilityThreadsAsDaemon = utilityThreadsAsDaemon;
    }

    public int getPeriodicEventDelay() {
        return this.periodicEventDelay;
    }

    public void setPeriodicEventDelay(int periodicEventDelay) {
        this.periodicEventDelay = periodicEventDelay;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addService(Service service) {
        service.setServer(this);
        Object object = this.servicesLock;
        synchronized (object) {
            Service[] results = new Service[this.services.length + 1];
            System.arraycopy(this.services, 0, results, 0, this.services.length);
            results[this.services.length] = service;
            this.services = results;
            if (this.getState().isAvailable()) {
                try {
                    service.start();
                }
                catch (LifecycleException lifecycleException) {
                    // empty catch block
                }
            }
            this.support.firePropertyChange("service", null, service);
        }
    }

    public void stopAwait() {
        this.stopAwait = true;
        Thread t = this.awaitThread;
        if (t != null) {
            ServerSocket s = this.awaitSocket;
            if (s != null) {
                this.awaitSocket = null;
                try {
                    s.close();
                }
                catch (IOException iOException) {
                    // empty catch block
                }
            }
            t.interrupt();
            try {
                t.join(1000L);
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
        }
    }

    /*
     * Exception decompiling
     */
    @Override
    public void await() {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 3 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Service findService(String name) {
        if (name == null) {
            return null;
        }
        Object object = this.servicesLock;
        synchronized (object) {
            for (Service service : this.services) {
                if (!name.equals(service.getName())) continue;
                return service;
            }
        }
        return null;
    }

    @Override
    public Service[] findServices() {
        return this.services;
    }

    public ObjectName[] getServiceNames() {
        ObjectName[] onames = new ObjectName[this.services.length];
        for (int i = 0; i < this.services.length; ++i) {
            onames[i] = ((StandardService)this.services[i]).getObjectName();
        }
        return onames;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeService(Service service) {
        Object object = this.servicesLock;
        synchronized (object) {
            int j = -1;
            for (int i = 0; i < this.services.length; ++i) {
                if (service != this.services[i]) continue;
                j = i;
                break;
            }
            if (j < 0) {
                return;
            }
            try {
                this.services[j].stop();
            }
            catch (LifecycleException i) {
                // empty catch block
            }
            int k = 0;
            Service[] results = new Service[this.services.length - 1];
            for (int i = 0; i < this.services.length; ++i) {
                if (i == j) continue;
                results[k++] = this.services[i];
            }
            this.services = results;
            this.support.firePropertyChange("service", service, null);
        }
    }

    @Override
    public File getCatalinaBase() {
        if (this.catalinaBase != null) {
            return this.catalinaBase;
        }
        this.catalinaBase = this.getCatalinaHome();
        return this.catalinaBase;
    }

    @Override
    public void setCatalinaBase(File catalinaBase) {
        this.catalinaBase = catalinaBase;
    }

    @Override
    public File getCatalinaHome() {
        return this.catalinaHome;
    }

    @Override
    public void setCatalinaHome(File catalinaHome) {
        this.catalinaHome = catalinaHome;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.support.removePropertyChangeListener(listener);
    }

    public String toString() {
        return "StandardServer[" + this.getPort() + ']';
    }

    public synchronized void storeConfig() throws InstanceNotFoundException, MBeanException {
        try {
            ObjectName sname = new ObjectName("Catalina:type=StoreConfig");
            MBeanServer server = Registry.getRegistry(null, null).getMBeanServer();
            if (server.isRegistered(sname)) {
                server.invoke(sname, "storeConfig", null, null);
            } else {
                log.error((Object)sm.getString("standardServer.storeConfig.notAvailable", new Object[]{sname}));
            }
        }
        catch (Throwable t) {
            ExceptionUtils.handleThrowable((Throwable)t);
            log.error((Object)sm.getString("standardServer.storeConfig.error"), t);
        }
    }

    public synchronized void storeContext(Context context) throws InstanceNotFoundException, MBeanException {
        try {
            ObjectName sname = new ObjectName("Catalina:type=StoreConfig");
            MBeanServer server = Registry.getRegistry(null, null).getMBeanServer();
            if (server.isRegistered(sname)) {
                server.invoke(sname, "store", new Object[]{context}, new String[]{"java.lang.String"});
            } else {
                log.error((Object)sm.getString("standardServer.storeConfig.notAvailable", new Object[]{sname}));
            }
        }
        catch (Throwable t) {
            ExceptionUtils.handleThrowable((Throwable)t);
            log.error((Object)sm.getString("standardServer.storeConfig.contextError", new Object[]{context.getName()}), t);
        }
    }

    private boolean isUseNaming() {
        boolean useNaming = true;
        String useNamingProperty = System.getProperty("catalina.useNaming");
        if (useNamingProperty != null && useNamingProperty.equals("false")) {
            useNaming = false;
        }
        return useNaming;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void startInternal() throws LifecycleException {
        this.fireLifecycleEvent("configure_start", null);
        this.setState(LifecycleState.STARTING);
        Object object = this.utilityExecutorLock;
        synchronized (object) {
            this.reconfigureUtilityExecutor(StandardServer.getUtilityThreadsInternal(this.utilityThreads));
            this.register(this.utilityExecutor, "type=UtilityExecutor");
        }
        this.globalNamingResources.start();
        object = this.servicesLock;
        synchronized (object) {
            for (Service service : this.services) {
                service.start();
            }
        }
        if (this.periodicEventDelay > 0) {
            this.monitorFuture = this.getUtilityExecutor().scheduleWithFixedDelay(this::startPeriodicLifecycleEvent, 0L, 60L, TimeUnit.SECONDS);
        }
    }

    private void startPeriodicLifecycleEvent() {
        if (this.periodicLifecycleEventFuture == null || this.periodicLifecycleEventFuture.isDone()) {
            if (this.periodicLifecycleEventFuture != null && this.periodicLifecycleEventFuture.isDone()) {
                try {
                    this.periodicLifecycleEventFuture.get();
                }
                catch (InterruptedException | ExecutionException e) {
                    log.error((Object)sm.getString("standardServer.periodicEventError"), (Throwable)e);
                }
            }
            this.periodicLifecycleEventFuture = this.getUtilityExecutor().scheduleAtFixedRate(() -> this.fireLifecycleEvent("periodic", null), this.periodicEventDelay, this.periodicEventDelay, TimeUnit.SECONDS);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void stopInternal() throws LifecycleException {
        this.setState(LifecycleState.STOPPING);
        if (this.monitorFuture != null) {
            this.monitorFuture.cancel(true);
            this.monitorFuture = null;
        }
        if (this.periodicLifecycleEventFuture != null) {
            this.periodicLifecycleEventFuture.cancel(false);
            this.periodicLifecycleEventFuture = null;
        }
        this.fireLifecycleEvent("configure_stop", null);
        for (Service service : this.services) {
            service.stop();
        }
        Object object = this.utilityExecutorLock;
        synchronized (object) {
            if (this.utilityExecutor != null) {
                this.utilityExecutor.shutdownNow();
                this.unregister("type=UtilityExecutor");
                this.utilityExecutor = null;
            }
        }
        this.globalNamingResources.stop();
        this.stopAwait();
    }

    @Override
    protected void initInternal() throws LifecycleException {
        super.initInternal();
        this.onameStringCache = this.register(new StringCache(), "type=StringCache");
        MBeanFactory factory = new MBeanFactory();
        factory.setContainer(this);
        this.onameMBeanFactory = this.register(factory, "type=MBeanFactory");
        this.globalNamingResources.init();
        if (this.getCatalina() != null) {
            for (ClassLoader cl = this.getCatalina().getParentClassLoader(); cl != null && cl != ClassLoader.getSystemClassLoader(); cl = cl.getParent()) {
                URL[] urls;
                if (!(cl instanceof URLClassLoader)) continue;
                for (URL url : urls = ((URLClassLoader)cl).getURLs()) {
                    if (!url.getProtocol().equals("file")) continue;
                    try {
                        File f = new File(url.toURI());
                        if (!f.isFile() || !f.getName().endsWith(".jar")) continue;
                        ExtensionValidator.addSystemResource(f);
                    }
                    catch (IOException | URISyntaxException exception) {
                        // empty catch block
                    }
                }
            }
        }
        for (Service service : this.services) {
            service.init();
        }
    }

    @Override
    protected void destroyInternal() throws LifecycleException {
        for (Service service : this.services) {
            service.destroy();
        }
        this.globalNamingResources.destroy();
        this.unregister(this.onameMBeanFactory);
        this.unregister(this.onameStringCache);
        super.destroyInternal();
    }

    @Override
    public ClassLoader getParentClassLoader() {
        if (this.parentClassLoader != null) {
            return this.parentClassLoader;
        }
        if (this.catalina != null) {
            return this.catalina.getParentClassLoader();
        }
        return ClassLoader.getSystemClassLoader();
    }

    @Override
    public void setParentClassLoader(ClassLoader parent) {
        ClassLoader oldParentClassLoader = this.parentClassLoader;
        this.parentClassLoader = parent;
        this.support.firePropertyChange("parentClassLoader", oldParentClassLoader, this.parentClassLoader);
    }

    @Override
    protected String getDomainInternal() {
        Service service;
        String domain = null;
        Service[] services = this.findServices();
        if (services.length > 0 && (service = services[0]) != null) {
            domain = service.getDomain();
        }
        return domain;
    }

    @Override
    protected String getObjectNameKeyProperties() {
        return "type=Server";
    }

    @Override
    public ScheduledExecutorService getUtilityExecutor() {
        return this.utilityExecutorWrapper;
    }
}

