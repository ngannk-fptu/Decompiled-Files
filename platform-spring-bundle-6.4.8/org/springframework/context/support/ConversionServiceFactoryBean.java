/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.support;

import java.util.Set;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.convert.ConversionService;
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

    @Override
    public void afterPropertiesSet() {
        this.conversionService = this.createConversionService();
        ConversionServiceFactory.registerConverters(this.converters, this.conversionService);
    }

    protected GenericConversionService createConversionService() {
        return new DefaultConversionService();
    }

    @Override
    @Nullable
    public ConversionService getObject() {
        return this.conversionService;
    }

    @Override
    public Class<? extends ConversionService> getObjectType() {
        return GenericConversionService.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}

