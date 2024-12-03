/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ejb.config;

import org.springframework.ejb.config.AbstractJndiLocatingBeanDefinitionParser;
import org.w3c.dom.Element;

class RemoteStatelessSessionBeanDefinitionParser
extends AbstractJndiLocatingBeanDefinitionParser {
    RemoteStatelessSessionBeanDefinitionParser() {
    }

    protected String getBeanClassName(Element element) {
        return "org.springframework.ejb.access.SimpleRemoteStatelessSessionProxyFactoryBean";
    }
}

