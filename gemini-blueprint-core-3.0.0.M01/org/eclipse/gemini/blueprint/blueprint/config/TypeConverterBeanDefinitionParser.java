/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.BeanDefinitionStoreException
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.support.AbstractBeanDefinition
 *  org.springframework.beans.factory.support.BeanDefinitionBuilder
 *  org.springframework.beans.factory.support.ManagedList
 *  org.springframework.beans.factory.xml.AbstractBeanDefinitionParser
 *  org.springframework.beans.factory.xml.ParserContext
 *  org.springframework.util.xml.DomUtils
 */
package org.eclipse.gemini.blueprint.blueprint.config;

import java.util.List;
import org.eclipse.gemini.blueprint.blueprint.config.internal.BlueprintParser;
import org.eclipse.gemini.blueprint.blueprint.config.internal.ParsingUtils;
import org.eclipse.gemini.blueprint.blueprint.container.BlueprintConverterConfigurer;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

class TypeConverterBeanDefinitionParser
extends AbstractBeanDefinitionParser {
    private static final String EDITOR_CONFIGURER_PROPERTY = "propertyEditorRegistrars";
    public static final String TYPE_CONVERTERS = "type-converters";

    TypeConverterBeanDefinitionParser() {
    }

    protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
        BeanDefinitionBuilder registrarDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(BlueprintConverterConfigurer.class);
        List components = DomUtils.getChildElementsByTagName((Element)element, (String)"bean");
        List componentRefs = DomUtils.getChildElementsByTagName((Element)element, (String)"ref");
        ManagedList converterList = new ManagedList(componentRefs.size() + components.size());
        for (Element component : components) {
            converterList.add(BlueprintParser.parsePropertySubElement(parserContext, component, (BeanDefinition)registrarDefinitionBuilder.getBeanDefinition()));
        }
        for (Element componentRef : componentRefs) {
            converterList.add(BlueprintParser.parsePropertySubElement(parserContext, componentRef, (BeanDefinition)registrarDefinitionBuilder.getBeanDefinition()));
        }
        registrarDefinitionBuilder.addConstructorArgValue((Object)converterList);
        registrarDefinitionBuilder.setRole(1);
        registrarDefinitionBuilder.getRawBeanDefinition().setSynthetic(true);
        return registrarDefinitionBuilder.getBeanDefinition();
    }

    protected boolean shouldGenerateId() {
        return true;
    }

    protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext) throws BeanDefinitionStoreException {
        return ParsingUtils.resolveId(element, definition, parserContext, this.shouldGenerateId(), this.shouldGenerateIdAsFallback());
    }
}

