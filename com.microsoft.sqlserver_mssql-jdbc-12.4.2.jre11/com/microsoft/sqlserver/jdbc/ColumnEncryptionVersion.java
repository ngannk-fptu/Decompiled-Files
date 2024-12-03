/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

enum ColumnEncryptionVersion {
    AE_NOTSUPPORTED,
    AE_V1,
    AE_V2,
    AE_V3;


    int value() {
        return this.ordinal() + 1;
    }
}

