/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.internal;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.hibernate.AssertionFailure;
import org.hibernate.CustomEntityDirtinessStrategy;
import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.bytecode.enhance.spi.interceptor.EnhancementAsProxyLazinessInterceptor;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.internal.EntityEntryExtraStateHolder;
import org.hibernate.engine.internal.ManagedTypeHelper;
import org.hibernate.engine.spi.CachedNaturalIdValueSource;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.EntityEntryExtraState;
import org.hibernate.engine.spi.EntityKey;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.PersistentAttributeInterceptable;
import org.hibernate.engine.spi.PersistentAttributeInterceptor;
import org.hibernate.engine.spi.SelfDirtinessTracker;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.engine.spi.Status;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.UniqueKeyLoadable;
import org.hibernate.pretty.MessageHelper;
import org.hibernate.proxy.HibernateProxy;

public abstract class AbstractEntityEntry
implements Serializable,
EntityEntry {
    protected final Serializable id;
    protected Object[] loadedState;
    protected Object version;
    protected final EntityPersister persister;
    protected transient EntityKey cachedEntityKey;
    protected final transient Object rowId;
    protected final transient PersistenceContext persistenceContext;
    protected EntityEntryExtraState next;
    private transient int compressedState;
    private static final Object[] DEFAULT_DELETED_STATE = null;

    @Deprecated
    public AbstractEntityEntry(Status status, Object[] loadedState, Object rowId, Serializable id, Object version, LockMode lockMode, boolean existsInDatabase, EntityPersister persister, EntityMode entityMode, String tenantId, boolean disableVersionIncrement, PersistenceContext persistenceContext) {
        this(status, loadedState, rowId, id, version, lockMode, existsInDatabase, persister, disableVersionIncrement, persistenceContext);
    }

    public AbstractEntityEntry(Status status, Object[] loadedState, Object rowId, Serializable id, Object version, LockMode lockMode, boolean existsInDatabase, EntityPersister persister, boolean disableVersionIncrement, PersistenceContext persistenceContext) {
        this.setCompressedValue(EnumState.STATUS, status);
        this.setCompressedValue(EnumState.PREVIOUS_STATUS, null);
        if (status != Status.READ_ONLY) {
            this.loadedState = loadedState;
        }
        this.id = id;
        this.rowId = rowId;
        this.setCompressedValue(BooleanState.EXISTS_IN_DATABASE, existsInDatabase);
        this.version = version;
        this.setCompressedValue(EnumState.LOCK_MODE, lockMode);
        this.setCompressedValue(BooleanState.IS_BEING_REPLICATED, disableVersionIncrement);
        this.persister = persister;
        this.persistenceContext = persistenceContext;
    }

    protected AbstractEntityEntry(SessionFactoryImplementor factory, String entityName, Serializable id, Status status, Status previousStatus, Object[] loadedState, Object[] deletedState, Object version, LockMode lockMode, boolean existsInDatabase, boolean isBeingReplicated, PersistenceContext persistenceContext) {
        this.persister = factory == null ? null : factory.getEntityPersister(entityName);
        this.id = id;
        this.setCompressedValue(EnumState.STATUS, status);
        this.setCompressedValue(EnumState.PREVIOUS_STATUS, previousStatus);
        this.loadedState = loadedState;
        this.setDeletedState(deletedState);
        this.version = version;
        this.setCompressedValue(EnumState.LOCK_MODE, lockMode);
        this.setCompressedValue(BooleanState.EXISTS_IN_DATABASE, existsInDatabase);
        this.setCompressedValue(BooleanState.IS_BEING_REPLICATED, isBeingReplicated);
        this.rowId = null;
        this.persistenceContext = persistenceContext;
    }

    @Override
    public LockMode getLockMode() {
        return this.getCompressedValue(EnumState.LOCK_MODE);
    }

    @Override
    public void setLockMode(LockMode lockMode) {
        this.setCompressedValue(EnumState.LOCK_MODE, lockMode);
    }

    @Override
    public Status getStatus() {
        return this.getCompressedValue(EnumState.STATUS);
    }

    private Status getPreviousStatus() {
        return this.getCompressedValue(EnumState.PREVIOUS_STATUS);
    }

    @Override
    public void setStatus(Status status) {
        Status currentStatus;
        if (status == Status.READ_ONLY) {
            this.loadedState = null;
        }
        if ((currentStatus = this.getStatus()) != status) {
            this.setCompressedValue(EnumState.PREVIOUS_STATUS, currentStatus);
            this.setCompressedValue(EnumState.STATUS, status);
        }
    }

    @Override
    public Serializable getId() {
        return this.id;
    }

    @Override
    public Object[] getLoadedState() {
        return this.loadedState;
    }

    @Override
    public Object[] getDeletedState() {
        EntityEntryExtraStateHolder extra = this.getExtraState(EntityEntryExtraStateHolder.class);
        return extra != null ? extra.getDeletedState() : DEFAULT_DELETED_STATE;
    }

    @Override
    public void setDeletedState(Object[] deletedState) {
        EntityEntryExtraStateHolder extra = this.getExtraState(EntityEntryExtraStateHolder.class);
        if (extra == null && deletedState == DEFAULT_DELETED_STATE) {
            return;
        }
        if (extra == null) {
            extra = new EntityEntryExtraStateHolder();
            this.addExtraState(extra);
        }
        extra.setDeletedState(deletedState);
    }

    @Override
    public boolean isExistsInDatabase() {
        return this.getCompressedValue(BooleanState.EXISTS_IN_DATABASE);
    }

    @Override
    public Object getVersion() {
        return this.version;
    }

    @Override
    public EntityPersister getPersister() {
        return this.persister;
    }

    @Override
    public EntityKey getEntityKey() {
        if (this.cachedEntityKey == null) {
            if (this.getId() == null) {
                throw new IllegalStateException("cannot generate an EntityKey when id is null.");
            }
            this.cachedEntityKey = new EntityKey(this.getId(), this.getPersister());
        }
        return this.cachedEntityKey;
    }

    @Override
    public String getEntityName() {
        return this.persister == null ? null : this.persister.getEntityName();
    }

    @Override
    public boolean isBeingReplicated() {
        return this.getCompressedValue(BooleanState.IS_BEING_REPLICATED);
    }

    @Override
    public Object getRowId() {
        return this.rowId;
    }

    @Override
    public void postUpdate(Object entity, Object[] updatedState, Object nextVersion) {
        this.loadedState = updatedState;
        this.setLockMode(LockMode.WRITE);
        if (this.getPersister().isVersioned()) {
            this.version = nextVersion;
            this.getPersister().setPropertyValue(entity, this.getPersister().getVersionProperty(), nextVersion);
        }
        ManagedTypeHelper.processIfSelfDirtinessTracker(entity, AbstractEntityEntry::clearDirtyAttributes);
        this.getPersistenceContext().getSession().getFactory().getCustomEntityDirtinessStrategy().resetDirty(entity, this.getPersister(), (Session)((Object)this.getPersistenceContext().getSession()));
    }

    private static void clearDirtyAttributes(SelfDirtinessTracker entity) {
        entity.$$_hibernate_clearDirtyAttributes();
    }

    @Override
    public void postDelete() {
        this.setCompressedValue(EnumState.PREVIOUS_STATUS, this.getStatus());
        this.setCompressedValue(EnumState.STATUS, Status.GONE);
        this.setCompressedValue(BooleanState.EXISTS_IN_DATABASE, false);
    }

    @Override
    public void postInsert(Object[] insertedState) {
        this.setCompressedValue(BooleanState.EXISTS_IN_DATABASE, true);
    }

    @Override
    public boolean isNullifiable(boolean earlyInsert, SharedSessionContractImplementor session) {
        if (this.getStatus() == Status.SAVING) {
            return true;
        }
        if (earlyInsert) {
            return !this.isExistsInDatabase();
        }
        return session.getPersistenceContextInternal().containsNullifiableEntityKey(this::getEntityKey);
    }

    @Override
    public Object getLoadedValue(String propertyName) {
        if (this.loadedState == null || propertyName == null) {
            return null;
        }
        int propertyIndex = ((UniqueKeyLoadable)this.persister).getPropertyIndex(propertyName);
        return this.loadedState[propertyIndex];
    }

    @Override
    public void overwriteLoadedStateCollectionValue(String propertyName, PersistentCollection collection) {
        if (this.getStatus() != Status.READ_ONLY) {
            assert (propertyName != null);
            assert (this.loadedState != null);
            int propertyIndex = ((UniqueKeyLoadable)this.persister).getPropertyIndex(propertyName);
            this.loadedState[propertyIndex] = collection;
        }
    }

    @Override
    public boolean requiresDirtyCheck(Object entity) {
        return this.isModifiableEntity() && !this.isUnequivocallyNonDirty(entity);
    }

    private boolean isUnequivocallyNonDirty(Object entity) {
        PersistentAttributeInterceptable interceptable;
        PersistentAttributeInterceptor interceptor;
        if (ManagedTypeHelper.isSelfDirtinessTracker(entity)) {
            boolean uninitializedProxy = false;
            if (ManagedTypeHelper.isPersistentAttributeInterceptable(entity)) {
                PersistentAttributeInterceptable interceptable2 = ManagedTypeHelper.asPersistentAttributeInterceptable(entity);
                PersistentAttributeInterceptor interceptor2 = interceptable2.$$_hibernate_getInterceptor();
                if (interceptor2 instanceof EnhancementAsProxyLazinessInterceptor) {
                    EnhancementAsProxyLazinessInterceptor enhancementAsProxyLazinessInterceptor = (EnhancementAsProxyLazinessInterceptor)interceptor2;
                    return !enhancementAsProxyLazinessInterceptor.hasWrittenFieldNames();
                }
            } else if (entity instanceof HibernateProxy) {
                uninitializedProxy = ((HibernateProxy)entity).getHibernateLazyInitializer().isUninitialized();
            }
            return uninitializedProxy || !this.persister.hasCollections() && !this.persister.hasMutableProperties() && !ManagedTypeHelper.asSelfDirtinessTracker(entity).$$_hibernate_hasDirtyAttributes();
        }
        if (ManagedTypeHelper.isPersistentAttributeInterceptable(entity) && (interceptor = (interceptable = ManagedTypeHelper.asPersistentAttributeInterceptable(entity)).$$_hibernate_getInterceptor()) instanceof EnhancementAsProxyLazinessInterceptor) {
            return true;
        }
        CustomEntityDirtinessStrategy customEntityDirtinessStrategy = this.getPersistenceContext().getSession().getFactory().getCustomEntityDirtinessStrategy();
        if (customEntityDirtinessStrategy.canDirtyCheck(entity, this.getPersister(), (Session)((Object)this.getPersistenceContext().getSession()))) {
            return !customEntityDirtinessStrategy.isDirty(entity, this.getPersister(), (Session)((Object)this.getPersistenceContext().getSession()));
        }
        if (this.getPersister().hasMutableProperties()) {
            return false;
        }
        return false;
    }

    @Override
    public boolean isModifiableEntity() {
        Status status = this.getStatus();
        Status previousStatus = this.getPreviousStatus();
        return this.getPersister().isMutable() && status != Status.READ_ONLY && (status != Status.DELETED || previousStatus != Status.READ_ONLY);
    }

    @Override
    public void forceLocked(Object entity, Object nextVersion) {
        this.loadedState[this.persister.getVersionProperty()] = this.version = nextVersion;
        this.setLockMode(LockMode.FORCE);
        this.persister.setPropertyValue(entity, this.getPersister().getVersionProperty(), nextVersion);
    }

    @Override
    public boolean isReadOnly() {
        Status status = this.getStatus();
        if (status != Status.MANAGED && status != Status.READ_ONLY) {
            throw new HibernateException("instance was not in a valid state");
        }
        return status == Status.READ_ONLY;
    }

    @Override
    public void setReadOnly(boolean readOnly, Object entity) {
        if (readOnly == this.isReadOnly()) {
            return;
        }
        if (readOnly) {
            this.setStatus(Status.READ_ONLY);
            this.loadedState = null;
        } else {
            if (!this.persister.isMutable()) {
                throw new IllegalStateException("Cannot make an immutable entity modifiable.");
            }
            this.setStatus(Status.MANAGED);
            this.loadedState = this.getPersister().getPropertyValues(entity);
            this.getPersistenceContext().getNaturalIdHelper().manageLocalNaturalIdCrossReference(this.persister, this.id, this.loadedState, null, CachedNaturalIdValueSource.LOAD);
        }
    }

    @Override
    public String toString() {
        return "EntityEntry" + MessageHelper.infoString(this.getPersister().getEntityName(), this.id) + '(' + (Object)((Object)this.getStatus()) + ')';
    }

    @Override
    public void serialize(ObjectOutputStream oos) throws IOException {
        Status previousStatus = this.getPreviousStatus();
        oos.writeObject(this.getEntityName());
        oos.writeObject(this.id);
        oos.writeObject(this.getStatus().name());
        oos.writeObject(previousStatus == null ? "" : previousStatus.name());
        oos.writeObject(this.loadedState);
        oos.writeObject(this.getDeletedState());
        oos.writeObject(this.version);
        oos.writeObject(this.getLockMode().toString());
        oos.writeBoolean(this.isExistsInDatabase());
        oos.writeBoolean(this.isBeingReplicated());
    }

    @Override
    public void addExtraState(EntityEntryExtraState extraState) {
        if (this.next == null) {
            this.next = extraState;
        } else {
            this.next.addExtraState(extraState);
        }
    }

    @Override
    public <T extends EntityEntryExtraState> T getExtraState(Class<T> extraStateType) {
        if (this.next == null) {
            return null;
        }
        if (extraStateType.isAssignableFrom(this.next.getClass())) {
            return (T)this.next;
        }
        return this.next.getExtraState(extraStateType);
    }

    public PersistenceContext getPersistenceContext() {
        return this.persistenceContext;
    }

    protected <E extends Enum<E>> void setCompressedValue(EnumState<E> state, E value) {
        this.compressedState &= ((EnumState)state).getUnsetMask();
        this.compressedState |= ((EnumState)state).getValue(value) << ((EnumState)state).getOffset();
    }

    protected <E extends Enum<E>> E getCompressedValue(EnumState<E> state) {
        int index = ((this.compressedState & ((EnumState)state).getMask()) >> ((EnumState)state).getOffset()) - 1;
        return (E)(index == -1 ? null : ((EnumState)state).getEnumConstants()[index]);
    }

    protected void setCompressedValue(BooleanState state, boolean value) {
        this.compressedState &= state.getUnsetMask();
        this.compressedState |= state.getValue(value) << state.getOffset();
    }

    protected boolean getCompressedValue(BooleanState state) {
        return (this.compressedState & state.getMask()) >> state.getOffset() == 1;
    }

    protected static enum BooleanState {
        EXISTS_IN_DATABASE(13),
        IS_BEING_REPLICATED(14);

        private final int offset;
        private final int mask;
        private final int unsetMask;

        private BooleanState(int offset) {
            this.offset = offset;
            this.mask = 1 << offset;
            this.unsetMask = 0xFFFF & ~this.mask;
        }

        private int getValue(boolean value) {
            return value ? 1 : 0;
        }

        private int getOffset() {
            return this.offset;
        }

        private int getMask() {
            return this.mask;
        }

        private int getUnsetMask() {
            return this.unsetMask;
        }
    }

    protected static class EnumState<E extends Enum<E>> {
        protected static final EnumState<LockMode> LOCK_MODE = new EnumState<LockMode>(0, LockMode.class);
        protected static final EnumState<Status> STATUS = new EnumState<Status>(4, Status.class);
        protected static final EnumState<Status> PREVIOUS_STATUS = new EnumState<Status>(8, Status.class);
        protected final int offset;
        protected final E[] enumConstants;
        protected final int mask;
        protected final int unsetMask;

        private EnumState(int offset, Class<E> enumType) {
            Enum[] enumConstants = (Enum[])enumType.getEnumConstants();
            if (enumConstants.length > 15) {
                throw new AssertionFailure("Cannot store enum type " + enumType.getName() + " in compressed state as it has too many values.");
            }
            this.offset = offset;
            this.enumConstants = enumConstants;
            this.mask = 15 << offset;
            this.unsetMask = 0xFFFF & ~this.mask;
        }

        private int getValue(E value) {
            return value != null ? ((Enum)value).ordinal() + 1 : 0;
        }

        private int getOffset() {
            return this.offset;
        }

        private int getMask() {
            return this.mask;
        }

        private int getUnsetMask() {
            return this.unsetMask;
        }

        private E[] getEnumConstants() {
            return this.enumConstants;
        }
    }
}

