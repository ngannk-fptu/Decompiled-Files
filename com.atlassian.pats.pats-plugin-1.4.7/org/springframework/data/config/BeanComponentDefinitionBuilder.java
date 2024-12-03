/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.parsing.BeanComponentDefinition
 *  org.springframework.beans.factory.support.AbstractBeanDefinition
 *  org.springframework.beans.factory.support.BeanDefinitionBuilder
 *  org.springframework.beans.factory.support.BeanDefinitionReaderUtils
 *  org.springframework.beans.factory.support.BeanDefinitionRegistry
 *  org.springframework.beans.factory.xml.ParserContext
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package org.springframework.data.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

public class BeanComponentDefinitionBuilder {
    private final Element defaultSource;
    private final ParserContext context;

    public BeanComponentDefinitionBuilder(Element defaultSource, ParserContext context) {
        Assert.notNull((Object)defaultSource, (String)"DefaultSource must not be null!");
        Assert.notNull((Object)context, (String)"Context must not be null!");
        this.defaultSource = defaultSource;
        this.context = context;
    }

    public BeanComponentDefinition getComponent(BeanDefinitionBuilder builder) {
        Assert.notNull((Object)builder, (String)"Builder must not be null!");
        AbstractBeanDefinition definition = builder.getRawBeanDefinition();
        String name = BeanDefinitionReaderUtils.generateBeanName((BeanDefinition)definition, (BeanDefinitionRegistry)this.context.getRegistry(), (boolean)this.context.isNested());
        return this.getComponent(builder, name);
    }

    public BeanComponentDefinition getComponentIdButFallback(BeanDefinitionBuilder builder, String fallback) {
        Assert.hasText((String)fallback, (String)"Fallback component id must not be null or empty!");
        String id = this.defaultSource.getAttribute("id");
        return this.getComponent(builder, StringUtils.hasText((String)id) ? id : fallback);
    }

    public BeanComponentDefinition getComponent(BeanDefinitionBuilder builder, String name) {
        return this.getComponent(builder, name, this.defaultSource);
    }

    public BeanComponentDefinition getComponent(BeanDefinitionBuilder builder, String name, Object rawSource) {
        Assert.notNull((Object)builder, (String)"Builder must not be null!");
        Assert.hasText((String)name, (String)"Name of bean must not be null or empty!");
        AbstractBeanDefinition definition = builder.getRawBeanDefinition();
        definition.setSource(this.context.extractSource(rawSource));
        return new BeanComponentDefinition((BeanDefinition)definition, name);
    }
}

