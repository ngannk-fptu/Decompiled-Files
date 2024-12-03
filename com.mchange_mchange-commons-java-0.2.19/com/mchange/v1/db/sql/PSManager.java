/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.db.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;

public interface PSManager {
    public PreparedStatement getPS(Connection var1, String var2);

    public void putPS(Connection var1, String var2, PreparedStatement var3);
}

