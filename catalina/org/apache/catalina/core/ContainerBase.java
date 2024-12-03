/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.ExceptionUtils
 *  org.apache.tomcat.util.MultiThrowable
 *  org.apache.tomcat.util.res.StringManager
 *  org.apache.tomcat.util.threads.InlineExecutorService
 */
package org.apache.catalina.core;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.management.ObjectName;
import org.apache.catalina.AccessLog;
import org.apache.catalina.Cluster;
import org.apache.catalina.Container;
import org.apache.catalina.ContainerEvent;
import org.apache.catalina.ContainerListener;
import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Globals;
import org.apache.catalina.Host;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Pipeline;
import org.apache.catalina.Realm;
import org.apache.catalina.Server;
import org.apache.catalina.Valve;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.core.AccessLogAdapter;
import org.apache.catalina.core.StandardPipeline;
import org.apache.catalina.util.ContextName;
import org.apache.catalina.util.LifecycleMBeanBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.MultiThrowable;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.util.threads.InlineExecutorService;

public abstract class ContainerBase
extends LifecycleMBeanBase
implements Container {
    private static final Log log = LogFactory.getLog(ContainerBase.class);
    protected final HashMap<String, Container> children = new HashMap();
    protected int backgroundProcessorDelay = -1;
    protected ScheduledFuture<?> backgroundProcessorFuture;
    protected ScheduledFuture<?> monitorFuture;
    protected final List<ContainerListener> listeners = new CopyOnWriteArrayList<ContainerListener>();
    protected Log logger = null;
    protected String logName = null;
    protected Cluster cluster = null;
    private final ReadWriteLock clusterLock = new ReentrantReadWriteLock();
    protected String name = null;
    protected Container parent = null;
    protected ClassLoader parentClassLoader = null;
    protected final Pipeline pipeline = new StandardPipeline(this);
    private volatile Realm realm = null;
    private final ReadWriteLock realmLock = new ReentrantReadWriteLock();
    protected static final StringManager sm = StringManager.getManager(ContainerBase.class);
    protected boolean startChildren = true;
    protected final PropertyChangeSupport support = new PropertyChangeSupport(this);
    protected volatile AccessLog accessLog = null;
    private volatile boolean accessLogScanComplete = false;
    private int startStopThreads = 1;
    protected ExecutorService startStopExecutor;

    @Override
    public int getStartStopThreads() {
        return this.startStopThreads;
    }

    @Override
    public void setStartStopThreads(int startStopThreads) {
        int oldStartStopThreads = this.startStopThreads;
        this.startStopThreads = startStopThreads;
        if (oldStartStopThreads != startStopThreads && this.startStopExecutor != null) {
            this.reconfigureStartStopExecutor(this.getStartStopThreads());
        }
    }

    @Override
    public int getBackgroundProcessorDelay() {
        return this.backgroundProcessorDelay;
    }

    @Override
    public void setBackgroundProcessorDelay(int delay) {
        this.backgroundProcessorDelay = delay;
    }

    @Override
    public Log getLogger() {
        if (this.logger != null) {
            return this.logger;
        }
        this.logger = LogFactory.getLog((String)this.getLogName());
        return this.logger;
    }

    @Override
    public String getLogName() {
        if (this.logName != null) {
            return this.logName;
        }
        String loggerName = null;
        for (Container current = this; current != null; current = current.getParent()) {
            String name = current.getName();
            if (name == null || name.equals("")) {
                name = "/";
            } else if (name.startsWith("##")) {
                name = "/" + name;
            }
            loggerName = "[" + name + "]" + (loggerName != null ? "." + loggerName : "");
        }
        this.logName = ContainerBase.class.getName() + "." + loggerName;
        return this.logName;
    }

    @Override
    public Cluster getCluster() {
        Lock readLock = this.clusterLock.readLock();
        readLock.lock();
        try {
            if (this.cluster != null) {
                Cluster cluster = this.cluster;
                return cluster;
            }
            if (this.parent != null) {
                Cluster cluster = this.parent.getCluster();
                return cluster;
            }
            Cluster cluster = null;
            return cluster;
        }
        finally {
            readLock.unlock();
        }
    }

    protected Cluster getClusterInternal() {
        Lock readLock = this.clusterLock.readLock();
        readLock.lock();
        try {
            Cluster cluster = this.cluster;
            return cluster;
        }
        finally {
            readLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setCluster(Cluster cluster) {
        Cluster oldCluster = null;
        Lock writeLock = this.clusterLock.writeLock();
        writeLock.lock();
        try {
            oldCluster = this.cluster;
            if (oldCluster == cluster) {
                return;
            }
            this.cluster = cluster;
            if (this.getState().isAvailable() && oldCluster instanceof Lifecycle) {
                try {
                    ((Lifecycle)((Object)oldCluster)).stop();
                }
                catch (LifecycleException e) {
                    log.error((Object)sm.getString("containerBase.cluster.stop"), (Throwable)e);
                }
            }
            if (cluster != null) {
                cluster.setContainer(this);
            }
            if (this.getState().isAvailable() && cluster instanceof Lifecycle) {
                try {
                    ((Lifecycle)((Object)cluster)).start();
                }
                catch (LifecycleException e) {
                    log.error((Object)sm.getString("containerBase.cluster.start"), (Throwable)e);
                }
            }
        }
        finally {
            writeLock.unlock();
        }
        this.support.firePropertyChange("cluster", oldCluster, cluster);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        if (name == null) {
            throw new IllegalArgumentException(sm.getString("containerBase.nullName"));
        }
        String oldName = this.name;
        this.name = name;
        this.support.firePropertyChange("name", oldName, this.name);
    }

    public boolean getStartChildren() {
        return this.startChildren;
    }

    public void setStartChildren(boolean startChildren) {
        boolean oldStartChildren = this.startChildren;
        this.startChildren = startChildren;
        this.support.firePropertyChange("startChildren", oldStartChildren, this.startChildren);
    }

    @Override
    public Container getParent() {
        return this.parent;
    }

    @Override
    public void setParent(Container container) {
        Container oldParent = this.parent;
        this.parent = container;
        this.support.firePropertyChange("parent", oldParent, this.parent);
    }

    @Override
    public ClassLoader getParentClassLoader() {
        if (this.parentClassLoader != null) {
            return this.parentClassLoader;
        }
        if (this.parent != null) {
            return this.parent.getParentClassLoader();
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
    public Pipeline getPipeline() {
        return this.pipeline;
    }

    @Override
    public Realm getRealm() {
        Lock l = this.realmLock.readLock();
        l.lock();
        try {
            if (this.realm != null) {
                Realm realm = this.realm;
                return realm;
            }
            if (this.parent != null) {
                Realm realm = this.parent.getRealm();
                return realm;
            }
            Realm realm = null;
            return realm;
        }
        finally {
            l.unlock();
        }
    }

    protected Realm getRealmInternal() {
        Lock l = this.realmLock.readLock();
        l.lock();
        try {
            Realm realm = this.realm;
            return realm;
        }
        finally {
            l.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setRealm(Realm realm) {
        Lock l = this.realmLock.writeLock();
        l.lock();
        try {
            Realm oldRealm = this.realm;
            if (oldRealm == realm) {
                return;
            }
            this.realm = realm;
            if (this.getState().isAvailable() && oldRealm instanceof Lifecycle) {
                try {
                    ((Lifecycle)((Object)oldRealm)).stop();
                }
                catch (LifecycleException e) {
                    log.error((Object)sm.getString("containerBase.realm.stop"), (Throwable)e);
                }
            }
            if (realm != null) {
                realm.setContainer(this);
            }
            if (this.getState().isAvailable() && realm instanceof Lifecycle) {
                try {
                    ((Lifecycle)((Object)realm)).start();
                }
                catch (LifecycleException e) {
                    log.error((Object)sm.getString("containerBase.realm.start"), (Throwable)e);
                }
            }
            this.support.firePropertyChange("realm", oldRealm, this.realm);
        }
        finally {
            l.unlock();
        }
    }

    @Override
    public void addChild(Container child) {
        if (Globals.IS_SECURITY_ENABLED) {
            PrivilegedAddChild dp = new PrivilegedAddChild(child);
            AccessController.doPrivileged(dp);
        } else {
            this.addChildInternal(child);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void addChildInternal(Container child) {
        if (log.isDebugEnabled()) {
            log.debug((Object)("Add child " + child + " " + this));
        }
        HashMap<String, Container> hashMap = this.children;
        synchronized (hashMap) {
            if (this.children.get(child.getName()) != null) {
                throw new IllegalArgumentException(sm.getString("containerBase.child.notUnique", new Object[]{child.getName()}));
            }
            child.setParent(this);
            this.children.put(child.getName(), child);
        }
        this.fireContainerEvent("addChild", child);
        try {
            if ((this.getState().isAvailable() || LifecycleState.STARTING_PREP.equals((Object)this.getState())) && this.startChildren) {
                child.start();
            }
        }
        catch (LifecycleException e) {
            throw new IllegalStateException(sm.getString("containerBase.child.start"), e);
        }
    }

    @Override
    public void addContainerListener(ContainerListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.support.addPropertyChangeListener(listener);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Container findChild(String name) {
        if (name == null) {
            return null;
        }
        HashMap<String, Container> hashMap = this.children;
        synchronized (hashMap) {
            return this.children.get(name);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Container[] findChildren() {
        HashMap<String, Container> hashMap = this.children;
        synchronized (hashMap) {
            return this.children.values().toArray(new Container[0]);
        }
    }

    @Override
    public ContainerListener[] findContainerListeners() {
        return this.listeners.toArray(new ContainerListener[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeChild(Container child) {
        if (child == null) {
            return;
        }
        try {
            if (child.getState().isAvailable()) {
                child.stop();
            }
        }
        catch (LifecycleException e) {
            log.error((Object)sm.getString("containerBase.child.stop"), (Throwable)e);
        }
        boolean destroy = false;
        try {
            if (!LifecycleState.DESTROYING.equals((Object)child.getState())) {
                child.destroy();
                destroy = true;
            }
        }
        catch (LifecycleException e) {
            log.error((Object)sm.getString("containerBase.child.destroy"), (Throwable)e);
        }
        if (!destroy) {
            this.fireContainerEvent("removeChild", child);
        }
        HashMap<String, Container> hashMap = this.children;
        synchronized (hashMap) {
            if (this.children.get(child.getName()) == null) {
                return;
            }
            this.children.remove(child.getName());
        }
    }

    @Override
    public void removeContainerListener(ContainerListener listener) {
        this.listeners.remove(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.support.removePropertyChangeListener(listener);
    }

    private void reconfigureStartStopExecutor(int threads) {
        if (threads == 1) {
            if (!(this.startStopExecutor instanceof InlineExecutorService)) {
                this.startStopExecutor = new InlineExecutorService();
            }
        } else {
            Server server = Container.getService(this).getServer();
            server.setUtilityThreads(threads);
            this.startStopExecutor = server.getUtilityExecutor();
        }
    }

    /*
     * WARNING - void declaration
     */
    @Override
    protected synchronized void startInternal() throws LifecycleException {
        void var7_9;
        Realm realm;
        this.reconfigureStartStopExecutor(this.getStartStopThreads());
        this.logger = null;
        this.getLogger();
        Cluster cluster = this.getClusterInternal();
        if (cluster instanceof Lifecycle) {
            ((Lifecycle)((Object)cluster)).start();
        }
        if ((realm = this.getRealmInternal()) instanceof Lifecycle) {
            ((Lifecycle)((Object)realm)).start();
        }
        Container[] children = this.findChildren();
        ArrayList<Future<Void>> results = new ArrayList<Future<Void>>(children.length);
        Container[] containerArray = children;
        int n = containerArray.length;
        boolean bl = false;
        while (var7_9 < n) {
            Container child = containerArray[var7_9];
            results.add(this.startStopExecutor.submit(new StartChild(child)));
            ++var7_9;
        }
        MultiThrowable multiThrowable = null;
        for (Future future : results) {
            try {
                future.get();
            }
            catch (Throwable e) {
                log.error((Object)sm.getString("containerBase.threadedStartFailed"), e);
                if (multiThrowable == null) {
                    multiThrowable = new MultiThrowable();
                }
                multiThrowable.add(e);
            }
        }
        if (multiThrowable != null) {
            throw new LifecycleException(sm.getString("containerBase.threadedStartFailed"), multiThrowable.getThrowable());
        }
        if (this.pipeline instanceof Lifecycle) {
            ((Lifecycle)((Object)this.pipeline)).start();
        }
        this.setState(LifecycleState.STARTING);
        if (this.backgroundProcessorDelay > 0) {
            this.monitorFuture = Container.getService(this).getServer().getUtilityExecutor().scheduleWithFixedDelay(new ContainerBackgroundProcessorMonitor(), 0L, 60L, TimeUnit.SECONDS);
        }
    }

    /*
     * WARNING - void declaration
     */
    @Override
    protected synchronized void stopInternal() throws LifecycleException {
        Cluster cluster;
        void var5_8;
        if (this.monitorFuture != null) {
            this.monitorFuture.cancel(true);
            this.monitorFuture = null;
        }
        this.threadStop();
        this.setState(LifecycleState.STOPPING);
        if (this.pipeline instanceof Lifecycle && ((Lifecycle)((Object)this.pipeline)).getState().isAvailable()) {
            ((Lifecycle)((Object)this.pipeline)).stop();
        }
        Container[] children = this.findChildren();
        ArrayList<Future<Void>> results = new ArrayList<Future<Void>>(children.length);
        Container[] containerArray = children;
        int n = containerArray.length;
        boolean bl = false;
        while (var5_8 < n) {
            Container child = containerArray[var5_8];
            results.add(this.startStopExecutor.submit(new StopChild(child)));
            ++var5_8;
        }
        boolean fail = false;
        for (Future future : results) {
            try {
                future.get();
            }
            catch (Exception e) {
                log.error((Object)sm.getString("containerBase.threadedStopFailed"), (Throwable)e);
                fail = true;
            }
        }
        if (fail) {
            throw new LifecycleException(sm.getString("containerBase.threadedStopFailed"));
        }
        Realm realm = this.getRealmInternal();
        if (realm instanceof Lifecycle) {
            ((Lifecycle)((Object)realm)).stop();
        }
        if ((cluster = this.getClusterInternal()) instanceof Lifecycle) {
            ((Lifecycle)((Object)cluster)).stop();
        }
        if (this.startStopExecutor != null) {
            this.startStopExecutor.shutdownNow();
            this.startStopExecutor = null;
        }
    }

    @Override
    protected void destroyInternal() throws LifecycleException {
        Cluster cluster;
        Realm realm = this.getRealmInternal();
        if (realm instanceof Lifecycle) {
            ((Lifecycle)((Object)realm)).destroy();
        }
        if ((cluster = this.getClusterInternal()) instanceof Lifecycle) {
            ((Lifecycle)((Object)cluster)).destroy();
        }
        if (this.pipeline instanceof Lifecycle) {
            ((Lifecycle)((Object)this.pipeline)).destroy();
        }
        for (Container child : this.findChildren()) {
            this.removeChild(child);
        }
        if (this.parent != null) {
            this.parent.removeChild(this);
        }
        super.destroyInternal();
    }

    @Override
    public void logAccess(Request request, Response response, long time, boolean useDefault) {
        boolean logged = false;
        if (this.getAccessLog() != null) {
            this.getAccessLog().log(request, response, time);
            logged = true;
        }
        if (this.getParent() != null) {
            this.getParent().logAccess(request, response, time, useDefault && !logged);
        }
    }

    @Override
    public AccessLog getAccessLog() {
        Valve[] valves;
        if (this.accessLogScanComplete) {
            return this.accessLog;
        }
        AccessLogAdapter adapter = null;
        for (Valve valve : valves = this.getPipeline().getValves()) {
            if (!(valve instanceof AccessLog)) continue;
            if (adapter == null) {
                adapter = new AccessLogAdapter((AccessLog)((Object)valve));
                continue;
            }
            adapter.add((AccessLog)((Object)valve));
        }
        if (adapter != null) {
            this.accessLog = adapter;
        }
        this.accessLogScanComplete = true;
        return this.accessLog;
    }

    public synchronized void addValve(Valve valve) {
        this.pipeline.addValve(valve);
    }

    @Override
    public void backgroundProcess() {
        Realm realm;
        if (!this.getState().isAvailable()) {
            return;
        }
        Cluster cluster = this.getClusterInternal();
        if (cluster != null) {
            try {
                cluster.backgroundProcess();
            }
            catch (Exception e) {
                log.warn((Object)sm.getString("containerBase.backgroundProcess.cluster", new Object[]{cluster}), (Throwable)e);
            }
        }
        if ((realm = this.getRealmInternal()) != null) {
            try {
                realm.backgroundProcess();
            }
            catch (Exception e) {
                log.warn((Object)sm.getString("containerBase.backgroundProcess.realm", new Object[]{realm}), (Throwable)e);
            }
        }
        for (Valve current = this.pipeline.getFirst(); current != null; current = current.getNext()) {
            try {
                current.backgroundProcess();
                continue;
            }
            catch (Exception e) {
                log.warn((Object)sm.getString("containerBase.backgroundProcess.valve", new Object[]{current}), (Throwable)e);
            }
        }
        this.fireLifecycleEvent("periodic", null);
    }

    @Override
    public File getCatalinaBase() {
        if (this.parent == null) {
            return null;
        }
        return this.parent.getCatalinaBase();
    }

    @Override
    public File getCatalinaHome() {
        if (this.parent == null) {
            return null;
        }
        return this.parent.getCatalinaHome();
    }

    @Override
    public void fireContainerEvent(String type, Object data) {
        if (this.listeners.size() < 1) {
            return;
        }
        ContainerEvent event = new ContainerEvent(this, type, data);
        for (ContainerListener listener : this.listeners) {
            listener.containerEvent(event);
        }
    }

    @Override
    protected String getDomainInternal() {
        Container p = this.getParent();
        if (p == null) {
            return null;
        }
        return p.getDomain();
    }

    @Override
    public String getMBeanKeyProperties() {
        Container c = this;
        StringBuilder keyProperties = new StringBuilder();
        int containerCount = 0;
        while (!(c instanceof Engine)) {
            if (c instanceof Wrapper) {
                keyProperties.insert(0, ",servlet=");
                keyProperties.insert(9, c.getName());
            } else if (c instanceof Context) {
                keyProperties.insert(0, ",context=");
                ContextName cn = new ContextName(c.getName(), false);
                keyProperties.insert(9, cn.getDisplayName());
            } else if (c instanceof Host) {
                keyProperties.insert(0, ",host=");
                keyProperties.insert(6, c.getName());
            } else {
                if (c == null) {
                    keyProperties.append(",container");
                    keyProperties.append(containerCount++);
                    keyProperties.append("=null");
                    break;
                }
                keyProperties.append(",container");
                keyProperties.append(containerCount++);
                keyProperties.append('=');
                keyProperties.append(c.getName());
            }
            c = c.getParent();
        }
        return keyProperties.toString();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ObjectName[] getChildren() {
        ArrayList<ObjectName> names;
        HashMap<String, Container> hashMap = this.children;
        synchronized (hashMap) {
            names = new ArrayList<ObjectName>(this.children.size());
            for (Container next : this.children.values()) {
                if (!(next instanceof ContainerBase)) continue;
                names.add(next.getObjectName());
            }
        }
        return names.toArray(new ObjectName[0]);
    }

    protected void threadStart() {
        if (this.backgroundProcessorDelay > 0 && (this.getState().isAvailable() || LifecycleState.STARTING_PREP.equals((Object)this.getState())) && (this.backgroundProcessorFuture == null || this.backgroundProcessorFuture.isDone())) {
            if (this.backgroundProcessorFuture != null && this.backgroundProcessorFuture.isDone()) {
                try {
                    this.backgroundProcessorFuture.get();
                }
                catch (InterruptedException | ExecutionException e) {
                    log.error((Object)sm.getString("containerBase.backgroundProcess.error"), (Throwable)e);
                }
            }
            this.backgroundProcessorFuture = Container.getService(this).getServer().getUtilityExecutor().scheduleWithFixedDelay(new ContainerBackgroundProcessor(), this.backgroundProcessorDelay, this.backgroundProcessorDelay, TimeUnit.SECONDS);
        }
    }

    protected void threadStop() {
        if (this.backgroundProcessorFuture != null) {
            this.backgroundProcessorFuture.cancel(true);
            this.backgroundProcessorFuture = null;
        }
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder();
        Container parent = this.getParent();
        if (parent != null) {
            sb.append(parent.toString());
            sb.append('.');
        }
        sb.append(this.getClass().getSimpleName());
        sb.append('[');
        sb.append(this.getName());
        sb.append(']');
        return sb.toString();
    }

    static /* synthetic */ Log access$100() {
        return log;
    }

    protected class PrivilegedAddChild
    implements PrivilegedAction<Void> {
        private final Container child;

        PrivilegedAddChild(Container child) {
            this.child = child;
        }

        @Override
        public Void run() {
            ContainerBase.this.addChildInternal(this.child);
            return null;
        }
    }

    private static class StartChild
    implements Callable<Void> {
        private Container child;

        StartChild(Container child) {
            this.child = child;
        }

        @Override
        public Void call() throws LifecycleException {
            this.child.start();
            return null;
        }
    }

    protected class ContainerBackgroundProcessorMonitor
    implements Runnable {
        protected ContainerBackgroundProcessorMonitor() {
        }

        @Override
        public void run() {
            if (ContainerBase.this.getState().isAvailable()) {
                ContainerBase.this.threadStart();
            }
        }
    }

    private static class StopChild
    implements Callable<Void> {
        private Container child;

        StopChild(Container child) {
            this.child = child;
        }

        @Override
        public Void call() throws LifecycleException {
            if (this.child.getState().isAvailable()) {
                this.child.stop();
            }
            return null;
        }
    }

    protected class ContainerBackgroundProcessor
    implements Runnable {
        protected ContainerBackgroundProcessor() {
        }

        @Override
        public void run() {
            this.processChildren(ContainerBase.this);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Unable to fully structure code
         */
        protected void processChildren(Container container) {
            block10: {
                originalClassLoader = null;
                if (!(container instanceof Context)) ** GOTO lbl12
                loader = ((Context)container).getLoader();
                if (loader != null) break block10;
                if (container instanceof Context) {
                    ((Context)container).unbind(false, originalClassLoader);
                }
                return;
            }
            try {
                originalClassLoader = ((Context)container).bind(false, null);
lbl12:
                // 2 sources

                container.backgroundProcess();
                for (Container child : children = container.findChildren()) {
                    if (child.getBackgroundProcessorDelay() > 0) continue;
                    this.processChildren(child);
                }
            }
            catch (Throwable t) {
                try {
                    ExceptionUtils.handleThrowable((Throwable)t);
                    ContainerBase.access$100().error((Object)ContainerBase.sm.getString("containerBase.backgroundProcess.error"), t);
                }
                catch (Throwable var8_9) {
                    if (container instanceof Context) {
                        ((Context)container).unbind(false, originalClassLoader);
                    }
                    throw var8_9;
                }
                if (container instanceof Context) {
                    ((Context)container).unbind(false, originalClassLoader);
                } else {
                    ** GOTO lbl31
                }
            }
            if (container instanceof Context) {
                ((Context)container).unbind(false, originalClassLoader);
            }
        }
    }
}

