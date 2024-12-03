/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.internal;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;
import java.util.Map;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.engine.internal.AbstractEntityEntry;
import org.hibernate.engine.internal.ImmutableEntityEntry;
import org.hibernate.engine.internal.ManagedTypeHelper;
import org.hibernate.engine.internal.StatefulPersistenceContext;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.ManagedEntity;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;

public class EntityEntryContext {
    private static final CoreMessageLogger log = CoreLogging.messageLogger(EntityEntryContext.class);
    private transient PersistenceContext persistenceContext;
    private transient IdentityHashMap<ManagedEntity, ImmutableManagedEntityHolder> immutableManagedEntityXref;
    private transient ManagedEntity head;
    private transient ManagedEntity tail;
    private transient int count;
    private transient IdentityHashMap<Object, ManagedEntity> nonEnhancedEntityXref;
    private transient Map.Entry<Object, EntityEntry>[] reentrantSafeEntries = new Map.Entry[0];
    private transient boolean dirty;

    public EntityEntryContext(PersistenceContext persistenceContext) {
        this.persistenceContext = persistenceContext;
    }

    public void addEntityEntry(Object entity, EntityEntry entityEntry) {
        boolean alreadyAssociated;
        this.dirty = true;
        assert (AbstractEntityEntry.class.isInstance(entityEntry));
        if (entityEntry.getPersister().isMutable()) assert (((AbstractEntityEntry)AbstractEntityEntry.class.cast(entityEntry)).getPersistenceContext() == this.persistenceContext);
        ManagedEntity managedEntity = this.getAssociatedManagedEntity(entity);
        boolean bl = alreadyAssociated = managedEntity != null;
        if (!alreadyAssociated) {
            if (ManagedTypeHelper.isManagedEntity(entity)) {
                ManagedEntity managed = ManagedTypeHelper.asManagedEntity(entity);
                if (entityEntry.getPersister().isMutable()) {
                    managedEntity = managed;
                    this.checkNotAssociatedWithOtherPersistenceContextIfMutable(managedEntity);
                } else {
                    managedEntity = new ImmutableManagedEntityHolder(managed);
                    if (this.immutableManagedEntityXref == null) {
                        this.immutableManagedEntityXref = new IdentityHashMap();
                    }
                    this.immutableManagedEntityXref.put(managed, (ImmutableManagedEntityHolder)managedEntity);
                }
            } else {
                if (this.nonEnhancedEntityXref == null) {
                    this.nonEnhancedEntityXref = new IdentityHashMap();
                }
                managedEntity = new ManagedEntityImpl(entity);
                this.nonEnhancedEntityXref.put(entity, managedEntity);
            }
        }
        managedEntity.$$_hibernate_setEntityEntry(entityEntry);
        if (alreadyAssociated) {
            return;
        }
        if (this.tail == null) {
            assert (this.head == null);
            managedEntity.$$_hibernate_setPreviousManagedEntity(null);
            managedEntity.$$_hibernate_setNextManagedEntity(null);
            this.tail = this.head = managedEntity;
            this.count = 1;
        } else {
            this.tail.$$_hibernate_setNextManagedEntity(managedEntity);
            managedEntity.$$_hibernate_setPreviousManagedEntity(this.tail);
            managedEntity.$$_hibernate_setNextManagedEntity(null);
            this.tail = managedEntity;
            ++this.count;
        }
    }

    private ManagedEntity getAssociatedManagedEntity(Object entity) {
        if (ManagedTypeHelper.isManagedEntity(entity)) {
            ManagedEntity managedEntity = ManagedTypeHelper.asManagedEntity(entity);
            if (managedEntity.$$_hibernate_getEntityEntry() == null) {
                return null;
            }
            AbstractEntityEntry entityEntry = (AbstractEntityEntry)managedEntity.$$_hibernate_getEntityEntry();
            if (entityEntry.getPersister().isMutable()) {
                return entityEntry.getPersistenceContext() == this.persistenceContext ? managedEntity : null;
            }
            return this.immutableManagedEntityXref != null ? (ManagedEntity)this.immutableManagedEntityXref.get(managedEntity) : null;
        }
        return this.nonEnhancedEntityXref != null ? this.nonEnhancedEntityXref.get(entity) : null;
    }

