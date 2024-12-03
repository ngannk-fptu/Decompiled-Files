/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.text.MessageFormat;
import java.util.Locale;

enum EncryptOption {
    FALSE("False"),
    NO("No"),
    OPTIONAL("Optional"),
    TRUE("True"),
    MANDATORY("Mandatory"),
    STRICT("Strict");

    private final String name;

    private EncryptOption(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    static EncryptOption valueOfString(String value) throws SQLServerException {
        EncryptOption option = null;
        String val = value.toLowerCase(Locale.US);
        if (val.equalsIgnoreCase(FALSE.toString()) || val.equalsIgnoreCase(NO.toString()) || val.equalsIgnoreCase(OPTIONAL.toString())) {
            option = FALSE;
        } else if (val.equalsIgnoreCase(TRUE.toString()) || val.equalsIgnoreCase(MANDATORY.toString())) {
            option = TRUE;
        } else if (val.equalsIgnoreCase(STRICT.toString())) {
            option = STRICT;
        } else {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_InvalidConnectionSetting"));
            Object[] msgArgs = new Object[]{"EncryptOption", value};
            throw new SQLServerException(form.format(msgArgs), null);
        }
        return option;
    }

    static boolean isValidEncryptOption(String option) {
        for (EncryptOption t : EncryptOption.values()) {
            if (!option.equalsIgnoreCase(t.toString())) continue;
            return true;
        }
        return false;
    }
}

