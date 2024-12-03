/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.hibernate.EntityMode;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.PropertyNotFoundException;
import org.hibernate.bytecode.enhance.spi.LazyPropertyInitializer;
import org.hibernate.engine.jdbc.Size;
import org.hibernate.engine.spi.CascadeStyle;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.tuple.StandardProperty;
import org.hibernate.tuple.ValueGeneration;
import org.hibernate.tuple.component.ComponentMetamodel;
import org.hibernate.tuple.component.ComponentTuplizer;
import org.hibernate.type.AbstractType;
import org.hibernate.type.CompositeType;
import org.hibernate.type.ForeignKeyDirection;
import org.hibernate.type.ProcedureParameterExtractionAware;
import org.hibernate.type.Type;
import org.hibernate.type.TypeFactory;
import org.hibernate.type.TypeHelper;

public class ComponentType
extends AbstractType
implements CompositeType,
ProcedureParameterExtractionAware {
    private final String[] propertyNames;
    private final Type[] propertyTypes;
    private final ValueGeneration[] propertyValueGenerationStrategies;
    private final boolean[] propertyNullability;
    protected final int propertySpan;
    private final CascadeStyle[] cascade;
    private final FetchMode[] joinedFetch;
    private final boolean isKey;
    private boolean hasNotNullProperty;
    private final boolean createEmptyCompositesEnabled;
    protected final EntityMode entityMode;
    protected final ComponentTuplizer componentTuplizer;
    private Boolean canDoExtraction;

    @Deprecated
    public ComponentType(TypeFactory.TypeScope typeScope, ComponentMetamodel metamodel) {
        this(metamodel);
    }

    public ComponentType(ComponentMetamodel metamodel) {
        this.isKey = metamodel.isKey();
        this.propertySpan = metamodel.getPropertySpan();
        this.propertyNames = new String[this.propertySpan];
        this.propertyTypes = new Type[this.propertySpan];
        this.propertyValueGenerationStrategies = new ValueGeneration[this.propertySpan];
        this.propertyNullability = new boolean[this.propertySpan];
        this.cascade = new CascadeStyle[this.propertySpan];
        this.joinedFetch = new FetchMode[this.propertySpan];
        for (int i = 0; i < this.propertySpan; ++i) {
            StandardProperty prop = metamodel.getProperty(i);
            this.propertyNames[i] = prop.getName();
            this.propertyTypes[i] = prop.getType();
            this.propertyNullability[i] = prop.isNullable();
            this.cascade[i] = prop.getCascadeStyle();
            this.joinedFetch[i] = prop.getFetchMode();
            if (!prop.isNullable()) {
                this.hasNotNullProperty = true;
            }
            this.propertyValueGenerationStrategies[i] = prop.getValueGenerationStrategy();
        }
        this.entityMode = metamodel.getEntityMode();
        this.componentTuplizer = metamodel.getComponentTuplizer();
        this.createEmptyCompositesEnabled = metamodel.isCreateEmptyCompositesEnabled();
    }

    public boolean isKey() {
        return this.isKey;
    }

    public EntityMode getEntityMode() {
        return this.entityMode;
    }

    public ComponentTuplizer getComponentTuplizer() {
        return this.componentTuplizer;
    }

    @Override
    public int getColumnSpan(Mapping mapping) throws MappingException {
        int span = 0;
        for (int i = 0; i < this.propertySpan; ++i) {
            span += this.propertyTypes[i].getColumnSpan(mapping);
        }
        return span;
    }

    @Override
    public int[] sqlTypes(Mapping mapping) throws MappingException {
        int[] sqlTypes = new int[this.getColumnSpan(mapping)];
        int n = 0;
        for (int i = 0; i < this.propertySpan; ++i) {
            int[] subtypes;
            for (int subtype : subtypes = this.propertyTypes[i].sqlTypes(mapping)) {
                sqlTypes[n++] = subtype;
            }
        }
        return sqlTypes;
    }

    @Override
    public Size[] dictatedSizes(Mapping mapping) throws MappingException {
        Size[] sizes = new Size[this.getColumnSpan(mapping)];
        int soFar = 0;
        for (Type propertyType : this.propertyTypes) {
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
        for (Type propertyType : this.propertyTypes) {
            Size[] propertySizes = propertyType.defaultSizes(mapping);
            System.arraycopy(propertySizes, 0, sizes, soFar, propertySizes.length);
            soFar += propertySizes.length;
        }
        return sizes;
    }

    @Override
    public final boolean isComponentType() {
        return true;
    }

    @Override
    public Class getReturnedClass() {
        return this.componentTuplizer.getMappedClass();
    }

    @Override
    public boolean isSame(Object x, Object y) throws HibernateException {
        if (x == y) {
            return true;
        }
        Object[] xvalues = this.getPropertyValues(x, this.entityMode);
        Object[] yvalues = this.getPropertyValues(y, this.entityMode);
        for (int i = 0; i < this.propertySpan; ++i) {
            if (this.propertyTypes[i].isSame(xvalues[i], yvalues[i])) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean isEqual(Object x, Object y) throws HibernateException {
        if (x == y) {
            return true;
        }
        for (int i = 0; i < this.propertySpan; ++i) {
            if (this.propertyTypes[i].isEqual(this.getPropertyValue(x, i), this.getPropertyValue(y, i))) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean isEqual(Object x, Object y, SessionFactoryImplementor factory) throws HibernateException {
        if (x == y) {
            return true;
        }
        for (int i = 0; i < this.propertySpan; ++i) {
            if (this.propertyTypes[i].isEqual(this.getPropertyValue(x, i), this.getPropertyValue(y, i), factory)) continue;
            return false;
        }
        return true;
    }

    @Override
    public int compare(Object x, Object y) {
        if (x == y) {
            return 0;
        }
        for (int i = 0; i < this.propertySpan; ++i) {
            int propertyCompare = this.propertyTypes[i].compare(this.getPropertyValue(x, i), this.getPropertyValue(y, i));
            if (propertyCompare == 0) continue;
            return propertyCompare;
        }
        return 0;
    }

    @Override
    public boolean isMethodOf(Method method) {
        return false;
    }

    @Override
    public int getHashCode(Object x) {
        int result = 17;
        for (int i = 0; i < this.propertySpan; ++i) {
            Object y = this.getPropertyValue(x, i);
            result *= 37;
            if (y == null) continue;
            result += this.propertyTypes[i].getHashCode(y);
        }
        return result;
    }

    @Override
    public int getHashCode(Object x, SessionFactoryImplementor factory) {
        int result = 17;
        for (int i = 0; i < this.propertySpan; ++i) {
            Object y = this.getPropertyValue(x, i);
            result *= 37;
            if (y == null) continue;
            result += this.propertyTypes[i].getHashCode(y, factory);
        }
        return result;
    }

    @Override
    public boolean isDirty(Object x, Object y, SharedSessionContractImplementor session) throws HibernateException {
        if (x == y) {
            return false;
        }
        for (int i = 0; i < this.propertySpan; ++i) {
            if (!this.propertyTypes[i].isDirty(this.getPropertyValue(x, i), this.getPropertyValue(y, i), session)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isDirty(Object x, Object y, boolean[] checkable, SharedSessionContractImplementor session) throws HibernateException {
        if (x == y) {
            return false;
        }
        int loc = 0;
        for (int i = 0; i < this.propertySpan; ++i) {
            int len = this.propertyTypes[i].getColumnSpan(session.getFactory());
            if (len <= 1) {
                boolean dirty;
                boolean bl = dirty = (len == 0 || checkable[loc]) && this.propertyTypes[i].isDirty(this.getPropertyValue(x, i), this.getPropertyValue(y, i), session);
                if (dirty) {
                    return true;
                }
            } else {
                boolean[] subcheckable = new boolean[len];
                System.arraycopy(checkable, loc, subcheckable, 0, len);
                boolean dirty = this.propertyTypes[i].isDirty(this.getPropertyValue(x, i), this.getPropertyValue(y, i), subcheckable, session);
                if (dirty) {
                    return true;
                }
            }
            loc += len;
        }
        return false;
    }

    @Override
    public boolean isModified(Object old, Object current, boolean[] checkable, SharedSessionContractImplementor session) throws HibernateException {
        if (old == current) {
            return false;
        }
        int loc = 0;
        for (int i = 0; i < this.propertySpan; ++i) {
            int len = this.propertyTypes[i].getColumnSpan(session.getFactory());
            boolean[] subcheckable = new boolean[len];
            System.arraycopy(checkable, loc, subcheckable, 0, len);
            if (this.propertyTypes[i].isModified(this.getPropertyValue(old, i), this.getPropertyValue(current, i), subcheckable, session)) {
                return true;
            }
            loc += len;
        }
        return false;
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        return this.resolve(this.hydrate(rs, names, session, owner), session, owner);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int begin, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        Object[] subvalues = this.nullSafeGetValues(value, this.entityMode);
        for (int i = 0; i < this.propertySpan; ++i) {
            this.propertyTypes[i].nullSafeSet(st, subvalues[i], begin, session);
            begin += this.propertyTypes[i].getColumnSpan(session.getFactory());
        }
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int begin, boolean[] settable, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        Object[] subvalues = this.nullSafeGetValues(value, this.entityMode);
        int loc = 0;
        for (int i = 0; i < this.propertySpan; ++i) {
            int len = this.propertyTypes[i].getColumnSpan(session.getFactory());
            if (len != 0) {
                if (len == 1) {
                    if (settable[loc]) {
                        this.propertyTypes[i].nullSafeSet(st, subvalues[i], begin, session);
                        ++begin;
                    }
                } else {
                    boolean[] subsettable = new boolean[len];
                    System.arraycopy(settable, loc, subsettable, 0, len);
                    this.propertyTypes[i].nullSafeSet(st, subvalues[i], begin, subsettable, session);
                    begin += ArrayHelper.countTrue(subsettable);
                }
            }
            loc += len;
        }
    }

    private Object[] nullSafeGetValues(Object value, EntityMode entityMode) throws HibernateException {
        if (value == null) {
            return new Object[this.propertySpan];
        }
        return this.getPropertyValues(value, entityMode);
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String name, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        return this.nullSafeGet(rs, new String[]{name}, session, owner);
    }

    @Override
    public Object getPropertyValue(Object component, int i, SharedSessionContractImplementor session) throws HibernateException {
        return this.getPropertyValue(component, i);
    }

    public Object getPropertyValue(Object component, int i, EntityMode entityMode) throws HibernateException {
        return this.getPropertyValue(component, i);
    }

    public Object getPropertyValue(Object component, int i) throws HibernateException {
        if (component == null) {
            component = new Object[this.propertySpan];
        }
        if (component instanceof Object[]) {
            return component[i];
        }
        return this.componentTuplizer.getPropertyValue(component, i);
    }

    @Override
    public Object[] getPropertyValues(Object component, SharedSessionContractImplementor session) throws HibernateException {
        return this.getPropertyValues(component, this.entityMode);
    }

    @Override
    public Object[] getPropertyValues(Object component, EntityMode entityMode) throws HibernateException {
        if (component == null) {
            component = new Object[this.propertySpan];
        }
        if (component instanceof Object[]) {
            return component;
        }
        return this.componentTuplizer.getPropertyValues(component);
    }

    @Override
    public void setPropertyValues(Object component, Object[] values, EntityMode entityMode) throws HibernateException {
        this.componentTuplizer.setPropertyValues(component, values);
    }

    @Override
    public Type[] getSubtypes() {
        return this.propertyTypes;
    }

    public ValueGeneration[] getPropertyValueGenerationStrategies() {
        return this.propertyValueGenerationStrategies;
    }

    @Override
    public String getName() {
        return "component" + ArrayHelper.toString(this.propertyNames);
    }

    @Override
    public String toLoggableString(Object value, SessionFactoryImplementor factory) throws HibernateException {
        if (value == null) {
            return "null";
        }
        if (this.entityMode == null) {
            throw new ClassCastException(value.getClass().getName());
        }
        HashMap<String, String> result = new HashMap<String, String>();
        Object[] values = this.getPropertyValues(value, this.entityMode);
        for (int i = 0; i < this.propertyTypes.length; ++i) {
            if (values[i] == LazyPropertyInitializer.UNFETCHED_PROPERTY) {
                result.put(this.propertyNames[i], "<uninitialized>");
                continue;
            }
            result.put(this.propertyNames[i], this.propertyTypes[i].toLoggableString(values[i], factory));
        }
        return StringHelper.unqualify(this.getName()) + ((Object)result).toString();
    }

    @Override
    public String[] getPropertyNames() {
        return this.propertyNames;
    }

    @Override
    public Object deepCopy(Object component, SessionFactoryImplementor factory) throws HibernateException {
        if (component == null) {
            return null;
        }
        Object[] values = this.getPropertyValues(component, this.entityMode);
        for (int i = 0; i < this.propertySpan; ++i) {
            values[i] = this.propertyTypes[i].deepCopy(values[i], factory);
        }
        Object result = this.instantiate(this.entityMode);
        this.setPropertyValues(result, values, this.entityMode);
        if (this.componentTuplizer.hasParentProperty()) {
            this.componentTuplizer.setParent(result, this.componentTuplizer.getParent(component), factory);
        }
        return result;
    }

    @Override
    public Object replace(Object original, Object target, SharedSessionContractImplementor session, Object owner, Map copyCache) throws HibernateException {
        if (original == null) {
            return null;
        }
        Object result = target == null ? this.instantiate(owner, session) : target;
        Object[] values = TypeHelper.replace(this.getPropertyValues(original, this.entityMode), this.getPropertyValues(result, this.entityMode), this.propertyTypes, session, owner, copyCache);
        this.setPropertyValues(result, values, this.entityMode);
        return result;
    }

    @Override
    public Object replace(Object original, Object target, SharedSessionContractImplementor session, Object owner, Map copyCache, ForeignKeyDirection foreignKeyDirection) throws HibernateException {
        if (original == null) {
            return null;
        }
        Object result = target == null ? this.instantiate(owner, session) : target;
        Object[] values = TypeHelper.replace(this.getPropertyValues(original, this.entityMode), this.getPropertyValues(result, this.entityMode), this.propertyTypes, session, owner, copyCache, foreignKeyDirection);
        this.setPropertyValues(result, values, this.entityMode);
        return result;
    }

    public Object instantiate(EntityMode entityMode) throws HibernateException {
        return this.componentTuplizer.instantiate();
    }

    public Object instantiate(Object parent, SharedSessionContractImplementor session) throws HibernateException {
        Object result = this.instantiate(this.entityMode);
        if (this.componentTuplizer.hasParentProperty() && parent != null) {
            this.componentTuplizer.setParent(result, session.getPersistenceContextInternal().proxyFor(parent), session.getFactory());
        }
        return result;
    }

    @Override
    public CascadeStyle getCascadeStyle(int i) {
        return this.cascade[i];
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public Serializable disassemble(Object value, SharedSessionContractImplementor session, Object owner) throws HibernateException {
        if (value == null) {
            return null;
        }
        Object[] values = this.getPropertyValues(value, this.entityMode);
        for (int i = 0; i < this.propertyTypes.length; ++i) {
            values[i] = this.propertyTypes[i].disassemble(values[i], session, owner);
        }
        return values;
    }

    @Override
    public Object assemble(Serializable object, SharedSessionContractImplementor session, Object owner) throws HibernateException {
        if (object == null) {
            return null;
        }
        Object[] values = (Object[])object;
        Object[] assembled = new Object[values.length];
        for (int i = 0; i < this.propertyTypes.length; ++i) {
            assembled[i] = this.propertyTypes[i].assemble((Serializable)values[i], session, owner);
        }
        Object result = this.instantiate(owner, session);
        this.setPropertyValues(result, assembled, this.entityMode);
        return result;
    }

    @Override
    public FetchMode getFetchMode(int i) {
        return this.joinedFetch[i];
    }

    @Override
    public Object hydrate(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        int begin = 0;
        boolean notNull = false;
        Object[] values = new Object[this.propertySpan];
        for (int i = 0; i < this.propertySpan; ++i) {
            int length = this.propertyTypes[i].getColumnSpan(session.getFactory());
            String[] range = ArrayHelper.slice(names, begin, length);
            Object val = this.propertyTypes[i].hydrate(rs, range, session, owner);
            if (val == null) {
                if (this.isKey) {
                    return null;
                }
            } else {
                notNull = true;
            }
            values[i] = val;
            begin += length;
        }
        return notNull ? values : null;
    }

    @Override
    public Object resolve(Object value, SharedSessionContractImplementor session, Object owner) throws HibernateException {
        if (value != null) {
            Object result = this.instantiate(owner, session);
            Object[] values = (Object[])value;
            Object[] resolvedValues = new Object[values.length];
            for (int i = 0; i < values.length; ++i) {
                resolvedValues[i] = this.propertyTypes[i].resolve(values[i], session, owner);
            }
            this.setPropertyValues(result, resolvedValues, this.entityMode);
            return result;
        }
        if (this.isCreateEmptyCompositesEnabled()) {
            return this.instantiate(owner, session);
        }
        return null;
    }

    @Override
    public Object semiResolve(Object value, SharedSessionContractImplementor session, Object owner) throws HibernateException {
        return this.resolve(value, session, owner);
    }

    @Override
    public boolean[] getPropertyNullability() {
        return this.propertyNullability;
    }

    @Override
    public boolean[] toColumnNullness(Object value, Mapping mapping) {
        boolean[] result = new boolean[this.getColumnSpan(mapping)];
        if (value == null) {
            return result;
        }
        Object[] values = this.getPropertyValues(value, EntityMode.POJO);
        int loc = 0;
        for (int i = 0; i < this.propertyTypes.length; ++i) {
            boolean[] propertyNullness = this.propertyTypes[i].toColumnNullness(values[i], mapping);
            System.arraycopy(propertyNullness, 0, result, loc, propertyNullness.length);
            loc += propertyNullness.length;
        }
        return result;
    }

    @Override
    public boolean isEmbedded() {
        return false;
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
    public boolean canDoExtraction() {
        if (this.canDoExtraction == null) {
            this.canDoExtraction = this.determineIfProcedureParamExtractionCanBePerformed();
        }
        return this.canDoExtraction;
    }

    private boolean determineIfProcedureParamExtractionCanBePerformed() {
        for (Type propertyType : this.propertyTypes) {
            if (!ProcedureParameterExtractionAware.class.isInstance(propertyType)) {
                return false;
            }
            if (((ProcedureParameterExtractionAware)((Object)propertyType)).canDoExtraction()) continue;
            return false;
        }
        return true;
    }

    public Object extract(CallableStatement statement, int startIndex, SharedSessionContractImplementor session) throws SQLException {
        Object[] values = new Object[this.propertySpan];
        int currentIndex = startIndex;
        boolean notNull = false;
        for (int i = 0; i < this.propertySpan; ++i) {
            Type propertyType = this.propertyTypes[i];
            Object value = ((ProcedureParameterExtractionAware)((Object)propertyType)).extract(statement, currentIndex, session);
            if (value == null) {
                if (this.isKey) {
                    return null;
                }
            } else {
                notNull = true;
            }
            values[i] = value;
            currentIndex += propertyType.getColumnSpan(session.getFactory());
        }
        if (!notNull) {
            values = null;
        }
        return this.resolve(values, session, null);
    }

    public Object extract(CallableStatement statement, String[] paramNames, SharedSessionContractImplementor session) throws SQLException {
        Object[] values = new Object[this.propertySpan];
        int indx = 0;
        boolean notNull = false;
        for (String paramName : paramNames) {
            ProcedureParameterExtractionAware propertyType = (ProcedureParameterExtractionAware)((Object)this.propertyTypes[indx]);
            Object value = propertyType.extract(statement, new String[]{paramName}, session);
            if (value == null) {
                if (this.isKey) {
                    return null;
                }
            } else {
                notNull = true;
            }
            values[indx] = value;
        }
        if (!notNull) {
            values = null;
        }
        return this.resolve(values, session, null);
    }

    @Override
    public boolean hasNotNullProperty() {
        return this.hasNotNullProperty;
    }

    private boolean isCreateEmptyCompositesEnabled() {
        return this.createEmptyCompositesEnabled;
    }
}

