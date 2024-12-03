/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.auditing.config;

import org.springframework.data.auditing.IsNewAwareAuditingHandler;
import org.springframework.data.auditing.config.AuditingHandlerBeanDefinitionParser;
import org.w3c.dom.Element;

public class IsNewAwareAuditingHandlerBeanDefinitionParser
extends AuditingHandlerBeanDefinitionParser {
    public IsNewAwareAuditingHandlerBeanDefinitionParser(String mappingContextBeanName) {
        super(mappingContextBeanName);
    }

    @Override
    protected Class<?> getBeanClass(Element element) {
        return IsNewAwareAuditingHandler.class;
    }
}

