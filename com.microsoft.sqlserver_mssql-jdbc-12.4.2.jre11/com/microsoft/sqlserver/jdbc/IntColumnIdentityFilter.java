/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.ColumnFilter;
import com.microsoft.sqlserver.jdbc.DataTypes;
import com.microsoft.sqlserver.jdbc.JDBCType;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.Util;

class IntColumnIdentityFilter
extends ColumnFilter {
    IntColumnIdentityFilter() {
    }

    @Override
    final Object apply(Object value, JDBCType asJDBCType) throws SQLServerException {
        if (null == value) {
            return value;
        }
        switch (asJDBCType) {
            case INTEGER: 
            case SMALLINT: {
                assert (value instanceof Number);
                return Util.zeroOneToYesNo(((Number)value).intValue());
            }
            case CHAR: 
            case VARCHAR: 
            case LONGVARCHAR: {
                assert (value instanceof String);
                return Util.zeroOneToYesNo(Integer.parseInt((String)value));
            }
        }
        DataTypes.throwConversionError("char", asJDBCType.toString());
        return value;
    }
}

