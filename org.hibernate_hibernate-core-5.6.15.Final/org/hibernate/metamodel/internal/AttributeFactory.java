/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.ManyToMany
 *  javax.persistence.OneToOne
 *  javax.persistence.metamodel.Attribute$PersistentAttributeType
 *  javax.persistence.metamodel.PluralAttribute$CollectionType
 *  javax.persistence.metamodel.Type$PersistenceType
 */
package org.hibernate.metamodel.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.Type;
import org.hibernate.AssertionFailure;
import org.hibernate.internal.EntityManagerMessageLogger;
import org.hibernate.internal.HEMLogging;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.Map;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Value;
import org.hibernate.metamodel.internal.MetadataContext;
import org.hibernate.metamodel.model.domain.internal.AbstractIdentifiableType;
import org.hibernate.metamodel.model.domain.internal.BasicTypeImpl;
import org.hibernate.metamodel.model.domain.internal.EmbeddableTypeImpl;
import org.hibernate.metamodel.model.domain.internal.MapMember;
import org.hibernate.metamodel.model.domain.internal.MappedSuperclassTypeImpl;
import org.hibernate.metamodel.model.domain.internal.PluralAttributeBuilder;
import org.hibernate.metamodel.model.domain.internal.SingularAttributeImpl;
import org.hibernate.metamodel.model.domain.spi.EmbeddedTypeDescriptor;
import org.hibernate.metamodel.model.domain.spi.IdentifiableTypeDescriptor;
import org.hibernate.metamodel.model.domain.spi.ManagedTypeDescriptor;
import org.hibernate.metamodel.model.domain.spi.PersistentAttributeDescriptor;
import org.hibernate.metamodel.model.domain.spi.SimpleTypeDescriptor;
import org.hibernate.metamodel.model.domain.spi.SingularPersistentAttribute;
import org.hibernate.property.access.internal.PropertyAccessMapImpl;
import org.hibernate.property.access.spi.Getter;
import org.hibernate.tuple.entity.EntityMetamodel;
import org.hibernate.type.ComponentType;
import org.hibernate.type.CompositeType;
import org.hibernate.type.EmbeddedComponentType;
import org.hibernate.type.EntityType;
import org.hibernate.type.Type;

