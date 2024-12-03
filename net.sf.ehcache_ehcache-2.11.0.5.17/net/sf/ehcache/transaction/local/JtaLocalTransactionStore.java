/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.transaction.RollbackException
 *  javax.transaction.Synchronization
 *  javax.transaction.SystemException
 *  javax.transaction.Transaction
 *  javax.transaction.TransactionManager
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.sf.ehcache.transaction.local;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.transaction.RollbackException;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import net.sf.ehcache.CacheEntry;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.TransactionController;
import net.sf.ehcache.store.ElementValueComparator;
import net.sf.ehcache.transaction.AbstractTransactionStore;
import net.sf.ehcache.transaction.TransactionException;
import net.sf.ehcache.transaction.TransactionID;
import net.sf.ehcache.transaction.local.LocalTransactionStore;
import net.sf.ehcache.transaction.manager.TransactionManagerLookup;
import net.sf.ehcache.transaction.xa.EhcacheXAResource;
import net.sf.ehcache.transaction.xa.XAExecutionListener;
import net.sf.ehcache.transaction.xa.XATransactionContext;
import net.sf.ehcache.util.lang.VicariousThreadLocal;
import net.sf.ehcache.writer.CacheWriterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JtaLocalTransactionStore
extends AbstractTransactionStore {
    private static final Logger LOG = LoggerFactory.getLogger((String)JtaLocalTransactionStore.class.getName());
    private static final String ALTERNATIVE_TERMINATION_MODE_SYS_PROPERTY_NAME = "net.sf.ehcache.transaction.xa.alternativeTerminationMode";
    private static final AtomicBoolean ATOMIKOS_WARNING_ISSUED = new AtomicBoolean(false);
    private static final VicariousThreadLocal<Transaction> BOUND_JTA_TRANSACTIONS = new VicariousThreadLocal();
    private final TransactionManagerLookup transactionManagerLookup;
    private final TransactionController transactionController;
    private final TransactionManager transactionManager;
    private final Ehcache cache;

    public JtaLocalTransactionStore(LocalTransactionStore underlyingStore, TransactionManagerLookup transactionManagerLookup, TransactionController transactionController) {
        super(underlyingStore);
        this.transactionManagerLookup = transactionManagerLookup;
        this.transactionController = transactionController;
        this.transactionManager = transactionManagerLookup.getTransactionManager();
        if (this.transactionManager == null) {
            throw new TransactionException("no JTA transaction manager could be located");
        }
        this.cache = underlyingStore.getCache();
        if (this.transactionManager.getClass().getName().contains("atomikos")) {
            System.setProperty(ALTERNATIVE_TERMINATION_MODE_SYS_PROPERTY_NAME, "true");
            if (ATOMIKOS_WARNING_ISSUED.compareAndSet(false, true)) {
                LOG.warn("Atomikos transaction manager detected, make sure you configured com.atomikos.icatch.threaded_2pc=false");
            }
        }
    }

    private void registerInJtaContext() {
        try {
            if (this.transactionController.getCurrentTransactionContext() != null) {
                Transaction tx = this.transactionManager.getTransaction();
                if (!BOUND_JTA_TRANSACTIONS.get().equals(tx)) {
                    throw new TransactionException("Invalid JTA transaction context, cache was first used in transaction [" + BOUND_JTA_TRANSACTIONS.get() + "] but is now used in transaction [" + tx + "].");
                }
            } else {
                Transaction tx = this.transactionManager.getTransaction();
                if (tx == null) {
                    throw new TransactionException("no JTA transaction context started, xa caches cannot be used outside of JTA transactions");
                }
                BOUND_JTA_TRANSACTIONS.set(tx);
                this.transactionController.begin();
                if (Boolean.getBoolean(ALTERNATIVE_TERMINATION_MODE_SYS_PROPERTY_NAME)) {
                    JtaLocalEhcacheXAResource xaRes = new JtaLocalEhcacheXAResource(this.transactionController, this.transactionController.getCurrentTransactionContext().getTransactionId(), this.transactionManagerLookup);
                    this.transactionManagerLookup.register(xaRes, false);
                    tx.enlistResource((XAResource)xaRes);
                } else {
                    tx.registerSynchronization((Synchronization)new JtaLocalEhcacheSynchronization(this.transactionController, this.transactionController.getCurrentTransactionContext().getTransactionId()));
                }
            }
        }
        catch (SystemException e) {
            throw new TransactionException("internal JTA transaction manager error, cannot bind xa cache with it", e);
        }
        catch (RollbackException e) {
            throw new TransactionException("JTA transaction rolled back, cannot bind xa cache with it", e);
        }
    }

    private void setRollbackOnly() {
        try {
            BOUND_JTA_TRANSACTIONS.get().setRollbackOnly();
            this.transactionController.setRollbackOnly();
        }
        catch (SystemException e) {
            LOG.warn("internal JTA transaction manager error", (Throwable)e);
        }
    }

    @Override
    public Element getOldElement(Object key) {
        return ((AbstractTransactionStore)this.underlyingStore).getOldElement(key);
    }

    @Override
    public boolean put(Element element) throws CacheException {
        this.registerInJtaContext();
        try {
            return this.underlyingStore.put(element);
        }
        catch (CacheException e) {
            this.setRollbackOnly();
            throw e;
        }
    }

    @Override
    public void putAll(Collection<Element> elements) throws CacheException {
        this.registerInJtaContext();
        try {
            this.underlyingStore.putAll(elements);
        }
        catch (CacheException e) {
            this.setRollbackOnly();
            throw e;
        }
    }

    @Override
    public boolean putWithWriter(final Element element, final CacheWriterManager writerManager) throws CacheException {
        this.registerInJtaContext();
        try {
            boolean put = this.underlyingStore.put(element);
            this.transactionManager.getTransaction().registerSynchronization(new Synchronization(){

                public void beforeCompletion() {
                    if (writerManager != null) {
                        writerManager.put(element);
                    } else {
                        JtaLocalTransactionStore.this.cache.getWriterManager().put(element);
                    }
                }

                public void afterCompletion(int status) {
                }
            });
            return put;
        }
        catch (CacheException e) {
            this.setRollbackOnly();
            throw e;
        }
        catch (RollbackException e) {
            throw new TransactionException("error registering writer synchronization", e);
        }
        catch (SystemException e) {
            throw new TransactionException("error registering writer synchronization", e);
        }
    }

    @Override
    public Element get(Object key) {
        this.registerInJtaContext();
        try {
            return this.underlyingStore.get(key);
        }
        catch (CacheException e) {
            this.setRollbackOnly();
            throw e;
        }
    }

    @Override
    public Element getQuiet(Object key) {
        this.registerInJtaContext();
        try {
            return this.underlyingStore.getQuiet(key);
        }
        catch (CacheException e) {
            this.setRollbackOnly();
            throw e;
        }
    }

    @Override
    public List getKeys() {
        this.registerInJtaContext();
        try {
            return this.underlyingStore.getKeys();
        }
        catch (CacheException e) {
            this.setRollbackOnly();
            throw e;
        }
    }

    @Override
    public Element remove(Object key) {
        this.registerInJtaContext();
        try {
            return this.underlyingStore.remove(key);
        }
        catch (CacheException e) {
            this.setRollbackOnly();
            throw e;
        }
    }

    @Override
    public void removeAll(Collection<?> keys) {
        this.registerInJtaContext();
        try {
            this.underlyingStore.removeAll(keys);
        }
        catch (CacheException e) {
            this.setRollbackOnly();
            throw e;
        }
    }

    @Override
    public Element removeWithWriter(Object key, final CacheWriterManager writerManager) throws CacheException {
        this.registerInJtaContext();
        try {
            Element removed = this.underlyingStore.remove(key);
            final CacheEntry cacheEntry = new CacheEntry(key, this.getQuiet(key));
            this.transactionManager.getTransaction().registerSynchronization(new Synchronization(){

                public void beforeCompletion() {
                    if (writerManager != null) {
                        writerManager.remove(cacheEntry);
                    } else {
                        JtaLocalTransactionStore.this.cache.getWriterManager().remove(cacheEntry);
                    }
                }

                public void afterCompletion(int status) {
                }
            });
            return removed;
        }
        catch (CacheException e) {
            this.setRollbackOnly();
            throw e;
        }
        catch (RollbackException e) {
            throw new TransactionException("error registering writer synchronization", e);
        }
        catch (SystemException e) {
            throw new TransactionException("error registering writer synchronization", e);
        }
    }

    @Override
    public void removeAll() throws CacheException {
        this.registerInJtaContext();
        try {
            this.underlyingStore.removeAll();
        }
        catch (CacheException e) {
            this.setRollbackOnly();
            throw e;
        }
    }

    @Override
    public Element putIfAbsent(Element element) throws NullPointerException {
        this.registerInJtaContext();
        try {
            return this.underlyingStore.putIfAbsent(element);
        }
        catch (CacheException e) {
            this.setRollbackOnly();
            throw e;
        }
    }

    @Override
    public Element removeElement(Element element, ElementValueComparator comparator) throws NullPointerException {
        this.registerInJtaContext();
        try {
            return this.underlyingStore.removeElement(element, comparator);
        }
        catch (CacheException e) {
            this.setRollbackOnly();
            throw e;
        }
    }

    @Override
    public boolean replace(Element old, Element element, ElementValueComparator comparator) throws NullPointerException, IllegalArgumentException {
        this.registerInJtaContext();
        try {
            return this.underlyingStore.replace(old, element, comparator);
        }
        catch (CacheException e) {
            this.setRollbackOnly();
            throw e;
        }
    }

    @Override
    public Element replace(Element element) throws NullPointerException {
        this.registerInJtaContext();
        try {
            return this.underlyingStore.replace(element);
        }
        catch (CacheException e) {
            this.setRollbackOnly();
            throw e;
        }
    }

    @Override
    public int getSize() {
        this.registerInJtaContext();
        try {
            return this.underlyingStore.getSize();
        }
        catch (CacheException e) {
            this.setRollbackOnly();
            throw e;
        }
    }

    @Override
    public int getTerracottaClusteredSize() {
        if (this.transactionController.getCurrentTransactionContext() == null) {
            return this.underlyingStore.getTerracottaClusteredSize();
        }
        this.registerInJtaContext();
        try {
            return this.underlyingStore.getTerracottaClusteredSize();
        }
        catch (CacheException e) {
            this.setRollbackOnly();
            throw e;
        }
    }

    @Override
    public boolean containsKey(Object key) {
        this.registerInJtaContext();
        try {
            return this.underlyingStore.containsKey(key);
        }
        catch (CacheException e) {
            this.setRollbackOnly();
            throw e;
        }
    }

    private static final class JtaLocalEhcacheXAResource
    implements EhcacheXAResource {
        private final TransactionController transactionController;
        private final TransactionID transactionId;
        private final TransactionManagerLookup transactionManagerLookup;

        private JtaLocalEhcacheXAResource(TransactionController transactionController, TransactionID transactionId, TransactionManagerLookup transactionManagerLookup) {
            this.transactionController = transactionController;
            this.transactionId = transactionId;
            this.transactionManagerLookup = transactionManagerLookup;
        }

        @Override
        public void commit(Xid xid, boolean onePhase) throws XAException {
            this.transactionController.commit(true);
            BOUND_JTA_TRANSACTIONS.remove();
            this.transactionManagerLookup.unregister(this, false);
        }

        @Override
        public void end(Xid xid, int flag) throws XAException {
        }

        @Override
        public void forget(Xid xid) throws XAException {
        }

        @Override
        public int getTransactionTimeout() throws XAException {
            return 0;
        }

        @Override
        public boolean isSameRM(XAResource xaResource) throws XAException {
            return xaResource == this;
        }

        @Override
        public int prepare(Xid xid) throws XAException {
            return 0;
        }

        @Override
        public Xid[] recover(int flags) throws XAException {
            return new Xid[0];
        }

        @Override
        public void rollback(Xid xid) throws XAException {
            this.transactionController.rollback();
            BOUND_JTA_TRANSACTIONS.remove();
            this.transactionManagerLookup.unregister(this, false);
        }

        @Override
        public boolean setTransactionTimeout(int timeout) throws XAException {
            return false;
        }

        @Override
        public void start(Xid xid, int flag) throws XAException {
        }

        @Override
        public void addTwoPcExecutionListener(XAExecutionListener listener) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getCacheName() {
            return this.transactionId.toString();
        }

        @Override
        public XATransactionContext createTransactionContext() throws SystemException, RollbackException {
            throw new UnsupportedOperationException();
        }

        @Override
        public XATransactionContext getCurrentTransactionContext() {
            throw new UnsupportedOperationException();
        }

        public String toString() {
            return "JtaLocalEhcacheXAResource of transaction [" + this.transactionId + "]";
        }
    }

    private static final class JtaLocalEhcacheSynchronization
    implements Synchronization {
        private final TransactionController transactionController;
        private final TransactionID transactionId;

        private JtaLocalEhcacheSynchronization(TransactionController transactionController, TransactionID transactionId) {
            this.transactionController = transactionController;
            this.transactionId = transactionId;
        }

        public void beforeCompletion() {
        }

        public void afterCompletion(int status) {
            BOUND_JTA_TRANSACTIONS.remove();
            if (status == 3) {
                this.transactionController.commit(true);
            } else if (status == 4) {
                this.transactionController.rollback();
            } else {
                this.transactionController.rollback();
                LOG.warn("The transaction manager reported UNKNOWN transaction status upon termination. The ehcache transaction has been rolled back!");
            }
        }

        public String toString() {
            return "JtaLocalEhcacheSynchronization of transaction [" + this.transactionId + "]";
        }
    }
}

