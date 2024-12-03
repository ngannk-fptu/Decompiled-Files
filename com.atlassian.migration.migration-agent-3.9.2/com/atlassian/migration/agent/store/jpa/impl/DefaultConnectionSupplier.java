/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.store.jpa.impl;

import com.atlassian.migration.agent.store.jpa.impl.ConnectionSupplier;
import com.atlassian.migration.agent.store.jpa.interfaces.ConnectionHelper;
import java.sql.Connection;
import java.sql.SQLException;

public class DefaultConnectionSupplier
implements ConnectionSupplier {
    private final ConnectionHelper connectionHelper;

    public DefaultConnectionSupplier(ConnectionHelper connectionHelper) {
        this.connectionHelper = connectionHelper;
    }

    @Override
    public Connection supply() throws SQLException {
        return this.connectionHelper.getConnection();
    }
}

