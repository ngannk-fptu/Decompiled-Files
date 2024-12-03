/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.config;

import com.opensymphony.xwork2.FileManager;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ContainerProvider;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.inject.Scope;
import com.opensymphony.xwork2.util.location.LocatableProperties;

public class FileManagerProvider
implements ContainerProvider {
    private Class<? extends FileManager> fileManagerClass;
    private String name;

    public FileManagerProvider(Class<? extends FileManager> fileManagerClass, String name) {
        this.fileManagerClass = fileManagerClass;
        this.name = name;
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
        builder.factory(FileManager.class, this.name, this.fileManagerClass, Scope.SINGLETON);
    }
}

