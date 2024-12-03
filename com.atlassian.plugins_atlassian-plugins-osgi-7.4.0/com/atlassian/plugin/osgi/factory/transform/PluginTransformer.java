/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginArtifact
 */
package com.atlassian.plugin.osgi.factory.transform;

import com.atlassian.plugin.PluginArtifact;
import com.atlassian.plugin.osgi.hostcomponents.HostComponentRegistration;
import java.io.File;
import java.util.List;

public interface PluginTransformer {
    public File transform(PluginArtifact var1, List<HostComponentRegistration> var2);
}

