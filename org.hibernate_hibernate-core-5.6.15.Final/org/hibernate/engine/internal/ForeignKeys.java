/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.internal;

import java.io.Serializable;
import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.TransientObjectException;
import org.hibernate.bytecode.enhance.spi.LazyPropertyInitializer;
import org.hibernate.engine.internal.NonNullableTransientDependencies;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.SelfDirtinessTracker;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.hibernate.type.CompositeType;
import org.hibernate.type.EntityType;
import org.hibernate.type.Type;

public final class ForeignKeys {
    public static boolean isNotTransient(String entityName, Object entity, Boolean assumed, SharedSessionContractImplementor session) {
        if (entity instanceof HibernateProxy) {
            return true;
        }
        if (session.getPersistenceContextInternal().isEntryFor(entity)) {
            return true;
        }
        return !ForeignKeys.isTransient(entityName, entity, assumed, session);
    }

    public static boolean isTransient(String entityName, Object entity, Boolean assumed, SharedSessionContractImplementor session) {
        if (entity == LazyPropertyInitializer.UNFETCHED_PROPERTY) {
            return false;
        }
        Boolean isUnsaved = session.getInterceptor().isTransient(entity);
        if (isUnsaved != null) {
            return isUnsaved;
        }
        EntityPersister persister = session.getEntityPersister(entityName, entity);
        isUnsaved = persister.isTransient(entity, session);
        if (isUnsaved != null) {
            return isUnsaved;
        }
        if (assumed != null) {
            return assumed;
        }
        Object[] snapshot = session.getPersistenceContextInternal().getDatabaseSnapshot(persister.getIdentifier(entity, session), persister);
        return snapshot == null;
    }

    public static Serializable getEntityIdentifierIfNotUnsaved(String entityName, Object object, SharedSessionContractImplementor session) throws TransientObjectException {
        if (object == null) {
            return null;
        }
        Serializable id = session.getContextEntityIdentifier(object);
        if (id == null) {
            if (ForeignKeys.isTransient(entityName, object, Boolean.FALSE, session)) {
                throw new TransientObjectException("object references an unsaved transient instance - save the transient instance before flushing: " + (entityName == null ? session.guessEntityName(object) : entityName));
            }
            id = session.getEntityPersister(entityName, object).getIdentifier(object, session);
        }
        return id;
    }

    public static NonNullableTransientDependencies findNonNullableTransientEntities(String entityName, Object entity, Object[] values, boolean isEarlyInsert, SharedSessionContractImplementor session) {
        EntityPersister persister = session.getEntityPersister(entityName, entity);
        Nullifier nullifier = new Nullifier(entity, false, isEarlyInsert, session, persister);
        String[] propertyNames = persister.getPropertyNames();
        Type[] types = persister.getPropertyTypes();
        boolean[] nullability = persister.getPropertyNullability();
        NonNullableTransientDependencies nonNullableTransientEntities = new NonNullableTransientDependencies();
        for (int i = 0; i < types.length; ++i) {
            ForeignKeys.collectNonNullableTransientEntities(nullifier, values[i], propertyNames[i], types[i], nullability[i], session, nonNullableTransientEntities);
        }
        return nonNullableTransientEntities.isEmpty() ? null : nonNullableTransientEntities;
    }

    private static void collectNonNullableTransientEntities(Nullifier nullifier, Object value, String propertyName, Type type, boolean isNullable, SharedSessionContractImplementor session, NonNullableTransientDependencies nonNullableTransientEntities) {
        CompositeType actype;
        boolean[] subValueNullability;
        if (value == null) {
            return;
        }
        if (type.isEntityType()) {
            EntityType entityType = (EntityType)type;
            if (!isNullable && !entityType.isOneToOne() && nullifier.isNullifiable(entityType.getAssociatedEntityName(), value)) {
                nonNullableTransientEntities.add(propertyName, value);
            }
        } else if (type.isAnyType()) {
            if (!isNullable && nullifier.isNullifiable(null, value)) {
                nonNullableTransientEntities.add(propertyName, value);
            }
        } else if (type.isComponentType() && (subValueNullability = (actype = (CompositeType)type).getPropertyNullability()) != null) {
            String[] subPropertyNames = actype.getPropertyNames();
            Object[] subvalues = actype.getPropertyValues(value, session);
            Type[] subtypes = actype.getSubtypes();
            for (int j = 0; j < subvalues.length; ++j) {
                ForeignKeys.collectNonNullableTransientEntities(nullifier, subvalues[j], subPropertyNames[j], subtypes[j], subValueNullability[j], session, nonNullableTransientEntities);
            }
        }
    }

