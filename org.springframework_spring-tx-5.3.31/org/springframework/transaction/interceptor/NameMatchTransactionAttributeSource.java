/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.context.EmbeddedValueResolverAware
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.PatternMatchUtils
 *  org.springframework.util.StringValueResolver
 */
package org.springframework.transaction.interceptor;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.lang.Nullable;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttributeEditor;
import org.springframework.transaction.interceptor.TransactionAttributeSource;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.PatternMatchUtils;
import org.springframework.util.StringValueResolver;

public class NameMatchTransactionAttributeSource
implements TransactionAttributeSource,
EmbeddedValueResolverAware,
InitializingBean,
Serializable {
    protected static final Log logger = LogFactory.getLog(NameMatchTransactionAttributeSource.class);
    private final Map<String, TransactionAttribute> nameMap = new HashMap<String, TransactionAttribute>();
    @Nullable
    private StringValueResolver embeddedValueResolver;

    public void setNameMap(Map<String, TransactionAttribute> nameMap) {
        nameMap.forEach(this::addTransactionalMethod);
    }

    public void setProperties(Properties transactionAttributes) {
        TransactionAttributeEditor tae = new TransactionAttributeEditor();
        Enumeration<?> propNames = transactionAttributes.propertyNames();
        while (propNames.hasMoreElements()) {
            String methodName = (String)propNames.nextElement();
            String value = transactionAttributes.getProperty(methodName);
            tae.setAsText(value);
            TransactionAttribute attr = (TransactionAttribute)tae.getValue();
            this.addTransactionalMethod(methodName, attr);
        }
    }

    public void addTransactionalMethod(String methodName, TransactionAttribute attr) {
        if (logger.isDebugEnabled()) {
            logger.debug((Object)("Adding transactional method [" + methodName + "] with attribute [" + attr + "]"));
        }
        if (this.embeddedValueResolver != null && attr instanceof DefaultTransactionAttribute) {
            ((DefaultTransactionAttribute)attr).resolveAttributeStrings(this.embeddedValueResolver);
        }
        this.nameMap.put(methodName, attr);
    }

    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.embeddedValueResolver = resolver;
    }

    public void afterPropertiesSet() {
        for (TransactionAttribute attr : this.nameMap.values()) {
            if (!(attr instanceof DefaultTransactionAttribute)) continue;
            ((DefaultTransactionAttribute)attr).resolveAttributeStrings(this.embeddedValueResolver);
        }
    }

    @Override
    @Nullable
    public TransactionAttribute getTransactionAttribute(Method method, @Nullable Class<?> targetClass) {
        if (!ClassUtils.isUserLevelMethod((Method)method)) {
            return null;
        }
        String methodName = method.getName();
        TransactionAttribute attr = this.nameMap.get(methodName);
        if (attr == null) {
            String bestNameMatch = null;
            for (String mappedName : this.nameMap.keySet()) {
                if (!this.isMatch(methodName, mappedName) || bestNameMatch != null && bestNameMatch.length() > mappedName.length()) continue;
                attr = this.nameMap.get(mappedName);
                bestNameMatch = mappedName;
            }
        }
        return attr;
    }

    protected boolean isMatch(String methodName, String mappedName) {
        return PatternMatchUtils.simpleMatch((String)mappedName, (String)methodName);
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof NameMatchTransactionAttributeSource)) {
            return false;
        }
        NameMatchTransactionAttributeSource otherTas = (NameMatchTransactionAttributeSource)other;
        return ObjectUtils.nullSafeEquals(this.nameMap, otherTas.nameMap);
    }

    public int hashCode() {
        return NameMatchTransactionAttributeSource.class.hashCode();
    }

    public String toString() {
        return this.getClass().getName() + ": " + this.nameMap;
    }
}

