/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.hostcontainer.HostContainer
 *  com.atlassian.webresource.api.data.WebResourceDataProvider
 *  com.google.common.base.Function
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 *  org.dom4j.Element
 */
package com.atlassian.plugin.webresource.data;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.hostcontainer.HostContainer;
import com.atlassian.plugin.webresource.data.KeyedDataProvider;
import com.atlassian.plugin.webresource.util.PluginClassLoader;
import com.atlassian.webresource.api.data.WebResourceDataProvider;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import org.dom4j.Element;

public class WebResourceDataProviderParser {
    private final HostContainer hostContainer;
    private final List<KeyedDataProvider> keyedDataProviders;

    public WebResourceDataProviderParser(HostContainer hostContainer, List<Element> dataElements) {
        this.hostContainer = hostContainer;
        this.keyedDataProviders = ImmutableList.copyOf((Collection)Lists.transform(dataElements, (Function)new Function<Element, KeyedDataProvider>(){

            public KeyedDataProvider apply(@Nullable Element e) {
                return new KeyedDataProvider(e);
            }
        }));
    }

    public Map<String, WebResourceDataProvider> createDataProviders(Plugin plugin, Class<?> callingClass) throws ClassNotFoundException, PluginParseException {
        LinkedHashMap<String, WebResourceDataProvider> dps = new LinkedHashMap<String, WebResourceDataProvider>(this.keyedDataProviders.size());
        for (KeyedDataProvider dataKey : this.keyedDataProviders) {
            dps.put(dataKey.getKey(), this.createDataProvider(plugin, callingClass, dataKey.getClassName()));
        }
        return ImmutableMap.copyOf(dps);
    }

    private WebResourceDataProvider createDataProvider(Plugin plugin, Class<?> callingClass, String className) throws ClassNotFoundException {
        return (WebResourceDataProvider)PluginClassLoader.create(plugin, callingClass, this.hostContainer, className);
    }
}