    private void checkNotAssociatedWithOtherPersistenceContextIfMutable(ManagedEntity managedEntity) {
        AbstractEntityEntry entityEntry = (AbstractEntityEntry)managedEntity.$$_hibernate_getEntityEntry();
        if (entityEntry == null || !entityEntry.getPersister().isMutable() || entityEntry.getPersistenceContext() == null || entityEntry.getPersistenceContext() == this.persistenceContext) {
            return;
        }
        if (entityEntry.getPersistenceContext().getSession().isOpen()) {
            throw new HibernateException("Illegal attempt to associate a ManagedEntity with two open persistence contexts. " + entityEntry);
        }
        log.stalePersistenceContextInEntityEntry(entityEntry.toString());
    }

    public boolean hasEntityEntry(Object entity) {
        return this.getEntityEntry(entity) != null;
    }

    public EntityEntry getEntityEntry(Object entity) {
        ManagedEntity managedEntity = this.getAssociatedManagedEntity(entity);
        return managedEntity == null ? null : managedEntity.$$_hibernate_getEntityEntry();
    }

    public EntityEntry removeEntityEntry(Object entity) {
        ManagedEntity managedEntity = this.getAssociatedManagedEntity(entity);
        if (managedEntity == null) {
            return null;
        }
        this.dirty = true;
        if (ImmutableManagedEntityHolder.class.isInstance(managedEntity)) {
            assert (entity == ((ImmutableManagedEntityHolder)managedEntity).managedEntity);
            this.immutableManagedEntityXref.remove((ManagedEntity)entity);
        } else if (!ManagedEntity.class.isInstance(entity)) {
            this.nonEnhancedEntityXref.remove(entity);
        }
        ManagedEntity previous = managedEntity.$$_hibernate_getPreviousManagedEntity();
        ManagedEntity next = managedEntity.$$_hibernate_getNextManagedEntity();
        managedEntity.$$_hibernate_setPreviousManagedEntity(null);
        managedEntity.$$_hibernate_setNextManagedEntity(null);
        --this.count;
        if (this.count == 0) {
            this.head = null;
            this.tail = null;
            assert (previous == null);
            assert (next == null);
        } else {
            if (previous == null) {
                assert (managedEntity == this.head);
                this.head = next;
            } else {
                previous.$$_hibernate_setNextManagedEntity(next);
            }
            if (next == null) {
                assert (managedEntity == this.tail);
                this.tail = previous;
            } else {
                next.$$_hibernate_setPreviousManagedEntity(previous);
            }
        }
        EntityEntry theEntityEntry = managedEntity.$$_hibernate_getEntityEntry();
        managedEntity.$$_hibernate_setEntityEntry(null);
        return theEntityEntry;
    }

    public Map.Entry<Object, EntityEntry>[] reentrantSafeEntityEntries() {
        if (this.dirty) {
            this.reentrantSafeEntries = new EntityEntryCrossRefImpl[this.count];
            int i = 0;
            for (ManagedEntity managedEntity = this.head; managedEntity != null; managedEntity = managedEntity.$$_hibernate_getNextManagedEntity()) {
                this.reentrantSafeEntries[i++] = new EntityEntryCrossRefImpl(managedEntity.$$_hibernate_getEntityInstance(), managedEntity.$$_hibernate_getEntityEntry());
            }
            this.dirty = false;
        }
        return this.reentrantSafeEntries;
    }

    public void clear() {
        this.dirty = true;
        ManagedEntity node = this.head;
        while (node != null) {
            ManagedEntity nextNode = node.$$_hibernate_getNextManagedEntity();
            node.$$_hibernate_setEntityEntry(null);
            node.$$_hibernate_setPreviousManagedEntity(null);
            node.$$_hibernate_setNextManagedEntity(null);
            node = nextNode;
        }
        if (this.immutableManagedEntityXref != null) {
            this.immutableManagedEntityXref.clear();
        }
        if (this.nonEnhancedEntityXref != null) {
            this.nonEnhancedEntityXref.clear();
        }
        this.head = null;
        this.tail = null;
        this.count = 0;
        this.reentrantSafeEntries = null;
    }

