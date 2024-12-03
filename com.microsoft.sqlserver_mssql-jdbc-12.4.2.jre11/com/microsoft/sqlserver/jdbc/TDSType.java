/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.text.MessageFormat;

enum TDSType {
    BIT1(50),
    INT8(127),
    INT4(56),
    INT2(52),
    INT1(48),
    FLOAT4(59),
    FLOAT8(62),
    DATETIME4(58),
    DATETIME8(61),
    MONEY4(122),
    MONEY8(60),
    BITN(104),
    INTN(38),
    DECIMALN(106),
    NUMERICN(108),
    FLOATN(109),
    MONEYN(110),
    DATETIMEN(111),
    GUID(36),
    DATEN(40),
    TIMEN(41),
    DATETIME2N(42),
    DATETIMEOFFSETN(43),
    BIGCHAR(175),
    BIGVARCHAR(167),
    BIGBINARY(173),
    BIGVARBINARY(165),
    NCHAR(239),
    NVARCHAR(231),
    IMAGE(34),
    TEXT(35),
    NTEXT(99),
    UDT(240),
    XML(241),
    SQL_VARIANT(98);

    private final int intValue;
    private static final int MAXELEMENTS = 256;
    private static final TDSType[] VALUES;
    private static final TDSType[] valuesTypes;

    byte byteValue() {
        return (byte)this.intValue;
    }

    private TDSType(int intValue) {
        this.intValue = intValue;
    }

    static TDSType valueOf(int intValue) throws IllegalArgumentException {
        TDSType tdsType;
        if (0 > intValue || intValue >= valuesTypes.length || null == (tdsType = valuesTypes[intValue])) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_unknownSSType"));
            Object[] msgArgs = new Object[]{intValue};
            throw new IllegalArgumentException(form.format(msgArgs));
        }
        return tdsType;
    }

    static {
        VALUES = TDSType.values();
        valuesTypes = new TDSType[256];
        TDSType[] tDSTypeArray = VALUES;
        int n = tDSTypeArray.length;
        for (int i = 0; i < n; ++i) {
            TDSType s;
            TDSType.valuesTypes[s.intValue] = s = tDSTypeArray[i];
        }
    }
}

