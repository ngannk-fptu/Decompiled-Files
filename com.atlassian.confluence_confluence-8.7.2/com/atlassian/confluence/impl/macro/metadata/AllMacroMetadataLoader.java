/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.predicate.ModuleDescriptorPredicate
 *  com.atlassian.renderer.v2.macro.Macro
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.macro.metadata;

import com.atlassian.confluence.impl.macro.metadata.AllMacroMetadataCache;
import com.atlassian.confluence.impl.macro.metadata.AllMacroMetadataProvider;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.browser.MacroMetadataSource;
import com.atlassian.confluence.macro.browser.beans.MacroBody;
import com.atlassian.confluence.macro.browser.beans.MacroMetadata;
import com.atlassian.confluence.plugin.descriptor.CustomMacroModuleDescriptor;
import com.atlassian.confluence.plugin.descriptor.MacroFallbackParser;
import com.atlassian.confluence.plugin.descriptor.XhtmlMacroModuleDescriptor;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.predicate.ModuleDescriptorPredicate;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AllMacroMetadataLoader<T extends ModuleDescriptor<Macro> & MacroMetadataSource>
implements AllMacroMetadataProvider<T> {
    private static final Logger log = LoggerFactory.getLogger(AllMacroMetadataCache.class);
    private final PluginAccessor pluginAccessor;
    private final MacroFallbackParser macroFallbackParser;

    public AllMacroMetadataLoader(PluginAccessor pluginAccessor, MacroFallbackParser macroFallbackParser) {
        this.pluginAccessor = Objects.requireNonNull(pluginAccessor);
        this.macroFallbackParser = Objects.requireNonNull(macroFallbackParser);
    }

    @Override
    public @NonNull Map<String, MacroMetadata> apply(Class<T> descriptorClass, ModuleDescriptorPredicate<Macro> descriptorPredicate) {
        return this.loadMacroMetadata(descriptorClass, descriptorPredicate);
    }

    private @NonNull Map<String, MacroMetadata> loadMacroMetadata(Class<T> descriptorClass, ModuleDescriptorPredicate<Macro> descriptorPredicate) {
        HashMap macroMetadata = Maps.newHashMap();
        HashSet aliases = Sets.newHashSet();
        this.pluginAccessor.getEnabledModuleDescriptorsByClass(descriptorClass).stream().filter(x$0 -> descriptorPredicate.matches((ModuleDescriptor)x$0)).forEach(moduleDescriptor -> {
            try {
                MacroMetadata metadata = this.makeMetadata(moduleDescriptor);
                if (metadata != null) {
                    String macroName = metadata.getMacroName();
                    macroMetadata.put(macroName, metadata);
                    aliases.addAll(metadata.getAliases());
                }
            }
            catch (LinkageError e) {
                log.debug("Failed to make metadata for module '{}': {}", new Object[]{moduleDescriptor.getCompleteKey(), e.getMessage(), e});
            }
            catch (RuntimeException e) {
                log.warn("Failed to make metadata for module '{}': {}", new Object[]{moduleDescriptor.getCompleteKey(), e.getMessage(), e});
            }
        });
        aliases.forEach(macroMetadata::remove);
        return macroMetadata;
    }

    private @Nullable MacroMetadata makeMetadata(T macroModuleDescriptor) {
        String macroName = macroModuleDescriptor.getName();
        MacroMetadata metadata = ((MacroMetadataSource)macroModuleDescriptor).getMacroMetadata();
        if (metadata == null) {
            metadata = this.macroFallbackParser.getMetadata().get(macroName);
        }
        if (metadata != null && metadata.getFormDetails() != null) {
            MacroBody body = metadata.getFormDetails().getBody();
            Macro.BodyType bodyType = this.getMacroBodyType((ModuleDescriptor)macroModuleDescriptor);
            if (bodyType != null) {
                if (body == null) {
                    body = new MacroBody(metadata.getPluginKey(), macroName, false);
                    metadata.getFormDetails().setBody(body);
                }
                body.setBodyType(bodyType.toString());
            }
        }
        return metadata;
    }

    private @Nullable Macro.BodyType getMacroBodyType(ModuleDescriptor moduleDescriptor) {
        Macro.BodyType bodyType = null;
        if (moduleDescriptor instanceof CustomMacroModuleDescriptor) {
            com.atlassian.renderer.v2.macro.Macro v2Macro = ((CustomMacroModuleDescriptor)moduleDescriptor).getModule();
            if (!v2Macro.hasBody()) {
                bodyType = Macro.BodyType.NONE;
            }
        } else if (moduleDescriptor instanceof XhtmlMacroModuleDescriptor) {
            Macro xhtmlMacro = ((XhtmlMacroModuleDescriptor)moduleDescriptor).getModule();
            bodyType = xhtmlMacro.getBodyType();
        }
        return bodyType;
    }
}

