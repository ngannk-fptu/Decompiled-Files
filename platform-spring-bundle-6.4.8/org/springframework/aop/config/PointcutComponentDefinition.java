/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.AbstractComponentDefinition;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class PointcutComponentDefinition
extends AbstractComponentDefinition {
    private final String pointcutBeanName;
    private final BeanDefinition pointcutDefinition;
    private final String description;

    public PointcutComponentDefinition(String pointcutBeanName, BeanDefinition pointcutDefinition, String expression) {
        Assert.notNull((Object)pointcutBeanName, "Bean name must not be null");
        Assert.notNull((Object)pointcutDefinition, "Pointcut definition must not be null");
        Assert.notNull((Object)expression, "Expression must not be null");
        this.pointcutBeanName = pointcutBeanName;
        this.pointcutDefinition = pointcutDefinition;
        this.description = "Pointcut <name='" + pointcutBeanName + "', expression=[" + expression + "]>";
    }

    @Override
    public String getName() {
        return this.pointcutBeanName;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public BeanDefinition[] getBeanDefinitions() {
        return new BeanDefinition[]{this.pointcutDefinition};
    }

    @Override
    @Nullable
    public Object getSource() {
        return this.pointcutDefinition.getSource();
    }
}

