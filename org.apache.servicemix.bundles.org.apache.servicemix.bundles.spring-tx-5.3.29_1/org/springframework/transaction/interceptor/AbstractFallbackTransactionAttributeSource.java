/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.aop.support.AopUtils
 *  org.springframework.context.EmbeddedValueResolverAware
 *  org.springframework.core.MethodClassKey
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.StringValueResolver
 */
package org.springframework.transaction.interceptor;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.core.MethodClassKey;
import org.springframework.lang.Nullable;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttributeSource;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringValueResolver;

public abstract class AbstractFallbackTransactionAttributeSource
implements TransactionAttributeSource,
EmbeddedValueResolverAware {
    private static final TransactionAttribute NULL_TRANSACTION_ATTRIBUTE = new DefaultTransactionAttribute(){

        @Override
        public String toString() {
            return "null";
        }
    };
    protected final Log logger = LogFactory.getLog(this.getClass());
    @Nullable
    private transient StringValueResolver embeddedValueResolver;
    private final Map<Object, TransactionAttribute> attributeCache = new ConcurrentHashMap<Object, TransactionAttribute>(1024);

    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.embeddedValueResolver = resolver;
    }

    @Override
    @Nullable
    public TransactionAttribute getTransactionAttribute(Method method, @Nullable Class<?> targetClass) {
        if (method.getDeclaringClass() == Object.class) {
            return null;
        }
        Object cacheKey = this.getCacheKey(method, targetClass);
        TransactionAttribute cached = this.attributeCache.get(cacheKey);
        if (cached != null) {
            if (cached == NULL_TRANSACTION_ATTRIBUTE) {
                return null;
            }
            return cached;
        }
        TransactionAttribute txAttr = this.computeTransactionAttribute(method, targetClass);
        if (txAttr == null) {
            this.attributeCache.put(cacheKey, NULL_TRANSACTION_ATTRIBUTE);
        } else {
            String methodIdentification = ClassUtils.getQualifiedMethodName((Method)method, targetClass);
            if (txAttr instanceof DefaultTransactionAttribute) {
                DefaultTransactionAttribute dta = (DefaultTransactionAttribute)txAttr;
                dta.setDescriptor(methodIdentification);
                dta.resolveAttributeStrings(this.embeddedValueResolver);
            }
            if (this.logger.isTraceEnabled()) {
                this.logger.trace((Object)("Adding transactional method '" + methodIdentification + "' with attribute: " + txAttr));
            }
            this.attributeCache.put(cacheKey, txAttr);
        }
        return txAttr;
    }

    protected Object getCacheKey(Method method, @Nullable Class<?> targetClass) {
        return new MethodClassKey(method, targetClass);
    }

    @Nullable
    protected TransactionAttribute computeTransactionAttribute(Method method, @Nullable Class<?> targetClass) {
        if (this.allowPublicMethodsOnly() && !Modifier.isPublic(method.getModifiers())) {
            return null;
        }
        Method specificMethod = AopUtils.getMostSpecificMethod((Method)method, targetClass);
        TransactionAttribute txAttr = this.findTransactionAttribute(specificMethod);
        if (txAttr != null) {
            return txAttr;
        }
        txAttr = this.findTransactionAttribute(specificMethod.getDeclaringClass());
        if (txAttr != null && ClassUtils.isUserLevelMethod((Method)method)) {
            return txAttr;
        }
        if (specificMethod != method) {
            txAttr = this.findTransactionAttribute(method);
            if (txAttr != null) {
                return txAttr;
            }
            txAttr = this.findTransactionAttribute(method.getDeclaringClass());
            if (txAttr != null && ClassUtils.isUserLevelMethod((Method)method)) {
                return txAttr;
            }
        }
        return null;
    }

    @Nullable
    protected abstract TransactionAttribute findTransactionAttribute(Class<?> var1);

    @Nullable
    protected abstract TransactionAttribute findTransactionAttribute(Method var1);

    protected boolean allowPublicMethodsOnly() {
        return false;
    }
}

