/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.config;

import org.springframework.beans.BeanMetadataElement;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public class BeanDefinitionHolder
implements BeanMetadataElement {
    private final BeanDefinition beanDefinition;
    private final String beanName;
    @Nullable
    private final String[] aliases;

    public BeanDefinitionHolder(BeanDefinition beanDefinition, String beanName) {
        this(beanDefinition, beanName, null);
    }

    public BeanDefinitionHolder(BeanDefinition beanDefinition, String beanName, @Nullable String[] aliases) {
        Assert.notNull((Object)beanDefinition, "BeanDefinition must not be null");
        Assert.notNull((Object)beanName, "Bean name must not be null");
        this.beanDefinition = beanDefinition;
        this.beanName = beanName;
        this.aliases = aliases;
    }

    public BeanDefinitionHolder(BeanDefinitionHolder beanDefinitionHolder) {
        Assert.notNull((Object)beanDefinitionHolder, "BeanDefinitionHolder must not be null");
        this.beanDefinition = beanDefinitionHolder.getBeanDefinition();
        this.beanName = beanDefinitionHolder.getBeanName();
        this.aliases = beanDefinitionHolder.getAliases();
    }

    public BeanDefinition getBeanDefinition() {
        return this.beanDefinition;
    }

    public String getBeanName() {
        return this.beanName;
    }

    @Nullable
    public String[] getAliases() {
        return this.aliases;
    }

    @Override
    @Nullable
    public Object getSource() {
        return this.beanDefinition.getSource();
    }

    public boolean matchesName(@Nullable String candidateName) {
        return candidateName != null && (candidateName.equals(this.beanName) || candidateName.equals(BeanFactoryUtils.transformedBeanName(this.beanName)) || ObjectUtils.containsElement(this.aliases, candidateName));
    }

    public String getShortDescription() {
        if (this.aliases == null) {
            return "Bean definition with name '" + this.beanName + "'";
        }
        return "Bean definition with name '" + this.beanName + "' and aliases [" + StringUtils.arrayToCommaDelimitedString(this.aliases) + ']';
    }

    public String getLongDescription() {
        return this.getShortDescription() + ": " + this.beanDefinition;
    }

    public String toString() {
        return this.getLongDescription();
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof BeanDefinitionHolder)) {
            return false;
        }
        BeanDefinitionHolder otherHolder = (BeanDefinitionHolder)other;
        return this.beanDefinition.equals(otherHolder.beanDefinition) && this.beanName.equals(otherHolder.beanName) && ObjectUtils.nullSafeEquals(this.aliases, otherHolder.aliases);
    }

    public int hashCode() {
        int hashCode = this.beanDefinition.hashCode();
        hashCode = 29 * hashCode + this.beanName.hashCode();
        hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.aliases);
        return hashCode;
    }
}

