/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.convert.converter.ConverterRegistry
 *  org.springframework.core.convert.support.DefaultConversionService
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.StringValueResolver
 */
package org.springframework.format.support;

import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.DateFormatterRegistrar;
import org.springframework.format.datetime.joda.JodaTimeFormatterRegistrar;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.format.number.NumberFormatAnnotationFormatterFactory;
import org.springframework.format.number.money.CurrencyUnitFormatter;
import org.springframework.format.number.money.Jsr354NumberFormatAnnotationFormatterFactory;
import org.springframework.format.number.money.MonetaryAmountFormatter;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringValueResolver;

public class DefaultFormattingConversionService
extends FormattingConversionService {
    private static final boolean jsr354Present;
    private static final boolean jodaTimePresent;

    public DefaultFormattingConversionService() {
        this(null, true);
    }

    public DefaultFormattingConversionService(boolean registerDefaultFormatters) {
        this(null, registerDefaultFormatters);
    }

    public DefaultFormattingConversionService(@Nullable StringValueResolver embeddedValueResolver, boolean registerDefaultFormatters) {
        if (embeddedValueResolver != null) {
            this.setEmbeddedValueResolver(embeddedValueResolver);
        }
        DefaultConversionService.addDefaultConverters((ConverterRegistry)this);
        if (registerDefaultFormatters) {
            DefaultFormattingConversionService.addDefaultFormatters(this);
        }
    }

    public static void addDefaultFormatters(FormatterRegistry formatterRegistry) {
        formatterRegistry.addFormatterForFieldAnnotation(new NumberFormatAnnotationFormatterFactory());
        if (jsr354Present) {
            formatterRegistry.addFormatter(new CurrencyUnitFormatter());
            formatterRegistry.addFormatter(new MonetaryAmountFormatter());
            formatterRegistry.addFormatterForFieldAnnotation(new Jsr354NumberFormatAnnotationFormatterFactory());
        }
        new DateTimeFormatterRegistrar().registerFormatters(formatterRegistry);
        if (jodaTimePresent) {
            new JodaTimeFormatterRegistrar().registerFormatters(formatterRegistry);
        } else {
            new DateFormatterRegistrar().registerFormatters(formatterRegistry);
        }
    }

    static {
        ClassLoader classLoader = DefaultFormattingConversionService.class.getClassLoader();
        jsr354Present = ClassUtils.isPresent((String)"javax.money.MonetaryAmount", (ClassLoader)classLoader);
        jodaTimePresent = ClassUtils.isPresent((String)"org.joda.time.YearMonth", (ClassLoader)classLoader);
    }
}

