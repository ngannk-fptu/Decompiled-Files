/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.soy.renderer.CustomSoyDataMapper
 *  com.atlassian.soy.renderer.SanitizationType
 *  com.atlassian.soy.renderer.SanitizedString
 *  com.atlassian.soy.renderer.SoyDataMapper
 *  com.atlassian.soy.renderer.SoyException
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Supplier
 *  com.google.common.base.Suppliers
 *  com.google.common.base.Throwables
 *  com.google.template.soy.data.SanitizedContent$ContentKind
 *  com.google.template.soy.data.SoyCustomValueConverter
 *  com.google.template.soy.data.SoyValueConverter
 *  com.google.template.soy.data.SoyValueProvider
 *  com.google.template.soy.data.UnsafeSanitizedContentOrdainer
 */
package com.atlassian.soy.impl.data;

import com.atlassian.soy.impl.data.EnumSoyValue;
import com.atlassian.soy.impl.data.JavaBeanAccessorResolver;
import com.atlassian.soy.impl.data.JavaBeanSoyDict;
import com.atlassian.soy.impl.data.SoyDataMapperManager;
import com.atlassian.soy.renderer.CustomSoyDataMapper;
import com.atlassian.soy.renderer.SanitizationType;
import com.atlassian.soy.renderer.SanitizedString;
import com.atlassian.soy.renderer.SoyDataMapper;
import com.atlassian.soy.renderer.SoyException;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.base.Throwables;
import com.google.template.soy.data.SanitizedContent;
import com.google.template.soy.data.SoyCustomValueConverter;
import com.google.template.soy.data.SoyValueConverter;
import com.google.template.soy.data.SoyValueProvider;
import com.google.template.soy.data.UnsafeSanitizedContentOrdainer;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.Callable;

public class AtlassianSoyCustomValueConverter
implements SoyCustomValueConverter {
    private final JavaBeanAccessorResolver accessorResolver;
    private final SoyDataMapperManager soyDataMapperManager;

    public AtlassianSoyCustomValueConverter(JavaBeanAccessorResolver accessorResolver, SoyDataMapperManager soyDataMapperManager) {
        this.accessorResolver = accessorResolver;
        this.soyDataMapperManager = soyDataMapperManager;
    }

    public SoyValueProvider convert(SoyValueConverter converter, Object value) {
        Object newValue;
        if (value instanceof Enum) {
            return new EnumSoyValue((Enum)value);
        }
        if (value instanceof SanitizedString) {
            SanitizedString sanitizedString = (SanitizedString)value;
            return UnsafeSanitizedContentOrdainer.ordainAsSafe((String)sanitizedString.getValue(), (SanitizedContent.ContentKind)AtlassianSoyCustomValueConverter.toContentKind(sanitizedString.getType()));
        }
        if (value.getClass().isArray()) {
            int length = Array.getLength(value);
            ArrayList<Object> list = new ArrayList<Object>(length);
            for (int i = 0; i < length; ++i) {
                list.add(Array.get(value, i));
            }
            return converter.convert(list);
        }
        SoyDataMapper<Object, ?> mapper = this.findMapper(value);
        if (mapper != null && (newValue = mapper.convert(value)) != value) {
            return converter.convert(newValue);
        }
        return new JavaBeanSoyDict(this.accessorResolver, converter, this.toSupplier(value));
    }

    private Supplier<?> toSupplier(Object value) {
        if (value instanceof Supplier) {
            return (Supplier)value;
        }
        if (value instanceof Callable) {
            return new CallableSupplier((Callable)value);
        }
        return Suppliers.ofInstance((Object)value);
    }

    private <T> SoyDataMapper<T, ?> findMapper(T value) {
        CustomSoyDataMapper mapperConfig = value.getClass().getAnnotation(CustomSoyDataMapper.class);
        if (mapperConfig == null) {
            return null;
        }
        String mapperName = mapperConfig.value();
        SoyDataMapper mapper = this.soyDataMapperManager.getMapper(mapperName);
        if (mapper == null) {
            throw new SoyException("Could not find custom mapper " + mapperName + " for class " + value.getClass());
        }
        return mapper;
    }

    private static SanitizedContent.ContentKind toContentKind(SanitizationType type) {
        switch (type) {
            case CSS: {
                return SanitizedContent.ContentKind.CSS;
            }
            case JS: {
                return SanitizedContent.ContentKind.JS;
            }
            case JS_STRING: {
                return SanitizedContent.ContentKind.JS_STR_CHARS;
            }
            case HTML: {
                return SanitizedContent.ContentKind.HTML;
            }
            case HTML_ATTRIBUTE: {
                return SanitizedContent.ContentKind.ATTRIBUTES;
            }
            case TEXT: {
                return SanitizedContent.ContentKind.TEXT;
            }
            case URI: {
                return SanitizedContent.ContentKind.URI;
            }
        }
        throw new UnsupportedOperationException("Unsupported type " + type);
    }

    private static class CallableSupplier
    implements Supplier<Object> {
        private final Callable<?> callable;

        private CallableSupplier(Callable<?> callable) {
            this.callable = (Callable)Preconditions.checkNotNull(callable, (Object)"callable");
        }

        public Object get() {
            try {
                return this.callable.call();
            }
            catch (Exception e) {
                throw Throwables.propagate((Throwable)e);
            }
        }
    }
}

