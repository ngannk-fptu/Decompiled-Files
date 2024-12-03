/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.c3p0;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.sql.Statement;

public interface C3P0ProxyStatement
extends Statement {
    public static final Object RAW_STATEMENT = new Object();

    public Object rawStatementOperation(Method var1, Object var2, Object[] var3) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, SQLException;
}

