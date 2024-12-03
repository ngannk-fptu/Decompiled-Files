/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.hibernate.EntityMode;
import org.hibernate.EntityNameResolver;
import org.hibernate.FetchMode;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.PropertyNotFoundException;
import org.hibernate.TransientObjectException;
import org.hibernate.bytecode.enhance.spi.LazyPropertyInitializer;
import org.hibernate.engine.internal.ForeignKeys;
import org.hibernate.engine.jdbc.Size;
import org.hibernate.engine.spi.CascadeStyle;
import org.hibernate.engine.spi.CascadeStyles;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.Joinable;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.HibernateProxyHelper;
import org.hibernate.proxy.LazyInitializer;
import org.hibernate.type.AbstractType;
import org.hibernate.type.AssociationType;
import org.hibernate.type.CompositeType;
import org.hibernate.type.ForeignKeyDirection;
import org.hibernate.type.Type;
import org.hibernate.type.TypeFactory;

public class AnyType
extends AbstractType
implements CompositeType,
AssociationType {
    private final TypeFactory.TypeScope scope;
    private final Type identifierType;
    private final Type discriminatorType;
    private final boolean eager;
    private static final String[] PROPERTY_NAMES = new String[]{"class", "id"};
    private static final boolean[] NULLABILITY = new boolean[]{false, false};

    protected AnyType(Type discriminatorType, Type identifierType) {
        this(null, discriminatorType, identifierType, true);
    }

    public AnyType(TypeFactory.TypeScope scope, Type discriminatorType, Type identifierType, boolean lazy) {
        this.scope = scope;
        this.discriminatorType = discriminatorType;
        this.identifierType = identifierType;
        this.eager = !lazy;
    }

    public Type getIdentifierType() {
        return this.identifierType;
    }

    public Type getDiscriminatorType() {
        return this.discriminatorType;
    }

    @Override
    public String getName() {
        return "object";
    }

    @Override
    public Class getReturnedClass() {
        return Object.class;
    }

    @Override
    public int[] sqlTypes(Mapping mapping) throws MappingException {
        return ArrayHelper.join(this.discriminatorType.sqlTypes(mapping), this.identifierType.sqlTypes(mapping));
    }

    @Override
    public Size[] dictatedSizes(Mapping mapping) throws MappingException {
        return ArrayHelper.join(this.discriminatorType.dictatedSizes(mapping), this.identifierType.dictatedSizes(mapping));
    }

    @Override
    public Size[] defaultSizes(Mapping mapping) throws MappingException {
        return ArrayHelper.join(this.discriminatorType.defaultSizes(mapping), this.identifierType.defaultSizes(mapping));
    }

    @Override
    public Object[] getPropertyValues(Object component, EntityMode entityMode) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isAnyType() {
        return true;
    }

    @Override
    public boolean isAssociationType() {
        return true;
    }

    @Override
    public boolean isComponentType() {
        return true;
    }

    @Override
    public boolean isEmbedded() {
        return false;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Object deepCopy(Object value, SessionFactoryImplementor factory) {
        return value;
    }

    @Override
    public int compare(Object x, Object y) {
        if (x == null) {
            return y == null ? 0 : -1;
        }
        if (y == null) {
            return 1;
        }
        Object xId = this.extractIdentifier(x);
        Object yId = this.extractIdentifier(y);
        return this.getIdentifierType().compare(xId, yId);
    }

    private Object extractIdentifier(Object entity) {
        EntityPersister concretePersister = this.guessEntityPersister(entity);
        return concretePersister == null ? null : concretePersister.getEntityTuplizer().getIdentifier(entity, null);
    }

    private EntityPersister guessEntityPersister(Object object) {
        if (this.scope == null) {
            return null;
        }
        String entityName = null;
        Object entity = object;
        if (entity instanceof HibernateProxy) {
            LazyInitializer initializer = ((HibernateProxy)entity).getHibernateLazyInitializer();
            if (initializer.isUninitialized()) {
                entityName = initializer.getEntityName();
            }
            entity = initializer.getImplementation();
        }
        if (entityName == null) {
            EntityNameResolver resolver;
            Iterator<EntityNameResolver> iterator = this.scope.getTypeConfiguration().getSessionFactory().getMetamodel().getEntityNameResolvers().iterator();
            while (iterator.hasNext() && (entityName = (resolver = iterator.next()).resolveEntityName(entity)) == null) {
            }
        }
        if (entityName == null) {
            entityName = object.getClass().getName();
        }
        return this.scope.getTypeConfiguration().getSessionFactory().getMetamodel().entityPersister(entityName);
    }

    @Override
    public boolean isSame(Object x, Object y) throws HibernateException {
        return x == y;
    }

    @Override
    public boolean isModified(Object old, Object current, boolean[] checkable, SharedSessionContractImplementor session) throws HibernateException {
        if (current == null) {
            return old != null;
        }
        if (old == null) {
            return true;
        }
        ObjectTypeCacheEntry holder = (ObjectTypeCacheEntry)old;
        boolean[] idCheckable = new boolean[checkable.length - 1];
        System.arraycopy(checkable, 1, idCheckable, 0, idCheckable.length);
        return checkable[0] && !holder.entityName.equals(session.bestGuessEntityName(current)) || this.identifierType.isModified(holder.id, this.getIdentifier(current, session), idCheckable, session);
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
        return this.isDirty(old, current, session);
    }

    @Override
    public int getColumnSpan(Mapping session) {
        return 2;
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        return this.resolveAny((String)this.discriminatorType.nullSafeGet(rs, names[0], session, owner), (Serializable)this.identifierType.nullSafeGet(rs, names[1], session, owner), session);
    }

    @Override
    public Object hydrate(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        String entityName = (String)this.discriminatorType.nullSafeGet(rs, names[0], session, owner);
        Serializable id = (Serializable)this.identifierType.nullSafeGet(rs, names[1], session, owner);
        return new ObjectTypeCacheEntry(entityName, id);
    }

    @Override
    public Object resolve(Object value, SharedSessionContractImplementor session, Object owner) throws HibernateException {
        ObjectTypeCacheEntry holder = (ObjectTypeCacheEntry)value;
        return this.resolveAny(holder.entityName, holder.id, session);
    }

    private Object resolveAny(String entityName, Serializable id, SharedSessionContractImplementor session) throws HibernateException {
        return entityName == null || id == null ? null : session.internalLoad(entityName, id, this.eager, false);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        this.nullSafeSet(st, value, index, null, session);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, boolean[] settable, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        String entityName;
        Serializable id;
        if (value == null) {
            id = null;
            entityName = null;
        } else {
            entityName = session.bestGuessEntityName(value);
            id = ForeignKeys.getEntityIdentifierIfNotUnsaved(entityName, value, session);
        }
        if (settable == null || settable[0]) {
            this.discriminatorType.nullSafeSet(st, entityName, index, session);
        }
        if (settable == null) {
            this.identifierType.nullSafeSet(st, id, index + 1, session);
        } else {
            boolean[] idSettable = new boolean[settable.length - 1];
            System.arraycopy(settable, 1, idSettable, 0, idSettable.length);
            this.identifierType.nullSafeSet(st, id, index + 1, idSettable, session);
        }
    }

    @Override
    public String toLoggableString(Object value, SessionFactoryImplementor factory) throws HibernateException {
        if (value == null) {
            return "null";
        }
        if (value == LazyPropertyInitializer.UNFETCHED_PROPERTY || !Hibernate.isInitialized(value)) {
            return "<uninitialized>";
        }
        Class valueClass = HibernateProxyHelper.getClassWithoutInitializingProxy(value);
        return factory.getTypeHelper().entity(valueClass).toLoggableString(value, factory);
    }

    @Override
    public Object assemble(Serializable cached, SharedSessionContractImplementor session, Object owner) throws HibernateException {
        ObjectTypeCacheEntry e = (ObjectTypeCacheEntry)cached;
        return e == null ? null : session.internalLoad(e.entityName, e.id, this.eager, false);
    }

    @Override
    public Serializable disassemble(Object value, SharedSessionContractImplementor session, Object owner) throws HibernateException {
        if (value == null) {
            return null;
        }
        return new ObjectTypeCacheEntry(session.bestGuessEntityName(value), ForeignKeys.getEntityIdentifierIfNotUnsaved(session.bestGuessEntityName(value), value, session));
    }

    @Override
    public Object replace(Object original, Object target, SharedSessionContractImplementor session, Object owner, Map copyCache) throws HibernateException {
        if (original == null) {
            return null;
        }
        String entityName = session.bestGuessEntityName(original);
        Serializable id = ForeignKeys.getEntityIdentifierIfNotUnsaved(entityName, original, session);
        return session.internalLoad(entityName, id, this.eager, false);
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String name, SharedSessionContractImplementor session, Object owner) {
        throw new UnsupportedOperationException("object is a multicolumn type");
    }

    @Override
    public Object semiResolve(Object value, SharedSessionContractImplementor session, Object owner) {
        throw new UnsupportedOperationException("any mappings may not form part of a property-ref");
    }

    @Override
    public boolean isMethodOf(Method method) {
        return false;
    }

    @Override
    public String[] getPropertyNames() {
        return PROPERTY_NAMES;
    }

    @Override
    public int getPropertyIndex(String name) {
        if (PROPERTY_NAMES[0].equals(name)) {
            return 0;
        }
        if (PROPERTY_NAMES[1].equals(name)) {
            return 1;
        }
        throw new PropertyNotFoundException("Unable to locate property named " + name + " on AnyType");
    }

    @Override
    public Object getPropertyValue(Object component, int i, SharedSessionContractImplementor session) throws HibernateException {
        return i == 0 ? session.bestGuessEntityName(component) : this.getIdentifier(component, session);
    }

    @Override
    public Object[] getPropertyValues(Object component, SharedSessionContractImplementor session) throws HibernateException {
        return new Object[]{session.bestGuessEntityName(component), this.getIdentifier(component, session)};
    }

    private Serializable getIdentifier(Object value, SharedSessionContractImplementor session) throws HibernateException {
        try {
            return ForeignKeys.getEntityIdentifierIfNotUnsaved(session.bestGuessEntityName(value), value, session);
        }
        catch (TransientObjectException toe) {
            return null;
        }
    }

    @Override
    public void setPropertyValues(Object component, Object[] values, EntityMode entityMode) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean[] getPropertyNullability() {
        return NULLABILITY;
    }

    @Override
    public boolean hasNotNullProperty() {
        return true;
    }

    @Override
    public Type[] getSubtypes() {
        return new Type[]{this.discriminatorType, this.identifierType};
    }

    @Override
    public CascadeStyle getCascadeStyle(int i) {
        return CascadeStyles.NONE;
    }

    @Override
    public FetchMode getFetchMode(int i) {
        return FetchMode.SELECT;
    }

    @Override
    public ForeignKeyDirection getForeignKeyDirection() {
        return ForeignKeyDirection.FROM_PARENT;
    }

    @Override
    public boolean useLHSPrimaryKey() {
        return false;
    }

    @Override
    public String getLHSPropertyName() {
        return null;
    }

    public boolean isReferenceToPrimaryKey() {
        return true;
    }

    @Override
    public String getRHSUniqueKeyPropertyName() {
        return null;
    }

    @Override
    public boolean isAlwaysDirtyChecked() {
        return false;
    }

    @Override
    public Joinable getAssociatedJoinable(SessionFactoryImplementor factory) {
        throw new UnsupportedOperationException("any types do not have a unique referenced persister");
    }

    @Override
    public String getAssociatedEntityName(SessionFactoryImplementor factory) {
        throw new UnsupportedOperationException("any types do not have a unique referenced persister");
    }

    @Override
    public String getOnCondition(String alias, SessionFactoryImplementor factory, Map enabledFilters) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getOnCondition(String alias, SessionFactoryImplementor factory, Map enabledFilters, Set<String> treatAsDeclarations) {
        throw new UnsupportedOperationException();
    }

    public static final class ObjectTypeCacheEntry
    implements Serializable {
        final String entityName;
        final Serializable id;

        ObjectTypeCacheEntry(String entityName, Serializable id) {
            this.entityName = entityName;
            this.id = id;
        }

        public int hashCode() {
            return Objects.hash(this.entityName, this.id);
        }

        public boolean equals(Object object) {
            if (object instanceof ObjectTypeCacheEntry) {
                ObjectTypeCacheEntry objectTypeCacheEntry = (ObjectTypeCacheEntry)object;
                return Objects.equals(objectTypeCacheEntry.entityName, this.entityName) && Objects.equals(objectTypeCacheEntry.id, this.id);
            }
            return false;
        }
    }
}

