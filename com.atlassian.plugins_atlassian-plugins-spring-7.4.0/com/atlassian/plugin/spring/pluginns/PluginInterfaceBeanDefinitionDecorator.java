/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.springframework.beans.factory.config.BeanDefinitionHolder
 *  org.springframework.beans.factory.xml.BeanDefinitionDecorator
 *  org.springframework.beans.factory.xml.ParserContext
 */
package com.atlassian.plugin.spring.pluginns;

import com.atlassian.plugin.spring.PluginBeanDefinitionRegistry;
import javax.annotation.Nonnull;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.xml.BeanDefinitionDecorator;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Node;

public class PluginInterfaceBeanDefinitionDecorator
implements BeanDefinitionDecorator {
    @Nonnull
    public BeanDefinitionHolder decorate(@Nonnull Node source, @Nonnull BeanDefinitionHolder holder, @Nonnull ParserContext ctx) {
        String inf = source.getTextContent();
        if (inf != null) {
            new PluginBeanDefinitionRegistry(ctx.getRegistry()).addBeanInterface(holder.getBeanName(), inf.trim());
        }
        return holder;
    }
}

