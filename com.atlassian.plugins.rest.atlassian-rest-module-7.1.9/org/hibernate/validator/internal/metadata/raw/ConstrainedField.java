/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.metadata.raw;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.util.Set;
import org.hibernate.validator.internal.metadata.aggregated.CascadingMetaDataBuilder;
import org.hibernate.validator.internal.metadata.core.MetaConstraint;
import org.hibernate.validator.internal.metadata.raw.AbstractConstrainedElement;
import org.hibernate.validator.internal.metadata.raw.ConfigurationSource;
import org.hibernate.validator.internal.metadata.raw.ConstrainedElement;
import org.hibernate.validator.internal.util.StringHelper;

public class ConstrainedField
extends AbstractConstrainedElement {
    private final Field field;

    public ConstrainedField(ConfigurationSource source, Field field, Set<MetaConstraint<?>> constraints, Set<MetaConstraint<?>> typeArgumentConstraints, CascadingMetaDataBuilder cascadingMetaDataBuilder) {
        super(source, ConstrainedElement.ConstrainedElementKind.FIELD, constraints, typeArgumentConstraints, cascadingMetaDataBuilder);
        this.field = field;
    }

    public Field getField() {
        return this.field;
    }

    @Override
    public String toString() {
        return "ConstrainedField [field=" + StringHelper.toShortString((Member)this.field) + "]";
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = super.hashCode();
        result = 31 * result + (this.field == null ? 0 : this.field.hashCode());
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
        ConstrainedField other = (ConstrainedField)obj;
        return !(this.field == null ? other.field != null : !this.field.equals(other.field));
    }
}

