/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.MutablePropertyValues
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.parsing.BeanComponentDefinition
 *  org.springframework.beans.factory.parsing.ComponentDefinition
 *  org.springframework.beans.factory.parsing.CompositeComponentDefinition
 *  org.springframework.beans.factory.support.ManagedList
 *  org.springframework.beans.factory.support.RootBeanDefinition
 *  org.springframework.beans.factory.xml.BeanDefinitionParser
 *  org.springframework.beans.factory.xml.ParserContext
 *  org.springframework.util.xml.DomUtils
 */
package org.springframework.web.servlet.config;

import java.util.List;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.parsing.ComponentDefinition;
import org.springframework.beans.factory.parsing.CompositeComponentDefinition;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.springframework.web.servlet.config.MvcNamespaceUtils;
import org.springframework.web.servlet.view.BeanNameViewResolver;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.ViewResolverComposite;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;
import org.springframework.web.servlet.view.groovy.GroovyMarkupViewResolver;
import org.springframework.web.servlet.view.script.ScriptTemplateViewResolver;
import org.springframework.web.servlet.view.tiles3.TilesViewResolver;
import org.w3c.dom.Element;

public class ViewResolversBeanDefinitionParser
implements BeanDefinitionParser {
    public static final String VIEW_RESOLVER_BEAN_NAME = "mvcViewResolver";

    public BeanDefinition parse(Element element, ParserContext context) {
        Object source = context.extractSource((Object)element);
        context.pushContainingComponent(new CompositeComponentDefinition(element.getTagName(), source));
        ManagedList resolvers = new ManagedList(4);
        resolvers.setSource(context.extractSource((Object)element));
        String[] names = new String[]{"jsp", "tiles", "bean-name", "freemarker", "groovy", "script-template", "bean", "ref"};
        for (Element resolverElement : DomUtils.getChildElementsByTagName((Element)element, (String[])names)) {
            RootBeanDefinition resolverBeanDef;
            String name = resolverElement.getLocalName();
            if ("bean".equals(name) || "ref".equals(name)) {
                resolvers.add(context.getDelegate().parsePropertySubElement(resolverElement, null));
                continue;
            }
            if ("jsp".equals(name)) {
                resolverBeanDef = new RootBeanDefinition(InternalResourceViewResolver.class);
                resolverBeanDef.getPropertyValues().add("prefix", (Object)"/WEB-INF/");
                resolverBeanDef.getPropertyValues().add("suffix", (Object)".jsp");
                this.addUrlBasedViewResolverProperties(resolverElement, resolverBeanDef);
            } else if ("tiles".equals(name)) {
                resolverBeanDef = new RootBeanDefinition(TilesViewResolver.class);
                this.addUrlBasedViewResolverProperties(resolverElement, resolverBeanDef);
            } else if ("freemarker".equals(name)) {
                resolverBeanDef = new RootBeanDefinition(FreeMarkerViewResolver.class);
                resolverBeanDef.getPropertyValues().add("suffix", (Object)".ftl");
                this.addUrlBasedViewResolverProperties(resolverElement, resolverBeanDef);
            } else if ("groovy".equals(name)) {
                resolverBeanDef = new RootBeanDefinition(GroovyMarkupViewResolver.class);
                resolverBeanDef.getPropertyValues().add("suffix", (Object)".tpl");
                this.addUrlBasedViewResolverProperties(resolverElement, resolverBeanDef);
            } else if ("script-template".equals(name)) {
                resolverBeanDef = new RootBeanDefinition(ScriptTemplateViewResolver.class);
                this.addUrlBasedViewResolverProperties(resolverElement, resolverBeanDef);
            } else if ("bean-name".equals(name)) {
                resolverBeanDef = new RootBeanDefinition(BeanNameViewResolver.class);
            } else {
                throw new IllegalStateException("Unexpected element name: " + name);
            }
            resolverBeanDef.setSource(source);
            resolverBeanDef.setRole(2);
            resolvers.add((Object)resolverBeanDef);
        }
        String beanName = VIEW_RESOLVER_BEAN_NAME;
        RootBeanDefinition compositeResolverBeanDef = new RootBeanDefinition(ViewResolverComposite.class);
        compositeResolverBeanDef.setSource(source);
        compositeResolverBeanDef.setRole(2);
        names = new String[]{"content-negotiation"};
        List contentNegotiationElements = DomUtils.getChildElementsByTagName((Element)element, (String[])names);
        if (contentNegotiationElements.isEmpty()) {
            compositeResolverBeanDef.getPropertyValues().add("viewResolvers", (Object)resolvers);
        } else if (contentNegotiationElements.size() == 1) {
            BeanDefinition beanDef = this.createContentNegotiatingViewResolver((Element)contentNegotiationElements.get(0), context);
            beanDef.getPropertyValues().add("viewResolvers", (Object)resolvers);
            ManagedList list = new ManagedList(1);
            list.add((Object)beanDef);
            compositeResolverBeanDef.getPropertyValues().add("order", (Object)Integer.MIN_VALUE);
            compositeResolverBeanDef.getPropertyValues().add("viewResolvers", (Object)list);
        } else {
            throw new IllegalArgumentException("Only one <content-negotiation> element is allowed.");
        }
        if (element.hasAttribute("order")) {
            compositeResolverBeanDef.getPropertyValues().add("order", (Object)element.getAttribute("order"));
        }
        context.getReaderContext().getRegistry().registerBeanDefinition(beanName, (BeanDefinition)compositeResolverBeanDef);
        context.registerComponent((ComponentDefinition)new BeanComponentDefinition((BeanDefinition)compositeResolverBeanDef, beanName));
        context.popAndRegisterContainingComponent();
        return null;
    }

    private void addUrlBasedViewResolverProperties(Element element, RootBeanDefinition beanDefinition) {
        if (element.hasAttribute("prefix")) {
            beanDefinition.getPropertyValues().add("prefix", (Object)element.getAttribute("prefix"));
        }
        if (element.hasAttribute("suffix")) {
            beanDefinition.getPropertyValues().add("suffix", (Object)element.getAttribute("suffix"));
        }
        if (element.hasAttribute("cache-views")) {
            beanDefinition.getPropertyValues().add("cache", (Object)element.getAttribute("cache-views"));
        }
        if (element.hasAttribute("view-class")) {
            beanDefinition.getPropertyValues().add("viewClass", (Object)element.getAttribute("view-class"));
        }
        if (element.hasAttribute("view-names")) {
            beanDefinition.getPropertyValues().add("viewNames", (Object)element.getAttribute("view-names"));
        }
    }

    private BeanDefinition createContentNegotiatingViewResolver(Element resolverElement, ParserContext context) {
        Object manager;
        RootBeanDefinition beanDef = new RootBeanDefinition(ContentNegotiatingViewResolver.class);
        beanDef.setSource(context.extractSource((Object)resolverElement));
        beanDef.setRole(2);
        MutablePropertyValues values = beanDef.getPropertyValues();
        List elements = DomUtils.getChildElementsByTagName((Element)resolverElement, (String)"default-views");
        if (!elements.isEmpty()) {
            ManagedList list = new ManagedList();
            for (Element element : DomUtils.getChildElementsByTagName((Element)((Element)elements.get(0)), (String[])new String[]{"bean", "ref"})) {
                list.add(context.getDelegate().parsePropertySubElement(element, null));
            }
            values.add("defaultViews", (Object)list);
        }
        if (resolverElement.hasAttribute("use-not-acceptable")) {
            values.add("useNotAcceptableStatusCode", (Object)resolverElement.getAttribute("use-not-acceptable"));
        }
        if ((manager = MvcNamespaceUtils.getContentNegotiationManager(context)) != null) {
            values.add("contentNegotiationManager", manager);
        }
        return beanDef;
    }
}

