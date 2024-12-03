/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginArtifact
 */
package com.atlassian.confluence.ext.code.languages.impl;

import com.atlassian.confluence.ext.code.languages.Language;
import com.atlassian.plugin.PluginArtifact;
import java.io.IOException;
import java.io.Reader;

public interface PluginGenerator {
    public PluginArtifact createPluginForLanguage(Language var1, Reader var2) throws IOException;
}

