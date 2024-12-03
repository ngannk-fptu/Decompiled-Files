/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.c3p0;

import java.io.Serializable;
import java.sql.Connection;

public interface ConnectionTester
extends Serializable {
    public static final int CONNECTION_IS_OKAY = 0;
    public static final int CONNECTION_IS_INVALID = -1;
    public static final int DATABASE_IS_INVALID = -8;

    public int activeCheckConnection(Connection var1);

    public int statusOnException(Connection var1, Throwable var2);

    public boolean equals(Object var1);

    public int hashCode();
}

