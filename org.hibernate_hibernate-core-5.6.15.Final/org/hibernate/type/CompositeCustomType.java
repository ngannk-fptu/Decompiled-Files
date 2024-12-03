/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import org.hibernate.EntityMode;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.PropertyNotFoundException;
import org.hibernate.engine.jdbc.Size;
import org.hibernate.engine.spi.CascadeStyle;
import org.hibernate.engine.spi.CascadeStyles;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.type.AbstractType;
import org.hibernate.type.BasicType;
import org.hibernate.type.CompositeType;
import org.hibernate.type.Type;
import org.hibernate.usertype.CompositeUserType;
import org.hibernate.usertype.LoggableUserType;

public class CompositeCustomType
extends AbstractType
implements CompositeType,
BasicType {
    private final CompositeUserType userType;
    private final String[] registrationKeys;
    private final String name;
    private final boolean customLogging;

    public CompositeCustomType(CompositeUserType userType) {
        this(userType, ArrayHelper.EMPTY_STRING_ARRAY);
    }

    public CompositeCustomType(CompositeUserType userType, String[] registrationKeys) {
        this.userType = userType;
        this.name = userType.getClass().getName();
        this.customLogging = LoggableUserType.class.isInstance(userType);
        this.registrationKeys = registrationKeys;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Class getReturnedClass() {
        return this.userType.returnedClass();
    }

    @Override
    public boolean isMutable() {
        return this.userType.isMutable();
    }

    @Override
    public String[] getRegistrationKeys() {
        return this.registrationKeys;
    }

    public CompositeUserType getUserType() {
        return this.userType;
    }

    @Override
    public boolean isMethodOf(Method method) {
        return false;
    }

    @Override
    public Type[] getSubtypes() {
        return this.userType.getPropertyTypes();
    }

    @Override
    public String[] getPropertyNames() {
        return this.userType.getPropertyNames();
    }

    @Override
    public int getPropertyIndex(String name) {
        String[] names = this.getPropertyNames();
        int max = names.length;
        for (int i = 0; i < max; ++i) {
            if (!names[i].equals(name)) continue;
            return i;
        }
        throw new PropertyNotFoundException("Unable to locate property named " + name + " on " + this.getReturnedClass().getName());
    }

    @Override
    public Object[] getPropertyValues(Object component, SharedSessionContractImplementor session) throws HibernateException {
        return this.getPropertyValues(component, EntityMode.POJO);
    }

    @Override
    public Object[] getPropertyValues(Object component, EntityMode entityMode) throws HibernateException {
        int len = this.getSubtypes().length;
        Object[] result = new Object[len];
        for (int i = 0; i < len; ++i) {
            result[i] = this.getPropertyValue(component, i);
        }
        return result;
    }

    @Override
    public void setPropertyValues(Object component, Object[] values, EntityMode entityMode) throws HibernateException {
        for (int i = 0; i < values.length; ++i) {
            this.userType.setPropertyValue(component, i, values[i]);
        }
    }

    @Override
    public Object getPropertyValue(Object component, int i, SharedSessionContractImplementor session) throws HibernateException {
        return this.getPropertyValue(component, i);
    }

    public Object getPropertyValue(Object component, int i) throws HibernateException {
        return this.userType.getPropertyValue(component, i);
    }

    @Override
    public CascadeStyle getCascadeStyle(int i) {
        return CascadeStyles.NONE;
    }

    @Override
    public FetchMode getFetchMode(int i) {
        return FetchMode.DEFAULT;
    }

    @Override
    public boolean isComponentType() {
        return true;
    }

    @Override
    public Object deepCopy(Object value, SessionFactoryImplementor factory) throws HibernateException {
        return this.userType.deepCopy(value);
    }

    @Override
    public Object assemble(Serializable cached, SharedSessionContractImplementor session, Object owner) throws HibernateException {
        return this.userType.assemble(cached, session, owner);
    }

    @Override
    public Serializable disassemble(Object value, SharedSessionContractImplementor session, Object owner) throws HibernateException {
        return this.userType.disassemble(value, session);
    }

    @Override
    public Object replace(Object original, Object target, SharedSessionContractImplementor session, Object owner, Map copyCache) throws HibernateException {
        return this.userType.replace(original, target, session, owner);
    }

    @Override
    public boolean isEqual(Object x, Object y) throws HibernateException {
        return this.userType.equals(x, y);
    }

    @Override
    public int getHashCode(Object x) {
        return this.userType.hashCode(x);
    }

    @Override
    public int getColumnSpan(Mapping mapping) throws MappingException {
        Type[] types = this.userType.getPropertyTypes();
        int n = 0;
        for (Type type : types) {
            n += type.getColumnSpan(mapping);
        }
        return n;
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String columnName, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        return this.userType.nullSafeGet(rs, new String[]{columnName}, session, owner);
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        return this.userType.nullSafeGet(rs, names, session, owner);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        this.userType.nullSafeSet(st, value, index, session);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, boolean[] settable, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        this.userType.nullSafeSet(st, value, index, session);
    }

    @Override
    public int[] sqlTypes(Mapping mapping) throws MappingException {
        int[] result = new int[this.getColumnSpan(mapping)];
        int n = 0;
        for (Type type : this.userType.getPropertyTypes()) {
            for (int sqlType : type.sqlTypes(mapping)) {
                result[n++] = sqlType;
            }
        }
        return result;
    }

    @Override
    public Size[] dictatedSizes(Mapping mapping) throws MappingException {
        Size[] sizes = new Size[this.getColumnSpan(mapping)];
        int soFar = 0;
        for (Type propertyType : this.userType.getPropertyTypes()) {
            Size[] propertySizes = propertyType.dictatedSizes(mapping);
            System.arraycopy(propertySizes, 0, sizes, soFar, propertySizes.length);
            soFar += propertySizes.length;
        }
        return sizes;
    }

    @Override
    public Size[] defaultSizes(Mapping mapping) throws MappingException {
        Size[] sizes = new Size[this.getColumnSpan(mapping)];
        int soFar = 0;
        for (Type propertyType : this.userType.getPropertyTypes()) {
            Size[] propertySizes = propertyType.defaultSizes(mapping);
            System.arraycopy(propertySizes, 0, sizes, soFar, propertySizes.length);
            soFar += propertySizes.length;
        }
        return sizes;
    }

    @Override
    public String toLoggableString(Object value, SessionFactoryImplementor factory) throws HibernateException {
        if (value == null) {
            return "null";
        }
        if (this.customLogging) {
            return ((LoggableUserType)((Object)this.userType)).toLoggableString(value, factory);
        }
        return value.toString();
    }

    @Override
    public boolean[] getPropertyNullability() {
        return null;
    }

    @Override
    public boolean[] toColumnNullness(Object value, Mapping mapping) {
        boolean[] result = new boolean[this.getColumnSpan(mapping)];
        if (value == null) {
            return result;
        }
        Object[] values = this.getPropertyValues(value, EntityMode.POJO);
        int loc = 0;
        Type[] propertyTypes = this.getSubtypes();
        for (int i = 0; i < propertyTypes.length; ++i) {
            boolean[] propertyNullness = propertyTypes[i].toColumnNullness(values[i], mapping);
            System.arraycopy(propertyNullness, 0, result, loc, propertyNullness.length);
            loc += propertyNullness.length;
        }
        return result;
    }

    @Override
    public boolean isDirty(Object old, Object current, boolean[] checkable, SharedSessionContractImplementor session) throws HibernateException {
        return this.isDirty(old, current, session);
    }

    @Override
    public boolean isEmbedded() {
        return false;
    }

    @Override
    public boolean hasNotNullProperty() {
        return false;
    }
}

