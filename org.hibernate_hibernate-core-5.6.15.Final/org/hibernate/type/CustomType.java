/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.Size;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.type.AbstractType;
import org.hibernate.type.BasicType;
import org.hibernate.type.DiscriminatorType;
import org.hibernate.type.IdentifierType;
import org.hibernate.type.ProcedureParameterExtractionAware;
import org.hibernate.type.ProcedureParameterNamedBinder;
import org.hibernate.type.StringRepresentableType;
import org.hibernate.type.VersionType;
import org.hibernate.usertype.EnhancedUserType;
import org.hibernate.usertype.LoggableUserType;
import org.hibernate.usertype.Sized;
import org.hibernate.usertype.UserType;
import org.hibernate.usertype.UserVersionType;

public class CustomType
extends AbstractType
implements IdentifierType,
DiscriminatorType,
VersionType,
BasicType,
StringRepresentableType,
ProcedureParameterNamedBinder,
ProcedureParameterExtractionAware {
    private final UserType userType;
    private final String name;
    private final int[] types;
    private final Size[] dictatedSizes;
    private final Size[] defaultSizes;
    private final boolean customLogging;
    private final String[] registrationKeys;

    public CustomType(UserType userType) throws MappingException {
        this(userType, ArrayHelper.EMPTY_STRING_ARRAY);
    }

    public CustomType(UserType userType, String[] registrationKeys) throws MappingException {
        this.userType = userType;
        this.name = userType.getClass().getName();
        this.types = userType.sqlTypes();
        this.dictatedSizes = Sized.class.isInstance(userType) ? ((Sized)((Object)userType)).dictatedSizes() : new Size[this.types.length];
        this.defaultSizes = Sized.class.isInstance(userType) ? ((Sized)((Object)userType)).defaultSizes() : new Size[this.types.length];
        this.customLogging = LoggableUserType.class.isInstance(userType);
        this.registrationKeys = registrationKeys;
    }

    public UserType getUserType() {
        return this.userType;
    }

    @Override
    public String[] getRegistrationKeys() {
        return this.registrationKeys;
    }

    @Override
    public int[] sqlTypes(Mapping pi) {
        return this.types;
    }

    @Override
    public Size[] dictatedSizes(Mapping mapping) throws MappingException {
        return this.dictatedSizes;
    }

    @Override
    public Size[] defaultSizes(Mapping mapping) throws MappingException {
        return this.defaultSizes;
    }

    @Override
    public int getColumnSpan(Mapping session) {
        return this.types.length;
    }

    @Override
    public Class getReturnedClass() {
        return this.getUserType().returnedClass();
    }

    @Override
    public boolean isEqual(Object x, Object y) throws HibernateException {
        return this.getUserType().equals(x, y);
    }

    @Override
    public int getHashCode(Object x) {
        return this.getUserType().hashCode(x);
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws SQLException {
        return this.getUserType().nullSafeGet(rs, names, session, owner);
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String columnName, SharedSessionContractImplementor session, Object owner) throws SQLException {
        return this.nullSafeGet(rs, new String[]{columnName}, session, owner);
    }

    @Override
    public Object assemble(Serializable cached, SharedSessionContractImplementor session, Object owner) {
        return this.getUserType().assemble(cached, owner);
    }

    @Override
    public Serializable disassemble(Object value, SharedSessionContractImplementor session, Object owner) {
        return this.getUserType().disassemble(value);
    }

    @Override
    public Object replace(Object original, Object target, SharedSessionContractImplementor session, Object owner, Map copyCache) throws HibernateException {
        return this.getUserType().replace(original, target, owner);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, boolean[] settable, SharedSessionContractImplementor session) throws SQLException {
        if (settable[0]) {
            this.getUserType().nullSafeSet(st, value, index, session);
        }
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws SQLException {
        this.getUserType().nullSafeSet(st, value, index, session);
    }

    public String toXMLString(Object value, SessionFactoryImplementor factory) {
        return this.toString(value);
    }

    public Object fromXMLString(String xml, Mapping factory) {
        return this.fromStringValue(xml);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Object deepCopy(Object value, SessionFactoryImplementor factory) throws HibernateException {
        return this.getUserType().deepCopy(value);
    }

    @Override
    public boolean isMutable() {
        return this.getUserType().isMutable();
    }

    @Override
    public Object stringToObject(String xml) {
        return this.fromStringValue(xml);
    }

    @Override
    public String objectToSQLString(Object value, Dialect dialect) throws Exception {
        return ((EnhancedUserType)this.getUserType()).objectToSQLString(value);
    }

    public Comparator getComparator() {
        return (Comparator)((Object)this.getUserType());
    }

    public Object next(Object current, SharedSessionContractImplementor session) {
        return ((UserVersionType)this.getUserType()).next(current, session);
    }

    public Object seed(SharedSessionContractImplementor session) {
        return ((UserVersionType)this.getUserType()).seed(session);
    }

    @Override
    public String toLoggableString(Object value, SessionFactoryImplementor factory) throws HibernateException {
        if (value == null) {
            return "null";
        }
        if (this.customLogging) {
            return ((LoggableUserType)((Object)this.getUserType())).toLoggableString(value, factory);
        }
        return this.toXMLString(value, factory);
    }

    @Override
    public boolean[] toColumnNullness(Object value, Mapping mapping) {
        boolean[] result = new boolean[this.getColumnSpan(mapping)];
        if (value != null) {
            Arrays.fill(result, true);
        }
        return result;
    }

    @Override
    public boolean isDirty(Object old, Object current, boolean[] checkable, SharedSessionContractImplementor session) throws HibernateException {
        return checkable[0] && this.isDirty(old, current, session);
    }

    public String toString(Object value) throws HibernateException {
        if (StringRepresentableType.class.isInstance(this.getUserType())) {
            return ((StringRepresentableType)((Object)this.getUserType())).toString(value);
        }
        if (value == null) {
            return null;
        }
        if (EnhancedUserType.class.isInstance(this.getUserType())) {
            return ((EnhancedUserType)this.getUserType()).toXMLString(value);
        }
        return value.toString();
    }

    public Object fromStringValue(String string) throws HibernateException {
        if (StringRepresentableType.class.isInstance(this.getUserType())) {
            return ((StringRepresentableType)((Object)this.getUserType())).fromStringValue(string);
        }
        if (EnhancedUserType.class.isInstance(this.getUserType())) {
            return ((EnhancedUserType)this.getUserType()).fromXMLString(string);
        }
        throw new HibernateException(String.format("Could not process #fromStringValue, UserType class [%s] did not implement %s or %s", this.name, StringRepresentableType.class.getName(), EnhancedUserType.class.getName()));
    }

    @Override
    public boolean canDoSetting() {
        if (ProcedureParameterNamedBinder.class.isInstance(this.getUserType())) {
            return ((ProcedureParameterNamedBinder)((Object)this.getUserType())).canDoSetting();
        }
        return false;
    }

    @Override
    public void nullSafeSet(CallableStatement statement, Object value, String name, SharedSessionContractImplementor session) throws SQLException {
        if (!this.canDoSetting()) {
            throw new UnsupportedOperationException("Type [" + this.getUserType() + "] does support parameter binding by name");
        }
        ((ProcedureParameterNamedBinder)((Object)this.getUserType())).nullSafeSet(statement, value, name, session);
    }

    @Override
    public boolean canDoExtraction() {
        if (ProcedureParameterExtractionAware.class.isInstance(this.getUserType())) {
            return ((ProcedureParameterExtractionAware)((Object)this.getUserType())).canDoExtraction();
        }
        return false;
    }

    public Object extract(CallableStatement statement, int startIndex, SharedSessionContractImplementor session) throws SQLException {
        if (this.canDoExtraction()) {
            return ((ProcedureParameterExtractionAware)((Object)this.getUserType())).extract(statement, startIndex, session);
        }
        throw new UnsupportedOperationException("Type [" + this.getUserType() + "] does support parameter value extraction");
    }

    public Object extract(CallableStatement statement, String[] paramNames, SharedSessionContractImplementor session) throws SQLException {
        if (this.canDoExtraction()) {
            return ((ProcedureParameterExtractionAware)((Object)this.getUserType())).extract(statement, paramNames, session);
        }
        throw new UnsupportedOperationException("Type [" + this.getUserType() + "] does support parameter value extraction");
    }

    public int hashCode() {
        return this.getUserType().hashCode();
    }

    public boolean equals(Object obj) {
        return obj instanceof CustomType && this.getUserType().equals(((CustomType)obj).getUserType());
    }
}

