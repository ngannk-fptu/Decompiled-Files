/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Application
 *  com.atlassian.plugin.InstallationMode
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.ModuleDescriptorFactory
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginInformation
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.PluginPermission
 *  com.atlassian.plugin.Resourced
 *  com.atlassian.security.xml.SecureXmlParserFactory
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  com.google.common.collect.Iterables
 *  org.apache.commons.lang3.StringUtils
 *  org.dom4j.Document
 *  org.dom4j.Element
 *  org.dom4j.Node
 *  org.dom4j.io.DOMReader
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.parsers;

import com.atlassian.plugin.Application;
import com.atlassian.plugin.InstallationMode;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.ModuleDescriptorFactory;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginInformation;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.PluginPermission;
import com.atlassian.plugin.Resourced;
import com.atlassian.plugin.descriptors.UnloadableModuleDescriptor;
import com.atlassian.plugin.descriptors.UnloadableModuleDescriptorFactory;
import com.atlassian.plugin.impl.UnloadablePluginFactory;
import com.atlassian.plugin.parsers.DescriptorParser;
import com.atlassian.plugin.parsers.PluginDescriptorReader;
import com.atlassian.plugin.parsers.PluginInformationReader;
import com.atlassian.plugin.parsers.XmlDescriptorParserUtils;
import com.atlassian.security.xml.SecureXmlParserFactory;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.DOMReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XmlDescriptorParser
implements DescriptorParser {
    private static final Logger log = LoggerFactory.getLogger(XmlDescriptorParser.class);
    private final PluginDescriptorReader descriptorReader;

    public XmlDescriptorParser(Document source, Set<Application> applications) {
        this.descriptorReader = new PluginDescriptorReader((Document)Preconditions.checkNotNull((Object)source, (Object)"XML descriptor source document cannot be null"), (Set)Preconditions.checkNotNull(applications));
    }

    public XmlDescriptorParser(InputStream source, Set<Application> applications) {
        this(XmlDescriptorParser.createDocument((InputStream)Preconditions.checkNotNull((Object)source, (Object)"XML descriptor source cannot be null")), applications);
    }

    public XmlDescriptorParser(InputStream source, Iterable<InputStream> supplementalSources, Set<Application> applications) {
        Preconditions.checkNotNull((Object)source, (Object)"XML descriptor source cannot be null");
        Preconditions.checkNotNull(supplementalSources, (Object)"Supplemental XML descriptors cannot be null");
        Document mainDescriptor = XmlDescriptorParser.createDocument(source);
        Iterable supplementalDocs = Iterables.transform(supplementalSources, XmlDescriptorParser::createDocument);
        mainDescriptor = XmlDescriptorParser.mergeDocuments(mainDescriptor, supplementalDocs);
        this.descriptorReader = new PluginDescriptorReader(mainDescriptor, (Set)Preconditions.checkNotNull(applications));
    }

    protected static Document createDocument(InputStream source) {
        DocumentBuilder documentBuilder = SecureXmlParserFactory.newNamespaceAwareDocumentBuilder();
        documentBuilder.setErrorHandler(NoopErrorHandler.INSTANCE);
        try {
            org.w3c.dom.Document document = documentBuilder.parse(new InputSource(source));
            document.normalize();
            return new DOMReader().read(document);
        }
        catch (IOException | SAXException e) {
            throw new PluginParseException("Cannot parse XML plugin descriptor", (Throwable)e);
        }
    }

    protected static Document mergeDocuments(Document mainDocument, Iterable<Document> supplementalDocuments) {
        Element mainRootElement = mainDocument.getRootElement();
        for (Document supplementalDocument : supplementalDocuments) {
            Element supplementaryRoot = supplementalDocument.getRootElement();
            Iterator iter = supplementaryRoot.content().iterator();
            while (iter.hasNext()) {
                Node node = (Node)iter.next();
                iter.remove();
                mainRootElement.add(node);
            }
        }
        return mainDocument;
    }

    protected Document getDocument() {
        return this.descriptorReader.getDescriptor();
    }

    @Override
    public Plugin configurePlugin(ModuleDescriptorFactory moduleDescriptorFactory, Plugin plugin) {
        plugin.setName(this.descriptorReader.getPluginName());
        plugin.setKey(this.getKey());
        plugin.setPluginsVersion(this.getPluginsVersion());
        plugin.setSystemPlugin(this.isSystemPlugin());
        plugin.setI18nNameKey(this.descriptorReader.getI18nPluginNameKey().orElseGet(() -> ((Plugin)plugin).getI18nNameKey()));
        if (plugin.getKey().indexOf(58) > 0) {
            throw new PluginParseException("Plugin keys cannot contain ':'. Key is '" + plugin.getKey() + "'");
        }
        plugin.setEnabledByDefault(this.descriptorReader.isEnabledByDefault());
        plugin.setResources((Resourced)this.descriptorReader.getResources());
        plugin.setPluginInformation(this.createPluginInformation());
        for (Element module : this.descriptorReader.getModules(plugin.getInstallationMode())) {
            ModuleDescriptor<?> moduleDescriptor = this.createModuleDescriptor(plugin, module, moduleDescriptorFactory);
            if (moduleDescriptor == null) continue;
            if (plugin.getModuleDescriptor(moduleDescriptor.getKey()) != null) {
                throw new PluginParseException("Found duplicate key '" + moduleDescriptor.getKey() + "' within plugin '" + plugin.getKey() + "'");
            }
            plugin.addModuleDescriptor(moduleDescriptor);
            if (!(moduleDescriptor instanceof UnloadableModuleDescriptor)) continue;
            log.error("There were errors loading the plugin '{}' of version '{}'. The plugin has been disabled.", (Object)plugin.getName(), (Object)plugin.getPluginInformation().getVersion());
            return UnloadablePluginFactory.createUnloadablePlugin(plugin);
        }
        return plugin;
    }

    @Override
    public ModuleDescriptor<?> addModule(ModuleDescriptorFactory moduleDescriptorFactory, Plugin plugin, Element module) {
        return XmlDescriptorParserUtils.addModule(moduleDescriptorFactory, plugin, module);
    }

    protected ModuleDescriptor<?> createModuleDescriptor(Plugin plugin, Element element, ModuleDescriptorFactory moduleDescriptorFactory) {
        String name = element.getName();
        ModuleDescriptor<?> moduleDescriptor = XmlDescriptorParserUtils.newModuleDescriptor(plugin, element, moduleDescriptorFactory);
        if (moduleDescriptor == null) {
            log.info("The module '{}' in plugin '{}' is in the list of excluded module descriptors, so not enabling.", (Object)name, (Object)plugin.getName());
            return null;
        }
        try {
            moduleDescriptor.init(plugin, element);
        }
        catch (Exception e) {
            UnloadableModuleDescriptor descriptor = UnloadableModuleDescriptorFactory.createUnloadableModuleDescriptor(plugin, element, e, moduleDescriptorFactory);
            log.error("There were problems loading the module '{}'. The module and its plugin ('{}' of version '{}') have been disabled.", new Object[]{name, plugin.getName(), plugin.getPluginInformation().getVersion()});
            log.error(descriptor.getErrorText(), (Throwable)e);
            return descriptor;
        }
        return moduleDescriptor;
    }

    protected PluginInformation createPluginInformation() {
        PluginInformationReader pluginInformationReader = this.descriptorReader.getPluginInformationReader();
        PluginInformation pluginInfo = new PluginInformation();
        pluginInfo.setDescription(pluginInformationReader.getDescription().orElseGet(() -> ((PluginInformation)pluginInfo).getDescription()));
        pluginInfo.setDescriptionKey(pluginInformationReader.getDescriptionKey().orElseGet(() -> ((PluginInformation)pluginInfo).getDescriptionKey()));
        pluginInfo.setVersion(pluginInformationReader.getVersion().orElseGet(() -> ((PluginInformation)pluginInfo).getVersion()));
        pluginInfo.setVendorName(pluginInformationReader.getVendorName().orElseGet(() -> ((PluginInformation)pluginInfo).getVendorName()));
        pluginInfo.setVendorUrl(pluginInformationReader.getVendorUrl().orElseGet(() -> ((PluginInformation)pluginInfo).getVendorUrl()));
        pluginInfo.setScopeKey(pluginInformationReader.getScopeKey());
        for (Map.Entry<String, String> param : pluginInformationReader.getParameters().entrySet()) {
            pluginInfo.addParameter(param.getKey(), param.getValue());
        }
        pluginInfo.setMinJavaVersion(pluginInformationReader.getMinJavaVersion().orElseGet(() -> ((PluginInformation)pluginInfo).getMinJavaVersion()));
        pluginInfo.setStartup(pluginInformationReader.getStartup().orElseGet(() -> ((PluginInformation)pluginInfo).getStartup()));
        pluginInfo.setModuleScanFolders(pluginInformationReader.getModuleScanFolders());
        Map<String, Optional<String>> readPermissions = pluginInformationReader.getPermissions();
        if (pluginInformationReader.hasAllPermissions()) {
            pluginInfo.setPermissions((Set)ImmutableSet.of((Object)PluginPermission.ALL));
        } else {
            ImmutableSet.Builder permissions = ImmutableSet.builder();
            for (Map.Entry<String, Optional<String>> permission : readPermissions.entrySet()) {
                String permissionKey = permission.getKey();
                Optional<String> readInstallationMode = permission.getValue();
                Optional installationMode = readInstallationMode.flatMap(InstallationMode::of);
                if (StringUtils.isNotBlank((CharSequence)readInstallationMode.orElse(null)) && !installationMode.isPresent()) {
                    log.warn("The parsed installation mode '{}' for permission '{}' didn't match any of the valid values: {}", new Object[]{readInstallationMode, permission.getKey(), Iterables.transform((Iterable)ImmutableList.copyOf((Object[])InstallationMode.values()), InstallationMode::getKey)});
                }
                permissions.add((Object)new PluginPermission(permissionKey, (InstallationMode)installationMode.orElse(null)));
            }
            pluginInfo.setPermissions((Set)permissions.build());
        }
        return pluginInfo;
    }

    @Override
    public String getKey() {
        return this.descriptorReader.getPluginKey();
    }

    @Override
    public int getPluginsVersion() {
        return this.descriptorReader.getPluginsVersion();
    }

    @Override
    public PluginInformation getPluginInformation() {
        return this.createPluginInformation();
    }

    @Override
    public boolean isSystemPlugin() {
        return this.descriptorReader.isSystemPlugin();
    }

    private static class NoopErrorHandler
    implements ErrorHandler {
        static final NoopErrorHandler INSTANCE = new NoopErrorHandler();

        private NoopErrorHandler() {
        }

        @Override
        public void warning(SAXParseException exception) {
        }

        @Override
        public void error(SAXParseException exception) {
        }

        @Override
        public void fatalError(SAXParseException exception) {
        }
    }
}

