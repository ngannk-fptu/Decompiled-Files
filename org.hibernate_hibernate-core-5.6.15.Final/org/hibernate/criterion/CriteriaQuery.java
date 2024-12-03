/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.criterion;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.cfg.NotYetImplementedException;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.TypedValue;
import org.hibernate.type.Type;

public interface CriteriaQuery {
    public SessionFactoryImplementor getFactory();

    public String getColumn(Criteria var1, String var2) throws HibernateException;

    public String[] getColumns(String var1, Criteria var2) throws HibernateException;

    public String[] findColumns(String var1, Criteria var2) throws HibernateException;

    public Type getType(Criteria var1, String var2) throws HibernateException;

    public String[] getColumnsUsingProjection(Criteria var1, String var2) throws HibernateException;

    public Type getTypeUsingProjection(Criteria var1, String var2) throws HibernateException;

    public TypedValue getTypedValue(Criteria var1, String var2, Object var3) throws HibernateException;

    public String getEntityName(Criteria var1);

    public String getEntityName(Criteria var1, String var2);

    public String getSQLAlias(Criteria var1);

    public String getSQLAlias(Criteria var1, String var2);

    public String getPropertyName(String var1);

    public String[] getIdentifierColumns(Criteria var1);

    public Type getIdentifierType(Criteria var1);

    public TypedValue getTypedIdentifierValue(Criteria var1, Object var2);

    public String generateSQLAlias();

    default public Type getForeignKeyType(Criteria criteria, String associationPropertyName) {
        throw new NotYetImplementedException("CriteriaQuery#getForeignKeyType() has not been yet implemented!");
    }

    default public String[] getForeignKeyColumns(Criteria criteria, String associationPropertyName) {
        throw new NotYetImplementedException("CriteriaQuery#getForeignKeyColumns() has not been yet implemented!");
    }

    default public TypedValue getForeignKeyTypeValue(Criteria criteria, String associationPropertyName, Object value) {
        throw new NotYetImplementedException("CriteriaQuery#getForeignKeyTypeValue() has not been yet implemented!");
    }
}

