/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.internal;

import java.io.Serializable;
import java.util.Map;
import org.hibernate.LockMode;
import org.hibernate.NonUniqueObjectException;
import org.hibernate.action.internal.AbstractEntityInsertAction;
import org.hibernate.action.internal.EntityIdentityInsertAction;
import org.hibernate.action.internal.EntityInsertAction;
import org.hibernate.classic.Lifecycle;
import org.hibernate.engine.internal.Cascade;
import org.hibernate.engine.internal.CascadePoint;
import org.hibernate.engine.internal.ManagedTypeHelper;
import org.hibernate.engine.internal.Versioning;
import org.hibernate.engine.spi.CascadingAction;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.EntityEntryExtraState;
import org.hibernate.engine.spi.EntityKey;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.SelfDirtinessTracker;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.Status;
import org.hibernate.event.internal.AbstractReassociateEventListener;
import org.hibernate.event.internal.WrapVisitor;
import org.hibernate.event.spi.EventSource;
import org.hibernate.id.IdentifierGenerationException;
import org.hibernate.id.IdentifierGeneratorHelper;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.jpa.event.spi.CallbackRegistry;
import org.hibernate.jpa.event.spi.CallbackRegistryConsumer;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.pretty.MessageHelper;
import org.hibernate.type.Type;
import org.hibernate.type.TypeHelper;

