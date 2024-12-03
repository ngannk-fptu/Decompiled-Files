/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.store.jpa.impl;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionSupplier {
    public Connection supply() throws SQLException;
}

