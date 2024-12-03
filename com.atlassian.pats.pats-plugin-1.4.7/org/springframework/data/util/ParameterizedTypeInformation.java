/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.StringUtils
 */
package org.springframework.data.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.data.util.Lazy;
import org.springframework.data.util.Pair;
import org.springframework.data.util.ParentTypeAwareTypeInformation;
import org.springframework.data.util.TypeDiscoverer;
import org.springframework.data.util.TypeInformation;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

class ParameterizedTypeInformation<T>
extends ParentTypeAwareTypeInformation<T> {
    private final ParameterizedType type;
    private final Lazy<Boolean> resolved;

    public ParameterizedTypeInformation(ParameterizedType type, TypeDiscoverer<?> parent) {
        super(type, parent, ParameterizedTypeInformation.calculateTypeVariables(type, parent));
        this.type = type;
        this.resolved = Lazy.of(() -> this.isResolvedCompletely());
    }

    @Override
    @Nullable
    protected TypeInformation<?> doGetMapValueType() {
        Type[] arguments;
        if (Map.class.isAssignableFrom(this.getType()) && (arguments = this.type.getActualTypeArguments()).length > 1) {
            return this.createInfo(arguments[1]);
        }
        Class rawType = this.getType();
        HashSet<Type> supertypes = new HashSet<Type>();
        Optional.ofNullable(rawType.getGenericSuperclass()).ifPresent(supertypes::add);
        supertypes.addAll(Arrays.asList(rawType.getGenericInterfaces()));
        Optional<TypeInformation> result = supertypes.stream().map(it -> Pair.of(it, this.resolveType((Type)it))).filter(it -> Map.class.isAssignableFrom((Class)it.getSecond())).map(it -> {
            ParameterizedType parameterizedSupertype = (ParameterizedType)it.getFirst();
            Type[] arguments = parameterizedSupertype.getActualTypeArguments();
            return this.createInfo(arguments[1]);
        }).findFirst();
        return result.orElseGet(() -> super.doGetMapValueType());
    }

    @Override
    public List<TypeInformation<?>> getTypeArguments() {
        ArrayList result = new ArrayList();
        for (Type argument : this.type.getActualTypeArguments()) {
            result.add(this.createInfo(argument));
        }
        return result;
    }

    @Override
    public boolean isAssignableFrom(TypeInformation<?> target) {
        List<Object> typeParameters;
        Class<?> rawTargetType;
        if (this.equals(target)) {
            return true;
        }
        Class rawType = this.getType();
        if (!rawType.isAssignableFrom(rawTargetType = target.getType())) {
            return false;
        }
        TypeInformation<?> otherTypeInformation = rawType.equals(rawTargetType) ? target : target.getSuperTypeInformation(rawType);
        List<TypeInformation<?>> myParameters = this.getTypeArguments();
        List<Object> list = typeParameters = otherTypeInformation == null ? Collections.emptyList() : otherTypeInformation.getTypeArguments();
        if (myParameters.size() != typeParameters.size()) {
            return false;
        }
        for (int i = 0; i < myParameters.size(); ++i) {
            if (myParameters.get(i).isAssignableFrom((TypeInformation)typeParameters.get(i))) continue;
            return false;
        }
        return true;
    }

    @Override
    @Nullable
    protected TypeInformation<?> doGetComponentType() {
        boolean isCustomMapImplementation;
        boolean bl = isCustomMapImplementation = this.isMap() && !this.getType().equals(Map.class);
        if (isCustomMapImplementation) {
            return this.getRequiredSuperTypeInformation(Map.class).getComponentType();
        }
        return this.createInfo(this.type.getActualTypeArguments()[0]);
    }

    @Override
    public TypeInformation<? extends T> specialize(ClassTypeInformation<?> type) {
        if (this.isResolvedCompletely()) {
            return type;
        }
        TypeInformation asSupertype = type.getSuperTypeInformation(this.getType());
        if (asSupertype == null || !ParameterizedTypeInformation.class.isInstance(asSupertype)) {
            return super.specialize((ClassTypeInformation)type);
        }
        return ((ParameterizedTypeInformation)asSupertype).isResolvedCompletely() ? type : super.specialize((ClassTypeInformation)type);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ParameterizedTypeInformation)) {
            return false;
        }
        ParameterizedTypeInformation that = (ParameterizedTypeInformation)obj;
        if (this.isResolved() && that.isResolved()) {
            return this.type.equals(that.type);
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return this.isResolved() ? this.type.hashCode() : super.hashCode();
    }

    public String toString() {
        return String.format("%s<%s>", this.getType().getName(), StringUtils.collectionToCommaDelimitedString(this.getTypeArguments()));
    }

    private boolean isResolved() {
        return this.resolved.get();
    }

    private boolean isResolvedCompletely() {
        Type[] typeArguments = this.type.getActualTypeArguments();
        if (typeArguments.length == 0) {
            return false;
        }
        for (Type typeArgument : typeArguments) {
            TypeInformation<?> info = this.createInfo(typeArgument);
            if (info instanceof ParameterizedTypeInformation && !((ParameterizedTypeInformation)info).isResolvedCompletely()) {
                return false;
            }
            if (info instanceof ClassTypeInformation) continue;
            return false;
        }
        return true;
    }

    private static Map<TypeVariable<?>, Type> calculateTypeVariables(ParameterizedType type, TypeDiscoverer<?> parent) {
        Class<?> resolvedType = parent.resolveType(type);
        TypeVariable[] typeParameters = resolvedType.getTypeParameters();
        Type[] arguments = type.getActualTypeArguments();
        HashMap localTypeVariables = new HashMap(parent.getTypeVariableMap());
        IntStream.range(0, typeParameters.length).mapToObj(it -> Pair.of(typeParameters[it], ParameterizedTypeInformation.flattenTypeVariable(arguments[it], localTypeVariables))).forEach(it -> {
            Type cfr_ignored_0 = (Type)localTypeVariables.put((TypeVariable<?>)it.getFirst(), (Type)it.getSecond());
        });
        return localTypeVariables;
    }

    private static Type flattenTypeVariable(Type source, Map<TypeVariable<?>, Type> variables) {
        if (!(source instanceof TypeVariable)) {
            return source;
        }
        Type value = variables.get(source);
        return value == null ? source : ParameterizedTypeInformation.flattenTypeVariable(value, variables);
    }
}

