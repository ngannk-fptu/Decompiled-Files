/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.NumberUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.format.number;

import java.util.Set;
import org.springframework.context.support.EmbeddedValueResolutionSupport;
import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Formatter;
import org.springframework.format.Parser;
import org.springframework.format.Printer;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.number.CurrencyStyleFormatter;
import org.springframework.format.number.NumberStyleFormatter;
import org.springframework.format.number.PercentStyleFormatter;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;

public class NumberFormatAnnotationFormatterFactory
extends EmbeddedValueResolutionSupport
implements AnnotationFormatterFactory<NumberFormat> {
    @Override
    public Set<Class<?>> getFieldTypes() {
        return NumberUtils.STANDARD_NUMBER_TYPES;
    }

    @Override
    public Printer<Number> getPrinter(NumberFormat annotation, Class<?> fieldType) {
        return this.configureFormatterFrom(annotation);
    }

    @Override
    public Parser<Number> getParser(NumberFormat annotation, Class<?> fieldType) {
        return this.configureFormatterFrom(annotation);
    }

    private Formatter<Number> configureFormatterFrom(NumberFormat annotation) {
        String pattern = this.resolveEmbeddedValue(annotation.pattern());
        if (StringUtils.hasLength((String)pattern)) {
            return new NumberStyleFormatter(pattern);
        }
        NumberFormat.Style style = annotation.style();
        if (style == NumberFormat.Style.CURRENCY) {
            return new CurrencyStyleFormatter();
        }
        if (style == NumberFormat.Style.PERCENT) {
            return new PercentStyleFormatter();
        }
        return new NumberStyleFormatter();
    }
}

