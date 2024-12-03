/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.transaction.HeuristicMixedException
 *  javax.transaction.HeuristicRollbackException
 *  javax.transaction.InvalidTransactionException
 *  javax.transaction.NotSupportedException
 *  javax.transaction.RollbackException
 *  javax.transaction.Synchronization
 *  javax.transaction.SystemException
 *  javax.transaction.Transaction
 *  javax.transaction.TransactionManager
 *  javax.transaction.TransactionSynchronizationRegistry
 *  javax.transaction.UserTransaction
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.jndi.JndiTemplate
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package org.springframework.transaction.jta;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Properties;
import javax.naming.NamingException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.InvalidTransactionException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;
import javax.transaction.UserTransaction;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jndi.JndiTemplate;
import org.springframework.lang.Nullable;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.transaction.HeuristicCompletionException;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.InvalidIsolationLevelException;
import org.springframework.transaction.NestedTransactionNotSupportedException;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionSuspensionNotSupportedException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.jta.JtaAfterCompletionSynchronization;
import org.springframework.transaction.jta.JtaTransactionObject;
import org.springframework.transaction.jta.ManagedTransactionAdapter;
import org.springframework.transaction.jta.TransactionFactory;
import org.springframework.transaction.jta.UserTransactionAdapter;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class JtaTransactionManager
extends AbstractPlatformTransactionManager
implements TransactionFactory,
InitializingBean,
Serializable {
    public static final String DEFAULT_USER_TRANSACTION_NAME = "java:comp/UserTransaction";
    public static final String[] FALLBACK_TRANSACTION_MANAGER_NAMES = new String[]{"java:comp/TransactionManager", "java:appserver/TransactionManager", "java:pm/TransactionManager", "java:/TransactionManager"};
    public static final String DEFAULT_TRANSACTION_SYNCHRONIZATION_REGISTRY_NAME = "java:comp/TransactionSynchronizationRegistry";
    private transient JndiTemplate jndiTemplate = new JndiTemplate();
    @Nullable
    private transient UserTransaction userTransaction;
    @Nullable
    private String userTransactionName;
    private boolean autodetectUserTransaction = true;
    private boolean cacheUserTransaction = true;
    private boolean userTransactionObtainedFromJndi = false;
    @Nullable
    private transient TransactionManager transactionManager;
    @Nullable
    private String transactionManagerName;
    private boolean autodetectTransactionManager = true;
    @Nullable
    private transient TransactionSynchronizationRegistry transactionSynchronizationRegistry;
    @Nullable
    private String transactionSynchronizationRegistryName;
    private boolean autodetectTransactionSynchronizationRegistry = true;
    private boolean allowCustomIsolationLevels = false;

    public JtaTransactionManager() {
        this.setNestedTransactionAllowed(true);
    }

    public JtaTransactionManager(UserTransaction userTransaction) {
        this();
        Assert.notNull((Object)userTransaction, (String)"UserTransaction must not be null");
        this.userTransaction = userTransaction;
    }

    public JtaTransactionManager(UserTransaction userTransaction, TransactionManager transactionManager) {
        this();
        Assert.notNull((Object)userTransaction, (String)"UserTransaction must not be null");
        Assert.notNull((Object)transactionManager, (String)"TransactionManager must not be null");
        this.userTransaction = userTransaction;
        this.transactionManager = transactionManager;
    }

    public JtaTransactionManager(TransactionManager transactionManager) {
        this();
        Assert.notNull((Object)transactionManager, (String)"TransactionManager must not be null");
        this.transactionManager = transactionManager;
        this.userTransaction = this.buildUserTransaction(transactionManager);
    }

    public void setJndiTemplate(JndiTemplate jndiTemplate) {
        Assert.notNull((Object)jndiTemplate, (String)"JndiTemplate must not be null");
        this.jndiTemplate = jndiTemplate;
    }

    public JndiTemplate getJndiTemplate() {
        return this.jndiTemplate;
    }

    public void setJndiEnvironment(@Nullable Properties jndiEnvironment) {
        this.jndiTemplate = new JndiTemplate(jndiEnvironment);
    }

    @Nullable
    public Properties getJndiEnvironment() {
        return this.jndiTemplate.getEnvironment();
    }

    public void setUserTransaction(@Nullable UserTransaction userTransaction) {
        this.userTransaction = userTransaction;
    }

    @Nullable
    public UserTransaction getUserTransaction() {
        return this.userTransaction;
    }

    public void setUserTransactionName(String userTransactionName) {
        this.userTransactionName = userTransactionName;
    }

    public void setAutodetectUserTransaction(boolean autodetectUserTransaction) {
        this.autodetectUserTransaction = autodetectUserTransaction;
    }

    public void setCacheUserTransaction(boolean cacheUserTransaction) {
        this.cacheUserTransaction = cacheUserTransaction;
    }

    public void setTransactionManager(@Nullable TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Nullable
    public TransactionManager getTransactionManager() {
        return this.transactionManager;
    }

    public void setTransactionManagerName(String transactionManagerName) {
        this.transactionManagerName = transactionManagerName;
    }

    public void setAutodetectTransactionManager(boolean autodetectTransactionManager) {
        this.autodetectTransactionManager = autodetectTransactionManager;
    }

    public void setTransactionSynchronizationRegistry(@Nullable TransactionSynchronizationRegistry transactionSynchronizationRegistry) {
        this.transactionSynchronizationRegistry = transactionSynchronizationRegistry;
    }

    @Nullable
    public TransactionSynchronizationRegistry getTransactionSynchronizationRegistry() {
        return this.transactionSynchronizationRegistry;
    }

    public void setTransactionSynchronizationRegistryName(String transactionSynchronizationRegistryName) {
        this.transactionSynchronizationRegistryName = transactionSynchronizationRegistryName;
    }

    public void setAutodetectTransactionSynchronizationRegistry(boolean autodetectTransactionSynchronizationRegistry) {
        this.autodetectTransactionSynchronizationRegistry = autodetectTransactionSynchronizationRegistry;
    }

    public void setAllowCustomIsolationLevels(boolean allowCustomIsolationLevels) {
        this.allowCustomIsolationLevels = allowCustomIsolationLevels;
    }

    public void afterPropertiesSet() throws TransactionSystemException {
        this.initUserTransactionAndTransactionManager();
        this.checkUserTransactionAndTransactionManager();
        this.initTransactionSynchronizationRegistry();
    }

    protected void initUserTransactionAndTransactionManager() throws TransactionSystemException {
        if (this.userTransaction == null) {
            if (StringUtils.hasLength((String)this.userTransactionName)) {
                this.userTransaction = this.lookupUserTransaction(this.userTransactionName);
                this.userTransactionObtainedFromJndi = true;
            } else {
                this.userTransaction = this.retrieveUserTransaction();
                if (this.userTransaction == null && this.autodetectUserTransaction) {
                    this.userTransaction = this.findUserTransaction();
                }
            }
        }
        if (this.transactionManager == null) {
            if (StringUtils.hasLength((String)this.transactionManagerName)) {
                this.transactionManager = this.lookupTransactionManager(this.transactionManagerName);
            } else {
                this.transactionManager = this.retrieveTransactionManager();
                if (this.transactionManager == null && this.autodetectTransactionManager) {
                    this.transactionManager = this.findTransactionManager(this.userTransaction);
                }
            }
        }
        if (this.userTransaction == null && this.transactionManager != null) {
            this.userTransaction = this.buildUserTransaction(this.transactionManager);
        }
    }

    protected void checkUserTransactionAndTransactionManager() throws IllegalStateException {
        if (this.userTransaction != null) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Using JTA UserTransaction: " + this.userTransaction));
            }
        } else {
            throw new IllegalStateException("No JTA UserTransaction available - specify either 'userTransaction' or 'userTransactionName' or 'transactionManager' or 'transactionManagerName'");
        }
        if (this.transactionManager != null) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Using JTA TransactionManager: " + this.transactionManager));
            }
        } else {
            this.logger.warn((Object)"No JTA TransactionManager found: transaction suspension not available");
        }
    }

    protected void initTransactionSynchronizationRegistry() {
        if (this.transactionSynchronizationRegistry == null) {
            if (StringUtils.hasLength((String)this.transactionSynchronizationRegistryName)) {
                this.transactionSynchronizationRegistry = this.lookupTransactionSynchronizationRegistry(this.transactionSynchronizationRegistryName);
            } else {
                this.transactionSynchronizationRegistry = this.retrieveTransactionSynchronizationRegistry();
                if (this.transactionSynchronizationRegistry == null && this.autodetectTransactionSynchronizationRegistry) {
                    this.transactionSynchronizationRegistry = this.findTransactionSynchronizationRegistry(this.userTransaction, this.transactionManager);
                }
            }
        }
        if (this.transactionSynchronizationRegistry != null && this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("Using JTA TransactionSynchronizationRegistry: " + this.transactionSynchronizationRegistry));
        }
    }

    protected UserTransaction buildUserTransaction(TransactionManager transactionManager) {
        if (transactionManager instanceof UserTransaction) {
            return (UserTransaction)transactionManager;
        }
        return new UserTransactionAdapter(transactionManager);
    }

    protected UserTransaction lookupUserTransaction(String userTransactionName) throws TransactionSystemException {
        try {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Retrieving JTA UserTransaction from JNDI location [" + userTransactionName + "]"));
            }
            return (UserTransaction)this.getJndiTemplate().lookup(userTransactionName, UserTransaction.class);
        }
        catch (NamingException ex) {
            throw new TransactionSystemException("JTA UserTransaction is not available at JNDI location [" + userTransactionName + "]", ex);
        }
    }

    protected TransactionManager lookupTransactionManager(String transactionManagerName) throws TransactionSystemException {
        try {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Retrieving JTA TransactionManager from JNDI location [" + transactionManagerName + "]"));
            }
            return (TransactionManager)this.getJndiTemplate().lookup(transactionManagerName, TransactionManager.class);
        }
        catch (NamingException ex) {
            throw new TransactionSystemException("JTA TransactionManager is not available at JNDI location [" + transactionManagerName + "]", ex);
        }
    }

    protected TransactionSynchronizationRegistry lookupTransactionSynchronizationRegistry(String registryName) throws TransactionSystemException {
        try {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Retrieving JTA TransactionSynchronizationRegistry from JNDI location [" + registryName + "]"));
            }
            return (TransactionSynchronizationRegistry)this.getJndiTemplate().lookup(registryName, TransactionSynchronizationRegistry.class);
        }
        catch (NamingException ex) {
            throw new TransactionSystemException("JTA TransactionSynchronizationRegistry is not available at JNDI location [" + registryName + "]", ex);
        }
    }

    @Nullable
    protected UserTransaction retrieveUserTransaction() throws TransactionSystemException {
        return null;
    }

    @Nullable
    protected TransactionManager retrieveTransactionManager() throws TransactionSystemException {
        return null;
    }

    @Nullable
    protected TransactionSynchronizationRegistry retrieveTransactionSynchronizationRegistry() throws TransactionSystemException {
        return null;
    }

    @Nullable
    protected UserTransaction findUserTransaction() {
        String jndiName = DEFAULT_USER_TRANSACTION_NAME;
        try {
            UserTransaction ut = (UserTransaction)this.getJndiTemplate().lookup(jndiName, UserTransaction.class);
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("JTA UserTransaction found at default JNDI location [" + jndiName + "]"));
            }
            this.userTransactionObtainedFromJndi = true;
            return ut;
        }
        catch (NamingException ex) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("No JTA UserTransaction found at default JNDI location [" + jndiName + "]"), (Throwable)ex);
            }
            return null;
        }
    }

    @Nullable
    protected TransactionManager findTransactionManager(@Nullable UserTransaction ut) {
        if (ut instanceof TransactionManager) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("JTA UserTransaction object [" + ut + "] implements TransactionManager"));
            }
            return (TransactionManager)ut;
        }
        for (String jndiName : FALLBACK_TRANSACTION_MANAGER_NAMES) {
            try {
                TransactionManager tm = (TransactionManager)this.getJndiTemplate().lookup(jndiName, TransactionManager.class);
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug((Object)("JTA TransactionManager found at fallback JNDI location [" + jndiName + "]"));
                }
                return tm;
            }
            catch (NamingException ex) {
                if (!this.logger.isDebugEnabled()) continue;
                this.logger.debug((Object)("No JTA TransactionManager found at fallback JNDI location [" + jndiName + "]"), (Throwable)ex);
            }
        }
        return null;
    }

    @Nullable
    protected TransactionSynchronizationRegistry findTransactionSynchronizationRegistry(@Nullable UserTransaction ut, @Nullable TransactionManager tm) throws TransactionSystemException {
        block6: {
            if (this.userTransactionObtainedFromJndi) {
                String jndiName = DEFAULT_TRANSACTION_SYNCHRONIZATION_REGISTRY_NAME;
                try {
                    TransactionSynchronizationRegistry tsr = (TransactionSynchronizationRegistry)this.getJndiTemplate().lookup(jndiName, TransactionSynchronizationRegistry.class);
                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug((Object)("JTA TransactionSynchronizationRegistry found at default JNDI location [" + jndiName + "]"));
                    }
                    return tsr;
                }
                catch (NamingException ex) {
                    if (!this.logger.isDebugEnabled()) break block6;
                    this.logger.debug((Object)("No JTA TransactionSynchronizationRegistry found at default JNDI location [" + jndiName + "]"), (Throwable)ex);
                }
            }
        }
        if (ut instanceof TransactionSynchronizationRegistry) {
            return (TransactionSynchronizationRegistry)ut;
        }
        if (tm instanceof TransactionSynchronizationRegistry) {
            return (TransactionSynchronizationRegistry)tm;
        }
        return null;
    }

    @Override
    protected Object doGetTransaction() {
        UserTransaction ut = this.getUserTransaction();
        if (ut == null) {
            throw new CannotCreateTransactionException("No JTA UserTransaction available - programmatic PlatformTransactionManager.getTransaction usage not supported");
        }
        if (!this.cacheUserTransaction) {
            ut = this.lookupUserTransaction(this.userTransactionName != null ? this.userTransactionName : DEFAULT_USER_TRANSACTION_NAME);
        }
        return this.doGetJtaTransaction(ut);
    }

    protected JtaTransactionObject doGetJtaTransaction(UserTransaction ut) {
        return new JtaTransactionObject(ut);
    }

    @Override
    protected boolean isExistingTransaction(Object transaction) {
        JtaTransactionObject txObject = (JtaTransactionObject)transaction;
        try {
            return txObject.getUserTransaction().getStatus() != 6;
        }
        catch (SystemException ex) {
            throw new TransactionSystemException("JTA failure on getStatus", ex);
        }
    }

    @Override
    protected boolean useSavepointForNestedTransaction() {
        return false;
    }

    @Override
    protected void doBegin(Object transaction, TransactionDefinition definition) {
        JtaTransactionObject txObject = (JtaTransactionObject)transaction;
        try {
            this.doJtaBegin(txObject, definition);
        }
        catch (UnsupportedOperationException | NotSupportedException ex) {
            throw new NestedTransactionNotSupportedException("JTA implementation does not support nested transactions", ex);
        }
        catch (SystemException ex) {
            throw new CannotCreateTransactionException("JTA failure on begin", ex);
        }
    }

    protected void doJtaBegin(JtaTransactionObject txObject, TransactionDefinition definition) throws NotSupportedException, SystemException {
        this.applyIsolationLevel(txObject, definition.getIsolationLevel());
        int timeout = this.determineTimeout(definition);
        this.applyTimeout(txObject, timeout);
        txObject.getUserTransaction().begin();
    }

    protected void applyIsolationLevel(JtaTransactionObject txObject, int isolationLevel) throws InvalidIsolationLevelException, SystemException {
        if (!this.allowCustomIsolationLevels && isolationLevel != -1) {
            throw new InvalidIsolationLevelException("JtaTransactionManager does not support custom isolation levels by default - switch 'allowCustomIsolationLevels' to 'true'");
        }
    }

    protected void applyTimeout(JtaTransactionObject txObject, int timeout) throws SystemException {
        if (timeout > -1) {
            txObject.getUserTransaction().setTransactionTimeout(timeout);
            if (timeout > 0) {
                txObject.resetTransactionTimeout = true;
            }
        }
    }

    @Override
    protected Object doSuspend(Object transaction) {
        JtaTransactionObject txObject = (JtaTransactionObject)transaction;
        try {
            return this.doJtaSuspend(txObject);
        }
        catch (SystemException ex) {
            throw new TransactionSystemException("JTA failure on suspend", ex);
        }
    }

    protected Object doJtaSuspend(JtaTransactionObject txObject) throws SystemException {
        if (this.getTransactionManager() == null) {
            throw new TransactionSuspensionNotSupportedException("JtaTransactionManager needs a JTA TransactionManager for suspending a transaction: specify the 'transactionManager' or 'transactionManagerName' property");
        }
        return this.getTransactionManager().suspend();
    }

    @Override
    protected void doResume(@Nullable Object transaction, Object suspendedResources) {
        JtaTransactionObject txObject = (JtaTransactionObject)transaction;
        try {
            this.doJtaResume(txObject, suspendedResources);
        }
        catch (InvalidTransactionException ex) {
            throw new IllegalTransactionStateException("Tried to resume invalid JTA transaction", ex);
        }
        catch (IllegalStateException ex) {
            throw new TransactionSystemException("Unexpected internal transaction state", ex);
        }
        catch (SystemException ex) {
            throw new TransactionSystemException("JTA failure on resume", ex);
        }
    }

    protected void doJtaResume(@Nullable JtaTransactionObject txObject, Object suspendedTransaction) throws InvalidTransactionException, SystemException {
        if (this.getTransactionManager() == null) {
            throw new TransactionSuspensionNotSupportedException("JtaTransactionManager needs a JTA TransactionManager for suspending a transaction: specify the 'transactionManager' or 'transactionManagerName' property");
        }
        this.getTransactionManager().resume((Transaction)suspendedTransaction);
    }

    @Override
    protected boolean shouldCommitOnGlobalRollbackOnly() {
        return true;
    }

    @Override
    protected void doCommit(DefaultTransactionStatus status) {
        JtaTransactionObject txObject = (JtaTransactionObject)status.getTransaction();
        try {
            int jtaStatus = txObject.getUserTransaction().getStatus();
            if (jtaStatus == 6) {
                throw new UnexpectedRollbackException("JTA transaction already completed - probably rolled back");
            }
            if (jtaStatus == 4) {
                block10: {
                    try {
                        txObject.getUserTransaction().rollback();
                    }
                    catch (IllegalStateException ex) {
                        if (!this.logger.isDebugEnabled()) break block10;
                        this.logger.debug((Object)("Rollback failure with transaction already marked as rolled back: " + ex));
                    }
                }
                throw new UnexpectedRollbackException("JTA transaction already rolled back (probably due to a timeout)");
            }
            txObject.getUserTransaction().commit();
        }
        catch (RollbackException ex) {
            throw new UnexpectedRollbackException("JTA transaction unexpectedly rolled back (maybe due to a timeout)", ex);
        }
        catch (HeuristicMixedException ex) {
            throw new HeuristicCompletionException(3, (Throwable)ex);
        }
        catch (HeuristicRollbackException ex) {
            throw new HeuristicCompletionException(2, (Throwable)ex);
        }
        catch (IllegalStateException ex) {
            throw new TransactionSystemException("Unexpected internal transaction state", ex);
        }
        catch (SystemException ex) {
            throw new TransactionSystemException("JTA failure on commit", ex);
        }
    }

    @Override
    protected void doRollback(DefaultTransactionStatus status) {
        block6: {
            JtaTransactionObject txObject = (JtaTransactionObject)status.getTransaction();
            try {
                int jtaStatus = txObject.getUserTransaction().getStatus();
                if (jtaStatus == 6) break block6;
                try {
                    txObject.getUserTransaction().rollback();
                }
                catch (IllegalStateException ex) {
                    if (jtaStatus == 4) {
                        if (this.logger.isDebugEnabled()) {
                            this.logger.debug((Object)("Rollback failure with transaction already marked as rolled back: " + ex));
                        }
                        break block6;
                    }
                    throw new TransactionSystemException("Unexpected internal transaction state", ex);
                }
            }
            catch (SystemException ex) {
                throw new TransactionSystemException("JTA failure on rollback", ex);
            }
        }
    }

    @Override
    protected void doSetRollbackOnly(DefaultTransactionStatus status) {
        JtaTransactionObject txObject = (JtaTransactionObject)status.getTransaction();
        if (status.isDebug()) {
            this.logger.debug((Object)"Setting JTA transaction rollback-only");
        }
        try {
            int jtaStatus = txObject.getUserTransaction().getStatus();
            if (jtaStatus != 6 && jtaStatus != 4) {
                txObject.getUserTransaction().setRollbackOnly();
            }
        }
        catch (IllegalStateException ex) {
            throw new TransactionSystemException("Unexpected internal transaction state", ex);
        }
        catch (SystemException ex) {
            throw new TransactionSystemException("JTA failure on setRollbackOnly", ex);
        }
    }

    @Override
    protected void registerAfterCompletionWithExistingTransaction(Object transaction, List<TransactionSynchronization> synchronizations) {
        JtaTransactionObject txObject = (JtaTransactionObject)transaction;
        this.logger.debug((Object)"Registering after-completion synchronization with existing JTA transaction");
        try {
            this.doRegisterAfterCompletionWithJtaTransaction(txObject, synchronizations);
        }
        catch (SystemException ex) {
            throw new TransactionSystemException("JTA failure on registerSynchronization", ex);
        }
        catch (Exception ex) {
            if (ex instanceof RollbackException || ex.getCause() instanceof RollbackException) {
                this.logger.debug((Object)("Participating in existing JTA transaction that has been marked for rollback: cannot register Spring after-completion callbacks with outer JTA transaction - immediately performing Spring after-completion callbacks with outcome status 'rollback'. Original exception: " + ex));
                this.invokeAfterCompletion(synchronizations, 1);
            }
            this.logger.debug((Object)("Participating in existing JTA transaction, but unexpected internal transaction state encountered: cannot register Spring after-completion callbacks with outer JTA transaction - processing Spring after-completion callbacks with outcome status 'unknown'Original exception: " + ex));
            this.invokeAfterCompletion(synchronizations, 2);
        }
    }

    protected void doRegisterAfterCompletionWithJtaTransaction(JtaTransactionObject txObject, List<TransactionSynchronization> synchronizations) throws RollbackException, SystemException {
        int jtaStatus = txObject.getUserTransaction().getStatus();
        if (jtaStatus == 6) {
            throw new RollbackException("JTA transaction already completed - probably rolled back");
        }
        if (jtaStatus == 4) {
            throw new RollbackException("JTA transaction already rolled back (probably due to a timeout)");
        }
        if (this.transactionSynchronizationRegistry != null) {
            this.transactionSynchronizationRegistry.registerInterposedSynchronization((Synchronization)new JtaAfterCompletionSynchronization(synchronizations));
        } else if (this.getTransactionManager() != null) {
            Transaction transaction = this.getTransactionManager().getTransaction();
            if (transaction == null) {
                throw new IllegalStateException("No JTA Transaction available");
            }
            transaction.registerSynchronization((Synchronization)new JtaAfterCompletionSynchronization(synchronizations));
        } else {
            this.logger.warn((Object)"Participating in existing JTA transaction, but no JTA TransactionManager available: cannot register Spring after-completion callbacks with outer JTA transaction - processing Spring after-completion callbacks with outcome status 'unknown'");
            this.invokeAfterCompletion(synchronizations, 2);
        }
    }

    @Override
    protected void doCleanupAfterCompletion(Object transaction) {
        JtaTransactionObject txObject = (JtaTransactionObject)transaction;
        if (txObject.resetTransactionTimeout) {
            try {
                txObject.getUserTransaction().setTransactionTimeout(0);
            }
            catch (SystemException ex) {
                this.logger.debug((Object)"Failed to reset transaction timeout after JTA completion", (Throwable)ex);
            }
        }
    }

    @Override
    public Transaction createTransaction(@Nullable String name, int timeout) throws NotSupportedException, SystemException {
        TransactionManager tm = this.getTransactionManager();
        Assert.state((tm != null ? 1 : 0) != 0, (String)"No JTA TransactionManager available");
        if (timeout >= 0) {
            tm.setTransactionTimeout(timeout);
        }
        tm.begin();
        return new ManagedTransactionAdapter(tm);
    }

    @Override
    public boolean supportsResourceAdapterManagedTransactions() {
        return false;
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        this.jndiTemplate = new JndiTemplate();
        this.initUserTransactionAndTransactionManager();
        this.initTransactionSynchronizationRegistry();
    }
}

