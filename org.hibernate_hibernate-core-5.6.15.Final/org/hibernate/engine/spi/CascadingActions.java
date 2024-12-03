/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.engine.spi;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.ReplicationMode;
import org.hibernate.TransientPropertyValueException;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.internal.ForeignKeys;
import org.hibernate.engine.spi.CascadingAction;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.engine.spi.Status;
import org.hibernate.event.spi.EventSource;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.type.CollectionType;
import org.hibernate.type.EntityType;
import org.hibernate.type.Type;
import org.jboss.logging.Logger;

public class CascadingActions {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)CascadingAction.class.getName());
    public static final CascadingAction DELETE = new BaseCascadingAction(){

        @Override
        public void cascade(EventSource session, Object child, String entityName, Object anything, boolean isCascadeDeleteEnabled) {
            LOG.tracev("Cascading to delete: {0}", entityName);
            session.delete(entityName, child, isCascadeDeleteEnabled, (Set)anything);
        }

        @Override
        public Iterator getCascadableChildrenIterator(EventSource session, CollectionType collectionType, Object collection) {
            return CascadingActions.getAllElementsIterator(session, collectionType, collection);
        }

        @Override
        public boolean deleteOrphans() {
            return true;
        }

        public String toString() {
            return "ACTION_DELETE";
        }
    };
    public static final CascadingAction LOCK = new BaseCascadingAction(){

        @Override
        public void cascade(EventSource session, Object child, String entityName, Object anything, boolean isCascadeDeleteEnabled) {
            LOG.tracev("Cascading to lock: {0}", entityName);
            LockMode lockMode = LockMode.NONE;
            LockOptions lr = new LockOptions();
            if (anything instanceof LockOptions) {
                LockOptions lockOptions = (LockOptions)anything;
                lr.setTimeOut(lockOptions.getTimeOut());
                lr.setScope(lockOptions.getScope());
                lr.setFollowOnLocking(lockOptions.getFollowOnLocking());
                if (lockOptions.getScope()) {
                    lockMode = lockOptions.getLockMode();
                }
            }
            lr.setLockMode(lockMode);
            session.buildLockRequest(lr).lock(entityName, child);
        }

        @Override
        public Iterator getCascadableChildrenIterator(EventSource session, CollectionType collectionType, Object collection) {
            return CascadingActions.getLoadedElementsIterator(session, collectionType, collection);
        }

        @Override
        public boolean deleteOrphans() {
            return false;
        }

        public String toString() {
            return "ACTION_LOCK";
        }
    };
    public static final CascadingAction REFRESH = new BaseCascadingAction(){

        @Override
        public void cascade(EventSource session, Object child, String entityName, Object anything, boolean isCascadeDeleteEnabled) throws HibernateException {
            LOG.tracev("Cascading to refresh: {0}", entityName);
            session.refresh(entityName, child, (Map)anything);
        }

        @Override
        public Iterator getCascadableChildrenIterator(EventSource session, CollectionType collectionType, Object collection) {
            return CascadingActions.getLoadedElementsIterator(session, collectionType, collection);
        }

        @Override
        public boolean deleteOrphans() {
            return false;
        }

        public String toString() {
            return "ACTION_REFRESH";
        }
    };
    public static final CascadingAction EVICT = new BaseCascadingAction(){

        @Override
        public void cascade(EventSource session, Object child, String entityName, Object anything, boolean isCascadeDeleteEnabled) throws HibernateException {
            LOG.tracev("Cascading to evict: {0}", entityName);
            session.evict(child);
        }

        @Override
        public Iterator getCascadableChildrenIterator(EventSource session, CollectionType collectionType, Object collection) {
            return CascadingActions.getLoadedElementsIterator(session, collectionType, collection);
        }

        @Override
        public boolean deleteOrphans() {
            return false;
        }

        @Override
        public boolean performOnLazyProperty() {
            return false;
        }

        public String toString() {
            return "ACTION_EVICT";
        }
    };
    public static final CascadingAction SAVE_UPDATE = new BaseCascadingAction(){

        @Override
        public void cascade(EventSource session, Object child, String entityName, Object anything, boolean isCascadeDeleteEnabled) throws HibernateException {
            LOG.tracev("Cascading to save or update: {0}", entityName);
            session.saveOrUpdate(entityName, child);
        }

        @Override
        public Iterator getCascadableChildrenIterator(EventSource session, CollectionType collectionType, Object collection) {
            return CascadingActions.getLoadedElementsIterator(session, collectionType, collection);
        }

        @Override
        public boolean deleteOrphans() {
            return true;
        }

        @Override
        public boolean performOnLazyProperty() {
            return false;
        }

        public String toString() {
            return "ACTION_SAVE_UPDATE";
        }
    };
    public static final CascadingAction MERGE = new BaseCascadingAction(){

        @Override
        public void cascade(EventSource session, Object child, String entityName, Object anything, boolean isCascadeDeleteEnabled) throws HibernateException {
            LOG.tracev("Cascading to merge: {0}", entityName);
            session.merge(entityName, child, (Map)anything);
        }

        @Override
        public Iterator getCascadableChildrenIterator(EventSource session, CollectionType collectionType, Object collection) {
            return CascadingActions.getLoadedElementsIterator(session, collectionType, collection);
        }

        @Override
        public boolean deleteOrphans() {
            return false;
        }

        public String toString() {
            return "ACTION_MERGE";
        }
    };
    public static final CascadingAction PERSIST = new BaseCascadingAction(){

        @Override
        public void cascade(EventSource session, Object child, String entityName, Object anything, boolean isCascadeDeleteEnabled) throws HibernateException {
            LOG.tracev("Cascading to persist: {0}", entityName);
            session.persist(entityName, child, (Map)anything);
        }

        @Override
        public Iterator getCascadableChildrenIterator(EventSource session, CollectionType collectionType, Object collection) {
            return CascadingActions.getLoadedElementsIterator(session, collectionType, collection);
        }

        @Override
        public boolean deleteOrphans() {
            return false;
        }

        @Override
        public boolean performOnLazyProperty() {
            return false;
        }

        public String toString() {
            return "ACTION_PERSIST";
        }
    };
    public static final CascadingAction PERSIST_ON_FLUSH = new BaseCascadingAction(){

        @Override
        public void cascade(EventSource session, Object child, String entityName, Object anything, boolean isCascadeDeleteEnabled) throws HibernateException {
            LOG.tracev("Cascading to persist on flush: {0}", entityName);
            session.persistOnFlush(entityName, child, (Map)anything);
        }

        @Override
        public Iterator getCascadableChildrenIterator(EventSource session, CollectionType collectionType, Object collection) {
            return CascadingActions.getLoadedElementsIterator(session, collectionType, collection);
        }

        @Override
        public boolean deleteOrphans() {
            return true;
        }

        @Override
        public boolean requiresNoCascadeChecking() {
            return true;
        }

        @Override
        public void noCascade(EventSource session, Object parent, EntityPersister persister, Type propertyType, int propertyIndex) {
            String childEntityName;
            Object child;
            if (propertyType.isEntityType() && (child = persister.getPropertyValue(parent, propertyIndex)) != null && !this.isInManagedState(child, session) && !(child instanceof HibernateProxy) && ForeignKeys.isTransient(childEntityName = ((EntityType)propertyType).getAssociatedEntityName(session.getFactory()), child, null, session)) {
                String parentEntityName = persister.getEntityName();
                String propertyName = persister.getPropertyNames()[propertyIndex];
                throw new TransientPropertyValueException("object references an unsaved transient instance - save the transient instance before flushing", childEntityName, parentEntityName, propertyName);
            }
        }

        @Override
        public boolean performOnLazyProperty() {
            return false;
        }

        private boolean isInManagedState(Object child, EventSource session) {
            EntityEntry entry = session.getPersistenceContextInternal().getEntry(child);
            return entry != null && (entry.getStatus() == Status.MANAGED || entry.getStatus() == Status.READ_ONLY || entry.getStatus() == Status.SAVING);
        }

        public String toString() {
            return "ACTION_PERSIST_ON_FLUSH";
        }
    };
    public static final CascadingAction REPLICATE = new BaseCascadingAction(){

        @Override
        public void cascade(EventSource session, Object child, String entityName, Object anything, boolean isCascadeDeleteEnabled) throws HibernateException {
            LOG.tracev("Cascading to replicate: {0}", entityName);
            session.replicate(entityName, child, (ReplicationMode)((Object)anything));
        }

        @Override
        public Iterator getCascadableChildrenIterator(EventSource session, CollectionType collectionType, Object collection) {
            return CascadingActions.getLoadedElementsIterator(session, collectionType, collection);
        }

        @Override
        public boolean deleteOrphans() {
            return false;
        }

        public String toString() {
            return "ACTION_REPLICATE";
        }
    };

    private CascadingActions() {
    }

    public static Iterator getAllElementsIterator(EventSource session, CollectionType collectionType, Object collection) {
        return collectionType.getElementsIterator(collection, session);
    }

    public static Iterator getLoadedElementsIterator(SharedSessionContractImplementor session, CollectionType collectionType, Object collection) {
        if (CascadingActions.collectionIsInitialized(collection)) {
            return collectionType.getElementsIterator(collection, session);
        }
        return ((PersistentCollection)collection).queuedAdditionIterator();
    }

    private static boolean collectionIsInitialized(Object collection) {
        return !(collection instanceof PersistentCollection) || ((PersistentCollection)collection).wasInitialized();
    }

    public static abstract class BaseCascadingAction
    implements CascadingAction {
        @Override
        public boolean requiresNoCascadeChecking() {
            return false;
        }

        @Override
        public void noCascade(EventSource session, Object parent, EntityPersister persister, Type propertyType, int propertyIndex) {
        }

        @Override
        public boolean performOnLazyProperty() {
            return true;
        }
    }
}

