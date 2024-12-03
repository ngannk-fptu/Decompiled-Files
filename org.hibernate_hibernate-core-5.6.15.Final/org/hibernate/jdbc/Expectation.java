/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.hibernate.HibernateException;

public interface Expectation {
    public void verifyOutcome(int var1, PreparedStatement var2, int var3, String var4) throws SQLException, HibernateException;

    public int prepare(PreparedStatement var1) throws SQLException, HibernateException;

    public boolean canBeBatched();
}

