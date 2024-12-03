/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.inject.Module
 *  com.google.template.soy.SoyModule
 *  com.google.template.soy.data.SoyCustomValueConverter
 *  com.google.template.soy.xliffmsgplugin.XliffMsgPluginModule
 */
package com.atlassian.soy.impl.modules;

import com.atlassian.soy.impl.modules.BridgeModule;
import com.atlassian.soy.impl.modules.CoreFunctionsModule;
import com.atlassian.soy.impl.modules.CustomFunctionsModule;
import com.atlassian.soy.impl.modules.CustomValueModule;
import com.atlassian.soy.spi.functions.SoyFunctionSupplier;
import com.atlassian.soy.spi.i18n.I18nResolver;
import com.atlassian.soy.spi.i18n.JsLocaleResolver;
import com.atlassian.soy.spi.modules.GuiceModuleSupplier;
import com.atlassian.soy.spi.web.WebContextProvider;
import com.google.common.collect.ImmutableList;
import com.google.inject.Module;
import com.google.template.soy.SoyModule;
import com.google.template.soy.data.SoyCustomValueConverter;
import com.google.template.soy.xliffmsgplugin.XliffMsgPluginModule;
import java.util.Properties;

public class DefaultGuiceModuleSupplier
implements GuiceModuleSupplier {
    private final Iterable<Module> defaultModules;

    public DefaultGuiceModuleSupplier(SoyCustomValueConverter customValueConverter, I18nResolver i18nResolver, JsLocaleResolver jsLocaleResolver, Properties properties, SoyFunctionSupplier soyFunctionSupplier, WebContextProvider webContextProvider) {
        this.defaultModules = ImmutableList.builder().add((Object)new SoyModule()).add((Object)new XliffMsgPluginModule()).add((Object)new CustomValueModule()).add((Object)new CoreFunctionsModule()).add((Object)new CustomFunctionsModule(soyFunctionSupplier)).add((Object)new BridgeModule(customValueConverter, i18nResolver, jsLocaleResolver, properties, webContextProvider)).build();
    }

    @Override
    public Iterable<Module> get() {
        return this.defaultModules;
    }
}

