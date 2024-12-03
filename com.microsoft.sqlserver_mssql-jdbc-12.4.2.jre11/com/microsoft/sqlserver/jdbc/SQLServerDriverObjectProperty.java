/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

enum SQLServerDriverObjectProperty {
    GSS_CREDENTIAL("gsscredential", null),
    ACCESS_TOKEN_CALLBACK("accessTokenCallback", null);

    private final String name;
    private final String defaultValue;

    private SQLServerDriverObjectProperty(String name, String defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public String getDefaultValue() {
        return this.defaultValue;
    }

    public String toString() {
        return this.name;
    }
}

