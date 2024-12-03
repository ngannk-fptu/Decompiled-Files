/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.custom;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Set;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.Type;

class JdbcResultMetadata {
    private final SessionFactoryImplementor factory;
    private final ResultSet resultSet;
    private final ResultSetMetaData resultSetMetaData;

    public JdbcResultMetadata(SessionFactoryImplementor factory, ResultSet resultSet) throws HibernateException {
        try {
            this.factory = factory;
            this.resultSet = resultSet;
            this.resultSetMetaData = resultSet.getMetaData();
        }
        catch (SQLException e) {
            throw new HibernateException("Could not extract result set metadata", e);
        }
    }

    public int getColumnCount() throws HibernateException {
        try {
            return this.resultSetMetaData.getColumnCount();
        }
        catch (SQLException e) {
            throw new HibernateException("Could not determine result set column count", e);
        }
    }

    public int resolveColumnPosition(String columnName) throws HibernateException {
        try {
            return this.resultSet.findColumn(columnName);
        }
        catch (SQLException e) {
            throw new HibernateException("Could not resolve column name in result set [" + columnName + "]", e);
        }
    }

    public String getColumnName(int position) throws HibernateException {
        try {
            return this.factory.getDialect().getColumnAliasExtractor().extractColumnAlias(this.resultSetMetaData, position);
        }
        catch (SQLException e) {
            throw new HibernateException("Could not resolve column name [" + position + "]", e);
        }
    }

    public Type getHibernateType(int columnPos) throws SQLException {
        String hibernateTypeName;
        Set<String> hibernateTypeNames;
        int precision;
        int columnType = this.resultSetMetaData.getColumnType(columnPos);
        int scale = this.resultSetMetaData.getScale(columnPos);
        int length = precision = this.resultSetMetaData.getPrecision(columnPos);
        if (columnType == 1 && precision == 0) {
            length = this.resultSetMetaData.getColumnDisplaySize(columnPos);
        }
        if ((hibernateTypeNames = this.factory.getMetamodel().getTypeConfiguration().getJdbcToHibernateTypeContributionMap().get(columnType)) != null && !hibernateTypeNames.isEmpty()) {
            if (hibernateTypeNames.size() > 1) {
                throw new HibernateException(String.format("There are multiple Hibernate types: [%s] registered for the [%d] JDBC type code", String.join((CharSequence)", ", hibernateTypeNames), columnType));
            }
            hibernateTypeName = hibernateTypeNames.iterator().next();
        } else {
            hibernateTypeName = this.factory.getDialect().getHibernateTypeName(columnType, length, precision, scale);
        }
        return this.factory.getTypeResolver().heuristicType(hibernateTypeName);
    }
}