    public void downgradeLocks() {
        if (this.head == null) {
            return;
        }
        for (ManagedEntity node = this.head; node != null; node = node.$$_hibernate_getNextManagedEntity()) {
            node.$$_hibernate_getEntityEntry().setLockMode(LockMode.NONE);
        }
    }

    public void serialize(ObjectOutputStream oos) throws IOException {
        log.tracef("Starting serialization of [%s] EntityEntry entries", this.count);
        oos.writeInt(this.count);
        if (this.count == 0) {
            return;
        }
        for (ManagedEntity managedEntity = this.head; managedEntity != null; managedEntity = managedEntity.$$_hibernate_getNextManagedEntity()) {
            oos.writeBoolean(managedEntity == managedEntity.$$_hibernate_getEntityInstance());
            oos.writeObject(managedEntity.$$_hibernate_getEntityInstance());
            oos.writeInt(managedEntity.$$_hibernate_getEntityEntry().getClass().getName().length());
            oos.writeChars(managedEntity.$$_hibernate_getEntityEntry().getClass().getName());
            managedEntity.$$_hibernate_getEntityEntry().serialize(oos);
        }
    }

    public static EntityEntryContext deserialize(ObjectInputStream ois, StatefulPersistenceContext rtn) throws IOException, ClassNotFoundException {
        int count = ois.readInt();
        log.tracef("Starting deserialization of [%s] EntityEntry entries", count);
        EntityEntryContext context = new EntityEntryContext(rtn);
        context.count = count;
        context.dirty = true;
        if (count == 0) {
            return context;
        }
        ManagedEntity previous = null;
        for (int i = 0; i < count; ++i) {
            ManagedEntity managedEntity;
            boolean isEnhanced = ois.readBoolean();
            Object entity = ois.readObject();
            int numChars = ois.readInt();
            char[] entityEntryClassNameArr = new char[numChars];
            for (int j = 0; j < numChars; ++j) {
                entityEntryClassNameArr[j] = ois.readChar();
            }
            EntityEntry entry = EntityEntryContext.deserializeEntityEntry(entityEntryClassNameArr, ois, rtn);
            if (isEnhanced) {
                if (entry.getPersister().isMutable()) {
                    managedEntity = (ManagedEntity)entity;
                } else {
                    managedEntity = new ImmutableManagedEntityHolder((ManagedEntity)entity);
                    if (context.immutableManagedEntityXref == null) {
                        context.immutableManagedEntityXref = new IdentityHashMap();
                    }
                    context.immutableManagedEntityXref.put((ManagedEntity)entity, (ImmutableManagedEntityHolder)managedEntity);
                }
            } else {
                managedEntity = new ManagedEntityImpl(entity);
                if (context.nonEnhancedEntityXref == null) {
                    context.nonEnhancedEntityXref = new IdentityHashMap();
                }
                context.nonEnhancedEntityXref.put(entity, managedEntity);
            }
            managedEntity.$$_hibernate_setEntityEntry(entry);
            if (previous == null) {
                context.head = managedEntity;
            } else {
                previous.$$_hibernate_setNextManagedEntity(managedEntity);
                managedEntity.$$_hibernate_setPreviousManagedEntity(previous);
            }
            previous = managedEntity;
        }
        context.tail = previous;
        return context;
    }

    private static EntityEntry deserializeEntityEntry(char[] entityEntryClassNameArr, ObjectInputStream ois, StatefulPersistenceContext rtn) {
        EntityEntry entry = null;
        String entityEntryClassName = new String(entityEntryClassNameArr);
        Class entityEntryClass = rtn.getSession().getFactory().getServiceRegistry().getService(ClassLoaderService.class).classForName(entityEntryClassName);
        try {
            Method deserializeMethod = entityEntryClass.getDeclaredMethod("deserialize", ObjectInputStream.class, PersistenceContext.class);
            entry = (EntityEntry)deserializeMethod.invoke(null, ois, rtn);
        }
        catch (NoSuchMethodException e) {
            log.errorf("Enable to deserialize [%s]", entityEntryClassName);
        }
        catch (InvocationTargetException e) {
            log.errorf("Enable to deserialize [%s]", entityEntryClassName);
        }
        catch (IllegalAccessException e) {
            log.errorf("Enable to deserialize [%s]", entityEntryClassName);
        }
        return entry;
    }

