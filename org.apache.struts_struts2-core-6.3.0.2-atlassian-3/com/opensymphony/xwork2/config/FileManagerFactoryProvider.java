/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.config;

import com.opensymphony.xwork2.FileManagerFactory;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ContainerProvider;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.inject.Scope;
import com.opensymphony.xwork2.util.location.LocatableProperties;

public class FileManagerFactoryProvider
implements ContainerProvider {
    private Class<? extends FileManagerFactory> factoryClass;

    public FileManagerFactoryProvider(Class<? extends FileManagerFactory> factoryClass) {
        this.factoryClass = factoryClass;
    }

    @Override
    public void destroy() {
    }

    @Override
    public void init(Configuration configuration) throws ConfigurationException {
    }

    @Override
    public boolean needsReload() {
        return false;
    }

    @Override
    public void register(ContainerBuilder builder, LocatableProperties props) throws ConfigurationException {
        builder.factory(FileManagerFactory.class, this.factoryClass.getSimpleName(), this.factoryClass, Scope.SINGLETON);
    }
}

