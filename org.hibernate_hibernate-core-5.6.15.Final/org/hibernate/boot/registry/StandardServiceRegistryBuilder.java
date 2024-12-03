/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.registry;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.boot.cfgxml.internal.ConfigLoader;
import org.hibernate.boot.cfgxml.spi.LoadedConfig;
import org.hibernate.boot.registry.BootstrapServiceRegistry;
import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
import org.hibernate.boot.registry.StandardServiceInitiator;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.registry.internal.StandardServiceRegistryImpl;
import org.hibernate.cfg.Environment;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.integrator.spi.IntegratorService;
import org.hibernate.integrator.spi.ServiceContributingIntegrator;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.service.Service;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.StandardServiceInitiators;
import org.hibernate.service.internal.ProvidedService;
import org.hibernate.service.spi.ServiceContributor;

public class StandardServiceRegistryBuilder {
    public static final String DEFAULT_CFG_RESOURCE_NAME = "hibernate.cfg.xml";
    private final Map settings;
    private final List<StandardServiceInitiator> initiators;
    private final List<ProvidedService> providedServices = new ArrayList<ProvidedService>();
    private boolean autoCloseRegistry = true;
    private final BootstrapServiceRegistry bootstrapServiceRegistry;
    private final ConfigLoader configLoader;
    private final LoadedConfig aggregatedCfgXml;

    public static StandardServiceRegistryBuilder forJpa(BootstrapServiceRegistry bootstrapServiceRegistry) {
        LoadedConfig loadedConfig = new LoadedConfig(null){

            @Override
            protected void addConfigurationValues(Map configurationValues) {
            }
        };
        return new StandardServiceRegistryBuilder(bootstrapServiceRegistry, new HashMap(), loadedConfig){

            @Override
            public StandardServiceRegistryBuilder configure(LoadedConfig loadedConfig) {
                this.getAggregatedCfgXml().merge(loadedConfig);
                return this;
            }
        };
    }

    public StandardServiceRegistryBuilder() {
        this(new BootstrapServiceRegistryBuilder().enableAutoClose().build());
    }

    public StandardServiceRegistryBuilder(BootstrapServiceRegistry bootstrapServiceRegistry) {
        this(bootstrapServiceRegistry, LoadedConfig.baseline());
    }

    protected StandardServiceRegistryBuilder(BootstrapServiceRegistry bootstrapServiceRegistry, Map settings, LoadedConfig loadedConfig) {
        this.bootstrapServiceRegistry = bootstrapServiceRegistry;
        this.configLoader = new ConfigLoader(bootstrapServiceRegistry);
        this.settings = settings;
        this.aggregatedCfgXml = loadedConfig;
        this.initiators = StandardServiceRegistryBuilder.standardInitiatorList();
    }

    @Deprecated
    protected StandardServiceRegistryBuilder(BootstrapServiceRegistry bootstrapServiceRegistry, Map settings, LoadedConfig loadedConfig, List<StandardServiceInitiator> initiators) {
        this.bootstrapServiceRegistry = bootstrapServiceRegistry;
        this.configLoader = new ConfigLoader(bootstrapServiceRegistry);
        this.settings = settings;
        this.aggregatedCfgXml = loadedConfig;
        this.initiators = initiators;
    }

    protected StandardServiceRegistryBuilder(BootstrapServiceRegistry bootstrapServiceRegistry, Map settings, ConfigLoader loader, LoadedConfig loadedConfig, List<StandardServiceInitiator> initiators) {
        this.bootstrapServiceRegistry = bootstrapServiceRegistry;
        this.configLoader = loader;
        this.settings = settings;
        this.aggregatedCfgXml = loadedConfig;
        this.initiators = initiators;
    }

    public StandardServiceRegistryBuilder(BootstrapServiceRegistry bootstrapServiceRegistry, LoadedConfig loadedConfigBaseline) {
        this.settings = Environment.getProperties();
        this.bootstrapServiceRegistry = bootstrapServiceRegistry;
        this.configLoader = new ConfigLoader(bootstrapServiceRegistry);
        this.aggregatedCfgXml = loadedConfigBaseline;
        this.initiators = StandardServiceRegistryBuilder.standardInitiatorList();
    }

