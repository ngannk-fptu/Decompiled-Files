/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import org.hibernate.AssertionFailure;
import org.hibernate.FetchNotFoundException;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.engine.internal.ForeignKeys;
import org.hibernate.engine.jdbc.Size;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.EntityKey;
import org.hibernate.engine.spi.EntityUniqueKey;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.Loadable;
import org.hibernate.type.EntityType;
import org.hibernate.type.ForeignKeyDirection;
import org.hibernate.type.TypeFactory;

public class ManyToOneType
extends EntityType {
    private final String propertyName;
    private final NotFoundAction notFoundAction;
    private boolean isLogicalOneToOne;

    public ManyToOneType(TypeFactory.TypeScope scope, String referencedEntityName) {
        this(scope, referencedEntityName, false);
    }

    public ManyToOneType(TypeFactory.TypeScope scope, String referencedEntityName, boolean lazy) {
        this(scope, referencedEntityName, true, null, lazy, true, null, false);
    }

    @Deprecated
    public ManyToOneType(TypeFactory.TypeScope scope, String referencedEntityName, String uniqueKeyPropertyName, boolean lazy, boolean unwrapProxy, boolean isEmbeddedInXML, NotFoundAction notFoundAction, boolean isLogicalOneToOne) {
        this(scope, referencedEntityName, uniqueKeyPropertyName == null, uniqueKeyPropertyName, lazy, unwrapProxy, notFoundAction, isLogicalOneToOne);
    }

    @Deprecated
    public ManyToOneType(TypeFactory.TypeScope scope, String referencedEntityName, boolean referenceToPrimaryKey, String uniqueKeyPropertyName, boolean lazy, boolean unwrapProxy, NotFoundAction notFoundAction, boolean isLogicalOneToOne) {
        this(scope, referencedEntityName, referenceToPrimaryKey, uniqueKeyPropertyName, null, lazy, unwrapProxy, notFoundAction, isLogicalOneToOne);
    }

    public ManyToOneType(TypeFactory.TypeScope scope, String referencedEntityName, boolean referenceToPrimaryKey, String uniqueKeyPropertyName, String propertyName, boolean lazy, boolean unwrapProxy, NotFoundAction notFoundAction, boolean isLogicalOneToOne) {
        super(scope, referencedEntityName, referenceToPrimaryKey, uniqueKeyPropertyName, !lazy, unwrapProxy);
        this.propertyName = propertyName;
        this.notFoundAction = notFoundAction;
        this.isLogicalOneToOne = isLogicalOneToOne;
    }

    public ManyToOneType(ManyToOneType original, String superTypeEntityName) {
        super(original, superTypeEntityName);
        this.propertyName = original.propertyName;
        this.notFoundAction = original.notFoundAction;
        this.isLogicalOneToOne = original.isLogicalOneToOne;
    }

    @Override
    public boolean isNullable() {
        return this.notFoundAction != null;
    }

    @Override
    public NotFoundAction getNotFoundAction() {
        return this.notFoundAction;
    }

    @Override
    public String getPropertyName() {
        return this.propertyName;
    }

    @Override
    public boolean isAlwaysDirtyChecked() {
        return true;
    }

    @Override
    public boolean isOneToOne() {
        return false;
    }

    @Override
    public boolean isLogicalOneToOne() {
        return this.isLogicalOneToOne;
    }

    @Override
    public int getColumnSpan(Mapping mapping) throws MappingException {
        return this.requireIdentifierOrUniqueKeyType(mapping).getColumnSpan(mapping);
    }

    @Override
    public int[] sqlTypes(Mapping mapping) throws MappingException {
        return this.requireIdentifierOrUniqueKeyType(mapping).sqlTypes(mapping);
    }

    @Override
    public Size[] dictatedSizes(Mapping mapping) throws MappingException {
        return this.requireIdentifierOrUniqueKeyType(mapping).dictatedSizes(mapping);
    }

    @Override
    public Size[] defaultSizes(Mapping mapping) throws MappingException {
        return this.requireIdentifierOrUniqueKeyType(mapping).defaultSizes(mapping);
    }

    @Override
    public ForeignKeyDirection getForeignKeyDirection() {
        return ForeignKeyDirection.FROM_PARENT;
    }

    @Override
    public Object hydrate(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        Object hydratedId = this.getIdentifierOrUniqueKeyType(session.getFactory()).hydrate(rs, names, session, null);
        Serializable id = hydratedId != null ? (Serializable)this.getIdentifierOrUniqueKeyType(session.getFactory()).resolve(hydratedId, session, null) : null;
        this.scheduleBatchLoadIfNeeded(id, session);
        return id;
    }

    private void scheduleBatchLoadIfNeeded(Serializable id, SharedSessionContractImplementor session) throws MappingException {
        EntityPersister persister;
        if (this.uniqueKeyPropertyName == null && id != null && (persister = this.getAssociatedEntityPersister(session.getFactory())).isBatchLoadable()) {
            EntityKey entityKey = session.generateEntityKey(id, persister);
            PersistenceContext persistenceContext = session.getPersistenceContextInternal();
            if (!persistenceContext.containsEntity(entityKey)) {
                persistenceContext.getBatchFetchQueue().addBatchLoadableEntityKey(entityKey);
            }
        }
    }

    @Override
    public boolean useLHSPrimaryKey() {
        return false;
    }

    @Override
    public boolean isModified(Object old, Object current, boolean[] checkable, SharedSessionContractImplementor session) throws HibernateException {
        if (current == null) {
            return old != null;
        }
        if (old == null) {
            return true;
        }
        return this.getIdentifierOrUniqueKeyType(session.getFactory()).isDirty(old, this.getIdentifier(current, session), session);
    }

    @Override
    public Object resolve(Object value, SharedSessionContractImplementor session, Object owner, Boolean overridingEager) throws HibernateException {
        PersistenceContext persistenceContext;
        EntityEntry entry;
        Object resolvedValue;
        try {
            resolvedValue = super.resolve(value, session, owner, overridingEager);
        }
        catch (ObjectNotFoundException e) {
            throw new FetchNotFoundException(this.getAssociatedEntityName(), value);
        }
        if (value != null && resolvedValue == null && this.getNotFoundAction() == NotFoundAction.EXCEPTION) {
            throw new FetchNotFoundException(this.getAssociatedEntityName(), value);
        }
        if (this.isLogicalOneToOne && value != null && this.getPropertyName() != null && (entry = (persistenceContext = session.getPersistenceContextInternal()).getEntry(owner)) != null) {
            Loadable ownerPersister = (Loadable)session.getFactory().getMetamodel().entityPersister(entry.getEntityName());
            EntityUniqueKey entityKey = new EntityUniqueKey(ownerPersister.getEntityName(), this.getPropertyName(), value, this, ownerPersister.getEntityMode(), session.getFactory());
            persistenceContext.addEntity(entityKey, owner);
        }
        return resolvedValue;
    }

    @Override
    public Serializable disassemble(Object value, SharedSessionContractImplementor session, Object owner) throws HibernateException {
        if (value == null) {
            return null;
        }
        Serializable id = ForeignKeys.getEntityIdentifierIfNotUnsaved(this.getAssociatedEntityName(), value, session);
        if (id == null) {
            throw new AssertionFailure("cannot cache a reference to an object with a null id: " + this.getAssociatedEntityName());
        }
        return this.getIdentifierType(session).disassemble(id, session, owner);
    }

    @Override
    public Object assemble(Serializable oid, SharedSessionContractImplementor session, Object owner) throws HibernateException {
        Serializable id = this.assembleId(oid, session);
        if (id == null) {
            return null;
        }
        return this.resolveIdentifier(id, session);
    }

    private Serializable assembleId(Serializable oid, SharedSessionContractImplementor session) {
        return (Serializable)this.getIdentifierType(session).assemble(oid, session, null);
    }

    @Override
    public void beforeAssemble(Serializable oid, SharedSessionContractImplementor session) {
        this.scheduleBatchLoadIfNeeded(this.assembleId(oid, session), session);
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
    public boolean isDirty(Object old, Object current, SharedSessionContractImplementor session) throws HibernateException {
        if (this.isSame(old, current)) {
            return false;
        }
        Object oldid = this.getIdentifier(old, session);
        Object newid = this.getIdentifier(current, session);
        return this.getIdentifierType(session).isDirty(oldid, newid, session);
    }

    @Override
    public boolean isDirty(Object old, Object current, boolean[] checkable, SharedSessionContractImplementor session) throws HibernateException {
        if (this.isAlwaysDirtyChecked()) {
            return this.isDirty(old, current, session);
        }
        if (this.isSame(old, current)) {
            return false;
        }
        Object oldid = this.getIdentifier(old, session);
        Object newid = this.getIdentifier(current, session);
        return this.getIdentifierType(session).isDirty(oldid, newid, checkable, session);
    }
}