public abstract class AbstractSaveEventListener
extends AbstractReassociateEventListener
implements CallbackRegistryConsumer {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(AbstractSaveEventListener.class);
    private CallbackRegistry callbackRegistry;

    @Override
    public void injectCallbackRegistry(CallbackRegistry callbackRegistry) {
        this.callbackRegistry = callbackRegistry;
    }

    protected Serializable saveWithRequestedId(Object entity, Serializable requestedId, String entityName, Object anything, EventSource source) {
        this.callbackRegistry.preCreate(entity);
        return this.performSave(entity, requestedId, source.getEntityPersister(entityName, entity), false, anything, source, true);
    }

    protected Serializable saveWithGeneratedId(Object entity, String entityName, Object anything, EventSource source, boolean requiresImmediateIdAccess) {
        this.callbackRegistry.preCreate(entity);
        ManagedTypeHelper.processIfSelfDirtinessTracker(entity, SelfDirtinessTracker::$$_hibernate_clearDirtyAttributes);
        EntityPersister persister = source.getEntityPersister(entityName, entity);
        Serializable generatedId = persister.getIdentifierGenerator().generate(source, entity);
        if (generatedId == null) {
            throw new IdentifierGenerationException("null id generated for:" + entity.getClass());
        }
        if (generatedId == IdentifierGeneratorHelper.SHORT_CIRCUIT_INDICATOR) {
            return source.getIdentifier(entity);
        }
        if (generatedId == IdentifierGeneratorHelper.POST_INSERT_INDICATOR) {
            return this.performSave(entity, null, persister, true, anything, source, requiresImmediateIdAccess);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debugf("Generated identifier: %s, using strategy: %s", persister.getIdentifierType().toLoggableString(generatedId, source.getFactory()), persister.getIdentifierGenerator().getClass().getName());
        }
        return this.performSave(entity, generatedId, persister, false, anything, source, true);
    }

    protected Serializable performSave(Object entity, Serializable id, EntityPersister persister, boolean useIdentityColumn, Object anything, EventSource source, boolean requiresImmediateIdAccess) {
        EntityKey key;
        if (LOG.isTraceEnabled()) {
            LOG.tracev("Saving {0}", MessageHelper.infoString(persister, id, source.getFactory()));
        }
        if (!useIdentityColumn) {
            key = source.generateEntityKey(id, persister);
            PersistenceContext persistenceContext = source.getPersistenceContextInternal();
            Object old = persistenceContext.getEntity(key);
            if (old != null) {
                if (persistenceContext.getEntry(old).getStatus() == Status.DELETED) {
                    source.forceFlush(persistenceContext.getEntry(old));
                } else {
                    throw new NonUniqueObjectException(id, persister.getEntityName());
                }
            }
            persister.setIdentifier(entity, id, source);
        } else {
            key = null;
        }
        if (this.invokeSaveLifecycle(entity, persister, source)) {
            return id;
        }
        return this.performSaveOrReplicate(entity, key, persister, useIdentityColumn, anything, source, requiresImmediateIdAccess);
    }

    protected boolean invokeSaveLifecycle(Object entity, EntityPersister persister, EventSource source) {
        if (persister.implementsLifecycle()) {
            LOG.debug("Calling onSave()");
            if (((Lifecycle)entity).onSave(source)) {
                LOG.debug("Insertion vetoed by onSave()");
                return true;
            }
        }
        return false;
    }

    protected Serializable performSaveOrReplicate(Object entity, EntityKey key, EntityPersister persister, boolean useIdentityColumn, Object anything, EventSource source, boolean requiresImmediateIdAccess) {
        EntityEntryExtraState extraState;
        EntityEntry newEntry;
        Serializable id = key == null ? null : key.getIdentifier();
        boolean inTrx = source.isTransactionInProgress();
        boolean shouldDelayIdentityInserts = !inTrx && !requiresImmediateIdAccess;
        PersistenceContext persistenceContext = source.getPersistenceContextInternal();
        EntityEntry original = persistenceContext.addEntry(entity, Status.SAVING, null, null, id, null, LockMode.WRITE, useIdentityColumn, persister, false);
        this.cascadeBeforeSave(source, persister, entity, anything);
        Object[] values = persister.getPropertyValuesToInsert(entity, this.getMergeMap(anything), source);
        Type[] types = persister.getPropertyTypes();
        boolean substitute = this.substituteValuesIfNecessary(entity, id, values, persister, source);
        if (persister.hasCollections()) {
            boolean bl = substitute = this.visitCollectionsBeforeSave(entity, id, values, types, source) || substitute;
        }
        if (substitute) {
            persister.setPropertyValues(entity, values);
        }
        TypeHelper.deepCopy(values, types, persister.getPropertyUpdateability(), values, source);
        AbstractEntityInsertAction insert = this.addInsertAction(values, id, entity, persister, useIdentityColumn, source, shouldDelayIdentityInserts);
        this.cascadeAfterSave(source, persister, entity, anything);
        if (useIdentityColumn && insert.isEarlyInsert()) {
            if (!EntityIdentityInsertAction.class.isInstance(insert)) {
                throw new IllegalStateException("Insert should be using an identity column, but action is of unexpected type: " + insert.getClass().getName());
            }
            id = ((EntityIdentityInsertAction)insert).getGeneratedId();
            insert.handleNaturalIdPostSaveNotifications(id);
        }
        if ((newEntry = persistenceContext.getEntry(entity)) != original && (extraState = newEntry.getExtraState(EntityEntryExtraState.class)) == null) {
            newEntry.addExtraState(original.getExtraState(EntityEntryExtraState.class));
        }
        return id;
    }

    private AbstractEntityInsertAction addInsertAction(Object[] values, Serializable id, Object entity, EntityPersister persister, boolean useIdentityColumn, EventSource source, boolean shouldDelayIdentityInserts) {
        if (useIdentityColumn) {
            EntityIdentityInsertAction insert = new EntityIdentityInsertAction(values, entity, persister, this.isVersionIncrementDisabled(), source, shouldDelayIdentityInserts);
            source.getActionQueue().addAction(insert);
            return insert;
        }
        Object version = Versioning.getVersion(values, persister);
        EntityInsertAction insert = new EntityInsertAction(id, values, entity, version, persister, this.isVersionIncrementDisabled(), source);
        source.getActionQueue().addAction(insert);
        return insert;
    }

    protected Map getMergeMap(Object anything) {
        return null;
    }

    protected boolean isVersionIncrementDisabled() {
        return false;
    }

    protected boolean visitCollectionsBeforeSave(Object entity, Serializable id, Object[] values, Type[] types, EventSource source) {
        WrapVisitor visitor = new WrapVisitor(entity, id, source);
        visitor.processEntityPropertyValues(values, types);
        return visitor.isSubstitutionRequired();
    }

    protected boolean substituteValuesIfNecessary(Object entity, Serializable id, Object[] values, EntityPersister persister, SessionImplementor source) {
        boolean substitute = source.getInterceptor().onSave(entity, id, values, persister.getPropertyNames(), persister.getPropertyTypes());
        if (persister.isVersioned()) {
            substitute = Versioning.seedVersion(values, persister.getVersionProperty(), persister.getVersionType(), source) || substitute;
        }
        return substitute;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void cascadeBeforeSave(EventSource source, EntityPersister persister, Object entity, Object anything) {
        PersistenceContext persistenceContext = source.getPersistenceContextInternal();
        persistenceContext.incrementCascadeLevel();
        try {
            Cascade.cascade(this.getCascadeAction(), CascadePoint.BEFORE_INSERT_AFTER_DELETE, source, persister, entity, anything);
        }
        finally {
            persistenceContext.decrementCascadeLevel();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void cascadeAfterSave(EventSource source, EntityPersister persister, Object entity, Object anything) {
        PersistenceContext persistenceContext = source.getPersistenceContextInternal();
        persistenceContext.incrementCascadeLevel();
        try {
            Cascade.cascade(this.getCascadeAction(), CascadePoint.AFTER_INSERT_BEFORE_DELETE, source, persister, entity, anything);
        }
        finally {
            persistenceContext.decrementCascadeLevel();
        }
    }

    protected abstract CascadingAction getCascadeAction();
}

