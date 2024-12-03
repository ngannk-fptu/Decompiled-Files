/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.metamodel.Attribute$PersistentAttributeType
 */
package org.hibernate.metamodel.model.domain.internal;

import java.lang.reflect.Member;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.metamodel.Attribute;
import org.hibernate.mapping.Property;
import org.hibernate.metamodel.model.domain.internal.AbstractPluralAttribute;
import org.hibernate.metamodel.model.domain.internal.BagAttributeImpl;
import org.hibernate.metamodel.model.domain.internal.ListAttributeImpl;
import org.hibernate.metamodel.model.domain.internal.MapAttributeImpl;
import org.hibernate.metamodel.model.domain.internal.SetAttributeImpl;
import org.hibernate.metamodel.model.domain.spi.ManagedTypeDescriptor;
import org.hibernate.metamodel.model.domain.spi.SimpleTypeDescriptor;

public class PluralAttributeBuilder<D, C, E, K> {
    private final ManagedTypeDescriptor<D> declaringType;
    private final SimpleTypeDescriptor<E> valueType;
    private Attribute.PersistentAttributeType attributeNature;
    private Property property;
    private Member member;
    private Class<C> collectionClass;
    private SimpleTypeDescriptor<K> keyType;

    public PluralAttributeBuilder(ManagedTypeDescriptor<D> ownerType, SimpleTypeDescriptor<E> elementType, Class<C> collectionClass, SimpleTypeDescriptor<K> keyType) {
        this.declaringType = ownerType;
        this.valueType = elementType;
        this.collectionClass = collectionClass;
        this.keyType = keyType;
    }

    public ManagedTypeDescriptor<D> getDeclaringType() {
        return this.declaringType;
    }

    public Attribute.PersistentAttributeType getAttributeNature() {
        return this.attributeNature;
    }

    public SimpleTypeDescriptor<K> getKeyType() {
        return this.keyType;
    }

    public Class<C> getCollectionClass() {
        return this.collectionClass;
    }

    public SimpleTypeDescriptor<E> getValueType() {
        return this.valueType;
    }

    public Property getProperty() {
        return this.property;
    }

    public Member getMember() {
        return this.member;
    }

    public PluralAttributeBuilder<D, C, E, K> member(Member member) {
        this.member = member;
        return this;
    }

    public PluralAttributeBuilder<D, C, E, K> property(Property property) {
        this.property = property;
        return this;
    }

    public PluralAttributeBuilder<D, C, E, K> persistentAttributeType(Attribute.PersistentAttributeType attrType) {
        this.attributeNature = attrType;
        return this;
    }

    public AbstractPluralAttribute<D, C, E> build() {
        if (Map.class.equals(this.collectionClass)) {
            PluralAttributeBuilder builder = this;
            return new MapAttributeImpl(builder);
        }
        if (Set.class.equals(this.collectionClass)) {
            PluralAttributeBuilder builder = this;
            return new SetAttributeImpl(builder);
        }
        if (List.class.equals(this.collectionClass)) {
            PluralAttributeBuilder builder = this;
            return new ListAttributeImpl(builder);
        }
        if (Collection.class.equals(this.collectionClass)) {
            PluralAttributeBuilder builder = this;
            return new BagAttributeImpl(builder);
        }
        if (this.collectionClass.isArray()) {
            PluralAttributeBuilder builder = this;
            return new ListAttributeImpl(builder);
        }
        if (Map.class.isAssignableFrom(this.collectionClass)) {
            PluralAttributeBuilder builder = this;
            return new MapAttributeImpl(builder);
        }
        if (Set.class.isAssignableFrom(this.collectionClass)) {
            PluralAttributeBuilder builder = this;
            return new SetAttributeImpl(builder);
        }
        if (List.class.isAssignableFrom(this.collectionClass)) {
            PluralAttributeBuilder builder = this;
            return new ListAttributeImpl(builder);
        }
        if (Collection.class.isAssignableFrom(this.collectionClass)) {
            PluralAttributeBuilder builder = this;
            return new BagAttributeImpl(builder);
        }
        throw new UnsupportedOperationException("Unknown collection: " + this.collectionClass);
    }
}

