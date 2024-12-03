/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.metamodel.Attribute
 *  javax.persistence.metamodel.Bindable
 *  javax.persistence.metamodel.Bindable$BindableType
 *  javax.persistence.metamodel.CollectionAttribute
 *  javax.persistence.metamodel.ListAttribute
 *  javax.persistence.metamodel.MapAttribute
 *  javax.persistence.metamodel.PluralAttribute
 *  javax.persistence.metamodel.PluralAttribute$CollectionType
 *  javax.persistence.metamodel.SetAttribute
 *  javax.persistence.metamodel.SingularAttribute
 */
package org.hibernate.metamodel.model.domain.internal;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import org.hibernate.AssertionFailure;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.graph.internal.SubGraphImpl;
import org.hibernate.graph.spi.SubGraphImplementor;
import org.hibernate.metamodel.model.domain.internal.AbstractType;
import org.hibernate.metamodel.model.domain.spi.BagPersistentAttribute;
import org.hibernate.metamodel.model.domain.spi.DomainModelHelper;
import org.hibernate.metamodel.model.domain.spi.ListPersistentAttribute;
import org.hibernate.metamodel.model.domain.spi.ManagedTypeDescriptor;
import org.hibernate.metamodel.model.domain.spi.MapPersistentAttribute;
import org.hibernate.metamodel.model.domain.spi.PersistentAttributeDescriptor;
import org.hibernate.metamodel.model.domain.spi.PluralPersistentAttribute;
import org.hibernate.metamodel.model.domain.spi.SetPersistentAttribute;
import org.hibernate.metamodel.model.domain.spi.SingularPersistentAttribute;

