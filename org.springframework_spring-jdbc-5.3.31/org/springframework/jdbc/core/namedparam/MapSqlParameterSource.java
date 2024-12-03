/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.NonNull
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package org.springframework.jdbc.core.namedparam;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.namedparam.AbstractSqlParameterSource;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class MapSqlParameterSource
extends AbstractSqlParameterSource {
    private final Map<String, Object> values = new LinkedHashMap<String, Object>();

    public MapSqlParameterSource() {
    }

    public MapSqlParameterSource(String paramName, @Nullable Object value) {
        this.addValue(paramName, value);
    }

    public MapSqlParameterSource(@Nullable Map<String, ?> values) {
        this.addValues(values);
    }

    public MapSqlParameterSource addValue(String paramName, @Nullable Object value) {
        Assert.notNull((Object)paramName, (String)"Parameter name must not be null");
        this.values.put(paramName, value);
        if (value instanceof SqlParameterValue) {
            this.registerSqlType(paramName, ((SqlParameterValue)value).getSqlType());
        }
        return this;
    }

    public MapSqlParameterSource addValue(String paramName, @Nullable Object value, int sqlType) {
        Assert.notNull((Object)paramName, (String)"Parameter name must not be null");
        this.values.put(paramName, value);
        this.registerSqlType(paramName, sqlType);
        return this;
    }

    public MapSqlParameterSource addValue(String paramName, @Nullable Object value, int sqlType, String typeName) {
        Assert.notNull((Object)paramName, (String)"Parameter name must not be null");
        this.values.put(paramName, value);
        this.registerSqlType(paramName, sqlType);
        this.registerTypeName(paramName, typeName);
        return this;
    }

    public MapSqlParameterSource addValues(@Nullable Map<String, ?> values) {
        if (values != null) {
            values.forEach((key, value) -> {
                this.values.put((String)key, value);
                if (value instanceof SqlParameterValue) {
                    this.registerSqlType((String)key, ((SqlParameterValue)value).getSqlType());
                }
            });
        }
        return this;
    }

    public Map<String, Object> getValues() {
        return Collections.unmodifiableMap(this.values);
    }

    @Override
    public boolean hasValue(String paramName) {
        return this.values.containsKey(paramName);
    }

    @Override
    @Nullable
    public Object getValue(String paramName) {
        if (!this.hasValue(paramName)) {
            throw new IllegalArgumentException("No value registered for key '" + paramName + "'");
        }
        return this.values.get(paramName);
    }

    @Override
    @NonNull
    public String[] getParameterNames() {
        return StringUtils.toStringArray(this.values.keySet());
    }
}

