/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.xml.NamespaceHandlerSupport
 *  org.springframework.util.ClassUtils
 */
package org.springframework.web.socket.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.util.ClassUtils;
import org.springframework.web.socket.config.HandlersBeanDefinitionParser;
import org.springframework.web.socket.config.MessageBrokerBeanDefinitionParser;

public class WebSocketNamespaceHandler
extends NamespaceHandlerSupport {
    private static boolean isSpringMessagingPresent = ClassUtils.isPresent((String)"org.springframework.messaging.Message", (ClassLoader)WebSocketNamespaceHandler.class.getClassLoader());

    public void init() {
        this.registerBeanDefinitionParser("handlers", new HandlersBeanDefinitionParser());
        if (isSpringMessagingPresent) {
            this.registerBeanDefinitionParser("message-broker", new MessageBrokerBeanDefinitionParser());
        }
    }
}

