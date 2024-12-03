/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.dao.DataAccessResourceFailureException
 *  org.springframework.lang.Nullable
 */
package org.springframework.jdbc;

import java.sql.SQLException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.lang.Nullable;

public class CannotGetJdbcConnectionException
extends DataAccessResourceFailureException {
    public CannotGetJdbcConnectionException(String msg) {
        super(msg);
    }

    public CannotGetJdbcConnectionException(String msg, @Nullable SQLException ex) {
        super(msg, (Throwable)ex);
    }

    public CannotGetJdbcConnectionException(String msg, IllegalStateException ex) {
        super(msg, (Throwable)ex);
    }
}

