/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerException;

interface ICounter {
    public void increaseCounter(long var1) throws SQLServerException;

    public void resetCounter();
}

