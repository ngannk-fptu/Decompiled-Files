/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.servlet.config.annotation;

import java.util.List;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.Validator;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;

public interface WebMvcConfigurer {
    default public void configurePathMatch(PathMatchConfigurer configurer) {
    }

    default public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
    }

    default public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
    }

    default public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
    }

    default public void addFormatters(FormatterRegistry registry) {
    }

    default public void addInterceptors(InterceptorRegistry registry) {
    }

    default public void addResourceHandlers(ResourceHandlerRegistry registry) {
    }

    default public void addCorsMappings(CorsRegistry registry) {
    }

    default public void addViewControllers(ViewControllerRegistry registry) {
    }

    default public void configureViewResolvers(ViewResolverRegistry registry) {
    }

    default public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    }

    default public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> handlers) {
    }

    default public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    }

    default public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
    }

    default public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
    }

    default public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
    }

    @Nullable
    default public Validator getValidator() {
        return null;
    }

    @Nullable
    default public MessageCodesResolver getMessageCodesResolver() {
        return null;
    }
}

