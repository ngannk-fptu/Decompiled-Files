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
import org.hibernate.loader.custom.ResultColumnProcessor;
import org.hibernate.type.Type;

public class NonScalarResultColumnProcessor
implements ResultColumnProcessor {
    private final int position;

    public NonScalarResultColumnProcessor(int position) {
        this.position = position;
    }

    @Override
    public void performDiscovery(JdbcResultMetadata metadata, List<Type> types, List<String> aliases) {
    }

    @Override
    public Object extract(Object[] data, ResultSet resultSet, SharedSessionContractImplementor session) throws SQLException, HibernateException {
        return data[this.position];
    }
}

