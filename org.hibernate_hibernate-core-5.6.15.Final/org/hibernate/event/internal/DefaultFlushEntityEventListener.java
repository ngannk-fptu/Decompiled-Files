/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.internal;

import java.io.Serializable;
import java.util.Arrays;
import org.hibernate.AssertionFailure;
import org.hibernate.CustomEntityDirtinessStrategy;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.StaleObjectStateException;
import org.hibernate.action.internal.DelayedPostInsertIdentifier;
import org.hibernate.action.internal.EntityUpdateAction;
import org.hibernate.bytecode.enhance.spi.LazyPropertyInitializer;
import org.hibernate.bytecode.enhance.spi.interceptor.EnhancementAsProxyLazinessInterceptor;
import org.hibernate.engine.internal.ManagedTypeHelper;
import org.hibernate.engine.internal.Nullability;
import org.hibernate.engine.internal.Versioning;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.EntityKey;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.PersistentAttributeInterceptable;
import org.hibernate.engine.spi.PersistentAttributeInterceptor;
import org.hibernate.engine.spi.SelfDirtinessTracker;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.Status;
import org.hibernate.event.internal.DirtyCollectionSearchVisitor;
import org.hibernate.event.internal.FlushVisitor;
import org.hibernate.event.internal.WrapVisitor;
import org.hibernate.event.spi.EventSource;
import org.hibernate.event.spi.FlushEntityEvent;
import org.hibernate.event.spi.FlushEntityEventListener;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.jpa.event.spi.CallbackRegistry;
import org.hibernate.jpa.event.spi.CallbackRegistryConsumer;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.pretty.MessageHelper;
import org.hibernate.stat.spi.StatisticsImplementor;
import org.hibernate.type.Type;

