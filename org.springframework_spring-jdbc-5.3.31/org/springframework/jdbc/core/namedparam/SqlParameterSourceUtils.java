/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.jdbc.core.namedparam;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.lang.Nullable;

public abstract class SqlParameterSourceUtils {
    public static SqlParameterSource[] createBatch(Object ... candidates) {
        return SqlParameterSourceUtils.createBatch(Arrays.asList(candidates));
    }

    public static SqlParameterSource[] createBatch(Collection<?> candidates) {
        SqlParameterSource[] batch = new SqlParameterSource[candidates.size()];
        int i = 0;
        for (Object candidate : candidates) {
            batch[i] = candidate instanceof Map ? new MapSqlParameterSource((Map)candidate) : new BeanPropertySqlParameterSource(candidate);
            ++i;
        }
        return batch;
    }

    public static SqlParameterSource[] createBatch(Map<String, ?>[] valueMaps) {
        SqlParameterSource[] batch = new SqlParameterSource[valueMaps.length];
        for (int i = 0; i < valueMaps.length; ++i) {
            batch[i] = new MapSqlParameterSource(valueMaps[i]);
        }
        return batch;
    }

    @Nullable
    public static Object getTypedValue(SqlParameterSource source, String parameterName) {
        int sqlType = source.getSqlType(parameterName);
        if (sqlType != Integer.MIN_VALUE) {
            return new SqlParameterValue(sqlType, source.getTypeName(parameterName), source.getValue(parameterName));
        }
        return source.getValue(parameterName);
    }

    public static Map<String, String> extractCaseInsensitiveParameterNames(SqlParameterSource parameterSource) {
        HashMap<String, String> caseInsensitiveParameterNames = new HashMap<String, String>();
        String[] paramNames = parameterSource.getParameterNames();
        if (paramNames != null) {
            for (String name : paramNames) {
                caseInsensitiveParameterNames.put(name.toLowerCase(), name);
            }
        }
        return caseInsensitiveParameterNames;
    }
}

