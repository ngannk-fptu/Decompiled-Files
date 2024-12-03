/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.IntColumnFilter;
import com.microsoft.sqlserver.jdbc.JDBCType;
import com.microsoft.sqlserver.jdbc.SSType;

final class DataTypeFilter
extends IntColumnFilter {
    private static final int ODBC_SQL_GUID = -11;
    private static final int ODBC_SQL_WCHAR = -8;
    private static final int ODBC_SQL_WVARCHAR = -9;
    private static final int ODBC_SQL_WLONGVARCHAR = -10;
    private static final int ODBC_SQL_FLOAT = 6;
    private static final int ODBC_SQL_TIME = -154;
    private static final int ODBC_SQL_XML = -152;
    private static final int ODBC_SQL_UDT = -151;

    DataTypeFilter() {
    }

    @Override
    int oneValueToAnother(int odbcType) {
        switch (odbcType) {
            case 6: {
                return JDBCType.DOUBLE.asJavaSqlType();
            }
            case -11: {
                return JDBCType.CHAR.asJavaSqlType();
            }
            case -8: {
                return JDBCType.NCHAR.asJavaSqlType();
            }
            case -9: {
                return JDBCType.NVARCHAR.asJavaSqlType();
            }
            case -10: {
                return JDBCType.LONGNVARCHAR.asJavaSqlType();
            }
            case -154: {
                return JDBCType.TIME.asJavaSqlType();
            }
            case -152: {
                return SSType.XML.getJDBCType().asJavaSqlType();
            }
            case -151: {
                return SSType.UDT.getJDBCType().asJavaSqlType();
            }
        }
        return odbcType;
    }
}

