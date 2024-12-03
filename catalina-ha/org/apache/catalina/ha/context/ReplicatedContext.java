/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  org.apache.catalina.LifecycleException
 *  org.apache.catalina.Loader
 *  org.apache.catalina.core.ApplicationContext
 *  org.apache.catalina.core.StandardContext
 *  org.apache.catalina.tribes.tipis.AbstractReplicatedMap$MapOwner
 *  org.apache.catalina.tribes.tipis.ReplicatedMap
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.ha.context;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.ServletContext;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Loader;
import org.apache.catalina.core.ApplicationContext;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.ha.CatalinaCluster;
import org.apache.catalina.tribes.tipis.AbstractReplicatedMap;
import org.apache.catalina.tribes.tipis.ReplicatedMap;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

public class ReplicatedContext
extends StandardContext
implements AbstractReplicatedMap.MapOwner {
    private int mapSendOptions = 2;
    private static final Log log = LogFactory.getLog(ReplicatedContext.class);
    protected static final long DEFAULT_REPL_TIMEOUT = 15000L;
    private static final StringManager sm = StringManager.getManager(ReplicatedContext.class);

    protected synchronized void startInternal() throws LifecycleException {
        super.startInternal();
        try {
            CatalinaCluster catclust = (CatalinaCluster)this.getCluster();
            if (catclust != null) {
                ReplicatedMap map = new ReplicatedMap((AbstractReplicatedMap.MapOwner)this, catclust.getChannel(), 15000L, this.getName(), this.getClassLoaders());
                map.setChannelSendOptions(this.mapSendOptions);
                ((ReplApplContext)this.context).setAttributeMap((Map<String, Object>)map);
            }
        }
        catch (Exception x) {
            log.error((Object)sm.getString("replicatedContext.startUnable", new Object[]{this.getName()}), (Throwable)x);
            throw new LifecycleException(sm.getString("replicatedContext.startFailed", new Object[]{this.getName()}), (Throwable)x);
        }
    }

    protected synchronized void stopInternal() throws LifecycleException {
        Map<String, Object> map = ((ReplApplContext)this.context).getAttributeMap();
        super.stopInternal();
        if (map instanceof ReplicatedMap) {
            ((ReplicatedMap)map).breakdown();
        }
    }

    public void setMapSendOptions(int mapSendOptions) {
        this.mapSendOptions = mapSendOptions;
    }

    public int getMapSendOptions() {
        return this.mapSendOptions;
    }

    public ClassLoader[] getClassLoaders() {
        Loader loader = null;
        ClassLoader classLoader = null;
        loader = this.getLoader();
        if (loader != null) {
            classLoader = loader.getClassLoader();
        }
        Thread currentThread = Thread.currentThread();
        if (classLoader == null) {
            classLoader = currentThread.getContextClassLoader();
        }
        if (classLoader == currentThread.getContextClassLoader()) {
            return new ClassLoader[]{classLoader};
        }
        return new ClassLoader[]{classLoader, currentThread.getContextClassLoader()};
    }

    public ServletContext getServletContext() {
        if (this.context == null) {
            this.context = new ReplApplContext(this);
            if (this.getAltDDName() != null) {
                this.context.setAttribute("org.apache.catalina.deploy.alt_dd", (Object)this.getAltDDName());
            }
        }
        return ((ReplApplContext)this.context).getFacade();
    }

    public void objectMadePrimary(Object key, Object value) {
    }

    protected static class ReplApplContext
    extends ApplicationContext {
        protected final Map<String, Object> tomcatAttributes = new ConcurrentHashMap<String, Object>();

        public ReplApplContext(ReplicatedContext context) {
            super((StandardContext)context);
        }

        protected ReplicatedContext getParent() {
            return (ReplicatedContext)this.getContext();
        }

        protected ServletContext getFacade() {
            return super.getFacade();
        }

        public Map<String, Object> getAttributeMap() {
            return this.attributes;
        }

        public void setAttributeMap(Map<String, Object> map) {
            this.attributes = map;
        }

        public void removeAttribute(String name) {
            this.tomcatAttributes.remove(name);
            super.removeAttribute(name);
        }

        public void setAttribute(String name, Object value) {
            if (name == null) {
                throw new IllegalArgumentException(sm.getString("applicationContext.setAttribute.namenull"));
            }
            if (value == null) {
                this.removeAttribute(name);
                return;
            }
            if (!this.getParent().getState().isAvailable() || "org.apache.jasper.runtime.JspApplicationContextImpl".equals(name)) {
                this.tomcatAttributes.put(name, value);
            } else {
                super.setAttribute(name, value);
            }
        }

        public Object getAttribute(String name) {
            Object obj = this.tomcatAttributes.get(name);
            if (obj == null) {
                return super.getAttribute(name);
            }
            return obj;
        }

        public Enumeration<String> getAttributeNames() {
            HashSet names = new HashSet(this.attributes.keySet());
            return new MultiEnumeration<String>(new Enumeration[]{super.getAttributeNames(), Collections.enumeration(names)});
        }
    }

    protected static class MultiEnumeration<T>
    implements Enumeration<T> {
        private final Enumeration<T>[] enumerations;

        public MultiEnumeration(Enumeration<T>[] enumerations) {
            this.enumerations = enumerations;
        }

        @Override
        public boolean hasMoreElements() {
            for (Enumeration<T> enumeration : this.enumerations) {
                if (!enumeration.hasMoreElements()) continue;
                return true;
            }
            return false;
        }

        @Override
        public T nextElement() {
            for (Enumeration<T> enumeration : this.enumerations) {
                if (!enumeration.hasMoreElements()) continue;
                return enumeration.nextElement();
            }
            return null;
        }
    }
}

