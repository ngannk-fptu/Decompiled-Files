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

public class ScalarResultColumnProcessor
implements ResultColumnProcessor {
    private int position = -1;
    private String alias;
    private Type type;

    public ScalarResultColumnProcessor(int position) {
        this.position = position;
    }

    public ScalarResultColumnProcessor(String alias, Type type) {
        this.alias = alias;
        this.type = type;
    }

    @Override
    public void performDiscovery(JdbcResultMetadata metadata, List<Type> types, List<String> aliases) throws SQLException {
        if (this.alias == null) {
            this.alias = metadata.getColumnName(this.position);
        } else if (this.position < 0) {
            this.position = metadata.resolveColumnPosition(this.alias);
        }
        if (this.type == null) {
            this.type = metadata.getHibernateType(this.position);
        }
        types.add(this.type);
        aliases.add(this.alias);
    }

    @Override
    public Object extract(Object[] data, ResultSet resultSet, SharedSessionContractImplementor session) throws SQLException, HibernateException {
        return this.type.nullSafeGet(resultSet, this.alias, session, null);
    }
}

