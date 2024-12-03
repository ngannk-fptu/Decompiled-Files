/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  org.springframework.beans.factory.BeanClassLoaderAware
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.ObjectFactory
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.core.convert.ConversionService
 *  org.springframework.format.Formatter
 *  org.springframework.format.FormatterRegistry
 *  org.springframework.format.support.FormattingConversionService
 *  org.springframework.http.converter.HttpMessageConverter
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.web.method.support.HandlerMethodArgumentResolver
 *  org.springframework.web.servlet.config.annotation.WebMvcConfigurer
 */
package org.springframework.data.web.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.geo.format.DistanceFormatter;
import org.springframework.data.geo.format.PointFormatter;
import org.springframework.data.repository.support.DomainClassConverter;
import org.springframework.data.util.Lazy;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.ProjectingJackson2HttpMessageConverter;
import org.springframework.data.web.ProxyingHandlerMethodArgumentResolver;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.data.web.XmlBeamHttpMessageConverter;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;
import org.springframework.data.web.config.SortHandlerMethodArgumentResolverCustomizer;
import org.springframework.format.Formatter;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration(proxyBeanMethods=false)
public class SpringDataWebConfiguration
implements WebMvcConfigurer,
BeanClassLoaderAware {
    private final ApplicationContext context;
    private final ObjectFactory<ConversionService> conversionService;
    @Nullable
    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();
    private final Lazy<SortHandlerMethodArgumentResolver> sortResolver;
    private final Lazy<PageableHandlerMethodArgumentResolver> pageableResolver;
    private final Lazy<PageableHandlerMethodArgumentResolverCustomizer> pageableResolverCustomizer;
    private final Lazy<SortHandlerMethodArgumentResolverCustomizer> sortResolverCustomizer;

    public SpringDataWebConfiguration(ApplicationContext context, @Qualifier(value="mvcConversionService") ObjectFactory<ConversionService> conversionService) {
        Assert.notNull((Object)context, (String)"ApplicationContext must not be null!");
        Assert.notNull(conversionService, (String)"ConversionService must not be null!");
        this.context = context;
        this.conversionService = conversionService;
        this.sortResolver = Lazy.of(() -> (SortHandlerMethodArgumentResolver)context.getBean("sortResolver", SortHandlerMethodArgumentResolver.class));
        this.pageableResolver = Lazy.of(() -> (PageableHandlerMethodArgumentResolver)context.getBean("pageableResolver", PageableHandlerMethodArgumentResolver.class));
        this.pageableResolverCustomizer = Lazy.of(() -> (PageableHandlerMethodArgumentResolverCustomizer)context.getBeanProvider(PageableHandlerMethodArgumentResolverCustomizer.class).getIfAvailable());
        this.sortResolverCustomizer = Lazy.of(() -> (SortHandlerMethodArgumentResolverCustomizer)context.getBeanProvider(SortHandlerMethodArgumentResolverCustomizer.class).getIfAvailable());
    }

    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    @Bean
    public PageableHandlerMethodArgumentResolver pageableResolver() {
        PageableHandlerMethodArgumentResolver pageableResolver = new PageableHandlerMethodArgumentResolver(this.sortResolver.get());
        this.customizePageableResolver(pageableResolver);
        return pageableResolver;
    }

    @Bean
    public SortHandlerMethodArgumentResolver sortResolver() {
        SortHandlerMethodArgumentResolver sortResolver = new SortHandlerMethodArgumentResolver();
        this.customizeSortResolver(sortResolver);
        return sortResolver;
    }

    public void addFormatters(FormatterRegistry registry) {
        registry.addFormatter((Formatter)DistanceFormatter.INSTANCE);
        registry.addFormatter((Formatter)PointFormatter.INSTANCE);
        if (!(registry instanceof FormattingConversionService)) {
            return;
        }
        FormattingConversionService conversionService = (FormattingConversionService)registry;
        DomainClassConverter<FormattingConversionService> converter = new DomainClassConverter<FormattingConversionService>(conversionService);
        converter.setApplicationContext(this.context);
    }

    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(this.sortResolver.get());
        argumentResolvers.add(this.pageableResolver.get());
        ProxyingHandlerMethodArgumentResolver resolver = new ProxyingHandlerMethodArgumentResolver(this.conversionService, true);
        resolver.setBeanFactory((BeanFactory)this.context);
        this.forwardBeanClassLoader(resolver);
        argumentResolvers.add((HandlerMethodArgumentResolver)resolver);
    }

    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        if (ClassUtils.isPresent((String)"com.jayway.jsonpath.DocumentContext", (ClassLoader)this.context.getClassLoader()) && ClassUtils.isPresent((String)"com.fasterxml.jackson.databind.ObjectMapper", (ClassLoader)this.context.getClassLoader())) {
            ObjectMapper mapper = (ObjectMapper)this.context.getBeanProvider(ObjectMapper.class).getIfUnique(ObjectMapper::new);
            ProjectingJackson2HttpMessageConverter converter = new ProjectingJackson2HttpMessageConverter(mapper);
            converter.setBeanFactory((BeanFactory)this.context);
            this.forwardBeanClassLoader(converter);
            converters.add(0, (HttpMessageConverter<?>)converter);
        }
        if (ClassUtils.isPresent((String)"org.xmlbeam.XBProjector", (ClassLoader)this.context.getClassLoader())) {
            converters.add(0, (HttpMessageConverter<?>)this.context.getBeanProvider(XmlBeamHttpMessageConverter.class).getIfAvailable(XmlBeamHttpMessageConverter::new));
        }
    }

    protected void customizePageableResolver(PageableHandlerMethodArgumentResolver pageableResolver) {
        this.pageableResolverCustomizer.getOptional().ifPresent(c -> c.customize(pageableResolver));
    }

    protected void customizeSortResolver(SortHandlerMethodArgumentResolver sortResolver) {
        this.sortResolverCustomizer.getOptional().ifPresent(c -> c.customize(sortResolver));
    }

    private void forwardBeanClassLoader(BeanClassLoaderAware target) {
        if (this.beanClassLoader != null) {
            target.setBeanClassLoader(this.beanClassLoader);
        }
    }
}

