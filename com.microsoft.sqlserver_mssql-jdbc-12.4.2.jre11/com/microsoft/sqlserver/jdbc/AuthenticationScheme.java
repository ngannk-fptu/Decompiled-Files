/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.text.MessageFormat;
import java.util.Locale;

enum AuthenticationScheme {
    NATIVE_AUTHENTICATION("nativeAuthentication"),
    NTLM("ntlm"),
    JAVA_KERBEROS("javaKerberos");

    private final String name;

    private AuthenticationScheme(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    static AuthenticationScheme valueOfString(String value) throws SQLServerException {
        AuthenticationScheme scheme;
        if (value.toLowerCase(Locale.US).equalsIgnoreCase(JAVA_KERBEROS.toString())) {
            scheme = JAVA_KERBEROS;
        } else if (value.toLowerCase(Locale.US).equalsIgnoreCase(NATIVE_AUTHENTICATION.toString())) {
            scheme = NATIVE_AUTHENTICATION;
        } else if (value.toLowerCase(Locale.US).equalsIgnoreCase(NTLM.toString())) {
            scheme = NTLM;
        } else {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidAuthenticationScheme"));
            Object[] msgArgs = new Object[]{value};
            throw new SQLServerException(null, form.format(msgArgs), null, 0, false);
        }
        return scheme;
    }
}

