/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.servlet.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.config.MvcNamespaceUtils;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.ParameterizableViewController;
import org.springframework.web.servlet.view.RedirectView;
import org.w3c.dom.Element;

class ViewControllerBeanDefinitionParser
implements BeanDefinitionParser {
    private static final String HANDLER_MAPPING_BEAN_NAME = "org.springframework.web.servlet.config.viewControllerHandlerMapping";

    ViewControllerBeanDefinitionParser() {
    }

    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        ManagedMap<String, RootBeanDefinition> urlMap;
        String name;
        Object source = parserContext.extractSource(element);
        BeanDefinition hm = this.registerHandlerMapping(parserContext, source);
        MvcNamespaceUtils.registerDefaultComponents(parserContext, source);
        RootBeanDefinition controller = new RootBeanDefinition(ParameterizableViewController.class);
        controller.setSource(source);
        HttpStatus statusCode = null;
        if (element.hasAttribute("status-code")) {
            int statusValue = Integer.parseInt(element.getAttribute("status-code"));
            statusCode = HttpStatus.valueOf(statusValue);
        }
        switch (name = element.getLocalName()) {
            case "view-controller": {
                if (element.hasAttribute("view-name")) {
                    controller.getPropertyValues().add("viewName", element.getAttribute("view-name"));
                }
                if (statusCode == null) break;
                controller.getPropertyValues().add("statusCode", (Object)statusCode);
                break;
            }
            case "redirect-view-controller": {
                controller.getPropertyValues().add("view", this.getRedirectView(element, statusCode, source));
                break;
            }
            case "status-controller": {
                controller.getPropertyValues().add("statusCode", (Object)statusCode);
                controller.getPropertyValues().add("statusOnly", true);
                break;
            }
            default: {
                throw new IllegalStateException("Unexpected tag name: " + name);
            }
        }
        if ((urlMap = (ManagedMap<String, RootBeanDefinition>)hm.getPropertyValues().get("urlMap")) == null) {
            urlMap = new ManagedMap<String, RootBeanDefinition>();
            hm.getPropertyValues().add("urlMap", urlMap);
        }
        urlMap.put(element.getAttribute("path"), controller);
        return null;
    }

    private BeanDefinition registerHandlerMapping(ParserContext context, @Nullable Object source) {
        if (context.getRegistry().containsBeanDefinition(HANDLER_MAPPING_BEAN_NAME)) {
            return context.getRegistry().getBeanDefinition(HANDLER_MAPPING_BEAN_NAME);
        }
        RootBeanDefinition beanDef = new RootBeanDefinition(SimpleUrlHandlerMapping.class);
        beanDef.setRole(2);
        context.getRegistry().registerBeanDefinition(HANDLER_MAPPING_BEAN_NAME, beanDef);
        context.registerComponent(new BeanComponentDefinition(beanDef, HANDLER_MAPPING_BEAN_NAME));
        beanDef.setSource(source);
        beanDef.getPropertyValues().add("order", "1");
        beanDef.getPropertyValues().add("pathMatcher", MvcNamespaceUtils.registerPathMatcher(null, context, source));
        beanDef.getPropertyValues().add("urlPathHelper", MvcNamespaceUtils.registerUrlPathHelper(null, context, source));
        RuntimeBeanReference corsConfigurationsRef = MvcNamespaceUtils.registerCorsConfigurations(null, context, source);
        beanDef.getPropertyValues().add("corsConfigurations", corsConfigurationsRef);
        return beanDef;
    }

    private RootBeanDefinition getRedirectView(Element element, @Nullable HttpStatus status, @Nullable Object source) {
        RootBeanDefinition redirectView = new RootBeanDefinition(RedirectView.class);
        redirectView.setSource(source);
        redirectView.getConstructorArgumentValues().addIndexedArgumentValue(0, element.getAttribute("redirect-url"));
        if (status != null) {
            redirectView.getPropertyValues().add("statusCode", (Object)status);
        }
        if (element.hasAttribute("context-relative")) {
            redirectView.getPropertyValues().add("contextRelative", element.getAttribute("context-relative"));
        } else {
            redirectView.getPropertyValues().add("contextRelative", true);
        }
        if (element.hasAttribute("keep-query-params")) {
            redirectView.getPropertyValues().add("propagateQueryParams", element.getAttribute("keep-query-params"));
        }
        return redirectView;
    }
}

