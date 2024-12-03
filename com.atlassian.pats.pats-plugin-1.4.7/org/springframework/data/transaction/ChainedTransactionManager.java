/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.lang.Nullable
 *  org.springframework.transaction.CannotCreateTransactionException
 *  org.springframework.transaction.HeuristicCompletionException
 *  org.springframework.transaction.PlatformTransactionManager
 *  org.springframework.transaction.TransactionDefinition
 *  org.springframework.transaction.TransactionException
 *  org.springframework.transaction.TransactionStatus
 *  org.springframework.transaction.UnexpectedRollbackException
 *  org.springframework.util.Assert
 */
package org.springframework.data.transaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.transaction.MultiTransactionStatus;
import org.springframework.data.transaction.SpringTransactionSynchronizationManager;
import org.springframework.data.transaction.SynchronizationManager;
import org.springframework.lang.Nullable;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.transaction.HeuristicCompletionException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.util.Assert;

@Deprecated
public class ChainedTransactionManager
implements PlatformTransactionManager {
    private static final Log logger = LogFactory.getLog(ChainedTransactionManager.class);
    private final List<PlatformTransactionManager> transactionManagers;
    private final SynchronizationManager synchronizationManager;

    public ChainedTransactionManager(PlatformTransactionManager ... transactionManagers) {
        this(SpringTransactionSynchronizationManager.INSTANCE, transactionManagers);
    }

    ChainedTransactionManager(SynchronizationManager synchronizationManager, PlatformTransactionManager ... transactionManagers) {
        Assert.notNull((Object)synchronizationManager, (String)"SynchronizationManager must not be null!");
        Assert.notNull((Object)transactionManagers, (String)"Transaction managers must not be null!");
        Assert.isTrue((transactionManagers.length > 0 ? 1 : 0) != 0, (String)"At least one PlatformTransactionManager must be given!");
        this.synchronizationManager = synchronizationManager;
        this.transactionManagers = Arrays.asList(transactionManagers);
    }

    public MultiTransactionStatus getTransaction(@Nullable TransactionDefinition definition) throws TransactionException {
        MultiTransactionStatus mts = new MultiTransactionStatus(this.transactionManagers.get(0));
        if (definition == null) {
            return mts;
        }
        if (!this.synchronizationManager.isSynchronizationActive()) {
            this.synchronizationManager.initSynchronization();
            mts.setNewSynchonization();
        }
        try {
            for (PlatformTransactionManager transactionManager : this.transactionManagers) {
                mts.registerTransactionManager(definition, transactionManager);
            }
        }
        catch (Exception ex) {
            Map<PlatformTransactionManager, TransactionStatus> transactionStatuses = mts.getTransactionStatuses();
            for (PlatformTransactionManager transactionManager : this.transactionManagers) {
                try {
                    if (transactionStatuses.get(transactionManager) == null) continue;
                    transactionManager.rollback(transactionStatuses.get(transactionManager));
                }
                catch (Exception ex2) {
                    logger.warn((Object)("Rollback exception (" + transactionManager + ") " + ex2.getMessage()), (Throwable)ex2);
                }
            }
            if (mts.isNewSynchonization()) {
                this.synchronizationManager.clearSynchronization();
            }
            throw new CannotCreateTransactionException(ex.getMessage(), (Throwable)ex);
        }
        return mts;
    }

    public void commit(TransactionStatus status) throws TransactionException {
        MultiTransactionStatus multiTransactionStatus = (MultiTransactionStatus)status;
        boolean commit = true;
        Exception commitException = null;
        PlatformTransactionManager commitExceptionTransactionManager = null;
        for (PlatformTransactionManager transactionManager : this.reverse(this.transactionManagers)) {
            if (commit) {
                try {
                    multiTransactionStatus.commit(transactionManager);
                }
                catch (Exception ex) {
                    commit = false;
                    commitException = ex;
                    commitExceptionTransactionManager = transactionManager;
                }
                continue;
            }
            try {
                multiTransactionStatus.rollback(transactionManager);
            }
            catch (Exception ex) {
                logger.warn((Object)("Rollback exception (after commit) (" + transactionManager + ") " + ex.getMessage()), (Throwable)ex);
            }
        }
        if (multiTransactionStatus.isNewSynchonization()) {
            this.synchronizationManager.clearSynchronization();
        }
        if (commitException != null) {
            boolean firstTransactionManagerFailed = commitExceptionTransactionManager == this.getLastTransactionManager();
            int transactionState = firstTransactionManagerFailed ? 2 : 3;
            throw new HeuristicCompletionException(transactionState, (Throwable)commitException);
        }
    }

    public void rollback(TransactionStatus status) throws TransactionException {
        Exception rollbackException = null;
        PlatformTransactionManager rollbackExceptionTransactionManager = null;
        MultiTransactionStatus multiTransactionStatus = (MultiTransactionStatus)status;
        for (PlatformTransactionManager transactionManager : this.reverse(this.transactionManagers)) {
            try {
                multiTransactionStatus.rollback(transactionManager);
            }
            catch (Exception ex) {
                if (rollbackException == null) {
                    rollbackException = ex;
                    rollbackExceptionTransactionManager = transactionManager;
                    continue;
                }
                logger.warn((Object)("Rollback exception (" + transactionManager + ") " + ex.getMessage()), (Throwable)ex);
            }
        }
        if (multiTransactionStatus.isNewSynchonization()) {
            this.synchronizationManager.clearSynchronization();
        }
        if (rollbackException != null) {
            throw new UnexpectedRollbackException("Rollback exception, originated at (" + rollbackExceptionTransactionManager + ") " + rollbackException.getMessage(), (Throwable)rollbackException);
        }
    }

    private <T> Iterable<T> reverse(Collection<T> collection) {
        ArrayList<T> list = new ArrayList<T>(collection);
        Collections.reverse(list);
        return list;
    }

    private PlatformTransactionManager getLastTransactionManager() {
        return this.transactionManagers.get(this.lastTransactionManagerIndex());
    }

    private int lastTransactionManagerIndex() {
        return this.transactionManagers.size() - 1;
    }
}

