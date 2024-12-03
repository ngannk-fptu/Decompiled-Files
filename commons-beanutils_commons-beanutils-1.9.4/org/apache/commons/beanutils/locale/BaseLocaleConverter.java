/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.beanutils.locale;

import java.text.ParseException;
import java.util.Locale;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.locale.LocaleConverter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class BaseLocaleConverter
implements LocaleConverter {
    private final Log log = LogFactory.getLog(BaseLocaleConverter.class);
    private Object defaultValue = null;
    protected boolean useDefault = false;
    protected Locale locale = Locale.getDefault();
    protected String pattern = null;
    protected boolean locPattern = false;

    protected BaseLocaleConverter(Locale locale, String pattern) {
        this(null, locale, pattern, false, false);
    }

    protected BaseLocaleConverter(Locale locale, String pattern, boolean locPattern) {
        this(null, locale, pattern, false, locPattern);
    }

    protected BaseLocaleConverter(Object defaultValue, Locale locale, String pattern) {
        this(defaultValue, locale, pattern, false);
    }

    protected BaseLocaleConverter(Object defaultValue, Locale locale, String pattern, boolean locPattern) {
        this(defaultValue, locale, pattern, true, locPattern);
    }

    private BaseLocaleConverter(Object defaultValue, Locale locale, String pattern, boolean useDefault, boolean locPattern) {
        if (useDefault) {
            this.defaultValue = defaultValue;
            this.useDefault = true;
        }
        if (locale != null) {
            this.locale = locale;
        }
        this.pattern = pattern;
        this.locPattern = locPattern;
    }

    protected abstract Object parse(Object var1, String var2) throws ParseException;

    public Object convert(Object value) {
        return this.convert(value, null);
    }

    public Object convert(Object value, String pattern) {
        return this.convert(null, value, pattern);
    }

    @Override
    public <T> T convert(Class<T> type, Object value) {
        return this.convert(type, value, null);
    }

    @Override
    public <T> T convert(Class<T> type, Object value, String pattern) {
        Class<T> targetType = ConvertUtils.primitiveToWrapper(type);
        if (value == null) {
            if (this.useDefault) {
                return this.getDefaultAs(targetType);
            }
            this.log.debug((Object)"Null value specified for conversion, returing null");
            return null;
        }
        try {
            if (pattern != null) {
                return BaseLocaleConverter.checkConversionResult(targetType, this.parse(value, pattern));
            }
            return BaseLocaleConverter.checkConversionResult(targetType, this.parse(value, this.pattern));
        }
        catch (Exception e) {
            if (this.useDefault) {
                return this.getDefaultAs(targetType);
            }
            if (e instanceof ConversionException) {
                throw (ConversionException)e;
            }
            throw new ConversionException(e);
        }
    }

    private <T> T getDefaultAs(Class<T> type) {
        return BaseLocaleConverter.checkConversionResult(type, this.defaultValue);
    }

    private static <T> T checkConversionResult(Class<T> type, Object result) {
        if (type == null) {
            Object temp = result;
            return (T)temp;
        }
        if (result == null) {
            return null;
        }
        if (type.isInstance(result)) {
            return type.cast(result);
        }
        throw new ConversionException("Unsupported target type: " + type);
    }
}

