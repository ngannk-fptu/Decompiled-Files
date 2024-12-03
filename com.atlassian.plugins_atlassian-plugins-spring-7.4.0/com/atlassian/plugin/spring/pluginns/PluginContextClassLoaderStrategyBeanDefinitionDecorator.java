/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.osgi.hostcomponents.ContextClassLoaderStrategy
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.config.BeanDefinitionHolder
 *  org.springframework.beans.factory.xml.BeanDefinitionDecorator
 *  org.springframework.beans.factory.xml.ParserContext
 */
package com.atlassian.plugin.spring.pluginns;

import com.atlassian.plugin.osgi.hostcomponents.ContextClassLoaderStrategy;
import com.atlassian.plugin.spring.PluginBeanDefinitionRegistry;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.xml.BeanDefinitionDecorator;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;

public class PluginContextClassLoaderStrategyBeanDefinitionDecorator
implements BeanDefinitionDecorator {
    private static final Logger log = LoggerFactory.getLogger(PluginContextClassLoaderStrategyBeanDefinitionDecorator.class);

    @Nonnull
    public BeanDefinitionHolder decorate(@Nonnull Node source, @Nonnull BeanDefinitionHolder holder, @Nonnull ParserContext ctx) {
        String contextClassLoaderStrategy = ((Attr)source).getValue();
        if (contextClassLoaderStrategy != null) {
            new PluginBeanDefinitionRegistry(ctx.getRegistry()).addContextClassLoaderStrategy(holder.getBeanName(), this.getContextClassLoaderStrategy(contextClassLoaderStrategy));
        }
        return holder;
    }

    private ContextClassLoaderStrategy getContextClassLoaderStrategy(String contextClassLoaderStrategy) {
        try {
            return ContextClassLoaderStrategy.valueOf((String)contextClassLoaderStrategy);
        }
        catch (IllegalArgumentException e) {
            log.warn("Cannot parse '{}' to a valid context class loader strategy, will use default '{}'", (Object)contextClassLoaderStrategy, (Object)ContextClassLoaderStrategy.USE_HOST);
            return ContextClassLoaderStrategy.USE_HOST;
        }
    }
}

