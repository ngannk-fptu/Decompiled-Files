/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

enum DriverError {
    NOT_SET(0);

    private final int errorCode;

    final int getErrorCode() {
        return this.errorCode;
    }

    private DriverError(int errorCode) {
        this.errorCode = errorCode;
    }
}

