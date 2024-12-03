/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.text.MessageFormat;
import java.util.Locale;

final class ApplicationIntent
extends Enum<ApplicationIntent> {
    public static final /* enum */ ApplicationIntent READ_WRITE = new ApplicationIntent("readwrite");
    public static final /* enum */ ApplicationIntent READ_ONLY = new ApplicationIntent("readonly");
    private final String value;
    private static final /* synthetic */ ApplicationIntent[] $VALUES;

    public static ApplicationIntent[] values() {
        return (ApplicationIntent[])$VALUES.clone();
    }

    public static ApplicationIntent valueOf(String name) {
        return Enum.valueOf(ApplicationIntent.class, name);
    }

    private ApplicationIntent(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }

    static ApplicationIntent valueOfString(String value) throws SQLServerException {
        ApplicationIntent applicationIntent;
        assert (value != null);
        if ((value = value.toUpperCase(Locale.US).toLowerCase(Locale.US)).equalsIgnoreCase(READ_ONLY.toString())) {
            applicationIntent = READ_ONLY;
        } else if (value.equalsIgnoreCase(READ_WRITE.toString())) {
            applicationIntent = READ_WRITE;
        } else {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidapplicationIntent"));
            Object[] msgArgs = new Object[]{value};
            throw new SQLServerException(null, form.format(msgArgs), null, 0, false);
        }
        return applicationIntent;
    }

    static {
        $VALUES = new ApplicationIntent[]{READ_WRITE, READ_ONLY};
    }
}

