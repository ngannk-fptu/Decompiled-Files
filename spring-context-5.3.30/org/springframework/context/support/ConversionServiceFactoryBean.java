/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.core.convert.ConversionService
 *  org.springframework.core.convert.converter.ConverterRegistry
 *  org.springframework.core.convert.support.ConversionServiceFactory
 *  org.springframework.core.convert.support.DefaultConversionService
 *  org.springframework.core.convert.support.GenericConversionService
 *  org.springframework.lang.Nullable
 */
package org.springframework.context.support;

import java.util.Set;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.core.convert.support.ConversionServiceFactory;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.lang.Nullable;

public class ConversionServiceFactoryBean
implements FactoryBean<ConversionService>,
InitializingBean {
    @Nullable
    private Set<?> converters;
    @Nullable
    private GenericConversionService conversionService;

    public void setConverters(Set<?> converters) {
        this.converters = converters;
    }

    public void afterPropertiesSet() {
        this.conversionService = this.createConversionService();
        ConversionServiceFactory.registerConverters(this.converters, (ConverterRegistry)this.conversionService);
    }

    protected GenericConversionService createConversionService() {
        return new DefaultConversionService();
    }

    @Nullable
    public ConversionService getObject() {
        return this.conversionService;
    }

    public Class<? extends ConversionService> getObjectType() {
        return GenericConversionService.class;
    }

    public boolean isSingleton() {
        return true;
    }
}

