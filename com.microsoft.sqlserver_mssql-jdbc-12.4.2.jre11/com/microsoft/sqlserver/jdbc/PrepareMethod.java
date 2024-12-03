/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerDriverStringProperty;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.text.MessageFormat;

final class PrepareMethod
extends Enum<PrepareMethod> {
    public static final /* enum */ PrepareMethod PREPEXEC = new PrepareMethod("prepexec");
    public static final /* enum */ PrepareMethod PREPARE = new PrepareMethod("prepare");
    private final String value;
    private static final /* synthetic */ PrepareMethod[] $VALUES;

    public static PrepareMethod[] values() {
        return (PrepareMethod[])$VALUES.clone();
    }

    public static PrepareMethod valueOf(String name) {
        return Enum.valueOf(PrepareMethod.class, name);
    }

    private PrepareMethod(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }

    static PrepareMethod valueOfString(String value) throws SQLServerException {
        assert (value != null);
        for (PrepareMethod prepareMethod : PrepareMethod.values()) {
            if (!prepareMethod.toString().equalsIgnoreCase(value)) continue;
            return prepareMethod;
        }
        MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_InvalidConnectionSetting"));
        Object[] msgArgs = new Object[]{SQLServerDriverStringProperty.PREPARE_METHOD.toString(), value};
        throw new SQLServerException(form.format(msgArgs), null);
    }

    static {
        $VALUES = new PrepareMethod[]{PREPEXEC, PREPARE};
    }
}

