/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.FactoryBeanNotInitializedException
 *  org.springframework.beans.factory.SmartFactoryBean
 */
package org.eclipse.gemini.blueprint.service.importer.support;

import java.security.AccessController;
import java.security.PrivilegedAction;
import org.eclipse.gemini.blueprint.context.support.internal.classloader.ChainedClassLoader;
import org.eclipse.gemini.blueprint.context.support.internal.classloader.ClassLoaderFactory;
import org.eclipse.gemini.blueprint.service.importer.support.AbstractOsgiServiceImportFactoryBean;
import org.springframework.beans.factory.FactoryBeanNotInitializedException;
import org.springframework.beans.factory.SmartFactoryBean;

abstract class AbstractServiceImporterProxyFactoryBean
extends AbstractOsgiServiceImportFactoryBean
implements SmartFactoryBean<Object> {
    private volatile boolean initialized = false;
    protected Object proxy;
    private boolean useBlueprintException = false;
    private volatile boolean lazyProxy = false;
    private ChainedClassLoader aopClassLoader;
    private boolean blueprintCompliant;

    AbstractServiceImporterProxyFactoryBean() {
    }

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        if (this.blueprintCompliant) {
            this.setUseBlueprintExceptions(true);
        }
        Class<?>[] intfs = this.getInterfaces();
        for (int i = 0; i < intfs.length; ++i) {
            Class<?> intf = intfs[i];
            if (this.blueprintCompliant && !intf.isInterface()) {
                throw new IllegalArgumentException("Blueprint importers support only interfaces - for concrete classes, use the Spring DM namespace");
            }
            this.aopClassLoader.addClassLoader(intf);
        }
        this.initialized = true;
    }

    public void destroy() throws Exception {
        Runnable callback = this.getProxyDestructionCallback();
        try {
            if (callback != null) {
                callback.run();
            }
        }
        finally {
            this.proxy = null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object getObject() {
        AbstractServiceImporterProxyFactoryBean abstractServiceImporterProxyFactoryBean;
        if (!this.initialized) {
            throw new FactoryBeanNotInitializedException();
        }
        if (this.proxy == null) {
            abstractServiceImporterProxyFactoryBean = this;
            synchronized (abstractServiceImporterProxyFactoryBean) {
                if (this.proxy == null) {
                    this.proxy = this.createProxy(false);
                }
            }
        }
        if (this.lazyProxy) {
            abstractServiceImporterProxyFactoryBean = this;
            synchronized (abstractServiceImporterProxyFactoryBean) {
                if (this.lazyProxy) {
                    this.getProxyInitializer().run();
                    this.lazyProxy = false;
                }
            }
        }
        return this.proxy;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Class<?> getObjectType() {
        if (!this.initialized) {
            return null;
        }
        if (this.proxy == null) {
            AbstractServiceImporterProxyFactoryBean abstractServiceImporterProxyFactoryBean = this;
            synchronized (abstractServiceImporterProxyFactoryBean) {
                if (this.proxy == null) {
                    this.proxy = this.createProxy(true);
                    this.lazyProxy = true;
                }
            }
        }
        return this.proxy.getClass();
    }

    public boolean isSingleton() {
        return true;
    }

    public boolean isEagerInit() {
        return true;
    }

    public boolean isPrototype() {
        return false;
    }

    abstract Object createProxy(boolean var1);

    abstract Runnable getProxyInitializer();

    abstract Runnable getProxyDestructionCallback();

    ClassLoader getAopClassLoader() {
        return this.aopClassLoader;
    }

    @Override
    public void setBeanClassLoader(final ClassLoader classLoader) {
        super.setBeanClassLoader(classLoader);
        if (System.getSecurityManager() != null) {
            AccessController.doPrivileged(new PrivilegedAction<Object>(){

                @Override
                public Object run() {
                    AbstractServiceImporterProxyFactoryBean.this.aopClassLoader = ClassLoaderFactory.getAopClassLoaderFor(classLoader);
                    return null;
                }
            });
        } else {
            this.aopClassLoader = ClassLoaderFactory.getAopClassLoaderFor(classLoader);
        }
    }

    public void setUseBlueprintExceptions(boolean useBlueprintExceptions) {
        this.useBlueprintException = useBlueprintExceptions;
    }

    boolean isUseBlueprintExceptions() {
        return this.useBlueprintException;
    }

    public void setBlueprintCompliant(boolean compliant) {
        this.blueprintCompliant = compliant;
    }

    boolean isBlueprintCompliant() {
        return this.blueprintCompliant;
    }
}

