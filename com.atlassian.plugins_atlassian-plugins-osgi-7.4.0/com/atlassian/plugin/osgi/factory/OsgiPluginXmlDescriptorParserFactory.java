/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Application
 *  com.atlassian.plugin.parsers.CompositeDescriptorParserFactory
 *  com.atlassian.plugin.parsers.DescriptorParser
 *  com.atlassian.plugin.parsers.DescriptorParserFactory
 *  com.google.common.base.Preconditions
 */
package com.atlassian.plugin.osgi.factory;

import com.atlassian.plugin.Application;
import com.atlassian.plugin.osgi.factory.OsgiPluginXmlDescriptorParser;
import com.atlassian.plugin.parsers.CompositeDescriptorParserFactory;
import com.atlassian.plugin.parsers.DescriptorParser;
import com.atlassian.plugin.parsers.DescriptorParserFactory;
import com.google.common.base.Preconditions;
import java.io.InputStream;
import java.util.Set;

public class OsgiPluginXmlDescriptorParserFactory
implements DescriptorParserFactory,
CompositeDescriptorParserFactory {
    public DescriptorParser getInstance(InputStream source, Set<Application> applications) {
        return new OsgiPluginXmlDescriptorParser((InputStream)Preconditions.checkNotNull((Object)source, (Object)"The descriptor source must not be null"), applications);
    }

    public DescriptorParser getInstance(InputStream source, Iterable<InputStream> supplementalSources, Set<Application> applications) {
        return new OsgiPluginXmlDescriptorParser(source, supplementalSources, applications);
    }
}

