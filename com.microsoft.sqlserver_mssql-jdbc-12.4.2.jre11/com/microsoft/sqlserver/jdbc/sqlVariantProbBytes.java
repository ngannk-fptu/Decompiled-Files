/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.text.MessageFormat;

enum sqlVariantProbBytes {
    INTN(0),
    INT8(0),
    INT4(0),
    INT2(0),
    INT1(0),
    FLOAT4(0),
    FLOAT8(0),
    DATETIME4(0),
    DATETIME8(0),
    MONEY4(0),
    MONEY8(0),
    BITN(0),
    GUID(0),
    DATEN(0),
    TIMEN(1),
    DATETIME2N(1),
    DECIMALN(2),
    NUMERICN(2),
    BIGBINARY(2),
    BIGVARBINARY(2),
    BIGCHAR(7),
    BIGVARCHAR(7),
    NCHAR(7),
    NVARCHAR(7);

    private final int intValue;
    private static final int MAXELEMENTS = 23;
    private static final sqlVariantProbBytes[] valuesTypes;

    private sqlVariantProbBytes(int intValue) {
        this.intValue = intValue;
    }

    int getIntValue() {
        return this.intValue;
    }

    static sqlVariantProbBytes valueOf(int intValue) {
        sqlVariantProbBytes tdsType;
        if (0 > intValue || intValue >= valuesTypes.length || null == (tdsType = valuesTypes[intValue])) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_unknownSSType"));
            Object[] msgArgs = new Object[]{intValue};
            throw new IllegalArgumentException(form.format(msgArgs));
        }
        return tdsType;
    }

    static {
        valuesTypes = new sqlVariantProbBytes[23];
    }
}

