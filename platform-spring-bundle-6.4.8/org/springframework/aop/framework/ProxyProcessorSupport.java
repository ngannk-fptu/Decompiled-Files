/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.framework;

import java.io.Closeable;
import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.aop.framework.ProxyConfig;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.Aware;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

public class ProxyProcessorSupport
extends ProxyConfig
implements Ordered,
BeanClassLoaderAware,
AopInfrastructureBean {
    private int order = Integer.MAX_VALUE;
    @Nullable
    private ClassLoader proxyClassLoader = ClassUtils.getDefaultClassLoader();
    private boolean classLoaderConfigured = false;

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int getOrder() {
        return this.order;
    }

    public void setProxyClassLoader(@Nullable ClassLoader classLoader) {
        this.proxyClassLoader = classLoader;
        this.classLoaderConfigured = classLoader != null;
    }

    @Nullable
    protected ClassLoader getProxyClassLoader() {
        return this.proxyClassLoader;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        if (!this.classLoaderConfigured) {
            this.proxyClassLoader = classLoader;
        }
    }

    protected void evaluateProxyInterfaces(Class<?> beanClass, ProxyFactory proxyFactory) {
        Class<?>[] targetInterfaces = ClassUtils.getAllInterfacesForClass(beanClass, this.getProxyClassLoader());
        boolean hasReasonableProxyInterface = false;
        for (Class<?> ifc : targetInterfaces) {
            if (this.isConfigurationCallbackInterface(ifc) || this.isInternalLanguageInterface(ifc) || ifc.getMethods().length <= 0) continue;
            hasReasonableProxyInterface = true;
            break;
        }
        if (hasReasonableProxyInterface) {
            for (Class<?> ifc : targetInterfaces) {
                proxyFactory.addInterface(ifc);
            }
        } else {
            proxyFactory.setProxyTargetClass(true);
        }
    }

    protected boolean isConfigurationCallbackInterface(Class<?> ifc) {
        return InitializingBean.class == ifc || DisposableBean.class == ifc || Closeable.class == ifc || AutoCloseable.class == ifc || ObjectUtils.containsElement(ifc.getInterfaces(), Aware.class);
    }

    protected boolean isInternalLanguageInterface(Class<?> ifc) {
        return ifc.getName().equals("groovy.lang.GroovyObject") || ifc.getName().endsWith(".cglib.proxy.Factory") || ifc.getName().endsWith(".bytebuddy.MockAccess");
    }
}

