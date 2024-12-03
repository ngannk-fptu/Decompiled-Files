/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.jdbc.Size;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.AbstractType;
import org.hibernate.type.Type;

public class MetaType
extends AbstractType {
    public static final String[] REGISTRATION_KEYS = new String[0];
    private final Type baseType;
    private final Map<Object, String> discriminatorValuesToEntityNameMap;
    private final Map<String, Object> entityNameToDiscriminatorValueMap;

    public MetaType(Map<Object, String> discriminatorValuesToEntityNameMap, Type baseType) {
        this.baseType = baseType;
        this.discriminatorValuesToEntityNameMap = discriminatorValuesToEntityNameMap;
        this.entityNameToDiscriminatorValueMap = new HashMap<String, Object>();
        for (Map.Entry<Object, String> entry : discriminatorValuesToEntityNameMap.entrySet()) {
            this.entityNameToDiscriminatorValueMap.put(entry.getValue(), entry.getKey());
        }
    }

    public String[] getRegistrationKeys() {
        return REGISTRATION_KEYS;
    }

    public Map<Object, String> getDiscriminatorValuesToEntityNameMap() {
        return this.discriminatorValuesToEntityNameMap;
    }

    @Override
    public int[] sqlTypes(Mapping mapping) throws MappingException {
        return this.baseType.sqlTypes(mapping);
    }

    @Override
    public Size[] dictatedSizes(Mapping mapping) throws MappingException {
        return this.baseType.dictatedSizes(mapping);
    }

    @Override
    public Size[] defaultSizes(Mapping mapping) throws MappingException {
        return this.baseType.defaultSizes(mapping);
    }

    @Override
    public int getColumnSpan(Mapping mapping) throws MappingException {
        return this.baseType.getColumnSpan(mapping);
    }

    @Override
    public Class getReturnedClass() {
        return String.class;
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        Object key = this.baseType.nullSafeGet(rs, names, session, owner);
        return key == null ? null : this.discriminatorValuesToEntityNameMap.get(key);
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String name, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        Object key = this.baseType.nullSafeGet(rs, name, session, owner);
        return key == null ? null : this.discriminatorValuesToEntityNameMap.get(key);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        this.baseType.nullSafeSet(st, value == null ? null : this.entityNameToDiscriminatorValueMap.get(value), index, session);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, boolean[] settable, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        if (settable[0]) {
            this.nullSafeSet(st, value, index, session);
        }
    }

    @Override
    public String toLoggableString(Object value, SessionFactoryImplementor factory) throws HibernateException {
        return this.toXMLString(value, factory);
    }

    public String toXMLString(Object value, SessionFactoryImplementor factory) throws HibernateException {
        return (String)value;
    }

    public Object fromXMLString(String xml, Mapping factory) throws HibernateException {
        return xml;
    }

    @Override
    public String getName() {
        return this.baseType.getName();
    }

    @Override
    public Object deepCopy(Object value, SessionFactoryImplementor factory) throws HibernateException {
        return value;
    }

    @Override
    public Object replace(Object original, Object target, SharedSessionContractImplementor session, Object owner, Map copyCache) {
        return original;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public boolean[] toColumnNullness(Object value, Mapping mapping) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isDirty(Object old, Object current, boolean[] checkable, SharedSessionContractImplementor session) throws HibernateException {
        return checkable[0] && this.isDirty(old, current, session);
    }
}

