/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.config.RuntimeBeanReference
 *  org.springframework.beans.factory.parsing.BeanComponentDefinition
 *  org.springframework.beans.factory.parsing.ComponentDefinition
 *  org.springframework.beans.factory.parsing.CompositeComponentDefinition
 *  org.springframework.beans.factory.support.ManagedList
 *  org.springframework.beans.factory.support.RootBeanDefinition
 *  org.springframework.beans.factory.xml.BeanDefinitionParser
 *  org.springframework.beans.factory.xml.ParserContext
 *  org.springframework.lang.Nullable
 *  org.springframework.util.xml.DomUtils
 */
package org.springframework.web.servlet.config;

import java.util.List;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.parsing.ComponentDefinition;
import org.springframework.beans.factory.parsing.CompositeComponentDefinition;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.lang.Nullable;
import org.springframework.util.xml.DomUtils;
import org.springframework.web.servlet.handler.MappedInterceptor;
import org.w3c.dom.Element;

class InterceptorsBeanDefinitionParser
implements BeanDefinitionParser {
    InterceptorsBeanDefinitionParser() {
    }

    @Nullable
    public BeanDefinition parse(Element element, ParserContext context) {
        context.pushContainingComponent(new CompositeComponentDefinition(element.getTagName(), context.extractSource((Object)element)));
        RuntimeBeanReference pathMatcherRef = null;
        if (element.hasAttribute("path-matcher")) {
            pathMatcherRef = new RuntimeBeanReference(element.getAttribute("path-matcher"));
        }
        List interceptors = DomUtils.getChildElementsByTagName((Element)element, (String[])new String[]{"bean", "ref", "interceptor"});
        for (Element interceptor : interceptors) {
            Object interceptorBean;
            RootBeanDefinition mappedInterceptorDef = new RootBeanDefinition(MappedInterceptor.class);
            mappedInterceptorDef.setSource(context.extractSource((Object)interceptor));
            mappedInterceptorDef.setRole(2);
            ManagedList<String> includePatterns = null;
            ManagedList<String> excludePatterns = null;
            if ("interceptor".equals(interceptor.getLocalName())) {
                includePatterns = this.getIncludePatterns(interceptor, "mapping");
                excludePatterns = this.getIncludePatterns(interceptor, "exclude-mapping");
                Element beanElem = (Element)DomUtils.getChildElementsByTagName((Element)interceptor, (String[])new String[]{"bean", "ref"}).get(0);
                interceptorBean = context.getDelegate().parsePropertySubElement(beanElem, null);
            } else {
                interceptorBean = context.getDelegate().parsePropertySubElement(interceptor, null);
            }
            mappedInterceptorDef.getConstructorArgumentValues().addIndexedArgumentValue(0, includePatterns);
            mappedInterceptorDef.getConstructorArgumentValues().addIndexedArgumentValue(1, excludePatterns);
            mappedInterceptorDef.getConstructorArgumentValues().addIndexedArgumentValue(2, interceptorBean);
            if (pathMatcherRef != null) {
                mappedInterceptorDef.getPropertyValues().add("pathMatcher", (Object)pathMatcherRef);
            }
            String beanName = context.getReaderContext().registerWithGeneratedName((BeanDefinition)mappedInterceptorDef);
            context.registerComponent((ComponentDefinition)new BeanComponentDefinition((BeanDefinition)mappedInterceptorDef, beanName));
        }
        context.popAndRegisterContainingComponent();
        return null;
    }

    private ManagedList<String> getIncludePatterns(Element interceptor, String elementName) {
        List paths = DomUtils.getChildElementsByTagName((Element)interceptor, (String)elementName);
        ManagedList patterns = new ManagedList(paths.size());
        for (Element path : paths) {
            patterns.add((Object)path.getAttribute("path"));
        }
        return patterns;
    }
}

