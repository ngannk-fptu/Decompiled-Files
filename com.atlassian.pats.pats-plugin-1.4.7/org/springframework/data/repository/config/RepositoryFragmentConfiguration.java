/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.support.AbstractBeanDefinition
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.data.repository.config;

import java.beans.Introspector;
import java.util.Optional;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.data.config.ConfigurationUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

public final class RepositoryFragmentConfiguration {
    private final String interfaceName;
    private final String className;
    private final Optional<AbstractBeanDefinition> beanDefinition;

    public RepositoryFragmentConfiguration(String interfaceName, String className) {
        Assert.hasText((String)interfaceName, (String)"Interface name must not be null or empty!");
        Assert.hasText((String)className, (String)"Class name must not be null or empty!");
        this.interfaceName = interfaceName;
        this.className = className;
        this.beanDefinition = Optional.empty();
    }

    public RepositoryFragmentConfiguration(String interfaceName, AbstractBeanDefinition beanDefinition) {
        Assert.hasText((String)interfaceName, (String)"Interface name must not be null or empty!");
        Assert.notNull((Object)beanDefinition, (String)"Bean definition must not be null!");
        this.interfaceName = interfaceName;
        this.className = ConfigurationUtils.getRequiredBeanClassName((BeanDefinition)beanDefinition);
        this.beanDefinition = Optional.of(beanDefinition);
    }

    public RepositoryFragmentConfiguration(String interfaceName, String className, Optional<AbstractBeanDefinition> beanDefinition) {
        this.interfaceName = interfaceName;
        this.className = className;
        this.beanDefinition = beanDefinition;
    }

    public String getImplementationBeanName() {
        return Introspector.decapitalize(ClassUtils.getShortName((String)this.getClassName()));
    }

    public String getFragmentBeanName() {
        return this.getImplementationBeanName() + "Fragment";
    }

    public String getInterfaceName() {
        return this.interfaceName;
    }

    public String getClassName() {
        return this.className;
    }

    public Optional<AbstractBeanDefinition> getBeanDefinition() {
        return this.beanDefinition;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RepositoryFragmentConfiguration)) {
            return false;
        }
        RepositoryFragmentConfiguration that = (RepositoryFragmentConfiguration)o;
        if (!ObjectUtils.nullSafeEquals((Object)this.interfaceName, (Object)that.interfaceName)) {
            return false;
        }
        if (!ObjectUtils.nullSafeEquals((Object)this.className, (Object)that.className)) {
            return false;
        }
        return ObjectUtils.nullSafeEquals(this.beanDefinition, that.beanDefinition);
    }

    public int hashCode() {
        int result = ObjectUtils.nullSafeHashCode((Object)this.interfaceName);
        result = 31 * result + ObjectUtils.nullSafeHashCode((Object)this.className);
        result = 31 * result + ObjectUtils.nullSafeHashCode(this.beanDefinition);
        return result;
    }

    public String toString() {
        return "RepositoryFragmentConfiguration(interfaceName=" + this.getInterfaceName() + ", className=" + this.getClassName() + ", beanDefinition=" + this.getBeanDefinition() + ")";
    }
}

