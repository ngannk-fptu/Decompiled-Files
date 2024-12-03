/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.xml.BeanDefinitionParser
 *  org.springframework.beans.factory.xml.NamespaceHandlerSupport
 */
package org.springframework.jdbc.config;

import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.jdbc.config.EmbeddedDatabaseBeanDefinitionParser;
import org.springframework.jdbc.config.InitializeDatabaseBeanDefinitionParser;

public class JdbcNamespaceHandler
extends NamespaceHandlerSupport {
    public void init() {
        this.registerBeanDefinitionParser("embedded-database", (BeanDefinitionParser)new EmbeddedDatabaseBeanDefinitionParser());
        this.registerBeanDefinitionParser("initialize-database", (BeanDefinitionParser)new InitializeDatabaseBeanDefinitionParser());
    }
}

