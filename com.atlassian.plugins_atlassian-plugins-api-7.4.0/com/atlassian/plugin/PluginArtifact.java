/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin;

import com.atlassian.plugin.ReferenceMode;
import java.io.File;
import java.io.InputStream;
import java.util.Set;

public interface PluginArtifact {
    public boolean doesResourceExist(String var1);

    public InputStream getResourceAsStream(String var1);

    public String getName();

    public InputStream getInputStream();

    public File toFile();

    public boolean containsJavaExecutableCode();

    public boolean containsSpringContext();

    public ReferenceMode getReferenceMode();

    public static interface HasExtraModuleDescriptors {
        public Set<String> extraModuleDescriptorFiles(String var1);
    }
}