public class AttributeFactory {
    private static final EntityManagerMessageLogger LOG = HEMLogging.messageLogger(AttributeFactory.class);
    private final MetadataContext context;
    private final MemberResolver embeddedMemberResolver = new MemberResolver(){

        @Override
        public Member resolveMember(AttributeContext attributeContext) {
            EmbeddedTypeDescriptor embeddableType = (EmbeddedTypeDescriptor)attributeContext.getOwnerType();
            String attributeName = attributeContext.getPropertyMapping().getName();
            Getter getter = ((ComponentType)embeddableType.getHibernateType()).getComponentTuplizer().getGetter(embeddableType.getHibernateType().getPropertyIndex(attributeName));
            return PropertyAccessMapImpl.GetterImpl.class.isInstance(getter) ? new MapMember(attributeName, attributeContext.getPropertyMapping().getType().getReturnedClass()) : getter.getMember();
        }
    };
    private final MemberResolver virtualIdentifierMemberResolver = new MemberResolver(){

        @Override
        public Member resolveMember(AttributeContext attributeContext) {
            AbstractIdentifiableType identifiableType = (AbstractIdentifiableType)attributeContext.getOwnerType();
            EntityMetamodel entityMetamodel = AttributeFactory.this.getDeclarerEntityMetamodel(identifiableType);
            if (!entityMetamodel.getIdentifierProperty().isVirtual()) {
                throw new IllegalArgumentException("expecting IdClass mapping");
            }
            Type type = entityMetamodel.getIdentifierProperty().getType();
            if (!EmbeddedComponentType.class.isInstance(type)) {
                throw new IllegalArgumentException("expecting IdClass mapping");
            }
            EmbeddedComponentType componentType = (EmbeddedComponentType)type;
            String attributeName = attributeContext.getPropertyMapping().getName();
            Getter getter = componentType.getComponentTuplizer().getGetter(componentType.getPropertyIndex(attributeName));
            return PropertyAccessMapImpl.GetterImpl.class.isInstance(getter) ? new MapMember(attributeName, attributeContext.getPropertyMapping().getType().getReturnedClass()) : getter.getMember();
        }
    };
    private final MemberResolver normalMemberResolver = new MemberResolver(){

        @Override
        public Member resolveMember(AttributeContext attributeContext) {
            ManagedTypeDescriptor ownerType = attributeContext.getOwnerType();
            Property property = attributeContext.getPropertyMapping();
            Type.PersistenceType persistenceType = ownerType.getPersistenceType();
            if (Type.PersistenceType.EMBEDDABLE == persistenceType) {
                return AttributeFactory.this.embeddedMemberResolver.resolveMember(attributeContext);
            }
            if (Type.PersistenceType.ENTITY == persistenceType || Type.PersistenceType.MAPPED_SUPERCLASS == persistenceType) {
                String propertyName;
                AbstractIdentifiableType identifiableType = (AbstractIdentifiableType)ownerType;
                EntityMetamodel entityMetamodel = AttributeFactory.this.getDeclarerEntityMetamodel(identifiableType);
                Integer index = entityMetamodel.getPropertyIndexOrNull(propertyName = property.getName());
                if (index == null) {
                    return AttributeFactory.this.virtualIdentifierMemberResolver.resolveMember(attributeContext);
                }
                Getter getter = entityMetamodel.getTuplizer().getGetter(index);
                return PropertyAccessMapImpl.GetterImpl.class.isInstance(getter) ? new MapMember(propertyName, property.getType().getReturnedClass()) : getter.getMember();
            }
            throw new IllegalArgumentException("Unexpected owner type : " + persistenceType);
        }
    };
    private final MemberResolver identifierMemberResolver = new MemberResolver(){

        @Override
        public Member resolveMember(AttributeContext attributeContext) {
            AbstractIdentifiableType identifiableType = (AbstractIdentifiableType)attributeContext.getOwnerType();
            EntityMetamodel entityMetamodel = AttributeFactory.this.getDeclarerEntityMetamodel(identifiableType);
            if (!attributeContext.getPropertyMapping().getName().equals(entityMetamodel.getIdentifierProperty().getName())) {
                return AttributeFactory.this.virtualIdentifierMemberResolver.resolveMember(attributeContext);
            }
            Getter getter = entityMetamodel.getTuplizer().getIdentifierGetter();
            if (PropertyAccessMapImpl.GetterImpl.class.isInstance(getter)) {
                return new MapMember(entityMetamodel.getIdentifierProperty().getName(), entityMetamodel.getIdentifierProperty().getType().getReturnedClass());
            }
            return getter.getMember();
        }
    };
    private final MemberResolver versionMemberResolver = new MemberResolver(){

        @Override
        public Member resolveMember(AttributeContext attributeContext) {
            AbstractIdentifiableType identifiableType = (AbstractIdentifiableType)attributeContext.getOwnerType();
            EntityMetamodel entityMetamodel = AttributeFactory.this.getDeclarerEntityMetamodel(identifiableType);
            String versionPropertyName = attributeContext.getPropertyMapping().getName();
            if (!versionPropertyName.equals(entityMetamodel.getVersionProperty().getName())) {
                throw new IllegalArgumentException("Given property did not match declared version property");
            }
            Getter getter = entityMetamodel.getTuplizer().getVersionGetter();
            if (PropertyAccessMapImpl.GetterImpl.class.isInstance(getter)) {
                return new MapMember(versionPropertyName, attributeContext.getPropertyMapping().getType().getReturnedClass());
            }
            return getter.getMember();
        }
    };

    public AttributeFactory(MetadataContext context) {
        this.context = context;
    }

