/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.c3p0;

import com.mchange.v2.c3p0.QueryConnectionTester;
import java.sql.Connection;

public interface FullQueryConnectionTester
extends QueryConnectionTester {
    public int statusOnException(Connection var1, Throwable var2, String var3);
}