public class DefaultFlushEntityEventListener
implements FlushEntityEventListener,
CallbackRegistryConsumer {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(DefaultFlushEntityEventListener.class);
    private CallbackRegistry callbackRegistry;

    @Override
    public void injectCallbackRegistry(CallbackRegistry callbackRegistry) {
        this.callbackRegistry = callbackRegistry;
    }

    public void checkId(Object object, EntityPersister persister, Serializable id, SessionImplementor session) throws HibernateException {
        if (id != null && id instanceof DelayedPostInsertIdentifier) {
            return;
        }
        if (persister.canExtractIdOutOfEntity()) {
            Serializable oid = persister.getIdentifier(object, session);
            if (id == null) {
                throw new AssertionFailure("null id in " + persister.getEntityName() + " entry (don't flush the Session after an exception occurs)");
            }
            if (!persister.getIdentifierType().isEqual(id, oid, session.getFactory())) {
                throw new HibernateException("identifier of an instance of " + persister.getEntityName() + " was altered from " + id + " to " + oid);
            }
        }
    }

    private void checkNaturalId(EntityPersister persister, Object entity, EntityEntry entry, Object[] current, Object[] loaded, SessionImplementor session) {
        PersistentAttributeInterceptable asPersistentAttributeInterceptable;
        PersistentAttributeInterceptor interceptor;
        if (ManagedTypeHelper.isPersistentAttributeInterceptable(entity) && (interceptor = (asPersistentAttributeInterceptable = ManagedTypeHelper.asPersistentAttributeInterceptable(entity)).$$_hibernate_getInterceptor()) instanceof EnhancementAsProxyLazinessInterceptor) {
            return;
        }
        if (persister.hasNaturalIdentifier() && entry.getStatus() != Status.READ_ONLY) {
            if (!persister.getEntityMetamodel().hasImmutableNaturalId()) {
                return;
            }
            int[] naturalIdentifierPropertiesIndexes = persister.getNaturalIdentifierProperties();
            Type[] propertyTypes = persister.getPropertyTypes();
            boolean[] propertyUpdateability = persister.getPropertyUpdateability();
            PersistenceContext persistenceContext = session.getPersistenceContextInternal();
            Object[] snapshot = loaded == null ? persistenceContext.getNaturalIdSnapshot(entry.getId(), persister) : persistenceContext.getNaturalIdHelper().extractNaturalIdValues(loaded, persister);
            for (int i = 0; i < naturalIdentifierPropertiesIndexes.length; ++i) {
                Type propertyType;
                int naturalIdentifierPropertyIndex = naturalIdentifierPropertiesIndexes[i];
                if (propertyUpdateability[naturalIdentifierPropertyIndex] || (propertyType = propertyTypes[naturalIdentifierPropertyIndex]).isEqual(current[naturalIdentifierPropertyIndex], snapshot[i])) continue;
                throw new HibernateException(String.format("An immutable natural identifier of entity %s was altered from `%s` to `%s`", persister.getEntityName(), propertyTypes[naturalIdentifierPropertyIndex].toLoggableString(snapshot[i], session.getFactory()), propertyTypes[naturalIdentifierPropertyIndex].toLoggableString(current[naturalIdentifierPropertyIndex], session.getFactory())));
            }
        }
    }

    @Override
    public void onFlushEntity(FlushEntityEvent event) throws HibernateException {
        Object entity = event.getEntity();
        EntityEntry entry = event.getEntityEntry();
        EventSource session = event.getSession();
        EntityPersister persister = entry.getPersister();
        Status status = entry.getStatus();
        Type[] types = persister.getPropertyTypes();
        boolean mightBeDirty = entry.requiresDirtyCheck(entity);
        Object[] values = this.getValues(entity, entry, mightBeDirty, session);
        event.setPropertyValues(values);
        boolean substitute = this.wrapCollections(session, persister, entity, entry.getId(), types, values);
        if (this.isUpdateNecessary(event, mightBeDirty)) {
            boolean bl = substitute = this.scheduleUpdate(event) || substitute;
        }
        if (status != Status.DELETED) {
            if (substitute) {
                persister.setPropertyValues(entity, values);
            }
            if (persister.hasCollections()) {
                new FlushVisitor(session, entity).processEntityPropertyValues(values, types);
            }
        }
    }

    private Object[] getValues(Object entity, EntityEntry entry, boolean mightBeDirty, SessionImplementor session) {
        Object[] values;
        Object[] loadedState = entry.getLoadedState();
        Status status = entry.getStatus();
        EntityPersister persister = entry.getPersister();
        if (status == Status.DELETED) {
            values = entry.getDeletedState();
        } else if (!mightBeDirty && loadedState != null) {
            values = loadedState;
        } else {
            this.checkId(entity, persister, entry.getId(), session);
            values = persister.getPropertyValues(entity);
            this.checkNaturalId(persister, entity, entry, values, loadedState, session);
        }
        return values;
    }

    private boolean wrapCollections(EventSource session, EntityPersister persister, Object entity, Serializable id, Type[] types, Object[] values) {
        if (persister.hasCollections()) {
            WrapVisitor visitor = new WrapVisitor(entity, id, session);
            visitor.processEntityPropertyValues(values, types);
            return visitor.isSubstitutionRequired();
        }
        return false;
    }

    private boolean isUpdateNecessary(FlushEntityEvent event, boolean mightBeDirty) {
        Status status = event.getEntityEntry().getStatus();
        if (mightBeDirty || status == Status.DELETED) {
            this.dirtyCheck(event);
            if (this.isUpdateNecessary(event)) {
                return true;
            }
            ManagedTypeHelper.processIfSelfDirtinessTracker(event.getEntity(), SelfDirtinessTracker::$$_hibernate_clearDirtyAttributes);
            event.getSession().getFactory().getCustomEntityDirtinessStrategy().resetDirty(event.getEntity(), event.getEntityEntry().getPersister(), event.getSession());
            return false;
        }
        return this.hasDirtyCollections(event, event.getEntityEntry().getPersister(), status);
    }

    private boolean scheduleUpdate(FlushEntityEvent event) {
        EntityEntry entry = event.getEntityEntry();
        EventSource session = event.getSession();
        Object entity = event.getEntity();
        Status status = entry.getStatus();
        EntityPersister persister = entry.getPersister();
        Object[] values = event.getPropertyValues();
        if (LOG.isTraceEnabled()) {
            if (status == Status.DELETED) {
                if (!persister.isMutable()) {
                    LOG.tracev("Updating immutable, deleted entity: {0}", MessageHelper.infoString(persister, entry.getId(), session.getFactory()));
                } else if (!entry.isModifiableEntity()) {
                    LOG.tracev("Updating non-modifiable, deleted entity: {0}", MessageHelper.infoString(persister, entry.getId(), session.getFactory()));
                } else {
                    LOG.tracev("Updating deleted entity: ", MessageHelper.infoString(persister, entry.getId(), session.getFactory()));
                }
            } else {
                LOG.tracev("Updating entity: {0}", MessageHelper.infoString(persister, entry.getId(), session.getFactory()));
            }
        }
        boolean intercepted = !entry.isBeingReplicated() && this.handleInterception(event);
        Object nextVersion = this.getNextVersion(event);
        int[] dirtyProperties = event.getDirtyProperties();
        if (event.isDirtyCheckPossible() && dirtyProperties == null) {
            if (!intercepted && !event.hasDirtyCollection()) {
                throw new AssertionFailure("dirty, but no dirty properties");
            }
            dirtyProperties = ArrayHelper.EMPTY_INT_ARRAY;
        }
        new Nullability(session).checkNullability(values, persister, true);
        session.getActionQueue().addAction(new EntityUpdateAction(entry.getId(), values, dirtyProperties, event.hasDirtyCollection(), status == Status.DELETED && !entry.isModifiableEntity() ? persister.getPropertyValues(entity) : entry.getLoadedState(), entry.getVersion(), nextVersion, entity, entry.getRowId(), persister, session));
        return intercepted;
    }

    protected boolean handleInterception(FlushEntityEvent event) {
        Object[] values;
        EventSource session = event.getSession();
        EntityEntry entry = event.getEntityEntry();
        EntityPersister persister = entry.getPersister();
        Object entity = event.getEntity();
        boolean intercepted = this.invokeInterceptor(session, entity, entry, values = event.getPropertyValues(), persister);
        if (intercepted && event.isDirtyCheckPossible()) {
            this.dirtyCheck(event);
        }
        return intercepted;
    }

    protected boolean invokeInterceptor(SessionImplementor session, Object entity, EntityEntry entry, Object[] values, EntityPersister persister) {
        boolean answerFromInterceptor;
        boolean isDirty = false;
        if (entry.getStatus() != Status.DELETED && this.callbackRegistry.preUpdate(entity)) {
            isDirty = this.copyState(entity, persister.getPropertyTypes(), values, session.getFactory());
        }
        return (answerFromInterceptor = session.getInterceptor().onFlushDirty(entity, entry.getId(), values, entry.getLoadedState(), persister.getPropertyNames(), persister.getPropertyTypes())) || isDirty;
    }

    private boolean copyState(Object entity, Type[] types, Object[] state, SessionFactory sf) {
        ClassMetadata metadata = sf.getClassMetadata(entity.getClass());
        Object[] newState = metadata.getPropertyValues(entity);
        int size = newState.length;
        boolean isDirty = false;
        for (int index = 0; index < size; ++index) {
            if ((state[index] != LazyPropertyInitializer.UNFETCHED_PROPERTY || newState[index] == LazyPropertyInitializer.UNFETCHED_PROPERTY) && (state[index] == newState[index] || types[index].isEqual(state[index], newState[index]))) continue;
            isDirty = true;
            state[index] = newState[index];
        }
        return isDirty;
    }

    private Object getNextVersion(FlushEntityEvent event) throws HibernateException {
        EntityEntry entry = event.getEntityEntry();
        EntityPersister persister = entry.getPersister();
        if (persister.isVersioned()) {
            Object[] values = event.getPropertyValues();
            if (entry.isBeingReplicated()) {
                return Versioning.getVersion(values, persister);
            }
            int[] dirtyProperties = event.getDirtyProperties();
            boolean isVersionIncrementRequired = this.isVersionIncrementRequired(event, entry, persister, dirtyProperties);
            Object nextVersion = isVersionIncrementRequired ? Versioning.increment(entry.getVersion(), persister.getVersionType(), event.getSession()) : entry.getVersion();
            Versioning.setVersion(values, nextVersion, persister);
            return nextVersion;
        }
        return null;
    }

    private boolean isVersionIncrementRequired(FlushEntityEvent event, EntityEntry entry, EntityPersister persister, int[] dirtyProperties) {
        boolean isVersionIncrementRequired = entry.getStatus() != Status.DELETED && (dirtyProperties == null || Versioning.isVersionIncrementRequired(dirtyProperties, event.hasDirtyCollection(), persister.getPropertyVersionability()));
        return isVersionIncrementRequired;
    }

    protected final boolean isUpdateNecessary(FlushEntityEvent event) throws HibernateException {
        EntityPersister persister = event.getEntityEntry().getPersister();
        Status status = event.getEntityEntry().getStatus();
        if (!event.isDirtyCheckPossible()) {
            return true;
        }
        int[] dirtyProperties = event.getDirtyProperties();
        if (dirtyProperties != null && dirtyProperties.length != 0) {
            return true;
        }
        return this.hasDirtyCollections(event, persister, status);
    }

    private boolean hasDirtyCollections(FlushEntityEvent event, EntityPersister persister, Status status) {
        if (this.isCollectionDirtyCheckNecessary(persister, status)) {
            DirtyCollectionSearchVisitor visitor = new DirtyCollectionSearchVisitor(event.getEntity(), event.getSession(), persister.getPropertyVersionability());
            visitor.processEntityPropertyValues(event.getPropertyValues(), persister.getPropertyTypes());
            boolean hasDirtyCollections = visitor.wasDirtyCollectionFound();
            event.setHasDirtyCollection(hasDirtyCollections);
            return hasDirtyCollections;
        }
        return false;
    }

    private boolean isCollectionDirtyCheckNecessary(EntityPersister persister, Status status) {
        return (status == Status.MANAGED || status == Status.READ_ONLY) && persister.isVersioned() && persister.hasCollections();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void dirtyCheck(final FlushEntityEvent event) throws HibernateException {
        boolean interceptorHandledDirtyCheck;
        boolean dirtyCheckPossible;
        int[] dirtyProperties;
        Serializable id;
        EntityPersister persister;
        block10: {
            block9: {
                EventSource session;
                block8: {
                    Object entity = event.getEntity();
                    Object[] values = event.getPropertyValues();
                    session = event.getSession();
                    EntityEntry entry = event.getEntityEntry();
                    persister = entry.getPersister();
                    id = entry.getId();
                    Object[] loadedState = entry.getLoadedState();
                    dirtyProperties = session.getInterceptor().findDirty(entity, id, values, loadedState, persister.getPropertyNames(), persister.getPropertyTypes());
                    if (dirtyProperties == null) {
                        if (ManagedTypeHelper.isSelfDirtinessTracker(entity)) {
                            SelfDirtinessTracker asSelfDirtinessTracker = ManagedTypeHelper.asSelfDirtinessTracker(entity);
                            dirtyProperties = asSelfDirtinessTracker.$$_hibernate_hasDirtyAttributes() || persister.hasMutableProperties() ? persister.resolveDirtyAttributeIndexes(values, loadedState, asSelfDirtinessTracker.$$_hibernate_getDirtyAttributes(), session) : ArrayHelper.EMPTY_INT_ARRAY;
                        } else {
                            class DirtyCheckContextImpl
                            implements CustomEntityDirtinessStrategy.DirtyCheckContext {
                                private int[] found;

                                DirtyCheckContextImpl() {
                                }

                                @Override
                                public void doDirtyChecking(CustomEntityDirtinessStrategy.AttributeChecker attributeChecker) {
                                    this.found = new DirtyCheckAttributeInfoImpl(event).visitAttributes(attributeChecker);
                                    if (this.found != null && this.found.length == 0) {
                                        this.found = null;
                                    }
                                }
                            }
                            DirtyCheckContextImpl context = new DirtyCheckContextImpl();
                            session.getFactory().getCustomEntityDirtinessStrategy().findDirty(entity, persister, session, context);
                            dirtyProperties = context.found;
                        }
                    }
                    event.setDatabaseSnapshot(null);
                    dirtyCheckPossible = true;
                    if (dirtyProperties != null) break block9;
                    try {
                        session.getEventListenerManager().dirtyCalculationStart();
                        interceptorHandledDirtyCheck = false;
                        boolean bl = dirtyCheckPossible = loadedState != null;
                        if (dirtyCheckPossible) {
                            dirtyProperties = persister.findDirty(values, loadedState, entity, session);
                            break block8;
                        }
                        if (entry.getStatus() == Status.DELETED && !event.getEntityEntry().isModifiableEntity()) {
                            if (values != entry.getDeletedState()) {
                                throw new IllegalStateException("Entity has status Status.DELETED but values != entry.getDeletedState");
                            }
                            Object[] currentState = persister.getPropertyValues(event.getEntity());
                            dirtyProperties = persister.findDirty(entry.getDeletedState(), currentState, entity, session);
                            dirtyCheckPossible = true;
                            break block8;
                        }
                        Object[] databaseSnapshot = this.getDatabaseSnapshot(session, persister, id);
                        if (databaseSnapshot == null) break block8;
                        dirtyProperties = persister.findModified(databaseSnapshot, values, entity, session);
                        dirtyCheckPossible = true;
                        event.setDatabaseSnapshot(databaseSnapshot);
                    }
                    catch (Throwable throwable) {
                        session.getEventListenerManager().dirtyCalculationEnd(dirtyProperties != null);
                        throw throwable;
                    }
                }
                session.getEventListenerManager().dirtyCalculationEnd(dirtyProperties != null);
                break block10;
            }
            interceptorHandledDirtyCheck = true;
        }
        this.logDirtyProperties(id, dirtyProperties, persister);
        event.setDirtyProperties(dirtyProperties);
        event.setDirtyCheckHandledByInterceptor(interceptorHandledDirtyCheck);
        event.setDirtyCheckPossible(dirtyCheckPossible);
    }

    private void logDirtyProperties(Serializable id, int[] dirtyProperties, EntityPersister persister) {
        if (dirtyProperties != null && dirtyProperties.length > 0 && LOG.isTraceEnabled()) {
            String[] allPropertyNames = persister.getPropertyNames();
            Object[] dirtyPropertyNames = new String[dirtyProperties.length];
            for (int i = 0; i < dirtyProperties.length; ++i) {
                dirtyPropertyNames[i] = allPropertyNames[dirtyProperties[i]];
            }
            LOG.tracev("Found dirty properties [{0}] : {1}", MessageHelper.infoString(persister.getEntityName(), id), Arrays.toString(dirtyPropertyNames));
        }
    }

    private Object[] getDatabaseSnapshot(SessionImplementor session, EntityPersister persister, Serializable id) {
        PersistenceContext persistenceContext = session.getPersistenceContextInternal();
        if (persister.isSelectBeforeUpdateRequired()) {
            Object[] snapshot = persistenceContext.getDatabaseSnapshot(id, persister);
            if (snapshot == null) {
                StatisticsImplementor statistics = session.getFactory().getStatistics();
                if (statistics.isStatisticsEnabled()) {
                    statistics.optimisticFailure(persister.getEntityName());
                }
                throw new StaleObjectStateException(persister.getEntityName(), id);
            }
            return snapshot;
        }
        EntityKey entityKey = session.generateEntityKey(id, persister);
        return persistenceContext.getCachedDatabaseSnapshot(entityKey);
    }

    private class DirtyCheckAttributeInfoImpl
    implements CustomEntityDirtinessStrategy.AttributeInformation {
        private final FlushEntityEvent event;
        private final EntityPersister persister;
        private final int numberOfAttributes;
        private int index;
        Object[] databaseSnapshot;

        private DirtyCheckAttributeInfoImpl(FlushEntityEvent event) {
            this.event = event;
            this.persister = event.getEntityEntry().getPersister();
            this.numberOfAttributes = this.persister.getPropertyNames().length;
        }

        @Override
        public EntityPersister getContainingPersister() {
            return this.persister;
        }

        @Override
        public int getAttributeIndex() {
            return this.index;
        }

        @Override
        public String getName() {
            return this.persister.getPropertyNames()[this.index];
        }

        @Override
        public Type getType() {
            return this.persister.getPropertyTypes()[this.index];
        }

        @Override
        public Object getCurrentValue() {
            return this.event.getPropertyValues()[this.index];
        }

        @Override
        public Object getLoadedValue() {
            if (this.databaseSnapshot == null) {
                this.databaseSnapshot = DefaultFlushEntityEventListener.this.getDatabaseSnapshot(this.event.getSession(), this.persister, this.event.getEntityEntry().getId());
            }
            return this.databaseSnapshot[this.index];
        }

        public int[] visitAttributes(CustomEntityDirtinessStrategy.AttributeChecker attributeChecker) {
            this.databaseSnapshot = null;
            this.index = 0;
            int[] indexes = new int[this.numberOfAttributes];
            int count = 0;
            while (this.index < this.numberOfAttributes) {
                if (attributeChecker.isDirty(this)) {
                    indexes[count++] = this.index;
                }
                ++this.index;
            }
            return Arrays.copyOf(indexes, count);
        }
    }
}

