/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.impresence2;

import com.atlassian.confluence.extra.impresence2.PresenceManager;
import com.atlassian.confluence.extra.impresence2.PresenceReporterModuleDescriptor;
import com.atlassian.confluence.extra.impresence2.reporter.PresenceReporter;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultPresenceManager
implements PresenceManager {
    private final PluginAccessor pluginAccessor;

    @Autowired
    public DefaultPresenceManager(@ComponentImport PluginAccessor pluginAccessor) {
        this.pluginAccessor = pluginAccessor;
    }

    @Override
    public PresenceReporter getReporter(String key) {
        return this.getPresenceReportersMap().get(key);
    }

    private Map<String, PresenceReporter> getPresenceReportersMap() {
        List dictionaryModuleDescriptors = this.pluginAccessor.getEnabledModuleDescriptorsByClass(PresenceReporterModuleDescriptor.class);
        HashMap<String, PresenceReporter> presenceReporters = new HashMap<String, PresenceReporter>();
        for (PresenceReporterModuleDescriptor presenceReporterModuleDescriptor : dictionaryModuleDescriptors) {
            PresenceReporter module = presenceReporterModuleDescriptor.getModule();
            if (presenceReporters.containsKey(module.getKey())) continue;
            presenceReporters.put(module.getKey(), module);
        }
        return presenceReporters;
    }

    @Override
    public Collection<PresenceReporter> getReporters() {
        return this.getPresenceReportersMap().values();
    }
}

