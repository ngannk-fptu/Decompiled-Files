/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.ObjectFactoryCreatingFactoryBean
 *  org.springframework.beans.factory.support.AbstractBeanDefinition
 *  org.springframework.beans.factory.support.BeanDefinitionBuilder
 *  org.springframework.beans.factory.xml.ParserContext
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package org.springframework.data.config;

import org.springframework.beans.factory.config.ObjectFactoryCreatingFactoryBean;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

public abstract class ParsingUtils {
    private ParsingUtils() {
    }

    public static void setPropertyValue(BeanDefinitionBuilder builder, Element element, String attrName, String propertyName) {
        Assert.notNull((Object)builder, (String)"BeanDefinitionBuilder must not be null!");
        Assert.notNull((Object)element, (String)"Element must not be null!");
        Assert.hasText((String)attrName, (String)"Attribute name must not be null!");
        Assert.hasText((String)propertyName, (String)"Property name must not be null!");
        String attr = element.getAttribute(attrName);
        if (StringUtils.hasText((String)attr)) {
            builder.addPropertyValue(propertyName, (Object)attr);
        }
    }

    public static void setPropertyValue(BeanDefinitionBuilder builder, Element element, String attribute) {
        ParsingUtils.setPropertyValue(builder, element, attribute, attribute);
    }

    public static void setPropertyReference(BeanDefinitionBuilder builder, Element element, String attribute, String property) {
        Assert.notNull((Object)builder, (String)"BeanDefinitionBuilder must not be null!");
        Assert.notNull((Object)element, (String)"Element must not be null!");
        Assert.hasText((String)attribute, (String)"Attribute name must not be null!");
        Assert.hasText((String)property, (String)"Property name must not be null!");
        String value = element.getAttribute(attribute);
        if (StringUtils.hasText((String)value)) {
            builder.addPropertyReference(property, value);
        }
    }

    public static AbstractBeanDefinition getSourceBeanDefinition(BeanDefinitionBuilder builder, ParserContext context, Element element) {
        Assert.notNull((Object)element, (String)"Element must not be null!");
        Assert.notNull((Object)context, (String)"ParserContext must not be null!");
        return ParsingUtils.getSourceBeanDefinition(builder, context.extractSource((Object)element));
    }

    public static AbstractBeanDefinition getSourceBeanDefinition(BeanDefinitionBuilder builder, @Nullable Object source) {
        Assert.notNull((Object)builder, (String)"Builder must not be null!");
        AbstractBeanDefinition definition = builder.getRawBeanDefinition();
        definition.setSource(source);
        return definition;
    }

    public static AbstractBeanDefinition getObjectFactoryBeanDefinition(String targetBeanName, @Nullable Object source) {
        Assert.hasText((String)targetBeanName, (String)"Target bean name must not be null or empty!");
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(ObjectFactoryCreatingFactoryBean.class);
        builder.addPropertyValue("targetBeanName", (Object)targetBeanName);
        builder.setRole(2);
        return ParsingUtils.getSourceBeanDefinition(builder, source);
    }
}

