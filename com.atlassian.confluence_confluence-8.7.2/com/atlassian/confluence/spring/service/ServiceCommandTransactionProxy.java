/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.transaction.PlatformTransactionManager
 *  org.springframework.transaction.TransactionDefinition
 *  org.springframework.transaction.TransactionStatus
 *  org.springframework.transaction.support.DefaultTransactionDefinition
 */
package com.atlassian.confluence.spring.service;

import com.atlassian.confluence.core.service.NotAuthorizedException;
import com.atlassian.confluence.core.service.NotValidException;
import com.atlassian.confluence.core.service.ServiceCommand;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class ServiceCommandTransactionProxy {
    private final PlatformTransactionManager transactionManager;
    private final String isolationLevel;
    private final String propagation;

    public ServiceCommandTransactionProxy(PlatformTransactionManager transactionManager) {
        this(transactionManager, "ISOLATION_DEFAULT", "PROPAGATION_REQUIRED");
    }

    public ServiceCommandTransactionProxy(PlatformTransactionManager transactionManager, String isolationLevel, String propagation) {
        this.transactionManager = transactionManager;
        this.isolationLevel = isolationLevel;
        this.propagation = propagation;
    }

    public ServiceCommand proxyCommand(ServiceCommand command, Class proxyInterface) {
        return (ServiceCommand)Proxy.newProxyInstance(command.getClass().getClassLoader(), new Class[]{proxyInterface}, (InvocationHandler)new TransactionProxyInvocationHandler(command, this.transactionManager, this.isolationLevel, this.propagation));
    }

    private static class TransactionProxyInvocationHandler
    implements InvocationHandler {
        private static final String METHOD_NAME_EXECUTE = "execute";
        private static final String METHOD_NAME_IS_AUTHORIZED = "isAuthorized";
        private static final String METHOD_NAME_IS_VALID = "isValid";
        private final PlatformTransactionManager transactionManager;
        private final Object delegate;
        private final String isolationLevel;
        private final String propagation;

        public TransactionProxyInvocationHandler(Object delegate, PlatformTransactionManager transactionManager, String isolationLevel, String propagation) {
            this.delegate = delegate;
            this.transactionManager = transactionManager;
            this.isolationLevel = isolationLevel;
            this.propagation = propagation;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            TransactionStatus transactionStatus = null;
            if (this.isTransactionAware(method.getName())) {
                transactionStatus = this.openTransaction();
            }
            boolean shouldClose = false;
            try {
                Object retValue = method.invoke(this.delegate, args);
                shouldClose = true;
                Object object = retValue;
                return object;
            }
            catch (InvocationTargetException e) {
                if (!this.isExpectedException(e.getTargetException())) {
                    this.rollbackTransaction(transactionStatus);
                } else {
                    shouldClose = true;
                }
                throw e.getTargetException();
            }
            catch (Exception e) {
                this.rollbackTransaction(transactionStatus);
                throw e;
            }
            finally {
                if (shouldClose) {
                    this.closeTransaction(transactionStatus);
                }
            }
        }

        private void rollbackTransaction(TransactionStatus transactionStatus) {
            if (transactionStatus != null) {
                this.transactionManager.rollback(transactionStatus);
            }
        }

        private void closeTransaction(TransactionStatus transactionStatus) {
            if (transactionStatus != null) {
                this.transactionManager.commit(transactionStatus);
            }
        }

        private TransactionStatus openTransaction() {
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            def.setIsolationLevelName(this.isolationLevel);
            def.setPropagationBehaviorName(this.propagation);
            return this.transactionManager.getTransaction((TransactionDefinition)def);
        }

        private boolean isExpectedException(Throwable exception) {
            return exception instanceof NotValidException || exception instanceof NotAuthorizedException;
        }

        private boolean isTransactionAware(String methodName) {
            return METHOD_NAME_IS_AUTHORIZED.equals(methodName) || METHOD_NAME_EXECUTE.equals(methodName) || METHOD_NAME_IS_VALID.equals(methodName);
        }
    }
}

