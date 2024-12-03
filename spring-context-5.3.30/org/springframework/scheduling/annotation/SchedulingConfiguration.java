/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.scheduling.annotation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;

@Configuration(proxyBeanMethods=false)
@Role(value=2)
public class SchedulingConfiguration {
    @Bean(name={"org.springframework.context.annotation.internalScheduledAnnotationProcessor"})
    @Role(value=2)
    public ScheduledAnnotationBeanPostProcessor scheduledAnnotationProcessor() {
        return new ScheduledAnnotationBeanPostProcessor();
    }
}

