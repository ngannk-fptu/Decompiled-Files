/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.soy.renderer.SoyDataMapper
 */
package com.atlassian.soy.impl;

import com.atlassian.soy.impl.DefaultSoyManager;
import com.atlassian.soy.impl.SimpleTemplateSetFactory;
import com.atlassian.soy.impl.data.AtlassianSoyCustomValueConverter;
import com.atlassian.soy.impl.data.CachingJavaBeanAccessorResolver;
import com.atlassian.soy.impl.data.IntrospectorJavaBeanAccessorResolver;
import com.atlassian.soy.impl.data.SoyDataMapperManager;
import com.atlassian.soy.impl.functions.ServiceLoaderSoyFunctionSupplier;
import com.atlassian.soy.impl.i18n.ResourceBundleI18nResolver;
import com.atlassian.soy.impl.i18n.WebContextJsLocaleResolver;
import com.atlassian.soy.impl.modules.DefaultGuiceModuleSupplier;
import com.atlassian.soy.impl.web.SimpleWebContextProvider;
import com.atlassian.soy.renderer.SoyDataMapper;
import com.atlassian.soy.spi.TemplateSetFactory;
import com.atlassian.soy.spi.functions.SoyFunctionSupplier;
import com.atlassian.soy.spi.i18n.I18nResolver;
import com.atlassian.soy.spi.i18n.JsLocaleResolver;
import com.atlassian.soy.spi.modules.GuiceModuleSupplier;
import com.atlassian.soy.spi.web.WebContextProvider;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class SoyManagerBuilder {
    private I18nResolver i18nResolver;
    private JsLocaleResolver jsLocaleResolver;
    private GuiceModuleSupplier moduleSupplier;
    private Properties properties;
    private List<SoyDataMapper<?, ?>> soyDataMappers;
    private SoyFunctionSupplier soyFunctionSupplier;
    private TemplateSetFactory templateSetFactory;
    private WebContextProvider webContextProvider;

    public SoyManagerBuilder webContextProvider(WebContextProvider webContextProvider) {
        this.webContextProvider = webContextProvider;
        return this;
    }

    public boolean hasWebContextProvider() {
        return this.webContextProvider != null;
    }

    public SoyManagerBuilder templateSetFactory(TemplateSetFactory templateSetFactory) {
        this.templateSetFactory = templateSetFactory;
        return this;
    }

    public boolean hasTemplateSetFactory() {
        return this.templateSetFactory != null;
    }

    public SoyManagerBuilder localeResolver(JsLocaleResolver jsLocaleResolver) {
        this.jsLocaleResolver = jsLocaleResolver;
        return this;
    }

    public boolean hasLocaleResolver() {
        return this.jsLocaleResolver != null;
    }

    public SoyManagerBuilder i18nResolver(I18nResolver i18nResolver) {
        this.i18nResolver = i18nResolver;
        return this;
    }

    public boolean hasI18nResolver() {
        return this.i18nResolver != null;
    }

    public SoyManagerBuilder moduleSupplier(GuiceModuleSupplier moduleSupplier) {
        this.moduleSupplier = moduleSupplier;
        return this;
    }

    public boolean hasModuleSupplier() {
        return this.moduleSupplier != null;
    }

    public SoyManagerBuilder dataMappers(List<SoyDataMapper<?, ?>> soyDataMappers) {
        this.soyDataMappers = soyDataMappers;
        return this;
    }

    public SoyManagerBuilder dataMappers(SoyDataMapper<?, ?> ... soyDataMappers) {
        return this.dataMappers(Arrays.asList(soyDataMappers));
    }

    public boolean hasDataMappers() {
        return this.soyDataMappers != null;
    }

    public SoyManagerBuilder functionSupplier(SoyFunctionSupplier soyFunctionSupplier) {
        this.soyFunctionSupplier = soyFunctionSupplier;
        return this;
    }

    public boolean hasFunctionSupplier() {
        return this.soyFunctionSupplier != null;
    }

    public SoyManagerBuilder properties(Properties properties) {
        this.properties = properties;
        return this;
    }

    public boolean hasProperties() {
        return this.properties != null;
    }

    public DefaultSoyManager build() {
        SoyDataMapperManager soyDataMapperManager;
        SoyDataMapperManager soyDataMapperManager2 = soyDataMapperManager = this.hasDataMappers() ? new SoyDataMapperManager(this.soyDataMappers) : new SoyDataMapperManager();
        if (!this.hasFunctionSupplier()) {
            this.functionSupplier(new ServiceLoaderSoyFunctionSupplier());
        }
        CachingJavaBeanAccessorResolver javaBeanAccessorResolver = new CachingJavaBeanAccessorResolver(new IntrospectorJavaBeanAccessorResolver());
        if (!this.hasModuleSupplier()) {
            if (!this.hasWebContextProvider()) {
                this.webContextProvider(new SimpleWebContextProvider());
            }
            if (!this.hasI18nResolver()) {
                this.i18nResolver(new ResourceBundleI18nResolver(this.webContextProvider));
            }
            if (!this.hasLocaleResolver()) {
                this.localeResolver(new WebContextJsLocaleResolver(this.webContextProvider));
            }
            if (!this.hasProperties()) {
                this.properties(System.getProperties());
            }
            this.moduleSupplier(new DefaultGuiceModuleSupplier(new AtlassianSoyCustomValueConverter(javaBeanAccessorResolver, soyDataMapperManager), this.i18nResolver, this.jsLocaleResolver, this.properties, this.soyFunctionSupplier, this.webContextProvider));
        }
        if (!this.hasTemplateSetFactory()) {
            this.templateSetFactory(new SimpleTemplateSetFactory(Collections.emptySet()));
        }
        return new DefaultSoyManager(this.moduleSupplier, javaBeanAccessorResolver, this.templateSetFactory);
    }
}

