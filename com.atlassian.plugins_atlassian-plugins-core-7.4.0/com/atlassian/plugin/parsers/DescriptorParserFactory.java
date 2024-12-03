/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Application
 */
package com.atlassian.plugin.parsers;

import com.atlassian.plugin.Application;
import com.atlassian.plugin.parsers.DescriptorParser;
import java.io.InputStream;
import java.util.Set;

public interface DescriptorParserFactory {
    public DescriptorParser getInstance(InputStream var1, Set<Application> var2);
}

