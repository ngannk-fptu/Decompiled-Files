/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.jdbc.core.namedparam;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public abstract class AbstractSqlParameterSource
implements SqlParameterSource {
    private final Map<String, Integer> sqlTypes = new HashMap<String, Integer>();
    private final Map<String, String> typeNames = new HashMap<String, String>();

    public void registerSqlType(String paramName, int sqlType) {
        Assert.notNull((Object)paramName, (String)"Parameter name must not be null");
        this.sqlTypes.put(paramName, sqlType);
    }

    public void registerTypeName(String paramName, String typeName) {
        Assert.notNull((Object)paramName, (String)"Parameter name must not be null");
        this.typeNames.put(paramName, typeName);
    }

    @Override
    public int getSqlType(String paramName) {
        Assert.notNull((Object)paramName, (String)"Parameter name must not be null");
        return this.sqlTypes.getOrDefault(paramName, Integer.MIN_VALUE);
    }

    @Override
    @Nullable
    public String getTypeName(String paramName) {
        Assert.notNull((Object)paramName, (String)"Parameter name must not be null");
        return this.typeNames.get(paramName);
    }

    public String toString() {
        String[] parameterNames = this.getParameterNames();
        if (parameterNames != null) {
            StringJoiner result = new StringJoiner(", ", this.getClass().getSimpleName() + " {", "}");
            for (String parameterName : parameterNames) {
                int sqlType;
                String typeName;
                Object value = this.getValue(parameterName);
                if (value instanceof SqlParameterValue) {
                    value = ((SqlParameterValue)value).getValue();
                }
                if ((typeName = this.getTypeName(parameterName)) == null && (sqlType = this.getSqlType(parameterName)) != Integer.MIN_VALUE && (typeName = JdbcUtils.resolveTypeName(sqlType)) == null) {
                    typeName = String.valueOf(sqlType);
                }
                StringBuilder entry = new StringBuilder();
                entry.append(parameterName).append('=').append(value);
                if (typeName != null) {
                    entry.append(" (type:").append(typeName).append(')');
                }
                result.add(entry);
            }
            return result.toString();
        }
        return this.getClass().getSimpleName();
    }
}

