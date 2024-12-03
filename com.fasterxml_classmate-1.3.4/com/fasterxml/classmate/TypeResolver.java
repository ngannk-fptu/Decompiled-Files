/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.classmate;

import com.fasterxml.classmate.GenericType;
import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeBindings;
import com.fasterxml.classmate.types.ResolvedArrayType;
import com.fasterxml.classmate.types.ResolvedInterfaceType;
import com.fasterxml.classmate.types.ResolvedObjectType;
import com.fasterxml.classmate.types.ResolvedPrimitiveType;
import com.fasterxml.classmate.types.ResolvedRecursiveType;
import com.fasterxml.classmate.types.TypePlaceHolder;
import com.fasterxml.classmate.util.ClassKey;
import com.fasterxml.classmate.util.ClassStack;
import com.fasterxml.classmate.util.ResolvedTypeCache;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.HashMap;
import java.util.List;

public class TypeResolver
implements Serializable {
    private static final ResolvedType[] NO_TYPES = new ResolvedType[0];
    private static final ResolvedObjectType sJavaLangObject = ResolvedObjectType.create(Object.class, null, null, null);
    protected static final HashMap<ClassKey, ResolvedType> _primitiveTypes = new HashMap(16);
    protected final ResolvedTypeCache _resolvedTypes = new ResolvedTypeCache(200);

    public ResolvedType resolve(Type type, Type ... typeParameters) {
        ResolvedType rt;
        Class<?> rawBase;
        TypeBindings bindings;
        boolean noParams;
        boolean bl = noParams = typeParameters == null || typeParameters.length == 0;
        if (type instanceof Class) {
            bindings = TypeBindings.emptyBindings();
            if (noParams) {
                return this._fromClass(null, (Class)type, bindings);
            }
            rawBase = (Class<?>)type;
        } else if (type instanceof GenericType) {
            bindings = TypeBindings.emptyBindings();
            if (noParams) {
                return this._fromGenericType(null, (GenericType)type, bindings);
            }
            rt = this._fromAny(null, type, bindings);
            rawBase = rt.getErasedType();
        } else if (type instanceof ResolvedType) {
            rt = (ResolvedType)type;
            if (noParams) {
                return rt;
            }
            bindings = rt.getTypeBindings();
            rawBase = rt.getErasedType();
        } else {
            bindings = TypeBindings.emptyBindings();
            if (noParams) {
                return this.resolve(bindings, type);
            }
            rt = this._fromAny(null, type, bindings);
            rawBase = rt.getErasedType();
        }
        int len = typeParameters.length;
        ResolvedType[] resolvedParams = new ResolvedType[len];
        for (int i = 0; i < len; ++i) {
            resolvedParams[i] = this._fromAny(null, typeParameters[i], bindings);
        }
        return this._fromClass(null, rawBase, TypeBindings.create(rawBase, resolvedParams));
    }

    public ResolvedArrayType arrayType(Type elementType) {
        ResolvedType resolvedElementType = this.resolve(TypeBindings.emptyBindings(), elementType);
        Object emptyArray = Array.newInstance(resolvedElementType.getErasedType(), 0);
        return new ResolvedArrayType(emptyArray.getClass(), TypeBindings.emptyBindings(), resolvedElementType);
    }

    public ResolvedType resolve(TypeBindings typeBindings, Type jdkType) {
        return this._fromAny(null, jdkType, typeBindings);
    }

    public ResolvedType resolveSubtype(ResolvedType supertype, Class<?> subtype) throws IllegalArgumentException, UnsupportedOperationException {
        int i;
        ResolvedType resolvedSubtype;
        TypePlaceHolder[] placeholders;
        Class<?> superclass;
        ResolvedType refType = supertype.getSelfReferencedType();
        if (refType != null) {
            supertype = refType;
        }
        if ((superclass = supertype.getErasedType()) == subtype) {
            return supertype;
        }
        if (!supertype.canCreateSubtypes()) {
            throw new UnsupportedOperationException("Can not subtype primitive or array types (type " + supertype.getFullDescription() + ")");
        }
        if (!superclass.isAssignableFrom(subtype)) {
            throw new IllegalArgumentException("Can not sub-class " + supertype.getBriefDescription() + " into " + subtype.getName());
        }
        int paramCount = subtype.getTypeParameters().length;
        if (paramCount == 0) {
            placeholders = null;
            resolvedSubtype = this._fromClass(null, subtype, TypeBindings.emptyBindings());
        } else {
            placeholders = new TypePlaceHolder[paramCount];
            for (int i2 = 0; i2 < paramCount; ++i2) {
                placeholders[i2] = new TypePlaceHolder(i2);
            }
            ResolvedType[] resolvedParams = new ResolvedType[paramCount];
            TypeBindings bindings = TypeBindings.emptyBindings();
            for (i = 0; i < paramCount; ++i) {
                resolvedParams[i] = this._fromAny(null, placeholders[i], bindings);
            }
            resolvedSubtype = this._fromClass(null, subtype, TypeBindings.create(subtype, resolvedParams));
        }
        ResolvedType resolvedSupertype = resolvedSubtype.findSupertype(superclass);
        if (resolvedSupertype == null) {
            throw new IllegalArgumentException("Internal error: unable to locate supertype (" + subtype.getName() + ") for type " + supertype.getBriefDescription());
        }
        this._resolveTypePlaceholders(supertype, resolvedSupertype);
        if (paramCount == 0) {
            return resolvedSubtype;
        }
        Type[] typeParams = new ResolvedType[paramCount];
        for (i = 0; i < paramCount; ++i) {
            ResolvedType t = placeholders[i].actualType();
            if (t == null) {
                throw new IllegalArgumentException("Failed to find type parameter #" + (i + 1) + "/" + paramCount + " for " + subtype.getName());
            }
            typeParams[i] = t;
        }
        return this.resolve(subtype, typeParams);
    }

    public static boolean isSelfReference(ResolvedType type) {
        return type instanceof ResolvedRecursiveType;
    }

    private ResolvedType _fromAny(ClassStack context, Type mainType, TypeBindings typeBindings) {
        if (mainType instanceof Class) {
            return this._fromClass(context, (Class)mainType, typeBindings);
        }
        if (mainType instanceof ParameterizedType) {
            return this._fromParamType(context, (ParameterizedType)mainType, typeBindings);
        }
        if (mainType instanceof ResolvedType) {
            return (ResolvedType)mainType;
        }
        if (mainType instanceof GenericType) {
            return this._fromGenericType(context, (GenericType)mainType, typeBindings);
        }
        if (mainType instanceof GenericArrayType) {
            return this._fromArrayType(context, (GenericArrayType)mainType, typeBindings);
        }
        if (mainType instanceof TypeVariable) {
            return this._fromVariable(context, (TypeVariable)mainType, typeBindings);
        }
        if (mainType instanceof WildcardType) {
            return this._fromWildcard(context, (WildcardType)mainType, typeBindings);
        }
        throw new IllegalArgumentException("Unrecognized type class: " + mainType.getClass().getName());
    }

    private ResolvedType _fromClass(ClassStack context, Class<?> rawType, TypeBindings typeBindings) {
        ResolvedType type = _primitiveTypes.get(new ClassKey(rawType));
        if (type != null) {
            return type;
        }
        if (context == null) {
            context = new ClassStack(rawType);
        } else {
            ClassStack prev = context.find(rawType);
            if (prev != null) {
                ResolvedRecursiveType selfRef = new ResolvedRecursiveType(rawType, typeBindings);
                prev.addSelfReference(selfRef);
                return selfRef;
            }
            context = context.child(rawType);
        }
        ResolvedType[] typeParameters = typeBindings.typeParameterArray();
        ResolvedTypeCache.Key key = this._resolvedTypes.key(rawType, typeParameters);
        if (key == null) {
            type = this._constructType(context, rawType, typeBindings);
        } else {
            type = this._resolvedTypes.find(key);
            if (type == null) {
                type = this._constructType(context, rawType, typeBindings);
                this._resolvedTypes.put(key, type);
            }
        }
        context.resolveSelfReferences(type);
        return type;
    }

    private ResolvedType _fromGenericType(ClassStack context, GenericType<?> generic, TypeBindings typeBindings) {
        ResolvedType type = this._fromClass(context, generic.getClass(), typeBindings);
        ResolvedType genType = type.findSupertype(GenericType.class);
        if (genType == null) {
            throw new IllegalArgumentException("Unparameterized GenericType instance (" + generic.getClass().getName() + ")");
        }
        TypeBindings b = genType.getTypeBindings();
        ResolvedType[] params = b.typeParameterArray();
        if (params.length == 0) {
            throw new IllegalArgumentException("Unparameterized GenericType instance (" + generic.getClass().getName() + ")");
        }
        return params[0];
    }

    private ResolvedType _constructType(ClassStack context, Class<?> rawType, TypeBindings typeBindings) {
        if (rawType.isArray()) {
            ResolvedType elementType = this._fromAny(context, rawType.getComponentType(), typeBindings);
            return new ResolvedArrayType(rawType, typeBindings, elementType);
        }
        if (!typeBindings.isEmpty() && rawType.getTypeParameters().length == 0) {
            typeBindings = TypeBindings.emptyBindings();
        }
        if (rawType.isInterface()) {
            return new ResolvedInterfaceType(rawType, typeBindings, this._resolveSuperInterfaces(context, rawType, typeBindings));
        }
        return new ResolvedObjectType(rawType, typeBindings, this._resolveSuperClass(context, rawType, typeBindings), this._resolveSuperInterfaces(context, rawType, typeBindings));
    }

    private ResolvedType[] _resolveSuperInterfaces(ClassStack context, Class<?> rawType, TypeBindings typeBindings) {
        Type[] types = rawType.getGenericInterfaces();
        if (types == null || types.length == 0) {
            return NO_TYPES;
        }
        int len = types.length;
        ResolvedType[] resolved = new ResolvedType[len];
        for (int i = 0; i < len; ++i) {
            resolved[i] = this._fromAny(context, types[i], typeBindings);
        }
        return resolved;
    }

    private ResolvedType _resolveSuperClass(ClassStack context, Class<?> rawType, TypeBindings typeBindings) {
        Type parent = rawType.getGenericSuperclass();
        if (parent == null) {
            return null;
        }
        return this._fromAny(context, parent, typeBindings);
    }

    private ResolvedType _fromParamType(ClassStack context, ParameterizedType ptype, TypeBindings parentBindings) {
        Class rawType = (Class)ptype.getRawType();
        Type[] params = ptype.getActualTypeArguments();
        int len = params.length;
        ResolvedType[] types = new ResolvedType[len];
        for (int i = 0; i < len; ++i) {
            types[i] = this._fromAny(context, params[i], parentBindings);
        }
        TypeBindings newBindings = TypeBindings.create(rawType, types);
        return this._fromClass(context, rawType, newBindings);
    }

    private ResolvedType _fromArrayType(ClassStack context, GenericArrayType arrayType, TypeBindings typeBindings) {
        ResolvedType elementType = this._fromAny(context, arrayType.getGenericComponentType(), typeBindings);
        Object emptyArray = Array.newInstance(elementType.getErasedType(), 0);
        return new ResolvedArrayType(emptyArray.getClass(), typeBindings, elementType);
    }

    private ResolvedType _fromWildcard(ClassStack context, WildcardType wildType, TypeBindings typeBindings) {
        return this._fromAny(context, wildType.getUpperBounds()[0], typeBindings);
    }

    private ResolvedType _fromVariable(ClassStack context, TypeVariable<?> variable, TypeBindings typeBindings) {
        String name = variable.getName();
        ResolvedType type = typeBindings.findBoundType(name);
        if (type != null) {
            return type;
        }
        if (typeBindings.hasUnbound(name)) {
            return sJavaLangObject;
        }
        typeBindings = typeBindings.withUnboundVariable(name);
        Type[] bounds = variable.getBounds();
        return this._fromAny(context, bounds[0], typeBindings);
    }

    private void _resolveTypePlaceholders(ResolvedType sourceType, ResolvedType actualType) throws IllegalArgumentException {
        List<ResolvedType> expectedTypes = sourceType.getTypeParameters();
        List<ResolvedType> actualTypes = actualType.getTypeParameters();
        int len = expectedTypes.size();
        for (int i = 0; i < len; ++i) {
            ResolvedType act;
            ResolvedType exp = expectedTypes.get(i);
            if (this._verifyAndResolve(exp, act = actualTypes.get(i))) continue;
            throw new IllegalArgumentException("Type parameter #" + (i + 1) + "/" + len + " differs; expected " + exp.getBriefDescription() + ", got " + act.getBriefDescription());
        }
    }

    private boolean _verifyAndResolve(ResolvedType exp, ResolvedType act) {
        if (act instanceof TypePlaceHolder) {
            ((TypePlaceHolder)act).actualType(exp);
            return true;
        }
        if (exp.getErasedType() != act.getErasedType()) {
            return false;
        }
        List<ResolvedType> expectedTypes = exp.getTypeParameters();
        List<ResolvedType> actualTypes = act.getTypeParameters();
        int len = expectedTypes.size();
        for (int i = 0; i < len; ++i) {
            ResolvedType act2;
            ResolvedType exp2 = expectedTypes.get(i);
            if (this._verifyAndResolve(exp2, act2 = actualTypes.get(i))) continue;
            return false;
        }
        return true;
    }

    static {
        for (ResolvedPrimitiveType type : ResolvedPrimitiveType.all()) {
            _primitiveTypes.put(new ClassKey(type.getErasedType()), type);
        }
        _primitiveTypes.put(new ClassKey(Void.TYPE), ResolvedPrimitiveType.voidType());
        _primitiveTypes.put(new ClassKey(Object.class), sJavaLangObject);
    }
}

