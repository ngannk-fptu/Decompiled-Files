/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.webresource.WebResourceManager
 *  com.atlassian.soy.impl.modules.DefaultGuiceModuleSupplier
 *  com.atlassian.soy.spi.functions.SoyFunctionSupplier
 *  com.atlassian.soy.spi.i18n.I18nResolver
 *  com.atlassian.soy.spi.i18n.JsLocaleResolver
 *  com.atlassian.soy.spi.modules.GuiceModuleSupplier
 *  com.atlassian.soy.spi.web.WebContextProvider
 *  com.google.common.collect.ImmutableList
 *  com.google.inject.Module
 *  com.google.template.soy.data.SoyCustomValueConverter
 */
package com.atlassian.soy.impl.modules;

import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.soy.impl.modules.DefaultGuiceModuleSupplier;
import com.atlassian.soy.impl.modules.PluginsBridgeModule;
import com.atlassian.soy.impl.modules.WebResourceFunctionsModule;
import com.atlassian.soy.spi.functions.SoyFunctionSupplier;
import com.atlassian.soy.spi.i18n.I18nResolver;
import com.atlassian.soy.spi.i18n.JsLocaleResolver;
import com.atlassian.soy.spi.modules.GuiceModuleSupplier;
import com.atlassian.soy.spi.web.WebContextProvider;
import com.google.common.collect.ImmutableList;
import com.google.inject.Module;
import com.google.template.soy.data.SoyCustomValueConverter;
import java.util.Properties;

public class PluginsGuiceModuleSupplier
implements GuiceModuleSupplier {
    private final Iterable<Module> modules;

    public PluginsGuiceModuleSupplier(SoyCustomValueConverter customValueConverter, I18nResolver i18nResolver, JsLocaleResolver jsLocaleResolver, Properties properties, SoyFunctionSupplier soyFunctionSupplier, WebContextProvider webContextProvider, WebResourceManager webResourceManager) {
        DefaultGuiceModuleSupplier defaultModules = new DefaultGuiceModuleSupplier(customValueConverter, i18nResolver, jsLocaleResolver, properties, soyFunctionSupplier, webContextProvider);
        this.modules = ImmutableList.builder().addAll(defaultModules.get()).add((Object)new PluginsBridgeModule(webResourceManager)).add((Object)new WebResourceFunctionsModule()).build();
    }

    public Iterable<Module> get() {
        return this.modules;
    }
}

