/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.parsing.BeanComponentDefinition
 *  org.springframework.beans.factory.parsing.ComponentDefinition
 *  org.springframework.beans.factory.support.ManagedMap
 *  org.springframework.beans.factory.support.RootBeanDefinition
 *  org.springframework.beans.factory.xml.BeanDefinitionParser
 *  org.springframework.beans.factory.xml.ParserContext
 *  org.springframework.lang.Nullable
 *  org.springframework.util.StringUtils
 */
package org.springframework.web.servlet.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.parsing.ComponentDefinition;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.MvcNamespaceUtils;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.resource.DefaultServletHttpRequestHandler;
import org.w3c.dom.Element;

class DefaultServletHandlerBeanDefinitionParser
implements BeanDefinitionParser {
    DefaultServletHandlerBeanDefinitionParser() {
    }

    @Nullable
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        Object source = parserContext.extractSource((Object)element);
        String defaultServletName = element.getAttribute("default-servlet-name");
        RootBeanDefinition defaultServletHandlerDef = new RootBeanDefinition(DefaultServletHttpRequestHandler.class);
        defaultServletHandlerDef.setSource(source);
        defaultServletHandlerDef.setRole(2);
        if (StringUtils.hasText((String)defaultServletName)) {
            defaultServletHandlerDef.getPropertyValues().add("defaultServletName", (Object)defaultServletName);
        }
        String defaultServletHandlerName = parserContext.getReaderContext().generateBeanName((BeanDefinition)defaultServletHandlerDef);
        parserContext.getRegistry().registerBeanDefinition(defaultServletHandlerName, (BeanDefinition)defaultServletHandlerDef);
        parserContext.registerComponent((ComponentDefinition)new BeanComponentDefinition((BeanDefinition)defaultServletHandlerDef, defaultServletHandlerName));
        ManagedMap urlMap = new ManagedMap();
        urlMap.put("/**", defaultServletHandlerName);
        RootBeanDefinition handlerMappingDef = new RootBeanDefinition(SimpleUrlHandlerMapping.class);
        handlerMappingDef.setSource(source);
        handlerMappingDef.setRole(2);
        handlerMappingDef.getPropertyValues().add("urlMap", (Object)urlMap);
        String handlerMappingBeanName = parserContext.getReaderContext().generateBeanName((BeanDefinition)handlerMappingDef);
        parserContext.getRegistry().registerBeanDefinition(handlerMappingBeanName, (BeanDefinition)handlerMappingDef);
        parserContext.registerComponent((ComponentDefinition)new BeanComponentDefinition((BeanDefinition)handlerMappingDef, handlerMappingBeanName));
        MvcNamespaceUtils.registerDefaultComponents(parserContext, source);
        return null;
    }
}

