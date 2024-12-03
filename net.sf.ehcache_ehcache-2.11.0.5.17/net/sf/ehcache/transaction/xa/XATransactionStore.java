/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.transaction.RollbackException
 *  javax.transaction.Synchronization
 *  javax.transaction.SystemException
 *  javax.transaction.Transaction
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.sf.ehcache.transaction.xa;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import javax.transaction.RollbackException;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.xa.XAException;
import net.sf.ehcache.CacheEntry;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.statistics.StatisticBuilder;
import net.sf.ehcache.store.ElementValueComparator;
import net.sf.ehcache.store.Store;
import net.sf.ehcache.transaction.AbstractTransactionStore;
import net.sf.ehcache.transaction.SoftLock;
import net.sf.ehcache.transaction.SoftLockID;
import net.sf.ehcache.transaction.SoftLockManager;
import net.sf.ehcache.transaction.TransactionException;
import net.sf.ehcache.transaction.TransactionIDFactory;
import net.sf.ehcache.transaction.TransactionInterruptedException;
import net.sf.ehcache.transaction.TransactionTimeoutException;
import net.sf.ehcache.transaction.manager.TransactionManagerLookup;
import net.sf.ehcache.transaction.xa.EhcacheXAResource;
import net.sf.ehcache.transaction.xa.EhcacheXAResourceImpl;
import net.sf.ehcache.transaction.xa.XAExecutionListener;
import net.sf.ehcache.transaction.xa.XATransactionContext;
import net.sf.ehcache.transaction.xa.XaCommitOutcome;
import net.sf.ehcache.transaction.xa.XaRecoveryOutcome;
import net.sf.ehcache.transaction.xa.XaRollbackOutcome;
import net.sf.ehcache.transaction.xa.commands.StorePutCommand;
import net.sf.ehcache.transaction.xa.commands.StoreRemoveCommand;
import net.sf.ehcache.util.LargeSet;
import net.sf.ehcache.util.SetAsList;
import net.sf.ehcache.writer.CacheWriterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terracotta.statistics.observer.OperationObserver;