    public <X, Y> PersistentAttributeDescriptor<X, Y> buildAttribute(ManagedTypeDescriptor<X> ownerType, Property property) {
        if (property.isSynthetic()) {
            LOG.tracef("Skipping synthetic property %s(%s)", ownerType.getName(), property.getName());
            return null;
        }
        LOG.trace("Building attribute [" + ownerType.getName() + "." + property.getName() + "]");
        AttributeContext<X> attributeContext = this.wrap(ownerType, property);
        AttributeMetadata<X, Y> attributeMetadata = this.determineAttributeMetadata(attributeContext, this.normalMemberResolver);
        if (attributeMetadata == null) {
            return null;
        }
        if (attributeMetadata.isPlural()) {
            return this.buildPluralAttribute((PluralAttributeMetadata)attributeMetadata);
        }
        SingularAttributeMetadata singularAttributeMetadata = (SingularAttributeMetadata)attributeMetadata;
        SimpleTypeDescriptor<Y> metaModelType = this.determineSimpleType(singularAttributeMetadata.getValueContext());
        Attribute.PersistentAttributeType jpaAttributeNature = attributeMetadata.getJpaAttributeNature();
        if (attributeContext.getPropertyMapping().getType().isComponentType() && jpaAttributeNature.equals((Object)Attribute.PersistentAttributeType.BASIC)) {
            CompositeType compositeType = (CompositeType)attributeContext.getPropertyMapping().getType();
            metaModelType = this.context.locateEmbeddable(attributeMetadata.getJavaType(), compositeType);
            jpaAttributeNature = Attribute.PersistentAttributeType.EMBEDDED;
            if (metaModelType == null && (metaModelType = this.context.locateEmbeddable(attributeMetadata.getJavaType(), compositeType)) == null) {
                EmbeddableTypeImpl<Y> embeddableType = new EmbeddableTypeImpl<Y>(attributeMetadata.getJavaType(), ownerType, compositeType, this.context.getSessionFactory());
                this.context.registerEmbeddableType(embeddableType, compositeType);
                String[] propertyNames = compositeType.getPropertyNames();
                Type[] subtypes = compositeType.getSubtypes();
                ManagedTypeDescriptor.InFlightAccess<Y> inFlightAccess = embeddableType.getInFlightAccess();
                for (int i = 0; i < propertyNames.length; ++i) {
                    SingularAttributeImpl nestedAttribute = new SingularAttributeImpl(embeddableType, propertyNames[i], Attribute.PersistentAttributeType.BASIC, new BasicTypeImpl(subtypes[i].getReturnedClass(), Type.PersistenceType.BASIC), null, false, false, property.isOptional());
                    inFlightAccess.addAttribute(nestedAttribute);
                }
                metaModelType = embeddableType;
            }
        }
        return new SingularAttributeImpl<X, Y>(ownerType, attributeMetadata.getName(), jpaAttributeNature, metaModelType, attributeMetadata.getMember(), false, false, property.isOptional());
    }

    private <X> AttributeContext<X> wrap(final ManagedTypeDescriptor<X> ownerType, final Property property) {
        return new AttributeContext<X>(){

            @Override
            public ManagedTypeDescriptor<X> getOwnerType() {
                return ownerType;
            }

            @Override
            public Property getPropertyMapping() {
                return property;
            }
        };
    }

    public <X, Y> SingularPersistentAttribute<X, Y> buildIdAttribute(IdentifiableTypeDescriptor<X> ownerType, Property property) {
        LOG.trace("Building identifier attribute [" + ownerType.getName() + "." + property.getName() + "]");
        SingularAttributeMetadata attributeMetadata = (SingularAttributeMetadata)this.determineAttributeMetadata(this.wrap(ownerType, property), this.identifierMemberResolver);
        return new SingularAttributeImpl.Identifier<X, Y>(ownerType, property.getName(), this.determineSimpleType(attributeMetadata.getValueContext()), attributeMetadata.getMember(), attributeMetadata.getJpaAttributeNature());
    }

