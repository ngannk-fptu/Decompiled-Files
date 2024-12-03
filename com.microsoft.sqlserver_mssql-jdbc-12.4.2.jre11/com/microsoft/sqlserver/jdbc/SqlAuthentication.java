/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerDriver;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.text.MessageFormat;
import java.util.Locale;

enum SqlAuthentication {
    NOT_SPECIFIED("NotSpecified"),
    SQLPASSWORD("SqlPassword"),
    ACTIVE_DIRECTORY_PASSWORD("ActiveDirectoryPassword"),
    ACTIVE_DIRECTORY_INTEGRATED("ActiveDirectoryIntegrated"),
    ACTIVE_DIRECTORY_MANAGED_IDENTITY("ActiveDirectoryManagedIdentity"),
    ACTIVE_DIRECTORY_SERVICE_PRINCIPAL("ActiveDirectoryServicePrincipal"),
    ACTIVE_DIRECTORY_SERVICE_PRINCIPAL_CERTIFICATE("ActiveDirectoryServicePrincipalCertificate"),
    ACTIVE_DIRECTORY_INTERACTIVE("ActiveDirectoryInteractive"),
    ACTIVE_DIRECTORY_DEFAULT("ActiveDirectoryDefault");

    private final String name;

    private SqlAuthentication(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    static SqlAuthentication valueOfString(String value) throws SQLServerException {
        SqlAuthentication method = null;
        if (value.toLowerCase(Locale.US).equalsIgnoreCase(NOT_SPECIFIED.toString())) {
            method = NOT_SPECIFIED;
        } else if (value.toLowerCase(Locale.US).equalsIgnoreCase(SQLPASSWORD.toString())) {
            method = SQLPASSWORD;
        } else if (value.toLowerCase(Locale.US).equalsIgnoreCase(ACTIVE_DIRECTORY_PASSWORD.toString())) {
            method = ACTIVE_DIRECTORY_PASSWORD;
        } else if (value.toLowerCase(Locale.US).equalsIgnoreCase(ACTIVE_DIRECTORY_INTEGRATED.toString())) {
            method = ACTIVE_DIRECTORY_INTEGRATED;
        } else if (value.toLowerCase(Locale.US).equalsIgnoreCase(ACTIVE_DIRECTORY_MANAGED_IDENTITY.toString()) || SQLServerDriver.getNormalizedPropertyValueName(value).toLowerCase(Locale.US).equalsIgnoreCase(ACTIVE_DIRECTORY_MANAGED_IDENTITY.toString())) {
            method = ACTIVE_DIRECTORY_MANAGED_IDENTITY;
        } else if (value.toLowerCase(Locale.US).equalsIgnoreCase(ACTIVE_DIRECTORY_SERVICE_PRINCIPAL.toString())) {
            method = ACTIVE_DIRECTORY_SERVICE_PRINCIPAL;
        } else if (value.toLowerCase(Locale.US).equalsIgnoreCase(ACTIVE_DIRECTORY_SERVICE_PRINCIPAL_CERTIFICATE.toString())) {
            method = ACTIVE_DIRECTORY_SERVICE_PRINCIPAL_CERTIFICATE;
        } else if (value.toLowerCase(Locale.US).equalsIgnoreCase(ACTIVE_DIRECTORY_INTERACTIVE.toString())) {
            method = ACTIVE_DIRECTORY_INTERACTIVE;
        } else if (value.toLowerCase(Locale.US).equalsIgnoreCase(ACTIVE_DIRECTORY_DEFAULT.toString())) {
            method = ACTIVE_DIRECTORY_DEFAULT;
        } else {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_InvalidConnectionSetting"));
            Object[] msgArgs = new Object[]{"authentication", value};
            throw new SQLServerException(null, form.format(msgArgs), null, 0, false);
        }
        return method;
    }
}

