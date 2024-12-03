/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.metadata.raw;

import java.util.List;
import java.util.Set;
import org.hibernate.validator.internal.metadata.raw.ConfigurationSource;
import org.hibernate.validator.internal.metadata.raw.ConstrainedElement;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.spi.group.DefaultGroupSequenceProvider;

public class BeanConfiguration<T> {
    private final ConfigurationSource source;
    private final Class<T> beanClass;
    private final Set<ConstrainedElement> constrainedElements;
    private final List<Class<?>> defaultGroupSequence;
    private final DefaultGroupSequenceProvider<? super T> defaultGroupSequenceProvider;

    public BeanConfiguration(ConfigurationSource source, Class<T> beanClass, Set<? extends ConstrainedElement> constrainedElements, List<Class<?>> defaultGroupSequence, DefaultGroupSequenceProvider<? super T> defaultGroupSequenceProvider) {
        this.source = source;
        this.beanClass = beanClass;
        this.constrainedElements = CollectionHelper.newHashSet(constrainedElements);
        this.defaultGroupSequence = defaultGroupSequence;
        this.defaultGroupSequenceProvider = defaultGroupSequenceProvider;
    }

    public ConfigurationSource getSource() {
        return this.source;
    }

    public Class<T> getBeanClass() {
        return this.beanClass;
    }

    public Set<ConstrainedElement> getConstrainedElements() {
        return this.constrainedElements;
    }

    public List<Class<?>> getDefaultGroupSequence() {
        return this.defaultGroupSequence;
    }

    public DefaultGroupSequenceProvider<? super T> getDefaultGroupSequenceProvider() {
        return this.defaultGroupSequenceProvider;
    }

    public String toString() {
        return "BeanConfiguration [beanClass=" + this.beanClass.getSimpleName() + ", source=" + (Object)((Object)this.source) + ", constrainedElements=" + this.constrainedElements + ", defaultGroupSequence=" + this.defaultGroupSequence + ", defaultGroupSequenceProvider=" + this.defaultGroupSequenceProvider + "]";
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.beanClass == null ? 0 : this.beanClass.hashCode());
        result = 31 * result + (this.source == null ? 0 : this.source.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        BeanConfiguration other = (BeanConfiguration)obj;
        if (this.beanClass == null ? other.beanClass != null : !this.beanClass.equals(other.beanClass)) {
            return false;
        }
        return this.source == other.source;
    }
}

