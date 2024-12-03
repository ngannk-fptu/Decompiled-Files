/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.annotation;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.wiring.BeanWiringInfo;
import org.springframework.beans.factory.wiring.BeanWiringInfoResolver;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

public class AnnotationBeanWiringInfoResolver
implements BeanWiringInfoResolver {
    @Override
    @Nullable
    public BeanWiringInfo resolveWiringInfo(Object beanInstance) {
        Assert.notNull(beanInstance, "Bean instance must not be null");
        Configurable annotation = beanInstance.getClass().getAnnotation(Configurable.class);
        return annotation != null ? this.buildWiringInfo(beanInstance, annotation) : null;
    }

    protected BeanWiringInfo buildWiringInfo(Object beanInstance, Configurable annotation) {
        if (!Autowire.NO.equals((Object)annotation.autowire())) {
            return new BeanWiringInfo(annotation.autowire().value(), annotation.dependencyCheck());
        }
        if (!annotation.value().isEmpty()) {
            return new BeanWiringInfo(annotation.value(), false);
        }
        return new BeanWiringInfo(this.getDefaultBeanName(beanInstance), true);
    }

    protected String getDefaultBeanName(Object beanInstance) {
        return ClassUtils.getUserClass(beanInstance).getName();
    }
}

