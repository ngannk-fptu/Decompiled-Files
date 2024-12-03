/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.entity;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.jdbc.Size;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.entity.OuterJoinLoadable;
import org.hibernate.type.AbstractType;

public class NaturalIdType
extends AbstractType {
    private OuterJoinLoadable persister;
    private boolean[] valueNullness;

    public NaturalIdType(OuterJoinLoadable persister, boolean[] valueNullness) {
        this.persister = persister;
        this.valueNullness = valueNullness;
    }

    @Override
    public int getColumnSpan(Mapping mapping) throws MappingException {
        int span = 0;
        int i = 0;
        for (int p : this.persister.getNaturalIdentifierProperties()) {
            if (this.valueNullness[i++]) continue;
            span += this.persister.getPropertyColumnNames(p).length;
        }
        return span;
    }

    @Override
    public int[] sqlTypes(Mapping mapping) throws MappingException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Size[] dictatedSizes(Mapping mapping) throws MappingException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Size[] defaultSizes(Mapping mapping) throws MappingException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Class getReturnedClass() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isDirty(Object oldState, Object currentState, boolean[] checkable, SharedSessionContractImplementor session) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String name, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, boolean[] settable, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        Object[] keys = (Object[])value;
        int i = 0;
        for (int p : this.persister.getNaturalIdentifierProperties()) {
            if (!this.valueNullness[i]) {
                this.persister.getPropertyTypes()[p].nullSafeSet(st, keys[i], index++, session);
            }
            ++i;
        }
    }

    @Override
    public String toLoggableString(Object value, SessionFactoryImplementor factory) {
        return "natural id";
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object deepCopy(Object value, SessionFactoryImplementor factory) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isMutable() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object resolve(Object value, SharedSessionContractImplementor session, Object owner, Boolean overridingEager) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object replace(Object original, Object target, SharedSessionContractImplementor session, Object owner, Map copyCache) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean[] toColumnNullness(Object value, Mapping mapping) {
        throw new UnsupportedOperationException();
    }
}

