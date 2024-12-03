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
import org.w3c.dom.Attr;
import org.w3c.dom.Node;

public class PluginTrackBundleBeanDefinitionDecorator
implements BeanDefinitionDecorator {
    @Nonnull
    public BeanDefinitionHolder decorate(@Nonnull Node source, @Nonnull BeanDefinitionHolder holder, @Nonnull ParserContext ctx) {
        String trackBundleAsString = ((Attr)source).getValue();
        if (this.isTrackBundleEnabled(trackBundleAsString)) {
            new PluginBeanDefinitionRegistry(ctx.getRegistry()).addBundleTrackingBean(holder.getBeanName());
        }
        return holder;
    }

    private boolean isTrackBundleEnabled(String trackBundle) {
        return Boolean.parseBoolean(trackBundle);
    }
}

