/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.sf.ehcache.transaction.local;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.sf.ehcache.CacheEntry;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.TransactionController;
import net.sf.ehcache.store.ElementValueComparator;
import net.sf.ehcache.store.Store;
import net.sf.ehcache.transaction.AbstractTransactionStore;
import net.sf.ehcache.transaction.DeadLockException;
import net.sf.ehcache.transaction.SoftLock;
import net.sf.ehcache.transaction.SoftLockID;
import net.sf.ehcache.transaction.SoftLockManager;
import net.sf.ehcache.transaction.TransactionException;
import net.sf.ehcache.transaction.TransactionID;
import net.sf.ehcache.transaction.TransactionIDFactory;
import net.sf.ehcache.transaction.TransactionInterruptedException;
import net.sf.ehcache.transaction.TransactionTimeoutException;
import net.sf.ehcache.transaction.local.LocalTransactionContext;
import net.sf.ehcache.transaction.local.TransactionListener;
import net.sf.ehcache.util.LargeSet;
import net.sf.ehcache.util.SetAsList;
import net.sf.ehcache.writer.CacheWriterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalTransactionStore
extends AbstractTransactionStore {
    private static final Logger LOG = LoggerFactory.getLogger((String)LocalTransactionStore.class.getName());
    private final TransactionController transactionController;
    private final TransactionIDFactory transactionIdFactory;
    private final SoftLockManager softLockManager;
    private final Ehcache cache;
    private final String cacheName;
    private final ElementValueComparator comparator;

    public LocalTransactionStore(TransactionController transactionController, TransactionIDFactory transactionIdFactory, SoftLockManager softLockManager, Ehcache cache, Store store, ElementValueComparator comparator) {
        super(store);
        this.transactionController = transactionController;
        this.transactionIdFactory = transactionIdFactory;
        this.softLockManager = softLockManager;
        this.cache = cache;
        this.comparator = comparator;
        this.cacheName = cache.getName();
        transactionController.getRecoveryManager().register(this);
    }

    @Override
    public void dispose() {
        super.dispose();
        this.transactionController.getRecoveryManager().unregister(this);
    }

    Ehcache getCache() {
        return this.cache;
    }

    private LocalTransactionContext getCurrentTransactionContext() {
        LocalTransactionContext currentTransactionContext = this.transactionController.getCurrentTransactionContext();
        if (currentTransactionContext == null) {
            throw new TransactionException("transaction not started");
        }
        return currentTransactionContext;
    }

    private void assertNotTimedOut() {
        if (this.getCurrentTransactionContext().timedOut()) {
            throw new TransactionTimeoutException("transaction [" + this.getCurrentTransactionContext().getTransactionId() + "] timed out");
        }
        if (Thread.interrupted()) {
            throw new TransactionInterruptedException("transaction [" + this.getCurrentTransactionContext().getTransactionId() + "] interrupted");
        }
    }

    private long timeBeforeTimeout() {
        return this.getCurrentTransactionContext().timeBeforeTimeout();
    }

    private Element createElement(Object key, SoftLockID softLockId) {
        Element element = new Element(key, (Object)softLockId);
        element.setEternal(true);
        return element;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean cleanupExpiredSoftLock(Element oldElement, SoftLockID softLockId) {
        SoftLock softLock = this.softLockManager.findSoftLockById(softLockId);
        if (softLock == null || !softLock.isExpired()) {
            return false;
        }
        softLock.lock();
        softLock.freeze();
        try {
            Element frozenElement = this.transactionIdFactory.isDecisionCommit(softLockId.getTransactionID()) ? softLockId.getNewElement() : softLockId.getOldElement();
            if (frozenElement != null) {
                this.underlyingStore.replace(oldElement, frozenElement, this.comparator);
            } else {
                this.underlyingStore.removeElement(oldElement, this.comparator);
            }
        }
        finally {
            softLock.unfreeze();
            softLock.unlock();
        }
        return true;
    }

    public Set<TransactionID> recover() {
        Set<TransactionID> allOurTransactionIDs = this.transactionIdFactory.getAllTransactionIDs();
        HashSet<TransactionID> recoveredIds = new HashSet<TransactionID>(allOurTransactionIDs);
        Iterator iterator = recoveredIds.iterator();
        while (iterator.hasNext()) {
            TransactionID transactionId = (TransactionID)iterator.next();
            if (this.transactionIdFactory.isExpired(transactionId)) continue;
            iterator.remove();
        }
        LOG.debug("recover: {} dead transactions are going to be recovered", (Object)recoveredIds.size());
        for (TransactionID transactionId : recoveredIds) {
            HashSet<SoftLock> softLocks = new HashSet<SoftLock>(this.softLockManager.collectAllSoftLocksForTransactionID(transactionId));
            Iterator softLockIterator = softLocks.iterator();
            while (softLockIterator.hasNext()) {
                SoftLock softLock = (SoftLock)softLockIterator.next();
                Element element = this.underlyingStore.getQuiet(softLock.getKey());
                if (element.getObjectValue() instanceof SoftLockID) {
                    SoftLockID softLockId = (SoftLockID)element.getObjectValue();
                    this.cleanupExpiredSoftLock(element, softLockId);
                    continue;
                }
                softLockIterator.remove();
            }
            LOG.debug("recover: recovered {} soft locks from dead transaction with ID [{}]", (Object)softLocks.size(), (Object)transactionId);
        }
        return recoveredIds;
    }

    @Override
    public boolean put(Element element) throws CacheException {
        if (element == null) {
            return true;
        }
        Object key = element.getObjectKey();
        while (true) {
            SoftLock softLock;
            Element newElement;
            SoftLockID softLockId;
            this.assertNotTimedOut();
            Element oldElement = this.underlyingStore.getQuiet(key);
            if (oldElement == null) {
                SoftLockID softLockId2 = this.softLockManager.createSoftLockID(this.getCurrentTransactionContext().getTransactionId(), key, element, null);
                SoftLock softLock2 = this.softLockManager.findSoftLockById(softLockId2);
                softLock2.lock();
                Element newElement2 = this.createElement(key, softLockId2);
                oldElement = this.underlyingStore.putIfAbsent(newElement2);
                if (oldElement == null) {
                    this.getCurrentTransactionContext().registerSoftLock(this.cacheName, this, softLock2);
                    LOG.debug("put: cache [{}] key [{}] was not in, soft lock inserted", (Object)this.cacheName, key);
                    return true;
                }
                softLock2.unlock();
                LOG.debug("put: cache [{}] key [{}] was not in, soft lock insertion failed, retrying...", (Object)this.cacheName, key);
                continue;
            }
            Object value = oldElement.getObjectValue();
            if (value instanceof SoftLockID) {
                softLockId = (SoftLockID)value;
                if (this.cleanupExpiredSoftLock(oldElement, softLockId)) {
                    LOG.debug("put: cache [{}] key [{}] guarded by expired soft lock, cleaned up {}", new Object[]{this.cacheName, key, softLockId});
                    continue;
                }
                if (softLockId.getTransactionID().equals(this.getCurrentTransactionContext().getTransactionId())) {
                    SoftLockID newSoftLockId = this.softLockManager.createSoftLockID(this.getCurrentTransactionContext().getTransactionId(), softLockId.getKey(), element, softLockId.getOldElement());
                    newElement = this.createElement(newSoftLockId.getKey(), newSoftLockId);
                    this.underlyingStore.put(newElement);
                    LOG.debug("put: cache [{}] key [{}] soft locked in current transaction, replaced old value with new one under soft lock", (Object)this.cacheName, key);
                    return false;
                }
                softLock = this.softLockManager.findSoftLockById(softLockId);
                if (softLock != null) {
                    LOG.debug("put: cache [{}] key [{}] soft locked in foreign transaction, waiting {}ms for soft lock to die...", new Object[]{this.cacheName, key, this.timeBeforeTimeout()});
                    try {
                        if (!softLock.tryLock(this.timeBeforeTimeout())) {
                            LOG.debug("put: cache [{}] key [{}] soft locked in foreign transaction and not released before current transaction timeout", (Object)this.cacheName, key);
                            if (!this.getCurrentTransactionContext().hasLockedAnything()) continue;
                            throw new DeadLockException("deadlock detected in cache [" + this.cacheName + "] on key [" + key + "] between current transaction [" + this.getCurrentTransactionContext().getTransactionId() + "] and foreign transaction [" + softLockId.getTransactionID() + "]");
                        }
                        softLock.clearTryLock();
                    }
                    catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
                LOG.debug("put: cache [{}] key [{}] soft locked in foreign transaction, soft lock died, retrying...", (Object)this.cacheName, key);
                continue;
            }
            softLockId = this.softLockManager.createSoftLockID(this.getCurrentTransactionContext().getTransactionId(), key, element, oldElement);
            softLock = this.softLockManager.findSoftLockById(softLockId);
            softLock.lock();
            newElement = this.createElement(key, softLockId);
            boolean replaced = this.underlyingStore.replace(oldElement, newElement, this.comparator);
            if (replaced) {
                this.getCurrentTransactionContext().registerSoftLock(this.cacheName, this, softLock);
                LOG.debug("put: cache [{}] key [{}] was in, replaced with soft lock", (Object)this.cacheName, key);
                return false;
            }
            softLock.unlock();
            LOG.debug("put: cache [{}] key [{}] was in, replacement by soft lock failed, retrying... ", (Object)this.cacheName, key);
        }
    }

    @Override
    public Element getQuiet(Object key) {
        Element oldElement;
        block4: {
            SoftLock softLock;
            SoftLockID softLockId;
            if (key == null) {
                return null;
            }
            while (true) {
                this.assertNotTimedOut();
                oldElement = this.underlyingStore.getQuiet(key);
                if (oldElement == null) {
                    LOG.debug("getQuiet: cache [{}] key [{}] is not present", (Object)this.cacheName, key);
                    return null;
                }
                Object value = oldElement.getObjectValue();
                if (!(value instanceof SoftLockID)) break block4;
                softLockId = (SoftLockID)value;
                if (this.cleanupExpiredSoftLock(oldElement, softLockId)) {
                    LOG.debug("getQuiet: cache [{}] key [{}] guarded by expired soft lock, cleaned up {}", new Object[]{this.cacheName, key, softLockId});
                    continue;
                }
                LOG.debug("getQuiet: cache [{}] key [{}] soft locked, returning soft locked element", (Object)this.cacheName, key);
                softLock = this.softLockManager.findSoftLockById(softLockId);
                if (softLock != null) break;
                LOG.debug("getQuiet: cache [{}] key [{}] soft locked in foreign transaction, soft lock died, retrying...", (Object)this.cacheName, key);
            }
            return softLock.getElement(this.getCurrentTransactionContext().getTransactionId(), softLockId);
        }
        LOG.debug("getQuiet: cache [{}] key [{}] not soft locked, returning underlying element", (Object)this.cacheName, key);
        return oldElement;
    }

    @Override
    public Element get(Object key) {
        Element oldElement;
        block4: {
            SoftLock softLock;
            SoftLockID softLockId;
            if (key == null) {
                return null;
            }
            while (true) {
                this.assertNotTimedOut();
                oldElement = this.underlyingStore.get(key);
                if (oldElement == null) {
                    LOG.debug("get: cache [{}] key [{}] is not present", (Object)this.cacheName, key);
                    return null;
                }
                Object value = oldElement.getObjectValue();
                if (!(value instanceof SoftLockID)) break block4;
                softLockId = (SoftLockID)value;
                if (this.cleanupExpiredSoftLock(oldElement, softLockId)) {
                    LOG.debug("get: cache [{}] key [{}] guarded by expired soft lock, cleaned up {}", new Object[]{this.cacheName, key, softLockId});
                    continue;
                }
                LOG.debug("get: cache [{}] key [{}] soft locked, returning soft locked element", (Object)this.cacheName, key);
                softLock = this.softLockManager.findSoftLockById(softLockId);
                if (softLock != null) break;
                LOG.debug("get: cache [{}] key [{}] soft locked in foreign transaction, soft lock died, retrying...", (Object)this.cacheName, key);
            }
            return softLock.getElement(this.getCurrentTransactionContext().getTransactionId(), softLockId);
        }
        LOG.debug("get: cache [{}] key [{}] not soft locked, returning underlying element", (Object)this.cacheName, key);
        return oldElement;
    }

    @Override
    public Element remove(Object key) {
        if (key == null) {
            return null;
        }
        while (true) {
            SoftLock softLock;
            Element newElement;
            SoftLockID softLockId;
            boolean isPinned = false;
            this.assertNotTimedOut();
            Element oldElement = this.underlyingStore.getQuiet(key);
            if (oldElement == null) {
                SoftLockID softLockId2 = this.softLockManager.createSoftLockID(this.getCurrentTransactionContext().getTransactionId(), key, null, null);
                SoftLock softLock2 = this.softLockManager.findSoftLockById(softLockId2);
                softLock2.lock();
                Element newElement2 = this.createElement(key, softLockId2);
                oldElement = this.underlyingStore.putIfAbsent(newElement2);
                if (oldElement == null) {
                    this.getCurrentTransactionContext().registerSoftLock(this.cacheName, this, softLock2);
                    LOG.debug("remove: cache [{}] key [{}] was not in, soft lock inserted", (Object)this.cacheName, key);
                    return null;
                }
                softLock2.unlock();
                LOG.debug("remove: cache [{}] key [{}] was not in, soft lock insertion failed, retrying...", (Object)this.cacheName, key);
                continue;
            }
            Object value = oldElement.getObjectValue();
            if (value instanceof SoftLockID) {
                softLockId = (SoftLockID)value;
                if (this.cleanupExpiredSoftLock(oldElement, softLockId)) {
                    LOG.debug("remove: cache [{}] key [{}] guarded by expired soft lock, cleaned up {}", new Object[]{this.cacheName, key, softLockId});
                    continue;
                }
                if (softLockId.getTransactionID().equals(this.getCurrentTransactionContext().getTransactionId())) {
                    SoftLockID newSoftLockId = this.softLockManager.createSoftLockID(this.getCurrentTransactionContext().getTransactionId(), softLockId.getKey(), null, softLockId.getOldElement());
                    newElement = this.createElement(newSoftLockId.getKey(), newSoftLockId);
                    this.underlyingStore.put(newElement);
                    LOG.debug("remove: cache [{}] key [{}] soft locked in current transaction, replaced old value with new one under soft lock", (Object)this.cacheName, key);
                    return softLockId.getNewElement();
                }
                softLock = this.softLockManager.findSoftLockById(softLockId);
                if (softLock != null) {
                    LOG.debug("remove: cache [{}] key [{}] soft locked in foreign transaction, waiting {}ms for soft lock to die...", new Object[]{this.cacheName, key, this.timeBeforeTimeout()});
                    try {
                        if (!softLock.tryLock(this.timeBeforeTimeout())) {
                            LOG.debug("remove: cache [{}] key [{}] soft locked in foreign transaction and not released before current transaction timeout", (Object)this.cacheName, key);
                            if (!this.getCurrentTransactionContext().hasLockedAnything()) continue;
                            throw new DeadLockException("deadlock detected in cache [" + this.cacheName + "] on key [" + key + "] between current transaction [" + this.getCurrentTransactionContext().getTransactionId() + "] and foreign transaction [" + softLockId.getTransactionID() + "]");
                        }
                        softLock.clearTryLock();
                    }
                    catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
                LOG.debug("remove: cache [{}] key [{}] soft locked in foreign transaction, soft lock died, retrying...", (Object)this.cacheName, key);
                continue;
            }
            softLockId = this.softLockManager.createSoftLockID(this.getCurrentTransactionContext().getTransactionId(), key, null, oldElement);
            softLock = this.softLockManager.findSoftLockById(softLockId);
            softLock.lock();
            newElement = this.createElement(key, softLockId);
            boolean replaced = this.underlyingStore.replace(oldElement, newElement, this.comparator);
            if (replaced) {
                this.getCurrentTransactionContext().registerSoftLock(this.cacheName, this, softLock);
                LOG.debug("remove: cache [{}] key [{}] was in, replaced with soft lock", (Object)this.cacheName, key);
                return oldElement;
            }
            softLock.unlock();
            LOG.debug("remove: cache [{}] key [{}] was in, replacement by soft lock failed, retrying...", (Object)this.cacheName, key);
        }
    }

    @Override
    public List getKeys() {
        this.assertNotTimedOut();
        LargeSet<Object> keys = new LargeSet<Object>(){

            @Override
            public int sourceSize() {
                return LocalTransactionStore.this.underlyingStore.getSize();
            }

            @Override
            public Iterator<Object> sourceIterator() {
                Iterator<Object> iterator = LocalTransactionStore.this.underlyingStore.getKeys().iterator();
                return iterator;
            }
        };
        keys.removeAll(this.softLockManager.getKeysInvisibleInContext(this.getCurrentTransactionContext(), this.underlyingStore));
        return new SetAsList<Object>(keys);
    }

    @Override
    public int getSize() {
        this.assertNotTimedOut();
        int sizeModifier = 0;
        return Math.max(0, this.underlyingStore.getSize() + (sizeModifier -= this.softLockManager.getKeysInvisibleInContext(this.getCurrentTransactionContext(), this.underlyingStore).size()));
    }

    @Override
    public int getTerracottaClusteredSize() {
        if (this.transactionController.getCurrentTransactionContext() == null) {
            return this.underlyingStore.getTerracottaClusteredSize();
        }
        int sizeModifier = 0;
        return this.underlyingStore.getTerracottaClusteredSize() + (sizeModifier -= this.softLockManager.getKeysInvisibleInContext(this.getCurrentTransactionContext(), this.underlyingStore).size());
    }

    @Override
    public boolean containsKey(Object key) {
        this.assertNotTimedOut();
        return this.getKeys().contains(key);
    }

    @Override
    public void removeAll() throws CacheException {
        this.assertNotTimedOut();
        List keys = this.getKeys();
        for (Object key : keys) {
            this.remove(key);
        }
    }

    @Override
    public boolean putWithWriter(final Element element, final CacheWriterManager writerManager) throws CacheException {
        if (element == null) {
            return true;
        }
        this.assertNotTimedOut();
        boolean put = this.put(element);
        this.getCurrentTransactionContext().addListener(new TransactionListener(){

            @Override
            public void beforeCommit() {
                if (writerManager != null) {
                    writerManager.put(element);
                } else {
                    LocalTransactionStore.this.cache.getWriterManager().put(element);
                }
            }

            @Override
            public void afterCommit() {
            }

            @Override
            public void afterRollback() {
            }
        });
        return put;
    }

    @Override
    public Element removeWithWriter(Object key, final CacheWriterManager writerManager) throws CacheException {
        if (key == null) {
            return null;
        }
        this.assertNotTimedOut();
        Element removed = this.remove(key);
        final CacheEntry cacheEntry = new CacheEntry(key, this.getQuiet(key));
        this.getCurrentTransactionContext().addListener(new TransactionListener(){

            @Override
            public void beforeCommit() {
                if (writerManager != null) {
                    writerManager.remove(cacheEntry);
                } else {
                    LocalTransactionStore.this.cache.getWriterManager().remove(cacheEntry);
                }
            }

            @Override
            public void afterCommit() {
            }

            @Override
            public void afterRollback() {
            }
        });
        return removed;
    }

    @Override
    public Element putIfAbsent(Element element) throws NullPointerException {
        Element oldElement;
        if (element == null) {
            throw new NullPointerException("element cannot be null");
        }
        if (element.getObjectKey() == null) {
            throw new NullPointerException("element key cannot be null");
        }
        Object key = element.getObjectKey();
        while (true) {
            SoftLock softLock;
            SoftLockID softLockId;
            this.assertNotTimedOut();
            oldElement = this.underlyingStore.getQuiet(key);
            if (oldElement == null) {
                softLockId = this.softLockManager.createSoftLockID(this.getCurrentTransactionContext().getTransactionId(), key, element, oldElement);
                softLock = this.softLockManager.findSoftLockById(softLockId);
                softLock.lock();
                Element newElement = this.createElement(key, softLockId);
                oldElement = this.underlyingStore.putIfAbsent(newElement);
                if (oldElement == null) {
                    this.getCurrentTransactionContext().registerSoftLock(this.cacheName, this, softLock);
                    LOG.debug("putIfAbsent: cache [{}] key [{}] was not in, soft lock inserted", (Object)this.cacheName, key);
                    return null;
                }
                softLock.unlock();
                LOG.debug("putIfAbsent: cache [{}] key [{}] was not in, soft lock insertion failed, retrying", (Object)this.cacheName, key);
                continue;
            }
            if (!(oldElement.getValue() instanceof SoftLockID)) break;
            softLockId = (SoftLockID)oldElement.getObjectValue();
            if (this.cleanupExpiredSoftLock(oldElement, softLockId)) {
                LOG.debug("putIfAbsent: cache [{}] key [{}] guarded by expired soft lock, cleaned up {}", new Object[]{this.cacheName, key, softLockId});
                continue;
            }
            if (softLockId.getTransactionID().equals(this.getCurrentTransactionContext().getTransactionId())) {
                softLock = this.softLockManager.findSoftLockById(softLockId);
                Element currentElement = softLock.getElement(this.getCurrentTransactionContext().getTransactionId(), softLockId);
                if (currentElement == null) {
                    SoftLockID newSoftLockId = this.softLockManager.createSoftLockID(this.getCurrentTransactionContext().getTransactionId(), softLockId.getKey(), element, softLockId.getOldElement());
                    Element newElement = this.createElement(newSoftLockId.getKey(), newSoftLockId);
                    this.underlyingStore.put(newElement);
                    LOG.debug("putIfAbsent: cache [{}] key [{}] soft locked in current transaction, replaced null with new element under soft lock", (Object)this.cacheName, key);
                    return null;
                }
                LOG.debug("putIfAbsent: cache [{}] key [{}] soft locked in current transaction, old element is not null", (Object)this.cacheName, key);
                return currentElement;
            }
            softLock = this.softLockManager.findSoftLockById(softLockId);
            if (softLock != null) {
                LOG.debug("putIfAbsent: cache [{}] key [{}] soft locked in foreign transaction, waiting {}ms for soft lock to die...", new Object[]{this.cacheName, key, this.timeBeforeTimeout()});
                try {
                    if (!softLock.tryLock(this.timeBeforeTimeout())) {
                        LOG.debug("putIfAbsent: cache [{}] key [{}] soft locked in foreign transaction and not released before current transaction timeout", (Object)this.cacheName, key);
                        if (!this.getCurrentTransactionContext().hasLockedAnything()) continue;
                        throw new DeadLockException("deadlock detected in cache [" + this.cacheName + "] on key [" + key + "] between current transaction [" + this.getCurrentTransactionContext().getTransactionId() + "] and foreign transaction [" + softLockId.getTransactionID() + "]");
                    }
                    softLock.clearTryLock();
                }
                catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
            LOG.debug("putIfAbsent: cache [{}] key [{}] soft locked in foreign transaction, soft lock died, retrying...", (Object)this.cacheName, key);
        }
        return oldElement;
    }

    @Override
    public Element removeElement(Element element, ElementValueComparator comparator) throws NullPointerException {
        SoftLock softLock;
        SoftLockID softLockId;
        Element oldElement;
        if (element == null) {
            throw new NullPointerException("element cannot be null");
        }
        if (element.getObjectKey() == null) {
            throw new NullPointerException("element key cannot be null");
        }
        if (comparator == null) {
            throw new NullPointerException("comparator cannot be null");
        }
        Object key = element.getObjectKey();
        while (true) {
            this.assertNotTimedOut();
            oldElement = this.underlyingStore.getQuiet(key);
            if (oldElement == null) {
                LOG.debug("removeElement: cache [{}] key [{}] was not in, nothing removed", (Object)this.cacheName, key);
                return null;
            }
            Object value = oldElement.getObjectValue();
            if (!(value instanceof SoftLockID)) break;
            softLockId = (SoftLockID)value;
            if (this.cleanupExpiredSoftLock(oldElement, softLockId)) {
                LOG.debug("removeElement: cache [{}] key [{}] guarded by expired soft lock, cleaned up {}", new Object[]{this.cacheName, key, softLockId});
                continue;
            }
            if (softLockId.getTransactionID().equals(this.getCurrentTransactionContext().getTransactionId())) {
                softLock = this.softLockManager.findSoftLockById(softLockId);
                Element currentElement = softLock.getElement(this.getCurrentTransactionContext().getTransactionId(), softLockId);
                if (comparator.equals(element, currentElement)) {
                    SoftLockID newSoftLockId = this.softLockManager.createSoftLockID(this.getCurrentTransactionContext().getTransactionId(), softLockId.getKey(), element, softLockId.getOldElement());
                    Element newElement = this.createElement(newSoftLockId.getKey(), newSoftLockId);
                    this.underlyingStore.put(newElement);
                    LOG.debug("removeElement: cache [{}] key [{}] soft locked in current transaction, replaced old element with null under soft lock", (Object)this.cacheName, key);
                    return softLockId.getNewElement();
                }
                LOG.debug("removeElement: cache [{}] key [{}] soft locked in current transaction, old element did not match element to remove", (Object)this.cacheName, key);
                return null;
            }
            softLock = this.softLockManager.findSoftLockById(softLockId);
            if (softLock != null) {
                LOG.debug("removeElement: cache [{}] key [{}] soft locked in foreign transaction, waiting {}ms for soft lock to die...", new Object[]{this.cacheName, key, this.timeBeforeTimeout()});
                try {
                    if (!softLock.tryLock(this.timeBeforeTimeout())) {
                        LOG.debug("removeElement: cache [{}] key [{}] soft locked in foreign transaction and not released before current transaction timeout", (Object)this.cacheName, key);
                        if (!this.getCurrentTransactionContext().hasLockedAnything()) continue;
                        throw new DeadLockException("deadlock detected in cache [" + this.cacheName + "] on key [" + key + "] between current transaction [" + this.getCurrentTransactionContext().getTransactionId() + "] and foreign transaction [" + softLockId.getTransactionID() + "]");
                    }
                    softLock.clearTryLock();
                }
                catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
            LOG.debug("removeElement: cache [{}] key [{}] soft locked in foreign transaction, soft lock died, retrying...", (Object)this.cacheName, key);
        }
        if (!comparator.equals(element, oldElement)) {
            LOG.debug("removeElement: cache [{}] key [{}] was altered by a committed transaction, old element did not match element to remove", (Object)this.cacheName, key);
            return null;
        }
        softLockId = this.softLockManager.createSoftLockID(this.getCurrentTransactionContext().getTransactionId(), key, null, oldElement);
        softLock = this.softLockManager.findSoftLockById(softLockId);
        softLock.lock();
        Element newElement = this.createElement(key, softLockId);
        boolean replaced = this.underlyingStore.replace(oldElement, newElement, comparator);
        if (replaced) {
            this.getCurrentTransactionContext().registerSoftLock(this.cacheName, this, softLock);
            LOG.debug("removeElement: cache [{}] key [{}] was in, replaced with soft lock", (Object)this.cacheName, key);
            return oldElement;
        }
        softLock.unlock();
        LOG.debug("removeElement: cache [{}] key [{}] was in, replacement by soft lock failed", (Object)this.cacheName, key);
        return null;
    }

    @Override
    public boolean replace(Element old, Element element, ElementValueComparator comparator) throws NullPointerException, IllegalArgumentException {
        SoftLock softLock;
        SoftLockID softLockId;
        Element oldElement;
        if (old == null) {
            throw new NullPointerException("old cannot be null");
        }
        if (old.getObjectKey() == null) {
            throw new NullPointerException("old key cannot be null");
        }
        if (element == null) {
            throw new NullPointerException("element cannot be null");
        }
        if (element.getObjectKey() == null) {
            throw new NullPointerException("element key cannot be null");
        }
        if (comparator == null) {
            throw new NullPointerException("comparator cannot be null");
        }
        if (!old.getKey().equals(element.getKey())) {
            throw new IllegalArgumentException("old and element keys are not equal");
        }
        Object key = element.getObjectKey();
        while (true) {
            this.assertNotTimedOut();
            oldElement = this.underlyingStore.getQuiet(key);
            if (oldElement == null) {
                LOG.debug("replace2: cache [{}] key [{}] was not in, nothing replaced", (Object)this.cacheName, key);
                return false;
            }
            Object value = oldElement.getObjectValue();
            if (!(value instanceof SoftLockID)) break;
            softLockId = (SoftLockID)value;
            if (this.cleanupExpiredSoftLock(oldElement, softLockId)) {
                LOG.debug("replace2: cache [{}] key [{}] guarded by expired soft lock, cleaned up {}", new Object[]{this.cacheName, key, softLockId});
                continue;
            }
            if (softLockId.getTransactionID().equals(this.getCurrentTransactionContext().getTransactionId())) {
                softLock = this.softLockManager.findSoftLockById(softLockId);
                Element currentElement = softLock.getElement(this.getCurrentTransactionContext().getTransactionId(), softLockId);
                if (comparator.equals(old, currentElement)) {
                    SoftLockID newSoftLockId = this.softLockManager.createSoftLockID(this.getCurrentTransactionContext().getTransactionId(), softLockId.getKey(), element, softLockId.getOldElement());
                    Element newElement = this.createElement(newSoftLockId.getKey(), newSoftLockId);
                    this.underlyingStore.put(newElement);
                    LOG.debug("replace2: cache [{}] key [{}] soft locked in current transaction, replaced old element with new one under soft lock", (Object)this.cacheName, key);
                    return true;
                }
                LOG.debug("replace2: cache [{}] key [{}] soft locked in current transaction, old element did not match element to replace", (Object)this.cacheName, key);
                return false;
            }
            softLock = this.softLockManager.findSoftLockById(softLockId);
            if (softLock != null) {
                LOG.debug("replace2: cache [{}] key [{}] soft locked in foreign transaction, waiting {}ms for soft lock to die...", new Object[]{this.cacheName, key, this.timeBeforeTimeout()});
                try {
                    if (!softLock.tryLock(this.timeBeforeTimeout())) {
                        LOG.debug("replace2: cache [{}] key [{}] soft locked in foreign transaction and not released before current transaction timeout", (Object)this.cacheName, key);
                        if (!this.getCurrentTransactionContext().hasLockedAnything()) continue;
                        throw new DeadLockException("deadlock detected in cache [" + this.cacheName + "] on key [" + key + "] between current transaction [" + this.getCurrentTransactionContext().getTransactionId() + "] and foreign transaction [" + softLockId.getTransactionID() + "]");
                    }
                    softLock.clearTryLock();
                }
                catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
            LOG.debug("replace2: cache [{}] key [{}] soft locked in foreign transaction, soft lock died, retrying...", (Object)this.cacheName, key);
        }
        if (!comparator.equals(old, oldElement)) {
            LOG.debug("replace2: cache [{}] key [{}] was altered by a committed transaction, old element did not match element to replace", (Object)this.cacheName, key);
            return false;
        }
        softLockId = this.softLockManager.createSoftLockID(this.getCurrentTransactionContext().getTransactionId(), key, element, oldElement);
        softLock = this.softLockManager.findSoftLockById(softLockId);
        softLock.lock();
        Element newElement = this.createElement(key, softLockId);
        boolean replaced = this.underlyingStore.replace(oldElement, newElement, comparator);
        if (replaced) {
            this.getCurrentTransactionContext().registerSoftLock(this.cacheName, this, softLock);
            LOG.debug("replace2: cache [{}] key [{}] was in, replaced with soft lock", (Object)this.cacheName, key);
            return true;
        }
        softLock.unlock();
        LOG.debug("replace2: cache [{}] key [{}] was in, replacement by soft lock failed", (Object)this.cacheName, key);
        return false;
    }

    @Override
    public Element replace(Element element) throws NullPointerException {
        SoftLock softLock;
        SoftLockID softLockId;
        Element oldElement;
        if (element == null) {
            throw new NullPointerException("element cannot be null");
        }
        Object key = element.getObjectKey();
        if (key == null) {
            throw new NullPointerException("element key cannot be null");
        }
        while (true) {
            this.assertNotTimedOut();
            oldElement = this.underlyingStore.getQuiet(key);
            if (oldElement == null) {
                LOG.debug("replace1: cache [{}] key [{}] was not in, nothing replaced", (Object)this.cacheName, key);
                return null;
            }
            Object value = oldElement.getObjectValue();
            if (!(value instanceof SoftLockID)) break;
            softLockId = (SoftLockID)value;
            if (this.cleanupExpiredSoftLock(oldElement, softLockId)) {
                LOG.debug("replace1: cache [{}] key [{}] guarded by expired soft lock, cleaned up {}", new Object[]{this.cacheName, key, softLockId});
                continue;
            }
            if (softLockId.getTransactionID().equals(this.getCurrentTransactionContext().getTransactionId())) {
                softLock = this.softLockManager.findSoftLockById(softLockId);
                Element currentElement = softLock.getElement(this.getCurrentTransactionContext().getTransactionId(), softLockId);
                if (currentElement != null) {
                    SoftLockID newSoftLockId = this.softLockManager.createSoftLockID(this.getCurrentTransactionContext().getTransactionId(), softLockId.getKey(), element, softLockId.getOldElement());
                    Element newElement = this.createElement(newSoftLockId.getKey(), newSoftLockId);
                    this.underlyingStore.put(newElement);
                    LOG.debug("replace1: cache [{}] key [{}] soft locked in current transaction, replaced old element with new one under soft lock", (Object)this.cacheName, key);
                    return softLockId.getNewElement();
                }
                LOG.debug("replace1: cache [{}] key [{}] soft locked in current transaction, old element was null, not replaced", (Object)this.cacheName, key);
                return null;
            }
            softLock = this.softLockManager.findSoftLockById(softLockId);
            if (softLock != null) {
                LOG.debug("replace1: cache [{}] key [{}] soft locked in foreign transaction, waiting {}ms for soft lock to die...", new Object[]{this.cacheName, key, this.timeBeforeTimeout()});
                try {
                    if (!softLock.tryLock(this.timeBeforeTimeout())) {
                        LOG.debug("replace1: cache [{}] key [{}] soft locked in foreign transaction and not released before current transaction timeout", (Object)this.cacheName, key);
                        if (!this.getCurrentTransactionContext().hasLockedAnything()) continue;
                        throw new DeadLockException("deadlock detected in cache [" + this.cacheName + "] on key [" + key + "] between current transaction [" + this.getCurrentTransactionContext().getTransactionId() + "] and foreign transaction [" + softLockId.getTransactionID() + "]");
                    }
                    softLock.clearTryLock();
                }
                catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
            LOG.debug("replace1: cache [{}] key [{}] soft locked in foreign transaction, soft lock died, retrying...", (Object)this.cacheName, key);
        }
        softLockId = this.softLockManager.createSoftLockID(this.getCurrentTransactionContext().getTransactionId(), key, element, oldElement);
        softLock = this.softLockManager.findSoftLockById(softLockId);
        softLock.lock();
        Element newElement = this.createElement(key, softLockId);
        Element replaced = this.underlyingStore.replace(newElement);
        if (replaced != null) {
            this.getCurrentTransactionContext().registerSoftLock(this.cacheName, this, softLock);
            LOG.debug("replace1: cache [{}] key [{}] was in, replaced with soft lock", (Object)this.cacheName, key);
            return replaced;
        }
        softLock.unlock();
        LOG.debug("replace1: cache [{}] key [{}] was in, replacement by soft lock failed", (Object)this.cacheName, key);
        return null;
    }

    void commit(List<SoftLock> softLocks, TransactionID transactionId) {
        LOG.debug("committing {} soft lock(s) in cache {}", (Object)softLocks.size(), (Object)this.cache.getName());
        for (SoftLock softLock : softLocks) {
            Element e = this.underlyingStore.getQuiet(softLock.getKey());
            if (e == null) {
                LOG.debug("soft lock ID with key '{}' is not present in underlying store, ignoring it", softLock.getKey());
                continue;
            }
            if (!(e.getObjectValue() instanceof SoftLockID)) {
                LOG.debug("soft lock ID with key '{}' replaced with value in underlying store, ignoring it", softLock.getKey());
                continue;
            }
            SoftLockID softLockId = (SoftLockID)e.getObjectValue();
            if (!softLockId.getTransactionID().equals(transactionId)) {
                LOG.debug("soft lock ID with key '{}' of foreign tx in underlying store, ignoring it", softLock.getKey());
                continue;
            }
            Element element = softLockId.getNewElement();
            if (element != null) {
                this.underlyingStore.put(element);
                continue;
            }
            this.underlyingStore.remove(softLock.getKey());
        }
    }

    void rollback(List<SoftLock> softLocks, TransactionID transactionId) {
        LOG.debug("rolling back {} soft lock(s) in cache {}", (Object)softLocks.size(), (Object)this.cache.getName());
        for (SoftLock softLock : softLocks) {
            Element e = this.underlyingStore.getQuiet(softLock.getKey());
            if (e == null) {
                LOG.debug("soft lock ID with key '{}' is not present in underlying store, ignoring it", softLock.getKey());
                continue;
            }
            if (!(e.getObjectValue() instanceof SoftLockID)) {
                LOG.debug("soft lock ID with key '{}' replaced with value in underlying store, ignoring it", softLock.getKey());
                continue;
            }
            SoftLockID softLockId = (SoftLockID)e.getObjectValue();
            if (!softLockId.getTransactionID().equals(transactionId)) {
                LOG.debug("soft lock ID with key '{}' of foreign tx in underlying store, ignoring it", softLock.getKey());
                continue;
            }
            Element element = softLockId.getOldElement();
            if (element != null) {
                this.underlyingStore.put(element);
                continue;
            }
            this.underlyingStore.remove(softLock.getKey());
        }
    }
}

