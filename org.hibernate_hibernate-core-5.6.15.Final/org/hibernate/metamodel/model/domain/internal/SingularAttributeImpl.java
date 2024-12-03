/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.metamodel.Attribute$PersistentAttributeType
 *  javax.persistence.metamodel.Bindable$BindableType
 */
package org.hibernate.metamodel.model.domain.internal;

import java.io.Serializable;
import java.lang.reflect.Member;
import java.util.function.Supplier;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Bindable;
import org.hibernate.graph.spi.GraphHelper;
import org.hibernate.metamodel.model.domain.internal.AbstractAttribute;
import org.hibernate.metamodel.model.domain.spi.ManagedTypeDescriptor;
import org.hibernate.metamodel.model.domain.spi.SimpleTypeDescriptor;
import org.hibernate.metamodel.model.domain.spi.SingularPersistentAttribute;

public class SingularAttributeImpl<D, J>
extends AbstractAttribute<D, J>
implements SingularPersistentAttribute<D, J>,
Serializable {
    private final boolean isIdentifier;
    private final boolean isVersion;
    private final boolean isOptional;
    private final SimpleTypeDescriptor<J> attributeType;
    private final DelayedKeyTypeAccess graphKeyTypeAccess = new DelayedKeyTypeAccess();

    public SingularAttributeImpl(ManagedTypeDescriptor<D> declaringType, String name, Attribute.PersistentAttributeType attributeNature, SimpleTypeDescriptor<J> attributeType, Member member, boolean isIdentifier, boolean isVersion, boolean isOptional) {
        super(declaringType, name, attributeNature, attributeType, member);
        this.isIdentifier = isIdentifier;
        this.isVersion = isVersion;
        this.isOptional = isOptional;
        this.attributeType = attributeType;
    }

    @Override
    public SimpleTypeDescriptor<J> getValueGraphType() {
        return this.attributeType;
    }

    @Override
    public SimpleTypeDescriptor<J> getKeyGraphType() {
        return this.graphKeyTypeAccess.get();
    }

    public boolean isId() {
        return this.isIdentifier;
    }

    public boolean isVersion() {
        return this.isVersion;
    }

    public boolean isOptional() {
        return this.isOptional;
    }

    @Override
    public SimpleTypeDescriptor<J> getType() {
        return this.attributeType;
    }

    public boolean isAssociation() {
        return this.getPersistentAttributeType() == Attribute.PersistentAttributeType.MANY_TO_ONE || this.getPersistentAttributeType() == Attribute.PersistentAttributeType.ONE_TO_ONE;
    }

    public boolean isCollection() {
        return false;
    }

    public Bindable.BindableType getBindableType() {
        return Bindable.BindableType.SINGULAR_ATTRIBUTE;
    }

    public Class<J> getBindableJavaType() {
        return this.attributeType.getJavaType();
    }

    private class DelayedKeyTypeAccess
    implements Supplier<SimpleTypeDescriptor<J>>,
    Serializable {
        private boolean resolved;
        private SimpleTypeDescriptor<J> type;

        private DelayedKeyTypeAccess() {
        }

        @Override
        public SimpleTypeDescriptor<J> get() {
            if (!this.resolved) {
                this.type = GraphHelper.resolveKeyTypeDescriptor(SingularAttributeImpl.this);
                this.resolved = true;
            }
            return this.type;
        }
    }

    public static class Version<X, Y>
    extends SingularAttributeImpl<X, Y> {
        public Version(ManagedTypeDescriptor<X> declaringType, String name, Attribute.PersistentAttributeType attributeNature, SimpleTypeDescriptor<Y> attributeType, Member member) {
            super(declaringType, name, attributeNature, attributeType, member, false, true, false);
        }
    }

    public static class Identifier<D, J>
    extends SingularAttributeImpl<D, J> {
        public Identifier(ManagedTypeDescriptor<D> declaringType, String name, SimpleTypeDescriptor<J> attributeType, Member member, Attribute.PersistentAttributeType attributeNature) {
            super(declaringType, name, attributeNature, attributeType, member, true, false, false);
        }
    }
}

