/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ProviderType
 */
package org.osgi.framework;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.Dictionary;
import org.osgi.annotation.versioning.ProviderType;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.BundleReference;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceObjects;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

@ProviderType
public interface BundleContext
extends BundleReference {
    public String getProperty(String var1);

    @Override
    public Bundle getBundle();

    public Bundle installBundle(String var1, InputStream var2) throws BundleException;

    public Bundle installBundle(String var1) throws BundleException;

    public Bundle getBundle(long var1);

    public Bundle[] getBundles();

    public void addServiceListener(ServiceListener var1, String var2) throws InvalidSyntaxException;

    public void addServiceListener(ServiceListener var1);

    public void removeServiceListener(ServiceListener var1);

    public void addBundleListener(BundleListener var1);

    public void removeBundleListener(BundleListener var1);

    public void addFrameworkListener(FrameworkListener var1);

    public void removeFrameworkListener(FrameworkListener var1);

    public ServiceRegistration<?> registerService(String[] var1, Object var2, Dictionary<String, ?> var3);

    public ServiceRegistration<?> registerService(String var1, Object var2, Dictionary<String, ?> var3);

    public <S> ServiceRegistration<S> registerService(Class<S> var1, S var2, Dictionary<String, ?> var3);

    public <S> ServiceRegistration<S> registerService(Class<S> var1, ServiceFactory<S> var2, Dictionary<String, ?> var3);

    public ServiceReference<?>[] getServiceReferences(String var1, String var2) throws InvalidSyntaxException;

    public ServiceReference<?>[] getAllServiceReferences(String var1, String var2) throws InvalidSyntaxException;

    public ServiceReference<?> getServiceReference(String var1);

    public <S> ServiceReference<S> getServiceReference(Class<S> var1);

    public <S> Collection<ServiceReference<S>> getServiceReferences(Class<S> var1, String var2) throws InvalidSyntaxException;

    public <S> S getService(ServiceReference<S> var1);

    public boolean ungetService(ServiceReference<?> var1);

    public <S> ServiceObjects<S> getServiceObjects(ServiceReference<S> var1);

    public File getDataFile(String var1);

    public Filter createFilter(String var1) throws InvalidSyntaxException;

    public Bundle getBundle(String var1);
}

