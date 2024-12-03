/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SqlAuthenticationToken;

public interface SQLServerAccessTokenCallback {
    public SqlAuthenticationToken getAccessToken(String var1, String var2);
}

