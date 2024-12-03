/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.bytecode.enhance.spi.LazyPropertyInitializer;
import org.hibernate.engine.jdbc.Size;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.type.BasicType;
import org.hibernate.type.ForeignKeyDirection;
import org.hibernate.type.ProcedureParameterExtractionAware;
import org.hibernate.type.ProcedureParameterNamedBinder;
import org.hibernate.type.StringRepresentableType;
import org.hibernate.type.Type;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
import org.hibernate.type.descriptor.java.MutabilityPlan;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;

public abstract class AbstractStandardBasicType<T>
implements BasicType,
StringRepresentableType<T>,
ProcedureParameterExtractionAware<T>,
ProcedureParameterNamedBinder {
    private static final Size DEFAULT_SIZE = new Size(19, 2, 255L, Size.LobMultiplier.NONE);
    private final Size dictatedSize = new Size();
    private SqlTypeDescriptor sqlTypeDescriptor;
    private JavaTypeDescriptor<T> javaTypeDescriptor;
    private int[] sqlTypes;

    public AbstractStandardBasicType(SqlTypeDescriptor sqlTypeDescriptor, JavaTypeDescriptor<T> javaTypeDescriptor) {
        this.sqlTypeDescriptor = sqlTypeDescriptor;
        this.sqlTypes = new int[]{sqlTypeDescriptor.getSqlType()};
        this.javaTypeDescriptor = javaTypeDescriptor;
    }

    public T fromString(String string) {
        return this.javaTypeDescriptor.fromString(string);
    }

    @Override
    public String toString(T value) {
        return this.javaTypeDescriptor.toString(value);
    }

    @Override
    public T fromStringValue(String xml) throws HibernateException {
        return this.fromString(xml);
    }

    protected MutabilityPlan<T> getMutabilityPlan() {
        return this.javaTypeDescriptor.getMutabilityPlan();
    }

    protected T getReplacement(T original, T target, SharedSessionContractImplementor session) {
        if (!this.isMutable() || target != null && this.isEqual(original, target)) {
            return original;
        }
        return this.deepCopy(original);
    }

    @Override
    public boolean[] toColumnNullness(Object value, Mapping mapping) {
        return value == null ? ArrayHelper.FALSE : ArrayHelper.TRUE;
    }

    @Override
    public String[] getRegistrationKeys() {
        String[] stringArray;
        if (this.registerUnderJavaType()) {
            String[] stringArray2 = new String[2];
            stringArray2[0] = this.getName();
            stringArray = stringArray2;
            stringArray2[1] = this.javaTypeDescriptor.getJavaType().getName();
        } else {
            String[] stringArray3 = new String[1];
            stringArray = stringArray3;
            stringArray3[0] = this.getName();
        }
        return stringArray;
    }

    protected boolean registerUnderJavaType() {
        return false;
    }

    protected static Size getDefaultSize() {
        return DEFAULT_SIZE;
    }

    protected Size getDictatedSize() {
        return this.dictatedSize;
    }

    public final JavaTypeDescriptor<T> getJavaTypeDescriptor() {
        return this.javaTypeDescriptor;
    }

    public final void setJavaTypeDescriptor(JavaTypeDescriptor<T> javaTypeDescriptor) {
        this.javaTypeDescriptor = javaTypeDescriptor;
    }

    public final SqlTypeDescriptor getSqlTypeDescriptor() {
        return this.sqlTypeDescriptor;
    }

    public final void setSqlTypeDescriptor(SqlTypeDescriptor sqlTypeDescriptor) {
        this.sqlTypeDescriptor = sqlTypeDescriptor;
        this.sqlTypes = new int[]{sqlTypeDescriptor.getSqlType()};
    }

    @Override
    public final Class getReturnedClass() {
        return this.javaTypeDescriptor.getJavaType();
    }

    @Override
    public final int getColumnSpan(Mapping mapping) throws MappingException {
        return 1;
    }

    @Override
    public final int[] sqlTypes(Mapping mapping) throws MappingException {
        return this.sqlTypes;
    }

    @Override
    public Size[] dictatedSizes(Mapping mapping) throws MappingException {
        return new Size[]{this.getDictatedSize()};
    }

    @Override
    public Size[] defaultSizes(Mapping mapping) throws MappingException {
        return new Size[]{AbstractStandardBasicType.getDefaultSize()};
    }

    @Override
    public final boolean isAssociationType() {
        return false;
    }

    @Override
    public final boolean isCollectionType() {
        return false;
    }

    @Override
    public final boolean isComponentType() {
        return false;
    }

    @Override
    public final boolean isEntityType() {
        return false;
    }

    @Override
    public final boolean isAnyType() {
        return false;
    }

    public final boolean isXMLElement() {
        return false;
    }

    @Override
    public final boolean isSame(Object x, Object y) {
        return this.isEqual(x, y);
    }

    @Override
    public final boolean isEqual(Object x, Object y, SessionFactoryImplementor factory) {
        return this.isEqual(x, y);
    }

    @Override
    public final boolean isEqual(Object one, Object another) {
        return this.javaTypeDescriptor.areEqual(one, another);
    }

    @Override
    public final int getHashCode(Object x) {
        return this.javaTypeDescriptor.extractHashCode(x);
    }

    @Override
    public final int getHashCode(Object x, SessionFactoryImplementor factory) {
        return this.getHashCode(x);
    }

    @Override
    public final int compare(Object x, Object y) {
        return this.javaTypeDescriptor.getComparator().compare(x, y);
    }

    @Override
    public final boolean isDirty(Object old, Object current, SharedSessionContractImplementor session) {
        return this.isDirty(old, current);
    }

    @Override
    public final boolean isDirty(Object old, Object current, boolean[] checkable, SharedSessionContractImplementor session) {
        return checkable[0] && this.isDirty(old, current);
    }

    protected final boolean isDirty(Object old, Object current) {
        return !this.isSame(old, current);
    }

    @Override
    public final boolean isModified(Object oldHydratedState, Object currentState, boolean[] checkable, SharedSessionContractImplementor session) {
        return this.isDirty(oldHydratedState, currentState);
    }

    @Override
    public final Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws SQLException {
        return this.nullSafeGet(rs, names[0], session);
    }

    @Override
    public final Object nullSafeGet(ResultSet rs, String name, SharedSessionContractImplementor session, Object owner) throws SQLException {
        return this.nullSafeGet(rs, name, session);
    }

    public final T nullSafeGet(ResultSet rs, String name, SharedSessionContractImplementor session) throws SQLException {
        return this.nullSafeGet(rs, name, (WrapperOptions)session);
    }

    protected final T nullSafeGet(ResultSet rs, String name, WrapperOptions options) throws SQLException {
        return this.remapSqlTypeDescriptor(options).getExtractor(this.javaTypeDescriptor).extract(rs, name, options);
    }

    public Object get(ResultSet rs, String name, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        return this.nullSafeGet(rs, name, session);
    }

    @Override
    public final void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws SQLException {
        this.nullSafeSet(st, value, index, (WrapperOptions)session);
    }

    protected final void nullSafeSet(PreparedStatement st, Object value, int index, WrapperOptions options) throws SQLException {
        this.remapSqlTypeDescriptor(options).getBinder(this.javaTypeDescriptor).bind(st, value, index, options);
    }

    protected SqlTypeDescriptor remapSqlTypeDescriptor(WrapperOptions options) {
        return options.remapSqlTypeDescriptor(this.sqlTypeDescriptor);
    }

    public void set(PreparedStatement st, T value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        this.nullSafeSet(st, value, index, session);
    }

    @Override
    public final String toLoggableString(Object value, SessionFactoryImplementor factory) {
        if (value == LazyPropertyInitializer.UNFETCHED_PROPERTY || !Hibernate.isInitialized(value)) {
            return "<uninitialized>";
        }
        return this.javaTypeDescriptor.extractLoggableRepresentation(value);
    }

    @Override
    public final boolean isMutable() {
        return this.getMutabilityPlan().isMutable();
    }

    @Override
    public final Object deepCopy(Object value, SessionFactoryImplementor factory) {
        return this.deepCopy(value);
    }

    protected final T deepCopy(T value) {
        return this.getMutabilityPlan().deepCopy(value);
    }

    @Override
    public final Serializable disassemble(Object value, SharedSessionContractImplementor session, Object owner) throws HibernateException {
        return this.getMutabilityPlan().disassemble(value);
    }

    @Override
    public final Object assemble(Serializable cached, SharedSessionContractImplementor session, Object owner) throws HibernateException {
        return this.getMutabilityPlan().assemble(cached);
    }

    @Override
    public final void beforeAssemble(Serializable cached, SharedSessionContractImplementor session) {
    }

    @Override
    public final Object hydrate(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        return this.nullSafeGet(rs, names, session, owner);
    }

    @Override
    public final Object resolve(Object value, SharedSessionContractImplementor session, Object owner) throws HibernateException {
        return value;
    }

    @Override
    public final Object semiResolve(Object value, SharedSessionContractImplementor session, Object owner) throws HibernateException {
        return value;
    }

    @Override
    public final Type getSemiResolvedType(SessionFactoryImplementor factory) {
        return this;
    }

    @Override
    public final Object replace(Object original, Object target, SharedSessionContractImplementor session, Object owner, Map copyCache) {
        if (original == null && target == null) {
            return null;
        }
        return this.getReplacement(original, target, session);
    }

    @Override
    public Object replace(Object original, Object target, SharedSessionContractImplementor session, Object owner, Map copyCache, ForeignKeyDirection foreignKeyDirection) {
        return ForeignKeyDirection.FROM_PARENT == foreignKeyDirection ? this.getReplacement(original, target, session) : target;
    }

    @Override
    public boolean canDoExtraction() {
        return true;
    }

    @Override
    public T extract(CallableStatement statement, int startIndex, SharedSessionContractImplementor session) throws SQLException {
        return this.remapSqlTypeDescriptor(session).getExtractor(this.javaTypeDescriptor).extract(statement, startIndex, (WrapperOptions)session);
    }

    @Override
    public T extract(CallableStatement statement, String[] paramNames, SharedSessionContractImplementor session) throws SQLException {
        return this.remapSqlTypeDescriptor(session).getExtractor(this.javaTypeDescriptor).extract(statement, paramNames, (WrapperOptions)session);
    }

    @Override
    public void nullSafeSet(CallableStatement st, Object value, String name, SharedSessionContractImplementor session) throws SQLException {
        this.nullSafeSet(st, value, name, (WrapperOptions)session);
    }

    protected final void nullSafeSet(CallableStatement st, Object value, String name, WrapperOptions options) throws SQLException {
        this.remapSqlTypeDescriptor(options).getBinder(this.javaTypeDescriptor).bind(st, value, name, options);
    }

    @Override
    public boolean canDoSetting() {
        return true;
    }
}

