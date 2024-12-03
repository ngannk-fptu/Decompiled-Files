/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.JDBCType;
import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.text.MessageFormat;

final class DataTypes {
    static final int SHORT_VARTYPE_MAX_CHARS = 4000;
    static final int SHORT_VARTYPE_MAX_BYTES = 8000;
    static final int SQL_USHORTVARMAXLEN = 65535;
    static final int NTEXT_MAX_CHARS = 0x3FFFFFFF;
    static final int IMAGE_TEXT_MAX_BYTES = Integer.MAX_VALUE;
    static final int MAX_VARTYPE_MAX_CHARS = 0x3FFFFFFF;
    static final int MAX_VARTYPE_MAX_BYTES = Integer.MAX_VALUE;
    static final int MAXTYPE_LENGTH = 65535;
    static final int UNKNOWN_STREAM_LENGTH = -1;

    private DataTypes() {
        throw new UnsupportedOperationException(SQLServerException.getErrString("R_notSupported"));
    }

    static final void throwConversionError(String fromType, String toType) throws SQLServerException {
        MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_unsupportedConversionFromTo"));
        Object[] msgArgs = new Object[]{fromType, toType};
        SQLServerException.makeFromDriverError(null, null, form.format(msgArgs), null, true);
    }

    static final long getCheckedLength(SQLServerConnection con, JDBCType jdbcType, long length, boolean allowUnknown) throws SQLServerException {
        long maxLength;
        switch (jdbcType) {
            case NCHAR: 
            case NVARCHAR: 
            case LONGNVARCHAR: 
            case NCLOB: {
                maxLength = 0x3FFFFFFFL;
                break;
            }
            default: {
                maxLength = Integer.MAX_VALUE;
            }
        }
        if (length < (long)(allowUnknown ? -1 : 0) || length > maxLength) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidLength"));
            Object[] msgArgs = new Object[]{length};
            SQLServerException.makeFromDriverError(con, null, form.format(msgArgs), null, false);
        }
        return length;
    }
}

