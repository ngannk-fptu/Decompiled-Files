/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.transaction.RollbackException
 *  javax.transaction.SystemException
 *  javax.transaction.Transaction
 *  javax.transaction.TransactionManager
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.sf.ehcache.transaction.xa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.store.ElementValueComparator;
import net.sf.ehcache.store.Store;
import net.sf.ehcache.transaction.SoftLock;
import net.sf.ehcache.transaction.SoftLockID;
import net.sf.ehcache.transaction.SoftLockManager;
import net.sf.ehcache.transaction.TransactionIDFactory;
import net.sf.ehcache.transaction.TransactionIDNotFoundException;
import net.sf.ehcache.transaction.manager.TransactionManagerLookup;
import net.sf.ehcache.transaction.xa.EhcacheXAException;
import net.sf.ehcache.transaction.xa.EhcacheXAResource;
import net.sf.ehcache.transaction.xa.OptimisticLockFailureException;
import net.sf.ehcache.transaction.xa.XAExecutionListener;
import net.sf.ehcache.transaction.xa.XATransactionContext;
import net.sf.ehcache.transaction.xa.XaCommitOutcome;
import net.sf.ehcache.transaction.xa.XaRecoveryOutcome;
import net.sf.ehcache.transaction.xa.XaRollbackOutcome;
import net.sf.ehcache.transaction.xa.XidTransactionID;
import net.sf.ehcache.transaction.xa.commands.Command;
import net.sf.ehcache.transaction.xa.processor.XARequest;
import net.sf.ehcache.transaction.xa.processor.XARequestProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terracotta.statistics.observer.OperationObserver;

