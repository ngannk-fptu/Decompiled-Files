/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jdbc.support;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface SqlValue {
    public void setValue(PreparedStatement var1, int var2) throws SQLException;

    public void cleanup();
}

