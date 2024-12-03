/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.xml.BeanDefinitionParser
 *  org.springframework.beans.factory.xml.NamespaceHandlerSupport
 */
package org.springframework.transaction.config;

import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.transaction.config.AnnotationDrivenBeanDefinitionParser;
import org.springframework.transaction.config.JtaTransactionManagerBeanDefinitionParser;
import org.springframework.transaction.config.TxAdviceBeanDefinitionParser;
import org.w3c.dom.Element;

public class TxNamespaceHandler
extends NamespaceHandlerSupport {
    static final String TRANSACTION_MANAGER_ATTRIBUTE = "transaction-manager";
    static final String DEFAULT_TRANSACTION_MANAGER_BEAN_NAME = "transactionManager";

    static String getTransactionManagerName(Element element) {
        return element.hasAttribute(TRANSACTION_MANAGER_ATTRIBUTE) ? element.getAttribute(TRANSACTION_MANAGER_ATTRIBUTE) : DEFAULT_TRANSACTION_MANAGER_BEAN_NAME;
    }

    public void init() {
        this.registerBeanDefinitionParser("advice", (BeanDefinitionParser)new TxAdviceBeanDefinitionParser());
        this.registerBeanDefinitionParser("annotation-driven", new AnnotationDrivenBeanDefinitionParser());
        this.registerBeanDefinitionParser("jta-transaction-manager", (BeanDefinitionParser)new JtaTransactionManagerBeanDefinitionParser());
    }
}

