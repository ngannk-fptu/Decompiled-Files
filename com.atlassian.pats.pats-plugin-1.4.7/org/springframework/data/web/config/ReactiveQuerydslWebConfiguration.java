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
 *  org.springframework.web.reactive.config.WebFluxConfigurer
 *  org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver
 *  org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer
 */
package org.springframework.data.web.config;

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
import org.springframework.data.web.querydsl.ReactiveQuerydslPredicateArgumentResolver;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer;

@Configuration(proxyBeanMethods=false)
public class ReactiveQuerydslWebConfiguration
implements WebFluxConfigurer {
    @Autowired
    @Qualifier(value="webFluxConversionService")
    ObjectProvider<ConversionService> conversionService;
    @Autowired
    ObjectProvider<EntityPathResolver> resolver;
    @Autowired
    BeanFactory beanFactory;

    @Lazy
    @Bean
    public ReactiveQuerydslPredicateArgumentResolver querydslPredicateArgumentResolver() {
        return new ReactiveQuerydslPredicateArgumentResolver((QuerydslBindingsFactory)this.beanFactory.getBean("querydslBindingsFactory", QuerydslBindingsFactory.class), (ConversionService)this.conversionService.getIfUnique(DefaultConversionService::getSharedInstance));
    }

    @Lazy
    @Bean
    public QuerydslBindingsFactory querydslBindingsFactory() {
        return new QuerydslBindingsFactory((EntityPathResolver)this.resolver.getIfUnique(() -> SimpleEntityPathResolver.INSTANCE));
    }

    public void configureArgumentResolvers(ArgumentResolverConfigurer configurer) {
        configurer.addCustomResolver(new HandlerMethodArgumentResolver[]{(HandlerMethodArgumentResolver)this.beanFactory.getBean("querydslPredicateArgumentResolver", ReactiveQuerydslPredicateArgumentResolver.class)});
    }
}

