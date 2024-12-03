/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.metadata.raw;

import java.util.Collections;
import java.util.Set;
import org.hibernate.validator.internal.metadata.aggregated.CascadingMetaDataBuilder;
import org.hibernate.validator.internal.metadata.core.MetaConstraint;
import org.hibernate.validator.internal.metadata.raw.AbstractConstrainedElement;
import org.hibernate.validator.internal.metadata.raw.ConfigurationSource;
import org.hibernate.validator.internal.metadata.raw.ConstrainedElement;

public class ConstrainedType
extends AbstractConstrainedElement {
    private final Class<?> beanClass;

    public ConstrainedType(ConfigurationSource source, Class<?> beanClass, Set<MetaConstraint<?>> constraints) {
        super(source, ConstrainedElement.ConstrainedElementKind.TYPE, constraints, Collections.emptySet(), CascadingMetaDataBuilder.nonCascading());
        this.beanClass = beanClass;
    }

    public Class<?> getBeanClass() {
        return this.beanClass;
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = super.hashCode();
        result = 31 * result + (this.beanClass == null ? 0 : this.beanClass.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        ConstrainedType other = (ConstrainedType)obj;
        return !(this.beanClass == null ? other.beanClass != null : !this.beanClass.equals(other.beanClass));
    }
}

