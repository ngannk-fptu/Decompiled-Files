/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.config.BeanDefinitionHolder
 *  org.springframework.beans.factory.support.BeanDefinitionBuilder
 *  org.springframework.beans.factory.xml.BeanDefinitionDecorator
 *  org.springframework.beans.factory.xml.ParserContext
 */
package org.eclipse.gemini.blueprint.compendium.config.internal;

import org.eclipse.gemini.blueprint.compendium.internal.cm.ManagedServiceInstanceTrackerPostProcessor;
import org.eclipse.gemini.blueprint.config.internal.util.AttributeCallback;
import org.eclipse.gemini.blueprint.config.internal.util.ParserUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionDecorator;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ManagedPropertiesDefinitionParser
implements BeanDefinitionDecorator {
    public BeanDefinitionHolder decorate(Node node, BeanDefinitionHolder definition, ParserContext parserContext) {
        BeanDefinition trackingBppDef = this.createTrackerBpp((Element)node, definition);
        String generatedName = parserContext.getReaderContext().generateBeanName(trackingBppDef) + "#" + definition.getBeanName();
        parserContext.getRegistry().registerBeanDefinition(generatedName, trackingBppDef);
        return definition;
    }

    private BeanDefinition createTrackerBpp(Element elem, BeanDefinitionHolder definition) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(ManagedServiceInstanceTrackerPostProcessor.class).setRole(2);
        builder.addConstructorArgValue((Object)definition.getBeanName());
        ParserUtils.parseCustomAttributes(elem, builder, (AttributeCallback[])null);
        return builder.getBeanDefinition();
    }
}