public class XATransactionStore
extends AbstractTransactionStore {
    private static final Logger LOG = LoggerFactory.getLogger((String)XATransactionStore.class.getName());
    private final TransactionManagerLookup transactionManagerLookup;
    private final TransactionIDFactory transactionIdFactory;
    private final ElementValueComparator comparator;
    private final SoftLockManager softLockManager;
    private final Ehcache cache;
    private final EhcacheXAResourceImpl recoveryResource;
    private final ConcurrentHashMap<Transaction, EhcacheXAResource> transactionToXAResourceMap = new ConcurrentHashMap();
    private final ConcurrentHashMap<Transaction, Long> transactionToTimeoutMap = new ConcurrentHashMap();
    private final OperationObserver<XaCommitOutcome> commitObserver = ((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)StatisticBuilder.operation(XaCommitOutcome.class).of(this)).named("xa-commit")).tag(new String[]{"xa-transactional"})).build();
    private final OperationObserver<XaRollbackOutcome> rollbackObserver = ((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)StatisticBuilder.operation(XaRollbackOutcome.class).of(this)).named("xa-rollback")).tag(new String[]{"xa-transactional"})).build();
    private final OperationObserver<XaRecoveryOutcome> recoveryObserver = ((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)StatisticBuilder.operation(XaRecoveryOutcome.class).of(this)).named("xa-recovery")).tag(new String[]{"xa-transactional"})).build();

    public XATransactionStore(TransactionManagerLookup transactionManagerLookup, SoftLockManager softLockManager, TransactionIDFactory transactionIdFactory, Ehcache cache, Store store, ElementValueComparator comparator) {
        super(store);
        this.transactionManagerLookup = transactionManagerLookup;
        this.transactionIdFactory = transactionIdFactory;
        this.comparator = comparator;
        if (transactionManagerLookup.getTransactionManager() == null) {
            throw new TransactionException("no JTA transaction manager could be located, cannot bind twopc cache with JTA");
        }
        this.softLockManager = softLockManager;
        this.cache = cache;
        this.recoveryResource = new EhcacheXAResourceImpl(cache, this.underlyingStore, transactionManagerLookup, softLockManager, transactionIdFactory, comparator, this.commitObserver, this.rollbackObserver, this.recoveryObserver);
        transactionManagerLookup.register(this.recoveryResource, true);
    }

    @Override
    public void dispose() {
        super.dispose();
        this.transactionManagerLookup.unregister(this.recoveryResource, true);
    }

    private Transaction getCurrentTransaction() throws SystemException {
        Transaction transaction = this.transactionManagerLookup.getTransactionManager().getTransaction();
        if (transaction == null) {
            throw new TransactionException("JTA transaction not started");
        }
        return transaction;
    }

    public EhcacheXAResourceImpl getOrCreateXAResource() throws SystemException {
        Transaction transaction = this.getCurrentTransaction();
        EhcacheXAResourceImpl xaResource = (EhcacheXAResourceImpl)this.transactionToXAResourceMap.get(transaction);
        if (xaResource == null) {
            LOG.debug("creating new XAResource");
            xaResource = new EhcacheXAResourceImpl(this.cache, this.underlyingStore, this.transactionManagerLookup, this.softLockManager, this.transactionIdFactory, this.comparator, this.commitObserver, this.rollbackObserver, this.recoveryObserver);
            this.transactionToXAResourceMap.put(transaction, xaResource);
            xaResource.addTwoPcExecutionListener(new CleanupXAResource(this.getCurrentTransaction()));
        }
        return xaResource;
    }

    private XATransactionContext getTransactionContext() {
        try {
            Transaction transaction = this.getCurrentTransaction();
            EhcacheXAResourceImpl xaResource = (EhcacheXAResourceImpl)this.transactionToXAResourceMap.get(transaction);
            if (xaResource == null) {
                return null;
            }
            XATransactionContext transactionContext = xaResource.getCurrentTransactionContext();
            if (transactionContext == null) {
                this.transactionManagerLookup.register(xaResource, false);
                LOG.debug("creating new XA context");
                transactionContext = xaResource.createTransactionContext();
                xaResource.addTwoPcExecutionListener(new UnregisterXAResource());
            } else {
                transactionContext = xaResource.getCurrentTransactionContext();
            }
            LOG.debug("using XA context {}", (Object)transactionContext);
            return transactionContext;
        }
        catch (SystemException e) {
            throw new TransactionException("cannot get the current transaction", e);
        }
        catch (RollbackException e) {
            throw new TransactionException("transaction rolled back", e);
        }
    }

    private XATransactionContext getOrCreateTransactionContext() {
        try {
            EhcacheXAResourceImpl xaResource = this.getOrCreateXAResource();
            XATransactionContext transactionContext = xaResource.getCurrentTransactionContext();
            if (transactionContext == null) {
                this.transactionManagerLookup.register(xaResource, false);
                LOG.debug("creating new XA context");
                transactionContext = xaResource.createTransactionContext();
                xaResource.addTwoPcExecutionListener(new UnregisterXAResource());
            } else {
                transactionContext = xaResource.getCurrentTransactionContext();
            }
            LOG.debug("using XA context {}", (Object)transactionContext);
            return transactionContext;
        }
        catch (SystemException e) {
            throw new TransactionException("cannot get the current transaction", e);
        }
        catch (RollbackException e) {
            throw new TransactionException("transaction rolled back", e);
        }
    }

    private long assertNotTimedOut() {
        try {
            if (Thread.interrupted()) {
                throw new TransactionInterruptedException("transaction interrupted");
            }
            Transaction transaction = this.getCurrentTransaction();
            Long timeoutTimestamp = this.transactionToTimeoutMap.get(transaction);
            long now = TimeUnit.MILLISECONDS.convert(System.nanoTime(), TimeUnit.NANOSECONDS);
            if (timeoutTimestamp == null) {
                long timeout;
                EhcacheXAResource xaResource = this.transactionToXAResourceMap.get(transaction);
                if (xaResource != null) {
                    int xaResourceTimeout = xaResource.getTransactionTimeout();
                    timeout = TimeUnit.MILLISECONDS.convert(xaResourceTimeout, TimeUnit.SECONDS);
                } else {
                    int defaultTransactionTimeout = this.cache.getCacheManager().getTransactionController().getDefaultTransactionTimeout();
                    timeout = TimeUnit.MILLISECONDS.convert(defaultTransactionTimeout, TimeUnit.SECONDS);
                }
                timeoutTimestamp = now + timeout;
                this.transactionToTimeoutMap.put(transaction, timeoutTimestamp);
                try {
                    transaction.registerSynchronization((Synchronization)new CleanupTimeout(transaction));
                }
                catch (RollbackException e) {
                    throw new TransactionException("transaction has been marked as rollback only", e);
                }
                return timeout;
            }
            long timeToExpiry = timeoutTimestamp - now;
            if (timeToExpiry <= 0L) {
                throw new TransactionTimeoutException("transaction timed out");
            }
            return timeToExpiry;
        }
        catch (SystemException e) {
            throw new TransactionException("cannot get the current transaction", e);
        }
        catch (XAException e) {
            throw new TransactionException("cannot get the XAResource transaction timeout", e);
        }
    }

    @Override
    public Element get(Object key) {
        Element element;
        LOG.debug("cache {} get {}", (Object)this.cache.getName(), key);
        XATransactionContext context = this.getTransactionContext();
        if (context == null) {
            element = this.getFromUnderlyingStore(key);
        } else {
            element = context.get(key);
            if (element == null && !context.isRemoved(key)) {
                element = this.getFromUnderlyingStore(key);
            }
        }
        return element;
    }

    @Override
    public Element getQuiet(Object key) {
        Element element;
        LOG.debug("cache {} getQuiet {}", (Object)this.cache.getName(), key);
        XATransactionContext context = this.getTransactionContext();
        if (context == null) {
            element = this.getQuietFromUnderlyingStore(key);
        } else {
            element = context.get(key);
            if (element == null && !context.isRemoved(key)) {
                element = this.getQuietFromUnderlyingStore(key);
            }
        }
        return element;
    }

    @Override
    public int getSize() {
        LOG.debug("cache {} getSize", (Object)this.cache.getName());
        XATransactionContext context = this.getOrCreateTransactionContext();
        int size = this.underlyingStore.getSize();
        return Math.max(0, size + context.getSizeModifier());
    }

    @Override
    public int getTerracottaClusteredSize() {
        try {
            Transaction transaction = this.transactionManagerLookup.getTransactionManager().getTransaction();
            if (transaction == null) {
                return this.underlyingStore.getTerracottaClusteredSize();
            }
        }
        catch (SystemException se) {
            throw new TransactionException("cannot get the current transaction", se);
        }
        LOG.debug("cache {} getTerracottaClusteredSize", (Object)this.cache.getName());
        XATransactionContext context = this.getOrCreateTransactionContext();
        int size = this.underlyingStore.getTerracottaClusteredSize();
        return size + context.getSizeModifier();
    }

    @Override
    public boolean containsKey(Object key) {
        LOG.debug("cache {} containsKey", (Object)this.cache.getName(), key);
        XATransactionContext context = this.getOrCreateTransactionContext();
        return !context.isRemoved(key) && (context.getAddedKeys().contains(key) || this.underlyingStore.containsKey(key));
    }

    @Override
    public List getKeys() {
        LOG.debug("cache {} getKeys", (Object)this.cache.getName());
        XATransactionContext context = this.getOrCreateTransactionContext();
        LargeSet<Object> keys = new LargeSet<Object>(){

            @Override
            public int sourceSize() {
                return XATransactionStore.this.underlyingStore.getSize();
            }

            @Override
            public Iterator<Object> sourceIterator() {
                return XATransactionStore.this.underlyingStore.getKeys().iterator();
            }
        };
        keys.addAll(context.getAddedKeys());
        keys.removeAll(context.getRemovedKeys());
        return new SetAsList<Object>(keys);
    }

    private Element getFromUnderlyingStore(Object key) {
        Element element;
        while (true) {
            long timeLeft = this.assertNotTimedOut();
            LOG.debug("cache {} underlying.get key {} not timed out, time left: " + timeLeft, (Object)this.cache.getName(), key);
            element = this.underlyingStore.get(key);
            if (element == null) {
                return null;
            }
            Object value = element.getObjectValue();
            if (!(value instanceof SoftLockID)) break;
            SoftLockID softLockId = (SoftLockID)value;
            SoftLock softLock = this.softLockManager.findSoftLockById(softLockId);
            if (softLock == null) {
                LOG.debug("cache {} underlying.get key {} soft lock died, retrying...", (Object)this.cache.getName(), key);
                continue;
            }
            try {
                LOG.debug("cache {} key {} soft locked, awaiting unlock...", (Object)this.cache.getName(), key);
                if (!softLock.tryLock(timeLeft)) continue;
                softLock.clearTryLock();
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        return element;
    }

    private Element getQuietFromUnderlyingStore(Object key) {
        Element element;
        while (true) {
            long timeLeft = this.assertNotTimedOut();
            LOG.debug("cache {} underlying.getQuiet key {} not timed out, time left: " + timeLeft, (Object)this.cache.getName(), key);
            element = this.underlyingStore.getQuiet(key);
            if (element == null) {
                return null;
            }
            Object value = element.getObjectValue();
            if (!(value instanceof SoftLockID)) break;
            SoftLockID softLockId = (SoftLockID)value;
            SoftLock softLock = this.softLockManager.findSoftLockById(softLockId);
            if (softLock == null) {
                LOG.debug("cache {} underlying.getQuiet key {} soft lock died, retrying...", (Object)this.cache.getName(), key);
                continue;
            }
            try {
                LOG.debug("cache {} key {} soft locked, awaiting unlock...", (Object)this.cache.getName(), key);
                if (!softLock.tryLock(timeLeft)) continue;
                softLock.clearTryLock();
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        return element;
    }

    private Element getCurrentElement(Object key, XATransactionContext context) {
        Element previous = context.get(key);
        if (previous == null && !context.isRemoved(key)) {
            previous = this.getQuietFromUnderlyingStore(key);
        }
        return previous;
    }

    @Override
    public boolean put(Element element) throws CacheException {
        LOG.debug("cache {} put {}", (Object)this.cache.getName(), (Object)element);
        this.getOrCreateTransactionContext();
        Element oldElement = this.getQuietFromUnderlyingStore(element.getObjectKey());
        return this.internalPut(new StorePutCommand(oldElement, element));
    }

    @Override
    public boolean putWithWriter(Element element, CacheWriterManager writerManager) throws CacheException {
        LOG.debug("cache {} putWithWriter {}", (Object)this.cache.getName(), (Object)element);
        this.getOrCreateTransactionContext();
        Element oldElement = this.getQuietFromUnderlyingStore(element.getObjectKey());
        if (writerManager != null) {
            writerManager.put(element);
        } else {
            this.cache.getWriterManager().put(element);
        }
        return this.internalPut(new StorePutCommand(oldElement, element));
    }

    private boolean internalPut(StorePutCommand putCommand) {
        boolean isNull;
        Element element = putCommand.getElement();
        if (element == null) {
            return true;
        }
        XATransactionContext context = this.getOrCreateTransactionContext();
        boolean bl = isNull = this.underlyingStore.get(element.getKey()) == null;
        if (isNull) {
            isNull = context.get(element.getKey()) == null;
        }
        context.addCommand(putCommand, element);
        return isNull;
    }

    @Override
    public Element remove(Object key) {
        LOG.debug("cache {} remove {}", (Object)this.cache.getName(), key);
        this.getOrCreateTransactionContext();
        Element oldElement = this.getQuietFromUnderlyingStore(key);
        return this.removeInternal(new StoreRemoveCommand(key, oldElement));
    }

    private Element removeInternal(StoreRemoveCommand command) {
        Element element = command.getEntry().getElement();
        this.getOrCreateTransactionContext().addCommand(command, element);
        return element;
    }

    @Override
    public Element removeWithWriter(Object key, CacheWriterManager writerManager) throws CacheException {
        LOG.debug("cache {} removeWithWriter {}", (Object)this.cache.getName(), key);
        this.getOrCreateTransactionContext();
        Element oldElement = this.getQuietFromUnderlyingStore(key);
        if (writerManager != null) {
            writerManager.remove(new CacheEntry(key, null));
        } else {
            this.cache.getWriterManager().remove(new CacheEntry(key, null));
        }
        return this.removeInternal(new StoreRemoveCommand(key, oldElement));
    }

    @Override
    public void removeAll() throws CacheException {
        LOG.debug("cache {} removeAll", (Object)this.cache.getName());
        List keys = this.getKeys();
        for (Object key : keys) {
            this.remove(key);
        }
    }

    @Override
    public Element putIfAbsent(Element element) throws NullPointerException {
        LOG.debug("cache {} putIfAbsent {}", (Object)this.cache.getName(), (Object)element);
        XATransactionContext context = this.getOrCreateTransactionContext();
        Element previous = this.getCurrentElement(element.getObjectKey(), context);
        if (previous == null) {
            Element oldElement = this.getQuietFromUnderlyingStore(element.getObjectKey());
            context.addCommand(new StorePutCommand(oldElement, element), element);
        }
        return previous;
    }

    @Override
    public Element removeElement(Element element, ElementValueComparator comparator) throws NullPointerException {
        LOG.debug("cache {} removeElement {}", (Object)this.cache.getName(), (Object)element);
        XATransactionContext context = this.getOrCreateTransactionContext();
        Element previous = this.getCurrentElement(element.getKey(), context);
        if (previous != null && comparator.equals(previous, element)) {
            Element oldElement = this.getQuietFromUnderlyingStore(element.getObjectKey());
            context.addCommand(new StoreRemoveCommand(element.getObjectKey(), oldElement), element);
            return previous;
        }
        return null;
    }

    @Override
    public boolean replace(Element old, Element element, ElementValueComparator comparator) throws NullPointerException, IllegalArgumentException {
        LOG.debug("cache {} replace2 {}", (Object)this.cache.getName(), (Object)element);
        XATransactionContext context = this.getOrCreateTransactionContext();
        Element previous = this.getCurrentElement(element.getKey(), context);
        boolean replaced = false;
        if (previous != null && comparator.equals(previous, old)) {
            Element oldElement = this.getQuietFromUnderlyingStore(element.getObjectKey());
            context.addCommand(new StorePutCommand(oldElement, element), element);
            replaced = true;
        }
        return replaced;
    }

    @Override
    public Element replace(Element element) throws NullPointerException {
        LOG.debug("cache {} replace1 {}", (Object)this.cache.getName(), (Object)element);
        XATransactionContext context = this.getOrCreateTransactionContext();
        Element previous = this.getCurrentElement(element.getKey(), context);
        if (previous != null) {
            Element oldElement = this.getQuietFromUnderlyingStore(element.getObjectKey());
            context.addCommand(new StorePutCommand(oldElement, element), element);
        }
        return previous;
    }

    private final class UnregisterXAResource
    implements XAExecutionListener {
        private UnregisterXAResource() {
        }

        @Override
        public void beforePrepare(EhcacheXAResource xaResource) {
        }

        @Override
        public void afterCommitOrRollback(EhcacheXAResource xaResource) {
            XATransactionStore.this.transactionManagerLookup.unregister(xaResource, false);
        }
    }

    private final class CleanupXAResource
    implements XAExecutionListener {
        private final Transaction transaction;

        private CleanupXAResource(Transaction transaction) {
            this.transaction = transaction;
        }

        @Override
        public void beforePrepare(EhcacheXAResource xaResource) {
        }

        @Override
        public void afterCommitOrRollback(EhcacheXAResource xaResource) {
            XATransactionStore.this.transactionToXAResourceMap.remove(this.transaction);
        }
    }

    private final class CleanupTimeout
    implements Synchronization {
        private final Transaction transaction;

        private CleanupTimeout(Transaction transaction) {
            this.transaction = transaction;
        }

        public void beforeCompletion() {
        }

        public void afterCompletion(int status) {
            XATransactionStore.this.transactionToTimeoutMap.remove(this.transaction);
        }
    }
}

