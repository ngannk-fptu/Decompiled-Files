/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.scheduling.annotation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.scheduling.annotation.AbstractAsyncConfiguration;
import org.springframework.scheduling.annotation.AsyncAnnotationBeanPostProcessor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.util.Assert;

@Configuration
@Role(value=2)
public class ProxyAsyncConfiguration
extends AbstractAsyncConfiguration {
    @Bean(name={"org.springframework.context.annotation.internalAsyncAnnotationProcessor"})
    @Role(value=2)
    public AsyncAnnotationBeanPostProcessor asyncAdvisor() {
        Assert.notNull((Object)this.enableAsync, "@EnableAsync annotation metadata was not injected");
        AsyncAnnotationBeanPostProcessor bpp = new AsyncAnnotationBeanPostProcessor();
        Class customAsyncAnnotation = this.enableAsync.getClass("annotation");
        if (customAsyncAnnotation != AnnotationUtils.getDefaultValue(EnableAsync.class, "annotation")) {
            bpp.setAsyncAnnotationType(customAsyncAnnotation);
        }
        if (this.executor != null) {
            bpp.setExecutor(this.executor);
        }
        if (this.exceptionHandler != null) {
            bpp.setExceptionHandler(this.exceptionHandler);
        }
        bpp.setProxyTargetClass(this.enableAsync.getBoolean("proxyTargetClass"));
        bpp.setOrder((Integer)this.enableAsync.getNumber("order"));
        return bpp;
    }
}

