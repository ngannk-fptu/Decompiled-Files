/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ibm.wsspi.uow.UOWAction
 *  com.ibm.wsspi.uow.UOWActionException
 *  com.ibm.wsspi.uow.UOWException
 *  com.ibm.wsspi.uow.UOWManager
 *  com.ibm.wsspi.uow.UOWManagerFactory
 *  javax.transaction.Synchronization
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ReflectionUtils
 */
package org.springframework.transaction.jta;

import com.ibm.wsspi.uow.UOWAction;
import com.ibm.wsspi.uow.UOWActionException;
import com.ibm.wsspi.uow.UOWException;
import com.ibm.wsspi.uow.UOWManager;
import com.ibm.wsspi.uow.UOWManagerFactory;
import java.util.List;
import javax.naming.NamingException;
import javax.transaction.Synchronization;
import org.springframework.lang.Nullable;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.InvalidTimeoutException;
import org.springframework.transaction.NestedTransactionNotSupportedException;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.jta.JtaAfterCompletionSynchronization;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.transaction.jta.JtaTransactionObject;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.CallbackPreferringPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.SmartTransactionObject;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionSynchronizationUtils;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

public class WebSphereUowTransactionManager
extends JtaTransactionManager
implements CallbackPreferringPlatformTransactionManager {
    public static final String DEFAULT_UOW_MANAGER_NAME = "java:comp/websphere/UOWManager";
    @Nullable
    private UOWManager uowManager;
    @Nullable
    private String uowManagerName;

    public WebSphereUowTransactionManager() {
        this.setAutodetectTransactionManager(false);
    }

    public WebSphereUowTransactionManager(UOWManager uowManager) {
        this();
        this.uowManager = uowManager;
    }

    public void setUowManager(UOWManager uowManager) {
        this.uowManager = uowManager;
    }

    public void setUowManagerName(String uowManagerName) {
        this.uowManagerName = uowManagerName;
    }

    @Override
    public void afterPropertiesSet() throws TransactionSystemException {
        this.initUserTransactionAndTransactionManager();
        if (this.uowManager == null) {
            this.uowManager = this.uowManagerName != null ? this.lookupUowManager(this.uowManagerName) : this.lookupDefaultUowManager();
        }
    }

    protected UOWManager lookupUowManager(String uowManagerName) throws TransactionSystemException {
        try {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Retrieving WebSphere UOWManager from JNDI location [" + uowManagerName + "]"));
            }
            return (UOWManager)this.getJndiTemplate().lookup(uowManagerName, UOWManager.class);
        }
        catch (NamingException ex) {
            throw new TransactionSystemException("WebSphere UOWManager is not available at JNDI location [" + uowManagerName + "]", ex);
        }
    }

    protected UOWManager lookupDefaultUowManager() throws TransactionSystemException {
        try {
            this.logger.debug((Object)"Retrieving WebSphere UOWManager from default JNDI location [java:comp/websphere/UOWManager]");
            return (UOWManager)this.getJndiTemplate().lookup(DEFAULT_UOW_MANAGER_NAME, UOWManager.class);
        }
        catch (NamingException ex) {
            this.logger.debug((Object)"WebSphere UOWManager is not available at default JNDI location [java:comp/websphere/UOWManager] - falling back to UOWManagerFactory lookup");
            return UOWManagerFactory.getUOWManager();
        }
    }

    private UOWManager obtainUOWManager() {
        Assert.state((this.uowManager != null ? 1 : 0) != 0, (String)"No UOWManager set");
        return this.uowManager;
    }

    @Override
    protected void doRegisterAfterCompletionWithJtaTransaction(JtaTransactionObject txObject, List<TransactionSynchronization> synchronizations) {
        this.obtainUOWManager().registerInterposedSynchronization((Synchronization)new JtaAfterCompletionSynchronization(synchronizations));
    }

    @Override
    public boolean supportsResourceAdapterManagedTransactions() {
        return true;
    }

    @Override
    @Nullable
    public <T> T execute(@Nullable TransactionDefinition definition, TransactionCallback<T> callback) throws TransactionException {
        TransactionDefinition def;
        TransactionDefinition transactionDefinition = def = definition != null ? definition : TransactionDefinition.withDefaults();
        if (def.getTimeout() < -1) {
            throw new InvalidTimeoutException("Invalid transaction timeout", def.getTimeout());
        }
        UOWManager uowManager = this.obtainUOWManager();
        int pb = def.getPropagationBehavior();
        boolean existingTx = uowManager.getUOWStatus() != 5 && uowManager.getUOWType() != 0;
        int uowType = 1;
        boolean joinTx = false;
        boolean newSynch = false;
        if (existingTx) {
            if (pb == 5) {
                throw new IllegalTransactionStateException("Transaction propagation 'never' but existing transaction found");
            }
            if (pb == 6) {
                throw new NestedTransactionNotSupportedException("Transaction propagation 'nested' not supported for WebSphere UOW transactions");
            }
            if (pb == 1 || pb == 0 || pb == 2) {
                joinTx = true;
                newSynch = this.getTransactionSynchronization() != 2;
            } else if (pb == 4) {
                uowType = 0;
                newSynch = this.getTransactionSynchronization() == 0;
            } else {
                newSynch = this.getTransactionSynchronization() != 2;
            }
        } else {
            if (pb == 2) {
                throw new IllegalTransactionStateException("Transaction propagation 'mandatory' but no existing transaction found");
            }
            if (pb == 1 || pb == 4 || pb == 5) {
                uowType = 0;
                newSynch = this.getTransactionSynchronization() == 0;
            } else {
                newSynch = this.getTransactionSynchronization() != 2;
            }
        }
        boolean debug = this.logger.isDebugEnabled();
        if (debug) {
            this.logger.debug((Object)("Creating new transaction with name [" + def.getName() + "]: " + def));
        }
        AbstractPlatformTransactionManager.SuspendedResourcesHolder suspendedResources = !joinTx ? this.suspend(null) : null;
        UOWActionAdapter<T> action = null;
        try {
            boolean actualTransaction;
            boolean bl = actualTransaction = uowType == 1;
            if (actualTransaction && def.getTimeout() > -1) {
                uowManager.setUOWTimeout(uowType, def.getTimeout());
            }
            if (debug) {
                this.logger.debug((Object)("Invoking WebSphere UOW action: type=" + uowType + ", join=" + joinTx));
            }
            action = new UOWActionAdapter<T>(def, callback, actualTransaction, !joinTx, newSynch, debug);
            uowManager.runUnderUOW(uowType, joinTx, action);
            if (debug) {
                this.logger.debug((Object)("Returned from WebSphere UOW action: type=" + uowType + ", join=" + joinTx));
            }
            T t = action.getResult();
            return t;
        }
        catch (UOWActionException | UOWException ex) {
            TransactionSystemException tse = new TransactionSystemException("UOWManager transaction processing failed", ex);
            Throwable appEx = action.getException();
            if (appEx != null) {
                this.logger.error((Object)"Application exception overridden by rollback exception", appEx);
                tse.initApplicationException(appEx);
            }
            throw tse;
        }
        finally {
            if (suspendedResources != null) {
                this.resume(null, suspendedResources);
            }
        }
    }

    private class UOWActionAdapter<T>
    implements UOWAction,
    SmartTransactionObject {
        private final TransactionDefinition definition;
        private final TransactionCallback<T> callback;
        private final boolean actualTransaction;
        private final boolean newTransaction;
        private final boolean newSynchronization;
        private boolean debug;
        @Nullable
        private T result;
        @Nullable
        private Throwable exception;

        public UOWActionAdapter(TransactionDefinition definition, TransactionCallback<T> callback, boolean actualTransaction, boolean newTransaction, boolean newSynchronization, boolean debug) {
            this.definition = definition;
            this.callback = callback;
            this.actualTransaction = actualTransaction;
            this.newTransaction = newTransaction;
            this.newSynchronization = newSynchronization;
            this.debug = debug;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void run() {
            UOWManager uowManager = WebSphereUowTransactionManager.this.obtainUOWManager();
            DefaultTransactionStatus status = WebSphereUowTransactionManager.this.prepareTransactionStatus(this.definition, this.actualTransaction ? this : null, this.newTransaction, this.newSynchronization, this.debug, null);
            try {
                this.result = this.callback.doInTransaction(status);
                WebSphereUowTransactionManager.this.triggerBeforeCommit(status);
            }
            catch (Throwable ex) {
                this.exception = ex;
                if (status.isDebug()) {
                    WebSphereUowTransactionManager.this.logger.debug((Object)"Rolling back on application exception from transaction callback", ex);
                }
                uowManager.setRollbackOnly();
            }
            finally {
                if (status.isLocalRollbackOnly()) {
                    if (status.isDebug()) {
                        WebSphereUowTransactionManager.this.logger.debug((Object)"Transaction callback has explicitly requested rollback");
                    }
                    uowManager.setRollbackOnly();
                }
                WebSphereUowTransactionManager.this.triggerBeforeCompletion(status);
                if (status.isNewSynchronization()) {
                    List<TransactionSynchronization> synchronizations = TransactionSynchronizationManager.getSynchronizations();
                    TransactionSynchronizationManager.clear();
                    if (!synchronizations.isEmpty()) {
                        uowManager.registerInterposedSynchronization((Synchronization)new JtaAfterCompletionSynchronization(synchronizations));
                    }
                }
            }
        }

        @Nullable
        public T getResult() {
            if (this.exception != null) {
                ReflectionUtils.rethrowRuntimeException((Throwable)this.exception);
            }
            return this.result;
        }

        @Nullable
        public Throwable getException() {
            return this.exception;
        }

        @Override
        public boolean isRollbackOnly() {
            return WebSphereUowTransactionManager.this.obtainUOWManager().getRollbackOnly();
        }

        @Override
        public void flush() {
            TransactionSynchronizationUtils.triggerFlush();
        }
    }
}