public class EhcacheXAResourceImpl
implements EhcacheXAResource {
    private static final Logger LOG = LoggerFactory.getLogger((String)EhcacheXAResourceImpl.class.getName());
    private static final long MILLISECOND_PER_SECOND = 1000L;
    private final Ehcache cache;
    private final Store underlyingStore;
    private final TransactionIDFactory transactionIDFactory;
    private final TransactionManager txnManager;
    private final SoftLockManager softLockManager;
    private final ConcurrentMap<Xid, XATransactionContext> xidToContextMap = new ConcurrentHashMap<Xid, XATransactionContext>();
    private final XARequestProcessor processor;
    private volatile Xid currentXid;
    private volatile int transactionTimeout;
    private final List<XAExecutionListener> listeners = new ArrayList<XAExecutionListener>();
    private final ElementValueComparator comparator;
    private final OperationObserver<XaCommitOutcome> commitObserver;
    private final OperationObserver<XaRollbackOutcome> rollbackObserver;
    private final OperationObserver<XaRecoveryOutcome> recoveryObserver;

    public EhcacheXAResourceImpl(Ehcache cache, Store underlyingStore, TransactionManagerLookup txnManagerLookup, SoftLockManager softLockManager, TransactionIDFactory transactionIDFactory, ElementValueComparator comparator, OperationObserver<XaCommitOutcome> commitObserver, OperationObserver<XaRollbackOutcome> rollbackObserver, OperationObserver<XaRecoveryOutcome> recoveryObserver) {
        this.cache = cache;
        this.underlyingStore = underlyingStore;
        this.transactionIDFactory = transactionIDFactory;
        this.txnManager = txnManagerLookup.getTransactionManager();
        this.softLockManager = softLockManager;
        this.processor = new XARequestProcessor(this);
        this.transactionTimeout = cache.getCacheManager().getTransactionController().getDefaultTransactionTimeout();
        this.comparator = comparator;
        this.commitObserver = commitObserver;
        this.rollbackObserver = rollbackObserver;
        this.recoveryObserver = recoveryObserver;
    }

    @Override
    public void start(Xid xid, int flag) throws XAException {
        LOG.debug("start [{}] [{}]", (Object)xid, (Object)EhcacheXAResourceImpl.prettyPrintXAResourceFlags(flag));
        if (this.currentXid != null) {
            throw new EhcacheXAException("resource already started on " + this.currentXid, -6);
        }
        if (flag == 0) {
            if (this.xidToContextMap.containsKey(xid)) {
                throw new EhcacheXAException("cannot start with duplicate XID: " + xid, -8);
            }
            this.currentXid = xid;
        } else if (flag == 0x8000000) {
            if (!this.xidToContextMap.containsKey(xid)) {
                throw new EhcacheXAException("cannot resume non-existent XID: " + xid, -4);
            }
            this.currentXid = xid;
        } else if (flag == 0x200000) {
            this.currentXid = xid;
        } else {
            throw new EhcacheXAException("unsupported flag: " + flag, -6);
        }
    }

    @Override
    public void end(Xid xid, int flag) throws XAException {
        LOG.debug("end [{}] [{}]", (Object)xid, (Object)EhcacheXAResourceImpl.prettyPrintXAResourceFlags(flag));
        if (this.currentXid == null) {
            throw new EhcacheXAException("resource not started on " + xid, -6);
        }
        if (flag == 0x4000000 || flag == 0x2000000) {
            if (!this.currentXid.equals(xid)) {
                throw new EhcacheXAException("cannot end working on unknown XID " + xid, -4);
            }
            this.currentXid = null;
        } else if (flag == 0x20000000) {
            if (!this.currentXid.equals(xid)) {
                throw new EhcacheXAException("cannot end working on " + xid + " while work on current XID " + this.currentXid + " hasn't ended", -6);
            }
            this.xidToContextMap.remove(xid);
            this.currentXid = null;
        } else {
            throw new EhcacheXAException("unsupported flag: " + flag, -6);
        }
    }

    @Override
    public void forget(Xid xid) throws XAException {
        LOG.debug("forget [{}]", (Object)xid);
        this.processor.process(new XARequest(XARequest.RequestType.FORGET, xid));
    }

    public void forgetInternal(Xid xid) throws XAException {
        List<Xid> xids = Arrays.asList(this.recover(0x1000000));
        if (!xids.contains(xid)) {
            throw new EhcacheXAException("forget called on in-doubt XID" + xid, -6);
        }
    }

    @Override
    public int getTransactionTimeout() throws XAException {
        return this.transactionTimeout;
    }

    @Override
    public boolean isSameRM(XAResource xaResource) throws XAException {
        boolean same = xaResource == this ? true : (xaResource instanceof EhcacheXAResourceImpl ? this.cache == ((EhcacheXAResourceImpl)xaResource).cache : false);
        LOG.debug("{} isSameRm {} -> " + same, (Object)this, (Object)xaResource);
        return same;
    }

    @Override
    public int prepare(Xid xid) throws XAException {
        LOG.debug("prepare [{}]", (Object)xid);
        if (this.currentXid != null) {
            throw new EhcacheXAException("prepare called on non-ended XID: " + xid, -6);
        }
        return this.processor.process(new XARequest(XARequest.RequestType.PREPARE, xid));
    }

    public int prepareInternal(Xid xid) throws XAException {
        this.fireBeforePrepare();
        XATransactionContext twopcTransactionContext = (XATransactionContext)this.xidToContextMap.get(xid);
        if (twopcTransactionContext == null) {
            throw new EhcacheXAException("transaction never started: " + xid, -4);
        }
        XidTransactionID xidTransactionID = this.transactionIDFactory.createXidTransactionID(xid, this.cache);
        List<Command> commands = twopcTransactionContext.getCommands();
        LinkedList<Command> preparedCommands = new LinkedList<Command>();
        boolean prepareUpdated = false;
        LOG.debug("preparing {} command(s) for [{}]", (Object)commands.size(), (Object)xid);
        for (Command command : commands) {
            try {
                prepareUpdated |= command.prepare(this.underlyingStore, this.softLockManager, xidTransactionID, this.comparator);
                preparedCommands.add(0, command);
            }
            catch (OptimisticLockFailureException ie) {
                for (Command preparedCommand : preparedCommands) {
                    preparedCommand.rollback(this.underlyingStore, this.softLockManager);
                }
                preparedCommands.clear();
                throw new EhcacheXAException(command + " failed because value changed between execution and 2PC", 103, ie);
            }
        }
        this.xidToContextMap.remove(xid);
        if (!prepareUpdated) {
            this.rollbackInternal(xid);
        }
        LOG.debug("prepared xid [{}] read only? {}", (Object)xid, (Object)(!prepareUpdated ? 1 : 0));
        return prepareUpdated ? 0 : 3;
    }

    @Override
    public void commit(Xid xid, boolean onePhase) throws XAException {
        LOG.debug("commit [{}] [{}]", (Object)xid, (Object)onePhase);
        if (this.currentXid != null) {
            throw new EhcacheXAException("commit called on non-ended XID: " + xid, -6);
        }
        this.processor.process(new XARequest(XARequest.RequestType.COMMIT, xid, onePhase));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void commitInternal(Xid xid, boolean onePhase) throws XAException {
        this.commitObserver.begin();
        XidTransactionID xidTransactionID = this.transactionIDFactory.createXidTransactionID(xid, this.cache);
        try {
            if (onePhase) {
                XATransactionContext twopcTransactionContext = (XATransactionContext)this.xidToContextMap.get(xid);
                if (twopcTransactionContext == null) {
                    throw new EhcacheXAException("cannot call commit(onePhase=true) after prepare", -6);
                }
                int rc = this.prepareInternal(xid);
                if (rc == 3) {
                    this.commitObserver.end(XaCommitOutcome.READ_ONLY);
                    return;
                }
            }
            Set<SoftLock> softLocks = this.softLockManager.collectAllSoftLocksForTransactionID(xidTransactionID);
            LOG.debug("committing {} soft lock(s) for [{}]", (Object)softLocks.size(), (Object)xid);
            for (SoftLock softLock : softLocks) {
                if (!softLock.isExpired()) continue;
                LOG.debug("freezing expired soft lock {}", (Object)softLock);
                softLock.lock();
                softLock.freeze();
            }
            LOG.debug("all {} soft lock(s) are frozen for [{}]", (Object)softLocks.size(), (Object)xid);
            try {
                this.transactionIDFactory.markForCommit(xidTransactionID);
                LOG.debug("marked tx ID from commit: {}", (Object)xidTransactionID);
            }
            catch (TransactionIDNotFoundException tnfe) {
                this.commitObserver.end(XaCommitOutcome.EXCEPTION);
                throw new EhcacheXAException("cannot find XID, it might have been duplicated and cleaned up earlier on: " + xid, -4, tnfe);
            }
            catch (IllegalStateException ise) {
                this.commitObserver.end(XaCommitOutcome.EXCEPTION);
                throw new EhcacheXAException("XID already was rolling back: " + xid, -3);
            }
            for (SoftLock softLock : softLocks) {
                LOG.debug("fetching underlying element with key '{}'", softLock.getKey());
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
                if (!softLockId.getTransactionID().equals(xidTransactionID)) {
                    LOG.debug("soft lock ID with key '{}' of foreign tx in underlying store, ignoring it", softLock.getKey());
                    continue;
                }
                Element frozenElement = softLockId.getNewElement();
                if (frozenElement != null) {
                    LOG.debug("replacing soft locked underlying element with key '{}' with new value", softLock.getKey());
                    this.underlyingStore.put(frozenElement);
                    continue;
                }
                LOG.debug("removing soft locked underlying element with key '{}'", softLock.getKey());
                this.underlyingStore.remove(softLock.getKey());
            }
            LOG.debug("unlocking {} soft lock(s) for [{}]", (Object)softLocks.size(), (Object)xid);
            for (SoftLock softLock : softLocks) {
                softLock.unfreeze();
                softLock.unlock();
            }
            LOG.debug("all {} soft lock(s) have been unfrozen for [{}]", (Object)softLocks.size(), (Object)xid);
            this.fireAfterCommitOrRollback();
            LOG.debug("AfterCommitOrRollback event fired for [{}]", (Object)xid);
            this.commitObserver.end(XaCommitOutcome.COMMITTED);
        }
        finally {
            this.transactionIDFactory.clear(xidTransactionID);
            LOG.debug("transaction ID cleared: {}", (Object)xidTransactionID);
        }
    }

    @Override
    public Xid[] recover(int flags) throws XAException {
        this.recoveryObserver.begin();
        LOG.debug("recover [{}]", (Object)EhcacheXAResourceImpl.prettyPrintXAResourceFlags(flags));
        if ((flags & 0x1000000) != 0x1000000) {
            return new Xid[0];
        }
        final Set xids = Collections.synchronizedSet(new HashSet());
        Thread t = new Thread("ehcache [" + this.cache.getName() + "] XA recovery thread"){

            @Override
            public void run() {
                for (XidTransactionID xidTransactionID : EhcacheXAResourceImpl.this.transactionIDFactory.getAllXidTransactionIDsFor(EhcacheXAResourceImpl.this.cache)) {
                    if (!EhcacheXAResourceImpl.this.transactionIDFactory.isExpired(xidTransactionID)) continue;
                    xids.add(xidTransactionID.getXid());
                }
            }
        };
        try {
            t.setDaemon(true);
            t.start();
            t.join((long)this.transactionTimeout * 1000L);
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
        if (t.isAlive()) {
            Exception exception = new Exception("thread dump");
            exception.setStackTrace(t.getStackTrace());
            LOG.debug("XA recovery thread was interrupted after timeout", (Throwable)exception);
            t.interrupt();
        }
        if (xids.isEmpty()) {
            this.recoveryObserver.end(XaRecoveryOutcome.NOTHING);
        } else {
            this.recoveryObserver.end(XaRecoveryOutcome.RECOVERED, xids.size());
        }
        return xids.toArray(new Xid[0]);
    }

    @Override
    public void rollback(Xid xid) throws XAException {
        LOG.debug("rollback [{}]", (Object)xid);
        this.processor.process(new XARequest(XARequest.RequestType.ROLLBACK, xid));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void rollbackInternal(Xid xid) throws XAException {
        this.rollbackObserver.begin();
        XidTransactionID xidTransactionID = this.transactionIDFactory.createXidTransactionID(xid, this.cache);
        try {
            Set<SoftLock> softLocks = this.softLockManager.collectAllSoftLocksForTransactionID(xidTransactionID);
            for (SoftLock softLock : softLocks) {
                if (!softLock.isExpired()) continue;
                softLock.lock();
                softLock.freeze();
            }
            try {
                this.transactionIDFactory.markForRollback(xidTransactionID);
            }
            catch (TransactionIDNotFoundException tnfe) {
                this.rollbackObserver.end(XaRollbackOutcome.EXCEPTION);
                throw new EhcacheXAException("cannot find XID, it might have been duplicated an cleaned up earlier on: " + xid, -4, tnfe);
            }
            catch (IllegalStateException ise) {
                this.rollbackObserver.end(XaRollbackOutcome.EXCEPTION);
                throw new EhcacheXAException("XID already was committing: " + xid, -3);
            }
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
                if (!softLockId.getTransactionID().equals(xidTransactionID)) {
                    LOG.debug("soft lock ID with key '{}' of foreign tx in underlying store, ignoring it", softLock.getKey());
                    continue;
                }
                Element frozenElement = softLockId.getOldElement();
                if (frozenElement != null) {
                    this.underlyingStore.put(frozenElement);
                    continue;
                }
                this.underlyingStore.remove(softLock.getKey());
            }
            for (SoftLock softLock : softLocks) {
                softLock.unfreeze();
                softLock.unlock();
            }
            this.xidToContextMap.remove(xid);
            this.fireAfterCommitOrRollback();
            this.rollbackObserver.end(XaRollbackOutcome.ROLLEDBACK);
        }
        finally {
            this.transactionIDFactory.clear(xidTransactionID);
        }
    }

    @Override
    public boolean setTransactionTimeout(int timeout) throws XAException {
        if (timeout < 0) {
            throw new EhcacheXAException("timeout must be >= 0, was: " + timeout, -5);
        }
        this.transactionTimeout = timeout == 0 ? this.cache.getCacheManager().getTransactionController().getDefaultTransactionTimeout() : timeout;
        return true;
    }

    @Override
    public void addTwoPcExecutionListener(XAExecutionListener listener) {
        this.listeners.add(listener);
    }

    private void fireBeforePrepare() {
        for (XAExecutionListener listener : this.listeners) {
            listener.beforePrepare(this);
        }
    }

    private void fireAfterCommitOrRollback() {
        for (XAExecutionListener listener : this.listeners) {
            listener.afterCommitOrRollback(this);
        }
    }

    @Override
    public String getCacheName() {
        return this.cache.getName();
    }

    @Override
    public XATransactionContext createTransactionContext() throws SystemException, RollbackException {
        XATransactionContext ctx = this.getCurrentTransactionContext();
        if (ctx != null) {
            return ctx;
        }
        Transaction transaction = this.txnManager.getTransaction();
        LOG.debug("enlisting {} in {}", (Object)this, (Object)transaction);
        transaction.enlistResource((XAResource)this);
        if (this.currentXid == null) {
            throw new CacheException("enlistment of XAResource of cache named '" + this.getCacheName() + "' did not end up calling XAResource.start()");
        }
        ctx = (XATransactionContext)this.xidToContextMap.get(this.currentXid);
        if (ctx == null) {
            LOG.debug("creating new context for XID [{}]", (Object)this.currentXid);
            ctx = new XATransactionContext(this.underlyingStore);
            this.xidToContextMap.put(this.currentXid, ctx);
        }
        return ctx;
    }

    @Override
    public XATransactionContext getCurrentTransactionContext() {
        if (this.currentXid == null) {
            LOG.debug("getting current TX context of XAResource with current XID [null]: null");
            return null;
        }
        XATransactionContext xaTransactionContext = (XATransactionContext)this.xidToContextMap.get(this.currentXid);
        LOG.debug("getting current TX context of XAResource with current XID [{}]: {}", (Object)this.currentXid, (Object)xaTransactionContext);
        return xaTransactionContext;
    }

    private static String prettyPrintXAResourceFlags(int flags) {
        StringBuilder sb = new StringBuilder();
        if ((flags & 0x800000) == 0x800000) {
            if (sb.length() > 0) {
                sb.append('|');
            }
            sb.append("TMENDRSCAN");
        }
        if ((flags & 0x20000000) == 0x20000000) {
            if (sb.length() > 0) {
                sb.append('|');
            }
            sb.append("TMFAIL");
        }
        if ((flags & 0x200000) == 0x200000) {
            if (sb.length() > 0) {
                sb.append('|');
            }
            sb.append("TMJOIN");
        }
        if ((flags & 0x40000000) == 0x40000000) {
            if (sb.length() > 0) {
                sb.append('|');
            }
            sb.append("TMONEPHASE");
        }
        if ((flags & 0x8000000) == 0x8000000) {
            if (sb.length() > 0) {
                sb.append('|');
            }
            sb.append("TMRESUME");
        }
        if ((flags & 0x1000000) == 0x1000000) {
            if (sb.length() > 0) {
                sb.append('|');
            }
            sb.append("TMSTARTRSCAN");
        }
        if ((flags & 0x4000000) == 0x4000000) {
            if (sb.length() > 0) {
                sb.append('|');
            }
            sb.append("TMSUCCESS");
        }
        if ((flags & 0x2000000) == 0x2000000) {
            if (sb.length() > 0) {
                sb.append('|');
            }
            sb.append("TMSUSPEND");
        }
        if (sb.length() == 0 && flags == 0) {
            sb.append("TMNOFLAGS");
        }
        if (sb.length() == 0) {
            sb.append("unknown flag: ").append(flags);
        }
        return sb.toString();
    }

    public String toString() {
        return "EhcacheXAResourceImpl of cache " + this.cache.getName();
    }
}

