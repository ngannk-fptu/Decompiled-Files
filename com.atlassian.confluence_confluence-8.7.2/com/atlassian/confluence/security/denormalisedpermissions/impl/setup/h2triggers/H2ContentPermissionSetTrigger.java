/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.h2.api.Trigger
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl.setup.h2triggers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.h2.api.Trigger;

public class H2ContentPermissionSetTrigger
implements Trigger {
    private static final int CPS_ID_COLUMN_POSITION = 0;
    private static final int CONTENT_ID_COLUMN_POSITION = 2;

    public void init(Connection connection, String s, String s1, String s2, boolean b, int i) throws SQLException {
    }

    public void fire(Connection connection, Object[] oldValues, Object[] newValues) throws SQLException {
        boolean deleteEvent;
        String INSERT_SQL = "INSERT INTO DENORMALISED_CONTENT_CHANGE_LOG   (content_id, cps_id) VALUES (?, ?)";
        boolean bl = deleteEvent = newValues == null;
        if (deleteEvent) {
            try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO DENORMALISED_CONTENT_CHANGE_LOG   (content_id, cps_id) VALUES (?, ?)");){
                stmt.setObject(1, oldValues[2]);
                stmt.setObject(2, oldValues[0]);
                stmt.executeUpdate();
            }
        }
    }

    public void close() throws SQLException {
    }

    public void remove() throws SQLException {
    }
}

