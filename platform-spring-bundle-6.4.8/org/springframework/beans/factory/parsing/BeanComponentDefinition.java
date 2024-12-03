/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.parsing;

import java.util.ArrayList;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.BeanReference;
import org.springframework.beans.factory.parsing.ComponentDefinition;
import org.springframework.lang.Nullable;

public class BeanComponentDefinition
extends BeanDefinitionHolder
implements ComponentDefinition {
    private BeanDefinition[] innerBeanDefinitions;
    private BeanReference[] beanReferences;

    public BeanComponentDefinition(BeanDefinition beanDefinition, String beanName) {
        this(new BeanDefinitionHolder(beanDefinition, beanName));
    }

    public BeanComponentDefinition(BeanDefinition beanDefinition, String beanName, @Nullable String[] aliases) {
        this(new BeanDefinitionHolder(beanDefinition, beanName, aliases));
    }

    public BeanComponentDefinition(BeanDefinitionHolder beanDefinitionHolder) {
        super(beanDefinitionHolder);
        ArrayList<BeanDefinition> innerBeans = new ArrayList<BeanDefinition>();
        ArrayList<BeanReference> references = new ArrayList<BeanReference>();
        MutablePropertyValues propertyValues = beanDefinitionHolder.getBeanDefinition().getPropertyValues();
        for (PropertyValue propertyValue : propertyValues.getPropertyValues()) {
            Object value = propertyValue.getValue();
            if (value instanceof BeanDefinitionHolder) {
                innerBeans.add(((BeanDefinitionHolder)value).getBeanDefinition());
                continue;
            }
            if (value instanceof BeanDefinition) {
                innerBeans.add((BeanDefinition)value);
                continue;
            }
            if (!(value instanceof BeanReference)) continue;
            references.add((BeanReference)value);
        }
        this.innerBeanDefinitions = innerBeans.toArray(new BeanDefinition[0]);
        this.beanReferences = references.toArray(new BeanReference[0]);
    }

    @Override
    public String getName() {
        return this.getBeanName();
    }

    @Override
    public String getDescription() {
        return this.getShortDescription();
    }

    @Override
    public BeanDefinition[] getBeanDefinitions() {
        return new BeanDefinition[]{this.getBeanDefinition()};
    }

    @Override
    public BeanDefinition[] getInnerBeanDefinitions() {
        return this.innerBeanDefinitions;
    }

    @Override
    public BeanReference[] getBeanReferences() {
        return this.beanReferences;
    }

    @Override
    public String toString() {
        return this.getDescription();
    }

    @Override
    public boolean equals(@Nullable Object other) {
        return this == other || other instanceof BeanComponentDefinition && super.equals(other);
    }
}

