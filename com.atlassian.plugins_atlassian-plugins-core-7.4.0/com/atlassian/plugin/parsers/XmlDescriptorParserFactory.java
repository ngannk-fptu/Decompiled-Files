/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Application
 */
package com.atlassian.plugin.parsers;

import com.atlassian.plugin.Application;
import com.atlassian.plugin.parsers.DescriptorParser;
import com.atlassian.plugin.parsers.DescriptorParserFactory;
import com.atlassian.plugin.parsers.XmlDescriptorParser;
import java.io.InputStream;
import java.util.Set;

public class XmlDescriptorParserFactory
implements DescriptorParserFactory {
    @Override
    public DescriptorParser getInstance(InputStream source, Set<Application> applications) {
        return new XmlDescriptorParser(source, applications);
    }
}