    public <X, Y> SingularAttributeImpl<X, Y> buildVersionAttribute(IdentifiableTypeDescriptor<X> ownerType, Property property) {
        LOG.trace("Building version attribute [ownerType.getTypeName().property.getName()]");
        SingularAttributeMetadata attributeMetadata = (SingularAttributeMetadata)this.determineAttributeMetadata(this.wrap(ownerType, property), this.versionMemberResolver);
        return new SingularAttributeImpl.Version<X, Y>(ownerType, property.getName(), attributeMetadata.getJpaAttributeNature(), this.determineSimpleType(attributeMetadata.getValueContext()), attributeMetadata.getMember());
    }

    private <X, Y, E, K> PersistentAttributeDescriptor<X, Y> buildPluralAttribute(PluralAttributeMetadata<X, Y, E> attributeMetadata) {
        PluralAttributeBuilder info = new PluralAttributeBuilder(attributeMetadata.getOwnerType(), this.determineSimpleType(attributeMetadata.getElementValueContext()), attributeMetadata.getJavaType(), java.util.Map.class.isAssignableFrom(attributeMetadata.getJavaType()) ? this.determineSimpleType(attributeMetadata.getMapKeyValueContext()) : null);
        return info.member(attributeMetadata.getMember()).property(attributeMetadata.getPropertyMapping()).persistentAttributeType(attributeMetadata.getJpaAttributeNature()).build();
    }

    private <Y> SimpleTypeDescriptor<Y> determineSimpleType(ValueContext typeContext) {
        switch (typeContext.getValueClassification()) {
            case BASIC: {
                return new BasicTypeImpl(typeContext.getJpaBindableType(), Type.PersistenceType.BASIC);
            }
            case ENTITY: {
                EntityType type = (EntityType)typeContext.getHibernateValue().getType();
                return this.context.locateEntityType(type.getAssociatedEntityName());
            }
            case EMBEDDABLE: {
                Class javaType;
                Component component = (Component)typeContext.getHibernateValue();
                CompositeType compositeType = (CompositeType)component.getType();
                if (component.getComponentClassName() == null) {
                    javaType = typeContext.getJpaBindableType();
                } else {
                    javaType = component.getComponentClass();
                    EmbeddedTypeDescriptor cached = this.context.locateEmbeddable(javaType, compositeType);
                    if (cached != null) {
                        return cached;
                    }
                }
                EmbeddableTypeImpl embeddableType = new EmbeddableTypeImpl(javaType, typeContext.getAttributeMetadata().getOwnerType(), (ComponentType)typeContext.getHibernateValue().getType(), this.context.getSessionFactory());
                this.context.registerEmbeddableType(embeddableType, compositeType);
                ManagedTypeDescriptor.InFlightAccess inFlightAccess = embeddableType.getInFlightAccess();
                Iterator subProperties = component.getPropertyIterator();
                while (subProperties.hasNext()) {
                    Property property = (Property)subProperties.next();
                    PersistentAttributeDescriptor attribute = this.buildAttribute(embeddableType, property);
                    if (attribute == null) continue;
                    inFlightAccess.addAttribute(attribute);
                }
                inFlightAccess.finishUp();
                return embeddableType;
            }
        }
        throw new AssertionFailure("Unknown type : " + (Object)((Object)typeContext.getValueClassification()));
    }

    private EntityMetamodel getDeclarerEntityMetamodel(AbstractIdentifiableType<?> ownerType) {
        Type.PersistenceType persistenceType = ownerType.getPersistenceType();
        if (persistenceType == Type.PersistenceType.ENTITY) {
            return this.context.getSessionFactory().getMetamodel().entityPersister(ownerType.getTypeName()).getEntityMetamodel();
        }
        if (persistenceType == Type.PersistenceType.MAPPED_SUPERCLASS) {
            PersistentClass persistentClass = this.context.getPersistentClassHostingProperties((MappedSuperclassTypeImpl)ownerType);
            return this.context.getSessionFactory().getMetamodel().entityPersister(persistentClass.getClassName()).getEntityMetamodel();
        }
        throw new AssertionFailure("Cannot get the metamodel for PersistenceType: " + persistenceType);
    }

