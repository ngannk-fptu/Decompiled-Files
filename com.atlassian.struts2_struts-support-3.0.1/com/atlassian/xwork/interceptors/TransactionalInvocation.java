/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.util.profiling.ProfilingUtils
 *  com.opensymphony.xwork2.ActionInvocation
 *  com.opensymphony.xwork2.ActionProxy
 *  com.opensymphony.xwork2.interceptor.PreResultListener
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.PlatformTransactionManager
 *  org.springframework.transaction.TransactionDefinition
 *  org.springframework.transaction.TransactionStatus
 *  org.springframework.transaction.interceptor.DefaultTransactionAttribute
 *  org.springframework.transaction.interceptor.TransactionAttribute
 */
package com.atlassian.xwork.interceptors;

import com.atlassian.util.profiling.ProfilingUtils;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;

class TransactionalInvocation {
    private static final Logger log = LoggerFactory.getLogger(TransactionalInvocation.class);
    private final TransactionAttribute transactionAttribute = new DefaultTransactionAttribute(0);
    private final PlatformTransactionManager transactionManager;
    private TransactionStatus transactionStatus;

    public TransactionalInvocation(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String invokeInTransaction(final ActionInvocation invocation) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("Creating transaction for action invocation: " + this.getDetails(invocation));
        }
        this.setTransactionStatus(this.getNewTransaction());
        invocation.addPreResultListener(new PreResultListener(){

            public void beforeResult(ActionInvocation actionInvocation, String s) {
                TransactionalInvocation.this.commitOrRollbackTransaction(invocation, false);
                if (log.isDebugEnabled()) {
                    log.debug("Creating transaction for action result: " + TransactionalInvocation.this.getDetails(invocation));
                }
                TransactionalInvocation.this.setTransactionStatus(TransactionalInvocation.this.getNewTransaction());
            }
        });
        boolean swallowCommitErrors = true;
        try {
            String result = this.invokeAndHandleExceptions(invocation);
            swallowCommitErrors = false;
            String string = result;
            return string;
        }
        finally {
            this.commitOrRollbackTransaction(invocation, swallowCommitErrors);
        }
    }

    private String invokeAndHandleExceptions(ActionInvocation invocation) throws Exception {
        try {
            return invocation.invoke();
        }
        catch (Exception ex) {
            this.handleInvocationException(invocation, this.transactionAttribute, this.transactionStatus, ex);
            throw ex;
        }
    }

    private void commitOrRollbackTransaction(ActionInvocation actionInvocation, boolean swallowCommitErrors) {
        try {
            if (this.transactionStatus.isCompleted()) {
                log.error("Action " + this.getDetails(actionInvocation) + " is already completed and can not be committed again.");
            } else if (this.transactionStatus.isRollbackOnly()) {
                if (log.isDebugEnabled()) {
                    log.debug("Transaction status for action " + this.getDetails(actionInvocation) + " set to rollback only. Invoking rollback()");
                }
                this.transactionManager.rollback(this.transactionStatus);
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Committing transaction for action " + this.getDetails(actionInvocation));
                }
                this.transactionManager.commit(this.transactionStatus);
            }
        }
        catch (RuntimeException e) {
            if (swallowCommitErrors) {
                log.error("Commit/Rollback exception occurred but was swallowed", (Throwable)e);
            }
            throw e;
        }
    }

    private void handleInvocationException(ActionInvocation invocation, TransactionAttribute txAtt, TransactionStatus status, Throwable ex) {
        if (status == null) {
            return;
        }
        if (txAtt.rollbackOn(ex)) {
            log.info("Invoking rollback for transaction on action '" + this.getDetails(invocation) + "' due to throwable: " + ex, ex);
            status.setRollbackOnly();
        } else if (log.isDebugEnabled()) {
            log.debug("Action " + this.getDetails(invocation) + " threw exception " + ex + " but did not trigger a rollback.");
        }
    }

    private TransactionStatus getNewTransaction() {
        return this.transactionManager.getTransaction((TransactionDefinition)this.transactionAttribute);
    }

    private void setTransactionStatus(TransactionStatus transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    private String getDetails(ActionInvocation invocation) {
        ActionProxy proxy = invocation.getProxy();
        String methodName = proxy.getConfig().getMethodName();
        if (methodName == null) {
            methodName = "execute";
        }
        String actionClazz = ProfilingUtils.getJustClassName((String)proxy.getConfig().getClassName());
        return proxy.getNamespace() + "/" + proxy.getActionName() + ".action (" + actionClazz + "." + methodName + "())";
    }
}

