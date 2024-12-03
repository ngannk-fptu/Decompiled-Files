/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.util.Assert
 */
package org.springframework.context.annotation;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.util.Assert;

public class FullyQualifiedAnnotationBeanNameGenerator
extends AnnotationBeanNameGenerator {
    public static final FullyQualifiedAnnotationBeanNameGenerator INSTANCE = new FullyQualifiedAnnotationBeanNameGenerator();

    @Override
    protected String buildDefaultBeanName(BeanDefinition definition) {
        String beanClassName = definition.getBeanClassName();
        Assert.state((beanClassName != null ? 1 : 0) != 0, (String)"No bean class name set");
        return beanClassName;
    }
}

