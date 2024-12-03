/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

class FedAuthDllInfo {
    byte[] accessTokenBytes = null;
    long expiresIn = 0L;

    FedAuthDllInfo(byte[] accessTokenBytes, long expiresIn) {
        this.accessTokenBytes = accessTokenBytes;
        this.expiresIn = expiresIn;
    }
}