    public ConfigLoader getConfigLoader() {
        return this.configLoader;
    }

    public LoadedConfig getAggregatedCfgXml() {
        return this.aggregatedCfgXml;
    }

    private static List<StandardServiceInitiator> standardInitiatorList() {
        ArrayList<StandardServiceInitiator> initiators = new ArrayList<StandardServiceInitiator>(StandardServiceInitiators.LIST.size());
        initiators.addAll(StandardServiceInitiators.LIST);
        return initiators;
    }

    public BootstrapServiceRegistry getBootstrapServiceRegistry() {
        return this.bootstrapServiceRegistry;
    }

    public StandardServiceRegistryBuilder loadProperties(String resourceName) {
        this.settings.putAll(this.configLoader.loadProperties(resourceName));
        return this;
    }

    public StandardServiceRegistryBuilder loadProperties(File file) {
        this.settings.putAll(this.configLoader.loadProperties(file));
        return this;
    }

    public StandardServiceRegistryBuilder configure() {
        return this.configure(DEFAULT_CFG_RESOURCE_NAME);
    }

    public StandardServiceRegistryBuilder configure(String resourceName) {
        return this.configure(this.configLoader.loadConfigXmlResource(resourceName));
    }

    public StandardServiceRegistryBuilder configure(File configurationFile) {
        return this.configure(this.configLoader.loadConfigXmlFile(configurationFile));
    }

    public StandardServiceRegistryBuilder configure(URL url) {
        return this.configure(this.configLoader.loadConfigXmlUrl(url));
    }

    public StandardServiceRegistryBuilder configure(LoadedConfig loadedConfig) {
        this.aggregatedCfgXml.merge(loadedConfig);
        this.settings.putAll(loadedConfig.getConfigurationValues());
        return this;
    }

    public StandardServiceRegistryBuilder applySetting(String settingName, Object value) {
        this.settings.put(settingName, value);
        return this;
    }

    public StandardServiceRegistryBuilder applySettings(Map settings) {
        this.settings.putAll(settings);
        return this;
    }

    public void clearSettings() {
        this.settings.clear();
    }

    public StandardServiceRegistryBuilder addInitiator(StandardServiceInitiator initiator) {
        this.initiators.add(initiator);
        return this;
    }

    public StandardServiceRegistryBuilder addService(Class serviceRole, Service service) {
        this.providedServices.add(new ProvidedService<Service>(serviceRole, service));
        return this;
    }

    public StandardServiceRegistryBuilder disableAutoClose() {
        this.autoCloseRegistry = false;
        return this;
    }

    public StandardServiceRegistryBuilder enableAutoClose() {
        this.autoCloseRegistry = true;
        return this;
    }

    public StandardServiceRegistry build() {
        this.applyServiceContributingIntegrators();
        this.applyServiceContributors();
        HashMap<String, LoadedConfig> settingsCopy = new HashMap<String, LoadedConfig>(this.settings);
        settingsCopy.put("hibernate.boot.CfgXmlAccessService.key", this.aggregatedCfgXml);
        ConfigurationHelper.resolvePlaceHolders(settingsCopy);
        return new StandardServiceRegistryImpl(this.autoCloseRegistry, this.bootstrapServiceRegistry, this.initiators, this.providedServices, settingsCopy);
    }

    private void applyServiceContributingIntegrators() {
        for (Integrator integrator : this.bootstrapServiceRegistry.getService(IntegratorService.class).getIntegrators()) {
            if (!ServiceContributingIntegrator.class.isInstance(integrator)) continue;
            ((ServiceContributingIntegrator)ServiceContributingIntegrator.class.cast(integrator)).prepareServices(this);
        }
    }

    private void applyServiceContributors() {
        Collection<ServiceContributor> serviceContributors = this.bootstrapServiceRegistry.getService(ClassLoaderService.class).loadJavaServices(ServiceContributor.class);
        for (ServiceContributor serviceContributor : serviceContributors) {
            serviceContributor.contribute(this);
        }
    }

    @Deprecated
    public Map getSettings() {
        return this.settings;
    }

    public static void destroy(ServiceRegistry serviceRegistry) {
        if (serviceRegistry == null) {
            return;
        }
        ((StandardServiceRegistryImpl)serviceRegistry).destroy();
    }
}

