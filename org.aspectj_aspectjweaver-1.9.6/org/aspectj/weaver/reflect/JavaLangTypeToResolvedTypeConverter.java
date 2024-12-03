/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.reflect;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.HashMap;
import java.util.Map;
import org.aspectj.weaver.BoundedReferenceType;
import org.aspectj.weaver.ReferenceType;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.TypeFactory;
import org.aspectj.weaver.TypeVariable;
import org.aspectj.weaver.TypeVariableReferenceType;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.World;

public class JavaLangTypeToResolvedTypeConverter {
    private Map<Type, TypeVariableReferenceType> typeVariablesInProgress = new HashMap<Type, TypeVariableReferenceType>();
    private final World world;

    public JavaLangTypeToResolvedTypeConverter(World aWorld) {
        this.world = aWorld;
    }

    private World getWorld() {
        return this.world;
    }

    public ResolvedType fromType(Type type) {
        if (type instanceof Class) {
            Class clazz = (Class)type;
            String name = clazz.getName();
            if (clazz.isArray()) {
                UnresolvedType ut = UnresolvedType.forSignature(name.replace('.', '/'));
                return this.getWorld().resolve(ut);
            }
            return this.getWorld().resolve(name);
        }
        if (type instanceof ParameterizedType) {
            Type ownerType = ((ParameterizedType)type).getOwnerType();
            ParameterizedType parameterizedType = (ParameterizedType)type;
            ResolvedType baseType = this.fromType(parameterizedType.getRawType());
            Type[] typeArguments = parameterizedType.getActualTypeArguments();
            if (baseType.isSimpleType() && typeArguments.length == 0 && ownerType != null) {
                return baseType;
            }
            UnresolvedType[] resolvedTypeArguments = this.fromTypes(typeArguments);
            return TypeFactory.createParameterizedType(baseType, resolvedTypeArguments, this.getWorld());
        }
        if (type instanceof java.lang.reflect.TypeVariable) {
            TypeVariableReferenceType inprogressVar = this.typeVariablesInProgress.get(type);
            if (inprogressVar != null) {
                return inprogressVar;
            }
            java.lang.reflect.TypeVariable tv = (java.lang.reflect.TypeVariable)type;
            TypeVariable rt_tv = new TypeVariable(tv.getName());
            TypeVariableReferenceType tvrt = new TypeVariableReferenceType(rt_tv, this.getWorld());
            this.typeVariablesInProgress.put(type, tvrt);
            Type[] bounds = tv.getBounds();
            ResolvedType[] resBounds = this.fromTypes(bounds);
            ResolvedType upperBound = resBounds[0];
            UnresolvedType[] additionalBounds = new ResolvedType[]{};
            if (resBounds.length > 1) {
                additionalBounds = new ResolvedType[resBounds.length - 1];
                System.arraycopy(resBounds, 1, additionalBounds, 0, additionalBounds.length);
            }
            rt_tv.setUpperBound(upperBound);
            rt_tv.setAdditionalInterfaceBounds(additionalBounds);
            this.typeVariablesInProgress.remove(type);
            return tvrt;
        }
        if (type instanceof WildcardType) {
            WildcardType wildType = (WildcardType)type;
            Type[] lowerBounds = wildType.getLowerBounds();
            Type[] upperBounds = wildType.getUpperBounds();
            ResolvedType bound = null;
            boolean isExtends = lowerBounds.length == 0;
            bound = isExtends ? this.fromType(upperBounds[0]) : this.fromType(lowerBounds[0]);
            return new BoundedReferenceType((ReferenceType)bound, isExtends, this.getWorld());
        }
        if (type instanceof GenericArrayType) {
            GenericArrayType genericArrayType = (GenericArrayType)type;
            Type componentType = genericArrayType.getGenericComponentType();
            return UnresolvedType.makeArray(this.fromType(componentType), 1).resolve(this.getWorld());
        }
        return ResolvedType.MISSING;
    }

    public ResolvedType[] fromTypes(Type[] types) {
        ResolvedType[] ret = new ResolvedType[types.length];
        for (int i = 0; i < ret.length; ++i) {
            ret[i] = this.fromType(types[i]);
        }
        return ret;
    }
}

