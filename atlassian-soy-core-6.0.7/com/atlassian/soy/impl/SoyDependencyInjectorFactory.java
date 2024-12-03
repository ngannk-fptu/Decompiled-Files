/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.tenancy.TenancyScope
 *  com.atlassian.annotations.tenancy.TenantAware
 *  com.google.common.base.Supplier
 *  com.google.inject.Guice
 *  com.google.inject.Injector
 *  com.google.inject.Module
 *  io.atlassian.util.concurrent.ResettableLazyReference
 */
package com.atlassian.soy.impl;

import com.atlassian.annotations.tenancy.TenancyScope;
import com.atlassian.annotations.tenancy.TenantAware;
import com.google.common.base.Supplier;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import io.atlassian.util.concurrent.ResettableLazyReference;

class SoyDependencyInjectorFactory {
    @TenantAware(value=TenancyScope.TENANTLESS, comment="SOY modules and dependencies needed for SOY Compiler, same for all tenants.")
    private final ResettableLazyReference<Injector> defaultInjectorRef;

    public SoyDependencyInjectorFactory(final Supplier<Iterable<Module>> moduleSupplier) {
        this.defaultInjectorRef = new ResettableLazyReference<Injector>(){

            protected Injector create() {
                return Guice.createInjector((Iterable)((Iterable)moduleSupplier.get()));
            }
        };
    }

    public void clear() {
        this.defaultInjectorRef.reset();
    }

    public Injector get() {
        return (Injector)this.defaultInjectorRef.get();
    }
}

