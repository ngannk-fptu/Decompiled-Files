/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.core.Constants
 *  org.springframework.lang.Nullable
 */
package org.springframework.transaction.support;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.Constants;
import org.springframework.lang.Nullable;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.InvalidTimeoutException;
import org.springframework.transaction.NestedTransactionNotSupportedException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.TransactionSuspensionNotSupportedException;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionSynchronizationUtils;

public abstract class AbstractPlatformTransactionManager
implements PlatformTransactionManager,
Serializable {
    public static final int SYNCHRONIZATION_ALWAYS = 0;
    public static final int SYNCHRONIZATION_ON_ACTUAL_TRANSACTION = 1;
    public static final int SYNCHRONIZATION_NEVER = 2;
    private static final Constants constants = new Constants(AbstractPlatformTransactionManager.class);
    protected transient Log logger = LogFactory.getLog(this.getClass());
    private int transactionSynchronization = 0;
    private int defaultTimeout = -1;
    private boolean nestedTransactionAllowed = false;
    private boolean validateExistingTransaction = false;
    private boolean globalRollbackOnParticipationFailure = true;
    private boolean failEarlyOnGlobalRollbackOnly = false;
    private boolean rollbackOnCommitFailure = false;

    public final void setTransactionSynchronizationName(String constantName) {
        this.setTransactionSynchronization(constants.asNumber(constantName).intValue());
    }

    public final void setTransactionSynchronization(int transactionSynchronization) {
        this.transactionSynchronization = transactionSynchronization;
    }

    public final int getTransactionSynchronization() {
        return this.transactionSynchronization;
    }

    public final void setDefaultTimeout(int defaultTimeout) {
        if (defaultTimeout < -1) {
            throw new InvalidTimeoutException("Invalid default timeout", defaultTimeout);
        }
        this.defaultTimeout = defaultTimeout;
    }

    public final int getDefaultTimeout() {
        return this.defaultTimeout;
    }

    public final void setNestedTransactionAllowed(boolean nestedTransactionAllowed) {
        this.nestedTransactionAllowed = nestedTransactionAllowed;
    }

    public final boolean isNestedTransactionAllowed() {
        return this.nestedTransactionAllowed;
    }

    public final void setValidateExistingTransaction(boolean validateExistingTransaction) {
        this.validateExistingTransaction = validateExistingTransaction;
    }

    public final boolean isValidateExistingTransaction() {
        return this.validateExistingTransaction;
    }

    public final void setGlobalRollbackOnParticipationFailure(boolean globalRollbackOnParticipationFailure) {
        this.globalRollbackOnParticipationFailure = globalRollbackOnParticipationFailure;
    }

    public final boolean isGlobalRollbackOnParticipationFailure() {
        return this.globalRollbackOnParticipationFailure;
    }

    public final void setFailEarlyOnGlobalRollbackOnly(boolean failEarlyOnGlobalRollbackOnly) {
        this.failEarlyOnGlobalRollbackOnly = failEarlyOnGlobalRollbackOnly;
    }

    public final boolean isFailEarlyOnGlobalRollbackOnly() {
        return this.failEarlyOnGlobalRollbackOnly;
    }

    public final void setRollbackOnCommitFailure(boolean rollbackOnCommitFailure) {
        this.rollbackOnCommitFailure = rollbackOnCommitFailure;
    }

    public final boolean isRollbackOnCommitFailure() {
        return this.rollbackOnCommitFailure;
    }

    @Override
    public final TransactionStatus getTransaction(@Nullable TransactionDefinition definition) throws TransactionException {
        TransactionDefinition def = definition != null ? definition : TransactionDefinition.withDefaults();
        Object transaction = this.doGetTransaction();
        boolean debugEnabled = this.logger.isDebugEnabled();
        if (this.isExistingTransaction(transaction)) {
            return this.handleExistingTransaction(def, transaction, debugEnabled);
        }
        if (def.getTimeout() < -1) {
            throw new InvalidTimeoutException("Invalid transaction timeout", def.getTimeout());
        }
        if (def.getPropagationBehavior() == 2) {
            throw new IllegalTransactionStateException("No existing transaction found for transaction marked with propagation 'mandatory'");
        }
        if (def.getPropagationBehavior() == 0 || def.getPropagationBehavior() == 3 || def.getPropagationBehavior() == 6) {
            SuspendedResourcesHolder suspendedResources = this.suspend(null);
            if (debugEnabled) {
                this.logger.debug((Object)("Creating new transaction with name [" + def.getName() + "]: " + def));
            }
            try {
                return this.startTransaction(def, transaction, debugEnabled, suspendedResources);
            }
            catch (Error | RuntimeException ex) {
                this.resume(null, suspendedResources);
                throw ex;
            }
        }
        if (def.getIsolationLevel() != -1 && this.logger.isWarnEnabled()) {
            this.logger.warn((Object)("Custom isolation level specified but no actual transaction initiated; isolation level will effectively be ignored: " + def));
        }
        boolean newSynchronization = this.getTransactionSynchronization() == 0;
        return this.prepareTransactionStatus(def, null, true, newSynchronization, debugEnabled, null);
    }

    private TransactionStatus startTransaction(TransactionDefinition definition, Object transaction, boolean debugEnabled, @Nullable SuspendedResourcesHolder suspendedResources) {
        boolean newSynchronization = this.getTransactionSynchronization() != 2;
        DefaultTransactionStatus status = this.newTransactionStatus(definition, transaction, true, newSynchronization, debugEnabled, suspendedResources);
        this.doBegin(transaction, definition);
        this.prepareSynchronization(status, definition);
        return status;
    }

    private TransactionStatus handleExistingTransaction(TransactionDefinition definition, Object transaction, boolean debugEnabled) throws TransactionException {
        if (definition.getPropagationBehavior() == 5) {
            throw new IllegalTransactionStateException("Existing transaction found for transaction marked with propagation 'never'");
        }
        if (definition.getPropagationBehavior() == 4) {
            if (debugEnabled) {
                this.logger.debug((Object)"Suspending current transaction");
            }
            SuspendedResourcesHolder suspendedResources = this.suspend(transaction);
            boolean newSynchronization = this.getTransactionSynchronization() == 0;
            return this.prepareTransactionStatus(definition, null, false, newSynchronization, debugEnabled, suspendedResources);
        }
        if (definition.getPropagationBehavior() == 3) {
            if (debugEnabled) {
                this.logger.debug((Object)("Suspending current transaction, creating new transaction with name [" + definition.getName() + "]"));
            }
            SuspendedResourcesHolder suspendedResources = this.suspend(transaction);
            try {
                return this.startTransaction(definition, transaction, debugEnabled, suspendedResources);
            }
            catch (Error | RuntimeException beginEx) {
                this.resumeAfterBeginException(transaction, suspendedResources, beginEx);
                throw beginEx;
            }
        }
        if (definition.getPropagationBehavior() == 6) {
            if (!this.isNestedTransactionAllowed()) {
                throw new NestedTransactionNotSupportedException("Transaction manager does not allow nested transactions by default - specify 'nestedTransactionAllowed' property with value 'true'");
            }
            if (debugEnabled) {
                this.logger.debug((Object)("Creating nested transaction with name [" + definition.getName() + "]"));
            }
            if (this.useSavepointForNestedTransaction()) {
                DefaultTransactionStatus status = this.prepareTransactionStatus(definition, transaction, false, false, debugEnabled, null);
                status.createAndHoldSavepoint();
                return status;
            }
            return this.startTransaction(definition, transaction, debugEnabled, null);
        }
        if (debugEnabled) {
            this.logger.debug((Object)"Participating in existing transaction");
        }
        if (this.isValidateExistingTransaction()) {
            Integer currentIsolationLevel;
            if (definition.getIsolationLevel() != -1 && ((currentIsolationLevel = TransactionSynchronizationManager.getCurrentTransactionIsolationLevel()) == null || currentIsolationLevel.intValue() != definition.getIsolationLevel())) {
                Constants isoConstants = DefaultTransactionDefinition.constants;
                throw new IllegalTransactionStateException("Participating transaction with definition [" + definition + "] specifies isolation level which is incompatible with existing transaction: " + (currentIsolationLevel != null ? isoConstants.toCode((Object)currentIsolationLevel, "ISOLATION_") : "(unknown)"));
            }
            if (!definition.isReadOnly() && TransactionSynchronizationManager.isCurrentTransactionReadOnly()) {
                throw new IllegalTransactionStateException("Participating transaction with definition [" + definition + "] is not marked as read-only but existing transaction is");
            }
        }
        boolean newSynchronization = this.getTransactionSynchronization() != 2;
        return this.prepareTransactionStatus(definition, transaction, false, newSynchronization, debugEnabled, null);
    }

    protected final DefaultTransactionStatus prepareTransactionStatus(TransactionDefinition definition, @Nullable Object transaction, boolean newTransaction, boolean newSynchronization, boolean debug, @Nullable Object suspendedResources) {
        DefaultTransactionStatus status = this.newTransactionStatus(definition, transaction, newTransaction, newSynchronization, debug, suspendedResources);
        this.prepareSynchronization(status, definition);
        return status;
    }

    protected DefaultTransactionStatus newTransactionStatus(TransactionDefinition definition, @Nullable Object transaction, boolean newTransaction, boolean newSynchronization, boolean debug, @Nullable Object suspendedResources) {
        boolean actualNewSynchronization = newSynchronization && !TransactionSynchronizationManager.isSynchronizationActive();
        return new DefaultTransactionStatus(transaction, newTransaction, actualNewSynchronization, definition.isReadOnly(), debug, suspendedResources);
    }

    protected void prepareSynchronization(DefaultTransactionStatus status, TransactionDefinition definition) {
        if (status.isNewSynchronization()) {
            TransactionSynchronizationManager.setActualTransactionActive(status.hasTransaction());
            TransactionSynchronizationManager.setCurrentTransactionIsolationLevel(definition.getIsolationLevel() != -1 ? Integer.valueOf(definition.getIsolationLevel()) : null);
            TransactionSynchronizationManager.setCurrentTransactionReadOnly(definition.isReadOnly());
            TransactionSynchronizationManager.setCurrentTransactionName(definition.getName());
            TransactionSynchronizationManager.initSynchronization();
        }
    }

    protected int determineTimeout(TransactionDefinition definition) {
        if (definition.getTimeout() != -1) {
            return definition.getTimeout();
        }
        return this.getDefaultTimeout();
    }

    @Nullable
    protected final SuspendedResourcesHolder suspend(@Nullable Object transaction) throws TransactionException {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            List<TransactionSynchronization> suspendedSynchronizations = this.doSuspendSynchronization();
            try {
                Object suspendedResources = null;
                if (transaction != null) {
                    suspendedResources = this.doSuspend(transaction);
                }
                String name = TransactionSynchronizationManager.getCurrentTransactionName();
                TransactionSynchronizationManager.setCurrentTransactionName(null);
                boolean readOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
                TransactionSynchronizationManager.setCurrentTransactionReadOnly(false);
                Integer isolationLevel = TransactionSynchronizationManager.getCurrentTransactionIsolationLevel();
                TransactionSynchronizationManager.setCurrentTransactionIsolationLevel(null);
                boolean wasActive = TransactionSynchronizationManager.isActualTransactionActive();
                TransactionSynchronizationManager.setActualTransactionActive(false);
                return new SuspendedResourcesHolder(suspendedResources, suspendedSynchronizations, name, readOnly, isolationLevel, wasActive);
            }
            catch (Error | RuntimeException ex) {
                this.doResumeSynchronization(suspendedSynchronizations);
                throw ex;
            }
        }
        if (transaction != null) {
            Object suspendedResources = this.doSuspend(transaction);
            return new SuspendedResourcesHolder(suspendedResources);
        }
        return null;
    }

    protected final void resume(@Nullable Object transaction, @Nullable SuspendedResourcesHolder resourcesHolder) throws TransactionException {
        if (resourcesHolder != null) {
            List suspendedSynchronizations;
            Object suspendedResources = resourcesHolder.suspendedResources;
            if (suspendedResources != null) {
                this.doResume(transaction, suspendedResources);
            }
            if ((suspendedSynchronizations = resourcesHolder.suspendedSynchronizations) != null) {
                TransactionSynchronizationManager.setActualTransactionActive(resourcesHolder.wasActive);
                TransactionSynchronizationManager.setCurrentTransactionIsolationLevel(resourcesHolder.isolationLevel);
                TransactionSynchronizationManager.setCurrentTransactionReadOnly(resourcesHolder.readOnly);
                TransactionSynchronizationManager.setCurrentTransactionName(resourcesHolder.name);
                this.doResumeSynchronization(suspendedSynchronizations);
            }
        }
    }

    private void resumeAfterBeginException(Object transaction, @Nullable SuspendedResourcesHolder suspendedResources, Throwable beginEx) {
        try {
            this.resume(transaction, suspendedResources);
        }
        catch (Error | RuntimeException resumeEx) {
            String exMessage = "Inner transaction begin exception overridden by outer transaction resume exception";
            this.logger.error((Object)exMessage, beginEx);
            throw resumeEx;
        }
    }

    private List<TransactionSynchronization> doSuspendSynchronization() {
        List<TransactionSynchronization> suspendedSynchronizations = TransactionSynchronizationManager.getSynchronizations();
        for (TransactionSynchronization synchronization : suspendedSynchronizations) {
            synchronization.suspend();
        }
        TransactionSynchronizationManager.clearSynchronization();
        return suspendedSynchronizations;
    }

    private void doResumeSynchronization(List<TransactionSynchronization> suspendedSynchronizations) {
        TransactionSynchronizationManager.initSynchronization();
        for (TransactionSynchronization synchronization : suspendedSynchronizations) {
            synchronization.resume();
            TransactionSynchronizationManager.registerSynchronization(synchronization);
        }
    }

    @Override
    public final void commit(TransactionStatus status) throws TransactionException {
        if (status.isCompleted()) {
            throw new IllegalTransactionStateException("Transaction is already completed - do not call commit or rollback more than once per transaction");
        }
        DefaultTransactionStatus defStatus = (DefaultTransactionStatus)status;
        if (defStatus.isLocalRollbackOnly()) {
            if (defStatus.isDebug()) {
                this.logger.debug((Object)"Transactional code has requested rollback");
            }
            this.processRollback(defStatus, false);
            return;
        }
        if (!this.shouldCommitOnGlobalRollbackOnly() && defStatus.isGlobalRollbackOnly()) {
            if (defStatus.isDebug()) {
                this.logger.debug((Object)"Global transaction is marked as rollback-only but transactional code requested commit");
            }
            this.processRollback(defStatus, true);
            return;
        }
        this.processCommit(defStatus);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void processCommit(DefaultTransactionStatus status) throws TransactionException {
        try {
            boolean beforeCompletionInvoked = false;
            try {
                boolean unexpectedRollback = false;
                this.prepareForCommit(status);
                this.triggerBeforeCommit(status);
                this.triggerBeforeCompletion(status);
                beforeCompletionInvoked = true;
                if (status.hasSavepoint()) {
                    if (status.isDebug()) {
                        this.logger.debug((Object)"Releasing transaction savepoint");
                    }
                    unexpectedRollback = status.isGlobalRollbackOnly();
                    status.releaseHeldSavepoint();
                } else if (status.isNewTransaction()) {
                    if (status.isDebug()) {
                        this.logger.debug((Object)"Initiating transaction commit");
                    }
                    unexpectedRollback = status.isGlobalRollbackOnly();
                    this.doCommit(status);
                } else if (this.isFailEarlyOnGlobalRollbackOnly()) {
                    unexpectedRollback = status.isGlobalRollbackOnly();
                }
                if (unexpectedRollback) {
                    throw new UnexpectedRollbackException("Transaction silently rolled back because it has been marked as rollback-only");
                }
            }
            catch (UnexpectedRollbackException ex) {
                this.triggerAfterCompletion(status, 1);
                throw ex;
            }
            catch (TransactionException ex) {
                if (this.isRollbackOnCommitFailure()) {
                    this.doRollbackOnCommitException(status, (Throwable)((Object)ex));
                } else {
                    this.triggerAfterCompletion(status, 2);
                }
                throw ex;
            }
            catch (Error | RuntimeException ex) {
                if (!beforeCompletionInvoked) {
                    this.triggerBeforeCompletion(status);
                }
                this.doRollbackOnCommitException(status, ex);
                throw ex;
            }
            try {
                this.triggerAfterCommit(status);
            }
            finally {
                this.triggerAfterCompletion(status, 0);
            }
        }
        finally {
            this.cleanupAfterCompletion(status);
        }
    }

    @Override
    public final void rollback(TransactionStatus status) throws TransactionException {
        if (status.isCompleted()) {
            throw new IllegalTransactionStateException("Transaction is already completed - do not call commit or rollback more than once per transaction");
        }
        DefaultTransactionStatus defStatus = (DefaultTransactionStatus)status;
        this.processRollback(defStatus, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void processRollback(DefaultTransactionStatus status, boolean unexpected) {
        try {
            boolean unexpectedRollback = unexpected;
            try {
                this.triggerBeforeCompletion(status);
                if (status.hasSavepoint()) {
                    if (status.isDebug()) {
                        this.logger.debug((Object)"Rolling back transaction to savepoint");
                    }
                    status.rollbackToHeldSavepoint();
                } else if (status.isNewTransaction()) {
                    if (status.isDebug()) {
                        this.logger.debug((Object)"Initiating transaction rollback");
                    }
                    this.doRollback(status);
                } else {
                    if (status.hasTransaction()) {
                        if (status.isLocalRollbackOnly() || this.isGlobalRollbackOnParticipationFailure()) {
                            if (status.isDebug()) {
                                this.logger.debug((Object)"Participating transaction failed - marking existing transaction as rollback-only");
                            }
                            this.doSetRollbackOnly(status);
                        } else if (status.isDebug()) {
                            this.logger.debug((Object)"Participating transaction failed - letting transaction originator decide on rollback");
                        }
                    } else {
                        this.logger.debug((Object)"Should roll back transaction but cannot - no transaction available");
                    }
                    if (!this.isFailEarlyOnGlobalRollbackOnly()) {
                        unexpectedRollback = false;
                    }
                }
            }
            catch (Error | RuntimeException ex) {
                this.triggerAfterCompletion(status, 2);
                throw ex;
            }
            this.triggerAfterCompletion(status, 1);
            if (unexpectedRollback) {
                throw new UnexpectedRollbackException("Transaction rolled back because it has been marked as rollback-only");
            }
        }
        finally {
            this.cleanupAfterCompletion(status);
        }
    }

    private void doRollbackOnCommitException(DefaultTransactionStatus status, Throwable ex) throws TransactionException {
        try {
            if (status.isNewTransaction()) {
                if (status.isDebug()) {
                    this.logger.debug((Object)"Initiating transaction rollback after commit exception", ex);
                }
                this.doRollback(status);
            } else if (status.hasTransaction() && this.isGlobalRollbackOnParticipationFailure()) {
                if (status.isDebug()) {
                    this.logger.debug((Object)"Marking existing transaction as rollback-only after commit exception", ex);
                }
                this.doSetRollbackOnly(status);
            }
        }
        catch (Error | RuntimeException rbex) {
            this.logger.error((Object)"Commit exception overridden by rollback exception", ex);
            this.triggerAfterCompletion(status, 2);
            throw rbex;
        }
        this.triggerAfterCompletion(status, 1);
    }

    protected final void triggerBeforeCommit(DefaultTransactionStatus status) {
        if (status.isNewSynchronization()) {
            TransactionSynchronizationUtils.triggerBeforeCommit(status.isReadOnly());
        }
    }

    protected final void triggerBeforeCompletion(DefaultTransactionStatus status) {
        if (status.isNewSynchronization()) {
            TransactionSynchronizationUtils.triggerBeforeCompletion();
        }
    }

    private void triggerAfterCommit(DefaultTransactionStatus status) {
        if (status.isNewSynchronization()) {
            TransactionSynchronizationUtils.triggerAfterCommit();
        }
    }

    private void triggerAfterCompletion(DefaultTransactionStatus status, int completionStatus) {
        if (status.isNewSynchronization()) {
            List<TransactionSynchronization> synchronizations = TransactionSynchronizationManager.getSynchronizations();
            TransactionSynchronizationManager.clearSynchronization();
            if (!status.hasTransaction() || status.isNewTransaction()) {
                this.invokeAfterCompletion(synchronizations, completionStatus);
            } else if (!synchronizations.isEmpty()) {
                this.registerAfterCompletionWithExistingTransaction(status.getTransaction(), synchronizations);
            }
        }
    }

    protected final void invokeAfterCompletion(List<TransactionSynchronization> synchronizations, int completionStatus) {
        TransactionSynchronizationUtils.invokeAfterCompletion(synchronizations, completionStatus);
    }

    private void cleanupAfterCompletion(DefaultTransactionStatus status) {
        status.setCompleted();
        if (status.isNewSynchronization()) {
            TransactionSynchronizationManager.clear();
        }
        if (status.isNewTransaction()) {
            this.doCleanupAfterCompletion(status.getTransaction());
        }
        if (status.getSuspendedResources() != null) {
            if (status.isDebug()) {
                this.logger.debug((Object)"Resuming suspended transaction after completion of inner transaction");
            }
            Object transaction = status.hasTransaction() ? status.getTransaction() : null;
            this.resume(transaction, (SuspendedResourcesHolder)status.getSuspendedResources());
        }
    }

    protected abstract Object doGetTransaction() throws TransactionException;

    protected boolean isExistingTransaction(Object transaction) throws TransactionException {
        return false;
    }

    protected boolean useSavepointForNestedTransaction() {
        return true;
    }

    protected abstract void doBegin(Object var1, TransactionDefinition var2) throws TransactionException;

    protected Object doSuspend(Object transaction) throws TransactionException {
        throw new TransactionSuspensionNotSupportedException("Transaction manager [" + this.getClass().getName() + "] does not support transaction suspension");
    }

    protected void doResume(@Nullable Object transaction, Object suspendedResources) throws TransactionException {
        throw new TransactionSuspensionNotSupportedException("Transaction manager [" + this.getClass().getName() + "] does not support transaction suspension");
    }

    protected boolean shouldCommitOnGlobalRollbackOnly() {
        return false;
    }

    protected void prepareForCommit(DefaultTransactionStatus status) {
    }

    protected abstract void doCommit(DefaultTransactionStatus var1) throws TransactionException;

    protected abstract void doRollback(DefaultTransactionStatus var1) throws TransactionException;

    protected void doSetRollbackOnly(DefaultTransactionStatus status) throws TransactionException {
        throw new IllegalTransactionStateException("Participating in existing transactions is not supported - when 'isExistingTransaction' returns true, appropriate 'doSetRollbackOnly' behavior must be provided");
    }

    protected void registerAfterCompletionWithExistingTransaction(Object transaction, List<TransactionSynchronization> synchronizations) throws TransactionException {
        this.logger.debug((Object)"Cannot register Spring after-completion synchronization with existing transaction - processing Spring after-completion callbacks immediately, with outcome status 'unknown'");
        this.invokeAfterCompletion(synchronizations, 2);
    }

    protected void doCleanupAfterCompletion(Object transaction) {
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        this.logger = LogFactory.getLog(this.getClass());
    }

    protected static final class SuspendedResourcesHolder {
        @Nullable
        private final Object suspendedResources;
        @Nullable
        private List<TransactionSynchronization> suspendedSynchronizations;
        @Nullable
        private String name;
        private boolean readOnly;
        @Nullable
        private Integer isolationLevel;
        private boolean wasActive;

        private SuspendedResourcesHolder(Object suspendedResources) {
            this.suspendedResources = suspendedResources;
        }

        private SuspendedResourcesHolder(@Nullable Object suspendedResources, List<TransactionSynchronization> suspendedSynchronizations, @Nullable String name, boolean readOnly, @Nullable Integer isolationLevel, boolean wasActive) {
            this.suspendedResources = suspendedResources;
            this.suspendedSynchronizations = suspendedSynchronizations;
            this.name = name;
            this.readOnly = readOnly;
            this.isolationLevel = isolationLevel;
            this.wasActive = wasActive;
        }
    }
}

