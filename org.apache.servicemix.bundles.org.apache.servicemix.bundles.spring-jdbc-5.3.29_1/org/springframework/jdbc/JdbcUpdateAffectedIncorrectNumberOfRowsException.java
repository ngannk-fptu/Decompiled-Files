/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.dao.IncorrectUpdateSemanticsDataAccessException
 */
package org.springframework.jdbc;

import org.springframework.dao.IncorrectUpdateSemanticsDataAccessException;

public class JdbcUpdateAffectedIncorrectNumberOfRowsException
extends IncorrectUpdateSemanticsDataAccessException {
    private final int expected;
    private final int actual;

    public JdbcUpdateAffectedIncorrectNumberOfRowsException(String sql, int expected, int actual) {
        super("SQL update '" + sql + "' affected " + actual + " rows, not " + expected + " as expected");
        this.expected = expected;
        this.actual = actual;
    }

    public int getExpectedRowsAffected() {
        return this.expected;
    }

    public int getActualRowsAffected() {
        return this.actual;
    }

    public boolean wasDataUpdated() {
        return this.getActualRowsAffected() > 0;
    }
}

