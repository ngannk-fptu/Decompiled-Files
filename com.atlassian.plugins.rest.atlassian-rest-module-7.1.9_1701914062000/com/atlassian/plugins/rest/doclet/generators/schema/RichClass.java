/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.tenancy.TenancyScope
 *  com.atlassian.annotations.tenancy.TenantAware
 *  com.google.common.base.Preconditions
 */
package com.atlassian.plugins.rest.doclet.generators.schema;

import com.atlassian.annotations.tenancy.TenancyScope;
import com.atlassian.annotations.tenancy.TenantAware;
import com.google.common.base.Preconditions;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class RichClass {
    private final Class<?> actualClass;
    private final List<Type> genericTypes;
    @TenantAware(value=TenancyScope.TENANTLESS)
    private final Map<String, Type> typeVariablesResolution;

    private RichClass(Class<?> actualClass, List<Type> genericTypes, Map<String, Type> typeVariablesResolution) {
        this.actualClass = actualClass;
        this.genericTypes = genericTypes;
        this.typeVariablesResolution = new HashMap<String, Type>(typeVariablesResolution);
        this.typeVariablesResolution.putAll(RichClass.typeVariablesResolutionsFromType(actualClass.getGenericSuperclass()));
        for (Type type : actualClass.getGenericInterfaces()) {
            this.typeVariablesResolution.putAll(RichClass.typeVariablesResolutionsFromType(type));
        }
    }

    public static RichClass of(final Class<?> actualClass, final Class<?> ... genericTypes) {
        Preconditions.checkArgument((actualClass.getTypeParameters().length == genericTypes.length ? 1 : 0) != 0, (Object)(actualClass + " has " + actualClass.getTypeParameters().length + " type parameters but " + genericTypes.length + " were provided"));
        Type type = genericTypes.length == 0 ? actualClass : new ParameterizedType(){

            @Override
            public Type[] getActualTypeArguments() {
                return genericTypes;
            }

            @Override
            public Type getRawType() {
                return actualClass;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }
        };
        return RichClass.of(type);
    }

    public static RichClass of(Type type) {
        return RichClass.create(type, Collections.emptyMap());
    }

    private static RichClass create(Type type, Map<String, Type> genericTypesMapping) {
        if (type instanceof Class) {
            Class clazz = (Class)type;
            if (clazz.isArray()) {
                return new RichClass(List.class, Collections.singletonList(clazz.getComponentType()), Collections.emptyMap());
            }
            return new RichClass(clazz, Collections.emptyList(), Collections.emptyMap());
        }
        if (type instanceof ParameterizedType) {
            Class actualClass = (Class)((ParameterizedType)type).getRawType();
            List<Type> actualTypeArguments = Arrays.asList(((ParameterizedType)type).getActualTypeArguments());
            Map<String, Type> typeVariablesResolution = RichClass.typeVariablesResolution(actualClass.getTypeParameters(), actualTypeArguments);
            typeVariablesResolution.putAll(genericTypesMapping);
            return new RichClass(actualClass, actualTypeArguments, typeVariablesResolution);
        }
        if (type instanceof WildcardType) {
            return RichClass.create(((WildcardType)type).getUpperBounds()[0], genericTypesMapping);
        }
        if (type instanceof TypeVariable) {
            Type declaredType = genericTypesMapping.get(((TypeVariable)type).getName());
            Preconditions.checkState((declaredType != null ? 1 : 0) != 0, (String)"unresolved type variable: %s", (Object)type);
            return RichClass.create(declaredType, genericTypesMapping);
        }
        throw new IllegalStateException("Unsupported type: " + type);
    }

    public RichClass createContainedType(Type type) {
        return RichClass.create(type, this.typeVariablesResolution);
    }

    private static Map<String, Type> typeVariablesResolutionsFromType(Type type) {
        if (type instanceof ParameterizedType) {
            Class actualClass = (Class)((ParameterizedType)type).getRawType();
            List<Type> actualTypeArguments = Arrays.asList(((ParameterizedType)type).getActualTypeArguments());
            return RichClass.typeVariablesResolution(actualClass.getTypeParameters(), actualTypeArguments);
        }
        return Collections.emptyMap();
    }

    private static <T> Map<String, Type> typeVariablesResolution(TypeVariable<Class<T>>[] typeParameters, List<Type> actualTypeArguments) {
        HashMap<String, Type> result = new HashMap<String, Type>();
        for (int i = 0; i < typeParameters.length; ++i) {
            Type actualType = actualTypeArguments.get(i);
            if (actualType instanceof TypeVariable) continue;
            result.put(typeParameters[i].getName(), actualType);
        }
        return result;
    }

    public Class<?> getActualClass() {
        return this.actualClass;
    }

    public boolean hasGenericType() {
        return !this.genericTypes.isEmpty();
    }

    public List<RichClass> getGenericTypes() {
        return Collections.unmodifiableList(this.genericTypes.stream().map(this::createContainedType).collect(Collectors.toList()));
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        RichClass that = (RichClass)o;
        return Objects.equals(this.actualClass, that.actualClass) && Objects.equals(this.genericTypes, that.genericTypes) && Objects.equals(this.typeVariablesResolution, that.typeVariablesResolution);
    }

    public int hashCode() {
        return Objects.hash(this.actualClass, this.genericTypes, this.typeVariablesResolution);
    }

    public String toString() {
        return this.actualClass.getSimpleName() + " " + this.genericTypes;
    }
}