    private <X, Y> AttributeMetadata<X, Y> determineAttributeMetadata(AttributeContext<X> attributeContext, MemberResolver memberResolver) {
        LOG.trace("Starting attribute metadata determination [" + attributeContext.getPropertyMapping().getName() + "]");
        Member member = memberResolver.resolveMember(attributeContext);
        LOG.trace("    Determined member [" + member + "]");
        Value value = attributeContext.getPropertyMapping().getValue();
        Type type = value.getType();
        LOG.trace("    Determined type [name=" + type.getName() + ", class=" + type.getClass().getName() + "]");
        if (type.isAnyType()) {
            if (this.context.isIgnoreUnsupported()) {
                return null;
            }
            throw new UnsupportedOperationException("ANY not supported");
        }
        if (type.isAssociationType()) {
            if (type.isEntityType()) {
                return new SingularAttributeMetadataImpl(attributeContext.getPropertyMapping(), attributeContext.getOwnerType(), member, AttributeFactory.determineSingularAssociationAttributeType(member));
            }
            if (value instanceof org.hibernate.mapping.Collection) {
                Attribute.PersistentAttributeType keyPersistentAttributeType;
                Attribute.PersistentAttributeType persistentAttributeType;
                Attribute.PersistentAttributeType elementPersistentAttributeType;
                org.hibernate.mapping.Collection collValue = (org.hibernate.mapping.Collection)value;
                Value elementValue = collValue.getElement();
                Type elementType = elementValue.getType();
                if (elementType.isAnyType()) {
                    if (this.context.isIgnoreUnsupported()) {
                        return null;
                    }
                    throw new UnsupportedOperationException("collection of any not supported yet");
                }
                boolean isManyToMany = AttributeFactory.isManyToMany(member);
                if (elementValue instanceof Component) {
                    elementPersistentAttributeType = Attribute.PersistentAttributeType.EMBEDDED;
                    persistentAttributeType = Attribute.PersistentAttributeType.ELEMENT_COLLECTION;
                } else if (elementType.isAssociationType()) {
                    persistentAttributeType = elementPersistentAttributeType = isManyToMany ? Attribute.PersistentAttributeType.MANY_TO_MANY : Attribute.PersistentAttributeType.ONE_TO_MANY;
                } else {
                    elementPersistentAttributeType = Attribute.PersistentAttributeType.BASIC;
                    persistentAttributeType = Attribute.PersistentAttributeType.ELEMENT_COLLECTION;
                }
                if (value instanceof Map) {
                    Value keyValue = ((Map)value).getIndex();
                    Type keyType = keyValue.getType();
                    if (keyType.isAnyType()) {
                        if (this.context.isIgnoreUnsupported()) {
                            return null;
                        }
                        throw new UnsupportedOperationException("collection of any not supported yet");
                    }
                    keyPersistentAttributeType = keyValue instanceof Component ? Attribute.PersistentAttributeType.EMBEDDED : (keyType.isAssociationType() ? Attribute.PersistentAttributeType.MANY_TO_ONE : Attribute.PersistentAttributeType.BASIC);
                } else {
                    keyPersistentAttributeType = null;
                }
                return new PluralAttributeMetadataImpl(attributeContext.getPropertyMapping(), attributeContext.getOwnerType(), member, persistentAttributeType, elementPersistentAttributeType, keyPersistentAttributeType);
            }
            if (value instanceof OneToMany) {
                throw new IllegalArgumentException("HUH???");
            }
        } else {
            if (attributeContext.getPropertyMapping().isComposite()) {
                return new SingularAttributeMetadataImpl(attributeContext.getPropertyMapping(), attributeContext.getOwnerType(), member, Attribute.PersistentAttributeType.EMBEDDED);
            }
            return new SingularAttributeMetadataImpl(attributeContext.getPropertyMapping(), attributeContext.getOwnerType(), member, Attribute.PersistentAttributeType.BASIC);
        }
        throw new UnsupportedOperationException("oops, we are missing something: " + attributeContext.getPropertyMapping());
    }

