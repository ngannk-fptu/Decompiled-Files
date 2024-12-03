/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.ObjectProvider
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.context.annotation.Lazy
 *  org.springframework.core.convert.ConversionService
 *  org.springframework.core.convert.support.DefaultConversionService
 *  org.springframework.web.method.support.HandlerMethodArgumentResolver
 *  org.springframework.web.servlet.config.annotation.WebMvcConfigurer
 */
package org.springframework.data.web.config;

import java.util.List;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.data.querydsl.EntityPathResolver;
import org.springframework.data.querydsl.SimpleEntityPathResolver;
import org.springframework.data.querydsl.binding.QuerydslBindingsFactory;
import org.springframework.data.web.querydsl.QuerydslPredicateArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration(proxyBeanMethods=false)
public class QuerydslWebConfiguration
implements WebMvcConfigurer {
    @Autowired
    @Qualifier(value="mvcConversionService")
    ObjectProvider<ConversionService> conversionService;
    @Autowired
    ObjectProvider<EntityPathResolver> resolver;
    @Autowired
    BeanFactory beanFactory;

    @Lazy
    @Bean
    public QuerydslPredicateArgumentResolver querydslPredicateArgumentResolver() {
        return new QuerydslPredicateArgumentResolver((QuerydslBindingsFactory)this.beanFactory.getBean("querydslBindingsFactory", QuerydslBindingsFactory.class), (ConversionService)this.conversionService.getIfUnique(DefaultConversionService::getSharedInstance));
    }

    @Lazy
    @Bean
    public QuerydslBindingsFactory querydslBindingsFactory() {
        return new QuerydslBindingsFactory((EntityPathResolver)this.resolver.getIfUnique(() -> SimpleEntityPathResolver.INSTANCE));
    }

    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(0, (HandlerMethodArgumentResolver)this.beanFactory.getBean("querydslPredicateArgumentResolver", QuerydslPredicateArgumentResolver.class));
    }
}

