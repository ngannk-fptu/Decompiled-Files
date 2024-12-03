/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.ExceptionUtils
 *  org.apache.tomcat.util.descriptor.web.ContextEjb
 *  org.apache.tomcat.util.descriptor.web.ContextEnvironment
 *  org.apache.tomcat.util.descriptor.web.ContextLocalEjb
 *  org.apache.tomcat.util.descriptor.web.ContextResource
 *  org.apache.tomcat.util.descriptor.web.ContextResourceEnvRef
 *  org.apache.tomcat.util.descriptor.web.ContextResourceLink
 *  org.apache.tomcat.util.descriptor.web.ContextService
 *  org.apache.tomcat.util.descriptor.web.ContextTransaction
 *  org.apache.tomcat.util.descriptor.web.InjectionTarget
 *  org.apache.tomcat.util.descriptor.web.MessageDestinationRef
 *  org.apache.tomcat.util.descriptor.web.NamingResources
 *  org.apache.tomcat.util.descriptor.web.ResourceBase
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.deploy;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.naming.NamingException;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.JmxEnabled;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Server;
import org.apache.catalina.mbeans.MBeanUtils;
import org.apache.catalina.util.Introspection;
import org.apache.catalina.util.LifecycleMBeanBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.naming.ContextBindings;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.descriptor.web.ContextEjb;
import org.apache.tomcat.util.descriptor.web.ContextEnvironment;
import org.apache.tomcat.util.descriptor.web.ContextLocalEjb;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.apache.tomcat.util.descriptor.web.ContextResourceEnvRef;
import org.apache.tomcat.util.descriptor.web.ContextResourceLink;
import org.apache.tomcat.util.descriptor.web.ContextService;
import org.apache.tomcat.util.descriptor.web.ContextTransaction;
import org.apache.tomcat.util.descriptor.web.InjectionTarget;
import org.apache.tomcat.util.descriptor.web.MessageDestinationRef;
import org.apache.tomcat.util.descriptor.web.NamingResources;
import org.apache.tomcat.util.descriptor.web.ResourceBase;
import org.apache.tomcat.util.res.StringManager;

