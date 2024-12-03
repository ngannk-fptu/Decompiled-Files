/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.persistence;

import java.sql.Connection;

@Deprecated
public interface DatabaseConnectionProvider {
    public Connection getConnection();
}

