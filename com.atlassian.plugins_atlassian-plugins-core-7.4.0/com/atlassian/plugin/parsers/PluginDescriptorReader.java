/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Application
 *  com.atlassian.plugin.InstallationMode
 *  com.atlassian.plugin.Resources
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterables
 *  org.dom4j.Document
 *  org.dom4j.Element
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.parsers;

import com.atlassian.plugin.Application;
import com.atlassian.plugin.InstallationMode;
import com.atlassian.plugin.Resources;
import com.atlassian.plugin.parsers.ModuleReader;
import com.atlassian.plugin.parsers.PluginInformationReader;
import com.atlassian.plugin.parsers.XmlDescriptorParserUtils;
import com.atlassian.plugin.util.PluginUtils;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PluginDescriptorReader {
    private static final Logger log = LoggerFactory.getLogger(PluginDescriptorReader.class);
    static final String RESOURCE = "resource";
    private final Document descriptor;
    private final Set<Application> applications;

    public PluginDescriptorReader(Document descriptor, Set<Application> applications) {
        this.descriptor = XmlDescriptorParserUtils.removeAllNamespaces((Document)Preconditions.checkNotNull((Object)descriptor));
        this.applications = ImmutableSet.copyOf((Collection)((Collection)Preconditions.checkNotNull(applications)));
    }

    public Document getDescriptor() {
        return this.descriptor;
    }

    private Element getPluginElement() {
        return this.descriptor.getRootElement();
    }

    public String getPluginKey() {
        return this.getPluginElement().attributeValue("key");
    }

    public String getPluginName() {
        return this.getPluginElement().attributeValue("name");
    }

    public boolean isSystemPlugin() {
        return Boolean.valueOf(this.getPluginElement().attributeValue("system"));
    }

    public Optional<String> getI18nPluginNameKey() {
        return Optional.ofNullable(this.getPluginElement().attributeValue("i18n-name-key"));
    }

    public boolean isEnabledByDefault() {
        return !"disabled".equalsIgnoreCase(this.getPluginElement().attributeValue("state"));
    }

    public Optional<Element> getPluginInformation() {
        return PluginDescriptorReader.elements(this.getPluginElement()).stream().filter(Objects::nonNull).filter(element -> "plugin-info".equalsIgnoreCase(element.getName())).findFirst();
    }

    public PluginInformationReader getPluginInformationReader() {
        return new PluginInformationReader(this.getPluginInformation().orElse(null), this.applications, this.getPluginsVersion());
    }

    public Iterable<Element> getModules(InstallationMode installationMode) {
        return PluginDescriptorReader.elements(this.getPluginElement()).stream().filter(element -> {
            String name = element.getName();
            return !"plugin-info".equalsIgnoreCase(name) && !RESOURCE.equalsIgnoreCase(name);
        }).filter(module -> {
            if (!PluginUtils.doesModuleElementApplyToApplication(module, this.applications, installationMode)) {
                log.debug("Ignoring module descriptor for this application: {}", (Object)module.attributeValue("key"));
                return false;
            }
            return true;
        }).collect(Collectors.toList());
    }

    public Iterable<ModuleReader> getModuleReaders(InstallationMode installationMode) {
        return Iterables.transform(this.getModules(installationMode), ModuleReader::new);
    }

    public Resources getResources() {
        return Resources.fromXml((Element)this.getPluginElement());
    }

    public int getPluginsVersion() {
        String val = this.getPluginElement().attributeValue("pluginsVersion");
        if (val == null) {
            val = this.getPluginElement().attributeValue("plugins-version");
        }
        if (val != null) {
            try {
                return Integer.parseInt(val);
            }
            catch (NumberFormatException e) {
                throw new RuntimeException("Could not parse pluginsVersion: " + e.getMessage(), e);
            }
        }
        return 1;
    }

    static List<Element> elements(Element e) {
        return e.elements();
    }

    static List<Element> elements(Element e, String name) {
        return e != null ? e.elements(name) : ImmutableList.of();
    }
}

