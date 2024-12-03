/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.config.BeanDefinitionHolder
 *  org.springframework.beans.factory.support.AbstractBeanDefinition
 *  org.springframework.beans.factory.support.BeanDefinitionReaderUtils
 *  org.springframework.beans.factory.support.BeanDefinitionRegistry
 *  org.springframework.beans.factory.support.ManagedList
 *  org.springframework.beans.factory.support.RootBeanDefinition
 *  org.springframework.beans.factory.xml.BeanDefinitionDecorator
 *  org.springframework.beans.factory.xml.ParserContext
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.aop.config;

import java.util.List;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionDecorator;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.w3c.dom.Node;

public abstract class AbstractInterceptorDrivenBeanDefinitionDecorator
implements BeanDefinitionDecorator {
    public final BeanDefinitionHolder decorate(Node node, BeanDefinitionHolder definitionHolder, ParserContext parserContext) {
        BeanDefinitionRegistry registry = parserContext.getRegistry();
        String existingBeanName = definitionHolder.getBeanName();
        BeanDefinition targetDefinition = definitionHolder.getBeanDefinition();
        BeanDefinitionHolder targetHolder = new BeanDefinitionHolder(targetDefinition, existingBeanName + ".TARGET");
        BeanDefinition interceptorDefinition = this.createInterceptorDefinition(node);
        String interceptorName = existingBeanName + '.' + this.getInterceptorNameSuffix(interceptorDefinition);
        BeanDefinitionReaderUtils.registerBeanDefinition((BeanDefinitionHolder)new BeanDefinitionHolder(interceptorDefinition, interceptorName), (BeanDefinitionRegistry)registry);
        BeanDefinitionHolder result = definitionHolder;
        if (!this.isProxyFactoryBeanDefinition(targetDefinition)) {
            RootBeanDefinition proxyDefinition = new RootBeanDefinition();
            proxyDefinition.setBeanClass(ProxyFactoryBean.class);
            proxyDefinition.setScope(targetDefinition.getScope());
            proxyDefinition.setLazyInit(targetDefinition.isLazyInit());
            proxyDefinition.setDecoratedDefinition(targetHolder);
            proxyDefinition.getPropertyValues().add("target", (Object)targetHolder);
            proxyDefinition.getPropertyValues().add("interceptorNames", (Object)new ManagedList());
            proxyDefinition.setAutowireCandidate(targetDefinition.isAutowireCandidate());
            proxyDefinition.setPrimary(targetDefinition.isPrimary());
            if (targetDefinition instanceof AbstractBeanDefinition) {
                proxyDefinition.copyQualifiersFrom((AbstractBeanDefinition)targetDefinition);
            }
            result = new BeanDefinitionHolder((BeanDefinition)proxyDefinition, existingBeanName);
        }
        this.addInterceptorNameToList(interceptorName, result.getBeanDefinition());
        return result;
    }

    private void addInterceptorNameToList(String interceptorName, BeanDefinition beanDefinition) {
        List list = (List)beanDefinition.getPropertyValues().get("interceptorNames");
        Assert.state((list != null ? 1 : 0) != 0, (String)"Missing 'interceptorNames' property");
        list.add(interceptorName);
    }

    private boolean isProxyFactoryBeanDefinition(BeanDefinition existingDefinition) {
        return ProxyFactoryBean.class.getName().equals(existingDefinition.getBeanClassName());
    }

    protected String getInterceptorNameSuffix(BeanDefinition interceptorDefinition) {
        String beanClassName = interceptorDefinition.getBeanClassName();
        return StringUtils.hasLength((String)beanClassName) ? StringUtils.uncapitalize((String)ClassUtils.getShortName((String)beanClassName)) : "";
    }

    protected abstract BeanDefinition createInterceptorDefinition(Node var1);
}

