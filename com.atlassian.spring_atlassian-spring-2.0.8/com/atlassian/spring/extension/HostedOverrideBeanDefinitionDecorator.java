/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.BeanDefinitionHolder
 *  org.springframework.beans.factory.xml.BeanDefinitionDecorator
 *  org.springframework.beans.factory.xml.ParserContext
 */
package com.atlassian.spring.extension;

import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.xml.BeanDefinitionDecorator;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;

public class HostedOverrideBeanDefinitionDecorator
implements BeanDefinitionDecorator {
    public static final String OVERRIDE = "override";

    public BeanDefinitionHolder decorate(Node source, BeanDefinitionHolder holder, ParserContext context) {
        String isAvailable = ((Attr)source).getValue();
        holder.getBeanDefinition().setAttribute(OVERRIDE, (Object)Boolean.parseBoolean(isAvailable));
        return holder;
    }
}

