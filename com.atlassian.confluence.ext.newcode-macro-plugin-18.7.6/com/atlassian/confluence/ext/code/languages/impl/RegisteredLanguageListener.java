/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.elements.ResourceDescriptor
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.tracker.DefaultPluginModuleTracker
 *  com.atlassian.plugin.tracker.PluginModuleTracker
 *  com.atlassian.plugin.tracker.PluginModuleTracker$Customizer
 *  com.atlassian.plugin.webresource.WebResourceModuleDescriptor
 *  com.google.common.collect.Maps
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.ext.code.languages.impl;

import com.atlassian.confluence.ext.code.descriptor.custom.CustomCodeSyntax;
import com.atlassian.confluence.ext.code.descriptor.custom.CustomCodeSyntaxModuleDescriptor;
import com.atlassian.confluence.ext.code.languages.DuplicateLanguageException;
import com.atlassian.confluence.ext.code.languages.InvalidLanguageException;
import com.atlassian.confluence.ext.code.languages.LanguageParser;
import com.atlassian.confluence.ext.code.languages.LanguageRegistry;
import com.atlassian.confluence.ext.code.languages.impl.RegisteredLanguage;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.elements.ResourceDescriptor;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.tracker.DefaultPluginModuleTracker;
import com.atlassian.plugin.tracker.PluginModuleTracker;
import com.atlassian.plugin.webresource.WebResourceModuleDescriptor;
import com.google.common.collect.Maps;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RegisteredLanguageListener
implements DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(RegisteredLanguageListener.class);
    private final LanguageRegistry languageRegistry;
    private final LanguageParser languageParser;
    private final PluginModuleTracker<CustomCodeSyntax, CustomCodeSyntaxModuleDescriptor> moduleTracker;

    @Autowired
    public RegisteredLanguageListener(LanguageRegistry languageRegistry, @ComponentImport PluginAccessor pluginAccessor, @ComponentImport PluginEventManager pluginEventManager, LanguageParser languageParser) {
        this.languageRegistry = languageRegistry;
        this.languageParser = languageParser;
        this.moduleTracker = new DefaultPluginModuleTracker(pluginAccessor, pluginEventManager, CustomCodeSyntaxModuleDescriptor.class, (PluginModuleTracker.Customizer)new Customizer());
    }

    public void destroy() {
        this.moduleTracker.close();
    }

    private class Customizer
    implements PluginModuleTracker.Customizer<CustomCodeSyntax, CustomCodeSyntaxModuleDescriptor> {
        private final Map<String, String> moduleKeyMap = Maps.newHashMap();

        private Customizer() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public CustomCodeSyntaxModuleDescriptor adding(CustomCodeSyntaxModuleDescriptor descriptor) {
            log.debug(String.format("Handling registration of new CustomCodeSyntaxModuleDescriptor %s", descriptor.getCompleteKey()));
            CustomCodeSyntax module = descriptor.getModule();
            Plugin otherPlugin = descriptor.getPlugin();
            ModuleDescriptor moduleDescriptor = otherPlugin.getModuleDescriptor(module.getResourceKey());
            if (!(moduleDescriptor instanceof WebResourceModuleDescriptor)) {
                log.error("Failed to register new code macro language " + descriptor.getCompleteKey() + " because its related resource key was not a web-resource plugin module.");
                return descriptor;
            }
            List resourceDescriptors = moduleDescriptor.getResourceDescriptors();
            if (resourceDescriptors.size() == 0) {
                log.error("Failed to register new code macro language " + descriptor.getCompleteKey() + " because its related web-resource module " + moduleDescriptor.getCompleteKey() + " had no declared resources.");
                return descriptor;
            }
            if (resourceDescriptors.size() > 1) {
                log.warn("More than one declared resource found in web-resource module " + moduleDescriptor.getCompleteKey() + " a single resource will be selected non-deterministically.");
                return descriptor;
            }
            ResourceDescriptor resourceDescriptor = (ResourceDescriptor)resourceDescriptors.get(0);
            ClassLoader classLoader = otherPlugin.getClassLoader();
            InputStream brushFile = null;
            try {
                RegisteredLanguage language;
                InputStreamReader reader;
                brushFile = classLoader.getResourceAsStream(resourceDescriptor.getLocation());
                try {
                    reader = new InputStreamReader(brushFile, "utf-8");
                }
                catch (UnsupportedEncodingException e) {
                    log.error("Failed to read input stream from code module in plugin " + otherPlugin.getKey() + ": " + e.getMessage(), (Throwable)e);
                    CustomCodeSyntaxModuleDescriptor customCodeSyntaxModuleDescriptor = descriptor;
                    if (brushFile != null) {
                        IOUtils.closeQuietly((InputStream)brushFile);
                    }
                    return customCodeSyntaxModuleDescriptor;
                }
                try {
                    language = RegisteredLanguageListener.this.languageParser.parseRegisteredLanguage(reader, module.getFriendlyName());
                    language.setWebResource(moduleDescriptor.getCompleteKey());
                }
                catch (InvalidLanguageException e) {
                    log.error("Language file from plugin " + otherPlugin.getKey() + " was invalid. Skipping. " + e.getMessage(), (Throwable)e);
                    CustomCodeSyntaxModuleDescriptor customCodeSyntaxModuleDescriptor = descriptor;
                    if (brushFile != null) {
                        IOUtils.closeQuietly((InputStream)brushFile);
                    }
                    return customCodeSyntaxModuleDescriptor;
                }
                try {
                    RegisteredLanguageListener.this.languageRegistry.addLanguage(language);
                    this.moduleKeyMap.put(descriptor.getCompleteKey(), language.getName());
                }
                catch (DuplicateLanguageException e) {
                    log.error("Failed to register new language " + language.getName() + " it or one of its aliases is already registered: " + e.getMessage(), (Throwable)e);
                    CustomCodeSyntaxModuleDescriptor customCodeSyntaxModuleDescriptor = descriptor;
                    if (brushFile != null) {
                        IOUtils.closeQuietly((InputStream)brushFile);
                    }
                    return customCodeSyntaxModuleDescriptor;
                }
            }
            finally {
                if (brushFile != null) {
                    IOUtils.closeQuietly((InputStream)brushFile);
                }
            }
            return descriptor;
        }

        public void removed(CustomCodeSyntaxModuleDescriptor descriptor) {
            log.debug("Handling possible code syntax removal for disabled plugin module " + descriptor.getCompleteKey());
            String pluginModuleKey = descriptor.getCompleteKey();
            String languageKey = this.moduleKeyMap.get(pluginModuleKey);
            if (!StringUtils.isBlank((CharSequence)languageKey)) {
                log.info("Removing registered language " + languageKey);
                RegisteredLanguageListener.this.languageRegistry.unregisterLanguage(languageKey);
                this.moduleKeyMap.remove(pluginModuleKey);
            }
        }
    }
}

