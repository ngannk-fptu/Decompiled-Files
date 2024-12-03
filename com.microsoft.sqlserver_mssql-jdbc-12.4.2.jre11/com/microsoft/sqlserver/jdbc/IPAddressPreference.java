/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.text.MessageFormat;
import java.util.Locale;

enum IPAddressPreference {
    IPV4_FIRST("IPv4First"),
    IPV6_FIRST("IPv6First"),
    USE_PLATFORM_DEFAULT("UsePlatformDefault");

    private final String name;

    private IPAddressPreference(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    static IPAddressPreference valueOfString(String value) throws SQLServerException {
        IPAddressPreference iptype = null;
        if (value.toLowerCase(Locale.US).equalsIgnoreCase(IPV4_FIRST.toString())) {
            iptype = IPV4_FIRST;
        } else if (value.toLowerCase(Locale.US).equalsIgnoreCase(IPV6_FIRST.toString())) {
            iptype = IPV6_FIRST;
        } else if (value.toLowerCase(Locale.US).equalsIgnoreCase(USE_PLATFORM_DEFAULT.toString())) {
            iptype = USE_PLATFORM_DEFAULT;
        } else {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_InvalidIPAddressPreference"));
            Object[] msgArgs = new Object[]{value};
            throw new SQLServerException(form.format(msgArgs), null);
        }
        return iptype;
    }
}

