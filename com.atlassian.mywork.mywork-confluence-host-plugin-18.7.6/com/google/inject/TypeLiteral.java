/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject;

import com.google.inject.Provider;
import com.google.inject.internal.MoreTypes;
import com.google.inject.internal.util.$ImmutableList;
import com.google.inject.internal.util.$Preconditions;
import com.google.inject.util.Types;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TypeLiteral<T> {
    final Class<? super T> rawType;
    final Type type;
    final int hashCode;

    protected TypeLiteral() {
        this.type = TypeLiteral.getSuperclassTypeParameter(this.getClass());
        this.rawType = MoreTypes.getRawType(this.type);
        this.hashCode = this.type.hashCode();
    }

    TypeLiteral(Type type) {
        this.type = MoreTypes.canonicalize($Preconditions.checkNotNull(type, "type"));
        this.rawType = MoreTypes.getRawType(this.type);
        this.hashCode = this.type.hashCode();
    }

    static Type getSuperclassTypeParameter(Class<?> subclass) {
        Type superclass = subclass.getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType parameterized = (ParameterizedType)superclass;
        return MoreTypes.canonicalize(parameterized.getActualTypeArguments()[0]);
    }

    static TypeLiteral<?> fromSuperclassTypeParameter(Class<?> subclass) {
        return new TypeLiteral(TypeLiteral.getSuperclassTypeParameter(subclass));
    }

    public final Class<? super T> getRawType() {
        return this.rawType;
    }

    public final Type getType() {
        return this.type;
    }

    final TypeLiteral<Provider<T>> providerType() {
        return TypeLiteral.get(Types.providerOf(this.getType()));
    }

    public final int hashCode() {
        return this.hashCode;
    }

    public final boolean equals(Object o) {
        return o instanceof TypeLiteral && MoreTypes.equals(this.type, ((TypeLiteral)o).type);
    }

    public final String toString() {
        return MoreTypes.typeToString(this.type);
    }

    public static TypeLiteral<?> get(Type type) {
        return new TypeLiteral(type);
    }

    public static <T> TypeLiteral<T> get(Class<T> type) {
        return new TypeLiteral<T>(type);
    }

    private List<TypeLiteral<?>> resolveAll(Type[] types) {
        TypeLiteral[] result = new TypeLiteral[types.length];
        for (int t = 0; t < types.length; ++t) {
            result[t] = this.resolve(types[t]);
        }
        return $ImmutableList.of(result);
    }

    TypeLiteral<?> resolve(Type toResolve) {
        return TypeLiteral.get(this.resolveType(toResolve));
    }

    Type resolveType(Type toResolve) {
        Type original;
        while (toResolve instanceof TypeVariable) {
            original = (TypeVariable)toResolve;
            if ((toResolve = MoreTypes.resolveTypeVariable(this.type, this.rawType, (TypeVariable)original)) != original) continue;
            return toResolve;
        }
        if (toResolve instanceof GenericArrayType) {
            Type newComponentType;
            original = (GenericArrayType)toResolve;
            Type componentType = original.getGenericComponentType();
            return componentType == (newComponentType = this.resolveType(componentType)) ? original : Types.arrayOf(newComponentType);
        }
        if (toResolve instanceof ParameterizedType) {
            original = (ParameterizedType)toResolve;
            Type ownerType = original.getOwnerType();
            Type newOwnerType = this.resolveType(ownerType);
            boolean changed = newOwnerType != ownerType;
            Type[] args = original.getActualTypeArguments();
            int length = args.length;
            for (int t = 0; t < length; ++t) {
                Type resolvedTypeArgument = this.resolveType(args[t]);
                if (resolvedTypeArgument == args[t]) continue;
                if (!changed) {
                    args = (Type[])args.clone();
                    changed = true;
                }
                args[t] = resolvedTypeArgument;
            }
            return changed ? Types.newParameterizedTypeWithOwner(newOwnerType, original.getRawType(), args) : original;
        }
        if (toResolve instanceof WildcardType) {
            Type upperBound;
            original = (WildcardType)toResolve;
            Type[] originalLowerBound = original.getLowerBounds();
            Type[] originalUpperBound = original.getUpperBounds();
            if (originalLowerBound.length == 1) {
                Type lowerBound = this.resolveType(originalLowerBound[0]);
                if (lowerBound != originalLowerBound[0]) {
                    return Types.supertypeOf(lowerBound);
                }
            } else if (originalUpperBound.length == 1 && (upperBound = this.resolveType(originalUpperBound[0])) != originalUpperBound[0]) {
                return Types.subtypeOf(upperBound);
            }
            return original;
        }
        return toResolve;
    }

    public TypeLiteral<?> getSupertype(Class<?> supertype) {
        $Preconditions.checkArgument(supertype.isAssignableFrom(this.rawType), "%s is not a supertype of %s", supertype, this.type);
        return this.resolve(MoreTypes.getGenericSupertype(this.type, this.rawType, supertype));
    }

    public TypeLiteral<?> getFieldType(Field field) {
        $Preconditions.checkArgument(field.getDeclaringClass().isAssignableFrom(this.rawType), "%s is not defined by a supertype of %s", field, this.type);
        return this.resolve(field.getGenericType());
    }

    public List<TypeLiteral<?>> getParameterTypes(Member methodOrConstructor) {
        Type[] genericParameterTypes;
        if (methodOrConstructor instanceof Method) {
            Method method = (Method)methodOrConstructor;
            $Preconditions.checkArgument(method.getDeclaringClass().isAssignableFrom(this.rawType), "%s is not defined by a supertype of %s", method, this.type);
            genericParameterTypes = method.getGenericParameterTypes();
        } else if (methodOrConstructor instanceof Constructor) {
            Constructor constructor = (Constructor)methodOrConstructor;
            $Preconditions.checkArgument(constructor.getDeclaringClass().isAssignableFrom(this.rawType), "%s does not construct a supertype of %s", constructor, this.type);
            genericParameterTypes = constructor.getGenericParameterTypes();
        } else {
            throw new IllegalArgumentException("Not a method or a constructor: " + methodOrConstructor);
        }
        return this.resolveAll(genericParameterTypes);
    }

    public List<TypeLiteral<?>> getExceptionTypes(Member methodOrConstructor) {
        Type[] genericExceptionTypes;
        if (methodOrConstructor instanceof Method) {
            Method method = (Method)methodOrConstructor;
            $Preconditions.checkArgument(method.getDeclaringClass().isAssignableFrom(this.rawType), "%s is not defined by a supertype of %s", method, this.type);
            genericExceptionTypes = method.getGenericExceptionTypes();
        } else if (methodOrConstructor instanceof Constructor) {
            Constructor constructor = (Constructor)methodOrConstructor;
            $Preconditions.checkArgument(constructor.getDeclaringClass().isAssignableFrom(this.rawType), "%s does not construct a supertype of %s", constructor, this.type);
            genericExceptionTypes = constructor.getGenericExceptionTypes();
        } else {
            throw new IllegalArgumentException("Not a method or a constructor: " + methodOrConstructor);
        }
        return this.resolveAll(genericExceptionTypes);
    }

    public TypeLiteral<?> getReturnType(Method method) {
        $Preconditions.checkArgument(method.getDeclaringClass().isAssignableFrom(this.rawType), "%s is not defined by a supertype of %s", method, this.type);
        return this.resolve(method.getGenericReturnType());
    }
}

