/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.felix.framework.BundleWiringImpl;
import org.apache.felix.framework.DTOFactory;
import org.apache.felix.framework.Felix;
import org.apache.felix.framework.ServiceRegistry;
import org.apache.felix.framework.util.MapToDictionary;
import org.apache.felix.framework.util.StringMap;
import org.apache.felix.framework.util.Util;
import org.apache.felix.framework.wiring.BundleCapabilityImpl;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleReference;
import org.osgi.framework.PrototypeServiceFactory;
import org.osgi.framework.ServiceException;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.dto.ServiceReferenceDTO;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleWire;

class ServiceRegistrationImpl
implements ServiceRegistration {
    private final ServiceRegistry m_registry;
    private final Bundle m_bundle;
    private final String[] m_classes;
    private final Long m_serviceId;
    private volatile Object m_svcObj;
    private volatile ServiceFactory m_factory;
    private volatile Map<String, Object> m_propMap = new StringMap();
    private final ServiceReferenceImpl m_ref;
    private volatile boolean m_isUnregistering = false;
    private final ThreadLocal<Boolean> m_threadLoopDetection = new ThreadLocal();
    private final Object syncObject = new Object();

    public ServiceRegistrationImpl(ServiceRegistry registry, Bundle bundle, String[] classes, Long serviceId, Object svcObj, Dictionary dict) {
        this.m_registry = registry;
        this.m_bundle = bundle;
        this.m_classes = classes;
        this.m_serviceId = serviceId;
        this.m_svcObj = svcObj;
        this.m_factory = this.m_svcObj instanceof ServiceFactory ? (ServiceFactory)this.m_svcObj : null;
        this.initializeProperties(dict);
        this.m_ref = new ServiceReferenceImpl();
    }

    protected boolean isValid() {
        return this.m_svcObj != null;
    }

    protected synchronized void invalidate() {
        this.m_svcObj = null;
    }

    public synchronized ServiceReference getReference() {
        if (!this.isValid()) {
            throw new IllegalStateException("The service registration is no longer valid.");
        }
        return this.m_ref;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setProperties(Dictionary dict) {
        Map<String, Object> oldProps;
        ServiceRegistrationImpl serviceRegistrationImpl = this;
        synchronized (serviceRegistrationImpl) {
            if (!this.isValid()) {
                throw new IllegalStateException("The service registration is no longer valid.");
            }
            oldProps = this.m_propMap;
            this.initializeProperties(dict);
        }
        this.m_registry.servicePropertiesModified(this, new MapToDictionary(oldProps));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unregister() {
        ServiceRegistrationImpl serviceRegistrationImpl = this;
        synchronized (serviceRegistrationImpl) {
            if (!this.isValid() || this.m_isUnregistering) {
                throw new IllegalStateException("Service already unregistered.");
            }
            this.m_isUnregistering = true;
        }
        this.m_registry.unregisterService(this.m_bundle, this);
        serviceRegistrationImpl = this;
        synchronized (serviceRegistrationImpl) {
            this.m_svcObj = null;
            this.m_factory = null;
        }
    }

    private boolean isClassAccessible(Class clazz) {
        if (this.m_factory != null && Felix.m_secureAction.getClassLoader(this.m_factory.getClass()) instanceof BundleReference && !((BundleReference)((Object)Felix.m_secureAction.getClassLoader(this.m_factory.getClass()))).getBundle().equals(this.m_bundle)) {
            try {
                Class<?> providedClazz = this.m_bundle.loadClass(clazz.getName());
                if (providedClazz != null) {
                    return providedClazz == clazz;
                }
            }
            catch (ClassNotFoundException providedClazz) {
                // empty catch block
            }
            return true;
        }
        Class<?> sourceClass = this.m_factory != null ? this.m_factory.getClass() : this.m_svcObj.getClass();
        return Util.loadClassUsingClass(sourceClass, clazz.getName(), Felix.m_secureAction) == clazz;
    }

    Object getProperty(String key) {
        return this.m_propMap.get(key);
    }

    private String[] getPropertyKeys() {
        Set<String> s = this.m_propMap.keySet();
        return s.toArray(new String[s.size()]);
    }

    private Bundle[] getUsingBundles() {
        return this.m_registry.getUsingBundles(this.m_ref);
    }

    Object getService() {
        return this.m_svcObj;
    }

    Object getService(Bundle acqBundle) {
        if (this.m_factory != null) {
            Object svcObj = null;
            try {
                svcObj = System.getSecurityManager() != null ? AccessController.doPrivileged(new ServiceFactoryPrivileged(acqBundle, null)) : this.getFactoryUnchecked(acqBundle);
            }
            catch (PrivilegedActionException ex) {
                if (ex.getException() instanceof ServiceException) {
                    throw (ServiceException)ex.getException();
                }
                throw new ServiceException("Service factory exception: " + ex.getException().getMessage(), 3, ex.getException());
            }
            return svcObj;
        }
        return this.m_svcObj;
    }

    void ungetService(Bundle relBundle, Object svcObj) {
        if (this.m_factory != null) {
            try {
                if (System.getSecurityManager() != null) {
                    AccessController.doPrivileged(new ServiceFactoryPrivileged(relBundle, svcObj));
                } else {
                    this.ungetFactoryUnchecked(relBundle, svcObj);
                }
            }
            catch (Throwable ex) {
                this.m_registry.getLogger().log(this.m_bundle, 1, "ServiceRegistrationImpl: Error ungetting service.", ex);
            }
        }
    }

    private void initializeProperties(Dictionary<String, Object> dict) {
        StringMap props = new StringMap();
        if (dict != null) {
            Enumeration<String> keys = dict.keys();
            while (keys.hasMoreElements()) {
                String key = keys.nextElement();
                if (props.get(key) == null) {
                    props.put(key, dict.get(key));
                    continue;
                }
                throw new IllegalArgumentException("Duplicate service property: " + key);
            }
        }
        props.put("objectClass", this.m_classes);
        props.put("service.id", this.m_serviceId);
        props.put("service.bundleid", this.m_bundle.getBundleId());
        if (this.m_factory != null) {
            props.put("service.scope", this.m_factory instanceof PrototypeServiceFactory ? "prototype" : "bundle");
        } else {
            props.put("service.scope", "singleton");
        }
        this.m_propMap = props;
    }

    private Object getFactoryUnchecked(Bundle bundle) {
        Object svcObj = null;
        try {
            svcObj = this.m_factory.getService(bundle, this);
        }
        catch (Throwable th) {
            throw new ServiceException("Service factory exception: " + th.getMessage(), 3, th);
        }
        if (svcObj != null) {
            for (int i = 0; i < this.m_classes.length; ++i) {
                Class clazz = Util.loadClassUsingClass(svcObj.getClass(), this.m_classes[i], Felix.m_secureAction);
                if (clazz != null && clazz.isAssignableFrom(svcObj.getClass())) continue;
                if (clazz == null) {
                    if (Util.checkImplementsWithName(svcObj.getClass(), this.m_classes[i])) continue;
                    throw new ServiceException("Service cannot be cast due to missing class: " + this.m_classes[i], 2);
                }
                throw new ServiceException("Service cannot be cast: " + this.m_classes[i], 2);
            }
        } else {
            throw new ServiceException("Service factory returned null. (" + this.m_factory + ")", 2);
        }
        return svcObj;
    }

    private void ungetFactoryUnchecked(Bundle bundle, Object svcObj) {
        this.m_factory.ungetService(bundle, this, svcObj);
    }

    boolean currentThreadMarked() {
        return this.m_threadLoopDetection.get() != null;
    }

    void markCurrentThread() {
        this.m_threadLoopDetection.set(Boolean.TRUE);
    }

    void unmarkCurrentThread() {
        this.m_threadLoopDetection.set(null);
    }

    private class ServiceReferenceMap
    implements Map {
        private ServiceReferenceMap() {
        }

        @Override
        public int size() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean isEmpty() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean containsKey(Object o) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean containsValue(Object o) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public Object get(Object o) {
            return ServiceRegistrationImpl.this.getProperty((String)o);
        }

        public Object put(Object k, Object v) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public Object remove(Object o) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void putAll(Map map) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public Set<Object> keySet() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public Collection<Object> values() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public Set<Map.Entry<Object, Object>> entrySet() {
            return Collections.EMPTY_SET;
        }
    }

    class ServiceReferenceImpl
    extends BundleCapabilityImpl
    implements ServiceReference {
        private final ServiceReferenceMap m_map;

        private ServiceReferenceImpl() {
            super(null, null, Collections.EMPTY_MAP, Collections.EMPTY_MAP);
            this.m_map = new ServiceReferenceMap();
        }

        ServiceRegistrationImpl getRegistration() {
            return ServiceRegistrationImpl.this;
        }

        @Override
        public BundleRevision getRevision() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String getNamespace() {
            return "service-reference";
        }

        @Override
        public Map<String, String> getDirectives() {
            return Collections.EMPTY_MAP;
        }

        @Override
        public Map<String, Object> getAttributes() {
            return this.m_map;
        }

        @Override
        public List<String> getUses() {
            return Collections.EMPTY_LIST;
        }

        @Override
        public Object getProperty(String s) {
            return ServiceRegistrationImpl.this.getProperty(s);
        }

        @Override
        public String[] getPropertyKeys() {
            return ServiceRegistrationImpl.this.getPropertyKeys();
        }

        @Override
        public Bundle getBundle() {
            return ServiceRegistrationImpl.this.isValid() ? ServiceRegistrationImpl.this.m_bundle : null;
        }

        @Override
        public Bundle[] getUsingBundles() {
            return ServiceRegistrationImpl.this.getUsingBundles();
        }

        @Override
        public String toString() {
            String[] ocs = (String[])this.getProperty("objectClass");
            String oc = "[";
            for (int i = 0; i < ocs.length; ++i) {
                oc = oc + ocs[i];
                if (i >= ocs.length - 1) continue;
                oc = oc + ", ";
            }
            oc = oc + "]";
            return oc;
        }

        @Override
        public boolean isAssignableTo(Bundle requester, String className) {
            if (requester == ServiceRegistrationImpl.this.m_bundle) {
                return true;
            }
            boolean allow = true;
            String pkgName = Util.getClassPackage(className);
            BundleRevision requesterRevision = requester.adapt(BundleRevision.class);
            BundleWire requesterWire = Util.getWire(requesterRevision, pkgName);
            BundleCapability requesterCap = Util.getPackageCapability(requesterRevision, pkgName);
            BundleRevision providerRevision = ServiceRegistrationImpl.this.m_bundle.adapt(BundleRevision.class);
            BundleWire providerWire = Util.getWire(providerRevision, pkgName);
            BundleCapability providerCap = Util.getPackageCapability(providerRevision, pkgName);
            if (requesterWire == null && providerWire == null) {
                try {
                    Class requestClass = ((BundleWiringImpl)requesterRevision.getWiring()).getClassByDelegation(className);
                    allow = this.getRegistration().isClassAccessible(requestClass);
                }
                catch (Exception ex) {
                    allow = true;
                }
            } else if (requesterWire == null && providerWire != null) {
                if (requesterCap != null) {
                    allow = providerWire.getProviderWiring().getRevision().equals(requesterRevision);
                } else {
                    try {
                        Class requestClass = ((BundleWiringImpl)requesterRevision.getWiring()).getClassByDelegation(className);
                        try {
                            allow = ((BundleWiringImpl)providerRevision.getWiring()).getClassByDelegation(className) == requestClass;
                        }
                        catch (Exception ex) {
                            allow = false;
                        }
                    }
                    catch (Exception ex) {
                        allow = true;
                    }
                }
            } else if (requesterWire != null && providerWire == null) {
                if (providerCap != null) {
                    allow = requesterWire.getProviderWiring().getRevision().equals(providerRevision);
                } else {
                    try {
                        Class requestClass = ((BundleWiringImpl)requesterRevision.getWiring()).getClassByDelegation(className);
                        allow = this.getRegistration().isClassAccessible(requestClass);
                    }
                    catch (Exception ex) {
                        allow = false;
                    }
                }
            } else {
                allow = providerWire.getProviderWiring().getRevision().equals(requesterWire.getProviderWiring().getRevision());
            }
            return allow;
        }

        @Override
        public int compareTo(Object reference) {
            Integer otherRank;
            Long otherId;
            ServiceReference other = (ServiceReference)reference;
            Long id = (Long)this.getProperty("service.id");
            if (id.equals(otherId = (Long)other.getProperty("service.id"))) {
                return 0;
            }
            Object rankObj = this.getProperty("service.ranking");
            Object otherRankObj = other.getProperty("service.ranking");
            rankObj = rankObj == null ? new Integer(0) : rankObj;
            otherRankObj = otherRankObj == null ? new Integer(0) : otherRankObj;
            Integer rank = rankObj instanceof Integer ? (Integer)rankObj : new Integer(0);
            Integer n = otherRank = otherRankObj instanceof Integer ? (Integer)otherRankObj : new Integer(0);
            if (rank.compareTo(otherRank) < 0) {
                return -1;
            }
            if (rank.compareTo(otherRank) > 0) {
                return 1;
            }
            return id.compareTo(otherId) < 0 ? 1 : -1;
        }

        @Override
        public Dictionary<String, Object> getProperties() {
            return new Hashtable<String, Object>(ServiceRegistrationImpl.this.m_propMap);
        }

        public Object adapt(Class type) {
            if (type == ServiceReferenceDTO.class) {
                return DTOFactory.createDTO(this);
            }
            return null;
        }
    }

    private class ServiceFactoryPrivileged
    implements PrivilegedExceptionAction {
        private Bundle m_bundle = null;
        private Object m_svcObj = null;

        public ServiceFactoryPrivileged(Bundle bundle, Object svcObj) {
            this.m_bundle = bundle;
            this.m_svcObj = svcObj;
        }

        public Object run() throws Exception {
            if (this.m_svcObj == null) {
                return ServiceRegistrationImpl.this.getFactoryUnchecked(this.m_bundle);
            }
            ServiceRegistrationImpl.this.ungetFactoryUnchecked(this.m_bundle, this.m_svcObj);
            return null;
        }
    }
}

