/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mchange.v1.util.ClosableResource
 */
package com.mchange.v2.c3p0.stmt;

import com.mchange.v1.util.ClosableResource;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;

public interface StatementCache
extends ClosableResource {
    public Object checkoutStatement(Connection var1, Method var2, Object[] var3) throws SQLException;

    public void checkinStatement(Object var1) throws SQLException;

    public void checkinAll(Connection var1) throws SQLException;

    public void closeAll(Connection var1) throws SQLException;

    public void close() throws SQLException;
}

