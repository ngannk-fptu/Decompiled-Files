/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.connections.internal;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionValidator {
    public static final ConnectionValidator ALWAYS_VALID = connection -> true;

    public boolean isValid(Connection var1) throws SQLException;
}

