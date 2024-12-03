/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.jcr.transaction;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import javax.jcr.Item;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import org.apache.jackrabbit.util.Text;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.jcr.JcrDavException;
import org.apache.jackrabbit.webdav.jcr.JcrDavSession;
import org.apache.jackrabbit.webdav.jcr.transaction.TransactionListener;
import org.apache.jackrabbit.webdav.lock.ActiveLock;
import org.apache.jackrabbit.webdav.lock.LockInfo;
import org.apache.jackrabbit.webdav.lock.Scope;
import org.apache.jackrabbit.webdav.lock.Type;
import org.apache.jackrabbit.webdav.transaction.TransactionConstants;
import org.apache.jackrabbit.webdav.transaction.TransactionInfo;
import org.apache.jackrabbit.webdav.transaction.TransactionResource;
import org.apache.jackrabbit.webdav.transaction.TxActiveLock;
import org.apache.jackrabbit.webdav.transaction.TxLockManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TxLockManagerImpl
implements TxLockManager {
    private static Logger log = LoggerFactory.getLogger(TxLockManagerImpl.class);
    private final TransactionMap map = new TransactionMap();
    private final Map<TransactionListener, TransactionListener> listeners = new IdentityHashMap<TransactionListener, TransactionListener>();

    @Override
    public ActiveLock createLock(LockInfo lockInfo, DavResource resource) throws DavException {
        if (resource == null || !(resource instanceof TransactionResource)) {
            throw new IllegalArgumentException("Invalid resource");
        }
        return this.createLock(lockInfo, (TransactionResource)resource);
    }

    private synchronized ActiveLock createLock(LockInfo lockInfo, TransactionResource resource) throws DavException {
        if (!lockInfo.isDeep() || !TransactionConstants.TRANSACTION.equals(lockInfo.getType())) {
            throw new DavException(412);
        }
        ActiveLock existing = this.getLock(lockInfo.getType(), lockInfo.getScope(), resource);
        if (existing != null) {
            throw new DavException(423);
        }
        Transaction tx = this.createTransaction(resource.getLocator(), lockInfo);
        tx.start(resource);
        TxLockManagerImpl.addReferences(tx, this.getMap(resource), resource);
        return tx.getLock();
    }

    private Transaction createTransaction(DavResourceLocator locator, LockInfo lockInfo) {
        if (TransactionConstants.GLOBAL.equals(lockInfo.getScope())) {
            return new GlobalTransaction(locator, new TxActiveLock(lockInfo));
        }
        return new LocalTransaction(locator, new TxActiveLock(lockInfo));
    }

    @Override
    public ActiveLock refreshLock(LockInfo lockInfo, String lockToken, DavResource resource) throws DavException {
        if (resource == null || !(resource instanceof TransactionResource)) {
            throw new IllegalArgumentException("Invalid resource");
        }
        return this.refreshLock(lockInfo, lockToken, (TransactionResource)resource);
    }

    private synchronized ActiveLock refreshLock(LockInfo lockInfo, String lockToken, TransactionResource resource) throws DavException {
        TransactionMap responsibleMap = this.getMap(resource);
        Transaction tx = responsibleMap.get(lockToken);
        if (tx == null) {
            throw new DavException(412, "No valid transaction lock found for resource '" + resource.getResourcePath() + "'");
        }
        if (tx.getLock().isExpired()) {
            TxLockManagerImpl.removeExpired(tx, responsibleMap, resource);
            throw new DavException(412, "Transaction lock for resource '" + resource.getResourcePath() + "' was already expired.");
        }
        tx.getLock().setTimeout(lockInfo.getTimeout());
        return tx.getLock();
    }

    @Override
    public void releaseLock(String lockToken, DavResource resource) throws DavException {
        throw new UnsupportedOperationException("A transaction lock can only be release with a TransactionInfo object and a lock token.");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public synchronized void releaseLock(TransactionInfo lockInfo, String lockToken, TransactionResource resource) throws DavException {
        if (resource == null) {
            throw new IllegalArgumentException("Resource must not be null.");
        }
        TransactionMap responsibleMap = this.getMap(resource);
        Transaction tx = responsibleMap.get(lockToken);
        if (tx == null) {
            throw new DavException(412, "No transaction lock found for resource '" + resource.getResourcePath() + "'");
        }
        if (tx.getLock().isExpired()) {
            TxLockManagerImpl.removeExpired(tx, responsibleMap, resource);
            throw new DavException(412, "Transaction lock for resource '" + resource.getResourcePath() + "' was already expired.");
        }
        if (lockInfo.isCommit()) {
            TransactionListener[] transactionListenerArray = this.listeners;
            synchronized (this.listeners) {
                TransactionListener[] txListeners = this.listeners.values().toArray(new TransactionListener[this.listeners.values().size()]);
                // ** MonitorExit[var7_6] (shouldn't be in output)
                for (TransactionListener txListener : txListeners) {
                    txListener.beforeCommit(resource, lockToken);
                }
                DavException ex = null;
                try {
                    tx.commit(resource);
                }
                catch (DavException e) {
                    ex = e;
                }
                for (TransactionListener txListener : txListeners) {
                    txListener.afterCommit(resource, lockToken, ex == null);
                }
                if (ex != null) {
                    throw ex;
                }
            }
        } else {
            tx.rollback(resource);
        }
        {
            TxLockManagerImpl.removeReferences(tx, responsibleMap, resource);
            return;
        }
    }

    @Override
    public ActiveLock getLock(Type type, Scope scope, DavResource resource) {
        return null;
    }

    @Override
    public boolean hasLock(String token, DavResource resource) {
        return this.getLock(token, null, resource) != null;
    }

    @Override
    public ActiveLock getLock(Type type, Scope scope, TransactionResource resource) {
        ActiveLock lock = null;
        if (TransactionConstants.TRANSACTION.equals(type)) {
            String[] sessionTokens = resource.getSession().getLockTokens();
            for (int i = 0; lock == null && i < sessionTokens.length; ++i) {
                String lockToken = sessionTokens[i];
                lock = this.getLock(lockToken, scope, (DavResource)resource);
            }
        }
        return lock;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addTransactionListener(TransactionListener listener) {
        Map<TransactionListener, TransactionListener> map = this.listeners;
        synchronized (map) {
            this.listeners.put(listener, listener);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeTransactionListener(TransactionListener listener) {
        Map<TransactionListener, TransactionListener> map = this.listeners;
        synchronized (map) {
            this.listeners.remove(listener);
        }
    }

    private ActiveLock getLock(String lockToken, Scope scope, DavResource resource) {
        if (!(resource instanceof TransactionResource)) {
            log.warn("TransactionResource expected");
            return null;
        }
        TxActiveLock lock = null;
        Transaction tx = null;
        TransactionMap m = this.map;
        if (m.containsKey(lockToken)) {
            tx = m.get(lockToken);
        } else {
            Iterator it = m.values().iterator();
            while (it.hasNext() && tx == null) {
                Transaction txMap = (Transaction)it.next();
                if (txMap.isLocal() || !(m = (TransactionMap)((Object)txMap)).containsKey(lockToken)) continue;
                tx = ((TransactionMap)((Object)txMap)).get(lockToken);
            }
        }
        if (tx != null) {
            if (tx.getLock().isExpired()) {
                TxLockManagerImpl.removeExpired(tx, m, (TransactionResource)resource);
            } else if (tx.appliesToResource(resource) && (scope == null || tx.getLock().getScope().equals(scope))) {
                lock = tx.getLock();
            }
        }
        return lock;
    }

    private TransactionMap getMap(TransactionResource resource) throws DavException {
        String txKey = resource.getTransactionId();
        if (txKey == null) {
            return this.map;
        }
        if (!this.map.containsKey(txKey)) {
            throw new DavException(412, "Transaction map '" + this.map + " does not contain a transaction with TransactionId '" + txKey + "'.");
        }
        Transaction tx = this.map.get(txKey);
        if (tx.isLocal()) {
            throw new DavException(412, "TransactionId '" + txKey + "' points to a local transaction, that cannot act as transaction map");
        }
        if (tx.getLock() != null && tx.getLock().isExpired()) {
            TxLockManagerImpl.removeExpired(tx, this.map, resource);
            throw new DavException(412, "Attempt to retrieve an expired global transaction.");
        }
        return (TransactionMap)((Object)tx);
    }

    private static void removeExpired(Transaction tx, TransactionMap responsibleMap, TransactionResource resource) {
        log.debug("Removing expired transaction lock " + tx);
        try {
            tx.rollback(resource);
            TxLockManagerImpl.removeReferences(tx, responsibleMap, resource);
        }
        catch (DavException e) {
            log.error("Error while removing expired transaction lock: " + e.getMessage());
        }
    }

    private static void addReferences(Transaction tx, TransactionMap responsibleMap, TransactionResource resource) {
        log.debug("Adding transactionId '" + tx.getId() + "' as session lock token.");
        resource.getSession().addLockToken(tx.getId());
        responsibleMap.put(tx.getId(), tx);
        resource.getSession().addReference(tx.getId());
    }

    private static void removeReferences(Transaction tx, TransactionMap responsibleMap, TransactionResource resource) {
        log.debug("Removing transactionId '" + tx.getId() + "' from session lock tokens.");
        resource.getSession().removeLockToken(tx.getId());
        responsibleMap.remove(tx.getId());
        resource.getSession().removeReference(tx.getId());
    }

    private static Session getRepositorySession(TransactionResource resource) throws DavException {
        return JcrDavSession.getRepositorySession(resource.getSession());
    }

    private static class XidImpl
    implements Xid {
        private final String id;

        private XidImpl(String id) {
            this.id = id;
        }

        @Override
        public int getFormatId() {
            return 1;
        }

        @Override
        public byte[] getBranchQualifier() {
            return new byte[0];
        }

        @Override
        public byte[] getGlobalTransactionId() {
            return this.id.getBytes();
        }
    }

    private static class TransactionMap
    extends HashMap<String, Transaction> {
        private TransactionMap() {
        }

        public Transaction get(String key) {
            Transaction tx = null;
            if (this.containsKey(key)) {
                tx = (Transaction)super.get(key);
            }
            return tx;
        }

        public Transaction putTransaction(String key, Transaction value) throws DavException {
            return super.put(key, value);
        }
    }

    private static class GlobalTransaction
    extends AbstractTransaction {
        private Xid xid;

        private GlobalTransaction(DavResourceLocator locator, TxActiveLock lock) {
            super(locator, lock);
            this.xid = new XidImpl(lock.getToken());
        }

        @Override
        public boolean isLocal() {
            return false;
        }

        @Override
        public void start(TransactionResource resource) throws DavException {
            XAResource xaRes = this.getXAResource(resource);
            try {
                xaRes.setTransactionTimeout((int)this.getLock().getTimeout() / 1000);
                xaRes.start(this.xid, 0);
            }
            catch (XAException e) {
                throw new DavException(403, e.getMessage());
            }
        }

        @Override
        public void commit(TransactionResource resource) throws DavException {
            XAResource xaRes = this.getXAResource(resource);
            try {
                xaRes.commit(this.xid, false);
                this.removeLocalTxReferences(resource);
            }
            catch (XAException e) {
                throw new DavException(403, e.getMessage());
            }
        }

        @Override
        public void rollback(TransactionResource resource) throws DavException {
            XAResource xaRes = this.getXAResource(resource);
            try {
                xaRes.rollback(this.xid);
                this.removeLocalTxReferences(resource);
            }
            catch (XAException e) {
                throw new DavException(403, e.getMessage());
            }
        }

        @Override
        public Transaction putTransaction(String key, Transaction value) throws DavException {
            if (!(value instanceof LocalTransaction)) {
                throw new DavException(412, "Attempt to nest global transaction into a global one.");
            }
            return super.put(key, value);
        }

        private XAResource getXAResource(TransactionResource resource) throws DavException {
            throw new DavException(403);
        }

        private void removeLocalTxReferences(TransactionResource resource) {
            for (Object o : this.values()) {
                Transaction tx = (Transaction)o;
                TxLockManagerImpl.removeReferences(tx, this, resource);
            }
        }
    }

    private static final class LocalTransaction
    extends AbstractTransaction {
        private LocalTransaction(DavResourceLocator locator, TxActiveLock lock) {
            super(locator, lock);
        }

        @Override
        public boolean isLocal() {
            return true;
        }

        @Override
        public void start(TransactionResource resource) throws DavException {
            try {
                if (!TxLockManagerImpl.getRepositorySession(resource).itemExists(resource.getLocator().getRepositoryPath())) {
                    throw new DavException(409, "Unable to start local transaction: no repository item present at " + this.getResourcePath());
                }
            }
            catch (RepositoryException e) {
                log.error("Unexpected error: " + e.getMessage());
                throw new JcrDavException(e);
            }
        }

        @Override
        public void commit(TransactionResource resource) throws DavException {
            try {
                this.getItem(resource).save();
            }
            catch (RepositoryException e) {
                throw new JcrDavException(e);
            }
        }

        @Override
        public void rollback(TransactionResource resource) throws DavException {
            try {
                this.getItem(resource).refresh(false);
            }
            catch (RepositoryException e) {
                throw new JcrDavException(e);
            }
        }

        @Override
        public Transaction putTransaction(String key, Transaction value) throws DavException {
            throw new DavException(412, "Attempt to nest a new transaction into a local one.");
        }

        private Item getItem(TransactionResource resource) throws PathNotFoundException, RepositoryException, DavException {
            String itemPath = resource.getLocator().getRepositoryPath();
            return TxLockManagerImpl.getRepositorySession(resource).getItem(itemPath);
        }
    }

    private static abstract class AbstractTransaction
    extends TransactionMap
    implements Transaction {
        private final DavResourceLocator locator;
        private final TxActiveLock lock;

        private AbstractTransaction(DavResourceLocator locator, TxActiveLock lock) {
            this.locator = locator;
            this.lock = lock;
        }

        @Override
        public TxActiveLock getLock() {
            return this.lock;
        }

        @Override
        public String getId() {
            return this.lock.getToken();
        }

        @Override
        public String getResourcePath() {
            return this.locator.getResourcePath();
        }

        @Override
        public boolean appliesToResource(DavResource resource) {
            if (this.locator.isSameWorkspace(resource.getLocator())) {
                String lockResourcePath = this.getResourcePath();
                String resPath = resource.getResourcePath();
                while (!"".equals(resPath)) {
                    if (lockResourcePath.equals(resPath)) {
                        return true;
                    }
                    resPath = Text.getRelativeParent(resPath, 1);
                }
            }
            return false;
        }
    }

    private static interface Transaction {
        public TxActiveLock getLock();

        public String getId();

        public String getResourcePath();

        public boolean appliesToResource(DavResource var1);

        public boolean isLocal();

        public void start(TransactionResource var1) throws DavException;

        public void commit(TransactionResource var1) throws DavException;

        public void rollback(TransactionResource var1) throws DavException;
    }
}

