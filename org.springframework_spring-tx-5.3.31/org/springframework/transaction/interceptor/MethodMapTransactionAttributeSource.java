/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.beans.factory.BeanClassLoaderAware
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.context.EmbeddedValueResolverAware
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.PatternMatchUtils
 *  org.springframework.util.StringValueResolver
 */
package org.springframework.transaction.interceptor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.lang.Nullable;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttributeSource;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.PatternMatchUtils;
import org.springframework.util.StringValueResolver;

public class MethodMapTransactionAttributeSource
implements TransactionAttributeSource,
EmbeddedValueResolverAware,
BeanClassLoaderAware,
InitializingBean {
    protected final Log logger = LogFactory.getLog(this.getClass());
    @Nullable
    private Map<String, TransactionAttribute> methodMap;
    @Nullable
    private StringValueResolver embeddedValueResolver;
    @Nullable
    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();
    private boolean eagerlyInitialized = false;
    private boolean initialized = false;
    private final Map<Method, TransactionAttribute> transactionAttributeMap = new HashMap<Method, TransactionAttribute>();
    private final Map<Method, String> methodNameMap = new HashMap<Method, String>();

    public void setMethodMap(Map<String, TransactionAttribute> methodMap) {
        this.methodMap = methodMap;
    }

    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.embeddedValueResolver = resolver;
    }

    public void setBeanClassLoader(ClassLoader beanClassLoader) {
        this.beanClassLoader = beanClassLoader;
    }

    public void afterPropertiesSet() {
        this.initMethodMap(this.methodMap);
        this.eagerlyInitialized = true;
        this.initialized = true;
    }

    protected void initMethodMap(@Nullable Map<String, TransactionAttribute> methodMap) {
        if (methodMap != null) {
            methodMap.forEach(this::addTransactionalMethod);
        }
    }

    public void addTransactionalMethod(String name, TransactionAttribute attr) {
        Assert.notNull((Object)name, (String)"Name must not be null");
        int lastDotIndex = name.lastIndexOf(46);
        if (lastDotIndex == -1) {
            throw new IllegalArgumentException("'" + name + "' is not a valid method name: format is FQN.methodName");
        }
        String className = name.substring(0, lastDotIndex);
        String methodName = name.substring(lastDotIndex + 1);
        Class clazz = ClassUtils.resolveClassName((String)className, (ClassLoader)this.beanClassLoader);
        this.addTransactionalMethod(clazz, methodName, attr);
    }

    public void addTransactionalMethod(Class<?> clazz, String mappedName, TransactionAttribute attr) {
        Assert.notNull(clazz, (String)"Class must not be null");
        Assert.notNull((Object)mappedName, (String)"Mapped name must not be null");
        String name = clazz.getName() + '.' + mappedName;
        Method[] methods = clazz.getDeclaredMethods();
        ArrayList<Method> matchingMethods = new ArrayList<Method>();
        for (Method method : methods) {
            if (!this.isMatch(method.getName(), mappedName)) continue;
            matchingMethods.add(method);
        }
        if (matchingMethods.isEmpty()) {
            throw new IllegalArgumentException("Could not find method '" + mappedName + "' on class [" + clazz.getName() + "]");
        }
        for (Method method : matchingMethods) {
            String regMethodName = this.methodNameMap.get(method);
            if (regMethodName == null || !regMethodName.equals(name) && regMethodName.length() <= name.length()) {
                if (this.logger.isDebugEnabled() && regMethodName != null) {
                    this.logger.debug((Object)("Replacing attribute for transactional method [" + method + "]: current name '" + name + "' is more specific than '" + regMethodName + "'"));
                }
                this.methodNameMap.put(method, name);
                this.addTransactionalMethod(method, attr);
                continue;
            }
            if (!this.logger.isDebugEnabled()) continue;
            this.logger.debug((Object)("Keeping attribute for transactional method [" + method + "]: current name '" + name + "' is not more specific than '" + regMethodName + "'"));
        }
    }

    public void addTransactionalMethod(Method method, TransactionAttribute attr) {
        Assert.notNull((Object)method, (String)"Method must not be null");
        Assert.notNull((Object)attr, (String)"TransactionAttribute must not be null");
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("Adding transactional method [" + method + "] with attribute [" + attr + "]"));
        }
        if (this.embeddedValueResolver != null && attr instanceof DefaultTransactionAttribute) {
            ((DefaultTransactionAttribute)attr).resolveAttributeStrings(this.embeddedValueResolver);
        }
        this.transactionAttributeMap.put(method, attr);
    }

    protected boolean isMatch(String methodName, String mappedName) {
        return PatternMatchUtils.simpleMatch((String)mappedName, (String)methodName);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    @Nullable
    public TransactionAttribute getTransactionAttribute(Method method, @Nullable Class<?> targetClass) {
        if (this.eagerlyInitialized) {
            return this.transactionAttributeMap.get(method);
        }
        Map<Method, TransactionAttribute> map = this.transactionAttributeMap;
        synchronized (map) {
            if (!this.initialized) {
                this.initMethodMap(this.methodMap);
                this.initialized = true;
            }
            return this.transactionAttributeMap.get(method);
        }
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MethodMapTransactionAttributeSource)) {
            return false;
        }
        MethodMapTransactionAttributeSource otherTas = (MethodMapTransactionAttributeSource)other;
        return ObjectUtils.nullSafeEquals(this.methodMap, otherTas.methodMap);
    }

    public int hashCode() {
        return MethodMapTransactionAttributeSource.class.hashCode();
    }

    public String toString() {
        return this.getClass().getName() + ": " + this.methodMap;
    }
}

