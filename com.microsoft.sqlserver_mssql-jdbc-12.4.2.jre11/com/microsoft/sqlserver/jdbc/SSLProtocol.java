/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.text.MessageFormat;
import java.util.Locale;

enum SSLProtocol {
    TLS("TLS"),
    TLS_V10("TLSv1"),
    TLS_V11("TLSv1.1"),
    TLS_V12("TLSv1.2"),
    TLS_V13("TLSv1.3");

    private final String name;

    private SSLProtocol(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    static SSLProtocol valueOfString(String value) throws SQLServerException {
        SSLProtocol protocol = null;
        if (value.toLowerCase(Locale.ENGLISH).equalsIgnoreCase(TLS.toString())) {
            protocol = TLS;
        } else if (value.toLowerCase(Locale.ENGLISH).equalsIgnoreCase(TLS_V10.toString())) {
            protocol = TLS_V10;
        } else if (value.toLowerCase(Locale.ENGLISH).equalsIgnoreCase(TLS_V11.toString())) {
            protocol = TLS_V11;
        } else if (value.toLowerCase(Locale.ENGLISH).equalsIgnoreCase(TLS_V12.toString())) {
            protocol = TLS_V12;
        } else if (value.toLowerCase(Locale.ENGLISH).equalsIgnoreCase(TLS_V13.toString())) {
            protocol = TLS_V13;
        } else {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidSSLProtocol"));
            Object[] msgArgs = new Object[]{value};
            throw new SQLServerException(null, form.format(msgArgs), null, 0, false);
        }
        return protocol;
    }
}

