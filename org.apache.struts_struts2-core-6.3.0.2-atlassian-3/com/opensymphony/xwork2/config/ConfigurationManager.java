/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2.config;

import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ContainerProvider;
import com.opensymphony.xwork2.config.PackageProvider;
import com.opensymphony.xwork2.config.impl.DefaultConfiguration;
import com.opensymphony.xwork2.config.providers.StrutsDefaultConfigurationProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfigurationManager {
    protected static final Logger LOG = LogManager.getLogger(ConfigurationManager.class);
    protected Configuration configuration;
    private List<ContainerProvider> containerProviders = new ArrayList<ContainerProvider>();
    private List<PackageProvider> packageProviders = new ArrayList<PackageProvider>();
    protected String defaultFrameworkBeanName;
    private boolean providersChanged = true;
    private boolean alwaysReloadConfigs = false;

    public ConfigurationManager(String name) {
        this.defaultFrameworkBeanName = name;
    }

    public synchronized Configuration getConfiguration() {
        if (this.wasConfigInitialised()) {
            this.conditionalReload();
        }
        return this.configuration;
    }

    private boolean wasConfigInitialised() {
        if (this.configuration == null) {
            this.initialiseConfiguration();
            return false;
        }
        return true;
    }

    protected void initialiseConfiguration() {
        if (this.containerProviders.isEmpty()) {
            this.addDefaultContainerProviders();
        }
        this.configuration = this.createConfiguration(this.defaultFrameworkBeanName);
        try {
            this.reload();
        }
        catch (ConfigurationException e) {
            this.configuration.destroy();
            this.configuration = null;
            this.providersChanged = true;
            throw new ConfigurationException("Unable to load configuration.", e);
        }
    }

    protected void addDefaultContainerProviders() {
        this.containerProviders.add(new StrutsDefaultConfigurationProvider());
    }

    protected Configuration createConfiguration(String beanName) {
        return new DefaultConfiguration(beanName);
    }

    public synchronized void destroyConfiguration() {
        this.clearContainerProviders();
        if (this.configuration != null) {
            this.configuration.destroy();
            this.configuration = null;
        }
    }

    public synchronized void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public synchronized List<ContainerProvider> getContainerProviders() {
        return new ArrayList<ContainerProvider>(this.containerProviders);
    }

    public synchronized void setContainerProviders(List<ContainerProvider> containerProviders) {
        this.containerProviders = new ArrayList<ContainerProvider>(containerProviders);
        this.providersChanged = true;
    }

    public synchronized void addContainerProvider(ContainerProvider provider) {
        if (!this.containerProviders.contains(provider)) {
            this.containerProviders.add(provider);
            this.providersChanged = true;
        }
    }

    public synchronized void removeContainerProvider(ContainerProvider provider) {
        if (this.containerProviders.remove(provider)) {
            this.destroyContainerProvider(provider);
            this.providersChanged = true;
        }
    }

    public synchronized void clearContainerProviders() {
        this.destroyContainerProviders();
        this.containerProviders.clear();
        this.providersChanged = true;
    }

    private void destroyContainerProviders() {
        LOG.debug("Destroying all providers.");
        this.containerProviders.forEach(this::destroyContainerProvider);
    }

    private void destroyContainerProvider(ContainerProvider containerProvider) {
        try {
            containerProvider.destroy();
        }
        catch (Exception e) {
            LOG.warn("Error while destroying container provider [{}]", (Object)containerProvider.toString(), (Object)e);
        }
    }

    public synchronized void conditionalReload() {
        if (this.alwaysReloadConfigs || this.providersChanged) {
            LOG.debug("Checking ConfigurationProviders for reload.");
            if (this.needReloadContainerProviders() || this.needReloadPackageProviders()) {
                this.destroyAndReload();
            }
            this.providersChanged = false;
        }
    }

    private void updateAlwaysReloadFlag() {
        boolean newValue = Boolean.parseBoolean(this.configuration.getContainer().getInstance(String.class, "struts.configuration.xml.reload"));
        if (this.alwaysReloadConfigs != newValue) {
            LOG.debug("Updating [{}], current value is [{}], new value [{}]", (Object)"struts.configuration.xml.reload", (Object)String.valueOf(this.alwaysReloadConfigs), (Object)String.valueOf(newValue));
            this.alwaysReloadConfigs = newValue;
        }
    }

    private boolean needReloadPackageProviders() {
        Optional<PackageProvider> provider = this.packageProviders.stream().filter(PackageProvider::needsReload).findAny();
        if (provider.isPresent()) {
            LOG.info("Detected package provider [{}] needs to be reloaded.", (Object)provider.get());
            return true;
        }
        return false;
    }

    private boolean needReloadContainerProviders() {
        Optional<ContainerProvider> provider = this.containerProviders.stream().filter(ContainerProvider::needsReload).findAny();
        if (provider.isPresent()) {
            LOG.info("Detected container provider [{}] needs to be reloaded.", (Object)provider.get());
            return true;
        }
        return false;
    }

    public synchronized void destroyAndReload() {
        this.destroyContainerProviders();
        this.reload();
    }

    public synchronized void reload() {
        if (this.wasConfigInitialised()) {
            LOG.debug("Reloading all providers.");
            this.packageProviders = this.configuration.reloadContainer(this.containerProviders);
            this.providersChanged = false;
            this.updateAlwaysReloadFlag();
        }
    }
}

