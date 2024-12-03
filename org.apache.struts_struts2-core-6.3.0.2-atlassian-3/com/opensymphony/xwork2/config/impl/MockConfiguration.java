/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.config.impl;

import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ContainerProvider;
import com.opensymphony.xwork2.config.PackageProvider;
import com.opensymphony.xwork2.config.RuntimeConfiguration;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.UnknownHandlerConfig;
import com.opensymphony.xwork2.config.impl.DefaultConfiguration;
import com.opensymphony.xwork2.config.providers.StrutsDefaultConfigurationProvider;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.inject.Scope;
import com.opensymphony.xwork2.util.location.LocatableProperties;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MockConfiguration
implements Configuration {
    private Map<String, PackageConfig> packages = new HashMap<String, PackageConfig>();
    private Set<String> loadedFiles = new HashSet<String>();
    private Container container;
    protected List<UnknownHandlerConfig> unknownHandlerStack;
    private ContainerBuilder builder = new ContainerBuilder();

    public void selfRegister() {
        this.builder.factory(Configuration.class, MockConfiguration.class, Scope.SINGLETON);
        LocatableProperties props = new LocatableProperties();
        new StrutsDefaultConfigurationProvider().register(this.builder, props);
        for (Map.Entry<String, Object> entry : DefaultConfiguration.BOOTSTRAP_CONSTANTS.entrySet()) {
            this.builder.constant(entry.getKey(), String.valueOf(entry.getValue()));
        }
        this.container = this.builder.create(true);
    }

    @Override
    public PackageConfig getPackageConfig(String name) {
        return this.packages.get(name);
    }

    @Override
    public Set<String> getPackageConfigNames() {
        return this.packages.keySet();
    }

    @Override
    public Map<String, PackageConfig> getPackageConfigs() {
        return this.packages;
    }

    @Override
    public RuntimeConfiguration getRuntimeConfiguration() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addPackageConfig(String name, PackageConfig packageContext) {
        this.packages.put(name, packageContext);
    }

    public void buildRuntimeConfiguration() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void destroy() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void rebuildRuntimeConfiguration() {
        throw new UnsupportedOperationException();
    }

    @Override
    public PackageConfig removePackageConfig(String name) {
        return this.packages.remove(name);
    }

    @Override
    public Container getContainer() {
        return this.container;
    }

    @Override
    public Set<String> getLoadedFileNames() {
        return this.loadedFiles;
    }

    @Override
    public List<PackageProvider> reloadContainer(List<ContainerProvider> containerProviders) throws ConfigurationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<UnknownHandlerConfig> getUnknownHandlerStack() {
        return this.unknownHandlerStack;
    }

    @Override
    public void setUnknownHandlerStack(List<UnknownHandlerConfig> unknownHandlerStack) {
        this.unknownHandlerStack = unknownHandlerStack;
    }
}

