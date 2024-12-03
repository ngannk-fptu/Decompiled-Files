/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jdbc.core;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface RowCallbackHandler {
    public void processRow(ResultSet var1) throws SQLException;
}

