/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.h2.api.Trigger
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl.setup.h2triggers;

import java.sql.Connection;
import java.sql.SQLException;
import org.h2.api.Trigger;

public class H2EmptyTrigger
implements Trigger {
    public void init(Connection connection, String s, String s1, String s2, boolean b, int i) throws SQLException {
    }

    public void fire(Connection connection, Object[] oldValues, Object[] newValues) throws SQLException {
    }

    public void close() throws SQLException {
    }

    public void remove() throws SQLException {
    }
}

