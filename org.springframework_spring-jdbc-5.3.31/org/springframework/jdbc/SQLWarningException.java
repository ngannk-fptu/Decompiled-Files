/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.dao.UncategorizedDataAccessException
 */
package org.springframework.jdbc;

import java.sql.SQLWarning;
import org.springframework.dao.UncategorizedDataAccessException;

public class SQLWarningException
extends UncategorizedDataAccessException {
    public SQLWarningException(String msg, SQLWarning ex) {
        super(msg, (Throwable)ex);
    }

    public SQLWarning getSQLWarning() {
        return (SQLWarning)this.getCause();
    }

    @Deprecated
    public SQLWarning SQLWarning() {
        return this.getSQLWarning();
    }
}

