/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.transaction.xa.commands;

import net.sf.ehcache.Element;
import net.sf.ehcache.store.ElementValueComparator;
import net.sf.ehcache.store.Store;
import net.sf.ehcache.transaction.SoftLock;
import net.sf.ehcache.transaction.SoftLockID;
import net.sf.ehcache.transaction.SoftLockManager;
import net.sf.ehcache.transaction.xa.OptimisticLockFailureException;
import net.sf.ehcache.transaction.xa.XidTransactionID;
import net.sf.ehcache.transaction.xa.commands.Command;

public abstract class AbstractStoreCommand
implements Command {
    private final Element oldElement;
    private final Element newElement;
    private Element softLockedElement;

    public AbstractStoreCommand(Element oldElement, Element newElement) {
        this.newElement = newElement;
        this.oldElement = oldElement;
    }

    protected Element getOldElement() {
        return this.oldElement;
    }

    protected Element getNewElement() {
        return this.newElement;
    }

    @Override
    public boolean prepare(Store store, SoftLockManager softLockManager, XidTransactionID transactionId, ElementValueComparator comparator) {
        Object objectKey = this.getObjectKey();
        SoftLockID softLockId = softLockManager.createSoftLockID(transactionId, objectKey, this.newElement, this.oldElement);
        SoftLock softLock = softLockManager.findSoftLockById(softLockId);
        this.softLockedElement = this.createElement(objectKey, softLockId, store, false);
        softLock.lock();
        softLock.freeze();
        if (this.oldElement == null) {
            Element previousElement = store.putIfAbsent(this.softLockedElement);
            if (previousElement != null) {
                softLock.unfreeze();
                softLock.unlock();
                this.softLockedElement = null;
                throw new OptimisticLockFailureException();
            }
        } else {
            boolean replaced = store.replace(this.oldElement, this.softLockedElement, comparator);
            if (!replaced) {
                softLock.unfreeze();
                softLock.unlock();
                this.softLockedElement = null;
                throw new OptimisticLockFailureException();
            }
        }
        return true;
    }

    @Override
    public void rollback(Store store, SoftLockManager softLockManager) {
        if (this.oldElement == null) {
            store.remove(this.getObjectKey());
        } else {
            store.put(this.oldElement);
        }
        SoftLockID softLockId = (SoftLockID)this.softLockedElement.getObjectValue();
        SoftLock softLock = softLockManager.findSoftLockById(softLockId);
        softLock.unfreeze();
        softLock.unlock();
        this.softLockedElement = null;
    }

    private Element createElement(Object key, SoftLockID softLockId, Store store, boolean wasPinned) {
        Element element = new Element(key, (Object)softLockId);
        element.setEternal(true);
        return element;
    }
}

