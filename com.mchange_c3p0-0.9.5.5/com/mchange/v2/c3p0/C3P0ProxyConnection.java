/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.c3p0;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;

public interface C3P0ProxyConnection
extends Connection {
    public static final Object RAW_CONNECTION = new Object();

    public Object rawConnectionOperation(Method var1, Object var2, Object[] var3) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, SQLException;
}

