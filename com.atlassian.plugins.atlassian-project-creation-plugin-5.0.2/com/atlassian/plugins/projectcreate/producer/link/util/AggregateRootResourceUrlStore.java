/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.google.common.base.Functions
 *  io.atlassian.fugue.Option
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.plugins.projectcreate.producer.link.util;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.google.common.base.Functions;
import io.atlassian.fugue.Option;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AggregateRootResourceUrlStore {
    private final PluginSettingsFactory pluginSettingsFactory;

    @Autowired
    public AggregateRootResourceUrlStore(@ComponentImport PluginSettingsFactory pluginSettingsFactory) {
        this.pluginSettingsFactory = pluginSettingsFactory;
    }

    public void storeResourceUrlForAggregateRoot(String resourceURL, String aggregateRootURL) {
        this.pluginSettingsFactory.createGlobalSettings().put(resourceURL, (Object)aggregateRootURL);
    }

    public Option<String> getAggregateRootUrlForResourceUrl(String resourceURL) {
        return Option.option((Object)this.pluginSettingsFactory.createGlobalSettings().get(resourceURL)).map((Function)Functions.toStringFunction());
    }
}

