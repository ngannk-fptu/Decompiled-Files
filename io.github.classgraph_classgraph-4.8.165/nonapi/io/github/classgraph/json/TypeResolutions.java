/*
 * Decompiled with CFR 0.152.
 */
package nonapi.io.github.classgraph.json;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import nonapi.io.github.classgraph.json.ParameterizedTypeImpl;

class TypeResolutions {
    private final TypeVariable<?>[] typeVariables;
    Type[] resolvedTypeArguments;

    TypeResolutions(ParameterizedType resolvedType) {
        this.typeVariables = ((Class)resolvedType.getRawType()).getTypeParameters();
        this.resolvedTypeArguments = resolvedType.getActualTypeArguments();
        if (this.resolvedTypeArguments.length != this.typeVariables.length) {
            throw new IllegalArgumentException("Type parameter count mismatch");
        }
    }

    Type resolveTypeVariables(Type type) {
        if (type instanceof Class) {
            return type;
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType)type;
            Type[] typeArgs = parameterizedType.getActualTypeArguments();
            Type[] typeArgsResolved = null;
            for (int i = 0; i < typeArgs.length; ++i) {
                Type typeArgResolved = this.resolveTypeVariables(typeArgs[i]);
                if (typeArgsResolved == null) {
                    if (typeArgResolved.equals(typeArgs[i])) continue;
                    typeArgsResolved = new Type[typeArgs.length];
                    System.arraycopy(typeArgs, 0, typeArgsResolved, 0, i);
                    typeArgsResolved[i] = typeArgResolved;
                    continue;
                }
                typeArgsResolved[i] = typeArgResolved;
            }
            if (typeArgsResolved == null) {
                return type;
            }
            return new ParameterizedTypeImpl((Class)parameterizedType.getRawType(), typeArgsResolved, parameterizedType.getOwnerType());
        }
        if (type instanceof TypeVariable) {
            TypeVariable typeVariable = (TypeVariable)type;
            for (int i = 0; i < this.typeVariables.length; ++i) {
                if (!this.typeVariables[i].getName().equals(typeVariable.getName())) continue;
                return this.resolvedTypeArguments[i];
            }
            return type;
        }
        if (type instanceof GenericArrayType) {
            int numArrayDims = 0;
            Type t = type;
            while (t instanceof GenericArrayType) {
                ++numArrayDims;
                t = ((GenericArrayType)t).getGenericComponentType();
            }
            Type innermostType = t;
            Type innermostTypeResolved = this.resolveTypeVariables(innermostType);
            if (!(innermostTypeResolved instanceof Class)) {
                throw new IllegalArgumentException("Could not resolve generic array type " + type);
            }
            Class innermostTypeResolvedClass = (Class)innermostTypeResolved;
            int[] dims = (int[])Array.newInstance(Integer.TYPE, numArrayDims);
            Object arrayInstance = Array.newInstance(innermostTypeResolvedClass, dims);
            return arrayInstance.getClass();
        }
        if (type instanceof WildcardType) {
            throw new RuntimeException("WildcardType not yet supported: " + type);
        }
        throw new RuntimeException("Got unexpected type: " + type);
    }

    public String toString() {
        if (this.typeVariables.length == 0) {
            return "{ }";
        }
        StringBuilder buf = new StringBuilder();
        buf.append("{ ");
        for (int i = 0; i < this.typeVariables.length; ++i) {
            if (i > 0) {
                buf.append(", ");
            }
            buf.append(this.typeVariables[i]).append(" => ").append(this.resolvedTypeArguments[i]);
        }
        buf.append(" }");
        return buf.toString();
    }
}

