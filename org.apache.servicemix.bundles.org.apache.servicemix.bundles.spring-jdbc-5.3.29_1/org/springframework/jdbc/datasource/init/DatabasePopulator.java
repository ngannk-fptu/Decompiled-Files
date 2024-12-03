/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jdbc.datasource.init;

import java.sql.Connection;
import java.sql.SQLException;
import org.springframework.jdbc.datasource.init.ScriptException;

@FunctionalInterface
public interface DatabasePopulator {
    public void populate(Connection var1) throws SQLException, ScriptException;
}

