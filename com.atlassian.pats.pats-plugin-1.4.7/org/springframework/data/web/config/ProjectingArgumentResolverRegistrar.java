/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.BeanClassLoaderAware
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 *  org.springframework.beans.factory.ObjectFactory
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.beans.factory.config.BeanPostProcessor
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.core.convert.ConversionService
 *  org.springframework.lang.Nullable
 *  org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter
 */
package org.springframework.data.web.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.web.ProxyingHandlerMethodArgumentResolver;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

@Configuration(proxyBeanMethods=false)
public class ProjectingArgumentResolverRegistrar {
    @Bean
    static ProjectingArgumentResolverBeanPostProcessor projectingArgumentResolverBeanPostProcessor(@Qualifier(value="mvcConversionService") ObjectFactory<ConversionService> conversionService) {
        return new ProjectingArgumentResolverBeanPostProcessor(conversionService);
    }

    static class ProjectingArgumentResolverBeanPostProcessor
    implements BeanPostProcessor,
    BeanFactoryAware,
    BeanClassLoaderAware {
        private ProxyingHandlerMethodArgumentResolver resolver;

        ProjectingArgumentResolverBeanPostProcessor(ObjectFactory<ConversionService> conversionService) {
            this.resolver = new ProxyingHandlerMethodArgumentResolver(conversionService, false);
        }

        public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
            this.resolver.setBeanFactory(beanFactory);
        }

        public void setBeanClassLoader(ClassLoader classLoader) {
            this.resolver.setBeanClassLoader(classLoader);
        }

        @Nullable
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            return bean;
        }

        @Nullable
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            if (!RequestMappingHandlerAdapter.class.isInstance(bean)) {
                return bean;
            }
            RequestMappingHandlerAdapter adapter = (RequestMappingHandlerAdapter)bean;
            List currentResolvers = adapter.getArgumentResolvers();
            if (currentResolvers == null) {
                throw new IllegalStateException(String.format("No HandlerMethodArgumentResolvers found in RequestMappingHandlerAdapter %s!", beanName));
            }
            ArrayList<ProxyingHandlerMethodArgumentResolver> newResolvers = new ArrayList<ProxyingHandlerMethodArgumentResolver>(currentResolvers.size() + 1);
            newResolvers.add(this.resolver);
            newResolvers.addAll(currentResolvers);
            adapter.setArgumentResolvers(newResolvers);
            return adapter;
        }
    }
}

