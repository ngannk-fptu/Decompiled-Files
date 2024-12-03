/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.text.MessageFormat;
import java.util.Locale;

final class DatetimeType
extends Enum<DatetimeType> {
    public static final /* enum */ DatetimeType DATETIME = new DatetimeType("datetime");
    public static final /* enum */ DatetimeType DATETIME2 = new DatetimeType("datetime2");
    public static final /* enum */ DatetimeType DATETIMEOFFSET = new DatetimeType("datetimeoffset");
    private final String value;
    private static final /* synthetic */ DatetimeType[] $VALUES;

    public static DatetimeType[] values() {
        return (DatetimeType[])$VALUES.clone();
    }

    public static DatetimeType valueOf(String name) {
        return Enum.valueOf(DatetimeType.class, name);
    }

    private DatetimeType(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }

    static DatetimeType valueOfString(String value) throws SQLServerException {
        DatetimeType datetimeType;
        assert (value != null);
        if ((value = value.toLowerCase(Locale.US)).equalsIgnoreCase(DATETIME.toString())) {
            datetimeType = DATETIME;
        } else if (value.equalsIgnoreCase(DATETIME2.toString())) {
            datetimeType = DATETIME2;
        } else if (value.equalsIgnoreCase(DATETIMEOFFSET.toString())) {
            datetimeType = DATETIMEOFFSET;
        } else {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidDatetimeType"));
            Object[] msgArgs = new Object[]{value};
            throw new SQLServerException(null, form.format(msgArgs), null, 0, false);
        }
        return datetimeType;
    }

    static {
        $VALUES = new DatetimeType[]{DATETIME, DATETIME2, DATETIMEOFFSET};
    }
}

