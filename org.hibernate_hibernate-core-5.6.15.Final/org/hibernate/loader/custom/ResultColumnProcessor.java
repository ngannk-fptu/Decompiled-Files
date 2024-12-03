/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.custom;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.loader.custom.JdbcResultMetadata;
import org.hibernate.type.Type;

interface ResultColumnProcessor {
    public void performDiscovery(JdbcResultMetadata var1, List<Type> var2, List<String> var3) throws SQLException, HibernateException;

    public Object extract(Object[] var1, ResultSet var2, SharedSessionContractImplementor var3) throws SQLException, HibernateException;
}

