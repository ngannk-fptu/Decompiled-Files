/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.core;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.catalina.AccessLog;
import org.apache.catalina.Container;
import org.apache.catalina.ContainerEvent;
import org.apache.catalina.ContainerListener;
import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Realm;
import org.apache.catalina.Server;
import org.apache.catalina.Service;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.core.ContainerBase;
import org.apache.catalina.core.StandardEngineValve;
import org.apache.catalina.realm.NullRealm;
import org.apache.catalina.util.ServerInfo;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class StandardEngine
extends ContainerBase
implements Engine {
    private static final Log log = LogFactory.getLog(StandardEngine.class);
    private String defaultHost = null;
    private Service service = null;
    private String jvmRouteId;
    private final AtomicReference<AccessLog> defaultAccessLog = new AtomicReference();

    public StandardEngine() {
        this.pipeline.setBasic(new StandardEngineValve());
        try {
            this.setJvmRoute(System.getProperty("jvmRoute"));
        }
        catch (Exception ex) {
            log.warn((Object)sm.getString("standardEngine.jvmRouteFail"));
        }
        this.backgroundProcessorDelay = 10;
    }

    @Override
    public Realm getRealm() {
        Realm configured = super.getRealm();
        if (configured == null) {
            configured = new NullRealm();
            this.setRealm(configured);
        }
        return configured;
    }

    @Override
    public String getDefaultHost() {
        return this.defaultHost;
    }

    @Override
    public void setDefaultHost(String host) {
        String oldDefaultHost = this.defaultHost;
        this.defaultHost = host == null ? null : host.toLowerCase(Locale.ENGLISH);
        if (this.getState().isAvailable()) {
            this.service.getMapper().setDefaultHostName(host);
        }
        this.support.firePropertyChange("defaultHost", oldDefaultHost, this.defaultHost);
    }

    @Override
    public void setJvmRoute(String routeId) {
        this.jvmRouteId = routeId;
    }

    @Override
    public String getJvmRoute() {
        return this.jvmRouteId;
    }

    @Override
    public Service getService() {
        return this.service;
    }

    @Override
    public void setService(Service service) {
        this.service = service;
    }

    @Override
    public void addChild(Container child) {
        if (!(child instanceof Host)) {
            throw new IllegalArgumentException(sm.getString("standardEngine.notHost"));
        }
        super.addChild(child);
    }

    @Override
    public void setParent(Container container) {
        throw new IllegalArgumentException(sm.getString("standardEngine.notParent"));
    }

    @Override
    protected void initInternal() throws LifecycleException {
        this.getRealm();
        super.initInternal();
    }

    @Override
    protected synchronized void startInternal() throws LifecycleException {
        if (log.isInfoEnabled()) {
            log.info((Object)sm.getString("standardEngine.start", new Object[]{ServerInfo.getServerInfo()}));
        }
        super.startInternal();
    }

    @Override
    public void logAccess(Request request, Response response, long time, boolean useDefault) {
        boolean logged = false;
        if (this.getAccessLog() != null) {
            this.accessLog.log(request, response, time);
            logged = true;
        }
        if (!logged && useDefault) {
            AccessLog newDefaultAccessLog = this.defaultAccessLog.get();
            if (newDefaultAccessLog == null) {
                AccessLogListener l;
                Host host = (Host)this.findChild(this.getDefaultHost());
                Context context = null;
                if (host != null && host.getState().isAvailable()) {
                    newDefaultAccessLog = host.getAccessLog();
                    if (newDefaultAccessLog != null) {
                        if (this.defaultAccessLog.compareAndSet(null, newDefaultAccessLog)) {
                            l = new AccessLogListener(this, host, null);
                            l.install();
                        }
                    } else {
                        context = (Context)host.findChild("");
                        if (context != null && context.getState().isAvailable() && (newDefaultAccessLog = context.getAccessLog()) != null && this.defaultAccessLog.compareAndSet(null, newDefaultAccessLog)) {
                            l = new AccessLogListener(this, null, context);
                            l.install();
                        }
                    }
                }
                if (newDefaultAccessLog == null && this.defaultAccessLog.compareAndSet(null, newDefaultAccessLog = new NoopAccessLog())) {
                    l = new AccessLogListener(this, host, context);
                    l.install();
                }
            }
            newDefaultAccessLog.log(request, response, time);
        }
    }

    @Override
    public ClassLoader getParentClassLoader() {
        if (this.parentClassLoader != null) {
            return this.parentClassLoader;
        }
        if (this.service != null) {
            return this.service.getParentClassLoader();
        }
        return ClassLoader.getSystemClassLoader();
    }

    @Override
    public File getCatalinaBase() {
        File base;
        Server s;
        if (this.service != null && (s = this.service.getServer()) != null && (base = s.getCatalinaBase()) != null) {
            return base;
        }
        return super.getCatalinaBase();
    }

    @Override
    public File getCatalinaHome() {
        File base;
        Server s;
        if (this.service != null && (s = this.service.getServer()) != null && (base = s.getCatalinaHome()) != null) {
            return base;
        }
        return super.getCatalinaHome();
    }

    @Override
    protected String getObjectNameKeyProperties() {
        return "type=Engine";
    }

    @Override
    protected String getDomainInternal() {
        return this.getName();
    }

    protected static final class AccessLogListener
    implements PropertyChangeListener,
    LifecycleListener,
    ContainerListener {
        private final StandardEngine engine;
        private final Host host;
        private final Context context;
        private volatile boolean disabled = false;

        public AccessLogListener(StandardEngine engine, Host host, Context context) {
            this.engine = engine;
            this.host = host;
            this.context = context;
        }

        public void install() {
            this.engine.addPropertyChangeListener(this);
            if (this.host != null) {
                this.host.addContainerListener(this);
                this.host.addLifecycleListener(this);
            }
            if (this.context != null) {
                this.context.addLifecycleListener(this);
            }
        }

        private void uninstall() {
            this.disabled = true;
            if (this.context != null) {
                this.context.removeLifecycleListener(this);
            }
            if (this.host != null) {
                this.host.removeLifecycleListener(this);
                this.host.removeContainerListener(this);
            }
            this.engine.removePropertyChangeListener(this);
        }

        @Override
        public void lifecycleEvent(LifecycleEvent event) {
            if (this.disabled) {
                return;
            }
            String type = event.getType();
            if ("after_start".equals(type) || "before_stop".equals(type) || "before_destroy".equals(type)) {
                this.engine.defaultAccessLog.set(null);
                this.uninstall();
            }
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (this.disabled) {
                return;
            }
            if ("defaultHost".equals(evt.getPropertyName())) {
                this.engine.defaultAccessLog.set(null);
                this.uninstall();
            }
        }

        @Override
        public void containerEvent(ContainerEvent event) {
            Context context;
            if (this.disabled) {
                return;
            }
            if ("addChild".equals(event.getType()) && (context = (Context)event.getData()).getPath().isEmpty()) {
                this.engine.defaultAccessLog.set(null);
                this.uninstall();
            }
        }
    }

    protected static final class NoopAccessLog
    implements AccessLog {
        protected NoopAccessLog() {
        }

        @Override
        public void log(Request request, Response response, long time) {
        }

        @Override
        public void setRequestAttributesEnabled(boolean requestAttributesEnabled) {
        }

        @Override
        public boolean getRequestAttributesEnabled() {
            return false;
        }
    }
}

