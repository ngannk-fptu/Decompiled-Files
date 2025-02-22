/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.persister.entity;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;
import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.jdbc.Size;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.Loadable;
import org.hibernate.type.AbstractType;
import org.hibernate.type.Type;

public class DiscriminatorType
extends AbstractType {
    private final Type underlyingType;
    private final Loadable persister;

    public DiscriminatorType(Type underlyingType, Loadable persister) {
        this.underlyingType = underlyingType;
        this.persister = persister;
    }

    @Override
    public Class getReturnedClass() {
        return Class.class;
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        return this.nullSafeGet(rs, names[0], session, owner);
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String name, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        Object discriminatorValue = this.underlyingType.nullSafeGet(rs, name, session, owner);
        String entityName = this.persister.getSubclassForDiscriminatorValue(discriminatorValue);
        if (entityName == null) {
            throw new HibernateException("Unable to resolve discriminator value [" + discriminatorValue + "] to entity name");
        }
        EntityPersister entityPersister = session.getEntityPersister(entityName, null);
        return EntityMode.POJO == entityPersister.getEntityMode() ? entityPersister.getMappedClass() : entityName;
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, boolean[] settable, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        this.nullSafeSet(st, value, index, session);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        String entityName = session.getFactory().getClassMetadata((Class)value).getEntityName();
        Loadable entityPersister = (Loadable)session.getFactory().getEntityPersister(entityName);
        this.underlyingType.nullSafeSet(st, entityPersister.getDiscriminatorValue(), index, session);
    }

    @Override
    public String toLoggableString(Object value, SessionFactoryImplementor factory) throws HibernateException {
        return value == null ? "[null]" : value.toString();
    }

    @Override
    public Object deepCopy(Object value, SessionFactoryImplementor factory) throws HibernateException {
        return value;
    }

    @Override
    public Object replace(Object original, Object target, SharedSessionContractImplementor session, Object owner, Map copyCache) throws HibernateException {
        return original;
    }

    @Override
    public boolean[] toColumnNullness(Object value, Mapping mapping) {
        return value == null ? ArrayHelper.FALSE : ArrayHelper.TRUE;
    }

    @Override
    public boolean isDirty(Object old, Object current, boolean[] checkable, SharedSessionContractImplementor session) throws HibernateException {
        return Objects.equals(old, current);
    }

    @Override
    public int[] sqlTypes(Mapping mapping) throws MappingException {
        return this.underlyingType.sqlTypes(mapping);
    }

    @Override
    public Size[] dictatedSizes(Mapping mapping) throws MappingException {
        return this.underlyingType.dictatedSizes(mapping);
    }

    @Override
    public Size[] defaultSizes(Mapping mapping) throws MappingException {
        return this.underlyingType.defaultSizes(mapping);
    }

    @Override
    public int getColumnSpan(Mapping mapping) throws MappingException {
        return this.underlyingType.getColumnSpan(mapping);
    }
}

