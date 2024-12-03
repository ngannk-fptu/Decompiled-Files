/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.codehaus.jackson.map.ObjectMapper
 */
package com.atlassian.business.insights.core.writer.convert;

import com.atlassian.business.insights.core.writer.convert.ValueConverter;
import com.atlassian.business.insights.core.writer.exception.ConversionException;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.codehaus.jackson.map.ObjectMapper;

public class JsonValueConverter
implements ValueConverter {
    private final ObjectMapper mapper;

    public JsonValueConverter(@Nonnull ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    @Nullable
    public Object convert(@Nullable Object value) {
        try {
            if (JsonValueConverter.isEmptyCollectionOrMap(value)) {
                return null;
            }
            if (value instanceof Collection || value instanceof Map) {
                return this.mapper.writeValueAsString(value);
            }
            return value;
        }
        catch (IOException e) {
            throw new ConversionException(String.format("Could not format %s as JSON.", value));
        }
    }

    private static boolean isEmptyCollectionOrMap(Object value) {
        return JsonValueConverter.isEmptyCollection(value) || JsonValueConverter.isEmptyMap(value);
    }

    private static boolean isEmptyCollection(Object value) {
        return value instanceof Collection && ((Collection)value).isEmpty();
    }

    private static boolean isEmptyMap(Object value) {
        return value instanceof Map && ((Map)value).isEmpty();
    }
}

