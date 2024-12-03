/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.support.TransactionSynchronizationManager
 */
package org.springframework.ldap.transaction.compensating.manager;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.transaction.compensating.LdapTransactionUtils;
import org.springframework.ldap.transaction.compensating.manager.DirContextHolder;
import org.springframework.transaction.compensating.support.CompensatingTransactionUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class TransactionAwareDirContextInvocationHandler
implements InvocationHandler {
    private static Logger log = LoggerFactory.getLogger(TransactionAwareDirContextInvocationHandler.class);
    private DirContext target;
    private ContextSource contextSource;

    public TransactionAwareDirContextInvocationHandler(DirContext target, ContextSource contextSource) {
        this.target = target;
        this.contextSource = contextSource;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        if (methodName.equals("getTargetContext")) {
            return this.target;
        }
        if (methodName.equals("equals")) {
            return proxy == args[0] ? Boolean.TRUE : Boolean.FALSE;
        }
        if (methodName.equals("hashCode")) {
            return this.hashCode();
        }
        if (methodName.equals("close")) {
            this.doCloseConnection(this.target, this.contextSource);
            return null;
        }
        if (LdapTransactionUtils.isSupportedWriteTransactionOperation(methodName)) {
            CompensatingTransactionUtils.performOperation(this.contextSource, this.target, method, args);
            return null;
        }
        try {
            return method.invoke((Object)this.target, args);
        }
        catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

    void doCloseConnection(DirContext context, ContextSource contextSource) throws NamingException {
        DirContextHolder transactionContextHolder = (DirContextHolder)((Object)TransactionSynchronizationManager.getResource((Object)contextSource));
        if (transactionContextHolder == null || transactionContextHolder.getCtx() != context) {
            log.debug("Closing context");
            context.close();
        } else {
            log.debug("Leaving transactional context open");
        }
    }
}

