/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.xml.NamespaceHandlerSupport
 */
package org.springframework.ldap.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.ldap.config.ContextSourceParser;
import org.springframework.ldap.config.LdapTemplateParser;
import org.springframework.ldap.config.TransactionManagerParser;

public class LdapNamespaceHandler
extends NamespaceHandlerSupport {
    public void init() {
        this.registerBeanDefinitionParser("context-source", new ContextSourceParser());
        this.registerBeanDefinitionParser("ldap-template", new LdapTemplateParser());
        this.registerBeanDefinitionParser("transaction-manager", new TransactionManagerParser());
    }
}

