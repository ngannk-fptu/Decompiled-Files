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

public class H2SpaceTrigger
implements Trigger {
    private static final int SPACE_ID_COLUMN_POSITION = 0;
    private static final int LOWER_SPACE_KEY_POSITION = 3;

    public void init(Connection connection, String s, String s1, String s2, boolean b, int i) throws SQLException {
    }

    public void fire(Connection connection, Object[] oldValues, Object[] newValues) throws SQLException {
        boolean updateEvent;
        String INSERT_SQL = "INSERT INTO DENORMALISED_SPACE_CHANGE_LOG (space_id) VALUES (?)";
        boolean bl = updateEvent = oldValues != null && newValues != null;
        if (updateEvent && Objects.equals(oldValues[3], newValues[3])) {
            return;
        }
        Object[] values = newValues != null ? newValues : oldValues;
        try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO DENORMALISED_SPACE_CHANGE_LOG (space_id) VALUES (?)");){
            stmt.setObject(1, values[0]);
            stmt.executeUpdate();
        }
    }

    public void close() throws SQLException {
    }

    public void remove() throws SQLException {
    }
}

