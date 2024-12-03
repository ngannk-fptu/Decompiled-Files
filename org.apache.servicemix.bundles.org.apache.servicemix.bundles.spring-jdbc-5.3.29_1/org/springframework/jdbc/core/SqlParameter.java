/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.jdbc.core;

import java.util.ArrayList;
import java.util.List;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class SqlParameter {
    @Nullable
    private String name;
    private final int sqlType;
    @Nullable
    private String typeName;
    @Nullable
    private Integer scale;

    public SqlParameter(int sqlType) {
        this.sqlType = sqlType;
    }

    public SqlParameter(int sqlType, @Nullable String typeName) {
        this.sqlType = sqlType;
        this.typeName = typeName;
    }

    public SqlParameter(int sqlType, int scale) {
        this.sqlType = sqlType;
        this.scale = scale;
    }

    public SqlParameter(String name, int sqlType) {
        this.name = name;
        this.sqlType = sqlType;
    }

    public SqlParameter(String name, int sqlType, @Nullable String typeName) {
        this.name = name;
        this.sqlType = sqlType;
        this.typeName = typeName;
    }

    public SqlParameter(String name, int sqlType, int scale) {
        this.name = name;
        this.sqlType = sqlType;
        this.scale = scale;
    }

    public SqlParameter(SqlParameter otherParam) {
        Assert.notNull((Object)otherParam, (String)"SqlParameter object must not be null");
        this.name = otherParam.name;
        this.sqlType = otherParam.sqlType;
        this.typeName = otherParam.typeName;
        this.scale = otherParam.scale;
    }

    @Nullable
    public String getName() {
        return this.name;
    }

    public int getSqlType() {
        return this.sqlType;
    }

    @Nullable
    public String getTypeName() {
        return this.typeName;
    }

    @Nullable
    public Integer getScale() {
        return this.scale;
    }

    public boolean isInputValueProvided() {
        return true;
    }

    public boolean isResultsParameter() {
        return false;
    }

    public static List<SqlParameter> sqlTypesToAnonymousParameterList(int ... types) {
        if (types == null) {
            return new ArrayList<SqlParameter>();
        }
        ArrayList<SqlParameter> result = new ArrayList<SqlParameter>(types.length);
        for (int type : types) {
            result.add(new SqlParameter(type));
        }
        return result;
    }
}

