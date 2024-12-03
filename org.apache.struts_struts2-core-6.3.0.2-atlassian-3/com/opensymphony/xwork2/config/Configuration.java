/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.config;

import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ContainerProvider;
import com.opensymphony.xwork2.config.PackageProvider;
import com.opensymphony.xwork2.config.RuntimeConfiguration;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.UnknownHandlerConfig;
import com.opensymphony.xwork2.inject.Container;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Configuration
extends Serializable {
    public void rebuildRuntimeConfiguration();

    public PackageConfig getPackageConfig(String var1);

    public Set<String> getPackageConfigNames();

    public Map<String, PackageConfig> getPackageConfigs();

    public RuntimeConfiguration getRuntimeConfiguration();

    public void addPackageConfig(String var1, PackageConfig var2);

    public PackageConfig removePackageConfig(String var1);

    public void destroy();

    public List<PackageProvider> reloadContainer(List<ContainerProvider> var1) throws ConfigurationException;

    public Container getContainer();

    public Set<String> getLoadedFileNames();

    public List<UnknownHandlerConfig> getUnknownHandlerStack();

    public void setUnknownHandlerStack(List<UnknownHandlerConfig> var1);
}

