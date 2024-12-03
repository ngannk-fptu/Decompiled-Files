/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.PluginAccessor
 */
package com.atlassian.confluence.internal.index.attachment;

import com.atlassian.confluence.index.attachment.AttachmentTextExtractor;
import com.atlassian.confluence.plugin.descriptor.AttachmentTextExtractorModuleDescriptor;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.PluginAccessor;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class PluginAttachmentTextExtractorsProvider
implements Supplier<Stream<AttachmentTextExtractor>> {
    private final PluginAccessor pluginAccessor;

    public PluginAttachmentTextExtractorsProvider(PluginAccessor pluginAccessor) {
        this.pluginAccessor = Objects.requireNonNull(pluginAccessor);
    }

    @Override
    public Stream<AttachmentTextExtractor> get() {
        return this.pluginAccessor.getEnabledModuleDescriptorsByClass(AttachmentTextExtractorModuleDescriptor.class).stream().sorted().map(ModuleDescriptor::getModule);
    }
}

