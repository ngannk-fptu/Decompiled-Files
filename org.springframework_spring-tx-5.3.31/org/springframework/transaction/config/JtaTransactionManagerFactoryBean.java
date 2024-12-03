/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.BeanUtils
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ClassUtils
 */
package org.springframework.transaction.config;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.util.ClassUtils;

public class JtaTransactionManagerFactoryBean
implements FactoryBean<JtaTransactionManager>,
InitializingBean {
    private static final String WEBLOGIC_JTA_TRANSACTION_MANAGER_CLASS_NAME = "org.springframework.transaction.jta.WebLogicJtaTransactionManager";
    private static final String WEBSPHERE_TRANSACTION_MANAGER_CLASS_NAME = "org.springframework.transaction.jta.WebSphereUowTransactionManager";
    private static final String JTA_TRANSACTION_MANAGER_CLASS_NAME = "org.springframework.transaction.jta.JtaTransactionManager";
    private static final boolean weblogicPresent;
    private static final boolean webspherePresent;
    private final JtaTransactionManager transactionManager;

    public JtaTransactionManagerFactoryBean() {
        String className = JtaTransactionManagerFactoryBean.resolveJtaTransactionManagerClassName();
        try {
            Class clazz = ClassUtils.forName((String)className, (ClassLoader)JtaTransactionManagerFactoryBean.class.getClassLoader());
            this.transactionManager = (JtaTransactionManager)BeanUtils.instantiateClass((Class)clazz);
        }
        catch (ClassNotFoundException ex) {
            throw new IllegalStateException("Failed to load JtaTransactionManager class: " + className, ex);
        }
    }

    public void afterPropertiesSet() throws TransactionSystemException {
        this.transactionManager.afterPropertiesSet();
    }

    @Nullable
    public JtaTransactionManager getObject() {
        return this.transactionManager;
    }

    public Class<?> getObjectType() {
        return this.transactionManager.getClass();
    }

    public boolean isSingleton() {
        return true;
    }

    static String resolveJtaTransactionManagerClassName() {
        if (weblogicPresent) {
            return WEBLOGIC_JTA_TRANSACTION_MANAGER_CLASS_NAME;
        }
        if (webspherePresent) {
            return WEBSPHERE_TRANSACTION_MANAGER_CLASS_NAME;
        }
        return JTA_TRANSACTION_MANAGER_CLASS_NAME;
    }

    static {
        ClassLoader classLoader = JtaTransactionManagerFactoryBean.class.getClassLoader();
        weblogicPresent = ClassUtils.isPresent((String)"weblogic.transaction.UserTransaction", (ClassLoader)classLoader);
        webspherePresent = ClassUtils.isPresent((String)"com.ibm.wsspi.uow.UOWManager", (ClassLoader)classLoader);
    }
}

