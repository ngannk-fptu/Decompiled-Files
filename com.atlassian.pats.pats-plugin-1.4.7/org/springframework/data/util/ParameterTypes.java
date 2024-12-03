/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.convert.TypeDescriptor
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ConcurrentReferenceHashMap
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.TypeUtils
 */
package org.springframework.data.util;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.data.util.Lazy;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.TypeUtils;

public class ParameterTypes {
    private static final TypeDescriptor OBJECT_DESCRIPTOR = TypeDescriptor.valueOf(Object.class);
    private static final ConcurrentMap<List<TypeDescriptor>, ParameterTypes> cache = new ConcurrentReferenceHashMap();
    private final List<TypeDescriptor> types;
    private final Lazy<Collection<ParameterTypes>> alternatives;

    private ParameterTypes(List<TypeDescriptor> types) {
        this.types = types;
        this.alternatives = Lazy.of(() -> this.getAlternatives());
    }

    public ParameterTypes(List<TypeDescriptor> types, Lazy<Collection<ParameterTypes>> alternatives) {
        this.types = types;
        this.alternatives = alternatives;
    }

    public static ParameterTypes of(List<TypeDescriptor> types) {
        Assert.notNull(types, (String)"Types must not be null!");
        return cache.computeIfAbsent(types, ParameterTypes::new);
    }

    static ParameterTypes of(Class<?> ... types) {
        Assert.notNull(types, (String)"Types must not be null!");
        Assert.noNullElements((Object[])types, (String)"Types must not have null elements!");
        return ParameterTypes.of(Arrays.stream(types).map(TypeDescriptor::valueOf).collect(Collectors.toList()));
    }

    public boolean areValidFor(Method method) {
        Assert.notNull((Object)method, (String)"Method must not be null!");
        if (this.areValidTypes(method)) {
            return true;
        }
        return this.hasValidAlternativeFor(method);
    }

    private boolean hasValidAlternativeFor(Method method) {
        return this.alternatives.get().stream().anyMatch(it -> it.areValidTypes(method)) || this.getParent().map(parent -> parent.hasValidAlternativeFor(method)).orElse(false) != false;
    }

    List<ParameterTypes> getAllAlternatives() {
        ArrayList<ParameterTypes> result = new ArrayList<ParameterTypes>();
        result.addAll(this.alternatives.get());
        this.getParent().ifPresent(it -> result.addAll(it.getAllAlternatives()));
        return result;
    }

    boolean hasTypes(Class<?> ... types) {
        Assert.notNull(types, (String)"Types must not be null!");
        return Arrays.stream(types).map(TypeDescriptor::valueOf).collect(Collectors.toList()).equals(this.types);
    }

    public boolean exactlyMatchParametersOf(Method method) {
        if (method.getParameterCount() != this.types.size()) {
            return false;
        }
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; ++i) {
            if (parameterTypes[i] == this.types.get(i).getType()) continue;
            return false;
        }
        return true;
    }

    public String toString() {
        return this.types.stream().map(TypeDescriptor::getType).map(Class::getSimpleName).collect(Collectors.joining(", ", "(", ")"));
    }

    protected Optional<ParameterTypes> getParent() {
        return this.types.isEmpty() ? Optional.empty() : this.getParent(this.getTail());
    }

    protected final Optional<ParameterTypes> getParent(TypeDescriptor tail) {
        return this.types.size() <= 1 ? Optional.empty() : Optional.of(ParentParameterTypes.of(this.types.subList(0, this.types.size() - 1), tail));
    }

    protected Optional<ParameterTypes> withLastVarArgs() {
        TypeDescriptor lastDescriptor = this.types.get(this.types.size() - 1);
        return lastDescriptor.isArray() ? Optional.empty() : Optional.ofNullable(this.withVarArgs(lastDescriptor));
    }

    private ParameterTypes withVarArgs(TypeDescriptor descriptor) {
        TypeDescriptor lastDescriptor = this.types.get(this.types.size() - 1);
        if (lastDescriptor.isArray() && lastDescriptor.getElementTypeDescriptor().equals((Object)descriptor)) {
            return this;
        }
        ArrayList<TypeDescriptor> result = new ArrayList<TypeDescriptor>(this.types.subList(0, this.types.size() - 1));
        result.add(TypeDescriptor.array((TypeDescriptor)descriptor));
        return ParameterTypes.of(result);
    }

    private Collection<ParameterTypes> getAlternatives() {
        if (this.types.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<ParameterTypes> alternatives = new ArrayList<ParameterTypes>();
        this.withLastVarArgs().ifPresent(alternatives::add);
        ParameterTypes objectVarArgs = this.withVarArgs(OBJECT_DESCRIPTOR);
        if (!alternatives.contains(objectVarArgs)) {
            alternatives.add(objectVarArgs);
        }
        return alternatives;
    }

    private boolean areValidTypes(Method method) {
        Assert.notNull((Object)method, (String)"Method must not be null!");
        if (method.getParameterCount() != this.types.size()) {
            return false;
        }
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; ++i) {
            if (TypeUtils.isAssignable(parameterTypes[i], (Type)this.types.get(i).getType())) continue;
            return false;
        }
        return true;
    }

    private TypeDescriptor getTail() {
        return this.types.get(this.types.size() - 1);
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ParameterTypes)) {
            return false;
        }
        ParameterTypes that = (ParameterTypes)o;
        return ObjectUtils.nullSafeEquals(this.types, that.types);
    }

    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.types);
    }

    static class ParentParameterTypes
    extends ParameterTypes {
        private final TypeDescriptor tail;

        private ParentParameterTypes(List<TypeDescriptor> types, TypeDescriptor tail) {
            super((List)types);
            this.tail = tail;
        }

        public static ParentParameterTypes of(List<TypeDescriptor> types, TypeDescriptor tail) {
            return new ParentParameterTypes(types, tail);
        }

        @Override
        protected Optional<ParameterTypes> getParent() {
            return super.getParent(this.tail);
        }

        @Override
        protected Optional<ParameterTypes> withLastVarArgs() {
            return !this.tail.isAssignableTo(((ParameterTypes)this).getTail()) ? Optional.empty() : super.withLastVarArgs();
        }

        @Override
        public boolean equals(@Nullable Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof ParentParameterTypes)) {
                return false;
            }
            if (!super.equals(o)) {
                return false;
            }
            ParentParameterTypes that = (ParentParameterTypes)o;
            return ObjectUtils.nullSafeEquals((Object)this.tail, (Object)that.tail);
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + ObjectUtils.nullSafeHashCode((Object)this.tail);
            return result;
        }
    }
}

