/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.metamodel.Attribute$PersistentAttributeType
 *  javax.persistence.metamodel.Bindable$BindableType
 */
package org.hibernate.metamodel.model.domain.internal;

import java.io.Serializable;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Bindable;
import org.hibernate.metamodel.model.domain.internal.AbstractAttribute;
import org.hibernate.metamodel.model.domain.internal.AbstractManagedType;
import org.hibernate.metamodel.model.domain.internal.PluralAttributeBuilder;
import org.hibernate.metamodel.model.domain.spi.PluralPersistentAttribute;
import org.hibernate.metamodel.model.domain.spi.SimpleTypeDescriptor;

public abstract class AbstractPluralAttribute<D, C, E>
extends AbstractAttribute<D, C>
implements PluralPersistentAttribute<D, C, E>,
Serializable {
    private final Class<C> collectionClass;

    protected AbstractPluralAttribute(PluralAttributeBuilder<D, C, E, ?> builder) {
        super(builder.getDeclaringType(), builder.getProperty().getName(), builder.getAttributeNature(), builder.getValueType(), builder.getMember());
        this.collectionClass = builder.getCollectionClass();
    }

    public static <X, C, E, K> PluralAttributeBuilder<X, C, E, K> create(AbstractManagedType<X> ownerType, SimpleTypeDescriptor<E> attrType, Class<C> collectionClass, SimpleTypeDescriptor<K> keyType) {
        return new PluralAttributeBuilder<X, C, E, K>(ownerType, attrType, collectionClass, keyType);
    }

    @Override
    public SimpleTypeDescriptor<E> getElementType() {
        return this.getValueGraphType();
    }

    @Override
    public SimpleTypeDescriptor<E> getValueGraphType() {
        return super.getValueGraphType();
    }

    @Override
    public SimpleTypeDescriptor<?> getKeyGraphType() {
        return null;
    }

    public boolean isAssociation() {
        return this.getPersistentAttributeType() == Attribute.PersistentAttributeType.ONE_TO_MANY || this.getPersistentAttributeType() == Attribute.PersistentAttributeType.MANY_TO_MANY;
    }

    public boolean isCollection() {
        return true;
    }

    public Bindable.BindableType getBindableType() {
        return Bindable.BindableType.PLURAL_ATTRIBUTE;
    }

    public Class<E> getBindableJavaType() {
        return this.getElementType().getJavaType();
    }

    public Class<C> getJavaType() {
        return this.collectionClass;
    }
}

