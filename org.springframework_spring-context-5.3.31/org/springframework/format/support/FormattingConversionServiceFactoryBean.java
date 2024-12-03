/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.core.convert.converter.ConverterRegistry
 *  org.springframework.core.convert.support.ConversionServiceFactory
 *  org.springframework.lang.Nullable
 *  org.springframework.util.StringValueResolver
 */
package org.springframework.format.support;

import java.util.Set;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.core.convert.support.ConversionServiceFactory;
import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Formatter;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.lang.Nullable;
import org.springframework.util.StringValueResolver;

public class FormattingConversionServiceFactoryBean
implements FactoryBean<FormattingConversionService>,
EmbeddedValueResolverAware,
InitializingBean {
    @Nullable
    private Set<?> converters;
    @Nullable
    private Set<?> formatters;
    @Nullable
    private Set<FormatterRegistrar> formatterRegistrars;
    private boolean registerDefaultFormatters = true;
    @Nullable
    private StringValueResolver embeddedValueResolver;
    @Nullable
    private FormattingConversionService conversionService;

    public void setConverters(Set<?> converters) {
        this.converters = converters;
    }

    public void setFormatters(Set<?> formatters) {
        this.formatters = formatters;
    }

    public void setFormatterRegistrars(Set<FormatterRegistrar> formatterRegistrars) {
        this.formatterRegistrars = formatterRegistrars;
    }

    public void setRegisterDefaultFormatters(boolean registerDefaultFormatters) {
        this.registerDefaultFormatters = registerDefaultFormatters;
    }

    @Override
    public void setEmbeddedValueResolver(StringValueResolver embeddedValueResolver) {
        this.embeddedValueResolver = embeddedValueResolver;
    }

    public void afterPropertiesSet() {
        this.conversionService = new DefaultFormattingConversionService(this.embeddedValueResolver, this.registerDefaultFormatters);
        ConversionServiceFactory.registerConverters(this.converters, (ConverterRegistry)this.conversionService);
        this.registerFormatters(this.conversionService);
    }

    private void registerFormatters(FormattingConversionService conversionService) {
        if (this.formatters != null) {
            for (Object formatter : this.formatters) {
                if (formatter instanceof Formatter) {
                    conversionService.addFormatter((Formatter)formatter);
                    continue;
                }
                if (formatter instanceof AnnotationFormatterFactory) {
                    conversionService.addFormatterForFieldAnnotation((AnnotationFormatterFactory)formatter);
                    continue;
                }
                throw new IllegalArgumentException("Custom formatters must be implementations of Formatter or AnnotationFormatterFactory");
            }
        }
        if (this.formatterRegistrars != null) {
            for (FormatterRegistrar registrar : this.formatterRegistrars) {
                registrar.registerFormatters(conversionService);
            }
        }
    }

    @Nullable
    public FormattingConversionService getObject() {
        return this.conversionService;
    }

    public Class<? extends FormattingConversionService> getObjectType() {
        return FormattingConversionService.class;
    }

    public boolean isSingleton() {
        return true;
    }
}

