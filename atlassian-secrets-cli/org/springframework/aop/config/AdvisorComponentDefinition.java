/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.config;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanReference;
import org.springframework.beans.factory.parsing.AbstractComponentDefinition;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class AdvisorComponentDefinition
extends AbstractComponentDefinition {
    private final String advisorBeanName;
    private final BeanDefinition advisorDefinition;
    private final String description;
    private final BeanReference[] beanReferences;
    private final BeanDefinition[] beanDefinitions;

    public AdvisorComponentDefinition(String advisorBeanName, BeanDefinition advisorDefinition) {
        this(advisorBeanName, advisorDefinition, null);
    }

    public AdvisorComponentDefinition(String advisorBeanName, BeanDefinition advisorDefinition, @Nullable BeanDefinition pointcutDefinition) {
        Assert.notNull((Object)advisorBeanName, "'advisorBeanName' must not be null");
        Assert.notNull((Object)advisorDefinition, "'advisorDefinition' must not be null");
        this.advisorBeanName = advisorBeanName;
        this.advisorDefinition = advisorDefinition;
        MutablePropertyValues pvs = advisorDefinition.getPropertyValues();
        BeanReference adviceReference = (BeanReference)pvs.get("adviceBeanName");
        Assert.state(adviceReference != null, "Missing 'adviceBeanName' property");
        if (pointcutDefinition != null) {
            this.beanReferences = new BeanReference[]{adviceReference};
            this.beanDefinitions = new BeanDefinition[]{advisorDefinition, pointcutDefinition};
            this.description = this.buildDescription(adviceReference, pointcutDefinition);
        } else {
            BeanReference pointcutReference = (BeanReference)pvs.get("pointcut");
            Assert.state(pointcutReference != null, "Missing 'pointcut' property");
            this.beanReferences = new BeanReference[]{adviceReference, pointcutReference};
            this.beanDefinitions = new BeanDefinition[]{advisorDefinition};
            this.description = this.buildDescription(adviceReference, pointcutReference);
        }
    }

    private String buildDescription(BeanReference adviceReference, BeanDefinition pointcutDefinition) {
        return "Advisor <advice(ref)='" + adviceReference.getBeanName() + "', pointcut(expression)=[" + pointcutDefinition.getPropertyValues().get("expression") + "]>";
    }

    private String buildDescription(BeanReference adviceReference, BeanReference pointcutReference) {
        return "Advisor <advice(ref)='" + adviceReference.getBeanName() + "', pointcut(ref)='" + pointcutReference.getBeanName() + "'>";
    }

    @Override
    public String getName() {
        return this.advisorBeanName;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public BeanDefinition[] getBeanDefinitions() {
        return this.beanDefinitions;
    }

    @Override
    public BeanReference[] getBeanReferences() {
        return this.beanReferences;
    }

    @Override
    @Nullable
    public Object getSource() {
        return this.advisorDefinition.getSource();
    }
}

