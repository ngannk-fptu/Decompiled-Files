/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.c3p0;

import java.sql.Connection;

public interface ConnectionCustomizer {
    public void onAcquire(Connection var1, String var2) throws Exception;

    public void onDestroy(Connection var1, String var2) throws Exception;

    public void onCheckOut(Connection var1, String var2) throws Exception;

    public void onCheckIn(Connection var1, String var2) throws Exception;
}

