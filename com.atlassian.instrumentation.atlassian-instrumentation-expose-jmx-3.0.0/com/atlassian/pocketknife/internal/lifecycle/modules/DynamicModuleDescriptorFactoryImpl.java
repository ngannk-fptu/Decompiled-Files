/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.ModuleDescriptorFactory
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.UnloadableModuleDescriptor
 *  com.atlassian.plugin.descriptors.UnloadableModuleDescriptorFactory
 *  com.atlassian.plugin.descriptors.UnrecognisedModuleDescriptor
 *  com.atlassian.plugin.descriptors.UnrecognisedModuleDescriptorFactory
 *  com.google.common.collect.Lists
 *  org.dom4j.Element
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Service
 */
package com.atlassian.pocketknife.internal.lifecycle.modules;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.ModuleDescriptorFactory;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.UnloadableModuleDescriptor;
import com.atlassian.plugin.descriptors.UnloadableModuleDescriptorFactory;
import com.atlassian.plugin.descriptors.UnrecognisedModuleDescriptor;
import com.atlassian.plugin.descriptors.UnrecognisedModuleDescriptorFactory;
import com.atlassian.pocketknife.api.lifecycle.modules.DynamicModuleDescriptorFactory;
import com.atlassian.pocketknife.api.lifecycle.modules.LoaderConfiguration;
import com.atlassian.pocketknife.api.lifecycle.modules.ModuleRegistrationHandle;
import com.atlassian.pocketknife.api.lifecycle.modules.ResourceLoader;
import com.atlassian.pocketknife.internal.lifecycle.modules.CombinedModuleDescriptorFactoryProvider;
import com.atlassian.pocketknife.internal.lifecycle.modules.DynamicModuleRegistration;
import com.atlassian.pocketknife.internal.lifecycle.modules.GhettoCode;
import com.atlassian.pocketknife.internal.lifecycle.modules.Kit;
import com.atlassian.pocketknife.internal.lifecycle.modules.PluginDescriptorReader;
import com.google.common.collect.Lists;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DynamicModuleDescriptorFactoryImpl
implements DynamicModuleDescriptorFactory {
    private static final Logger log = LoggerFactory.getLogger(DynamicModuleDescriptorFactoryImpl.class);
    private final DynamicModuleRegistration dynamicModuleRegistration;
    private final CombinedModuleDescriptorFactoryProvider combinedModuleDescriptorFactoryProvider;

    @Autowired
    public DynamicModuleDescriptorFactoryImpl(DynamicModuleRegistration dynamicModuleRegistration, CombinedModuleDescriptorFactoryProvider combinedModuleDescriptorFactoryProvider) {
        this.dynamicModuleRegistration = dynamicModuleRegistration;
        this.combinedModuleDescriptorFactoryProvider = combinedModuleDescriptorFactoryProvider;
    }

    @Override
    public ModuleRegistrationHandle loadModules(Plugin plugin, String ... pathsToAuxAtlassianPluginXMLs) {
        LoaderConfiguration loaderConfiguration = new LoaderConfiguration(plugin);
        loaderConfiguration.addPathsToAuxAtlassianPluginXMLs(pathsToAuxAtlassianPluginXMLs);
        return this.loadModules(loaderConfiguration);
    }

    @Override
    public ModuleRegistrationHandle loadModules(Plugin plugin, ResourceLoader resourceLoader, String ... pathsToAuxAtlassianPluginXMLs) {
        LoaderConfiguration loaderConfiguration = new LoaderConfiguration(plugin);
        loaderConfiguration.setResourceLoader(resourceLoader);
        loaderConfiguration.addPathsToAuxAtlassianPluginXMLs(pathsToAuxAtlassianPluginXMLs);
        return this.loadModules(loaderConfiguration);
    }

    @Override
    public ModuleRegistrationHandle loadModules(LoaderConfiguration loaderConfiguration) {
        ArrayList modules = Lists.newArrayList();
        ModuleDescriptorFactory moduleDescriptorFactory = this.combinedModuleDescriptorFactoryProvider.getModuleDescriptorFactory();
        for (String auxAtlassianPluginXML : loaderConfiguration.getPathsToAuxAtlassianPluginXMLs()) {
            PluginDescriptorReader descriptorReader = this.getPluginDescriptorReader(loaderConfiguration.getResourceLoader(), auxAtlassianPluginXML);
            for (Element moduleElement : descriptorReader.getModules()) {
                this.loadModulesHelper(loaderConfiguration, moduleElement, moduleDescriptorFactory, modules);
            }
        }
        return this.dynamicModuleRegistration.registerDescriptors(loaderConfiguration.getPlugin(), modules);
    }

    @Override
    public ModuleRegistrationHandle loadModules(Plugin plugin, Element element) {
        ArrayList modules = Lists.newArrayList();
        ModuleDescriptorFactory moduleDescriptorFactory = this.combinedModuleDescriptorFactoryProvider.getModuleDescriptorFactory();
        this.loadModulesHelper(new LoaderConfiguration(plugin), element, moduleDescriptorFactory, modules);
        return this.dynamicModuleRegistration.registerDescriptors(plugin, modules);
    }

    private void loadModulesHelper(LoaderConfiguration loaderConfiguration, Element moduleElement, ModuleDescriptorFactory moduleDescriptorFactory, List<ModuleDescriptor> modules) {
        String moduleType = moduleElement.getName();
        String moduleKey = Kit.getModuleIdentifier(moduleElement);
        String pluginId = Kit.pluginIdentifier(loaderConfiguration.getPlugin());
        ModuleDescriptor<?> moduleDescriptor = this.createModuleDescriptor(loaderConfiguration, moduleType, moduleElement, moduleDescriptorFactory);
        if (moduleDescriptor == null) {
            log.error(String.format("Skipping the module '%s' with key '%s' in plugin '%s'. Null was returned from the module descriptor factory...", moduleType, moduleKey, pluginId));
            return;
        }
        if (moduleDescriptor instanceof UnloadableModuleDescriptor) {
            log.error(String.format("There were problems loading the module '%s' with key '%s' in plugin '%s'. UnloadableModuleDescriptor returned.", moduleType, moduleKey, pluginId));
        } else {
            log.info(String.format("Loaded module '%s' with key '%s' in plugin '%s'.", moduleType, moduleKey, pluginId));
            modules.add(moduleDescriptor);
        }
    }

    protected ModuleDescriptor<?> createModuleDescriptor(LoaderConfiguration loaderConfiguration, String moduleType, Element element, ModuleDescriptorFactory moduleDescriptorFactory) throws PluginParseException {
        ModuleDescriptor moduleDescriptor;
        String moduleIdentifier = Kit.getModuleIdentifier(element);
        String pluginId = Kit.pluginIdentifier(loaderConfiguration.getPlugin());
        try {
            log.info(String.format("Creating module of type '%s' with key '%s' in plugin '%s'", moduleType, moduleIdentifier, pluginId));
            moduleDescriptor = moduleDescriptorFactory.getModuleDescriptor(moduleType);
            if (moduleDescriptor != null) {
                log.info(String.format("Successfully created module as type '%s'", moduleDescriptor.getClass().getName()));
            }
        }
        catch (Throwable e) {
            UnrecognisedModuleDescriptor descriptor = UnrecognisedModuleDescriptorFactory.createUnrecognisedModuleDescriptor((Plugin)loaderConfiguration.getPlugin(), (Element)element, (Throwable)e, (ModuleDescriptorFactory)moduleDescriptorFactory);
            log.error(String.format("There were problems loading the module '%s' with key '%s' in plugin '%s'. The module has been disabled.", moduleType, moduleIdentifier, pluginId));
            log.error(descriptor.getErrorText(), e);
            return descriptor;
        }
        if (moduleDescriptor == null) {
            log.info(String.format("The module '%s' with key '%s' in plugin '%s' is in the list of excluded module descriptors, so not enabling.", moduleType, moduleIdentifier, pluginId));
            return null;
        }
        if (moduleDescriptor.getKey() != null && loaderConfiguration.getPlugin().getModuleDescriptor(moduleDescriptor.getKey()) != null) {
            if (loaderConfiguration.isFailOnDuplicateKey()) {
                throw new PluginParseException("Found duplicate key '" + Kit.getModuleIdentifier(element) + "' within plugin '" + pluginId + "'");
            }
            log.warn(String.format("Found duplicate key '%s' within plugin '%s', but ignoring this and moving on.....", Kit.getModuleIdentifier(element), pluginId));
            return null;
        }
        try {
            log.info(String.format("Calling init on module of type '%s' with key '%s' in plugin '%s'", moduleType, moduleIdentifier, pluginId));
            moduleDescriptor.init(loaderConfiguration.getPlugin(), element);
        }
        catch (Exception e) {
            UnloadableModuleDescriptor descriptor = UnloadableModuleDescriptorFactory.createUnloadableModuleDescriptor((Plugin)loaderConfiguration.getPlugin(), (Element)element, (Throwable)e, (ModuleDescriptorFactory)moduleDescriptorFactory);
            log.error(String.format("There were problems loading the module '%s' with key '%s'. The module and its plugin have been disabled.", moduleType, moduleIdentifier));
            log.error(descriptor.getErrorText(), (Throwable)e);
            return descriptor;
        }
        GhettoCode.addModuleDescriptorElement(loaderConfiguration.getPlugin(), element, element.attributeValue("key"));
        return moduleDescriptor;
    }

    private PluginDescriptorReader getPluginDescriptorReader(ResourceLoader resourceLoader, String auxAtlassianPluginXML) {
        InputStream auxXML = this.readXML(resourceLoader, auxAtlassianPluginXML);
        if (auxXML == null) {
            throw new PluginParseException("Unable to get InputStream for '" + auxAtlassianPluginXML + "'");
        }
        return PluginDescriptorReader.createDescriptorReader(auxXML);
    }

    private InputStream readXML(ResourceLoader resourceLoader, String pathToAuxPluginsXML) {
        log.info(String.format("Reading module xml %s", pathToAuxPluginsXML));
        return resourceLoader.getResourceAsStream(pathToAuxPluginsXML);
    }
}

