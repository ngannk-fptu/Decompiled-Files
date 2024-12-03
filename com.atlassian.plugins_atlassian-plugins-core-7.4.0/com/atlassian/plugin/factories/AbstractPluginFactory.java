/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Application
 *  com.atlassian.plugin.PluginArtifact
 *  com.atlassian.plugin.factories.PluginFactory
 *  com.google.common.base.Preconditions
 *  org.apache.commons.io.IOUtils
 */
package com.atlassian.plugin.factories;

import com.atlassian.plugin.Application;
import com.atlassian.plugin.PluginArtifact;
import com.atlassian.plugin.factories.PluginFactory;
import com.atlassian.plugin.parsers.DescriptorParser;
import com.atlassian.plugin.parsers.DescriptorParserFactory;
import com.google.common.base.Preconditions;
import java.io.InputStream;
import java.util.Set;
import java.util.function.Predicate;
import org.apache.commons.io.IOUtils;

public abstract class AbstractPluginFactory
implements PluginFactory {
    protected final DescriptorParserFactory descriptorParserFactory;
    protected final Set<Application> applications;

    protected AbstractPluginFactory(DescriptorParserFactory descriptorParserFactory, Set<Application> applications) {
        this.descriptorParserFactory = (DescriptorParserFactory)Preconditions.checkNotNull((Object)descriptorParserFactory);
        this.applications = (Set)Preconditions.checkNotNull(applications);
    }

    public String canCreate(PluginArtifact pluginArtifact) {
        return this.getPluginKeyFromDescriptor(pluginArtifact);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected final boolean hasDescriptor(PluginArtifact pluginArtifact) {
        InputStream descriptorStream = null;
        try {
            descriptorStream = this.getDescriptorInputStream(pluginArtifact);
            boolean bl = descriptorStream != null;
            return bl;
        }
        finally {
            IOUtils.closeQuietly((InputStream)descriptorStream);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected final String getPluginKeyFromDescriptor(PluginArtifact pluginArtifact) {
        String pluginKey = null;
        InputStream descriptorStream = null;
        try {
            descriptorStream = this.getDescriptorInputStream(pluginArtifact);
            if (descriptorStream != null) {
                DescriptorParser descriptorParser = this.descriptorParserFactory.getInstance(descriptorStream, this.applications);
                if (this.isValidPluginsVersion().test(descriptorParser.getPluginsVersion())) {
                    pluginKey = descriptorParser.getKey();
                }
            }
        }
        finally {
            IOUtils.closeQuietly((InputStream)descriptorStream);
        }
        return pluginKey;
    }

    protected abstract InputStream getDescriptorInputStream(PluginArtifact var1);

    protected abstract Predicate<Integer> isValidPluginsVersion();
}

