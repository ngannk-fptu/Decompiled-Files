/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.intercept.MethodInterceptor
 *  org.aopalliance.intercept.MethodInvocation
 *  org.springframework.aop.support.AopUtils
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.lang.Nullable
 */
package org.springframework.transaction.interceptor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Properties;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.lang.Nullable;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.interceptor.TransactionAttributeSource;

public class TransactionInterceptor
extends TransactionAspectSupport
implements MethodInterceptor,
Serializable {
    public TransactionInterceptor() {
    }

    public TransactionInterceptor(TransactionManager ptm, TransactionAttributeSource tas) {
        this.setTransactionManager(ptm);
        this.setTransactionAttributeSource(tas);
    }

    @Deprecated
    public TransactionInterceptor(PlatformTransactionManager ptm, TransactionAttributeSource tas) {
        this.setTransactionManager(ptm);
        this.setTransactionAttributeSource(tas);
    }

    @Deprecated
    public TransactionInterceptor(PlatformTransactionManager ptm, Properties attributes) {
        this.setTransactionManager(ptm);
        this.setTransactionAttributes(attributes);
    }

    @Nullable
    public Object invoke(final MethodInvocation invocation) throws Throwable {
        Class targetClass = invocation.getThis() != null ? AopUtils.getTargetClass((Object)invocation.getThis()) : null;
        return this.invokeWithinTransaction(invocation.getMethod(), targetClass, new TransactionAspectSupport.CoroutinesInvocationCallback(){

            @Override
            @Nullable
            public Object proceedWithInvocation() throws Throwable {
                return invocation.proceed();
            }

            @Override
            public Object getTarget() {
                return invocation.getThis();
            }

            @Override
            public Object[] getArguments() {
                return invocation.getArguments();
            }
        });
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        oos.writeObject(this.getTransactionManagerBeanName());
        oos.writeObject(this.getTransactionManager());
        oos.writeObject(this.getTransactionAttributeSource());
        oos.writeObject(this.getBeanFactory());
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        this.setTransactionManagerBeanName((String)ois.readObject());
        this.setTransactionManager((PlatformTransactionManager)ois.readObject());
        this.setTransactionAttributeSource((TransactionAttributeSource)ois.readObject());
        this.setBeanFactory((BeanFactory)ois.readObject());
    }
}

