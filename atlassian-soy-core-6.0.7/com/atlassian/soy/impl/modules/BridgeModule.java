/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.inject.AbstractModule
 *  com.google.inject.Binder
 *  com.google.inject.Provides
 *  com.google.inject.Singleton
 *  com.google.inject.name.Names
 *  com.google.template.soy.data.SoyCustomValueConverter
 */
package com.atlassian.soy.impl.modules;

import com.atlassian.soy.spi.i18n.I18nResolver;
import com.atlassian.soy.spi.i18n.JsLocaleResolver;
import com.atlassian.soy.spi.web.WebContextProvider;
import com.google.common.collect.ImmutableList;
import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.google.template.soy.data.SoyCustomValueConverter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class BridgeModule
extends AbstractModule {
    private static final Properties DEFAULT_PROPERTIES;
    private final SoyCustomValueConverter customValueConverter;
    private final I18nResolver i18nResolver;
    private final JsLocaleResolver jsLocaleResolver;
    private final Properties properties;
    private final WebContextProvider webContextProvider;

    public BridgeModule(SoyCustomValueConverter customValueConverter, I18nResolver i18nResolver, JsLocaleResolver jsLocaleResolver, Properties properties, WebContextProvider webContextProvider) {
        this.customValueConverter = customValueConverter;
        this.i18nResolver = i18nResolver;
        this.jsLocaleResolver = jsLocaleResolver;
        this.properties = BridgeModule.buildProperties(properties);
        this.webContextProvider = webContextProvider;
    }

    protected void configure() {
        Names.bindProperties((Binder)this.binder(), (Properties)this.properties);
        this.binder().bind(I18nResolver.class).toInstance((Object)this.i18nResolver);
        this.binder().bind(JsLocaleResolver.class).toInstance((Object)this.jsLocaleResolver);
        this.binder().bind(WebContextProvider.class).toInstance((Object)this.webContextProvider);
    }

    @Provides
    @Singleton
    public List<SoyCustomValueConverter> provideValueConverters() {
        return ImmutableList.of((Object)this.customValueConverter);
    }

    private static Properties buildProperties(Properties overrides) {
        Properties props = new Properties(DEFAULT_PROPERTIES);
        for (String propertyName : overrides.stringPropertyNames()) {
            props.setProperty(propertyName, overrides.getProperty(propertyName));
        }
        return props;
    }

    static {
        Properties props;
        block5: {
            props = new Properties();
            try {
                InputStream in = BridgeModule.class.getResourceAsStream("/atlassian-soy-defaults.properties");
                if (in == null) break block5;
                try {
                    props.load(in);
                }
                finally {
                    in.close();
                }
            }
            catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
        DEFAULT_PROPERTIES = props;
    }
}

