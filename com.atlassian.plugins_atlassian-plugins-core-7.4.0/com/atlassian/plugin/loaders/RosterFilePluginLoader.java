/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ReferenceMode
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.factories.PluginFactory
 */
package com.atlassian.plugin.loaders;

import com.atlassian.plugin.DefaultPluginArtifactFactory;
import com.atlassian.plugin.PluginArtifactFactory;
import com.atlassian.plugin.ReferenceMode;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.factories.PluginFactory;
import com.atlassian.plugin.loaders.RosterFileScanner;
import com.atlassian.plugin.loaders.ScanningPluginLoader;
import com.atlassian.plugin.util.EnumUtils;
import java.io.File;
import java.util.List;

public class RosterFilePluginLoader
extends ScanningPluginLoader {
    public static String getReferenceModePropertyName() {
        return RosterFilePluginLoader.class.getName() + ".referenceMode";
    }

    private static ReferenceMode referenceModeFromProperty() {
        String propertyName = RosterFilePluginLoader.getReferenceModePropertyName();
        return (ReferenceMode)EnumUtils.enumValueFromProperty((String)propertyName, (Enum[])ReferenceMode.values(), (Enum)ReferenceMode.FORBID_REFERENCE);
    }

    public RosterFilePluginLoader(File rosterFile, List<PluginFactory> pluginFactories, PluginEventManager pluginEventManager) {
        this(rosterFile, pluginFactories, RosterFilePluginLoader.referenceModeFromProperty(), pluginEventManager);
    }

    public RosterFilePluginLoader(File rosterFile, List<PluginFactory> pluginFactories, ReferenceMode referenceMode, PluginEventManager pluginEventManager) {
        this(rosterFile, pluginFactories, (PluginArtifactFactory)new DefaultPluginArtifactFactory(referenceMode), pluginEventManager);
    }

    public RosterFilePluginLoader(File rosterFile, List<PluginFactory> pluginFactories, PluginArtifactFactory pluginArtifactFactory, PluginEventManager pluginEventManager) {
        super(new RosterFileScanner(rosterFile), pluginFactories, pluginArtifactFactory, pluginEventManager);
    }
}

