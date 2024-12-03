/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.aop.Advice
 *  org.springframework.aop.framework.ProxyFactory
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.ListableBeanFactory
 *  org.springframework.core.BridgeMethodResolver
 *  org.springframework.lang.Nullable
 *  org.springframework.transaction.annotation.AnnotationTransactionAttributeSource
 *  org.springframework.transaction.interceptor.TransactionAttribute
 *  org.springframework.transaction.interceptor.TransactionAttributeSource
 *  org.springframework.transaction.interceptor.TransactionInterceptor
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 */
package org.springframework.data.repository.core.support;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.aopalliance.aop.Advice;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.support.RepositoryProxyPostProcessor;
import org.springframework.data.util.ProxyUtils;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

class TransactionalRepositoryProxyPostProcessor
implements RepositoryProxyPostProcessor {
    private final BeanFactory beanFactory;
    private final String transactionManagerName;
    private final boolean enableDefaultTransactions;

    public TransactionalRepositoryProxyPostProcessor(ListableBeanFactory beanFactory, String transactionManagerName, boolean enableDefaultTransaction) {
        Assert.notNull((Object)beanFactory, (String)"BeanFactory must not be null!");
        Assert.notNull((Object)transactionManagerName, (String)"TransactionManagerName must not be null!");
        this.beanFactory = beanFactory;
        this.transactionManagerName = transactionManagerName;
        this.enableDefaultTransactions = enableDefaultTransaction;
    }

    @Override
    public void postProcess(ProxyFactory factory, RepositoryInformation repositoryInformation) {
        TransactionInterceptor transactionInterceptor = new TransactionInterceptor();
        transactionInterceptor.setTransactionAttributeSource((TransactionAttributeSource)new RepositoryAnnotationTransactionAttributeSource(repositoryInformation, this.enableDefaultTransactions));
        transactionInterceptor.setTransactionManagerBeanName(this.transactionManagerName);
        transactionInterceptor.setBeanFactory(this.beanFactory);
        transactionInterceptor.afterPropertiesSet();
        factory.addAdvice((Advice)transactionInterceptor);
    }

    static class RepositoryAnnotationTransactionAttributeSource
    extends AnnotationTransactionAttributeSource {
        private static final long serialVersionUID = 7229616838812819438L;
        private final RepositoryInformation repositoryInformation;
        private final boolean enableDefaultTransactions;

        public RepositoryAnnotationTransactionAttributeSource(RepositoryInformation repositoryInformation, boolean enableDefaultTransactions) {
            super(true);
            Assert.notNull((Object)repositoryInformation, (String)"RepositoryInformation must not be null!");
            this.enableDefaultTransactions = enableDefaultTransactions;
            this.repositoryInformation = repositoryInformation;
        }

        @Nullable
        protected TransactionAttribute computeTransactionAttribute(Method method, @Nullable Class<?> targetClass) {
            if (this.allowPublicMethodsOnly() && !Modifier.isPublic(method.getModifiers())) {
                return null;
            }
            Class<?> userClass = targetClass == null ? targetClass : ProxyUtils.getUserClass(targetClass);
            Method specificMethod = ClassUtils.getMostSpecificMethod((Method)method, userClass);
            specificMethod = BridgeMethodResolver.findBridgedMethod((Method)specificMethod);
            TransactionAttribute txAtt = null;
            if (specificMethod != method) {
                txAtt = this.findTransactionAttribute(method);
                if (txAtt != null) {
                    return txAtt;
                }
                txAtt = this.findTransactionAttribute(method.getDeclaringClass());
                if (txAtt != null || !this.enableDefaultTransactions) {
                    return txAtt;
                }
            }
            if ((txAtt = this.findTransactionAttribute(specificMethod)) != null) {
                return txAtt;
            }
            txAtt = this.findTransactionAttribute(specificMethod.getDeclaringClass());
            if (txAtt != null) {
                return txAtt;
            }
            if (!this.enableDefaultTransactions) {
                return null;
            }
            Method targetClassMethod = this.repositoryInformation.getTargetClassMethod(method);
            if (targetClassMethod.equals(method)) {
                return null;
            }
            txAtt = this.findTransactionAttribute(targetClassMethod);
            if (txAtt != null) {
                return txAtt;
            }
            txAtt = this.findTransactionAttribute(targetClassMethod.getDeclaringClass());
            if (txAtt != null) {
                return txAtt;
            }
            return null;
        }
    }
}

