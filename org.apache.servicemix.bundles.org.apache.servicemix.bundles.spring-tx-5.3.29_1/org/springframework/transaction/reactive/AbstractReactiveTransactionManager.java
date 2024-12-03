/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.lang.Nullable
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Mono
 */
package org.springframework.transaction.reactive;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.InvalidTimeoutException;
import org.springframework.transaction.ReactiveTransaction;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionSuspensionNotSupportedException;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.reactive.GenericReactiveTransaction;
import org.springframework.transaction.reactive.TransactionContextManager;
import org.springframework.transaction.reactive.TransactionSynchronization;
import org.springframework.transaction.reactive.TransactionSynchronizationManager;
import org.springframework.transaction.reactive.TransactionSynchronizationUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public abstract class AbstractReactiveTransactionManager
implements ReactiveTransactionManager,
Serializable {
    protected transient Log logger = LogFactory.getLog(this.getClass());

    @Override
    public final Mono<ReactiveTransaction> getReactiveTransaction(@Nullable TransactionDefinition definition) {
        TransactionDefinition def = definition != null ? definition : TransactionDefinition.withDefaults();
        return TransactionSynchronizationManager.forCurrentTransaction().flatMap(synchronizationManager -> {
            Object transaction = this.doGetTransaction((TransactionSynchronizationManager)synchronizationManager);
            boolean debugEnabled = this.logger.isDebugEnabled();
            if (this.isExistingTransaction(transaction)) {
                return this.handleExistingTransaction((TransactionSynchronizationManager)synchronizationManager, def, transaction, debugEnabled);
            }
            if (def.getTimeout() < -1) {
                return Mono.error((Throwable)((Object)new InvalidTimeoutException("Invalid transaction timeout", def.getTimeout())));
            }
            if (def.getPropagationBehavior() == 2) {
                return Mono.error((Throwable)((Object)new IllegalTransactionStateException("No existing transaction found for transaction marked with propagation 'mandatory'")));
            }
            if (def.getPropagationBehavior() == 0 || def.getPropagationBehavior() == 3 || def.getPropagationBehavior() == 6) {
                return TransactionContextManager.currentContext().map(TransactionSynchronizationManager::new).flatMap(nestedSynchronizationManager -> this.suspend((TransactionSynchronizationManager)nestedSynchronizationManager, null).map(Optional::of).defaultIfEmpty(Optional.empty()).flatMap(suspendedResources -> {
                    if (debugEnabled) {
                        this.logger.debug((Object)("Creating new transaction with name [" + def.getName() + "]: " + def));
                    }
                    return Mono.defer(() -> {
                        GenericReactiveTransaction status = this.newReactiveTransaction((TransactionSynchronizationManager)nestedSynchronizationManager, def, transaction, true, debugEnabled, suspendedResources.orElse(null));
                        return this.doBegin((TransactionSynchronizationManager)nestedSynchronizationManager, transaction, def).doOnSuccess(ignore -> this.prepareSynchronization((TransactionSynchronizationManager)nestedSynchronizationManager, status, def)).thenReturn((Object)status);
                    }).onErrorResume((Predicate)ErrorPredicates.RUNTIME_OR_ERROR, ex -> this.resume((TransactionSynchronizationManager)nestedSynchronizationManager, null, suspendedResources.orElse(null)).then(Mono.error((Throwable)ex)));
                }));
            }
            if (def.getIsolationLevel() != -1 && this.logger.isWarnEnabled()) {
                this.logger.warn((Object)("Custom isolation level specified but no actual transaction initiated; isolation level will effectively be ignored: " + def));
            }
            return Mono.just((Object)this.prepareReactiveTransaction((TransactionSynchronizationManager)synchronizationManager, def, null, true, debugEnabled, null));
        });
    }

    private Mono<ReactiveTransaction> handleExistingTransaction(TransactionSynchronizationManager synchronizationManager, TransactionDefinition definition, Object transaction, boolean debugEnabled) {
        if (definition.getPropagationBehavior() == 5) {
            return Mono.error((Throwable)((Object)new IllegalTransactionStateException("Existing transaction found for transaction marked with propagation 'never'")));
        }
        if (definition.getPropagationBehavior() == 4) {
            if (debugEnabled) {
                this.logger.debug((Object)"Suspending current transaction");
            }
            Mono<SuspendedResourcesHolder> suspend = this.suspend(synchronizationManager, transaction);
            return suspend.map(suspendedResources -> this.prepareReactiveTransaction(synchronizationManager, definition, null, false, debugEnabled, suspendedResources)).switchIfEmpty(Mono.fromSupplier(() -> this.prepareReactiveTransaction(synchronizationManager, definition, null, false, debugEnabled, null))).cast(ReactiveTransaction.class);
        }
        if (definition.getPropagationBehavior() == 3) {
            if (debugEnabled) {
                this.logger.debug((Object)("Suspending current transaction, creating new transaction with name [" + definition.getName() + "]"));
            }
            Mono<SuspendedResourcesHolder> suspendedResources2 = this.suspend(synchronizationManager, transaction);
            return suspendedResources2.flatMap(suspendedResourcesHolder -> {
                GenericReactiveTransaction status = this.newReactiveTransaction(synchronizationManager, definition, transaction, true, debugEnabled, suspendedResourcesHolder);
                return this.doBegin(synchronizationManager, transaction, definition).doOnSuccess(ignore -> this.prepareSynchronization(synchronizationManager, status, definition)).thenReturn((Object)status).onErrorResume((Predicate)ErrorPredicates.RUNTIME_OR_ERROR, beginEx -> this.resumeAfterBeginException(synchronizationManager, transaction, (SuspendedResourcesHolder)suspendedResourcesHolder, (Throwable)beginEx).then(Mono.error((Throwable)beginEx)));
            });
        }
        if (definition.getPropagationBehavior() == 6) {
            if (debugEnabled) {
                this.logger.debug((Object)("Creating nested transaction with name [" + definition.getName() + "]"));
            }
            GenericReactiveTransaction status = this.newReactiveTransaction(synchronizationManager, definition, transaction, true, debugEnabled, null);
            return this.doBegin(synchronizationManager, transaction, definition).doOnSuccess(ignore -> this.prepareSynchronization(synchronizationManager, status, definition)).thenReturn((Object)status);
        }
        if (debugEnabled) {
            this.logger.debug((Object)"Participating in existing transaction");
        }
        return Mono.just((Object)this.prepareReactiveTransaction(synchronizationManager, definition, transaction, false, debugEnabled, null));
    }

    private GenericReactiveTransaction prepareReactiveTransaction(TransactionSynchronizationManager synchronizationManager, TransactionDefinition definition, @Nullable Object transaction, boolean newTransaction, boolean debug, @Nullable Object suspendedResources) {
        GenericReactiveTransaction status = this.newReactiveTransaction(synchronizationManager, definition, transaction, newTransaction, debug, suspendedResources);
        this.prepareSynchronization(synchronizationManager, status, definition);
        return status;
    }

    private GenericReactiveTransaction newReactiveTransaction(TransactionSynchronizationManager synchronizationManager, TransactionDefinition definition, @Nullable Object transaction, boolean newTransaction, boolean debug, @Nullable Object suspendedResources) {
        return new GenericReactiveTransaction(transaction, newTransaction, !synchronizationManager.isSynchronizationActive(), definition.isReadOnly(), debug, suspendedResources);
    }

    private void prepareSynchronization(TransactionSynchronizationManager synchronizationManager, GenericReactiveTransaction status, TransactionDefinition definition) {
        if (status.isNewSynchronization()) {
            synchronizationManager.setActualTransactionActive(status.hasTransaction());
            synchronizationManager.setCurrentTransactionIsolationLevel(definition.getIsolationLevel() != -1 ? Integer.valueOf(definition.getIsolationLevel()) : null);
            synchronizationManager.setCurrentTransactionReadOnly(definition.isReadOnly());
            synchronizationManager.setCurrentTransactionName(definition.getName());
            synchronizationManager.initSynchronization();
        }
    }

    private Mono<SuspendedResourcesHolder> suspend(TransactionSynchronizationManager synchronizationManager, @Nullable Object transaction) {
        if (synchronizationManager.isSynchronizationActive()) {
            Mono<List<TransactionSynchronization>> suspendedSynchronizations = this.doSuspendSynchronization(synchronizationManager);
            return suspendedSynchronizations.flatMap(synchronizations -> {
                Mono suspendedResources = transaction != null ? this.doSuspend(synchronizationManager, transaction).map(Optional::of).defaultIfEmpty(Optional.empty()) : Mono.just(Optional.empty());
                return suspendedResources.map(it -> {
                    String name = synchronizationManager.getCurrentTransactionName();
                    synchronizationManager.setCurrentTransactionName(null);
                    boolean readOnly = synchronizationManager.isCurrentTransactionReadOnly();
                    synchronizationManager.setCurrentTransactionReadOnly(false);
                    Integer isolationLevel = synchronizationManager.getCurrentTransactionIsolationLevel();
                    synchronizationManager.setCurrentTransactionIsolationLevel(null);
                    boolean wasActive = synchronizationManager.isActualTransactionActive();
                    synchronizationManager.setActualTransactionActive(false);
                    return new SuspendedResourcesHolder(it.orElse(null), (List)synchronizations, name, readOnly, isolationLevel, wasActive);
                }).onErrorResume((Predicate)ErrorPredicates.RUNTIME_OR_ERROR, ex -> this.doResumeSynchronization(synchronizationManager, (List<TransactionSynchronization>)synchronizations).cast(SuspendedResourcesHolder.class));
            });
        }
        if (transaction != null) {
            Mono suspendedResources = this.doSuspend(synchronizationManager, transaction).map(Optional::of).defaultIfEmpty(Optional.empty());
            return suspendedResources.map(it -> new SuspendedResourcesHolder(it.orElse(null)));
        }
        return Mono.empty();
    }

    private Mono<Void> resume(TransactionSynchronizationManager synchronizationManager, @Nullable Object transaction, @Nullable SuspendedResourcesHolder resourcesHolder) {
        Mono<Void> resume = Mono.empty();
        if (resourcesHolder != null) {
            List suspendedSynchronizations;
            Object suspendedResources = resourcesHolder.suspendedResources;
            if (suspendedResources != null) {
                resume = this.doResume(synchronizationManager, transaction, suspendedResources);
            }
            if ((suspendedSynchronizations = resourcesHolder.suspendedSynchronizations) != null) {
                synchronizationManager.setActualTransactionActive(resourcesHolder.wasActive);
                synchronizationManager.setCurrentTransactionIsolationLevel(resourcesHolder.isolationLevel);
                synchronizationManager.setCurrentTransactionReadOnly(resourcesHolder.readOnly);
                synchronizationManager.setCurrentTransactionName(resourcesHolder.name);
                return resume.then(this.doResumeSynchronization(synchronizationManager, suspendedSynchronizations));
            }
        }
        return resume;
    }

    private Mono<Void> resumeAfterBeginException(TransactionSynchronizationManager synchronizationManager, Object transaction, @Nullable SuspendedResourcesHolder suspendedResources, Throwable beginEx) {
        String exMessage = "Inner transaction begin exception overridden by outer transaction resume exception";
        return this.resume(synchronizationManager, transaction, suspendedResources).doOnError((Predicate)ErrorPredicates.RUNTIME_OR_ERROR, ex -> this.logger.error((Object)exMessage, beginEx));
    }

    private Mono<List<TransactionSynchronization>> doSuspendSynchronization(TransactionSynchronizationManager synchronizationManager) {
        List<TransactionSynchronization> suspendedSynchronizations = synchronizationManager.getSynchronizations();
        return Flux.fromIterable(suspendedSynchronizations).concatMap(TransactionSynchronization::suspend).then(Mono.defer(() -> {
            synchronizationManager.clearSynchronization();
            return Mono.just((Object)suspendedSynchronizations);
        }));
    }

    private Mono<Void> doResumeSynchronization(TransactionSynchronizationManager synchronizationManager, List<TransactionSynchronization> suspendedSynchronizations) {
        synchronizationManager.initSynchronization();
        return Flux.fromIterable(suspendedSynchronizations).concatMap(synchronization -> synchronization.resume().doOnSuccess(ignore -> synchronizationManager.registerSynchronization((TransactionSynchronization)synchronization))).then();
    }

    @Override
    public final Mono<Void> commit(ReactiveTransaction transaction) {
        if (transaction.isCompleted()) {
            return Mono.error((Throwable)((Object)new IllegalTransactionStateException("Transaction is already completed - do not call commit or rollback more than once per transaction")));
        }
        return TransactionSynchronizationManager.forCurrentTransaction().flatMap(synchronizationManager -> {
            GenericReactiveTransaction reactiveTx = (GenericReactiveTransaction)transaction;
            if (reactiveTx.isRollbackOnly()) {
                if (reactiveTx.isDebug()) {
                    this.logger.debug((Object)"Transactional code has requested rollback");
                }
                return this.processRollback((TransactionSynchronizationManager)synchronizationManager, reactiveTx);
            }
            return this.processCommit((TransactionSynchronizationManager)synchronizationManager, reactiveTx);
        });
    }

    private Mono<Void> processCommit(TransactionSynchronizationManager synchronizationManager, GenericReactiveTransaction status) {
        AtomicBoolean beforeCompletionInvoked = new AtomicBoolean();
        Mono commit = this.prepareForCommit(synchronizationManager, status).then(this.triggerBeforeCommit(synchronizationManager, status)).then(this.triggerBeforeCompletion(synchronizationManager, status)).then(Mono.defer(() -> {
            beforeCompletionInvoked.set(true);
            if (status.isNewTransaction()) {
                if (status.isDebug()) {
                    this.logger.debug((Object)"Initiating transaction commit");
                }
                return this.doCommit(synchronizationManager, status);
            }
            return Mono.empty();
        })).then(Mono.empty().onErrorResume(ex -> {
            Mono propagateException;
            Mono result = propagateException = Mono.error((Throwable)ex);
            if (ErrorPredicates.UNEXPECTED_ROLLBACK.test((Throwable)ex)) {
                result = this.triggerAfterCompletion(synchronizationManager, status, 1).then(propagateException);
            } else if (ErrorPredicates.TRANSACTION_EXCEPTION.test((Throwable)ex)) {
                result = this.triggerAfterCompletion(synchronizationManager, status, 2).then(propagateException);
            } else if (ErrorPredicates.RUNTIME_OR_ERROR.test((Throwable)ex)) {
                Mono<Void> mono = !beforeCompletionInvoked.get() ? this.triggerBeforeCompletion(synchronizationManager, status) : Mono.empty();
                result = mono.then(this.doRollbackOnCommitException(synchronizationManager, status, (Throwable)ex)).then(propagateException);
            }
            return result;
        })).then(Mono.defer(() -> this.triggerAfterCommit(synchronizationManager, status).onErrorResume(ex -> this.triggerAfterCompletion(synchronizationManager, status, 0).then(Mono.error((Throwable)ex))).then(this.triggerAfterCompletion(synchronizationManager, status, 0))));
        return commit.onErrorResume(ex -> this.cleanupAfterCompletion(synchronizationManager, status).then(Mono.error((Throwable)ex))).then(this.cleanupAfterCompletion(synchronizationManager, status));
    }

    @Override
    public final Mono<Void> rollback(ReactiveTransaction transaction) {
        if (transaction.isCompleted()) {
            return Mono.error((Throwable)((Object)new IllegalTransactionStateException("Transaction is already completed - do not call commit or rollback more than once per transaction")));
        }
        return TransactionSynchronizationManager.forCurrentTransaction().flatMap(synchronizationManager -> {
            GenericReactiveTransaction reactiveTx = (GenericReactiveTransaction)transaction;
            return this.processRollback((TransactionSynchronizationManager)synchronizationManager, reactiveTx);
        });
    }

    private Mono<Void> processRollback(TransactionSynchronizationManager synchronizationManager, GenericReactiveTransaction status) {
        return this.triggerBeforeCompletion(synchronizationManager, status).then(Mono.defer(() -> {
            if (status.isNewTransaction()) {
                if (status.isDebug()) {
                    this.logger.debug((Object)"Initiating transaction rollback");
                }
                return this.doRollback(synchronizationManager, status);
            }
            Mono<Void> beforeCompletion = Mono.empty();
            if (status.hasTransaction()) {
                if (status.isDebug()) {
                    this.logger.debug((Object)"Participating transaction failed - marking existing transaction as rollback-only");
                }
                beforeCompletion = this.doSetRollbackOnly(synchronizationManager, status);
            } else {
                this.logger.debug((Object)"Should roll back transaction but cannot - no transaction available");
            }
            return beforeCompletion;
        })).onErrorResume((Predicate)ErrorPredicates.RUNTIME_OR_ERROR, ex -> this.triggerAfterCompletion(synchronizationManager, status, 2).then(Mono.error((Throwable)ex))).then(Mono.defer(() -> this.triggerAfterCompletion(synchronizationManager, status, 1))).onErrorResume(ex -> this.cleanupAfterCompletion(synchronizationManager, status).then(Mono.error((Throwable)ex))).then(this.cleanupAfterCompletion(synchronizationManager, status));
    }

    private Mono<Void> doRollbackOnCommitException(TransactionSynchronizationManager synchronizationManager, GenericReactiveTransaction status, Throwable ex) {
        return Mono.defer(() -> {
            if (status.isNewTransaction()) {
                if (status.isDebug()) {
                    this.logger.debug((Object)"Initiating transaction rollback after commit exception", ex);
                }
                return this.doRollback(synchronizationManager, status);
            }
            if (status.hasTransaction()) {
                if (status.isDebug()) {
                    this.logger.debug((Object)"Marking existing transaction as rollback-only after commit exception", ex);
                }
                return this.doSetRollbackOnly(synchronizationManager, status);
            }
            return Mono.empty();
        }).onErrorResume((Predicate)ErrorPredicates.RUNTIME_OR_ERROR, rbex -> {
            this.logger.error((Object)"Commit exception overridden by rollback exception", ex);
            return this.triggerAfterCompletion(synchronizationManager, status, 2).then(Mono.error((Throwable)rbex));
        }).then(this.triggerAfterCompletion(synchronizationManager, status, 1));
    }

    private Mono<Void> triggerBeforeCommit(TransactionSynchronizationManager synchronizationManager, GenericReactiveTransaction status) {
        if (status.isNewSynchronization()) {
            return TransactionSynchronizationUtils.triggerBeforeCommit(synchronizationManager.getSynchronizations(), status.isReadOnly());
        }
        return Mono.empty();
    }

    private Mono<Void> triggerBeforeCompletion(TransactionSynchronizationManager synchronizationManager, GenericReactiveTransaction status) {
        if (status.isNewSynchronization()) {
            return TransactionSynchronizationUtils.triggerBeforeCompletion(synchronizationManager.getSynchronizations());
        }
        return Mono.empty();
    }

    private Mono<Void> triggerAfterCommit(TransactionSynchronizationManager synchronizationManager, GenericReactiveTransaction status) {
        if (status.isNewSynchronization()) {
            return TransactionSynchronizationUtils.invokeAfterCommit(synchronizationManager.getSynchronizations());
        }
        return Mono.empty();
    }

    private Mono<Void> triggerAfterCompletion(TransactionSynchronizationManager synchronizationManager, GenericReactiveTransaction status, int completionStatus) {
        if (status.isNewSynchronization()) {
            List<TransactionSynchronization> synchronizations = synchronizationManager.getSynchronizations();
            synchronizationManager.clearSynchronization();
            if (!status.hasTransaction() || status.isNewTransaction()) {
                return this.invokeAfterCompletion(synchronizationManager, synchronizations, completionStatus);
            }
            if (!synchronizations.isEmpty()) {
                return this.registerAfterCompletionWithExistingTransaction(synchronizationManager, status.getTransaction(), synchronizations);
            }
        }
        return Mono.empty();
    }

    private Mono<Void> invokeAfterCompletion(TransactionSynchronizationManager synchronizationManager, List<TransactionSynchronization> synchronizations, int completionStatus) {
        return TransactionSynchronizationUtils.invokeAfterCompletion(synchronizations, completionStatus);
    }

    private Mono<Void> cleanupAfterCompletion(TransactionSynchronizationManager synchronizationManager, GenericReactiveTransaction status) {
        return Mono.defer(() -> {
            status.setCompleted();
            if (status.isNewSynchronization()) {
                synchronizationManager.clear();
            }
            Mono<Void> cleanup = Mono.empty();
            if (status.isNewTransaction()) {
                cleanup = this.doCleanupAfterCompletion(synchronizationManager, status.getTransaction());
            }
            if (status.getSuspendedResources() != null) {
                if (status.isDebug()) {
                    this.logger.debug((Object)"Resuming suspended transaction after completion of inner transaction");
                }
                Object transaction = status.hasTransaction() ? status.getTransaction() : null;
                return cleanup.then(this.resume(synchronizationManager, transaction, (SuspendedResourcesHolder)status.getSuspendedResources()));
            }
            return cleanup;
        });
    }

    protected abstract Object doGetTransaction(TransactionSynchronizationManager var1);

    protected boolean isExistingTransaction(Object transaction) {
        return false;
    }

    protected abstract Mono<Void> doBegin(TransactionSynchronizationManager var1, Object var2, TransactionDefinition var3);

    protected Mono<Object> doSuspend(TransactionSynchronizationManager synchronizationManager, Object transaction) {
        throw new TransactionSuspensionNotSupportedException("Transaction manager [" + this.getClass().getName() + "] does not support transaction suspension");
    }

    protected Mono<Void> doResume(TransactionSynchronizationManager synchronizationManager, @Nullable Object transaction, Object suspendedResources) {
        throw new TransactionSuspensionNotSupportedException("Transaction manager [" + this.getClass().getName() + "] does not support transaction suspension");
    }

    protected Mono<Void> prepareForCommit(TransactionSynchronizationManager synchronizationManager, GenericReactiveTransaction status) {
        return Mono.empty();
    }

    protected abstract Mono<Void> doCommit(TransactionSynchronizationManager var1, GenericReactiveTransaction var2);

    protected abstract Mono<Void> doRollback(TransactionSynchronizationManager var1, GenericReactiveTransaction var2);

    protected Mono<Void> doSetRollbackOnly(TransactionSynchronizationManager synchronizationManager, GenericReactiveTransaction status) {
        throw new IllegalTransactionStateException("Participating in existing transactions is not supported - when 'isExistingTransaction' returns true, appropriate 'doSetRollbackOnly' behavior must be provided");
    }

    protected Mono<Void> registerAfterCompletionWithExistingTransaction(TransactionSynchronizationManager synchronizationManager, Object transaction, List<TransactionSynchronization> synchronizations) {
        this.logger.debug((Object)"Cannot register Spring after-completion synchronization with existing transaction - processing Spring after-completion callbacks immediately, with outcome status 'unknown'");
        return this.invokeAfterCompletion(synchronizationManager, synchronizations, 2);
    }

    protected Mono<Void> doCleanupAfterCompletion(TransactionSynchronizationManager synchronizationManager, Object transaction) {
        return Mono.empty();
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        this.logger = LogFactory.getLog(this.getClass());
    }

    private static enum ErrorPredicates implements Predicate<Throwable>
    {
        RUNTIME_OR_ERROR{

            @Override
            public boolean test(Throwable throwable) {
                return throwable instanceof RuntimeException || throwable instanceof Error;
            }
        }
        ,
        TRANSACTION_EXCEPTION{

            @Override
            public boolean test(Throwable throwable) {
                return throwable instanceof TransactionException;
            }
        }
        ,
        UNEXPECTED_ROLLBACK{

            @Override
            public boolean test(Throwable throwable) {
                return throwable instanceof UnexpectedRollbackException;
            }
        };


        @Override
        public abstract boolean test(Throwable var1);
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

        private SuspendedResourcesHolder(@Nullable Object suspendedResources) {
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

