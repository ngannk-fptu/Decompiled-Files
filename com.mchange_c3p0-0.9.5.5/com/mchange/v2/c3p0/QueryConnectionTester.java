/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.c3p0;

import com.mchange.v2.c3p0.ConnectionTester;
import java.sql.Connection;

public interface QueryConnectionTester
extends ConnectionTester {
    public int activeCheckConnection(Connection var1, String var2);
}

