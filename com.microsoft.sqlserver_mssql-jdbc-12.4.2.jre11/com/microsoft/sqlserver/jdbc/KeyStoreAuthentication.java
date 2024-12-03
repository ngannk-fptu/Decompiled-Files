/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.text.MessageFormat;
import java.util.Locale;

enum KeyStoreAuthentication {
    JAVA_KEYSTORE_PASSWORD("JavaKeyStorePassword"),
    KEYVAULT_CLIENT_SECRET("KeyVaultClientSecret"),
    KEYVAULT_MANAGED_IDENTITY("KeyVaultManagedIdentity");

    private final String name;

    private KeyStoreAuthentication(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    static KeyStoreAuthentication valueOfString(String value) throws SQLServerException {
        KeyStoreAuthentication method = null;
        if (value.toLowerCase(Locale.US).equalsIgnoreCase(JAVA_KEYSTORE_PASSWORD.toString())) {
            method = JAVA_KEYSTORE_PASSWORD;
        } else if (value.toLowerCase(Locale.US).equalsIgnoreCase(KEYVAULT_CLIENT_SECRET.toString())) {
            method = KEYVAULT_CLIENT_SECRET;
        } else if (value.toLowerCase(Locale.US).equalsIgnoreCase(KEYVAULT_MANAGED_IDENTITY.toString())) {
            method = KEYVAULT_MANAGED_IDENTITY;
        } else {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_InvalidConnectionSetting"));
            Object[] msgArgs = new Object[]{"keyStoreAuthentication", value};
            throw new SQLServerException(form.format(msgArgs), null);
        }
        return method;
    }
}

