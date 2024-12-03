/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.Comparator;
import java.util.Map;
import java.util.Properties;
import org.hibernate.MappingException;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.classic.Lifecycle;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.tuple.component.ComponentMetamodel;
import org.hibernate.type.AnyType;
import org.hibernate.type.ArrayType;
import org.hibernate.type.BagType;
import org.hibernate.type.CollectionType;
import org.hibernate.type.ComponentType;
import org.hibernate.type.CompositeCustomType;
import org.hibernate.type.CustomCollectionType;
import org.hibernate.type.CustomType;
import org.hibernate.type.EmbeddedComponentType;
import org.hibernate.type.EntityType;
import org.hibernate.type.ForeignKeyDirection;
import org.hibernate.type.IdentifierBagType;
import org.hibernate.type.ListType;
import org.hibernate.type.ManyToOneType;
import org.hibernate.type.MapType;
import org.hibernate.type.OneToOneType;
import org.hibernate.type.OrderedMapType;
import org.hibernate.type.OrderedSetType;
import org.hibernate.type.SerializableType;
import org.hibernate.type.SetType;
import org.hibernate.type.SortedMapType;
import org.hibernate.type.SortedSetType;
import org.hibernate.type.SpecialOneToOneType;
import org.hibernate.type.Type;
import org.hibernate.type.spi.TypeBootstrapContext;
import org.hibernate.type.spi.TypeConfiguration;
import org.hibernate.type.spi.TypeConfigurationAware;
import org.hibernate.usertype.CompositeUserType;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;

