/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.c3p0;

import com.mchange.v2.c3p0.UnifiedConnectionTester;
import java.sql.Connection;

public abstract class AbstractConnectionTester
implements UnifiedConnectionTester {
    @Override
    public abstract int activeCheckConnection(Connection var1, String var2, Throwable[] var3);

    @Override
    public abstract int statusOnException(Connection var1, Throwable var2, String var3, Throwable[] var4);

    @Override
    public int activeCheckConnection(Connection c) {
        return this.activeCheckConnection(c, null, null);
    }

    @Override
    public int activeCheckConnection(Connection c, Throwable[] rootCauseOutParamHolder) {
        return this.activeCheckConnection(c, null, rootCauseOutParamHolder);
    }

    @Override
    public int activeCheckConnection(Connection c, String preferredTestQuery) {
        return this.activeCheckConnection(c, preferredTestQuery, null);
    }

    @Override
    public int statusOnException(Connection c, Throwable t) {
        return this.statusOnException(c, t, null, null);
    }

    @Override
    public int statusOnException(Connection c, Throwable t, Throwable[] rootCauseOutParamHolder) {
        return this.statusOnException(c, t, null, rootCauseOutParamHolder);
    }

    @Override
    public int statusOnException(Connection c, Throwable t, String preferredTestQuery) {
        return this.statusOnException(c, t, preferredTestQuery, null);
    }
}

