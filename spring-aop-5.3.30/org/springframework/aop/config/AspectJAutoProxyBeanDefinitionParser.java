/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.config.TypedStringValue
 *  org.springframework.beans.factory.support.ManagedList
 *  org.springframework.beans.factory.xml.BeanDefinitionParser
 *  org.springframework.beans.factory.xml.ParserContext
 *  org.springframework.lang.Nullable
 */
package org.springframework.aop.config;

import org.springframework.aop.config.AopNamespaceUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.lang.Nullable;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class AspectJAutoProxyBeanDefinitionParser
implements BeanDefinitionParser {
    AspectJAutoProxyBeanDefinitionParser() {
    }

    @Nullable
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        AopNamespaceUtils.registerAspectJAnnotationAutoProxyCreatorIfNecessary(parserContext, element);
        this.extendBeanDefinition(element, parserContext);
        return null;
    }

    private void extendBeanDefinition(Element element, ParserContext parserContext) {
        BeanDefinition beanDef = parserContext.getRegistry().getBeanDefinition("org.springframework.aop.config.internalAutoProxyCreator");
        if (element.hasChildNodes()) {
            this.addIncludePatterns(element, parserContext, beanDef);
        }
    }

    private void addIncludePatterns(Element element, ParserContext parserContext, BeanDefinition beanDef) {
        ManagedList includePatterns = new ManagedList();
        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); ++i) {
            Node node = childNodes.item(i);
            if (!(node instanceof Element)) continue;
            Element includeElement = (Element)node;
            TypedStringValue valueHolder = new TypedStringValue(includeElement.getAttribute("name"));
            valueHolder.setSource(parserContext.extractSource((Object)includeElement));
            includePatterns.add((Object)valueHolder);
        }
        if (!includePatterns.isEmpty()) {
            includePatterns.setSource(parserContext.extractSource((Object)element));
            beanDef.getPropertyValues().add("includePatterns", (Object)includePatterns);
        }
    }
}

