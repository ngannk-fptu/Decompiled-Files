/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.BeanUtils
 *  org.springframework.core.GenericTypeResolver
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.ReflectionUtils
 */
package org.springframework.data.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.data.util.GenericArrayTypeInformation;
import org.springframework.data.util.Lazy;
import org.springframework.data.util.Optionals;
import org.springframework.data.util.ParameterizedTypeInformation;
import org.springframework.data.util.Streamable;
import org.springframework.data.util.TypeInformation;
import org.springframework.data.util.TypeVariableTypeInformation;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

class TypeDiscoverer<S>
implements TypeInformation<S> {
    private static final Class<?>[] MAP_TYPES;
    private final Type type;
    private final Map<TypeVariable<?>, Type> typeVariableMap;
    private final Map<String, Optional<TypeInformation<?>>> fieldTypes = new ConcurrentHashMap();
    private final int hashCode;
    private final Lazy<Class<S>> resolvedType;
    private final Lazy<TypeInformation<?>> componentType;
    private final Lazy<TypeInformation<?>> valueType;

    protected TypeDiscoverer(Type type, Map<TypeVariable<?>, Type> typeVariableMap) {
        Assert.notNull((Object)type, (String)"Type must not be null!");
        Assert.notNull(typeVariableMap, (String)"TypeVariableMap must not be null!");
        this.type = type;
        this.resolvedType = Lazy.of(() -> this.resolveType(type));
        this.componentType = Lazy.of(this::doGetComponentType);
        this.valueType = Lazy.of(this::doGetMapValueType);
        this.typeVariableMap = typeVariableMap;
        this.hashCode = 17 + 31 * type.hashCode() + 31 * typeVariableMap.hashCode();
    }

    protected Map<TypeVariable<?>, Type> getTypeVariableMap() {
        return this.typeVariableMap;
    }

    protected TypeInformation<?> createInfo(Type fieldType) {
        Assert.notNull((Object)fieldType, (String)"Field type must not be null!");
        if (fieldType.equals(this.type)) {
            return this;
        }
        if (fieldType instanceof Class) {
            return ClassTypeInformation.from((Class)fieldType);
        }
        if (fieldType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType)fieldType;
            return new ParameterizedTypeInformation(parameterizedType, this);
        }
        if (fieldType instanceof TypeVariable) {
            TypeVariable variable = (TypeVariable)fieldType;
            return new TypeVariableTypeInformation(variable, this);
        }
        if (fieldType instanceof GenericArrayType) {
            return new GenericArrayTypeInformation((GenericArrayType)fieldType, this);
        }
        if (fieldType instanceof WildcardType) {
            WildcardType wildcardType = (WildcardType)fieldType;
            Type[] bounds = wildcardType.getLowerBounds();
            if (bounds.length > 0) {
                return this.createInfo(bounds[0]);
            }
            bounds = wildcardType.getUpperBounds();
            if (bounds.length > 0) {
                return this.createInfo(bounds[0]);
            }
        }
        throw new IllegalArgumentException();
    }

    protected Class<S> resolveType(Type type) {
        HashMap map = new HashMap();
        map.putAll(this.getTypeVariableMap());
        return GenericTypeResolver.resolveType((Type)type, map);
    }

    @Override
    public List<TypeInformation<?>> getParameterTypes(Constructor<?> constructor) {
        Assert.notNull(constructor, (String)"Constructor must not be null!");
        ArrayList parameterTypes = new ArrayList(constructor.getParameterCount());
        for (Parameter parameter : constructor.getParameters()) {
            parameterTypes.add(this.createInfo(parameter.getParameterizedType()));
        }
        return parameterTypes;
    }

    @Override
    @Nullable
    public TypeInformation<?> getProperty(String fieldname) {
        int separatorIndex = fieldname.indexOf(46);
        if (separatorIndex == -1) {
            return this.fieldTypes.computeIfAbsent(fieldname, this::getPropertyInformation).orElse(null);
        }
        String head = fieldname.substring(0, separatorIndex);
        TypeInformation<?> info = this.getProperty(head);
        if (info == null) {
            return null;
        }
        return info.getProperty(fieldname.substring(separatorIndex + 1));
    }

    private Optional<TypeInformation<?>> getPropertyInformation(String fieldname) {
        Class<S> rawType = this.getType();
        Field field = ReflectionUtils.findField(rawType, (String)fieldname);
        if (field != null) {
            return Optional.of(this.createInfo(field.getGenericType()));
        }
        return TypeDiscoverer.findPropertyDescriptor(rawType, fieldname).map(it -> this.createInfo(TypeDiscoverer.getGenericType(it)));
    }

    private static Optional<PropertyDescriptor> findPropertyDescriptor(Class<?> type, String fieldname) {
        PropertyDescriptor descriptor = BeanUtils.getPropertyDescriptor(type, (String)fieldname);
        if (descriptor != null) {
            return Optional.of(descriptor);
        }
        ArrayList superTypes = new ArrayList();
        superTypes.addAll(Arrays.asList(type.getInterfaces()));
        superTypes.add(type.getSuperclass());
        return Streamable.of(type.getInterfaces()).stream().flatMap(it -> Optionals.toStream(TypeDiscoverer.findPropertyDescriptor(it, fieldname))).findFirst();
    }

    @Nullable
    private static Type getGenericType(PropertyDescriptor descriptor) {
        Method method = descriptor.getReadMethod();
        if (method != null) {
            return method.getGenericReturnType();
        }
        method = descriptor.getWriteMethod();
        if (method == null) {
            return null;
        }
        Type[] parameterTypes = method.getGenericParameterTypes();
        return parameterTypes.length == 0 ? null : parameterTypes[0];
    }

    @Override
    public Class<S> getType() {
        return this.resolvedType.get();
    }

    @Override
    public ClassTypeInformation<?> getRawTypeInformation() {
        return ClassTypeInformation.from(this.getType()).getRawTypeInformation();
    }

    @Override
    @Nullable
    public TypeInformation<?> getActualType() {
        if (this.isMap()) {
            return this.getMapValueType();
        }
        if (this.isCollectionLike()) {
            return this.getComponentType();
        }
        if (this.isNullableWrapper()) {
            return this.getComponentType();
        }
        return this;
    }

    @Override
    public boolean isMap() {
        Class<S> type = this.getType();
        for (Class<S> clazz : MAP_TYPES) {
            if (!clazz.isAssignableFrom(type)) continue;
            return true;
        }
        return false;
    }

    @Override
    @Nullable
    public TypeInformation<?> getMapValueType() {
        return this.valueType.orElse(null);
    }

    @Nullable
    protected TypeInformation<?> doGetMapValueType() {
        return this.isMap() ? this.getTypeArgument(this.getBaseType(MAP_TYPES), 1) : (TypeInformation)this.getTypeArguments().stream().skip(1L).findFirst().orElse(null);
    }

    @Override
    public boolean isCollectionLike() {
        Class<S> rawType = this.getType();
        return rawType.isArray() || Iterable.class.equals(rawType) || Collection.class.isAssignableFrom(rawType) || Streamable.class.isAssignableFrom(rawType);
    }

    @Override
    @Nullable
    public final TypeInformation<?> getComponentType() {
        return this.componentType.orElse(null);
    }

    @Nullable
    protected TypeInformation<?> doGetComponentType() {
        Class<S> rawType = this.getType();
        if (rawType.isArray()) {
            return this.createInfo(rawType.getComponentType());
        }
        if (this.isMap()) {
            return this.getTypeArgument(this.getBaseType(MAP_TYPES), 0);
        }
        if (Iterable.class.isAssignableFrom(rawType)) {
            return this.getTypeArgument(Iterable.class, 0);
        }
        if (this.isNullableWrapper()) {
            return this.getTypeArgument(rawType, 0);
        }
        List<TypeInformation<?>> arguments = this.getTypeArguments();
        return arguments.size() > 0 ? arguments.get(0) : null;
    }

    @Override
    public TypeInformation<?> getReturnType(Method method) {
        Assert.notNull((Object)method, (String)"Method must not be null!");
        return this.createInfo(method.getGenericReturnType());
    }

    @Override
    public List<TypeInformation<?>> getParameterTypes(Method method) {
        Assert.notNull((Object)method, (String)"Method most not be null!");
        return Streamable.of(method.getGenericParameterTypes()).stream().map(this::createInfo).collect(Collectors.toList());
    }

    @Override
    @Nullable
    public TypeInformation<?> getSuperTypeInformation(Class<?> superType) {
        Class<S> rawType = this.getType();
        if (!superType.isAssignableFrom(rawType)) {
            return null;
        }
        if (this.getType().equals(superType)) {
            return this;
        }
        ArrayList<Type> candidates = new ArrayList<Type>();
        Type genericSuperclass = rawType.getGenericSuperclass();
        if (genericSuperclass != null) {
            candidates.add(genericSuperclass);
        }
        candidates.addAll(Arrays.asList(rawType.getGenericInterfaces()));
        for (Type candidate : candidates) {
            TypeInformation<S> candidateInfo = this.createInfo(candidate);
            if (superType.equals(candidateInfo.getType())) {
                return candidateInfo;
            }
            TypeInformation<?> nestedSuperType = candidateInfo.getSuperTypeInformation(superType);
            if (nestedSuperType == null) continue;
            return nestedSuperType;
        }
        return null;
    }

    @Override
    public List<TypeInformation<?>> getTypeArguments() {
        return Collections.emptyList();
    }

    @Override
    public boolean isAssignableFrom(TypeInformation<?> target) {
        TypeInformation<S> superTypeInformation = target.getSuperTypeInformation(this.getType());
        return superTypeInformation == null ? false : superTypeInformation.equals(this);
    }

    @Override
    public TypeInformation<? extends S> specialize(ClassTypeInformation<?> type) {
        Assert.notNull(type, (String)"Type must not be null!");
        Assert.isTrue((boolean)this.getType().isAssignableFrom(type.getType()), () -> String.format("%s must be assignable from %s", this.getType(), type.getType()));
        List<TypeInformation<?>> typeArguments = this.getTypeArguments();
        return typeArguments.isEmpty() ? type : type.createInfo(new SyntheticParamterizedType(type, this.getTypeArguments()));
    }

    @Nullable
    private TypeInformation<?> getTypeArgument(Class<?> bound, int index) {
        Class[] arguments = GenericTypeResolver.resolveTypeArguments(this.getType(), bound);
        if (arguments != null) {
            return this.createInfo(arguments[index]);
        }
        return this.getSuperTypeInformation(bound) instanceof ParameterizedTypeInformation ? ClassTypeInformation.OBJECT : null;
    }

    private Class<?> getBaseType(Class<?>[] candidates) {
        Class<S> type = this.getType();
        for (Class<S> clazz : candidates) {
            if (!clazz.isAssignableFrom(type)) continue;
            return clazz;
        }
        throw new IllegalArgumentException(String.format("Type %s not contained in candidates %s!", type, candidates));
    }

    public boolean equals(@Nullable Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!this.getClass().equals(obj.getClass())) {
            return false;
        }
        TypeDiscoverer that = (TypeDiscoverer)obj;
        if (!this.type.equals(that.type)) {
            return false;
        }
        if (this.typeVariableMap.isEmpty() && that.typeVariableMap.isEmpty()) {
            return true;
        }
        return this.typeVariableMap.equals(that.typeVariableMap);
    }

    public int hashCode() {
        return this.hashCode;
    }

    static {
        ClassLoader classLoader = TypeDiscoverer.class.getClassLoader();
        HashSet<Class> mapTypes = new HashSet<Class>();
        mapTypes.add(Map.class);
        try {
            mapTypes.add(ClassUtils.forName((String)"io.vavr.collection.Map", (ClassLoader)classLoader));
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
        MAP_TYPES = mapTypes.toArray(new Class[0]);
    }

    private static class SyntheticParamterizedType
    implements ParameterizedType {
        private final ClassTypeInformation<?> typeInformation;
        private final List<TypeInformation<?>> typeParameters;

        public SyntheticParamterizedType(ClassTypeInformation<?> typeInformation, List<TypeInformation<?>> typeParameters) {
            this.typeInformation = typeInformation;
            this.typeParameters = typeParameters;
        }

        @Override
        public Type getRawType() {
            return this.typeInformation.getType();
        }

        @Override
        @Nullable
        public Type getOwnerType() {
            return null;
        }

        @Override
        public Type[] getActualTypeArguments() {
            Type[] result = new Type[this.typeParameters.size()];
            for (int i = 0; i < this.typeParameters.size(); ++i) {
                result[i] = this.typeParameters.get(i).getType();
            }
            return result;
        }

        public boolean equals(@Nullable Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof SyntheticParamterizedType)) {
                return false;
            }
            SyntheticParamterizedType that = (SyntheticParamterizedType)o;
            if (!ObjectUtils.nullSafeEquals(this.typeInformation, that.typeInformation)) {
                return false;
            }
            return ObjectUtils.nullSafeEquals(this.typeParameters, that.typeParameters);
        }

        public int hashCode() {
            int result = ObjectUtils.nullSafeHashCode(this.typeInformation);
            result = 31 * result + ObjectUtils.nullSafeHashCode(this.typeParameters);
            return result;
        }
    }
}

