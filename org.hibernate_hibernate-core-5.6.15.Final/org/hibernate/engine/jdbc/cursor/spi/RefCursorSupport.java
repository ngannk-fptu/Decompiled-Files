/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.cursor.spi;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import org.hibernate.service.Service;

public interface RefCursorSupport
extends Service {
    public void registerRefCursorParameter(CallableStatement var1, int var2);

    public void registerRefCursorParameter(CallableStatement var1, String var2);

    public ResultSet getResultSet(CallableStatement var1, int var2);

    public ResultSet getResultSet(CallableStatement var1, String var2);
}