public class NamingResourcesImpl
extends LifecycleMBeanBase
implements Serializable,
NamingResources {
    private static final long serialVersionUID = 1L;
    private static final Log log = LogFactory.getLog(NamingResourcesImpl.class);
    private static final StringManager sm = StringManager.getManager(NamingResourcesImpl.class);
    private volatile boolean resourceRequireExplicitRegistration = false;
    private Object container = null;
    private final Set<String> entries = new HashSet<String>();
    private final Map<String, ContextEjb> ejbs = new HashMap<String, ContextEjb>();
    private final Map<String, ContextEnvironment> envs = new HashMap<String, ContextEnvironment>();
    private final Map<String, ContextLocalEjb> localEjbs = new HashMap<String, ContextLocalEjb>();
    private final Map<String, MessageDestinationRef> mdrs = new HashMap<String, MessageDestinationRef>();
    private final HashMap<String, ContextResourceEnvRef> resourceEnvRefs = new HashMap();
    private final HashMap<String, ContextResource> resources = new HashMap();
    private final HashMap<String, ContextResourceLink> resourceLinks = new HashMap();
    private final HashMap<String, ContextService> services = new HashMap();
    private ContextTransaction transaction = null;
    protected final PropertyChangeSupport support = new PropertyChangeSupport(this);

    public Object getContainer() {
        return this.container;
    }

    public void setContainer(Object container) {
        this.container = container;
    }

    public void setTransaction(ContextTransaction transaction) {
        this.transaction = transaction;
    }

    public ContextTransaction getTransaction() {
        return this.transaction;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addEjb(ContextEjb ejb) {
        String ejbLink = ejb.getLink();
        String lookupName = ejb.getLookupName();
        if (ejbLink != null && ejbLink.length() > 0 && lookupName != null && lookupName.length() > 0) {
            throw new IllegalArgumentException(sm.getString("namingResources.ejbLookupLink", new Object[]{ejb.getName()}));
        }
        if (this.entries.contains(ejb.getName())) {
            return;
        }
        this.entries.add(ejb.getName());
        Map<String, ContextEjb> map = this.ejbs;
        synchronized (map) {
            ejb.setNamingResources((NamingResources)this);
            this.ejbs.put(ejb.getName(), ejb);
        }
        this.support.firePropertyChange("ejb", null, ejb);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public void addEnvironment(ContextEnvironment environment) {
        if (this.entries.contains(environment.getName())) {
            ContextEnvironment ce = this.findEnvironment(environment.getName());
            ContextResourceLink rl = this.findResourceLink(environment.getName());
            if (ce != null) {
                if (!ce.getOverride()) return;
                this.removeEnvironment(environment.getName());
            } else {
                if (rl == null) return;
                NamingResourcesImpl global = this.getServer().getGlobalNamingResources();
                if (global.findEnvironment(rl.getGlobal()) != null) {
                    if (!global.findEnvironment(rl.getGlobal()).getOverride()) return;
                    this.removeResourceLink(environment.getName());
                }
            }
        }
        List injectionTargets = environment.getInjectionTargets();
        String value = environment.getValue();
        String lookupName = environment.getLookupName();
        if (injectionTargets != null && injectionTargets.size() > 0 && (value == null || value.length() == 0)) {
            return;
        }
        if (value != null && value.length() > 0 && lookupName != null && lookupName.length() > 0) {
            throw new IllegalArgumentException(sm.getString("namingResources.envEntryLookupValue", new Object[]{environment.getName()}));
        }
        if (!this.checkResourceType((ResourceBase)environment)) {
            throw new IllegalArgumentException(sm.getString("namingResources.resourceTypeFail", new Object[]{environment.getName(), environment.getType()}));
        }
        this.entries.add(environment.getName());
        Map<String, ContextEnvironment> map = this.envs;
        synchronized (map) {
            environment.setNamingResources((NamingResources)this);
            this.envs.put(environment.getName(), environment);
        }
        this.support.firePropertyChange("environment", null, environment);
        if (!this.resourceRequireExplicitRegistration) return;
        try {
            MBeanUtils.createMBean(environment);
            return;
        }
        catch (Exception e) {
            log.warn((Object)sm.getString("namingResources.mbeanCreateFail", new Object[]{environment.getName()}), (Throwable)e);
        }
    }

    private Server getServer() {
        if (this.container instanceof Server) {
            return (Server)this.container;
        }
        if (this.container instanceof Context) {
            Engine engine = (Engine)((Context)this.container).getParent().getParent();
            return engine.getService().getServer();
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addLocalEjb(ContextLocalEjb ejb) {
        if (this.entries.contains(ejb.getName())) {
            return;
        }
        this.entries.add(ejb.getName());
        Map<String, ContextLocalEjb> map = this.localEjbs;
        synchronized (map) {
            ejb.setNamingResources((NamingResources)this);
            this.localEjbs.put(ejb.getName(), ejb);
        }
        this.support.firePropertyChange("localEjb", null, ejb);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addMessageDestinationRef(MessageDestinationRef mdr) {
        if (this.entries.contains(mdr.getName())) {
            return;
        }
        if (!this.checkResourceType((ResourceBase)mdr)) {
            throw new IllegalArgumentException(sm.getString("namingResources.resourceTypeFail", new Object[]{mdr.getName(), mdr.getType()}));
        }
        this.entries.add(mdr.getName());
        Map<String, MessageDestinationRef> map = this.mdrs;
        synchronized (map) {
            mdr.setNamingResources((NamingResources)this);
            this.mdrs.put(mdr.getName(), mdr);
        }
        this.support.firePropertyChange("messageDestinationRef", null, mdr);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.support.addPropertyChangeListener(listener);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addResource(ContextResource resource) {
        if (this.entries.contains(resource.getName())) {
            return;
        }
        if (!this.checkResourceType((ResourceBase)resource)) {
            throw new IllegalArgumentException(sm.getString("namingResources.resourceTypeFail", new Object[]{resource.getName(), resource.getType()}));
        }
        this.entries.add(resource.getName());
        HashMap<String, ContextResource> hashMap = this.resources;
        synchronized (hashMap) {
            resource.setNamingResources((NamingResources)this);
            this.resources.put(resource.getName(), resource);
        }
        this.support.firePropertyChange("resource", null, resource);
        if (this.resourceRequireExplicitRegistration) {
            try {
                MBeanUtils.createMBean(resource);
            }
            catch (Exception e) {
                log.warn((Object)sm.getString("namingResources.mbeanCreateFail", new Object[]{resource.getName()}), (Throwable)e);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addResourceEnvRef(ContextResourceEnvRef resource) {
        if (this.entries.contains(resource.getName())) {
            return;
        }
        if (!this.checkResourceType((ResourceBase)resource)) {
            throw new IllegalArgumentException(sm.getString("namingResources.resourceTypeFail", new Object[]{resource.getName(), resource.getType()}));
        }
        this.entries.add(resource.getName());
        HashMap<String, ContextResourceEnvRef> hashMap = this.resourceEnvRefs;
        synchronized (hashMap) {
            resource.setNamingResources((NamingResources)this);
            this.resourceEnvRefs.put(resource.getName(), resource);
        }
        this.support.firePropertyChange("resourceEnvRef", null, resource);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addResourceLink(ContextResourceLink resourceLink) {
        if (this.entries.contains(resourceLink.getName())) {
            return;
        }
        this.entries.add(resourceLink.getName());
        HashMap<String, ContextResourceLink> hashMap = this.resourceLinks;
        synchronized (hashMap) {
            resourceLink.setNamingResources((NamingResources)this);
            this.resourceLinks.put(resourceLink.getName(), resourceLink);
        }
        this.support.firePropertyChange("resourceLink", null, resourceLink);
        if (this.resourceRequireExplicitRegistration) {
            try {
                MBeanUtils.createMBean(resourceLink);
            }
            catch (Exception e) {
                log.warn((Object)sm.getString("namingResources.mbeanCreateFail", new Object[]{resourceLink.getName()}), (Throwable)e);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addService(ContextService service) {
        if (this.entries.contains(service.getName())) {
            return;
        }
        this.entries.add(service.getName());
        HashMap<String, ContextService> hashMap = this.services;
        synchronized (hashMap) {
            service.setNamingResources((NamingResources)this);
            this.services.put(service.getName(), service);
        }
        this.support.firePropertyChange("service", null, service);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ContextEjb findEjb(String name) {
        Map<String, ContextEjb> map = this.ejbs;
        synchronized (map) {
            return this.ejbs.get(name);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ContextEjb[] findEjbs() {
        Map<String, ContextEjb> map = this.ejbs;
        synchronized (map) {
            return this.ejbs.values().toArray(new ContextEjb[0]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ContextEnvironment findEnvironment(String name) {
        Map<String, ContextEnvironment> map = this.envs;
        synchronized (map) {
            return this.envs.get(name);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ContextEnvironment[] findEnvironments() {
        Map<String, ContextEnvironment> map = this.envs;
        synchronized (map) {
            return this.envs.values().toArray(new ContextEnvironment[0]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ContextLocalEjb findLocalEjb(String name) {
        Map<String, ContextLocalEjb> map = this.localEjbs;
        synchronized (map) {
            return this.localEjbs.get(name);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ContextLocalEjb[] findLocalEjbs() {
        Map<String, ContextLocalEjb> map = this.localEjbs;
        synchronized (map) {
            return this.localEjbs.values().toArray(new ContextLocalEjb[0]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public MessageDestinationRef findMessageDestinationRef(String name) {
        Map<String, MessageDestinationRef> map = this.mdrs;
        synchronized (map) {
            return this.mdrs.get(name);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public MessageDestinationRef[] findMessageDestinationRefs() {
        Map<String, MessageDestinationRef> map = this.mdrs;
        synchronized (map) {
            return this.mdrs.values().toArray(new MessageDestinationRef[0]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ContextResource findResource(String name) {
        HashMap<String, ContextResource> hashMap = this.resources;
        synchronized (hashMap) {
            return this.resources.get(name);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ContextResourceLink findResourceLink(String name) {
        HashMap<String, ContextResourceLink> hashMap = this.resourceLinks;
        synchronized (hashMap) {
            return this.resourceLinks.get(name);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ContextResourceLink[] findResourceLinks() {
        HashMap<String, ContextResourceLink> hashMap = this.resourceLinks;
        synchronized (hashMap) {
            return this.resourceLinks.values().toArray(new ContextResourceLink[0]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ContextResource[] findResources() {
        HashMap<String, ContextResource> hashMap = this.resources;
        synchronized (hashMap) {
            return this.resources.values().toArray(new ContextResource[0]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ContextResourceEnvRef findResourceEnvRef(String name) {
        HashMap<String, ContextResourceEnvRef> hashMap = this.resourceEnvRefs;
        synchronized (hashMap) {
            return this.resourceEnvRefs.get(name);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ContextResourceEnvRef[] findResourceEnvRefs() {
        HashMap<String, ContextResourceEnvRef> hashMap = this.resourceEnvRefs;
        synchronized (hashMap) {
            return this.resourceEnvRefs.values().toArray(new ContextResourceEnvRef[0]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ContextService findService(String name) {
        HashMap<String, ContextService> hashMap = this.services;
        synchronized (hashMap) {
            return this.services.get(name);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ContextService[] findServices() {
        HashMap<String, ContextService> hashMap = this.services;
        synchronized (hashMap) {
            return this.services.values().toArray(new ContextService[0]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeEjb(String name) {
        this.entries.remove(name);
        ContextEjb ejb = null;
        Map<String, ContextEjb> map = this.ejbs;
        synchronized (map) {
            ejb = this.ejbs.remove(name);
        }
        if (ejb != null) {
            this.support.firePropertyChange("ejb", ejb, null);
            ejb.setNamingResources(null);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeEnvironment(String name) {
        this.entries.remove(name);
        ContextEnvironment environment = null;
        Map<String, ContextEnvironment> map = this.envs;
        synchronized (map) {
            environment = this.envs.remove(name);
        }
        if (environment != null) {
            this.support.firePropertyChange("environment", environment, null);
            if (this.resourceRequireExplicitRegistration) {
                try {
                    MBeanUtils.destroyMBean(environment);
                }
                catch (Exception e) {
                    log.warn((Object)sm.getString("namingResources.mbeanDestroyFail", new Object[]{environment.getName()}), (Throwable)e);
                }
            }
            environment.setNamingResources(null);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeLocalEjb(String name) {
        this.entries.remove(name);
        ContextLocalEjb localEjb = null;
        Map<String, ContextLocalEjb> map = this.localEjbs;
        synchronized (map) {
            localEjb = this.localEjbs.remove(name);
        }
        if (localEjb != null) {
            this.support.firePropertyChange("localEjb", localEjb, null);
            localEjb.setNamingResources(null);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeMessageDestinationRef(String name) {
        this.entries.remove(name);
        MessageDestinationRef mdr = null;
        Map<String, MessageDestinationRef> map = this.mdrs;
        synchronized (map) {
            mdr = this.mdrs.remove(name);
        }
        if (mdr != null) {
            this.support.firePropertyChange("messageDestinationRef", mdr, null);
            mdr.setNamingResources(null);
        }
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.support.removePropertyChangeListener(listener);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeResource(String name) {
        this.entries.remove(name);
        ContextResource resource = null;
        HashMap<String, ContextResource> hashMap = this.resources;
        synchronized (hashMap) {
            resource = this.resources.remove(name);
        }
        if (resource != null) {
            this.support.firePropertyChange("resource", resource, null);
            if (this.resourceRequireExplicitRegistration) {
                try {
                    MBeanUtils.destroyMBean(resource);
                }
                catch (Exception e) {
                    log.warn((Object)sm.getString("namingResources.mbeanDestroyFail", new Object[]{resource.getName()}), (Throwable)e);
                }
            }
            resource.setNamingResources(null);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeResourceEnvRef(String name) {
        this.entries.remove(name);
        ContextResourceEnvRef resourceEnvRef = null;
        HashMap<String, ContextResourceEnvRef> hashMap = this.resourceEnvRefs;
        synchronized (hashMap) {
            resourceEnvRef = this.resourceEnvRefs.remove(name);
        }
        if (resourceEnvRef != null) {
            this.support.firePropertyChange("resourceEnvRef", resourceEnvRef, null);
            resourceEnvRef.setNamingResources(null);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeResourceLink(String name) {
        this.entries.remove(name);
        ContextResourceLink resourceLink = null;
        HashMap<String, ContextResourceLink> hashMap = this.resourceLinks;
        synchronized (hashMap) {
            resourceLink = this.resourceLinks.remove(name);
        }
        if (resourceLink != null) {
            this.support.firePropertyChange("resourceLink", resourceLink, null);
            if (this.resourceRequireExplicitRegistration) {
                try {
                    MBeanUtils.destroyMBean(resourceLink);
                }
                catch (Exception e) {
                    log.warn((Object)sm.getString("namingResources.mbeanDestroyFail", new Object[]{resourceLink.getName()}), (Throwable)e);
                }
            }
            resourceLink.setNamingResources(null);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeService(String name) {
        this.entries.remove(name);
        ContextService service = null;
        HashMap<String, ContextService> hashMap = this.services;
        synchronized (hashMap) {
            service = this.services.remove(name);
        }
        if (service != null) {
            this.support.firePropertyChange("service", service, null);
            service.setNamingResources(null);
        }
    }

    @Override
    protected void initInternal() throws LifecycleException {
        super.initInternal();
        this.resourceRequireExplicitRegistration = true;
        for (ContextResource cr : this.resources.values()) {
            try {
                MBeanUtils.createMBean(cr);
            }
            catch (Exception e) {
                log.warn((Object)sm.getString("namingResources.mbeanCreateFail", new Object[]{cr.getName()}), (Throwable)e);
            }
        }
        for (ContextEnvironment ce : this.envs.values()) {
            try {
                MBeanUtils.createMBean(ce);
            }
            catch (Exception e) {
                log.warn((Object)sm.getString("namingResources.mbeanCreateFail", new Object[]{ce.getName()}), (Throwable)e);
            }
        }
        for (ContextResourceLink crl : this.resourceLinks.values()) {
            try {
                MBeanUtils.createMBean(crl);
            }
            catch (Exception e) {
                log.warn((Object)sm.getString("namingResources.mbeanCreateFail", new Object[]{crl.getName()}), (Throwable)e);
            }
        }
    }

    @Override
    protected void startInternal() throws LifecycleException {
        this.fireLifecycleEvent("configure_start", null);
        this.setState(LifecycleState.STARTING);
    }

    @Override
    protected void stopInternal() throws LifecycleException {
        this.cleanUp();
        this.setState(LifecycleState.STOPPING);
        this.fireLifecycleEvent("configure_stop", null);
    }

    private void cleanUp() {
        javax.naming.Context ctxt;
        if (this.resources.size() == 0) {
            return;
        }
        try {
            if (this.container instanceof Server) {
                ctxt = ((Server)this.container).getGlobalNamingContext();
            } else {
                ctxt = ContextBindings.getClassLoader();
                ctxt = (javax.naming.Context)ctxt.lookup("comp/env");
            }
        }
        catch (NamingException e) {
            log.warn((Object)sm.getString("namingResources.cleanupNoContext", new Object[]{this.container}), (Throwable)e);
            return;
        }
        for (ContextResource cr : this.resources.values()) {
            Object resource;
            String closeMethod;
            if (!cr.getSingleton() || (closeMethod = cr.getCloseMethod()) == null || closeMethod.length() <= 0) continue;
            String name = cr.getName();
            try {
                resource = ctxt.lookup(name);
            }
            catch (NamingException e) {
                log.warn((Object)sm.getString("namingResources.cleanupNoResource", new Object[]{cr.getName(), this.container}), (Throwable)e);
                continue;
            }
            this.cleanUp(resource, name, closeMethod);
        }
    }

    private void cleanUp(Object resource, String name, String closeMethod) {
        Method m = null;
        try {
            m = resource.getClass().getMethod(closeMethod, null);
        }
        catch (SecurityException e) {
            log.debug((Object)sm.getString("namingResources.cleanupCloseSecurity", new Object[]{closeMethod, name, this.container}));
            return;
        }
        catch (NoSuchMethodException e) {
            log.debug((Object)sm.getString("namingResources.cleanupNoClose", new Object[]{name, this.container, closeMethod}));
            return;
        }
        try {
            m.invoke(resource, (Object[])null);
        }
        catch (IllegalAccessException | IllegalArgumentException e) {
            log.warn((Object)sm.getString("namingResources.cleanupCloseFailed", new Object[]{closeMethod, name, this.container}), (Throwable)e);
        }
        catch (InvocationTargetException e) {
            Throwable t = ExceptionUtils.unwrapInvocationTargetException((Throwable)e);
            ExceptionUtils.handleThrowable((Throwable)t);
            log.warn((Object)sm.getString("namingResources.cleanupCloseFailed", new Object[]{closeMethod, name, this.container}), t);
        }
    }

    @Override
    protected void destroyInternal() throws LifecycleException {
        this.resourceRequireExplicitRegistration = false;
        for (ContextResourceLink crl : this.resourceLinks.values()) {
            try {
                MBeanUtils.destroyMBean(crl);
            }
            catch (Exception e) {
                log.warn((Object)sm.getString("namingResources.mbeanDestroyFail", new Object[]{crl.getName()}), (Throwable)e);
            }
        }
        for (ContextEnvironment ce : this.envs.values()) {
            try {
                MBeanUtils.destroyMBean(ce);
            }
            catch (Exception e) {
                log.warn((Object)sm.getString("namingResources.mbeanDestroyFail", new Object[]{ce.getName()}), (Throwable)e);
            }
        }
        for (ContextResource cr : this.resources.values()) {
            try {
                MBeanUtils.destroyMBean(cr);
            }
            catch (Exception e) {
                log.warn((Object)sm.getString("namingResources.mbeanDestroyFail", new Object[]{cr.getName()}), (Throwable)e);
            }
        }
        super.destroyInternal();
    }

    @Override
    protected String getDomainInternal() {
        Object c = this.getContainer();
        if (c instanceof JmxEnabled) {
            return ((JmxEnabled)c).getDomain();
        }
        return null;
    }

    @Override
    protected String getObjectNameKeyProperties() {
        Object c = this.getContainer();
        if (c instanceof Container) {
            return "type=NamingResources" + ((Container)c).getMBeanKeyProperties();
        }
        return "type=NamingResources";
    }

    private boolean checkResourceType(ResourceBase resource) {
        if (!(this.container instanceof Context)) {
            return true;
        }
        if (resource.getInjectionTargets() == null || resource.getInjectionTargets().size() == 0) {
            return true;
        }
        Context context = (Context)this.container;
        String typeName = resource.getType();
        Class<?> typeClass = null;
        if (typeName != null && (typeClass = Introspection.loadClass(context, typeName)) == null) {
            return true;
        }
        Class<?> compatibleClass = this.getCompatibleType(context, resource, typeClass);
        if (compatibleClass == null) {
            return false;
        }
        resource.setType(compatibleClass.getCanonicalName());
        return true;
    }

    private Class<?> getCompatibleType(Context context, ResourceBase resource, Class<?> typeClass) {
        Class<?> result = null;
        for (InjectionTarget injectionTarget : resource.getInjectionTargets()) {
            Class<?> clazz = Introspection.loadClass(context, injectionTarget.getTargetClass());
            if (clazz == null) continue;
            String targetName = injectionTarget.getTargetName();
            Class<?> targetType = this.getSetterType(clazz, targetName);
            if (targetType == null) {
                targetType = this.getFieldType(clazz, targetName);
            }
            if (targetType == null) continue;
            targetType = Introspection.convertPrimitiveType(targetType);
            if (typeClass == null) {
                if (result == null) {
                    result = targetType;
                    continue;
                }
                if (targetType.isAssignableFrom(result)) continue;
                if (result.isAssignableFrom(targetType)) {
                    result = targetType;
                    continue;
                }
                return null;
            }
            if (targetType.isAssignableFrom(typeClass)) {
                result = typeClass;
                continue;
            }
            return null;
        }
        return result;
    }

    private Class<?> getSetterType(Class<?> clazz, String name) {
        Method[] methods = Introspection.getDeclaredMethods(clazz);
        if (methods != null && methods.length > 0) {
            for (Method method : methods) {
                if (!Introspection.isValidSetter(method) || !Introspection.getPropertyName(method).equals(name)) continue;
                return method.getParameterTypes()[0];
            }
        }
        return null;
    }

    private Class<?> getFieldType(Class<?> clazz, String name) {
        Field[] fields = Introspection.getDeclaredFields(clazz);
        if (fields != null && fields.length > 0) {
            for (Field field : fields) {
                if (!field.getName().equals(name)) continue;
                return field.getType();
            }
        }
        return null;
    }
}

