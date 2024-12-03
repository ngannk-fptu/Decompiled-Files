/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.text.MessageFormat;

enum SQLServerEncryptionType {
    DETERMINISTIC(1),
    RANDOMIZED(2),
    PLAINTEXT(0);

    final byte value;
    private static final SQLServerEncryptionType[] VALUES;

    private SQLServerEncryptionType(byte val) {
        this.value = val;
    }

    byte getValue() {
        return this.value;
    }

    static SQLServerEncryptionType of(byte val) throws SQLServerException {
        for (SQLServerEncryptionType type : VALUES) {
            if (val != type.value) continue;
            return type;
        }
        MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_unknownColumnEncryptionType"));
        Object[] msgArgs = new Object[]{val};
        SQLServerException.makeFromDriverError(null, null, form.format(msgArgs), null, true);
        return null;
    }

    static {
        VALUES = SQLServerEncryptionType.values();
    }
}

