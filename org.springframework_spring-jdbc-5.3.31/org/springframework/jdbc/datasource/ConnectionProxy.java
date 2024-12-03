/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jdbc.datasource;

import java.sql.Connection;

public interface ConnectionProxy
extends Connection {
    public Connection getTargetConnection();
}

