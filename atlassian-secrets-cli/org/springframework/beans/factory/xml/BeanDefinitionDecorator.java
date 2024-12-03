/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.xml;

import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Node;

public interface BeanDefinitionDecorator {
    public BeanDefinitionHolder decorate(Node var1, BeanDefinitionHolder var2, ParserContext var3);
}

