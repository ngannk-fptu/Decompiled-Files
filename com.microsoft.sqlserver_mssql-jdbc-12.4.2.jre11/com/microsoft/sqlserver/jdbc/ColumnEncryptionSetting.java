/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.text.MessageFormat;
import java.util.Locale;

enum ColumnEncryptionSetting {
    ENABLED("Enabled"),
    DISABLED("Disabled");

    private final String name;

    private ColumnEncryptionSetting(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    static ColumnEncryptionSetting valueOfString(String value) throws SQLServerException {
        ColumnEncryptionSetting method = null;
        if (value.toLowerCase(Locale.US).equalsIgnoreCase(ENABLED.toString())) {
            method = ENABLED;
        } else if (value.toLowerCase(Locale.US).equalsIgnoreCase(DISABLED.toString())) {
            method = DISABLED;
        } else {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_InvalidConnectionSetting"));
            Object[] msgArgs = new Object[]{"columnEncryptionSetting", value};
            throw new SQLServerException(form.format(msgArgs), null);
        }
        return method;
    }
}

