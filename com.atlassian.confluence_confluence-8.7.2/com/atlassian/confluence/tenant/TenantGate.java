/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.util.concurrent.LazyReference
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Supplier
 *  org.aopalliance.aop.Advice
 *  org.aopalliance.intercept.MethodInterceptor
 *  org.apache.commons.lang3.ArrayUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.aop.TargetSource
 *  org.springframework.aop.framework.ProxyFactory
 *  org.springframework.beans.factory.FactoryBean
 */
package com.atlassian.confluence.tenant;

import com.atlassian.confluence.impl.tenant.ThreadLocalTenantGate;
import com.atlassian.confluence.tenant.TenantRegistry;
import com.atlassian.confluence.tenant.VacantException;
import com.atlassian.util.concurrent.LazyReference;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import java.util.Arrays;
import java.util.concurrent.Callable;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.FactoryBean;

@Deprecated(forRemoval=true)
public abstract class TenantGate<T>
implements FactoryBean<T> {
    private static final Logger log = LoggerFactory.getLogger(TenantGate.class);
    @Deprecated
    protected final TenantRegistry tenantRegistry;
    @Deprecated
    protected final Class[] proxyInterfaces;
    private final Supplier<T> vacantDelegateRef = new LazyReference<T>(){

        protected T create() throws Exception {
            log.debug("Creating vacant tenant delegate for {}", (Object)Arrays.toString(TenantGate.this.proxyInterfaces));
            return TenantGate.this.createVacantDelegate();
        }
    };
    private final Supplier<T> tenantedDelegateRef = new LazyReference<T>(){

        protected T create() throws Exception {
            log.debug("Creating tenanted delegate for {}", (Object)Arrays.toString(TenantGate.this.proxyInterfaces));
            return TenantGate.this.createTenantedDelegate();
        }
    };

    protected TenantGate(TenantRegistry tenantRegistry, Class[] proxyInterfaces) {
        this.tenantRegistry = tenantRegistry;
        this.proxyInterfaces = proxyInterfaces;
        Preconditions.checkArgument((boolean)ArrayUtils.isNotEmpty((Object[])proxyInterfaces), (Object)"No interface to proxy");
    }

    protected TenantRegistry getTenantRegistry() {
        return this.tenantRegistry;
    }

    protected Class[] getProxyInterfaces() {
        return Arrays.copyOf(this.proxyInterfaces, this.proxyInterfaces.length);
    }

    public boolean isPermitted() {
        return !this.tenantRegistry.isRegistryVacant() || ThreadLocalTenantGate.hasTenantPermit();
    }

    @Deprecated
    public static <T> Callable<T> open(Callable<T> callback) {
        return ThreadLocalTenantGate.withTenantPermit(callback);
    }

    @Deprecated
    public static <T> Callable<T> close(Callable<T> callback) {
        return ThreadLocalTenantGate.withoutTenantPermit(callback);
    }

    protected T createVacantDelegate() {
        return this.createDefaultVacantDelegate();
    }

    protected abstract T createTenantedDelegate();

    public final T getObject() {
        return this.getTenantAwareProxy();
    }

    private T getTenantAwareProxy() {
        ProxyFactory proxyFactory = new ProxyFactory(this.proxyInterfaces);
        proxyFactory.setTargetSource(this.createTargetSource(this::getDelegate));
        return (T)proxyFactory.getProxy();
    }

    private T getDelegate() {
        if (this.isPermitted()) {
            return (T)this.tenantedDelegateRef.get();
        }
        return (T)this.vacantDelegateRef.get();
    }

    private T createDefaultVacantDelegate() {
        ProxyFactory proxyFactory = new ProxyFactory(this.proxyInterfaces);
        proxyFactory.addAdvice((Advice)this.throwVacantExceptionOnMethodCall());
        return (T)proxyFactory.getProxy();
    }

    private MethodInterceptor throwVacantExceptionOnMethodCall() {
        return methodInvocation -> {
            if ("toString".equals(methodInvocation.getMethod().getName())) {
                return String.format("invoking toString on %s", Arrays.toString(this.proxyInterfaces));
            }
            throw new VacantException(methodInvocation);
        };
    }

    private TargetSource createTargetSource(final Supplier<T> lookup) {
        return new TargetSource(){

            public Class getTargetClass() {
                return TenantGate.this.getObjectType();
            }

            public boolean isStatic() {
                return false;
            }

            public Object getTarget() throws Exception {
                return lookup.get();
            }

            public void releaseTarget(Object target) throws Exception {
            }
        };
    }

    public final Class getObjectType() {
        return this.proxyInterfaces[0];
    }

    public final boolean isSingleton() {
        return true;
    }

    @Deprecated
    public static <T> Callable<T> permit(boolean permitted, Callable<T> callback) {
        return ThreadLocalTenantGate.wrap(permitted, callback);
    }
}

