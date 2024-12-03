/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.predicate.ModuleDescriptorPredicate
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.macro.browser;

import com.atlassian.confluence.impl.macro.metadata.AllMacroMetadataProvider;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.browser.MacroMetadataProvider;
import com.atlassian.confluence.macro.browser.MacroMetadataSource;
import com.atlassian.confluence.macro.browser.beans.MacroMetadata;
import com.atlassian.confluence.macro.browser.beans.MacroSummary;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.predicate.ModuleDescriptorPredicate;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

public class DefaultMacroMetadataProvider<T extends ModuleDescriptor<Macro> & MacroMetadataSource>
implements MacroMetadataProvider {
    private final AllMacroMetadataProvider<T> allMacroMetadataProvider;
    private final Class<T> descriptorClass;
    private final ModuleDescriptorPredicate<Macro> descriptorPredicate;

    public DefaultMacroMetadataProvider(AllMacroMetadataProvider<T> macroMetadataCache, Class<T> descriptorClass) {
        this(macroMetadataCache, descriptorClass, (ModuleDescriptorPredicate<Macro>)((ModuleDescriptorPredicate)desc -> true));
    }

    public DefaultMacroMetadataProvider(AllMacroMetadataProvider<T> allMacroMetadataProvider, Class<T> descriptorClass, ModuleDescriptorPredicate<Macro> descriptorPredicate) {
        this.allMacroMetadataProvider = Objects.requireNonNull(allMacroMetadataProvider);
        this.descriptorClass = Objects.requireNonNull(descriptorClass);
        this.descriptorPredicate = Objects.requireNonNull(descriptorPredicate);
    }

    @Override
    public Collection<MacroMetadata> getData() {
        return this.getAllMacroMetadata().values();
    }

    @Override
    public Collection<MacroSummary> getSummaries() {
        return this.getData().stream().map(MacroMetadata::extractMacroSummary).collect(Collectors.toList());
    }

    @Override
    public MacroMetadata getByMacroName(String macroName) {
        return this.getAllMacroMetadata().get(macroName);
    }

    @Override
    public @Nullable MacroMetadata getByMacroNameAndId(String macroName, String alternateId) {
        MacroMetadata macroMetadata = this.getAllMacroMetadata().get(macroName);
        if (macroMetadata != null && StringUtils.equals((CharSequence)macroMetadata.getAlternateId(), (CharSequence)alternateId)) {
            return macroMetadata;
        }
        return null;
    }

    private Map<String, MacroMetadata> getAllMacroMetadata() {
        return (Map)this.allMacroMetadataProvider.apply(this.descriptorClass, this.descriptorPredicate);
    }
}