public abstract class AbstractManagedType<J>
extends AbstractType<J>
implements ManagedTypeDescriptor<J>,
Serializable {
    private final SessionFactoryImplementor sessionFactory;
    private final ManagedTypeDescriptor<? super J> superType;
    private final Map<String, PersistentAttributeDescriptor<J, ?>> declaredAttributes = new HashMap();
    private final Map<String, SingularPersistentAttribute<J, ?>> declaredSingularAttributes = new HashMap();
    private volatile Map<String, PluralPersistentAttribute<J, ?, ?>> declaredPluralAttributes;
    private transient ManagedTypeDescriptor.InFlightAccess<J> inFlightAccess;

    protected AbstractManagedType(Class<J> javaType, String typeName, ManagedTypeDescriptor<? super J> superType, SessionFactoryImplementor sessionFactory) {
        super(javaType, typeName);
        this.superType = superType;
        this.sessionFactory = sessionFactory;
        this.inFlightAccess = this.createInFlightAccess();
    }

    protected ManagedTypeDescriptor.InFlightAccess<J> createInFlightAccess() {
        return new InFlightAccessImpl();
    }

    @Override
    public String getName() {
        return this.getTypeName();
    }

    @Override
    public ManagedTypeDescriptor<? super J> getSuperType() {
        return this.superType;
    }

    protected SessionFactoryImplementor sessionFactory() {
        return this.sessionFactory;
    }

    @Override
    public ManagedTypeDescriptor.InFlightAccess<J> getInFlightAccess() {
        if (this.inFlightAccess == null) {
            throw new IllegalStateException("Type has been locked");
        }
        return this.inFlightAccess;
    }

    public Set<Attribute<? super J, ?>> getAttributes() {
        HashSet attributes = new HashSet(this.declaredAttributes.values());
        if (this.getSuperType() != null) {
            attributes.addAll(this.getSuperType().getAttributes());
        }
        return attributes;
    }

    public Set<Attribute<J, ?>> getDeclaredAttributes() {
        return new HashSet(this.declaredAttributes.values());
    }

    @Override
    public PersistentAttributeDescriptor<? super J, ?> getAttribute(String name) {
        Attribute attribute = this.declaredAttributes.get(name);
        if (attribute == null && this.getSuperType() != null) {
            attribute = this.getSuperType().getAttribute(name);
        }
        this.checkNotNull("Attribute ", attribute, name);
        return attribute;
    }

    @Override
    public PersistentAttributeDescriptor<J, ?> findDeclaredAttribute(String name) {
        return this.declaredAttributes.get(name);
    }

    @Override
    public PersistentAttributeDescriptor<? super J, ?> findAttribute(String name) {
        PersistentAttributeDescriptor<J, ?> attribute = this.findDeclaredAttribute(name);
        if (attribute == null && this.getSuperType() != null) {
            attribute = this.getSuperType().findAttribute(name);
        }
        return attribute;
    }

    @Override
    public PersistentAttributeDescriptor<J, ?> getDeclaredAttribute(String name) {
        PersistentAttributeDescriptor<J, ?> attr = this.declaredAttributes.get(name);
        this.checkNotNull("Attribute ", attr, name);
        return attr;
    }

    private void checkNotNull(String attributeType, Attribute<?, ?> attribute, String name) {
        if (attribute == null) {
            throw new IllegalArgumentException(String.format("Unable to locate %s with the the given name [%s] on this ManagedType [%s]", attributeType, name, this.getTypeName()));
        }
    }

    public Set<SingularAttribute<? super J, ?>> getSingularAttributes() {
        HashSet attributes = new HashSet(this.declaredSingularAttributes.values());
        if (this.getSuperType() != null) {
            attributes.addAll(this.getSuperType().getSingularAttributes());
        }
        return attributes;
    }

    public Set<SingularAttribute<J, ?>> getDeclaredSingularAttributes() {
        return new HashSet(this.declaredSingularAttributes.values());
    }

    @Override
    public SingularAttribute<? super J, ?> getSingularAttribute(String name) {
        SingularAttribute<J, ?> attribute = (SingularAttribute<J, ?>)this.declaredSingularAttributes.get(name);
        if (attribute == null && this.getSuperType() != null) {
            attribute = this.getSuperType().getSingularAttribute(name);
        }
        this.checkNotNull("SingularAttribute ", (Attribute<?, ?>)attribute, name);
        return attribute;
    }

    @Override
    public SingularAttribute<J, ?> getDeclaredSingularAttribute(String name) {
        SingularAttribute attr = this.declaredSingularAttributes.get(name);
        this.checkNotNull("SingularAttribute ", (Attribute<?, ?>)attr, name);
        return attr;
    }

    @Override
    public <Y> SingularPersistentAttribute<? super J, Y> getSingularAttribute(String name, Class<Y> type) {
        SingularAttribute<J, ?> attribute = (SingularAttribute<J, ?>)this.declaredSingularAttributes.get(name);
        if (attribute == null && this.getSuperType() != null) {
            attribute = this.getSuperType().getSingularAttribute(name);
        }
        this.checkTypeForSingleAttribute("SingularAttribute ", attribute, name, type);
        return (SingularPersistentAttribute)attribute;
    }

    @Override
    public <Y> SingularPersistentAttribute<J, Y> getDeclaredSingularAttribute(String name, Class<Y> javaType) {
        SingularAttribute attr = this.declaredSingularAttributes.get(name);
        this.checkTypeForSingleAttribute("SingularAttribute ", attr, name, javaType);
        return (SingularPersistentAttribute)attr;
    }

    private <Y> void checkTypeForSingleAttribute(String attributeType, SingularAttribute<?, ?> attribute, String name, Class<Y> javaType) {
        if (attribute == null || javaType != null && !attribute.getBindableJavaType().equals(javaType)) {
            if (this.isPrimitiveVariant(attribute, javaType)) {
                return;
            }
            throw new IllegalArgumentException(attributeType + " named " + name + (javaType != null ? " and of type " + javaType.getName() : "") + " is not present");
        }
    }

    protected <Y> boolean isPrimitiveVariant(SingularAttribute<?, ?> attribute, Class<Y> javaType) {
        if (attribute == null) {
            return false;
        }
        Class declaredType = attribute.getBindableJavaType();
        if (declaredType.isPrimitive()) {
            return Boolean.class.equals(javaType) && Boolean.TYPE.equals(declaredType) || Character.class.equals(javaType) && Character.TYPE.equals(declaredType) || Byte.class.equals(javaType) && Byte.TYPE.equals(declaredType) || Short.class.equals(javaType) && Short.TYPE.equals(declaredType) || Integer.class.equals(javaType) && Integer.TYPE.equals(declaredType) || Long.class.equals(javaType) && Long.TYPE.equals(declaredType) || Float.class.equals(javaType) && Float.TYPE.equals(declaredType) || Double.class.equals(javaType) && Double.TYPE.equals(declaredType);
        }
        if (javaType.isPrimitive()) {
            return Boolean.class.equals((Object)declaredType) && Boolean.TYPE.equals(javaType) || Character.class.equals((Object)declaredType) && Character.TYPE.equals(javaType) || Byte.class.equals((Object)declaredType) && Byte.TYPE.equals(javaType) || Short.class.equals((Object)declaredType) && Short.TYPE.equals(javaType) || Integer.class.equals((Object)declaredType) && Integer.TYPE.equals(javaType) || Long.class.equals((Object)declaredType) && Long.TYPE.equals(javaType) || Float.class.equals((Object)declaredType) && Float.TYPE.equals(javaType) || Double.class.equals((Object)declaredType) && Double.TYPE.equals(javaType);
        }
        return false;
    }

    public Set<PluralAttribute<? super J, ?, ?>> getPluralAttributes() {
        HashSet attributes;
        HashSet<Object> hashSet = attributes = this.declaredPluralAttributes == null ? new HashSet() : new HashSet(this.declaredPluralAttributes.values());
        if (this.getSuperType() != null) {
            attributes.addAll(this.getSuperType().getPluralAttributes());
        }
        return attributes;
    }

    public Set<PluralAttribute<J, ?, ?>> getDeclaredPluralAttributes() {
        return this.declaredPluralAttributes == null ? Collections.EMPTY_SET : new HashSet(this.declaredPluralAttributes.values());
    }

    @Override
    public CollectionAttribute<? super J, ?> getCollection(String name) {
        PluralPersistentAttribute<J, Object, Object> attribute = this.getPluralAttribute(name);
        if (attribute == null && this.getSuperType() != null) {
            attribute = this.getSuperType().getPluralAttribute(name);
        }
        this.basicCollectionCheck(attribute, name);
        return (CollectionAttribute)attribute;
    }

    @Override
    public PluralPersistentAttribute<? super J, ?, ?> getPluralAttribute(String name) {
        return this.declaredPluralAttributes == null ? null : this.declaredPluralAttributes.get(name);
    }

    private void basicCollectionCheck(PluralAttribute<? super J, ?, ?> attribute, String name) {
        this.checkNotNull("CollectionAttribute", (Attribute<?, ?>)attribute, name);
        if (!CollectionAttribute.class.isAssignableFrom(attribute.getClass())) {
            throw new IllegalArgumentException(name + " is not a CollectionAttribute: " + attribute.getClass());
        }
    }

    @Override
    public CollectionAttribute<J, ?> getDeclaredCollection(String name) {
        PluralPersistentAttribute<J, ?, ?> attribute = this.getPluralAttribute(name);
        this.basicCollectionCheck(attribute, name);
        return (CollectionAttribute)attribute;
    }

    @Override
    public SetPersistentAttribute<? super J, ?> getSet(String name) {
        PluralPersistentAttribute<J, Object, Object> attribute = this.getPluralAttribute(name);
        if (attribute == null && this.getSuperType() != null) {
            attribute = this.getSuperType().getPluralAttribute(name);
        }
        this.basicSetCheck(attribute, name);
        return (SetPersistentAttribute)attribute;
    }

    private void basicSetCheck(PluralAttribute<? super J, ?, ?> attribute, String name) {
        this.checkNotNull("SetAttribute", (Attribute<?, ?>)attribute, name);
        if (!SetAttribute.class.isAssignableFrom(attribute.getClass())) {
            throw new IllegalArgumentException(name + " is not a SetAttribute: " + attribute.getClass());
        }
    }

    @Override
    public SetPersistentAttribute<J, ?> getDeclaredSet(String name) {
        PluralPersistentAttribute<J, ?, ?> attribute = this.getPluralAttribute(name);
        this.basicSetCheck(attribute, name);
        return (SetPersistentAttribute)attribute;
    }

    @Override
    public ListPersistentAttribute<? super J, ?> getList(String name) {
        PluralPersistentAttribute<J, Object, Object> attribute = this.getPluralAttribute(name);
        if (attribute == null && this.getSuperType() != null) {
            attribute = this.getSuperType().getPluralAttribute(name);
        }
        this.basicListCheck(attribute, name);
        return (ListPersistentAttribute)attribute;
    }

    private void basicListCheck(PluralAttribute<? super J, ?, ?> attribute, String name) {
        this.checkNotNull("ListAttribute", (Attribute<?, ?>)attribute, name);
        if (!ListAttribute.class.isAssignableFrom(attribute.getClass())) {
            throw new IllegalArgumentException(name + " is not a ListAttribute: " + attribute.getClass());
        }
    }

    @Override
    public ListPersistentAttribute<J, ?> getDeclaredList(String name) {
        PluralPersistentAttribute<J, ?, ?> attribute = this.getPluralAttribute(name);
        this.basicListCheck(attribute, name);
        return (ListPersistentAttribute)attribute;
    }

    @Override
    public MapPersistentAttribute<? super J, ?, ?> getMap(String name) {
        PluralPersistentAttribute<J, Object, Object> attribute = this.getPluralAttribute(name);
        if (attribute == null && this.getSuperType() != null) {
            attribute = this.getSuperType().getPluralAttribute(name);
        }
        this.basicMapCheck(attribute, name);
        return (MapPersistentAttribute)attribute;
    }

    private void basicMapCheck(PluralAttribute<? super J, ?, ?> attribute, String name) {
        this.checkNotNull("MapAttribute", (Attribute<?, ?>)attribute, name);
        if (!MapAttribute.class.isAssignableFrom(attribute.getClass())) {
            throw new IllegalArgumentException(name + " is not a MapAttribute: " + attribute.getClass());
        }
    }

    @Override
    public MapPersistentAttribute<J, ?, ?> getDeclaredMap(String name) {
        PluralPersistentAttribute<J, ?, ?> attribute = this.getPluralAttribute(name);
        this.basicMapCheck(attribute, name);
        return (MapPersistentAttribute)attribute;
    }

    @Override
    public <E> BagPersistentAttribute<? super J, E> getCollection(String name, Class<E> elementType) {
        PluralPersistentAttribute<J, Object, Object> attribute = this.getPluralAttribute(name);
        if (attribute == null && this.getSuperType() != null) {
            attribute = this.getSuperType().getPluralAttribute(name);
        }
        this.checkCollectionElementType(attribute, name, elementType);
        return (BagPersistentAttribute)attribute;
    }

    @Override
    public <E> CollectionAttribute<J, E> getDeclaredCollection(String name, Class<E> elementType) {
        PluralPersistentAttribute<J, ?, ?> attribute = this.getPluralAttribute(name);
        this.checkCollectionElementType(attribute, name, elementType);
        return (CollectionAttribute)attribute;
    }

    private <E> void checkCollectionElementType(PluralAttribute<?, ?, ?> attribute, String name, Class<E> elementType) {
        this.checkTypeForPluralAttributes("CollectionAttribute", attribute, name, elementType, PluralAttribute.CollectionType.COLLECTION);
    }

    private <E> void checkTypeForPluralAttributes(String attributeType, PluralAttribute<?, ?, ?> attribute, String name, Class<E> elementType, PluralAttribute.CollectionType collectionType) {
        if (attribute == null || elementType != null && !attribute.getBindableJavaType().equals(elementType) || attribute.getCollectionType() != collectionType) {
            throw new IllegalArgumentException(attributeType + " named " + name + (elementType != null ? " and of element type " + elementType : "") + " is not present");
        }
    }

    @Override
    public <E> SetAttribute<? super J, E> getSet(String name, Class<E> elementType) {
        PluralPersistentAttribute<J, Object, Object> attribute = this.getPluralAttribute(name);
        if (attribute == null && this.getSuperType() != null) {
            attribute = this.getSuperType().getPluralAttribute(name);
        }
        this.checkSetElementType(attribute, name, elementType);
        return (SetAttribute)attribute;
    }

    private <E> void checkSetElementType(PluralAttribute<? super J, ?, ?> attribute, String name, Class<E> elementType) {
        this.checkTypeForPluralAttributes("SetAttribute", attribute, name, elementType, PluralAttribute.CollectionType.SET);
    }

    @Override
    public <E> SetAttribute<J, E> getDeclaredSet(String name, Class<E> elementType) {
        PluralPersistentAttribute<J, ?, ?> attribute = this.getPluralAttribute(name);
        this.checkSetElementType(attribute, name, elementType);
        return (SetAttribute)attribute;
    }

    @Override
    public <E> ListAttribute<? super J, E> getList(String name, Class<E> elementType) {
        PluralPersistentAttribute<J, Object, Object> attribute = this.getPluralAttribute(name);
        if (attribute == null && this.getSuperType() != null) {
            attribute = this.getSuperType().getPluralAttribute(name);
        }
        this.checkListElementType(attribute, name, elementType);
        return (ListAttribute)attribute;
    }

    private <E> void checkListElementType(PluralAttribute<? super J, ?, ?> attribute, String name, Class<E> elementType) {
        this.checkTypeForPluralAttributes("ListAttribute", attribute, name, elementType, PluralAttribute.CollectionType.LIST);
    }

    @Override
    public <E> ListAttribute<J, E> getDeclaredList(String name, Class<E> elementType) {
        PluralPersistentAttribute<J, ?, ?> attribute = this.getPluralAttribute(name);
        this.checkListElementType(attribute, name, elementType);
        return (ListAttribute)attribute;
    }

    @Override
    public <K, V> MapAttribute<? super J, K, V> getMap(String name, Class<K> keyType, Class<V> valueType) {
        PluralPersistentAttribute<J, Object, Object> attribute = this.getPluralAttribute(name);
        if (attribute == null && this.getSuperType() != null) {
            attribute = this.getSuperType().getPluralAttribute(name);
        }
        this.checkMapValueType(attribute, name, valueType);
        MapAttribute mapAttribute = (MapAttribute)attribute;
        this.checkMapKeyType(mapAttribute, name, keyType);
        return mapAttribute;
    }

    private <V> void checkMapValueType(PluralAttribute<? super J, ?, ?> attribute, String name, Class<V> valueType) {
        this.checkTypeForPluralAttributes("MapAttribute", attribute, name, valueType, PluralAttribute.CollectionType.MAP);
    }

    private <K, V> void checkMapKeyType(MapAttribute<? super J, K, V> mapAttribute, String name, Class<K> keyType) {
        if (mapAttribute.getKeyJavaType() != keyType) {
            throw new IllegalArgumentException("MapAttribute named " + name + " does not support a key of type " + keyType);
        }
    }

    @Override
    public <K, V> MapAttribute<J, K, V> getDeclaredMap(String name, Class<K> keyType, Class<V> valueType) {
        PluralPersistentAttribute<J, ?, ?> attribute = this.getPluralAttribute(name);
        this.checkMapValueType(attribute, name, valueType);
        MapAttribute mapAttribute = (MapAttribute)attribute;
        this.checkMapKeyType(mapAttribute, name, keyType);
        return mapAttribute;
    }

    @Override
    public SubGraphImplementor<J> makeSubGraph() {
        return new SubGraphImpl(this, true, this.sessionFactory);
    }

    @Override
    public <S extends J> ManagedTypeDescriptor<S> findSubType(String subTypeName) {
        return DomainModelHelper.resolveSubType(this, subTypeName, this.sessionFactory());
    }

    @Override
    public <S extends J> ManagedTypeDescriptor<S> findSubType(Class<S> subType) {
        return DomainModelHelper.resolveSubType(this, subType, this.sessionFactory());
    }

    protected class InFlightAccessImpl
    implements ManagedTypeDescriptor.InFlightAccess<J> {
        protected InFlightAccessImpl() {
        }

        @Override
        public void addAttribute(PersistentAttributeDescriptor<J, ?> attribute) {
            AbstractManagedType.this.declaredAttributes.put(attribute.getName(), attribute);
            Bindable.BindableType bindableType = ((Bindable)attribute).getBindableType();
            switch (bindableType) {
                case SINGULAR_ATTRIBUTE: {
                    AbstractManagedType.this.declaredSingularAttributes.put(attribute.getName(), (SingularPersistentAttribute)attribute);
                    break;
                }
                case PLURAL_ATTRIBUTE: {
                    if (AbstractManagedType.this.declaredPluralAttributes == null) {
                        AbstractManagedType.this.declaredPluralAttributes = new HashMap();
                    }
                    AbstractManagedType.this.declaredPluralAttributes.put(attribute.getName(), (PluralPersistentAttribute)attribute);
                    break;
                }
                default: {
                    throw new AssertionFailure("unknown bindable type: " + bindableType);
                }
            }
        }

        @Override
        public void finishUp() {
            AbstractManagedType.this.inFlightAccess = null;
        }
    }
}

