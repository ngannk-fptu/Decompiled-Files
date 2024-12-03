/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.newexport;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface PrimaryKeyExtractor {
    public Object getPrimaryKey(ResultSet var1) throws SQLException;
}

