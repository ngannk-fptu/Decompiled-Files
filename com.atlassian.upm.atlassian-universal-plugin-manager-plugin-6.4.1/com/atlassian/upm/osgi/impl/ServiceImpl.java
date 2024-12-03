/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.ServiceReference
 */
package com.atlassian.upm.osgi.impl;

import com.atlassian.upm.osgi.Bundle;
import com.atlassian.upm.osgi.PackageAccessor;
import com.atlassian.upm.osgi.Service;
import com.atlassian.upm.osgi.impl.BundleImpl;
import com.atlassian.upm.osgi.impl.Wrapper;
import java.util.Arrays;
import java.util.Collections;
import javax.annotation.Nullable;
import org.apache.sling.commons.osgi.OsgiUtil;
import org.osgi.framework.ServiceReference;

public final class ServiceImpl
implements Service {
    private final ServiceReference service;
    private final PackageAccessor packageAccessor;

    ServiceImpl(ServiceReference service, PackageAccessor packageAccessor) {
        this.service = service;
        this.packageAccessor = packageAccessor;
    }

    @Override
    public Bundle getBundle() {
        return BundleImpl.wrap(this.packageAccessor).fromSingleton(this.service.getBundle());
    }

    @Override
    public Iterable<Bundle> getUsingBundles() {
        return BundleImpl.wrap(this.packageAccessor).fromArray((org.osgi.framework.Bundle[])this.service.getUsingBundles());
    }

    @Override
    public Iterable<String> getObjectClasses() {
        return this.getStringIterableProperty("objectClass");
    }

    @Override
    @Nullable
    public String getDescription() {
        return this.getNullableStringProperty("service.description");
    }

    @Override
    public long getId() {
        return OsgiUtil.toLong(this.service.getProperty("service.id"), 0L);
    }

    @Override
    public Iterable<String> getPid() {
        return this.getStringIterableProperty("service.pid");
    }

    @Override
    public int getRanking() {
        return OsgiUtil.toInteger(this.service.getProperty("service.ranking"), 0);
    }

    @Override
    @Nullable
    public String getVendor() {
        return this.getNullableStringProperty("service.vendor");
    }

    protected static Wrapper<ServiceReference, Service> wrap(final PackageAccessor packageAccessor) {
        return new Wrapper<ServiceReference, Service>("service"){

            @Override
            protected Service wrap(ServiceReference service) {
                return new ServiceImpl(service, packageAccessor);
            }
        };
    }

    @Nullable
    private String getNullableStringProperty(String key) {
        return OsgiUtil.toString(this.service.getProperty(key), null);
    }

    private Iterable<String> getStringIterableProperty(String key) {
        return Collections.unmodifiableList(Arrays.asList(OsgiUtil.toStringArray(this.service.getProperty(key), new String[0])));
    }
}

