/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.metamodel.Attribute
 *  javax.persistence.metamodel.Attribute$PersistentAttributeType
 *  javax.persistence.metamodel.Bindable
 *  javax.persistence.metamodel.EmbeddableType
 *  javax.persistence.metamodel.IdentifiableType
 *  javax.persistence.metamodel.ManagedType
 *  javax.persistence.metamodel.SingularAttribute
 */
package org.hibernate.query.criteria.internal.path;

import java.io.Serializable;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.EmbeddableType;
import javax.persistence.metamodel.IdentifiableType;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.SingularAttribute;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.PathSource;
import org.hibernate.query.criteria.internal.compile.RenderingContext;
import org.hibernate.query.criteria.internal.path.AbstractPathImpl;

public class SingularAttributePath<X>
extends AbstractPathImpl<X>
implements Serializable {
    private final SingularAttribute<?, X> attribute;
    private final ManagedType<X> managedType;

    public SingularAttributePath(CriteriaBuilderImpl criteriaBuilder, Class<X> javaType, PathSource pathSource, SingularAttribute<?, X> attribute) {
        super(criteriaBuilder, javaType, pathSource);
        this.attribute = attribute;
        this.managedType = this.resolveManagedType(attribute);
    }

    private ManagedType<X> resolveManagedType(SingularAttribute<?, X> attribute) {
        if (Attribute.PersistentAttributeType.BASIC == attribute.getPersistentAttributeType()) {
            return null;
        }
        if (Attribute.PersistentAttributeType.EMBEDDED == attribute.getPersistentAttributeType()) {
            return (EmbeddableType)attribute.getType();
        }
        return (IdentifiableType)attribute.getType();
    }

    @Override
    public SingularAttribute<?, X> getAttribute() {
        return this.attribute;
    }

    public Bindable<X> getModel() {
        return this.getAttribute();
    }

    @Override
    protected boolean canBeDereferenced() {
        return this.managedType != null;
    }

    @Override
    protected Attribute locateAttributeInternal(String attributeName) {
        Attribute attribute = this.managedType.getAttribute(attributeName);
        if (attribute == null) {
            throw new IllegalArgumentException("Could not resolve attribute named " + attributeName);
        }
        return attribute;
    }

    @Override
    public <T extends X> SingularAttributePath<T> treatAs(Class<T> treatAsType) {
        return new TreatedSingularAttributePath<T>(this, treatAsType);
    }

    public static class TreatedSingularAttributePath<T>
    extends SingularAttributePath<T> {
        private final SingularAttributePath<? super T> original;
        private final Class<T> treatAsType;

        public TreatedSingularAttributePath(SingularAttributePath<? super T> original, Class<T> treatAsType) {
            super(original.criteriaBuilder(), treatAsType, original.getPathSource(), original.getAttribute());
            this.original = original;
            this.treatAsType = treatAsType;
        }

        @Override
        public String getAlias() {
            return this.original.getAlias();
        }

        @Override
        public void prepareAlias(RenderingContext renderingContext) {
        }

        @Override
        public String render(RenderingContext renderingContext) {
            return "treat(" + this.original.render(renderingContext) + " as " + this.treatAsType.getName() + ")";
        }
    }
}

