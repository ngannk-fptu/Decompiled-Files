/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.config;

import org.springframework.aop.config.AopConfigUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.lang.Nullable;
import org.w3c.dom.Element;

public abstract class AopNamespaceUtils {
    public static final String PROXY_TARGET_CLASS_ATTRIBUTE = "proxy-target-class";
    private static final String EXPOSE_PROXY_ATTRIBUTE = "expose-proxy";

    public static void registerAutoProxyCreatorIfNecessary(ParserContext parserContext, Element sourceElement) {
        BeanDefinition beanDefinition = AopConfigUtils.registerAutoProxyCreatorIfNecessary(parserContext.getRegistry(), parserContext.extractSource(sourceElement));
        AopNamespaceUtils.useClassProxyingIfNecessary(parserContext.getRegistry(), sourceElement);
        AopNamespaceUtils.registerComponentIfNecessary(beanDefinition, parserContext);
    }

    public static void registerAspectJAutoProxyCreatorIfNecessary(ParserContext parserContext, Element sourceElement) {
        BeanDefinition beanDefinition = AopConfigUtils.registerAspectJAutoProxyCreatorIfNecessary(parserContext.getRegistry(), parserContext.extractSource(sourceElement));
        AopNamespaceUtils.useClassProxyingIfNecessary(parserContext.getRegistry(), sourceElement);
        AopNamespaceUtils.registerComponentIfNecessary(beanDefinition, parserContext);
    }

    public static void registerAspectJAnnotationAutoProxyCreatorIfNecessary(ParserContext parserContext, Element sourceElement) {
        BeanDefinition beanDefinition = AopConfigUtils.registerAspectJAnnotationAutoProxyCreatorIfNecessary(parserContext.getRegistry(), parserContext.extractSource(sourceElement));
        AopNamespaceUtils.useClassProxyingIfNecessary(parserContext.getRegistry(), sourceElement);
        AopNamespaceUtils.registerComponentIfNecessary(beanDefinition, parserContext);
    }

    private static void useClassProxyingIfNecessary(BeanDefinitionRegistry registry, @Nullable Element sourceElement) {
        if (sourceElement != null) {
            boolean exposeProxy;
            boolean proxyTargetClass = Boolean.parseBoolean(sourceElement.getAttribute(PROXY_TARGET_CLASS_ATTRIBUTE));
            if (proxyTargetClass) {
                AopConfigUtils.forceAutoProxyCreatorToUseClassProxying(registry);
            }
            if (exposeProxy = Boolean.parseBoolean(sourceElement.getAttribute(EXPOSE_PROXY_ATTRIBUTE))) {
                AopConfigUtils.forceAutoProxyCreatorToExposeProxy(registry);
            }
        }
    }

    private static void registerComponentIfNecessary(@Nullable BeanDefinition beanDefinition, ParserContext parserContext) {
        if (beanDefinition != null) {
            parserContext.registerComponent(new BeanComponentDefinition(beanDefinition, "org.springframework.aop.config.internalAutoProxyCreator"));
        }
    }
}

