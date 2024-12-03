/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.transaction.config;

public abstract class TransactionManagementConfigUtils {
    public static final String TRANSACTION_ADVISOR_BEAN_NAME = "org.springframework.transaction.config.internalTransactionAdvisor";
    public static final String TRANSACTION_ASPECT_BEAN_NAME = "org.springframework.transaction.config.internalTransactionAspect";
    public static final String TRANSACTION_ASPECT_CLASS_NAME = "org.springframework.transaction.aspectj.AnnotationTransactionAspect";
    public static final String TRANSACTION_ASPECT_CONFIGURATION_CLASS_NAME = "org.springframework.transaction.aspectj.AspectJTransactionManagementConfiguration";
    public static final String JTA_TRANSACTION_ASPECT_BEAN_NAME = "org.springframework.transaction.config.internalJtaTransactionAspect";
    public static final String JTA_TRANSACTION_ASPECT_CLASS_NAME = "org.springframework.transaction.aspectj.JtaAnnotationTransactionAspect";
    public static final String JTA_TRANSACTION_ASPECT_CONFIGURATION_CLASS_NAME = "org.springframework.transaction.aspectj.AspectJJtaTransactionManagementConfiguration";
    public static final String TRANSACTIONAL_EVENT_LISTENER_FACTORY_BEAN_NAME = "org.springframework.transaction.config.internalTransactionalEventListenerFactory";
}

