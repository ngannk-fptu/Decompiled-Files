/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.PropertyPermission;
import org.apache.felix.framework.BundleImpl;
import org.apache.felix.framework.Felix;
import org.apache.felix.framework.FilterImpl;
import org.apache.felix.framework.Logger;
import org.apache.felix.framework.ServiceRegistrationImpl;
import org.osgi.framework.AdminPermission;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceObjects;
import org.osgi.framework.ServicePermission;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.SynchronousBundleListener;

class BundleContextImpl
implements BundleContext {
    private Logger m_logger = null;
    private Felix m_felix = null;
    private BundleImpl m_bundle = null;
    private boolean m_valid = true;

    protected BundleContextImpl(Logger logger, Felix felix, BundleImpl bundle) {
        this.m_logger = logger;
        this.m_felix = felix;
        this.m_bundle = bundle;
    }

    protected void invalidate() {
        this.m_valid = false;
    }

    @Override
    public String getProperty(String name) {
        this.checkValidity();
        SecurityManager sm = System.getSecurityManager();
        if (!(sm == null || "org.osgi.framework.version".equals(name) || "org.osgi.framework.vendor".equals(name) || "org.osgi.framework.language".equals(name) || "org.osgi.framework.os.name".equals(name) || "org.osgi.framework.os.version".equals(name) || "org.osgi.framework.processor".equals(name))) {
            sm.checkPermission(new PropertyPermission(name, "read"));
        }
        return this.m_felix.getProperty(name);
    }

    @Override
    public Bundle getBundle() {
        this.checkValidity();
        return this.m_bundle;
    }

    @Override
    public Filter createFilter(String expr) throws InvalidSyntaxException {
        this.checkValidity();
        return new FilterImpl(expr);
    }

    @Override
    public Bundle installBundle(String location) throws BundleException {
        return this.installBundle(location, null);
    }

    @Override
    public Bundle installBundle(String location, InputStream is) throws BundleException {
        this.checkValidity();
        Bundle result = null;
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            result = this.m_felix.installBundle(this.m_bundle, location, is);
            sm.checkPermission(new AdminPermission(result, "lifecycle"));
        } else {
            result = this.m_felix.installBundle(this.m_bundle, location, is);
        }
        return result;
    }

    @Override
    public Bundle getBundle(long id) {
        this.checkValidity();
        return this.m_felix.getBundle(this, id);
    }

    @Override
    public Bundle getBundle(String location) {
        this.checkValidity();
        return this.m_felix.getBundle(location);
    }

    @Override
    public Bundle[] getBundles() {
        this.checkValidity();
        return this.m_felix.getBundles(this);
    }

    @Override
    public void addBundleListener(BundleListener l) {
        this.checkValidity();
        SecurityManager sm = System.getSecurityManager();
        if (sm != null && l instanceof SynchronousBundleListener) {
            sm.checkPermission(new AdminPermission(this.m_bundle, "listener"));
        }
        this.m_felix.addBundleListener(this.m_bundle, l);
    }

    @Override
    public void removeBundleListener(BundleListener l) {
        this.checkValidity();
        SecurityManager sm = System.getSecurityManager();
        if (sm != null && l instanceof SynchronousBundleListener) {
            sm.checkPermission(new AdminPermission(this.m_bundle, "listener"));
        }
        this.m_felix.removeBundleListener(this.m_bundle, l);
    }

    @Override
    public void addServiceListener(ServiceListener l) {
        try {
            this.addServiceListener(l, null);
        }
        catch (InvalidSyntaxException invalidSyntaxException) {
            // empty catch block
        }
    }

    @Override
    public void addServiceListener(ServiceListener l, String s) throws InvalidSyntaxException {
        this.checkValidity();
        this.m_felix.addServiceListener(this.m_bundle, l, s);
    }

    @Override
    public void removeServiceListener(ServiceListener l) {
        this.checkValidity();
        this.m_felix.removeServiceListener(this.m_bundle, l);
    }

    @Override
    public void addFrameworkListener(FrameworkListener l) {
        this.checkValidity();
        this.m_felix.addFrameworkListener(this.m_bundle, l);
    }

    @Override
    public void removeFrameworkListener(FrameworkListener l) {
        this.checkValidity();
        this.m_felix.removeFrameworkListener(this.m_bundle, l);
    }

    @Override
    public ServiceRegistration<?> registerService(String clazz, Object svcObj, Dictionary<String, ?> dict) {
        return this.registerService(new String[]{clazz}, svcObj, dict);
    }

    @Override
    public ServiceRegistration<?> registerService(String[] clazzes, Object svcObj, Dictionary<String, ?> dict) {
        this.checkValidity();
        SecurityManager sm = System.getSecurityManager();
        if (sm != null && clazzes != null) {
            for (int i = 0; i < clazzes.length; ++i) {
                sm.checkPermission(new ServicePermission(clazzes[i], "register"));
            }
        }
        return this.m_felix.registerService(this, clazzes, svcObj, dict);
    }

    @Override
    public <S> ServiceRegistration<S> registerService(Class<S> clazz, S svcObj, Dictionary<String, ?> dict) {
        return this.registerService(new String[]{clazz.getName()}, svcObj, dict);
    }

    @Override
    public ServiceReference<?> getServiceReference(String clazz) {
        this.checkValidity();
        try {
            ServiceReference[] refs = this.getServiceReferences(clazz, null);
            return this.getBestServiceReference(refs);
        }
        catch (InvalidSyntaxException ex) {
            this.m_logger.log(this.m_bundle, 1, "BundleContextImpl: " + ex);
            return null;
        }
    }

    @Override
    public <S> ServiceReference<S> getServiceReference(Class<S> clazz) {
        return this.getServiceReference(clazz.getName());
    }

    private ServiceReference getBestServiceReference(ServiceReference[] refs) {
        if (refs == null) {
            return null;
        }
        if (refs.length == 1) {
            return refs[0];
        }
        ServiceReference bestRef = refs[0];
        for (int i = 1; i < refs.length; ++i) {
            if (bestRef.compareTo(refs[i]) >= 0) continue;
            bestRef = refs[i];
        }
        return bestRef;
    }

    @Override
    public ServiceReference<?>[] getAllServiceReferences(String clazz, String filter) throws InvalidSyntaxException {
        this.checkValidity();
        return this.m_felix.getAllowedServiceReferences(this.m_bundle, clazz, filter, false);
    }

    @Override
    public ServiceReference<?>[] getServiceReferences(String clazz, String filter) throws InvalidSyntaxException {
        this.checkValidity();
        return this.m_felix.getAllowedServiceReferences(this.m_bundle, clazz, filter, true);
    }

    @Override
    public <S> Collection<ServiceReference<S>> getServiceReferences(Class<S> clazz, String filter) throws InvalidSyntaxException {
        ServiceReference<?>[] refs = this.getServiceReferences(clazz.getName(), filter);
        return refs == null ? Collections.EMPTY_LIST : Arrays.asList(refs);
    }

    @Override
    public <S> S getService(ServiceReference<S> ref) {
        this.checkValidity();
        if (ref == null) {
            throw new NullPointerException("Specified service reference cannot be null.");
        }
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new ServicePermission(ref, "get"));
        }
        return this.m_felix.getService(this.m_bundle, ref, false);
    }

    @Override
    public boolean ungetService(ServiceReference<?> ref) {
        this.checkValidity();
        if (ref == null) {
            throw new NullPointerException("Specified service reference cannot be null.");
        }
        return this.m_felix.ungetService(this.m_bundle, ref, null);
    }

    @Override
    public File getDataFile(String s) {
        this.checkValidity();
        return this.m_felix.getDataFile(this.m_bundle, s);
    }

    private void checkValidity() {
        if (this.m_valid) {
            switch (this.m_bundle.getState()) {
                case 8: 
                case 16: 
                case 32: {
                    return;
                }
            }
        }
        throw new IllegalStateException("Invalid BundleContext.");
    }

    @Override
    public <S> ServiceRegistration<S> registerService(Class<S> clazz, ServiceFactory<S> factory, Dictionary<String, ?> properties) {
        return this.registerService(new String[]{clazz.getName()}, factory, properties);
    }

    @Override
    public <S> ServiceObjects<S> getServiceObjects(ServiceReference<S> ref) {
        ServiceRegistrationImpl reg;
        this.checkValidity();
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new ServicePermission(ref, "get"));
        }
        if ((reg = ((ServiceRegistrationImpl.ServiceReferenceImpl)ref).getRegistration()).isValid()) {
            return new ServiceObjectsImpl<S>(ref);
        }
        return null;
    }

    class ServiceObjectsImpl<S>
    implements ServiceObjects<S> {
        private final ServiceReference<S> m_ref;

        public ServiceObjectsImpl(ServiceReference<S> ref) {
            this.m_ref = ref;
        }

        @Override
        public S getService() {
            BundleContextImpl.this.checkValidity();
            SecurityManager sm = System.getSecurityManager();
            if (sm != null) {
                sm.checkPermission(new ServicePermission(this.m_ref, "get"));
            }
            return BundleContextImpl.this.m_felix.getService(BundleContextImpl.this.m_bundle, this.m_ref, true);
        }

        @Override
        public void ungetService(S srvObj) {
            BundleContextImpl.this.checkValidity();
            if (!BundleContextImpl.this.m_felix.ungetService(BundleContextImpl.this.m_bundle, this.m_ref, srvObj) && this.m_ref.getBundle() != null) {
                throw new IllegalArgumentException();
            }
        }

        @Override
        public ServiceReference<S> getServiceReference() {
            return this.m_ref;
        }
    }
}

