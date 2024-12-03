/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;
import org.hibernate.AssertionFailure;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.engine.internal.ForeignKeys;
import org.hibernate.engine.spi.EntityUniqueKey;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.Joinable;
import org.hibernate.persister.entity.UniqueKeyLoadable;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.type.AbstractType;
import org.hibernate.type.AssociationType;
import org.hibernate.type.Type;
import org.hibernate.type.TypeFactory;

public abstract class EntityType
extends AbstractType
implements AssociationType {
    private final TypeFactory.TypeScope scope;
    private final String associatedEntityName;
    protected final String uniqueKeyPropertyName;
    private final boolean eager;
    private final boolean unwrapProxy;
    private final boolean referenceToPrimaryKey;
    private volatile transient Type associatedIdentifierType;
    private volatile transient EntityPersister associatedEntityPersister;
    private transient Class returnedClass;

    @Deprecated
    protected EntityType(TypeFactory.TypeScope scope, String entityName, String uniqueKeyPropertyName, boolean eager, boolean unwrapProxy) {
        this(scope, entityName, uniqueKeyPropertyName == null, uniqueKeyPropertyName, eager, unwrapProxy);
    }

    protected EntityType(TypeFactory.TypeScope scope, String entityName, boolean referenceToPrimaryKey, String uniqueKeyPropertyName, boolean eager, boolean unwrapProxy) {
        this.scope = scope;
        this.associatedEntityName = entityName;
        this.uniqueKeyPropertyName = uniqueKeyPropertyName;
        this.eager = eager;
        this.unwrapProxy = unwrapProxy;
        this.referenceToPrimaryKey = referenceToPrimaryKey;
    }

    protected EntityType(EntityType original, String superTypeEntityName) {
        this.scope = original.scope;
        this.associatedEntityName = superTypeEntityName;
        this.uniqueKeyPropertyName = original.uniqueKeyPropertyName;
        this.eager = original.eager;
        this.unwrapProxy = original.unwrapProxy;
        this.referenceToPrimaryKey = original.referenceToPrimaryKey;
    }

    protected TypeFactory.TypeScope scope() {
        return this.scope;
    }

    @Override
    public boolean isAssociationType() {
        return true;
    }

    @Override
    public final boolean isEntityType() {
        return true;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    public String toString() {
        return this.getClass().getName() + '(' + this.getAssociatedEntityName() + ')';
    }

    @Override
    public String getName() {
        return this.associatedEntityName;
    }

    public boolean isReferenceToPrimaryKey() {
        return this.referenceToPrimaryKey;
    }

    @Override
    public String getRHSUniqueKeyPropertyName() {
        return this.referenceToPrimaryKey ? null : this.uniqueKeyPropertyName;
    }

    @Override
    public String getLHSPropertyName() {
        return null;
    }

    public String getPropertyName() {
        return null;
    }

    public final String getAssociatedEntityName() {
        return this.associatedEntityName;
    }

    @Override
    public String getAssociatedEntityName(SessionFactoryImplementor factory) {
        return this.getAssociatedEntityName();
    }

    @Override
    public Joinable getAssociatedJoinable(SessionFactoryImplementor factory) throws MappingException {
        return (Joinable)((Object)this.getAssociatedEntityPersister(factory));
    }

    @Override
    public final Class getReturnedClass() {
        if (this.returnedClass == null) {
            this.returnedClass = this.determineAssociatedEntityClass();
        }
        return this.returnedClass;
    }

    private Class determineAssociatedEntityClass() {
        String entityName = this.getAssociatedEntityName();
        try {
            return ReflectHelper.classForName(entityName);
        }
        catch (ClassNotFoundException cnfe) {
            return this.scope.getTypeConfiguration().getSessionFactory().getMetamodel().entityPersister(entityName).getEntityTuplizer().getMappedClass();
        }
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String name, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        return this.nullSafeGet(rs, new String[]{name}, session, owner);
    }

    @Override
    public final Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        return this.resolve(this.hydrate(rs, names, session, owner), session, owner);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, boolean[] settable, SharedSessionContractImplementor session) throws SQLException {
        if (settable.length > 0) {
            this.requireIdentifierOrUniqueKeyType(session.getFactory()).nullSafeSet(st, this.getIdentifier(value, session), index, settable, session);
        }
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws SQLException {
        this.requireIdentifierOrUniqueKeyType(session.getFactory()).nullSafeSet(st, this.getIdentifier(value, session), index, session);
    }

    @Override
    public final boolean isSame(Object x, Object y) {
        return x == y;
    }

    @Override
    public int compare(Object x, Object y) {
        return 0;
    }

    @Override
    public Object deepCopy(Object value, SessionFactoryImplementor factory) {
        return value;
    }

    @Override
    public Object replace(Object original, Object target, SharedSessionContractImplementor session, Object owner, Map copyCache) throws HibernateException {
        if (original == null) {
            return null;
        }
        Object cached = copyCache.get(original);
        if (cached != null) {
            return cached;
        }
        if (original == target) {
            return target;
        }
        if (session.getContextEntityIdentifier(original) == null && ForeignKeys.isTransient(this.associatedEntityName, original, Boolean.FALSE, session)) {
            if (copyCache.containsValue(original)) {
                return original;
            }
            Object copy = session.getEntityPersister(this.associatedEntityName, original).instantiate(null, session);
            copyCache.put(original, copy);
            return copy;
        }
        Object id = this.getIdentifier(original, session);
        if (id == null) {
            throw new AssertionFailure("non-transient entity has a null id: " + original.getClass().getName());
        }
        id = this.getIdentifierOrUniqueKeyType(session.getFactory()).replace(id, null, session, owner, copyCache);
        return this.resolve(id, session, owner);
    }

    @Override
    public int getHashCode(Object x, SessionFactoryImplementor factory) {
        Class mappedClass;
        EntityPersister persister = this.getAssociatedEntityPersister(factory);
        if (!persister.canExtractIdOutOfEntity()) {
            return super.getHashCode(x);
        }
        Serializable id = x instanceof HibernateProxy ? ((HibernateProxy)x).getHibernateLazyInitializer().getInternalIdentifier() : ((mappedClass = persister.getMappedClass()).isAssignableFrom(x.getClass()) ? persister.getIdentifier(x) : (Serializable)x);
        return persister.getIdentifierType().getHashCode(id, factory);
    }

    @Override
    public boolean isEqual(Object x, Object y, SessionFactoryImplementor factory) {
        if (x == null || y == null) {
            return x == y;
        }
        EntityPersister persister = this.getAssociatedEntityPersister(factory);
        if (!persister.canExtractIdOutOfEntity()) {
            return super.isEqual(x, y);
        }
        Class mappedClass = persister.getMappedClass();
        Serializable xid = x instanceof HibernateProxy ? ((HibernateProxy)x).getHibernateLazyInitializer().getInternalIdentifier() : (mappedClass.isAssignableFrom(x.getClass()) ? persister.getIdentifier(x) : (Serializable)x);
        Serializable yid = y instanceof HibernateProxy ? ((HibernateProxy)y).getHibernateLazyInitializer().getInternalIdentifier() : (mappedClass.isAssignableFrom(y.getClass()) ? persister.getIdentifier(y) : (Serializable)y);
        return persister.getIdentifierType().isEqual(xid, yid, factory);
    }

    @Override
    public String getOnCondition(String alias, SessionFactoryImplementor factory, Map enabledFilters) {
        return this.getOnCondition(alias, factory, enabledFilters, null);
    }

    @Override
    public String getOnCondition(String alias, SessionFactoryImplementor factory, Map enabledFilters, Set<String> treatAsDeclarations) {
        if (this.isReferenceToPrimaryKey() && (treatAsDeclarations == null || treatAsDeclarations.isEmpty())) {
            return "";
        }
        return this.getAssociatedJoinable(factory).filterFragment(alias, enabledFilters, treatAsDeclarations);
    }

    @Override
    public Object resolve(Object value, SharedSessionContractImplementor session, Object owner) throws HibernateException {
        return this.resolve(value, session, owner, null);
    }

    @Override
    public Object resolve(Object value, SharedSessionContractImplementor session, Object owner, Boolean overridingEager) throws HibernateException {
        if (value != null && !this.isNull(owner, session)) {
            if (this.isReferenceToPrimaryKey()) {
                return this.resolveIdentifier((Serializable)value, session, overridingEager);
            }
            if (this.uniqueKeyPropertyName != null) {
                return this.loadByUniqueKey(this.getAssociatedEntityName(), this.uniqueKeyPropertyName, value, session);
            }
        }
        return null;
    }

    public boolean isEager(Boolean overridingEager) {
        return overridingEager != null ? overridingEager : this.eager;
    }

    @Override
    public Type getSemiResolvedType(SessionFactoryImplementor factory) {
        return this.getAssociatedEntityPersister(factory).getIdentifierType();
    }

    public EntityPersister getAssociatedEntityPersister(SessionFactoryImplementor factory) {
        EntityPersister persister = this.associatedEntityPersister;
        if (persister == null) {
            this.associatedEntityPersister = factory.getMetamodel().entityPersister(this.getAssociatedEntityName());
            return this.associatedEntityPersister;
        }
        return persister;
    }

    protected final Object getIdentifier(Object value, SharedSessionContractImplementor session) throws HibernateException {
        if (this.isReferenceToIdentifierProperty()) {
            return ForeignKeys.getEntityIdentifierIfNotUnsaved(this.getAssociatedEntityName(), value, session);
        }
        if (value == null) {
            return null;
        }
        EntityPersister entityPersister = this.getAssociatedEntityPersister(session.getFactory());
        Object propertyValue = entityPersister.getPropertyValue(value, this.uniqueKeyPropertyName);
        Type type = entityPersister.getPropertyType(this.uniqueKeyPropertyName);
        if (type.isEntityType()) {
            propertyValue = ((EntityType)type).getIdentifier(propertyValue, session);
        }
        return propertyValue;
    }

    @Override
    public String toLoggableString(Object value, SessionFactoryImplementor factory) {
        if (value == null) {
            return "null";
        }
        EntityPersister persister = this.getAssociatedEntityPersister(factory);
        if (!persister.getEntityTuplizer().isInstance(value) && persister.getIdentifierType().getReturnedClass().isInstance(value)) {
            return this.associatedEntityName + "#" + value;
        }
        StringBuilder result = new StringBuilder().append(this.associatedEntityName);
        if (persister.hasIdentifierProperty()) {
            Serializable id;
            if (value instanceof HibernateProxy) {
                HibernateProxy proxy = (HibernateProxy)value;
                id = proxy.getHibernateLazyInitializer().getInternalIdentifier();
            } else {
                id = persister.getIdentifier(value);
            }
            result.append('#').append(persister.getIdentifierType().toLoggableString(id, factory));
        }
        return result.toString();
    }

    public abstract boolean isOneToOne();

    public boolean isLogicalOneToOne() {
        return this.isOneToOne();
    }

    Type getIdentifierType(Mapping factory) {
        Type type = this.associatedIdentifierType;
        if (type == null) {
            this.associatedIdentifierType = factory.getIdentifierType(this.getAssociatedEntityName());
            return this.associatedIdentifierType;
        }
        return type;
    }

    Type getIdentifierType(SharedSessionContractImplementor session) {
        Type type = this.associatedIdentifierType;
        if (type == null) {
            this.associatedIdentifierType = this.getIdentifierType(session.getFactory());
            return this.associatedIdentifierType;
        }
        return type;
    }

    public final Type getIdentifierOrUniqueKeyType(Mapping factory) throws MappingException {
        if (this.isReferenceToIdentifierProperty()) {
            return this.getIdentifierType(factory);
        }
        Type type = factory.getReferencedPropertyType(this.getAssociatedEntityName(), this.uniqueKeyPropertyName);
        if (type.isEntityType()) {
            type = ((EntityType)type).getIdentifierOrUniqueKeyType(factory);
        }
        return type;
    }

    public final String getIdentifierOrUniqueKeyPropertyName(Mapping factory) throws MappingException {
        return this.isReferenceToIdentifierProperty() ? factory.getIdentifierPropertyName(this.getAssociatedEntityName()) : this.uniqueKeyPropertyName;
    }

    public boolean isReferenceToIdentifierProperty() {
        return this.isReferenceToPrimaryKey() || this.uniqueKeyPropertyName == null;
    }

    public abstract boolean isNullable();

    public abstract NotFoundAction getNotFoundAction();

    public boolean hasNotFoundAction() {
        return this.getNotFoundAction() != null;
    }

    protected final Object resolveIdentifier(Serializable id, SharedSessionContractImplementor session, Boolean overridingEager) throws HibernateException {
        boolean isProxyUnwrapEnabled = this.unwrapProxy && this.getAssociatedEntityPersister(session.getFactory()).isInstrumented();
        Object proxyOrEntity = session.internalLoad(this.getAssociatedEntityName(), id, this.isEager(overridingEager), this.isNullable());
        if (proxyOrEntity instanceof HibernateProxy) {
            ((HibernateProxy)proxyOrEntity).getHibernateLazyInitializer().setUnwrap(isProxyUnwrapEnabled);
        }
        return proxyOrEntity;
    }

    protected final Object resolveIdentifier(Serializable id, SharedSessionContractImplementor session) throws HibernateException {
        return this.resolveIdentifier(id, session, null);
    }

    protected boolean isNull(Object owner, SharedSessionContractImplementor session) {
        return false;
    }

    public Object loadByUniqueKey(String entityName, String uniqueKeyPropertyName, Object key, SharedSessionContractImplementor session) throws HibernateException {
        SessionFactoryImplementor factory = session.getFactory();
        UniqueKeyLoadable persister = (UniqueKeyLoadable)factory.getMetamodel().entityPersister(entityName);
        EntityUniqueKey euk = new EntityUniqueKey(entityName, uniqueKeyPropertyName, key, this.getIdentifierOrUniqueKeyType(factory), persister.getEntityMode(), session.getFactory());
        PersistenceContext persistenceContext = session.getPersistenceContextInternal();
        Object result = persistenceContext.getEntity(euk);
        if (result == null && (result = persister.loadByUniqueKey(uniqueKeyPropertyName, key, session)) != null) {
            persistenceContext.addEntity(euk, result);
        }
        return result == null ? null : persistenceContext.proxyFor(result);
    }

    protected Type requireIdentifierOrUniqueKeyType(Mapping mapping) {
        Type fkTargetType = this.getIdentifierOrUniqueKeyType(mapping);
        if (fkTargetType == null) {
            throw new MappingException("Unable to determine FK target Type for many-to-one or one-to-one mapping: referenced-entity-name=[" + this.getAssociatedEntityName() + "], referenced-entity-attribute-name=[" + this.getLHSPropertyName() + "]");
        }
        return fkTargetType;
    }
}