@Deprecated
public final class TypeFactory
implements Serializable,
TypeBootstrapContext {
    private final TypeConfiguration typeConfiguration;
    private final TypeScope typeScope;
    private static final Properties EMPTY_PROPERTIES = new Properties();

    public TypeFactory(TypeConfiguration typeConfiguration) {
        this.typeConfiguration = typeConfiguration;
        this.typeScope = () -> typeConfiguration;
    }

    @Override
    public Map<String, Object> getConfigurationSettings() {
        return this.typeConfiguration.getServiceRegistry().getService(ConfigurationService.class).getSettings();
    }

    public SessionFactoryImplementor resolveSessionFactory() {
        return this.typeConfiguration.getSessionFactory();
    }

    public Type byClass(Class clazz, Properties parameters) {
        if (Type.class.isAssignableFrom(clazz)) {
            return this.type(clazz, parameters);
        }
        if (CompositeUserType.class.isAssignableFrom(clazz)) {
            return this.customComponent(clazz, parameters);
        }
        if (UserType.class.isAssignableFrom(clazz)) {
            return this.custom(clazz, parameters);
        }
        if (Lifecycle.class.isAssignableFrom(clazz)) {
            return this.manyToOne(clazz.getName());
        }
        if (Serializable.class.isAssignableFrom(clazz)) {
            return TypeFactory.serializable(clazz);
        }
        return null;
    }

    public Type type(Class<Type> typeClass, Properties parameters) {
        try {
            Constructor<Type> bootstrapContextAwareTypeConstructor = ReflectHelper.getConstructor(typeClass, TypeBootstrapContext.class);
            Type type = bootstrapContextAwareTypeConstructor != null ? bootstrapContextAwareTypeConstructor.newInstance(this) : typeClass.newInstance();
            TypeFactory.injectParameters(type, parameters);
            return type;
        }
        catch (Exception e) {
            throw new MappingException("Could not instantiate Type: " + typeClass.getName(), e);
        }
    }

    public static void injectParameters(Object type, Properties parameters) {
        if (ParameterizedType.class.isInstance(type)) {
            if (parameters == null) {
                ((ParameterizedType)type).setParameterValues(EMPTY_PROPERTIES);
            } else {
                ((ParameterizedType)type).setParameterValues(parameters);
            }
        } else if (parameters != null && !parameters.isEmpty()) {
            throw new MappingException("type is not parameterized: " + type.getClass().getName());
        }
    }

    public CompositeCustomType customComponent(Class<CompositeUserType> typeClass, Properties parameters) {
        return TypeFactory.customComponent(typeClass, parameters, this.typeScope);
    }

    @Deprecated
    public static CompositeCustomType customComponent(Class<CompositeUserType> typeClass, Properties parameters, TypeScope scope) {
        try {
            CompositeUserType userType = typeClass.newInstance();
            TypeFactory.injectParameters(userType, parameters);
            return new CompositeCustomType(userType);
        }
        catch (Exception e) {
            throw new MappingException("Unable to instantiate custom type: " + typeClass.getName(), e);
        }
    }

    public CollectionType customCollection(String typeName, Properties typeParameters, String role, String propertyRef) {
        Class typeClass;
        try {
            typeClass = ReflectHelper.classForName(typeName);
        }
        catch (ClassNotFoundException cnfe) {
            throw new MappingException("user collection type class not found: " + typeName, cnfe);
        }
        CustomCollectionType result = new CustomCollectionType(typeClass, role, propertyRef);
        if (typeParameters != null) {
            TypeFactory.injectParameters(result.getUserType(), typeParameters);
        }
        return result;
    }

    public CustomType custom(Class<UserType> typeClass, Properties parameters) {
        try {
            UserType userType = typeClass.newInstance();
            if (TypeConfigurationAware.class.isInstance(userType)) {
                ((TypeConfigurationAware)((Object)userType)).setTypeConfiguration(this.typeConfiguration);
            }
            TypeFactory.injectParameters(userType, parameters);
            return new CustomType(userType);
        }
        catch (Exception e) {
            throw new MappingException("Unable to instantiate custom type: " + typeClass.getName(), e);
        }
    }

    @Deprecated
    public static CustomType custom(Class<UserType> typeClass, Properties parameters, TypeScope scope) {
        try {
            UserType userType = typeClass.newInstance();
            TypeFactory.injectParameters(userType, parameters);
            return new CustomType(userType);
        }
        catch (Exception e) {
            throw new MappingException("Unable to instantiate custom type: " + typeClass.getName(), e);
        }
    }

    public static <T extends Serializable> SerializableType<T> serializable(Class<T> serializableClass) {
        return new SerializableType<T>(serializableClass);
    }

    @Deprecated
    public EntityType oneToOne(String persistentClass, ForeignKeyDirection foreignKeyType, boolean referenceToPrimaryKey, String uniqueKeyPropertyName, boolean lazy, boolean unwrapProxy, String entityName, String propertyName) {
        return this.oneToOne(persistentClass, foreignKeyType, referenceToPrimaryKey, uniqueKeyPropertyName, lazy, unwrapProxy, entityName, propertyName, foreignKeyType != ForeignKeyDirection.TO_PARENT);
    }

    @Deprecated
    public EntityType specialOneToOne(String persistentClass, ForeignKeyDirection foreignKeyType, boolean referenceToPrimaryKey, String uniqueKeyPropertyName, boolean lazy, boolean unwrapProxy, String entityName, String propertyName) {
        return this.specialOneToOne(persistentClass, foreignKeyType, referenceToPrimaryKey, uniqueKeyPropertyName, lazy, unwrapProxy, entityName, propertyName, foreignKeyType != ForeignKeyDirection.TO_PARENT);
    }

    public EntityType oneToOne(String persistentClass, ForeignKeyDirection foreignKeyType, boolean referenceToPrimaryKey, String uniqueKeyPropertyName, boolean lazy, boolean unwrapProxy, String entityName, String propertyName, boolean constrained) {
        return new OneToOneType(this.typeScope, persistentClass, foreignKeyType, referenceToPrimaryKey, uniqueKeyPropertyName, lazy, unwrapProxy, entityName, propertyName, constrained);
    }

    public EntityType specialOneToOne(String persistentClass, ForeignKeyDirection foreignKeyType, boolean referenceToPrimaryKey, String uniqueKeyPropertyName, boolean lazy, boolean unwrapProxy, String entityName, String propertyName, boolean constrained) {
        return new SpecialOneToOneType(this.typeScope, persistentClass, foreignKeyType, referenceToPrimaryKey, uniqueKeyPropertyName, lazy, unwrapProxy, entityName, propertyName, constrained);
    }

    public EntityType manyToOne(String persistentClass) {
        return new ManyToOneType(this.typeScope, persistentClass);
    }

    public EntityType manyToOne(String persistentClass, boolean lazy) {
        return new ManyToOneType(this.typeScope, persistentClass, lazy);
    }

    @Deprecated
    public EntityType manyToOne(String persistentClass, String uniqueKeyPropertyName, boolean lazy, boolean unwrapProxy, NotFoundAction notFoundAction, boolean isLogicalOneToOne) {
        return this.manyToOne(persistentClass, uniqueKeyPropertyName == null, uniqueKeyPropertyName, lazy, unwrapProxy, notFoundAction, isLogicalOneToOne);
    }

    @Deprecated
    public EntityType manyToOne(String persistentClass, boolean referenceToPrimaryKey, String uniqueKeyPropertyName, boolean lazy, boolean unwrapProxy, NotFoundAction notFoundAction, boolean isLogicalOneToOne) {
        return this.manyToOne(persistentClass, referenceToPrimaryKey, uniqueKeyPropertyName, null, lazy, unwrapProxy, notFoundAction, isLogicalOneToOne);
    }

    public EntityType manyToOne(String persistentClass, boolean referenceToPrimaryKey, String uniqueKeyPropertyName, String propertyName, boolean lazy, boolean unwrapProxy, NotFoundAction notFoundAction, boolean isLogicalOneToOne) {
        return new ManyToOneType(this.typeScope, persistentClass, referenceToPrimaryKey, uniqueKeyPropertyName, propertyName, lazy, unwrapProxy, notFoundAction, isLogicalOneToOne);
    }

    public CollectionType array(String role, String propertyRef, Class elementClass) {
        return new ArrayType(role, propertyRef, elementClass);
    }

    public CollectionType list(String role, String propertyRef) {
        return new ListType(role, propertyRef);
    }

    public CollectionType bag(String role, String propertyRef) {
        return new BagType(role, propertyRef);
    }

    public CollectionType idbag(String role, String propertyRef) {
        return new IdentifierBagType(role, propertyRef);
    }

    public CollectionType map(String role, String propertyRef) {
        return new MapType(role, propertyRef);
    }

    public CollectionType orderedMap(String role, String propertyRef) {
        return new OrderedMapType(role, propertyRef);
    }

    public CollectionType sortedMap(String role, String propertyRef, Comparator comparator) {
        return new SortedMapType(role, propertyRef, comparator);
    }

    public CollectionType set(String role, String propertyRef) {
        return new SetType(role, propertyRef);
    }

    public CollectionType orderedSet(String role, String propertyRef) {
        return new OrderedSetType(role, propertyRef);
    }

    public CollectionType sortedSet(String role, String propertyRef, Comparator comparator) {
        return new SortedSetType(role, propertyRef, comparator);
    }

    public ComponentType component(ComponentMetamodel metamodel) {
        return new ComponentType(metamodel);
    }

    public EmbeddedComponentType embeddedComponent(ComponentMetamodel metamodel) {
        return new EmbeddedComponentType(metamodel);
    }

    @Deprecated
    public Type any(Type metaType, Type identifierType) {
        return this.any(metaType, identifierType, true);
    }

    public Type any(Type metaType, Type identifierType, boolean lazy) {
        return new AnyType(this.typeScope, metaType, identifierType, lazy);
    }

    @Deprecated
    public static interface TypeScope
    extends Serializable {
        public TypeConfiguration getTypeConfiguration();
    }
}

