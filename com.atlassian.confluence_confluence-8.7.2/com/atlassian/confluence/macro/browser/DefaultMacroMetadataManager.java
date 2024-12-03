/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventListenerRegistrar
 *  com.atlassian.plugin.PluginAccessor
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Maps
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.macro.browser;

import com.atlassian.confluence.event.events.plugin.AsyncPluginFrameworkStartedEvent;
import com.atlassian.confluence.macro.browser.MacroMetadataManager;
import com.atlassian.confluence.macro.browser.MacroMetadataProvider;
import com.atlassian.confluence.macro.browser.beans.MacroFormDetails;
import com.atlassian.confluence.macro.browser.beans.MacroMetadata;
import com.atlassian.confluence.macro.browser.beans.MacroParameter;
import com.atlassian.confluence.macro.browser.beans.MacroParameterType;
import com.atlassian.confluence.macro.browser.beans.MacroSummary;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventListenerRegistrar;
import com.atlassian.plugin.PluginAccessor;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultMacroMetadataManager
implements MacroMetadataManager {
    private static final Logger log = LoggerFactory.getLogger(DefaultMacroMetadataManager.class);
    private final PluginAccessor pluginAccessor;
    private final EventListenerRegistrar eventListenerRegistrar;

    public DefaultMacroMetadataManager(PluginAccessor pluginAccessor, EventListenerRegistrar eventListenerRegistrar) {
        this.pluginAccessor = (PluginAccessor)Preconditions.checkNotNull((Object)pluginAccessor);
        this.eventListenerRegistrar = (EventListenerRegistrar)Preconditions.checkNotNull((Object)eventListenerRegistrar);
    }

    @PostConstruct
    public void init() {
        if (this.eventListenerRegistrar != null) {
            this.eventListenerRegistrar.register((Object)this);
        }
    }

    @PreDestroy
    public void destroy() {
        if (this.eventListenerRegistrar != null) {
            this.eventListenerRegistrar.unregister((Object)this);
        }
    }

    @EventListener
    public void onPluginFrameworkStartedEvent(AsyncPluginFrameworkStartedEvent event) {
        this.buildMacroMetadata();
    }

    @Override
    public @NonNull Set<MacroMetadata> getAllMacroMetadata() {
        return this.buildMacroMetadata();
    }

    @Override
    public @NonNull Set<MacroSummary> getAllMacroSummaries() {
        return this.buildMacroSummaries();
    }

    @Override
    public MacroMetadata getMacroMetadataByName(String macroName) {
        return this.getMacroMetadataByNameAndId(macroName, null);
    }

    @Override
    public MacroMetadata getMacroMetadataByNameAndId(String macroName, String alternateId) {
        for (MacroMetadataProvider macroMetadataProvider : this.getMetadataProviders()) {
            MacroMetadata macroMetadata = macroMetadataProvider.getByMacroNameAndId(macroName, alternateId);
            if (macroMetadata == null) continue;
            return macroMetadata;
        }
        return null;
    }

    @Override
    public @NonNull Map<String, MacroParameterType> getParameterTypes(String macroName) {
        return Maps.transformValues(this.getParameters(macroName), MacroParameter::getType);
    }

    @Override
    public @NonNull Map<String, MacroParameter> getParameters(String macroName) {
        ImmutableMap.Builder macroParams = ImmutableMap.builder();
        MacroMetadata macroMetadata = this.getMacroMetadataByName(macroName);
        if (macroMetadata == null) {
            return Collections.emptyMap();
        }
        MacroFormDetails formDetails = macroMetadata.getFormDetails();
        if (formDetails == null) {
            return Collections.emptyMap();
        }
        List<MacroParameter> parameterMetadata = formDetails.getParameters();
        for (MacroParameter parameter : parameterMetadata) {
            macroParams.put((Object)parameter.getName(), (Object)parameter);
            if (parameter.getAliases() == null) continue;
            for (String parameterAlias : parameter.getAliases()) {
                macroParams.put((Object)parameterAlias, (Object)parameter);
            }
        }
        return macroParams.build();
    }

    private Set<MacroMetadata> buildMacroMetadata() {
        List<MacroMetadataProvider> macroMetadataProviders = this.getMetadataProviders();
        HashSet<MacroMetadata> datas = new HashSet<MacroMetadata>();
        for (MacroMetadataProvider macroMetadataProvider : macroMetadataProviders) {
            try {
                Collection<MacroMetadata> macroMetadataProviderData = macroMetadataProvider.getData();
                datas.addAll(macroMetadataProviderData);
            }
            catch (RuntimeException e) {
                log.error("Error getting data from MacroMetadataProvider,", (Throwable)e);
            }
        }
        return datas;
    }

    private Set<MacroSummary> buildMacroSummaries() {
        List<MacroMetadataProvider> macroMetadataProviders = this.getMetadataProviders();
        HashSet<MacroSummary> summaries = new HashSet<MacroSummary>();
        for (MacroMetadataProvider macroMetadataProvider : macroMetadataProviders) {
            try {
                Collection<MacroSummary> macroMetadataProviderSummaries = macroMetadataProvider.getSummaries();
                summaries.addAll(macroMetadataProviderSummaries);
            }
            catch (RuntimeException e) {
                log.error("Error getting summaries from MacroMetadataProvider,", (Throwable)e);
            }
        }
        return summaries;
    }

    private List<MacroMetadataProvider> getMetadataProviders() {
        return this.pluginAccessor.getEnabledModulesByClass(MacroMetadataProvider.class);
    }
}