    public static Attribute.PersistentAttributeType determineSingularAssociationAttributeType(Member member) {
        if (Field.class.isInstance(member)) {
            return ((Field)member).getAnnotation(OneToOne.class) != null ? Attribute.PersistentAttributeType.ONE_TO_ONE : Attribute.PersistentAttributeType.MANY_TO_ONE;
        }
        if (MapMember.class.isInstance(member)) {
            return Attribute.PersistentAttributeType.MANY_TO_ONE;
        }
        return ((Method)member).getAnnotation(OneToOne.class) != null ? Attribute.PersistentAttributeType.ONE_TO_ONE : Attribute.PersistentAttributeType.MANY_TO_ONE;
    }

    protected <Y> Class<Y> accountForPrimitiveTypes(Class<Y> declaredType) {
        return declaredType;
    }

    public static ParameterizedType getSignatureType(Member member) {
        java.lang.reflect.Type type = Field.class.isInstance(member) ? ((Field)member).getGenericType() : (Method.class.isInstance(member) ? ((Method)member).getGenericReturnType() : ((MapMember)member).getType());
        if (type instanceof Class) {
            return null;
        }
        return (ParameterizedType)type;
    }

    public static PluralAttribute.CollectionType determineCollectionType(Class javaType) {
        if (List.class.isAssignableFrom(javaType)) {
            return PluralAttribute.CollectionType.LIST;
        }
        if (Set.class.isAssignableFrom(javaType)) {
            return PluralAttribute.CollectionType.SET;
        }
        if (java.util.Map.class.isAssignableFrom(javaType)) {
            return PluralAttribute.CollectionType.MAP;
        }
        if (Collection.class.isAssignableFrom(javaType)) {
            return PluralAttribute.CollectionType.COLLECTION;
        }
        if (javaType.isArray()) {
            return PluralAttribute.CollectionType.LIST;
        }
        throw new IllegalArgumentException("Expecting collection type [" + javaType.getName() + "]");
    }

    public static boolean isManyToMany(Member member) {
        if (Field.class.isInstance(member)) {
            return ((Field)member).getAnnotation(ManyToMany.class) != null;
        }
        if (Method.class.isInstance(member)) {
            return ((Method)member).getAnnotation(ManyToMany.class) != null;
        }
        return false;
    }

