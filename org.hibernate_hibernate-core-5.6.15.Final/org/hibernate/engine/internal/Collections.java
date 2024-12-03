/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.engine.internal;

import java.io.Serializable;
import org.hibernate.AssertionFailure;
import org.hibernate.HibernateException;
import org.hibernate.action.internal.DelayedPostInsertIdentifier;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.spi.CollectionEntry;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.EntityKey;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.Status;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.pretty.MessageHelper;
import org.hibernate.type.CollectionType;
import org.jboss.logging.Logger;

public final class Collections {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)Collections.class.getName());

    public static void processUnreachableCollection(PersistentCollection coll, SessionImplementor session) {
        if (coll.getOwner() == null) {
            Collections.processNeverReferencedCollection(coll, session);
        } else {
            Collections.processDereferencedCollection(coll, session);
        }
    }

    private static void processDereferencedCollection(PersistentCollection coll, SessionImplementor session) {
        boolean hasOrphanDelete;
        PersistenceContext persistenceContext = session.getPersistenceContextInternal();
        CollectionEntry entry = persistenceContext.getCollectionEntry(coll);
        CollectionPersister loadedPersister = entry.getLoadedPersister();
        if (loadedPersister != null && LOG.isDebugEnabled()) {
            LOG.debugf("Collection dereferenced: %s", MessageHelper.collectionInfoString(loadedPersister, coll, entry.getLoadedKey(), session));
        }
        boolean bl = hasOrphanDelete = loadedPersister != null && loadedPersister.hasOrphanDelete();
        if (hasOrphanDelete) {
            EntityKey key;
            Object owner;
            Serializable ownerId = loadedPersister.getOwnerEntityPersister().getIdentifier(coll.getOwner(), session);
            if (ownerId == null) {
                EntityEntry ownerEntry;
                if (session.getFactory().getSessionFactoryOptions().isIdentifierRollbackEnabled() && (ownerEntry = persistenceContext.getEntry(coll.getOwner())) != null) {
                    ownerId = ownerEntry.getId();
                }
                if (ownerId == null) {
                    throw new AssertionFailure("Unable to determine collection owner identifier for orphan-delete processing");
                }
            }
            if ((owner = persistenceContext.getEntity(key = session.generateEntityKey(ownerId, loadedPersister.getOwnerEntityPersister()))) == null) {
                throw new AssertionFailure("collection owner not associated with session: " + loadedPersister.getRole());
            }
            EntityEntry e = persistenceContext.getEntry(owner);
            if (e != null && e.getStatus() != Status.DELETED && e.getStatus() != Status.GONE) {
                throw new HibernateException("A collection with cascade=\"all-delete-orphan\" was no longer referenced by the owning entity instance: " + loadedPersister.getRole());
            }
        }
        entry.setCurrentPersister(null);
        entry.setCurrentKey(null);
        Collections.prepareCollectionForUpdate(coll, entry, session.getFactory());
    }

    private static void processNeverReferencedCollection(PersistentCollection coll, SessionImplementor session) throws HibernateException {
        PersistenceContext persistenceContext = session.getPersistenceContextInternal();
        CollectionEntry entry = persistenceContext.getCollectionEntry(coll);
        if (LOG.isDebugEnabled()) {
            LOG.debugf("Found collection with unloaded owner: %s", MessageHelper.collectionInfoString(entry.getLoadedPersister(), coll, entry.getLoadedKey(), session));
        }
        entry.setCurrentPersister(entry.getLoadedPersister());
        entry.setCurrentKey(entry.getLoadedKey());
        Collections.prepareCollectionForUpdate(coll, entry, session.getFactory());
    }

    public static void processReachableCollection(PersistentCollection collection, CollectionType type, Object entity, SessionImplementor session) {
        collection.setOwner(entity);
        PersistenceContext persistenceContext = session.getPersistenceContextInternal();
        CollectionEntry ce = persistenceContext.getCollectionEntry(collection);
        if (ce == null) {
            throw new HibernateException("Found two representations of same collection: " + type.getRole());
        }
        SessionFactoryImplementor factory = session.getFactory();
        CollectionPersister persister = factory.getMetamodel().collectionPersister(type.getRole());
        ce.setCurrentPersister(persister);
        ce.setCurrentKey(type.getKeyOfOwner(entity, session));
        boolean isBytecodeEnhanced = persister.getOwnerEntityPersister().getBytecodeEnhancementMetadata().isEnhancedForLazyLoading();
        if (isBytecodeEnhanced && !collection.wasInitialized()) {
            if (LOG.isDebugEnabled()) {
                LOG.debugf("Skipping uninitialized bytecode-lazy collection: %s", MessageHelper.collectionInfoString(persister, collection, ce.getCurrentKey(), session));
            }
            ce.setReached(true);
            ce.setProcessed(true);
            return;
        }
        if (ce.isReached()) {
            throw new HibernateException("Found shared references to a collection: " + type.getRole());
        }
        ce.setReached(true);
        if (LOG.isDebugEnabled()) {
            if (collection.wasInitialized()) {
                LOG.debugf("Collection found: %s, was: %s (initialized)", MessageHelper.collectionInfoString(persister, collection, ce.getCurrentKey(), session), MessageHelper.collectionInfoString(ce.getLoadedPersister(), collection, ce.getLoadedKey(), session));
            } else {
                LOG.debugf("Collection found: %s, was: %s (uninitialized)", MessageHelper.collectionInfoString(persister, collection, ce.getCurrentKey(), session), MessageHelper.collectionInfoString(ce.getLoadedPersister(), collection, ce.getLoadedKey(), session));
            }
        }
        Collections.prepareCollectionForUpdate(collection, ce, factory);
    }

    private static void prepareCollectionForUpdate(PersistentCollection collection, CollectionEntry entry, SessionFactoryImplementor factory) {
        if (entry.isProcessed()) {
            throw new AssertionFailure("collection was processed twice by flush()");
        }
        entry.setProcessed(true);
        CollectionPersister loadedPersister = entry.getLoadedPersister();
        CollectionPersister currentPersister = entry.getCurrentPersister();
        if (loadedPersister != null || currentPersister != null) {
            boolean ownerChanged;
            boolean keyChanged = currentPersister != null && entry != null && !currentPersister.getKeyType().isEqual(entry.getLoadedKey(), entry.getCurrentKey(), factory) && !(entry.getLoadedKey() instanceof DelayedPostInsertIdentifier);
            boolean bl = ownerChanged = loadedPersister != currentPersister || keyChanged;
            if (ownerChanged) {
                boolean orphanDeleteAndRoleChanged;
                boolean bl2 = orphanDeleteAndRoleChanged = loadedPersister != null && currentPersister != null && loadedPersister.hasOrphanDelete();
                if (orphanDeleteAndRoleChanged) {
                    throw new HibernateException("Don't change the reference to a collection with delete-orphan enabled : " + loadedPersister.getRole());
                }
                if (currentPersister != null) {
                    entry.setDorecreate(true);
                }
                if (loadedPersister != null) {
                    entry.setDoremove(true);
                    if (entry.isDorecreate()) {
                        LOG.trace("Forcing collection initialization");
                        collection.forceInitialization();
                    }
                }
            } else if (collection.isDirty()) {
                entry.setDoupdate(true);
            }
        }
    }

    private Collections() {
    }
}

