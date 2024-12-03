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

public class H2ContentTrigger
implements Trigger {
    private static final int CONTENTID_COLUMN_POSITION = 0;
    private static final int CONTENTTYPE_COLUMN_POSITION = 2;
    private static final int TITLE_COLUMN_POSITION = 3;
    private static final int CREATIONDATE_COLUMN_POSITION = 7;
    private static final int LASTMODDATE_COLUMN_POSITION = 9;
    private static final int PREVVER_COLUMN_POSITION = 11;
    private static final int CONTENT_STATUS_COLUMN_POSITION = 12;
    private static final int SPACEID_COLUMN_POSITION = 14;
    private static final int CHILD_POSITION_COLUMN_POSITION = 15;
    private static final int PARENTID_COLUMN_POSITION = 16;

    public void init(Connection connection, String s, String s1, String s2, boolean b, int i) throws SQLException {
    }

    public void fire(Connection connection, Object[] oldValues, Object[] newValues) throws SQLException {
        Object[] values;
        String INSERT_SQL = "INSERT INTO DENORMALISED_CONTENT_CHANGE_LOG   (content_id) VALUES (?)";
        boolean deleteEvent = oldValues != null && newValues == null;
        Object[] objectArray = values = deleteEvent ? oldValues : newValues;
        if (!values[2].toString().equalsIgnoreCase("PAGE") || values[11] != null) {
            return;
        }
        try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO DENORMALISED_CONTENT_CHANGE_LOG   (content_id) VALUES (?)");){
            stmt.setObject(1, values[0]);
            stmt.executeUpdate();
        }
    }

    public void close() throws SQLException {
    }

    public void remove() throws SQLException {
    }
}

