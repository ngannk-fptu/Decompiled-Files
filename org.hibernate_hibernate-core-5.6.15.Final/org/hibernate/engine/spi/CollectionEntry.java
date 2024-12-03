/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.spi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import org.hibernate.AssertionFailure;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.collection.internal.AbstractPersistentCollection;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.pretty.MessageHelper;

public final class CollectionEntry
implements Serializable {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(CollectionEntry.class);
    private Serializable snapshot;
    private String role;
    private transient CollectionPersister loadedPersister;
    private Serializable loadedKey;
    private transient boolean reached;
    private transient boolean processed;
    private transient boolean doupdate;
    private transient boolean doremove;
    private transient boolean dorecreate;
    private transient boolean ignore;
    private transient CollectionPersister currentPersister;
    private transient Serializable currentKey;
    private boolean fromMerge;

    public CollectionEntry(CollectionPersister persister, PersistentCollection collection) {
        this.ignore = false;
        collection.clearDirty();
        this.snapshot = persister.isMutable() ? collection.getSnapshot(persister) : null;
        collection.setSnapshot(this.loadedKey, this.role, this.snapshot);
    }

    public CollectionEntry(PersistentCollection collection, CollectionPersister loadedPersister, Serializable loadedKey, boolean ignore) {
        this.ignore = ignore;
        this.loadedKey = loadedKey;
        this.setLoadedPersister(loadedPersister);
        collection.setSnapshot(loadedKey, this.role, null);
    }

    public CollectionEntry(CollectionPersister loadedPersister, Serializable loadedKey) {
        this.ignore = false;
        this.loadedKey = loadedKey;
        this.setLoadedPersister(loadedPersister);
    }

    public CollectionEntry(PersistentCollection collection, SessionFactoryImplementor factory) throws MappingException {
        this.ignore = false;
        this.loadedKey = collection.getKey();
        this.setLoadedPersister(factory.getMetamodel().collectionPersister(collection.getRole()));
        this.snapshot = collection.getStoredSnapshot();
    }

    private CollectionEntry(String role, Serializable snapshot, Serializable loadedKey, SessionFactoryImplementor factory) {
        this.role = role;
        this.snapshot = snapshot;
        this.loadedKey = loadedKey;
        if (role != null) {
            this.afterDeserialize(factory);
        }
    }

    private void dirty(PersistentCollection collection) throws HibernateException {
        boolean forceDirty;
        CollectionPersister loadedPersister = this.getLoadedPersister();
        boolean bl = forceDirty = collection.wasInitialized() && !collection.isDirty() && loadedPersister != null && loadedPersister.isMutable() && (collection.isDirectlyAccessible() || loadedPersister.getElementType().isMutable()) && !collection.equalsSnapshot(loadedPersister);
        if (forceDirty) {
            collection.dirty();
        }
    }

    public void preFlush(PersistentCollection collection) throws HibernateException {
        boolean nonMutableChange;
        if (this.loadedKey == null && collection.getKey() != null) {
            this.loadedKey = collection.getKey();
        }
        CollectionPersister loadedPersister = this.getLoadedPersister();
        boolean bl = nonMutableChange = collection.isDirty() && loadedPersister != null && !loadedPersister.isMutable();
        if (nonMutableChange) {
            throw new HibernateException("changed an immutable collection instance: " + MessageHelper.collectionInfoString(loadedPersister.getRole(), this.getLoadedKey()));
        }
        this.dirty(collection);
        if (LOG.isDebugEnabled() && collection.isDirty() && loadedPersister != null) {
            LOG.debugf("Collection dirty: %s", MessageHelper.collectionInfoString(loadedPersister.getRole(), this.getLoadedKey()));
        }
        this.setReached(false);
        this.setProcessed(false);
        this.setDoupdate(false);
        this.setDoremove(false);
        this.setDorecreate(false);
    }

    public void postInitialize(PersistentCollection collection) throws HibernateException {
        CollectionPersister loadedPersister = this.getLoadedPersister();
        this.snapshot = loadedPersister.isMutable() ? collection.getSnapshot(loadedPersister) : null;
        collection.setSnapshot(this.loadedKey, this.role, this.snapshot);
        if (loadedPersister.getBatchSize() > 1) {
            ((AbstractPersistentCollection)collection).getSession().getPersistenceContextInternal().getBatchFetchQueue().removeBatchLoadableCollection(this);
        }
    }

    public void postFlush(PersistentCollection collection) throws HibernateException {
        if (this.isIgnore()) {
            this.ignore = false;
        } else if (!this.isProcessed()) {
            throw new HibernateException(LOG.collectionNotProcessedByFlush(collection.getRole()));
        }
        collection.setSnapshot(this.loadedKey, this.role, this.snapshot);
    }

    public void afterAction(PersistentCollection collection) {
        boolean resnapshot;
        this.loadedKey = this.getCurrentKey();
        this.setLoadedPersister(this.getCurrentPersister());
        boolean bl = resnapshot = collection.wasInitialized() && (this.isDoremove() || this.isDorecreate() || this.isDoupdate());
        if (resnapshot) {
            this.snapshot = this.loadedPersister == null || !this.loadedPersister.isMutable() ? null : collection.getSnapshot(this.loadedPersister);
        }
        collection.postAction();
    }

    public Serializable getKey() {
        return this.getLoadedKey();
    }

    public String getRole() {
        return this.role;
    }

    public Serializable getSnapshot() {
        return this.snapshot;
    }

    public void resetStoredSnapshot(PersistentCollection collection, Serializable storedSnapshot) {
        LOG.debugf("Reset storedSnapshot to %s for %s", storedSnapshot, this);
        if (this.fromMerge) {
            return;
        }
        this.snapshot = storedSnapshot;
        collection.setSnapshot(this.loadedKey, this.role, this.snapshot);
        this.fromMerge = true;
    }

    private void setLoadedPersister(CollectionPersister persister) {
        this.loadedPersister = persister;
        this.setRole(persister == null ? null : persister.getRole());
    }

    void afterDeserialize(SessionFactoryImplementor factory) {
        this.loadedPersister = factory == null ? null : factory.getMetamodel().collectionPersister(this.role);
    }

    public boolean wasDereferenced() {
        return this.getLoadedKey() == null;
    }

    public boolean isReached() {
        return this.reached;
    }

    public void setReached(boolean reached) {
        this.reached = reached;
    }

    public boolean isProcessed() {
        return this.processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public boolean isDoupdate() {
        return this.doupdate;
    }

    public void setDoupdate(boolean doupdate) {
        this.doupdate = doupdate;
    }

    public boolean isDoremove() {
        return this.doremove;
    }

    public void setDoremove(boolean doremove) {
        this.doremove = doremove;
    }

    public boolean isDorecreate() {
        return this.dorecreate;
    }

    public void setDorecreate(boolean dorecreate) {
        this.dorecreate = dorecreate;
    }

    public boolean isIgnore() {
        return this.ignore;
    }

    public CollectionPersister getCurrentPersister() {
        return this.currentPersister;
    }

    public void setCurrentPersister(CollectionPersister currentPersister) {
        this.currentPersister = currentPersister;
    }

    public Serializable getCurrentKey() {
        return this.currentKey;
    }

    public void setCurrentKey(Serializable currentKey) {
        this.currentKey = currentKey;
    }

    public CollectionPersister getLoadedPersister() {
        return this.loadedPersister;
    }

    public Serializable getLoadedKey() {
        return this.loadedKey;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String toString() {
        String result = "CollectionEntry" + MessageHelper.collectionInfoString(this.loadedPersister.getRole(), this.loadedKey);
        if (this.currentPersister != null) {
            result = result + "->" + MessageHelper.collectionInfoString(this.currentPersister.getRole(), this.currentKey);
        }
        return result;
    }

    public Collection getOrphans(String entityName, PersistentCollection collection) throws HibernateException {
        if (this.snapshot == null) {
            throw new AssertionFailure("no collection snapshot for orphan delete");
        }
        return collection.getOrphans(this.snapshot, entityName);
    }

    public boolean isSnapshotEmpty(PersistentCollection collection) {
        CollectionPersister loadedPersister = this.getLoadedPersister();
        return collection.wasInitialized() && (loadedPersister == null || loadedPersister.isMutable()) && collection.isSnapshotEmpty(this.getSnapshot());
    }

    public void serialize(ObjectOutputStream oos) throws IOException {
        oos.writeObject(this.role);
        oos.writeObject(this.snapshot);
        oos.writeObject(this.loadedKey);
    }

    public static CollectionEntry deserialize(ObjectInputStream ois, SessionImplementor session) throws IOException, ClassNotFoundException {
        return new CollectionEntry((String)ois.readObject(), (Serializable)ois.readObject(), (Serializable)ois.readObject(), session == null ? null : session.getFactory());
    }
}