    public int getNumberOfManagedEntities() {
        return this.count;
    }

    private static class EntityEntryCrossRefImpl
    implements EntityEntryCrossRef {
        private final Object entity;
        private EntityEntry entityEntry;

        private EntityEntryCrossRefImpl(Object entity, EntityEntry entityEntry) {
            this.entity = entity;
            this.entityEntry = entityEntry;
        }

        @Override
        public Object getEntity() {
            return this.entity;
        }

        @Override
        public EntityEntry getEntityEntry() {
            return this.entityEntry;
        }

        @Override
        public Object getKey() {
            return this.getEntity();
        }

        @Override
        public EntityEntry getValue() {
            return this.getEntityEntry();
        }

        @Override
        public EntityEntry setValue(EntityEntry entityEntry) {
            EntityEntry old = this.entityEntry;
            this.entityEntry = entityEntry;
            return old;
        }
    }

    public static interface EntityEntryCrossRef
    extends Map.Entry<Object, EntityEntry> {
        public Object getEntity();

        public EntityEntry getEntityEntry();
    }

    private static class ImmutableManagedEntityHolder
    implements ManagedEntity {
        private ManagedEntity managedEntity;
        private ManagedEntity previous;
        private ManagedEntity next;

        public ImmutableManagedEntityHolder(ManagedEntity immutableManagedEntity) {
            this.managedEntity = immutableManagedEntity;
        }

        @Override
        public Object $$_hibernate_getEntityInstance() {
            return this.managedEntity.$$_hibernate_getEntityInstance();
        }

        @Override
        public EntityEntry $$_hibernate_getEntityEntry() {
            return this.managedEntity.$$_hibernate_getEntityEntry();
        }

        @Override
        public void $$_hibernate_setEntityEntry(EntityEntry entityEntry) {
            if (entityEntry == null) {
                if (this.canClearEntityEntryReference()) {
                    this.managedEntity.$$_hibernate_setEntityEntry(null);
                }
            } else {
                this.managedEntity.$$_hibernate_setEntityEntry(entityEntry);
            }
        }

        @Override
        public ManagedEntity $$_hibernate_getPreviousManagedEntity() {
            return this.previous;
        }

        @Override
        public void $$_hibernate_setPreviousManagedEntity(ManagedEntity previous) {
            this.previous = previous;
        }

        @Override
        public ManagedEntity $$_hibernate_getNextManagedEntity() {
            return this.next;
        }

        @Override
        public void $$_hibernate_setNextManagedEntity(ManagedEntity next) {
            this.next = next;
        }

        private boolean canClearEntityEntryReference() {
            if (this.managedEntity.$$_hibernate_getEntityEntry() == null) {
                return true;
            }
            if (!(this.managedEntity.$$_hibernate_getEntityEntry() instanceof ImmutableEntityEntry)) {
                return true;
            }
            return !this.managedEntity.$$_hibernate_getEntityEntry().getPersister().canUseReferenceCacheEntries();
        }
    }

    private static class ManagedEntityImpl
    implements ManagedEntity {
        private final Object entityInstance;
        private EntityEntry entityEntry;
        private ManagedEntity previous;
        private ManagedEntity next;

        public ManagedEntityImpl(Object entityInstance) {
            this.entityInstance = entityInstance;
        }

        @Override
        public Object $$_hibernate_getEntityInstance() {
            return this.entityInstance;
        }

        @Override
        public EntityEntry $$_hibernate_getEntityEntry() {
            return this.entityEntry;
        }

        @Override
        public void $$_hibernate_setEntityEntry(EntityEntry entityEntry) {
            this.entityEntry = entityEntry;
        }

        @Override
        public ManagedEntity $$_hibernate_getNextManagedEntity() {
            return this.next;
        }

        @Override
        public void $$_hibernate_setNextManagedEntity(ManagedEntity next) {
            this.next = next;
        }

        @Override
        public ManagedEntity $$_hibernate_getPreviousManagedEntity() {
            return this.previous;
        }

        @Override
        public void $$_hibernate_setPreviousManagedEntity(ManagedEntity previous) {
            this.previous = previous;
        }
    }
}