    private class PluralAttributeMetadataImpl<X, Y, E>
    extends BaseAttributeMetadata<X, Y>
    implements PluralAttributeMetadata<X, Y, E> {
        private final PluralAttribute.CollectionType attributeCollectionType;
        private final Attribute.PersistentAttributeType elementPersistentAttributeType;
        private final Attribute.PersistentAttributeType keyPersistentAttributeType;
        private final Class elementJavaType;
        private final Class keyJavaType;
        private final ValueContext elementValueContext;
        private final ValueContext keyValueContext;

        private PluralAttributeMetadataImpl(Property propertyMapping, ManagedTypeDescriptor<X> ownerType, Member member, Attribute.PersistentAttributeType persistentAttributeType, Attribute.PersistentAttributeType elementPersistentAttributeType, Attribute.PersistentAttributeType keyPersistentAttributeType) {
            super(propertyMapping, ownerType, member, persistentAttributeType);
            this.attributeCollectionType = AttributeFactory.determineCollectionType(this.getJavaType());
            this.elementPersistentAttributeType = elementPersistentAttributeType;
            this.keyPersistentAttributeType = keyPersistentAttributeType;
            ParameterizedType signatureType = AttributeFactory.getSignatureType(member);
            if (keyPersistentAttributeType == null) {
                this.elementJavaType = signatureType != null ? this.getClassFromGenericArgument(signatureType.getActualTypeArguments()[0]) : Object.class;
                this.keyJavaType = null;
            } else {
                this.keyJavaType = signatureType != null ? this.getClassFromGenericArgument(signatureType.getActualTypeArguments()[0]) : Object.class;
                this.elementJavaType = signatureType != null ? this.getClassFromGenericArgument(signatureType.getActualTypeArguments()[1]) : Object.class;
            }
            this.elementValueContext = new ValueContext(){

                @Override
                public Value getHibernateValue() {
                    return ((org.hibernate.mapping.Collection)PluralAttributeMetadataImpl.this.getPropertyMapping().getValue()).getElement();
                }

                @Override
                public Class getJpaBindableType() {
                    return PluralAttributeMetadataImpl.this.elementJavaType;
                }

                @Override
                public ValueContext.ValueClassification getValueClassification() {
                    switch (PluralAttributeMetadataImpl.this.elementPersistentAttributeType) {
                        case EMBEDDED: {
                            return ValueContext.ValueClassification.EMBEDDABLE;
                        }
                        case BASIC: {
                            return ValueContext.ValueClassification.BASIC;
                        }
                    }
                    return ValueContext.ValueClassification.ENTITY;
                }

                @Override
                public AttributeMetadata getAttributeMetadata() {
                    return PluralAttributeMetadataImpl.this;
                }
            };
            this.keyValueContext = keyPersistentAttributeType != null ? new ValueContext(){

                @Override
                public Value getHibernateValue() {
                    return ((Map)PluralAttributeMetadataImpl.this.getPropertyMapping().getValue()).getIndex();
                }

                @Override
                public Class getJpaBindableType() {
                    return PluralAttributeMetadataImpl.this.keyJavaType;
                }

                @Override
                public ValueContext.ValueClassification getValueClassification() {
                    switch (PluralAttributeMetadataImpl.this.keyPersistentAttributeType) {
                        case EMBEDDED: {
                            return ValueContext.ValueClassification.EMBEDDABLE;
                        }
                        case BASIC: {
                            return ValueContext.ValueClassification.BASIC;
                        }
                    }
                    return ValueContext.ValueClassification.ENTITY;
                }

                @Override
                public AttributeMetadata getAttributeMetadata() {
                    return PluralAttributeMetadataImpl.this;
                }
            } : null;
        }

        private Class<?> getClassFromGenericArgument(java.lang.reflect.Type type) {
            if (type instanceof Class) {
                return (Class)type;
            }
            if (type instanceof TypeVariable) {
                java.lang.reflect.Type upperBound = ((TypeVariable)type).getBounds()[0];
                return this.getClassFromGenericArgument(upperBound);
            }
            if (type instanceof ParameterizedType) {
                java.lang.reflect.Type rawType = ((ParameterizedType)type).getRawType();
                return this.getClassFromGenericArgument(rawType);
            }
            if (type instanceof WildcardType) {
                java.lang.reflect.Type upperBound = ((WildcardType)type).getUpperBounds()[0];
                return this.getClassFromGenericArgument(upperBound);
            }
            throw new AssertionFailure("Fail to process type argument in a generic declaration. Member : " + this.getMemberDescription() + " Type: " + type.getClass());
        }

        @Override
        public ValueContext getElementValueContext() {
            return this.elementValueContext;
        }

        @Override
        public PluralAttribute.CollectionType getAttributeCollectionType() {
            return this.attributeCollectionType;
        }

        @Override
        public ValueContext getMapKeyValueContext() {
            return this.keyValueContext;
        }
    }

    private class SingularAttributeMetadataImpl<X, Y>
    extends BaseAttributeMetadata<X, Y>
    implements SingularAttributeMetadata<X, Y> {
        private final ValueContext valueContext;

        private SingularAttributeMetadataImpl(Property propertyMapping, ManagedTypeDescriptor<X> ownerType, Member member, Attribute.PersistentAttributeType persistentAttributeType) {
            super(propertyMapping, ownerType, member, persistentAttributeType);
            this.valueContext = new ValueContext(){

                @Override
                public Value getHibernateValue() {
                    return SingularAttributeMetadataImpl.this.getPropertyMapping().getValue();
                }

                @Override
                public Class getJpaBindableType() {
                    return this.getAttributeMetadata().getJavaType();
                }

                @Override
                public ValueContext.ValueClassification getValueClassification() {
                    switch (SingularAttributeMetadataImpl.this.getJpaAttributeNature()) {
                        case EMBEDDED: {
                            return ValueContext.ValueClassification.EMBEDDABLE;
                        }
                        case BASIC: {
                            return ValueContext.ValueClassification.BASIC;
                        }
                    }
                    return ValueContext.ValueClassification.ENTITY;
                }

                @Override
                public AttributeMetadata getAttributeMetadata() {
                    return SingularAttributeMetadataImpl.this;
                }
            };
        }

        @Override
        public ValueContext getValueContext() {
            return this.valueContext;
        }
    }

