/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.core;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import javax.management.ObjectName;
import org.apache.catalina.Engine;
import org.apache.catalina.Executor;
import org.apache.catalina.JmxEnabled;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Server;
import org.apache.catalina.Service;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.mapper.Mapper;
import org.apache.catalina.mapper.MapperListener;
import org.apache.catalina.util.LifecycleMBeanBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

public class StandardService
extends LifecycleMBeanBase
implements Service {
    private static final Log log = LogFactory.getLog(StandardService.class);
    private static final StringManager sm = StringManager.getManager(StandardService.class);
    private String name = null;
    private Server server = null;
    protected final PropertyChangeSupport support = new PropertyChangeSupport(this);
    protected Connector[] connectors = new Connector[0];
    private final Object connectorsLock = new Object();
    protected final ArrayList<Executor> executors = new ArrayList();
    private Engine engine = null;
    private ClassLoader parentClassLoader = null;
    protected final Mapper mapper = new Mapper();
    protected final MapperListener mapperListener = new MapperListener(this);
    private long gracefulStopAwaitMillis = 0L;

    public long getGracefulStopAwaitMillis() {
        return this.gracefulStopAwaitMillis;
    }

    public void setGracefulStopAwaitMillis(long gracefulStopAwaitMillis) {
        this.gracefulStopAwaitMillis = gracefulStopAwaitMillis;
    }

    @Override
    public Mapper getMapper() {
        return this.mapper;
    }

    @Override
    public Engine getContainer() {
        return this.engine;
    }

    @Override
    public void setContainer(Engine engine) {
        Engine oldEngine = this.engine;
        if (oldEngine != null) {
            oldEngine.setService(null);
        }
        this.engine = engine;
        if (this.engine != null) {
            this.engine.setService(this);
        }
        if (this.getState().isAvailable()) {
            if (this.engine != null) {
                try {
                    this.engine.start();
                }
                catch (LifecycleException e) {
                    log.error((Object)sm.getString("standardService.engine.startFailed"), (Throwable)e);
                }
            }
            try {
                this.mapperListener.stop();
            }
            catch (LifecycleException e) {
                log.error((Object)sm.getString("standardService.mapperListener.stopFailed"), (Throwable)e);
            }
            try {
                this.mapperListener.start();
            }
            catch (LifecycleException e) {
                log.error((Object)sm.getString("standardService.mapperListener.startFailed"), (Throwable)e);
            }
            if (oldEngine != null) {
                try {
                    oldEngine.stop();
                }
                catch (LifecycleException e) {
                    log.error((Object)sm.getString("standardService.engine.stopFailed"), (Throwable)e);
                }
            }
        }
        this.support.firePropertyChange("container", oldEngine, this.engine);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Server getServer() {
        return this.server;
    }

    @Override
    public void setServer(Server server) {
        this.server = server;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addConnector(Connector connector) {
        Object object = this.connectorsLock;
        synchronized (object) {
            connector.setService(this);
            Connector[] results = new Connector[this.connectors.length + 1];
            System.arraycopy(this.connectors, 0, results, 0, this.connectors.length);
            results[this.connectors.length] = connector;
            this.connectors = results;
        }
        try {
            if (this.getState().isAvailable()) {
                connector.start();
            }
        }
        catch (LifecycleException e) {
            throw new IllegalArgumentException(sm.getString("standardService.connector.startFailed", new Object[]{connector}), e);
        }
        this.support.firePropertyChange("connector", null, connector);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ObjectName[] getConnectorNames() {
        Object object = this.connectorsLock;
        synchronized (object) {
            ObjectName[] results = new ObjectName[this.connectors.length];
            for (int i = 0; i < results.length; ++i) {
                results[i] = this.connectors[i].getObjectName();
            }
            return results;
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.support.addPropertyChangeListener(listener);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Connector[] findConnectors() {
        Object object = this.connectorsLock;
        synchronized (object) {
            return (Connector[])this.connectors.clone();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeConnector(Connector connector) {
        Object object = this.connectorsLock;
        synchronized (object) {
            int j = -1;
            for (int i = 0; i < this.connectors.length; ++i) {
                if (connector != this.connectors[i]) continue;
                j = i;
                break;
            }
            if (j < 0) {
                return;
            }
            if (this.connectors[j].getState().isAvailable()) {
                try {
                    this.connectors[j].stop();
                }
                catch (LifecycleException e) {
                    log.error((Object)sm.getString("standardService.connector.stopFailed", new Object[]{this.connectors[j]}), (Throwable)e);
                }
            }
            connector.setService(null);
            int k = 0;
            Connector[] results = new Connector[this.connectors.length - 1];
            for (int i = 0; i < this.connectors.length; ++i) {
                if (i == j) continue;
                results[k++] = this.connectors[i];
            }
            this.connectors = results;
            this.support.firePropertyChange("connector", connector, null);
        }
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.support.removePropertyChangeListener(listener);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("StandardService[");
        sb.append(this.getName());
        sb.append(']');
        return sb.toString();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addExecutor(Executor ex) {
        ArrayList<Executor> arrayList = this.executors;
        synchronized (arrayList) {
            if (!this.executors.contains(ex)) {
                this.executors.add(ex);
                if (this.getState().isAvailable()) {
                    try {
                        ex.start();
                    }
                    catch (LifecycleException x) {
                        log.error((Object)sm.getString("standardService.executor.start"), (Throwable)x);
                    }
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Executor[] findExecutors() {
        ArrayList<Executor> arrayList = this.executors;
        synchronized (arrayList) {
            return this.executors.toArray(new Executor[0]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Executor getExecutor(String executorName) {
        ArrayList<Executor> arrayList = this.executors;
        synchronized (arrayList) {
            for (Executor executor : this.executors) {
                if (!executorName.equals(executor.getName())) continue;
                return executor;
            }
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeExecutor(Executor ex) {
        ArrayList<Executor> arrayList = this.executors;
        synchronized (arrayList) {
            if (this.executors.remove(ex) && this.getState().isAvailable()) {
                try {
                    ex.stop();
                }
                catch (LifecycleException e) {
                    log.error((Object)sm.getString("standardService.executor.stop"), (Throwable)e);
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void startInternal() throws LifecycleException {
        Object object;
        if (log.isInfoEnabled()) {
            log.info((Object)sm.getString("standardService.start.name", new Object[]{this.name}));
        }
        this.setState(LifecycleState.STARTING);
        if (this.engine != null) {
            object = this.engine;
            synchronized (object) {
                this.engine.start();
            }
        }
        object = this.executors;
        synchronized (object) {
            for (Executor executor : this.executors) {
                executor.start();
            }
        }
        this.mapperListener.start();
        object = this.connectorsLock;
        synchronized (object) {
            for (Connector connector : this.connectors) {
                if (connector.getState() == LifecycleState.FAILED) continue;
                connector.start();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void stopInternal() throws LifecycleException {
        ArrayList<Executor> arrayList = this.connectorsLock;
        synchronized (arrayList) {
            for (Connector connector : this.connectors) {
                connector.getProtocolHandler().closeServerSocketGraceful();
            }
            long waitMillis = this.gracefulStopAwaitMillis;
            if (waitMillis > 0L) {
                Connector[] connectorArray = this.connectors;
                int connector = connectorArray.length;
                for (int i = 0; i < connector; ++i) {
                    Connector connector2 = connectorArray[i];
                    waitMillis = connector2.getProtocolHandler().awaitConnectionsClose(waitMillis);
                }
            }
            for (Connector connector2 : this.connectors) {
                connector2.pause();
            }
        }
        if (log.isInfoEnabled()) {
            log.info((Object)sm.getString("standardService.stop.name", new Object[]{this.name}));
        }
        this.setState(LifecycleState.STOPPING);
        if (this.engine != null) {
            arrayList = this.engine;
            synchronized (arrayList) {
                this.engine.stop();
            }
        }
        arrayList = this.connectorsLock;
        synchronized (arrayList) {
            for (Connector connector : this.connectors) {
                if (!LifecycleState.STARTED.equals((Object)connector.getState())) continue;
                connector.stop();
            }
        }
        if (this.mapperListener.getState() != LifecycleState.INITIALIZED) {
            this.mapperListener.stop();
        }
        arrayList = this.executors;
        synchronized (arrayList) {
            for (Executor executor : this.executors) {
                executor.stop();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void initInternal() throws LifecycleException {
        super.initInternal();
        if (this.engine != null) {
            this.engine.init();
        }
        for (Executor executor : this.findExecutors()) {
            if (executor instanceof JmxEnabled) {
                ((JmxEnabled)((Object)executor)).setDomain(this.getDomain());
            }
            executor.init();
        }
        this.mapperListener.init();
        Object object = this.connectorsLock;
        synchronized (object) {
            for (Connector connector : this.connectors) {
                connector.init();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void destroyInternal() throws LifecycleException {
        this.mapperListener.destroy();
        Executor[] executorArray = this.connectorsLock;
        synchronized (this.connectorsLock) {
            Connector[] connectorArray = this.connectors;
            int n = connectorArray.length;
            for (int i = 0; i < n; ++i) {
                Connector connector = connectorArray[i];
                connector.destroy();
            }
            // ** MonitorExit[var1_1] (shouldn't be in output)
            for (Executor executor : this.findExecutors()) {
                executor.destroy();
            }
            if (this.engine != null) {
                this.engine.destroy();
            }
            super.destroyInternal();
            return;
        }
    }

    @Override
    public ClassLoader getParentClassLoader() {
        if (this.parentClassLoader != null) {
            return this.parentClassLoader;
        }
        if (this.server != null) {
            return this.server.getParentClassLoader();
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
        String domain = null;
        Engine engine = this.getContainer();
        if (engine != null) {
            domain = engine.getName();
        }
        if (domain == null) {
            domain = this.getName();
        }
        return domain;
    }

    @Override
    public final String getObjectNameKeyProperties() {
        return "type=Service";
    }
}

