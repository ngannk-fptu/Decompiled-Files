/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.parsing.AbstractComponentDefinition
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
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
        Assert.notNull((Object)pointcutBeanName, (String)"Bean name must not be null");
        Assert.notNull((Object)pointcutDefinition, (String)"Pointcut definition must not be null");
        Assert.notNull((Object)expression, (String)"Expression must not be null");
        this.pointcutBeanName = pointcutBeanName;
        this.pointcutDefinition = pointcutDefinition;
        this.description = "Pointcut <name='" + pointcutBeanName + "', expression=[" + expression + "]>";
    }

    public String getName() {
        return this.pointcutBeanName;
    }

    public String getDescription() {
        return this.description;
    }

    public BeanDefinition[] getBeanDefinitions() {
        return new BeanDefinition[]{this.pointcutDefinition};
    }

    @Nullable
    public Object getSource() {
        return this.pointcutDefinition.getSource();
    }
}

