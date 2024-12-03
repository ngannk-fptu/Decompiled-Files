/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.collections.FastHashMap
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.beanutils.locale;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.locale.LocaleBeanUtilsBean;
import org.apache.commons.beanutils.locale.LocaleConvertUtils;
import org.apache.commons.beanutils.locale.LocaleConverter;
import org.apache.commons.beanutils.locale.converters.BigDecimalLocaleConverter;
import org.apache.commons.beanutils.locale.converters.BigIntegerLocaleConverter;
import org.apache.commons.beanutils.locale.converters.ByteLocaleConverter;
import org.apache.commons.beanutils.locale.converters.DoubleLocaleConverter;
import org.apache.commons.beanutils.locale.converters.FloatLocaleConverter;
import org.apache.commons.beanutils.locale.converters.IntegerLocaleConverter;
import org.apache.commons.beanutils.locale.converters.LongLocaleConverter;
import org.apache.commons.beanutils.locale.converters.ShortLocaleConverter;
import org.apache.commons.beanutils.locale.converters.SqlDateLocaleConverter;
import org.apache.commons.beanutils.locale.converters.SqlTimeLocaleConverter;
import org.apache.commons.beanutils.locale.converters.SqlTimestampLocaleConverter;
import org.apache.commons.beanutils.locale.converters.StringLocaleConverter;
import org.apache.commons.collections.FastHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LocaleConvertUtilsBean {
    private Locale defaultLocale = Locale.getDefault();
    private boolean applyLocalized = false;
    private final Log log = LogFactory.getLog(LocaleConvertUtils.class);
    private final FastHashMap mapConverters = new DelegateFastHashMap(BeanUtils.createCache());

    public static LocaleConvertUtilsBean getInstance() {
        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getLocaleConvertUtils();
    }

    public LocaleConvertUtilsBean() {
        this.mapConverters.setFast(false);
        this.deregister();
        this.mapConverters.setFast(true);
    }

    public Locale getDefaultLocale() {
        return this.defaultLocale;
    }

    public void setDefaultLocale(Locale locale) {
        this.defaultLocale = locale == null ? Locale.getDefault() : locale;
    }

    public boolean getApplyLocalized() {
        return this.applyLocalized;
    }

    public void setApplyLocalized(boolean newApplyLocalized) {
        this.applyLocalized = newApplyLocalized;
    }

    public String convert(Object value) {
        return this.convert(value, this.defaultLocale, null);
    }

    public String convert(Object value, String pattern) {
        return this.convert(value, this.defaultLocale, pattern);
    }

    public String convert(Object value, Locale locale, String pattern) {
        LocaleConverter converter = this.lookup(String.class, locale);
        return converter.convert(String.class, value, pattern);
    }

    public Object convert(String value, Class<?> clazz) {
        return this.convert(value, clazz, this.defaultLocale, null);
    }

    public Object convert(String value, Class<?> clazz, String pattern) {
        return this.convert(value, clazz, this.defaultLocale, pattern);
    }

    public Object convert(String value, Class<?> clazz, Locale locale, String pattern) {
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("Convert string " + value + " to class " + clazz.getName() + " using " + locale + " locale and " + pattern + " pattern"));
        }
        Class<Object> targetClass = clazz;
        LocaleConverter converter = this.lookup(clazz, locale);
        if (converter == null) {
            converter = this.lookup(String.class, locale);
            targetClass = String.class;
        }
        if (this.log.isTraceEnabled()) {
            this.log.trace((Object)("  Using converter " + converter));
        }
        return converter.convert(targetClass, value, pattern);
    }

    public Object convert(String[] values, Class<?> clazz, String pattern) {
        return this.convert(values, clazz, this.getDefaultLocale(), pattern);
    }

    public Object convert(String[] values, Class<?> clazz) {
        return this.convert(values, clazz, this.getDefaultLocale(), null);
    }

    public Object convert(String[] values, Class<?> clazz, Locale locale, String pattern) {
        Class<?> type = clazz;
        if (clazz.isArray()) {
            type = clazz.getComponentType();
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("Convert String[" + values.length + "] to class " + type.getName() + "[] using " + locale + " locale and " + pattern + " pattern"));
        }
        Object array = Array.newInstance(type, values.length);
        for (int i = 0; i < values.length; ++i) {
            Array.set(array, i, this.convert(values[i], type, locale, pattern));
        }
        return array;
    }

    public void register(LocaleConverter converter, Class<?> clazz, Locale locale) {
        this.lookup(locale).put(clazz, (Object)converter);
    }

    public void deregister() {
        FastHashMap defaultConverter = this.lookup(this.defaultLocale);
        this.mapConverters.setFast(false);
        this.mapConverters.clear();
        this.mapConverters.put((Object)this.defaultLocale, (Object)defaultConverter);
        this.mapConverters.setFast(true);
    }

    public void deregister(Locale locale) {
        this.mapConverters.remove((Object)locale);
    }

    public void deregister(Class<?> clazz, Locale locale) {
        this.lookup(locale).remove(clazz);
    }

    public LocaleConverter lookup(Class<?> clazz, Locale locale) {
        LocaleConverter converter = (LocaleConverter)this.lookup(locale).get(clazz);
        if (this.log.isTraceEnabled()) {
            this.log.trace((Object)("LocaleConverter:" + converter));
        }
        return converter;
    }

    @Deprecated
    protected FastHashMap lookup(Locale locale) {
        FastHashMap localeConverters;
        if (locale == null) {
            localeConverters = (FastHashMap)this.mapConverters.get((Object)this.defaultLocale);
        } else {
            localeConverters = (FastHashMap)this.mapConverters.get((Object)locale);
            if (localeConverters == null) {
                localeConverters = this.create(locale);
                this.mapConverters.put((Object)locale, (Object)localeConverters);
            }
        }
        return localeConverters;
    }

    @Deprecated
    protected FastHashMap create(Locale locale) {
        DelegateFastHashMap converter = new DelegateFastHashMap(BeanUtils.createCache());
        converter.setFast(false);
        converter.put(BigDecimal.class, new BigDecimalLocaleConverter(locale, this.applyLocalized));
        converter.put(BigInteger.class, new BigIntegerLocaleConverter(locale, this.applyLocalized));
        converter.put(Byte.class, new ByteLocaleConverter(locale, this.applyLocalized));
        converter.put(Byte.TYPE, new ByteLocaleConverter(locale, this.applyLocalized));
        converter.put(Double.class, new DoubleLocaleConverter(locale, this.applyLocalized));
        converter.put(Double.TYPE, new DoubleLocaleConverter(locale, this.applyLocalized));
        converter.put(Float.class, new FloatLocaleConverter(locale, this.applyLocalized));
        converter.put(Float.TYPE, new FloatLocaleConverter(locale, this.applyLocalized));
        converter.put(Integer.class, new IntegerLocaleConverter(locale, this.applyLocalized));
        converter.put(Integer.TYPE, new IntegerLocaleConverter(locale, this.applyLocalized));
        converter.put(Long.class, new LongLocaleConverter(locale, this.applyLocalized));
        converter.put(Long.TYPE, new LongLocaleConverter(locale, this.applyLocalized));
        converter.put(Short.class, new ShortLocaleConverter(locale, this.applyLocalized));
        converter.put(Short.TYPE, new ShortLocaleConverter(locale, this.applyLocalized));
        converter.put(String.class, new StringLocaleConverter(locale, this.applyLocalized));
        converter.put(Date.class, new SqlDateLocaleConverter(locale, "yyyy-MM-dd"));
        converter.put(Time.class, new SqlTimeLocaleConverter(locale, "HH:mm:ss"));
        converter.put(Timestamp.class, new SqlTimestampLocaleConverter(locale, "yyyy-MM-dd HH:mm:ss.S"));
        converter.setFast(true);
        return converter;
    }

    private static class DelegateFastHashMap
    extends FastHashMap {
        private final Map<Object, Object> map;

        private DelegateFastHashMap(Map<Object, Object> map) {
            this.map = map;
        }

        public void clear() {
            this.map.clear();
        }

        public boolean containsKey(Object key) {
            return this.map.containsKey(key);
        }

        public boolean containsValue(Object value) {
            return this.map.containsValue(value);
        }

        public Set<Map.Entry<Object, Object>> entrySet() {
            return this.map.entrySet();
        }

        public boolean equals(Object o) {
            return this.map.equals(o);
        }

        public Object get(Object key) {
            return this.map.get(key);
        }

        public int hashCode() {
            return this.map.hashCode();
        }

        public boolean isEmpty() {
            return this.map.isEmpty();
        }

        public Set<Object> keySet() {
            return this.map.keySet();
        }

        public Object put(Object key, Object value) {
            return this.map.put(key, value);
        }

        public void putAll(Map m) {
            this.map.putAll(m);
        }

        public Object remove(Object key) {
            return this.map.remove(key);
        }

        public int size() {
            return this.map.size();
        }

        public Collection<Object> values() {
            return this.map.values();
        }

        public boolean getFast() {
            return BeanUtils.getCacheFast(this.map);
        }

        public void setFast(boolean fast) {
            BeanUtils.setCacheFast(this.map, fast);
        }
    }
}