    private abstract class BaseAttributeMetadata<X, Y>
    implements AttributeMetadata<X, Y> {
        private final Property propertyMapping;
        private final ManagedTypeDescriptor<X> ownerType;
        private final Member member;
        private final Class<Y> javaType;
        private final Attribute.PersistentAttributeType persistentAttributeType;

        protected BaseAttributeMetadata(Property propertyMapping, ManagedTypeDescriptor<X> ownerType, Member member, Attribute.PersistentAttributeType persistentAttributeType) {
            Class<?> declaredType;
            this.propertyMapping = propertyMapping;
            this.ownerType = ownerType;
            this.member = member;
            this.persistentAttributeType = persistentAttributeType;
            if (member == null) {
                declaredType = propertyMapping.getType().getReturnedClass();
            } else if (Field.class.isInstance(member)) {
                declaredType = ((Field)member).getType();
            } else if (Method.class.isInstance(member)) {
                declaredType = ((Method)member).getReturnType();
            } else if (MapMember.class.isInstance(member)) {
                declaredType = ((MapMember)member).getType();
            } else {
                throw new IllegalArgumentException("Cannot determine java-type from given member [" + member + "]");
            }
            this.javaType = AttributeFactory.this.accountForPrimitiveTypes(declaredType);
        }

        @Override
        public String getName() {
            return this.propertyMapping.getName();
        }

        @Override
        public Member getMember() {
            return this.member;
        }

        public String getMemberDescription() {
            return this.determineMemberDescription(this.getMember());
        }

        public String determineMemberDescription(Member member) {
            return member.getDeclaringClass().getName() + '#' + member.getName();
        }

        @Override
        public Class<Y> getJavaType() {
            return this.javaType;
        }

        @Override
        public Attribute.PersistentAttributeType getJpaAttributeNature() {
            return this.persistentAttributeType;
        }

        @Override
        public ManagedTypeDescriptor<X> getOwnerType() {
            return this.ownerType;
        }

        @Override
        public boolean isPlural() {
            return this.propertyMapping.getType().isCollectionType();
        }

        @Override
        public Property getPropertyMapping() {
            return this.propertyMapping;
        }
    }

    private static interface MemberResolver {
        public Member resolveMember(AttributeContext var1);
    }

    private static interface AttributeContext<X> {
        public ManagedTypeDescriptor<X> getOwnerType();

        public Property getPropertyMapping();
    }

    private static interface PluralAttributeMetadata<X, Y, E>
    extends AttributeMetadata<X, Y> {
        public PluralAttribute.CollectionType getAttributeCollectionType();

        public ValueContext getElementValueContext();

        public ValueContext getMapKeyValueContext();
    }

    private static interface SingularAttributeMetadata<X, Y>
    extends AttributeMetadata<X, Y> {
        public ValueContext getValueContext();
    }

    private static interface AttributeMetadata<X, Y> {
        public String getName();

        public Member getMember();

        public Class<Y> getJavaType();

        public Attribute.PersistentAttributeType getJpaAttributeNature();

        public ManagedTypeDescriptor<X> getOwnerType();

        public Property getPropertyMapping();

        public boolean isPlural();
    }

    private static interface ValueContext {
        public ValueClassification getValueClassification();

        public Value getHibernateValue();

        public Class getJpaBindableType();

        public AttributeMetadata getAttributeMetadata();

        public static enum ValueClassification {
            EMBEDDABLE,
            ENTITY,
            BASIC;

        }
    }
}

