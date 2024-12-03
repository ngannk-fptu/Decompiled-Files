/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.c3p0;

import com.mchange.v2.c3p0.FullQueryConnectionTester;
import java.sql.Connection;

public interface UnifiedConnectionTester
extends FullQueryConnectionTester {
    public static final int CONNECTION_IS_OKAY = 0;
    public static final int CONNECTION_IS_INVALID = -1;
    public static final int DATABASE_IS_INVALID = -8;

    @Override
    public int activeCheckConnection(Connection var1);

    public int activeCheckConnection(Connection var1, Throwable[] var2);

    @Override
    public int activeCheckConnection(Connection var1, String var2);

    public int activeCheckConnection(Connection var1, String var2, Throwable[] var3);

    @Override
    public int statusOnException(Connection var1, Throwable var2);

    public int statusOnException(Connection var1, Throwable var2, Throwable[] var3);

    @Override
    public int statusOnException(Connection var1, Throwable var2, String var3);

    public int statusOnException(Connection var1, Throwable var2, String var3, Throwable[] var4);

    @Override
    public boolean equals(Object var1);

    @Override
    public int hashCode();
}

