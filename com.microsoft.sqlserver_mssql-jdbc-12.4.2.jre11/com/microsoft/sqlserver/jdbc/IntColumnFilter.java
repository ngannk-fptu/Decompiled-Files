/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.ColumnFilter;
import com.microsoft.sqlserver.jdbc.DataTypes;
import com.microsoft.sqlserver.jdbc.JDBCType;
import com.microsoft.sqlserver.jdbc.SQLServerException;

abstract class IntColumnFilter
extends ColumnFilter {
    IntColumnFilter() {
    }

    abstract int oneValueToAnother(int var1);

    @Override
    final Object apply(Object value, JDBCType asJDBCType) throws SQLServerException {
        if (null == value) {
            return value;
        }
        switch (asJDBCType) {
            case INTEGER: {
                return this.oneValueToAnother((Integer)value);
            }
            case SMALLINT: 
            case TINYINT: {
                return (short)this.oneValueToAnother(((Short)value).intValue());
            }
            case BIGINT: {
                return (long)this.oneValueToAnother(((Long)value).intValue());
            }
            case CHAR: 
            case VARCHAR: 
            case LONGVARCHAR: {
                return Integer.toString(this.oneValueToAnother(Integer.parseInt((String)value)));
            }
        }
        DataTypes.throwConversionError("int", asJDBCType.toString());
        return value;
    }
}

