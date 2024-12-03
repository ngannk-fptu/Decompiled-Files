/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.transaction.InvalidTransactionException
 *  javax.transaction.NotSupportedException
 *  javax.transaction.SystemException
 *  javax.transaction.Transaction
 *  javax.transaction.TransactionManager
 *  javax.transaction.UserTransaction
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.transaction.jta;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.transaction.InvalidTransactionException;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import org.springframework.lang.Nullable;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.transaction.jta.JtaTransactionObject;
import org.springframework.transaction.jta.ManagedTransactionAdapter;
import org.springframework.util.Assert;

public class WebLogicJtaTransactionManager
extends JtaTransactionManager {
    private static final String USER_TRANSACTION_CLASS_NAME = "weblogic.transaction.UserTransaction";
    private static final String CLIENT_TRANSACTION_MANAGER_CLASS_NAME = "weblogic.transaction.ClientTransactionManager";
    private static final String TRANSACTION_CLASS_NAME = "weblogic.transaction.Transaction";
    private static final String TRANSACTION_HELPER_CLASS_NAME = "weblogic.transaction.TransactionHelper";
    private static final String ISOLATION_LEVEL_KEY = "ISOLATION LEVEL";
    private boolean weblogicUserTransactionAvailable;
    @Nullable
    private Method beginWithNameMethod;
    @Nullable
    private Method beginWithNameAndTimeoutMethod;
    private boolean weblogicTransactionManagerAvailable;
    @Nullable
    private Method forceResumeMethod;
    @Nullable
    private Method setPropertyMethod;
    @Nullable
    private Object transactionHelper;

    @Override
    public void afterPropertiesSet() throws TransactionSystemException {
        super.afterPropertiesSet();
        this.loadWebLogicTransactionClasses();
    }

    @Override
    @Nullable
    protected UserTransaction retrieveUserTransaction() throws TransactionSystemException {
        Object helper = this.loadWebLogicTransactionHelper();
        try {
            this.logger.trace((Object)"Retrieving JTA UserTransaction from WebLogic TransactionHelper");
            Method getUserTransactionMethod = helper.getClass().getMethod("getUserTransaction", new Class[0]);
            return (UserTransaction)getUserTransactionMethod.invoke(this.transactionHelper, new Object[0]);
        }
        catch (InvocationTargetException ex) {
            throw new TransactionSystemException("WebLogic's TransactionHelper.getUserTransaction() method failed", ex.getTargetException());
        }
        catch (Exception ex) {
            throw new TransactionSystemException("Could not invoke WebLogic's TransactionHelper.getUserTransaction() method", ex);
        }
    }

    @Override
    @Nullable
    protected TransactionManager retrieveTransactionManager() throws TransactionSystemException {
        Object helper = this.loadWebLogicTransactionHelper();
        try {
            this.logger.trace((Object)"Retrieving JTA TransactionManager from WebLogic TransactionHelper");
            Method getTransactionManagerMethod = helper.getClass().getMethod("getTransactionManager", new Class[0]);
            return (TransactionManager)getTransactionManagerMethod.invoke(this.transactionHelper, new Object[0]);
        }
        catch (InvocationTargetException ex) {
            throw new TransactionSystemException("WebLogic's TransactionHelper.getTransactionManager() method failed", ex.getTargetException());
        }
        catch (Exception ex) {
            throw new TransactionSystemException("Could not invoke WebLogic's TransactionHelper.getTransactionManager() method", ex);
        }
    }

    private Object loadWebLogicTransactionHelper() throws TransactionSystemException {
        Object helper = this.transactionHelper;
        if (helper == null) {
            try {
                Class<?> transactionHelperClass = this.getClass().getClassLoader().loadClass(TRANSACTION_HELPER_CLASS_NAME);
                Method getTransactionHelperMethod = transactionHelperClass.getMethod("getTransactionHelper", new Class[0]);
                this.transactionHelper = helper = getTransactionHelperMethod.invoke(null, new Object[0]);
                this.logger.trace((Object)"WebLogic TransactionHelper found");
            }
            catch (InvocationTargetException ex) {
                throw new TransactionSystemException("WebLogic's TransactionHelper.getTransactionHelper() method failed", ex.getTargetException());
            }
            catch (Exception ex) {
                throw new TransactionSystemException("Could not initialize WebLogicJtaTransactionManager because WebLogic API classes are not available", ex);
            }
        }
        return helper;
    }

    private void loadWebLogicTransactionClasses() throws TransactionSystemException {
        try {
            Class<?> userTransactionClass = this.getClass().getClassLoader().loadClass(USER_TRANSACTION_CLASS_NAME);
            this.weblogicUserTransactionAvailable = userTransactionClass.isInstance(this.getUserTransaction());
            if (this.weblogicUserTransactionAvailable) {
                this.beginWithNameMethod = userTransactionClass.getMethod("begin", String.class);
                this.beginWithNameAndTimeoutMethod = userTransactionClass.getMethod("begin", String.class, Integer.TYPE);
                this.logger.debug((Object)"Support for WebLogic transaction names available");
            } else {
                this.logger.debug((Object)"Support for WebLogic transaction names not available");
            }
            Class<?> transactionManagerClass = this.getClass().getClassLoader().loadClass(CLIENT_TRANSACTION_MANAGER_CLASS_NAME);
            this.logger.trace((Object)"WebLogic ClientTransactionManager found");
            this.weblogicTransactionManagerAvailable = transactionManagerClass.isInstance(this.getTransactionManager());
            if (this.weblogicTransactionManagerAvailable) {
                Class<?> transactionClass = this.getClass().getClassLoader().loadClass(TRANSACTION_CLASS_NAME);
                this.forceResumeMethod = transactionManagerClass.getMethod("forceResume", Transaction.class);
                this.setPropertyMethod = transactionClass.getMethod("setProperty", String.class, Serializable.class);
                this.logger.debug((Object)"Support for WebLogic forceResume available");
            } else {
                this.logger.debug((Object)"Support for WebLogic forceResume not available");
            }
        }
        catch (Exception ex) {
            throw new TransactionSystemException("Could not initialize WebLogicJtaTransactionManager because WebLogic API classes are not available", ex);
        }
    }

    private TransactionManager obtainTransactionManager() {
        TransactionManager tm = this.getTransactionManager();
        Assert.state((tm != null ? 1 : 0) != 0, (String)"No TransactionManager set");
        return tm;
    }

    @Override
    protected void doJtaBegin(JtaTransactionObject txObject, TransactionDefinition definition) throws NotSupportedException, SystemException {
        int timeout = this.determineTimeout(definition);
        if (this.weblogicUserTransactionAvailable && definition.getName() != null) {
            try {
                if (timeout > -1) {
                    Assert.state((this.beginWithNameAndTimeoutMethod != null ? 1 : 0) != 0, (String)"WebLogic JTA API not initialized");
                    this.beginWithNameAndTimeoutMethod.invoke((Object)txObject.getUserTransaction(), definition.getName(), timeout);
                }
                Assert.state((this.beginWithNameMethod != null ? 1 : 0) != 0, (String)"WebLogic JTA API not initialized");
                this.beginWithNameMethod.invoke((Object)txObject.getUserTransaction(), definition.getName());
            }
            catch (InvocationTargetException ex) {
                throw new TransactionSystemException("WebLogic's UserTransaction.begin() method failed", ex.getTargetException());
            }
            catch (Exception ex) {
                throw new TransactionSystemException("Could not invoke WebLogic's UserTransaction.begin() method", ex);
            }
        } else {
            this.applyTimeout(txObject, timeout);
            txObject.getUserTransaction().begin();
        }
        if (this.weblogicTransactionManagerAvailable) {
            if (definition.getIsolationLevel() != -1) {
                try {
                    Transaction tx = this.obtainTransactionManager().getTransaction();
                    Integer isolationLevel = definition.getIsolationLevel();
                    Assert.state((this.setPropertyMethod != null ? 1 : 0) != 0, (String)"WebLogic JTA API not initialized");
                    this.setPropertyMethod.invoke((Object)tx, ISOLATION_LEVEL_KEY, isolationLevel);
                }
                catch (InvocationTargetException ex) {
                    throw new TransactionSystemException("WebLogic's Transaction.setProperty(String, Serializable) method failed", ex.getTargetException());
                }
                catch (Exception ex) {
                    throw new TransactionSystemException("Could not invoke WebLogic's Transaction.setProperty(String, Serializable) method", ex);
                }
            }
        } else {
            this.applyIsolationLevel(txObject, definition.getIsolationLevel());
        }
    }

    @Override
    protected void doJtaResume(@Nullable JtaTransactionObject txObject, Object suspendedTransaction) throws InvalidTransactionException, SystemException {
        try {
            this.obtainTransactionManager().resume((Transaction)suspendedTransaction);
        }
        catch (InvalidTransactionException ex) {
            if (!this.weblogicTransactionManagerAvailable) {
                throw ex;
            }
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Standard JTA resume threw InvalidTransactionException: " + ex.getMessage() + " - trying WebLogic JTA forceResume"));
            }
            try {
                Assert.state((this.forceResumeMethod != null ? 1 : 0) != 0, (String)"WebLogic JTA API not initialized");
                this.forceResumeMethod.invoke((Object)this.getTransactionManager(), suspendedTransaction);
            }
            catch (InvocationTargetException ex2) {
                throw new TransactionSystemException("WebLogic's TransactionManager.forceResume(Transaction) method failed", ex2.getTargetException());
            }
            catch (Exception ex2) {
                throw new TransactionSystemException("Could not access WebLogic's TransactionManager.forceResume(Transaction) method", ex2);
            }
        }
    }

    @Override
    public Transaction createTransaction(@Nullable String name, int timeout) throws NotSupportedException, SystemException {
        if (this.weblogicUserTransactionAvailable && name != null) {
            try {
                if (timeout >= 0) {
                    Assert.state((this.beginWithNameAndTimeoutMethod != null ? 1 : 0) != 0, (String)"WebLogic JTA API not initialized");
                    this.beginWithNameAndTimeoutMethod.invoke((Object)this.getUserTransaction(), name, timeout);
                } else {
                    Assert.state((this.beginWithNameMethod != null ? 1 : 0) != 0, (String)"WebLogic JTA API not initialized");
                    this.beginWithNameMethod.invoke((Object)this.getUserTransaction(), name);
                }
            }
            catch (InvocationTargetException ex) {
                if (ex.getTargetException() instanceof NotSupportedException) {
                    throw (NotSupportedException)ex.getTargetException();
                }
                if (ex.getTargetException() instanceof SystemException) {
                    throw (SystemException)ex.getTargetException();
                }
                if (ex.getTargetException() instanceof RuntimeException) {
                    throw (RuntimeException)ex.getTargetException();
                }
                throw new SystemException("WebLogic's begin() method failed with an unexpected error: " + ex.getTargetException());
            }
            catch (Exception ex) {
                throw new SystemException("Could not invoke WebLogic's UserTransaction.begin() method: " + ex);
            }
            return new ManagedTransactionAdapter(this.obtainTransactionManager());
        }
        return super.createTransaction(name, timeout);
    }
}

