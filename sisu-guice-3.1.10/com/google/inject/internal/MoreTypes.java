/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableMap$Builder
 *  javax.inject.Provider
 */
package com.google.inject.internal;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.inject.ConfigurationException;
import com.google.inject.TypeLiteral;
import com.google.inject.internal.Errors;
import com.google.inject.util.Types;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Map;
import java.util.NoSuchElementException;
import javax.inject.Provider;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class MoreTypes {
    public static final Type[] EMPTY_TYPE_ARRAY = new Type[0];
    private static final Map<TypeLiteral<?>, TypeLiteral<?>> PRIMITIVE_TO_WRAPPER = new ImmutableMap.Builder().put(TypeLiteral.get(Boolean.TYPE), TypeLiteral.get(Boolean.class)).put(TypeLiteral.get(Byte.TYPE), TypeLiteral.get(Byte.class)).put(TypeLiteral.get(Short.TYPE), TypeLiteral.get(Short.class)).put(TypeLiteral.get(Integer.TYPE), TypeLiteral.get(Integer.class)).put(TypeLiteral.get(Long.TYPE), TypeLiteral.get(Long.class)).put(TypeLiteral.get(Float.TYPE), TypeLiteral.get(Float.class)).put(TypeLiteral.get(Double.TYPE), TypeLiteral.get(Double.class)).put(TypeLiteral.get(Character.TYPE), TypeLiteral.get(Character.class)).put(TypeLiteral.get(Void.TYPE), TypeLiteral.get(Void.class)).build();

    private MoreTypes() {
    }

    public static <T> TypeLiteral<T> canonicalizeForKey(TypeLiteral<T> typeLiteral) {
        Type type = typeLiteral.getType();
        if (!MoreTypes.isFullySpecified(type)) {
            Errors errors = new Errors().keyNotFullySpecified(typeLiteral);
            throw new ConfigurationException(errors.getMessages());
        }
        if (typeLiteral.getRawType() == Provider.class) {
            ParameterizedType parameterizedType = (ParameterizedType)type;
            TypeLiteral<?> guiceProviderType = TypeLiteral.get(Types.providerOf(parameterizedType.getActualTypeArguments()[0]));
            return guiceProviderType;
        }
        TypeLiteral<?> wrappedPrimitives = PRIMITIVE_TO_WRAPPER.get(typeLiteral);
        return wrappedPrimitives != null ? wrappedPrimitives : typeLiteral;
    }

    private static boolean isFullySpecified(Type type) {
        if (type instanceof Class) {
            return true;
        }
        if (type instanceof CompositeType) {
            return ((CompositeType)((Object)type)).isFullySpecified();
        }
        if (type instanceof TypeVariable) {
            return false;
        }
        return ((CompositeType)((Object)MoreTypes.canonicalize(type))).isFullySpecified();
    }

    public static Type canonicalize(Type type) {
        if (type instanceof Class) {
            Class c = (Class)type;
            return c.isArray() ? new GenericArrayTypeImpl(MoreTypes.canonicalize(c.getComponentType())) : c;
        }
        if (type instanceof CompositeType) {
            return type;
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType p = (ParameterizedType)type;
            return new ParameterizedTypeImpl(p.getOwnerType(), p.getRawType(), p.getActualTypeArguments());
        }
        if (type instanceof GenericArrayType) {
            GenericArrayType g = (GenericArrayType)type;
            return new GenericArrayTypeImpl(g.getGenericComponentType());
        }
        if (type instanceof WildcardType) {
            WildcardType w = (WildcardType)type;
            return new WildcardTypeImpl(w.getUpperBounds(), w.getLowerBounds());
        }
        return type;
    }

    public static Class<?> getRawType(Type type) {
        if (type instanceof Class) {
            return (Class)type;
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType)type;
            Type rawType = parameterizedType.getRawType();
            Preconditions.checkArgument((boolean)(rawType instanceof Class), (String)"Expected a Class, but <%s> is of type %s", (Object[])new Object[]{type, type.getClass().getName()});
            return (Class)rawType;
        }
        if (type instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType)type).getGenericComponentType();
            return Array.newInstance(MoreTypes.getRawType(componentType), 0).getClass();
        }
        if (type instanceof TypeVariable) {
            return Object.class;
        }
        throw new IllegalArgumentException("Expected a Class, ParameterizedType, or GenericArrayType, but <" + type + "> is of type " + type.getClass().getName());
    }

    public static boolean equals(Type a, Type b) {
        if (a == b) {
            return true;
        }
        if (a instanceof Class) {
            return a.equals(b);
        }
        if (a instanceof ParameterizedType) {
            if (!(b instanceof ParameterizedType)) {
                return false;
            }
            ParameterizedType pa = (ParameterizedType)a;
            ParameterizedType pb = (ParameterizedType)b;
            return Objects.equal((Object)pa.getOwnerType(), (Object)pb.getOwnerType()) && pa.getRawType().equals(pb.getRawType()) && Arrays.equals(pa.getActualTypeArguments(), pb.getActualTypeArguments());
        }
        if (a instanceof GenericArrayType) {
            if (!(b instanceof GenericArrayType)) {
                return false;
            }
            GenericArrayType ga = (GenericArrayType)a;
            GenericArrayType gb = (GenericArrayType)b;
            return MoreTypes.equals(ga.getGenericComponentType(), gb.getGenericComponentType());
        }
        if (a instanceof WildcardType) {
            if (!(b instanceof WildcardType)) {
                return false;
            }
            WildcardType wa = (WildcardType)a;
            WildcardType wb = (WildcardType)b;
            return Arrays.equals(wa.getUpperBounds(), wb.getUpperBounds()) && Arrays.equals(wa.getLowerBounds(), wb.getLowerBounds());
        }
        if (a instanceof TypeVariable) {
            if (!(b instanceof TypeVariable)) {
                return false;
            }
            TypeVariable va = (TypeVariable)a;
            TypeVariable vb = (TypeVariable)b;
            return va.getGenericDeclaration().equals(vb.getGenericDeclaration()) && va.getName().equals(vb.getName());
        }
        return false;
    }

    private static int hashCodeOrZero(Object o) {
        return o != null ? o.hashCode() : 0;
    }

    public static String typeToString(Type type) {
        return type instanceof Class ? ((Class)type).getName() : type.toString();
    }

    public static Type getGenericSupertype(Type type, Class<?> rawType, Class<?> toResolve) {
        if (toResolve == rawType) {
            return type;
        }
        if (toResolve.isInterface()) {
            Class<?>[] interfaces = rawType.getInterfaces();
            int length = interfaces.length;
            for (int i = 0; i < length; ++i) {
                if (interfaces[i] == toResolve) {
                    return rawType.getGenericInterfaces()[i];
                }
                if (!toResolve.isAssignableFrom(interfaces[i])) continue;
                return MoreTypes.getGenericSupertype(rawType.getGenericInterfaces()[i], interfaces[i], toResolve);
            }
        }
        if (!rawType.isInterface()) {
            while (rawType != Object.class) {
                Class<?> rawSupertype = rawType.getSuperclass();
                if (rawSupertype == toResolve) {
                    return rawType.getGenericSuperclass();
                }
                if (toResolve.isAssignableFrom(rawSupertype)) {
                    return MoreTypes.getGenericSupertype(rawType.getGenericSuperclass(), rawSupertype, toResolve);
                }
                rawType = rawSupertype;
            }
        }
        return toResolve;
    }

    public static Type resolveTypeVariable(Type type, Class<?> rawType, TypeVariable unknown) {
        Class<?> declaredByRaw = MoreTypes.declaringClassOf(unknown);
        if (declaredByRaw == null) {
            return unknown;
        }
        Type declaredBy = MoreTypes.getGenericSupertype(type, rawType, declaredByRaw);
        if (declaredBy instanceof ParameterizedType) {
            int index = MoreTypes.indexOf(declaredByRaw.getTypeParameters(), unknown);
            return ((ParameterizedType)declaredBy).getActualTypeArguments()[index];
        }
        return unknown;
    }

    private static int indexOf(Object[] array, Object toFind) {
        for (int i = 0; i < array.length; ++i) {
            if (!toFind.equals(array[i])) continue;
            return i;
        }
        throw new NoSuchElementException();
    }

    private static Class<?> declaringClassOf(TypeVariable typeVariable) {
        Object genericDeclaration = typeVariable.getGenericDeclaration();
        return genericDeclaration instanceof Class ? (Class)genericDeclaration : null;
    }

    private static void checkNotPrimitive(Type type, String use) {
        Preconditions.checkArgument((!(type instanceof Class) || !((Class)type).isPrimitive() ? 1 : 0) != 0, (String)"Primitive types are not allowed in %s: %s", (Object[])new Object[]{use, type});
    }

    private static interface CompositeType {
        public boolean isFullySpecified();
    }

    public static class WildcardTypeImpl
    implements WildcardType,
    Serializable,
    CompositeType {
        private final Type upperBound;
        private final Type lowerBound;
        private static final long serialVersionUID = 0L;

        public WildcardTypeImpl(Type[] upperBounds, Type[] lowerBounds) {
            Preconditions.checkArgument((lowerBounds.length <= 1 ? 1 : 0) != 0, (Object)"Must have at most one lower bound.");
            Preconditions.checkArgument((upperBounds.length == 1 ? 1 : 0) != 0, (Object)"Must have exactly one upper bound.");
            if (lowerBounds.length == 1) {
                Preconditions.checkNotNull((Object)lowerBounds[0], (Object)"lowerBound");
                MoreTypes.checkNotPrimitive(lowerBounds[0], "wildcard bounds");
                Preconditions.checkArgument((upperBounds[0] == Object.class ? 1 : 0) != 0, (Object)"bounded both ways");
                this.lowerBound = MoreTypes.canonicalize(lowerBounds[0]);
                this.upperBound = Object.class;
            } else {
                Preconditions.checkNotNull((Object)upperBounds[0], (Object)"upperBound");
                MoreTypes.checkNotPrimitive(upperBounds[0], "wildcard bounds");
                this.lowerBound = null;
                this.upperBound = MoreTypes.canonicalize(upperBounds[0]);
            }
        }

        public Type[] getUpperBounds() {
            return new Type[]{this.upperBound};
        }

        public Type[] getLowerBounds() {
            Type[] typeArray;
            if (this.lowerBound != null) {
                Type[] typeArray2 = new Type[1];
                typeArray = typeArray2;
                typeArray2[0] = this.lowerBound;
            } else {
                typeArray = EMPTY_TYPE_ARRAY;
            }
            return typeArray;
        }

        public boolean isFullySpecified() {
            return MoreTypes.isFullySpecified(this.upperBound) && (this.lowerBound == null || MoreTypes.isFullySpecified(this.lowerBound));
        }

        public boolean equals(Object other) {
            return other instanceof WildcardType && MoreTypes.equals(this, (WildcardType)other);
        }

        public int hashCode() {
            return (this.lowerBound != null ? 31 + this.lowerBound.hashCode() : 1) ^ 31 + this.upperBound.hashCode();
        }

        public String toString() {
            if (this.lowerBound != null) {
                return "? super " + MoreTypes.typeToString(this.lowerBound);
            }
            if (this.upperBound == Object.class) {
                return "?";
            }
            return "? extends " + MoreTypes.typeToString(this.upperBound);
        }
    }

    public static class GenericArrayTypeImpl
    implements GenericArrayType,
    Serializable,
    CompositeType {
        private final Type componentType;
        private static final long serialVersionUID = 0L;

        public GenericArrayTypeImpl(Type componentType) {
            this.componentType = MoreTypes.canonicalize(componentType);
        }

        public Type getGenericComponentType() {
            return this.componentType;
        }

        public boolean isFullySpecified() {
            return MoreTypes.isFullySpecified(this.componentType);
        }

        public boolean equals(Object o) {
            return o instanceof GenericArrayType && MoreTypes.equals(this, (GenericArrayType)o);
        }

        public int hashCode() {
            return this.componentType.hashCode();
        }

        public String toString() {
            return MoreTypes.typeToString(this.componentType) + "[]";
        }
    }

    public static class ParameterizedTypeImpl
    implements ParameterizedType,
    Serializable,
    CompositeType {
        private final Type ownerType;
        private final Type rawType;
        private final Type[] typeArguments;
        private static final long serialVersionUID = 0L;

        public ParameterizedTypeImpl(Type ownerType, Type rawType, Type ... typeArguments) {
            if (rawType instanceof Class) {
                Class rawTypeAsClass = (Class)rawType;
                Preconditions.checkArgument((ownerType != null || rawTypeAsClass.getEnclosingClass() == null ? 1 : 0) != 0, (String)"No owner type for enclosed %s", (Object[])new Object[]{rawType});
                Preconditions.checkArgument((ownerType == null || rawTypeAsClass.getEnclosingClass() != null ? 1 : 0) != 0, (String)"Owner type for unenclosed %s", (Object[])new Object[]{rawType});
            }
            this.ownerType = ownerType == null ? null : MoreTypes.canonicalize(ownerType);
            this.rawType = MoreTypes.canonicalize(rawType);
            this.typeArguments = (Type[])typeArguments.clone();
            for (int t = 0; t < this.typeArguments.length; ++t) {
                Preconditions.checkNotNull((Object)this.typeArguments[t], (Object)"type parameter");
                MoreTypes.checkNotPrimitive(this.typeArguments[t], "type parameters");
                this.typeArguments[t] = MoreTypes.canonicalize(this.typeArguments[t]);
            }
        }

        public Type[] getActualTypeArguments() {
            return (Type[])this.typeArguments.clone();
        }

        public Type getRawType() {
            return this.rawType;
        }

        public Type getOwnerType() {
            return this.ownerType;
        }

        public boolean isFullySpecified() {
            if (this.ownerType != null && !MoreTypes.isFullySpecified(this.ownerType)) {
                return false;
            }
            if (!MoreTypes.isFullySpecified(this.rawType)) {
                return false;
            }
            for (Type type : this.typeArguments) {
                if (MoreTypes.isFullySpecified(type)) continue;
                return false;
            }
            return true;
        }

        public boolean equals(Object other) {
            return other instanceof ParameterizedType && MoreTypes.equals(this, (ParameterizedType)other);
        }

        public int hashCode() {
            return Arrays.hashCode(this.typeArguments) ^ this.rawType.hashCode() ^ MoreTypes.hashCodeOrZero(this.ownerType);
        }

        public String toString() {
            StringBuilder stringBuilder = new StringBuilder(30 * (this.typeArguments.length + 1));
            stringBuilder.append(MoreTypes.typeToString(this.rawType));
            if (this.typeArguments.length == 0) {
                return stringBuilder.toString();
            }
            stringBuilder.append("<").append(MoreTypes.typeToString(this.typeArguments[0]));
            for (int i = 1; i < this.typeArguments.length; ++i) {
                stringBuilder.append(", ").append(MoreTypes.typeToString(this.typeArguments[i]));
            }
            return stringBuilder.append(">").toString();
        }
    }
}

