/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.money.CurrencyUnit
 *  javax.money.Monetary
 *  javax.money.MonetaryAmount
 */
package org.springframework.format.number.money;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Collections;
import java.util.Currency;
import java.util.Locale;
import java.util.Set;
import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import org.springframework.context.support.EmbeddedValueResolutionSupport;
import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Formatter;
import org.springframework.format.Parser;
import org.springframework.format.Printer;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.number.CurrencyStyleFormatter;
import org.springframework.format.number.NumberStyleFormatter;
import org.springframework.format.number.PercentStyleFormatter;
import org.springframework.util.StringUtils;

public class Jsr354NumberFormatAnnotationFormatterFactory
extends EmbeddedValueResolutionSupport
implements AnnotationFormatterFactory<NumberFormat> {
    private static final String CURRENCY_CODE_PATTERN = "\u00a4\u00a4";

    @Override
    public Set<Class<?>> getFieldTypes() {
        return Collections.singleton(MonetaryAmount.class);
    }

    @Override
    public Printer<MonetaryAmount> getPrinter(NumberFormat annotation, Class<?> fieldType) {
        return this.configureFormatterFrom(annotation);
    }

    @Override
    public Parser<MonetaryAmount> getParser(NumberFormat annotation, Class<?> fieldType) {
        return this.configureFormatterFrom(annotation);
    }

    private Formatter<MonetaryAmount> configureFormatterFrom(NumberFormat annotation) {
        String pattern = this.resolveEmbeddedValue(annotation.pattern());
        if (StringUtils.hasLength(pattern)) {
            return new PatternDecoratingFormatter(pattern);
        }
        NumberFormat.Style style = annotation.style();
        if (style == NumberFormat.Style.NUMBER) {
            return new NumberDecoratingFormatter(new NumberStyleFormatter());
        }
        if (style == NumberFormat.Style.PERCENT) {
            return new NumberDecoratingFormatter(new PercentStyleFormatter());
        }
        return new NumberDecoratingFormatter(new CurrencyStyleFormatter());
    }

    private static class PatternDecoratingFormatter
    implements Formatter<MonetaryAmount> {
        private final String pattern;

        public PatternDecoratingFormatter(String pattern) {
            this.pattern = pattern;
        }

        @Override
        public String print(MonetaryAmount object, Locale locale) {
            CurrencyStyleFormatter formatter = new CurrencyStyleFormatter();
            formatter.setCurrency(Currency.getInstance(object.getCurrency().getCurrencyCode()));
            formatter.setPattern(this.pattern);
            return formatter.print((Number)object.getNumber(), locale);
        }

        @Override
        public MonetaryAmount parse(String text, Locale locale) throws ParseException {
            CurrencyStyleFormatter formatter = new CurrencyStyleFormatter();
            Currency currency = this.determineCurrency(text, locale);
            CurrencyUnit currencyUnit = Monetary.getCurrency((String)currency.getCurrencyCode(), (String[])new String[0]);
            formatter.setCurrency(currency);
            formatter.setPattern(this.pattern);
            BigDecimal numberValue = formatter.parse(text, locale);
            return Monetary.getDefaultAmountFactory().setNumber((Number)numberValue).setCurrency(currencyUnit).create();
        }

        private Currency determineCurrency(String text, Locale locale) {
            try {
                if (text.length() < 3) {
                    return Currency.getInstance(locale);
                }
                if (this.pattern.startsWith(Jsr354NumberFormatAnnotationFormatterFactory.CURRENCY_CODE_PATTERN)) {
                    return Currency.getInstance(text.substring(0, 3));
                }
                if (this.pattern.endsWith(Jsr354NumberFormatAnnotationFormatterFactory.CURRENCY_CODE_PATTERN)) {
                    return Currency.getInstance(text.substring(text.length() - 3));
                }
                return Currency.getInstance(locale);
            }
            catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Cannot determine currency for number value [" + text + "]", ex);
            }
        }
    }

    private static class NumberDecoratingFormatter
    implements Formatter<MonetaryAmount> {
        private final Formatter<Number> numberFormatter;

        public NumberDecoratingFormatter(Formatter<Number> numberFormatter) {
            this.numberFormatter = numberFormatter;
        }

        @Override
        public String print(MonetaryAmount object, Locale locale) {
            return this.numberFormatter.print((Number)object.getNumber(), locale);
        }

        @Override
        public MonetaryAmount parse(String text, Locale locale) throws ParseException {
            CurrencyUnit currencyUnit = Monetary.getCurrency((Locale)locale, (String[])new String[0]);
            Number numberValue = (Number)this.numberFormatter.parse(text, locale);
            return Monetary.getDefaultAmountFactory().setNumber(numberValue).setCurrency(currencyUnit).create();
        }
    }
}