    private ForeignKeys() {
    }

    public static class Nullifier {
        private final boolean isDelete;
        private final boolean isEarlyInsert;
        private final SharedSessionContractImplementor session;
        private final Object self;
        private final EntityPersister persister;

        public Nullifier(Object self, boolean isDelete, boolean isEarlyInsert, SharedSessionContractImplementor session, EntityPersister persister) {
            this.isDelete = isDelete;
            this.isEarlyInsert = isEarlyInsert;
            this.session = session;
            this.persister = persister;
            this.self = self;
        }

        public void nullifyTransientReferences(Object[] values) {
            String[] propertyNames = this.persister.getPropertyNames();
            Type[] types = this.persister.getPropertyTypes();
            for (int i = 0; i < types.length; ++i) {
                values[i] = this.nullifyTransientReferences(values[i], propertyNames[i], types[i]);
            }
        }

        private Object nullifyTransientReferences(Object value, String propertyName, Type type) {
            Object returnedValue;
            if (value == null) {
                returnedValue = null;
            } else if (type.isEntityType()) {
                Object possiblyInitializedValue;
                EntityType entityType = (EntityType)type;
                returnedValue = entityType.isOneToOne() ? value : ((possiblyInitializedValue = this.initializeIfNecessary(value, propertyName, entityType)) == null ? null : (this.isNullifiable(entityType.getAssociatedEntityName(), possiblyInitializedValue) ? null : possiblyInitializedValue));
            } else if (type.isAnyType()) {
                returnedValue = this.isNullifiable(null, value) ? null : value;
            } else if (type.isComponentType()) {
                CompositeType actype = (CompositeType)type;
                Object[] subvalues = actype.getPropertyValues(value, this.session);
                Type[] subtypes = actype.getSubtypes();
                String[] subPropertyNames = actype.getPropertyNames();
                boolean substitute = false;
                for (int i = 0; i < subvalues.length; ++i) {
                    Object replacement = this.nullifyTransientReferences(subvalues[i], StringHelper.qualify(propertyName, subPropertyNames[i]), subtypes[i]);
                    if (replacement == subvalues[i]) continue;
                    substitute = true;
                    subvalues[i] = replacement;
                }
                if (substitute) {
                    actype.setPropertyValues(value, subvalues, EntityMode.POJO);
                }
                returnedValue = value;
            } else {
                returnedValue = value;
            }
            if (value != returnedValue && returnedValue == null && SelfDirtinessTracker.class.isInstance(this.self)) {
                ((SelfDirtinessTracker)this.self).$$_hibernate_trackChange(propertyName);
            }
            return returnedValue;
        }

        private Object initializeIfNecessary(Object value, String propertyName, Type type) {
            if (this.isDelete && value == LazyPropertyInitializer.UNFETCHED_PROPERTY && type.isEntityType() && !this.session.getPersistenceContextInternal().isNullifiableEntityKeysEmpty()) {
                return ((LazyPropertyInitializer)((Object)this.persister)).initializeLazyProperty(propertyName, this.self, this.session);
            }
            return value;
        }

        private boolean isNullifiable(String entityName, Object object) throws HibernateException {
            if (object == LazyPropertyInitializer.UNFETCHED_PROPERTY) {
                return false;
            }
            if (object instanceof HibernateProxy) {
                LazyInitializer li = ((HibernateProxy)object).getHibernateLazyInitializer();
                if (li.getImplementation(this.session) == null) {
                    return false;
                }
                object = li.getImplementation(this.session);
            }
            if (object == this.self) {
                return this.isEarlyInsert || this.isDelete && this.session.getFactory().getDialect().hasSelfReferentialForeignKeyBug();
            }
            EntityEntry entityEntry = this.session.getPersistenceContextInternal().getEntry(object);
            if (entityEntry == null) {
                return ForeignKeys.isTransient(entityName, object, null, this.session);
            }
            return entityEntry.isNullifiable(this.isEarlyInsert, this.session);
        }
    }
}

