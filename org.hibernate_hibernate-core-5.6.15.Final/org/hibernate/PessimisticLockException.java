/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate;

import java.sql.SQLException;
import org.hibernate.JDBCException;

public class PessimisticLockException
extends JDBCException {
    public PessimisticLockException(String message, SQLException sqlException, String sql) {
        super(message, sqlException, sql);
    }
}

