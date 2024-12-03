/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.ClassUtils
 */
package org.apache.commons.configuration2.convert;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import org.apache.commons.configuration2.convert.ConversionHandler;
import org.apache.commons.configuration2.convert.DisabledListDelimiterHandler;
import org.apache.commons.configuration2.convert.ListDelimiterHandler;
import org.apache.commons.configuration2.convert.PropertyConverter;
import org.apache.commons.configuration2.interpol.ConfigurationInterpolator;
import org.apache.commons.lang3.ClassUtils;

public class DefaultConversionHandler
implements ConversionHandler {
    public static final DefaultConversionHandler INSTANCE = new DefaultConversionHandler();
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final ConfigurationInterpolator NULL_INTERPOLATOR = new ConfigurationInterpolator(){

        @Override
        public Object interpolate(Object value) {
            return value;
        }
    };
    static final ListDelimiterHandler LIST_DELIMITER_HANDLER = DisabledListDelimiterHandler.INSTANCE;
    private volatile String dateFormat;
    private volatile ListDelimiterHandler listDelimiterHandler = DisabledListDelimiterHandler.INSTANCE;

    private static ConfigurationInterpolator fetchInterpolator(ConfigurationInterpolator ci) {
        return ci != null ? ci : NULL_INTERPOLATOR;
    }

    protected <T> T convert(Object src, Class<T> targetCls, ConfigurationInterpolator ci) {
        Object conversionSrc = this.isComplexObject(src) ? this.extractConversionValue(src, targetCls, ci) : src;
        return this.convertValue(ci.interpolate(conversionSrc), targetCls, ci);
    }

    private <T> void convertToCollection(Object src, Class<T> elemClass, ConfigurationInterpolator ci, Collection<T> dest) {
        this.extractValues(ci.interpolate(src)).forEach(o -> dest.add(this.convert(o, elemClass, ci)));
    }

    protected <T> T convertValue(Object src, Class<T> targetCls, ConfigurationInterpolator ci) {
        if (src == null) {
            return null;
        }
        Object result = PropertyConverter.to(targetCls, src, this);
        return (T)result;
    }

    protected Object extractConversionValue(Object container, Class<?> targetCls, ConfigurationInterpolator ci) {
        Collection<?> values = this.extractValues(container, 1);
        return values.isEmpty() ? null : ci.interpolate(values.iterator().next());
    }

    protected Collection<?> extractValues(Object source) {
        return this.extractValues(source, Integer.MAX_VALUE);
    }

    protected Collection<?> extractValues(Object source, int limit) {
        return this.listDelimiterHandler.flatten(source, limit);
    }

    public String getDateFormat() {
        String fmt = this.dateFormat;
        return fmt != null ? fmt : DEFAULT_DATE_FORMAT;
    }

    public ListDelimiterHandler getListDelimiterHandler() {
        return this.listDelimiterHandler;
    }

    protected boolean isComplexObject(Object src) {
        return src instanceof Iterator || src instanceof Iterable || src != null && src.getClass().isArray();
    }

    protected boolean isEmptyElement(Object src) {
        return src instanceof CharSequence && ((CharSequence)src).length() == 0;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public void setListDelimiterHandler(ListDelimiterHandler listDelimiterHandler) {
        this.listDelimiterHandler = listDelimiterHandler != null ? listDelimiterHandler : LIST_DELIMITER_HANDLER;
    }

    @Override
    public <T> T to(Object src, Class<T> targetCls, ConfigurationInterpolator ci) {
        ConfigurationInterpolator interpolator = DefaultConversionHandler.fetchInterpolator(ci);
        return this.convert(interpolator.interpolate(src), targetCls, interpolator);
    }

    @Override
    public Object toArray(Object src, Class<?> elemClass, ConfigurationInterpolator ci) {
        if (src == null) {
            return null;
        }
        if (this.isEmptyElement(src)) {
            return Array.newInstance(elemClass, 0);
        }
        ConfigurationInterpolator interpolator = DefaultConversionHandler.fetchInterpolator(ci);
        return elemClass.isPrimitive() ? this.toPrimitiveArray(src, elemClass, interpolator) : this.toObjectArray(src, elemClass, interpolator);
    }

    @Override
    public <T> void toCollection(Object src, Class<T> elemClass, ConfigurationInterpolator ci, Collection<T> dest) {
        if (dest == null) {
            throw new IllegalArgumentException("Target collection must not be null!");
        }
        if (src != null && !this.isEmptyElement(src)) {
            this.convertToCollection(src, elemClass, DefaultConversionHandler.fetchInterpolator(ci), dest);
        }
    }

    private <T> T[] toObjectArray(Object src, Class<T> elemClass, ConfigurationInterpolator ci) {
        LinkedList convertedCol = new LinkedList();
        this.convertToCollection(src, elemClass, ci, convertedCol);
        Object[] result = (Object[])Array.newInstance(elemClass, convertedCol.size());
        return convertedCol.toArray(result);
    }

    private Object toPrimitiveArray(Object src, Class<?> elemClass, ConfigurationInterpolator ci) {
        if (src.getClass().isArray()) {
            if (src.getClass().getComponentType().equals(elemClass)) {
                return src;
            }
            if (src.getClass().getComponentType().equals(ClassUtils.primitiveToWrapper(elemClass))) {
                int length = Array.getLength(src);
                Object array = Array.newInstance(elemClass, length);
                for (int i = 0; i < length; ++i) {
                    Array.set(array, i, Array.get(src, i));
                }
                return array;
            }
        }
        Collection<?> values = this.extractValues(src);
        Class targetClass = ClassUtils.primitiveToWrapper(elemClass);
        Object array = Array.newInstance(elemClass, values.size());
        int idx = 0;
        for (Object value : values) {
            Array.set(array, idx++, this.convertValue(ci.interpolate(value), targetClass, ci));
        }
        return array;
    }
}

