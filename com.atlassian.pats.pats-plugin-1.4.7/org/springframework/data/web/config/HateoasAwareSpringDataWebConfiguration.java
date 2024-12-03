/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.ObjectFactory
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.core.convert.ConversionService
 *  org.springframework.web.method.support.HandlerMethodArgumentResolver
 */
package org.springframework.data.web.config;

import java.util.List;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.util.Lazy;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.data.web.HateoasSortHandlerMethodArgumentResolver;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.data.web.PagedResourcesAssemblerArgumentResolver;
import org.springframework.data.web.config.SpringDataWebConfiguration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

@Configuration(proxyBeanMethods=false)
public class HateoasAwareSpringDataWebConfiguration
extends SpringDataWebConfiguration {
    private final Lazy<HateoasSortHandlerMethodArgumentResolver> sortResolver = Lazy.of(() -> (HateoasSortHandlerMethodArgumentResolver)context.getBean("sortResolver", HateoasSortHandlerMethodArgumentResolver.class));
    private final Lazy<HateoasPageableHandlerMethodArgumentResolver> pageableResolver = Lazy.of(() -> (HateoasPageableHandlerMethodArgumentResolver)context.getBean("pageableResolver", HateoasPageableHandlerMethodArgumentResolver.class));
    private final Lazy<PagedResourcesAssemblerArgumentResolver> argumentResolver = Lazy.of(() -> (PagedResourcesAssemblerArgumentResolver)context.getBean("pagedResourcesAssemblerArgumentResolver", PagedResourcesAssemblerArgumentResolver.class));

    public HateoasAwareSpringDataWebConfiguration(ApplicationContext context, @Qualifier(value="mvcConversionService") ObjectFactory<ConversionService> conversionService) {
        super(context, conversionService);
    }

    @Override
    @Bean
    public HateoasPageableHandlerMethodArgumentResolver pageableResolver() {
        HateoasPageableHandlerMethodArgumentResolver pageableResolver = new HateoasPageableHandlerMethodArgumentResolver(this.sortResolver.get());
        this.customizePageableResolver(pageableResolver);
        return pageableResolver;
    }

    @Override
    @Bean
    public HateoasSortHandlerMethodArgumentResolver sortResolver() {
        HateoasSortHandlerMethodArgumentResolver sortResolver = new HateoasSortHandlerMethodArgumentResolver();
        this.customizeSortResolver(sortResolver);
        return sortResolver;
    }

    @Bean
    public PagedResourcesAssembler<?> pagedResourcesAssembler() {
        return new PagedResourcesAssembler(this.pageableResolver.get(), null);
    }

    @Bean
    public PagedResourcesAssemblerArgumentResolver pagedResourcesAssemblerArgumentResolver() {
        return new PagedResourcesAssemblerArgumentResolver(this.pageableResolver.get());
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        super.addArgumentResolvers(argumentResolvers);
        argumentResolvers.add(this.argumentResolver.get());
    }
}

