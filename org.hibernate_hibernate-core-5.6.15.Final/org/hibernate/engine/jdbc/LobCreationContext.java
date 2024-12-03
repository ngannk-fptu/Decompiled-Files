/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

public interface LobCreationContext {
    public <T> T execute(Callback<T> var1);

    public static interface Callback<T> {
        public T executeOnConnection(Connection var1) throws SQLException;
    }
}

