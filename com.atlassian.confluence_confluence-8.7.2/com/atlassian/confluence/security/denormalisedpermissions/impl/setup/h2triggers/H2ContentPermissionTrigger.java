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
import java.util.Objects;
import org.h2.api.Trigger;

public class H2ContentPermissionTrigger
implements Trigger {
    private static final int CP_TYPE_COLUMN_POSITION = 1;
    private static final int USERNAME_COLUMN_POSITION = 2;
    private static final int GROUPNAME_COLUMN_POSITION = 3;
    private static final int CPS_ID_COLUMN_POSITION = 4;

    public void init(Connection connection, String s, String s1, String s2, boolean b, int i) throws SQLException {
    }

    public void fire(Connection connection, Object[] oldValues, Object[] newValues) throws SQLException {
        boolean updateEvent;
        String INSERT_SQL = "INSERT INTO DENORMALISED_CONTENT_CHANGE_LOG   (cps_id) VALUES (?)";
        boolean bl = updateEvent = oldValues != null && newValues != null;
        if (updateEvent && Objects.equals(oldValues[1], newValues[1]) && Objects.equals(oldValues[2], newValues[2]) && Objects.equals(oldValues[3], newValues[3]) && Objects.equals(oldValues[4], newValues[4])) {
            return;
        }
        Object[] values = newValues != null ? newValues : oldValues;
        try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO DENORMALISED_CONTENT_CHANGE_LOG   (cps_id) VALUES (?)");){
            stmt.setObject(1, values[4]);
            stmt.executeUpdate();
        }
    }

    public void close() throws SQLException {
    }

    public void remove() throws SQLException {
    }
}

