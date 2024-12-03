/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.type;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.codehaus.jackson.map.type.ArrayType;
import org.codehaus.jackson.map.type.CollectionLikeType;
import org.codehaus.jackson.map.type.CollectionType;
import org.codehaus.jackson.map.type.HierarchicType;
import org.codehaus.jackson.map.type.MapLikeType;
import org.codehaus.jackson.map.type.MapType;
import org.codehaus.jackson.map.type.SimpleType;
import org.codehaus.jackson.map.type.TypeBindings;
import org.codehaus.jackson.map.type.TypeModifier;
import org.codehaus.jackson.map.type.TypeParser;
import org.codehaus.jackson.map.util.ArrayBuilders;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class TypeFactory {
    @Deprecated
    public static final TypeFactory instance = new TypeFactory();
    private static final JavaType[] NO_TYPES = new JavaType[0];
    protected final TypeModifier[] _modifiers;
    protected final TypeParser _parser;
    protected HierarchicType _cachedHashMapType;
    protected HierarchicType _cachedArrayListType;

    private TypeFactory() {
        this._parser = new TypeParser(this);
        this._modifiers = null;
    }

    protected TypeFactory(TypeParser p, TypeModifier[] mods) {
        this._parser = p;
        this._modifiers = mods;
    }

    public TypeFactory withModifier(TypeModifier mod) {
        if (this._modifiers == null) {
            return new TypeFactory(this._parser, new TypeModifier[]{mod});
        }
        return new TypeFactory(this._parser, ArrayBuilders.insertInListNoDup(this._modifiers, mod));
    }

    public static TypeFactory defaultInstance() {
        return instance;
    }

    public static JavaType unknownType() {
        return TypeFactory.defaultInstance()._unknownType();
    }

    public static Class<?> rawClass(Type t) {
        if (t instanceof Class) {
            return (Class)t;
        }
        return TypeFactory.defaultInstance().constructType(t).getRawClass();
    }

    @Deprecated
    public static JavaType type(Type t) {
        return instance._constructType(t, null);
    }

    @Deprecated
    public static JavaType type(Type type, Class<?> context) {
        return instance.constructType(type, context);
    }

    @Deprecated
    public static JavaType type(Type type, JavaType context) {
        return instance.constructType(type, context);
    }

    @Deprecated
    public static JavaType type(Type type, TypeBindings bindings) {
        return instance._constructType(type, bindings);
    }

    @Deprecated
    public static JavaType type(TypeReference<?> ref) {
        return instance.constructType(ref.getType());
    }

    @Deprecated
    public static JavaType arrayType(Class<?> elementType) {
        return instance.constructArrayType(instance.constructType(elementType));
    }

    @Deprecated
    public static JavaType arrayType(JavaType elementType) {
        return instance.constructArrayType(elementType);
    }

    @Deprecated
    public static JavaType collectionType(Class<? extends Collection> collectionType, Class<?> elementType) {
        return instance.constructCollectionType(collectionType, instance.constructType(elementType));
    }

    @Deprecated
    public static JavaType collectionType(Class<? extends Collection> collectionType, JavaType elementType) {
        return instance.constructCollectionType(collectionType, elementType);
    }

    @Deprecated
    public static JavaType mapType(Class<? extends Map> mapClass, Class<?> keyType, Class<?> valueType) {
        return instance.constructMapType(mapClass, TypeFactory.type(keyType), instance.constructType(valueType));
    }

    @Deprecated
    public static JavaType mapType(Class<? extends Map> mapType, JavaType keyType, JavaType valueType) {
        return instance.constructMapType(mapType, keyType, valueType);
    }

    @Deprecated
    public static JavaType parametricType(Class<?> parametrized, Class<?> ... parameterClasses) {
        return instance.constructParametricType(parametrized, parameterClasses);
    }

    @Deprecated
    public static JavaType parametricType(Class<?> parametrized, JavaType ... parameterTypes) {
        return instance.constructParametricType(parametrized, parameterTypes);
    }

    public static JavaType fromCanonical(String canonical) throws IllegalArgumentException {
        return instance.constructFromCanonical(canonical);
    }

    @Deprecated
    public static JavaType specialize(JavaType baseType, Class<?> subclass) {
        return instance.constructSpecializedType(baseType, subclass);
    }

    @Deprecated
    public static JavaType fastSimpleType(Class<?> cls) {
        return instance.uncheckedSimpleType(cls);
    }

    @Deprecated
    public static JavaType[] findParameterTypes(Class<?> clz, Class<?> expType) {
        return instance.findTypeParameters(clz, expType);
    }

    @Deprecated
    public static JavaType[] findParameterTypes(Class<?> clz, Class<?> expType, TypeBindings bindings) {
        return instance.findTypeParameters(clz, expType, bindings);
    }

    @Deprecated
    public static JavaType[] findParameterTypes(JavaType type, Class<?> expType) {
        return instance.findTypeParameters(type, expType);
    }

    @Deprecated
    public static JavaType fromClass(Class<?> clz) {
        return instance._fromClass(clz, null);
    }

    @Deprecated
    public static JavaType fromTypeReference(TypeReference<?> ref) {
        return TypeFactory.type(ref.getType());
    }

    @Deprecated
    public static JavaType fromType(Type type) {
        return instance._constructType(type, null);
    }

    public JavaType constructSpecializedType(JavaType baseType, Class<?> subclass) {
        if (baseType instanceof SimpleType && (subclass.isArray() || Map.class.isAssignableFrom(subclass) || Collection.class.isAssignableFrom(subclass))) {
            if (!baseType.getRawClass().isAssignableFrom(subclass)) {
                throw new IllegalArgumentException("Class " + subclass.getClass().getName() + " not subtype of " + baseType);
            }
            JavaType subtype = this._fromClass(subclass, new TypeBindings(this, baseType.getRawClass()));
            Object h = baseType.getValueHandler();
            if (h != null) {
                subtype = subtype.withValueHandler(h);
            }
            if ((h = baseType.getTypeHandler()) != null) {
                subtype = subtype.withTypeHandler(h);
            }
            return subtype;
        }
        return baseType.narrowBy(subclass);
    }

    public JavaType constructFromCanonical(String canonical) throws IllegalArgumentException {
        return this._parser.parse(canonical);
    }

    public JavaType[] findTypeParameters(JavaType type, Class<?> expType) {
        Class<?> raw = type.getRawClass();
        if (raw == expType) {
            int count = type.containedTypeCount();
            if (count == 0) {
                return null;
            }
            JavaType[] result = new JavaType[count];
            for (int i = 0; i < count; ++i) {
                result[i] = type.containedType(i);
            }
            return result;
        }
        return this.findTypeParameters(raw, expType, new TypeBindings(this, type));
    }

    public JavaType[] findTypeParameters(Class<?> clz, Class<?> expType) {
        return this.findTypeParameters(clz, expType, new TypeBindings(this, clz));
    }

    public JavaType[] findTypeParameters(Class<?> clz, Class<?> expType, TypeBindings bindings) {
        HierarchicType subType = this._findSuperTypeChain(clz, expType);
        if (subType == null) {
            throw new IllegalArgumentException("Class " + clz.getName() + " is not a subtype of " + expType.getName());
        }
        HierarchicType superType = subType;
        while (superType.getSuperType() != null) {
            superType = superType.getSuperType();
            Class<?> raw = superType.getRawClass();
            TypeBindings newBindings = new TypeBindings(this, raw);
            if (superType.isGeneric()) {
                ParameterizedType pt = superType.asGeneric();
                Type[] actualTypes = pt.getActualTypeArguments();
                TypeVariable<Class<?>>[] vars = raw.getTypeParameters();
                int len = actualTypes.length;
                for (int i = 0; i < len; ++i) {
                    String name = vars[i].getName();
                    JavaType type = instance._constructType(actualTypes[i], bindings);
                    newBindings.addBinding(name, type);
                }
            }
            bindings = newBindings;
        }
        if (!superType.isGeneric()) {
            return null;
        }
        return bindings.typesAsArray();
    }

    public JavaType constructType(Type type) {
        return this._constructType(type, null);
    }

    public JavaType constructType(Type type, TypeBindings bindings) {
        return this._constructType(type, bindings);
    }

    public JavaType constructType(TypeReference<?> typeRef) {
        return this._constructType(typeRef.getType(), null);
    }

    public JavaType constructType(Type type, Class<?> context) {
        TypeBindings b = context == null ? null : new TypeBindings(this, context);
        return this._constructType(type, b);
    }

    public JavaType constructType(Type type, JavaType context) {
        TypeBindings b = context == null ? null : new TypeBindings(this, context);
        return this._constructType(type, b);
    }

    public JavaType _constructType(Type type, TypeBindings context) {
        JavaType resultType;
        if (type instanceof Class) {
            Class cls = (Class)type;
            if (context == null) {
                context = new TypeBindings(this, cls);
            }
            resultType = this._fromClass(cls, context);
        } else if (type instanceof ParameterizedType) {
            resultType = this._fromParamType((ParameterizedType)type, context);
        } else if (type instanceof GenericArrayType) {
            resultType = this._fromArrayType((GenericArrayType)type, context);
        } else if (type instanceof TypeVariable) {
            resultType = this._fromVariable((TypeVariable)type, context);
        } else if (type instanceof WildcardType) {
            resultType = this._fromWildcard((WildcardType)type, context);
        } else {
            throw new IllegalArgumentException("Unrecognized Type: " + type.toString());
        }
        if (this._modifiers != null && !resultType.isContainerType()) {
            for (TypeModifier mod : this._modifiers) {
                resultType = mod.modifyType(resultType, type, context, this);
            }
        }
        return resultType;
    }

    public ArrayType constructArrayType(Class<?> elementType) {
        return ArrayType.construct(this._constructType(elementType, null), null, null);
    }

    public ArrayType constructArrayType(JavaType elementType) {
        return ArrayType.construct(elementType, null, null);
    }

    public CollectionType constructCollectionType(Class<? extends Collection> collectionClass, Class<?> elementClass) {
        return CollectionType.construct(collectionClass, this.constructType(elementClass));
    }

    public CollectionType constructCollectionType(Class<? extends Collection> collectionClass, JavaType elementType) {
        return CollectionType.construct(collectionClass, elementType);
    }

    public CollectionLikeType constructCollectionLikeType(Class<?> collectionClass, Class<?> elementClass) {
        return CollectionLikeType.construct(collectionClass, this.constructType(elementClass));
    }

    public CollectionLikeType constructCollectionLikeType(Class<?> collectionClass, JavaType elementType) {
        return CollectionLikeType.construct(collectionClass, elementType);
    }

    public MapType constructMapType(Class<? extends Map> mapClass, JavaType keyType, JavaType valueType) {
        return MapType.construct(mapClass, keyType, valueType);
    }

    public MapType constructMapType(Class<? extends Map> mapClass, Class<?> keyClass, Class<?> valueClass) {
        return MapType.construct(mapClass, this.constructType(keyClass), this.constructType(valueClass));
    }

    public MapLikeType constructMapLikeType(Class<?> mapClass, JavaType keyType, JavaType valueType) {
        return MapLikeType.construct(mapClass, keyType, valueType);
    }

    public MapLikeType constructMapLikeType(Class<?> mapClass, Class<?> keyClass, Class<?> valueClass) {
        return MapType.construct(mapClass, this.constructType(keyClass), this.constructType(valueClass));
    }

    public JavaType constructSimpleType(Class<?> rawType, JavaType[] parameterTypes) {
        TypeVariable<Class<?>>[] typeVars = rawType.getTypeParameters();
        if (typeVars.length != parameterTypes.length) {
            throw new IllegalArgumentException("Parameter type mismatch for " + rawType.getName() + ": expected " + typeVars.length + " parameters, was given " + parameterTypes.length);
        }
        String[] names = new String[typeVars.length];
        int len = typeVars.length;
        for (int i = 0; i < len; ++i) {
            names[i] = typeVars[i].getName();
        }
        SimpleType resultType = new SimpleType(rawType, names, parameterTypes, null, null);
        return resultType;
    }

    public JavaType uncheckedSimpleType(Class<?> cls) {
        return new SimpleType(cls);
    }

    public JavaType constructParametricType(Class<?> parametrized, Class<?> ... parameterClasses) {
        int len = parameterClasses.length;
        JavaType[] pt = new JavaType[len];
        for (int i = 0; i < len; ++i) {
            pt[i] = this._fromClass(parameterClasses[i], null);
        }
        return this.constructParametricType(parametrized, pt);
    }

    public JavaType constructParametricType(Class<?> parametrized, JavaType ... parameterTypes) {
        JavaType resultType;
        if (parametrized.isArray()) {
            if (parameterTypes.length != 1) {
                throw new IllegalArgumentException("Need exactly 1 parameter type for arrays (" + parametrized.getName() + ")");
            }
            resultType = this.constructArrayType(parameterTypes[0]);
        } else if (Map.class.isAssignableFrom(parametrized)) {
            if (parameterTypes.length != 2) {
                throw new IllegalArgumentException("Need exactly 2 parameter types for Map types (" + parametrized.getName() + ")");
            }
            resultType = this.constructMapType(parametrized, parameterTypes[0], parameterTypes[1]);
        } else if (Collection.class.isAssignableFrom(parametrized)) {
            if (parameterTypes.length != 1) {
                throw new IllegalArgumentException("Need exactly 1 parameter type for Collection types (" + parametrized.getName() + ")");
            }
            resultType = this.constructCollectionType(parametrized, parameterTypes[0]);
        } else {
            resultType = this.constructSimpleType(parametrized, parameterTypes);
        }
        return resultType;
    }

    public CollectionType constructRawCollectionType(Class<? extends Collection> collectionClass) {
        return CollectionType.construct(collectionClass, TypeFactory.unknownType());
    }

    public CollectionLikeType constructRawCollectionLikeType(Class<?> collectionClass) {
        return CollectionLikeType.construct(collectionClass, TypeFactory.unknownType());
    }

    public MapType constructRawMapType(Class<? extends Map> mapClass) {
        return MapType.construct(mapClass, TypeFactory.unknownType(), TypeFactory.unknownType());
    }

    public MapLikeType constructRawMapLikeType(Class<?> mapClass) {
        return MapLikeType.construct(mapClass, TypeFactory.unknownType(), TypeFactory.unknownType());
    }

    protected JavaType _fromClass(Class<?> clz, TypeBindings context) {
        if (clz.isArray()) {
            return ArrayType.construct(this._constructType(clz.getComponentType(), null), null, null);
        }
        if (clz.isEnum()) {
            return new SimpleType(clz);
        }
        if (Map.class.isAssignableFrom(clz)) {
            return this._mapType(clz);
        }
        if (Collection.class.isAssignableFrom(clz)) {
            return this._collectionType(clz);
        }
        return new SimpleType(clz);
    }

    protected JavaType _fromParameterizedClass(Class<?> clz, List<JavaType> paramTypes) {
        if (clz.isArray()) {
            return ArrayType.construct(this._constructType(clz.getComponentType(), null), null, null);
        }
        if (clz.isEnum()) {
            return new SimpleType(clz);
        }
        if (Map.class.isAssignableFrom(clz)) {
            if (paramTypes.size() > 0) {
                JavaType keyType = paramTypes.get(0);
                JavaType contentType = paramTypes.size() >= 2 ? paramTypes.get(1) : this._unknownType();
                return MapType.construct(clz, keyType, contentType);
            }
            return this._mapType(clz);
        }
        if (Collection.class.isAssignableFrom(clz)) {
            if (paramTypes.size() >= 1) {
                return CollectionType.construct(clz, paramTypes.get(0));
            }
            return this._collectionType(clz);
        }
        if (paramTypes.size() == 0) {
            return new SimpleType(clz);
        }
        JavaType[] pt = paramTypes.toArray(new JavaType[paramTypes.size()]);
        return this.constructSimpleType(clz, pt);
    }

    protected JavaType _fromParamType(ParameterizedType type, TypeBindings context) {
        JavaType[] pt;
        int paramCount;
        Class rawType = (Class)type.getRawType();
        Type[] args = type.getActualTypeArguments();
        int n = paramCount = args == null ? 0 : args.length;
        if (paramCount == 0) {
            pt = NO_TYPES;
        } else {
            pt = new JavaType[paramCount];
            for (int i = 0; i < paramCount; ++i) {
                pt[i] = this._constructType(args[i], context);
            }
        }
        if (Map.class.isAssignableFrom(rawType)) {
            JavaType subtype = this.constructSimpleType(rawType, pt);
            JavaType[] mapParams = this.findTypeParameters(subtype, Map.class);
            if (mapParams.length != 2) {
                throw new IllegalArgumentException("Could not find 2 type parameters for Map class " + rawType.getName() + " (found " + mapParams.length + ")");
            }
            return MapType.construct(rawType, mapParams[0], mapParams[1]);
        }
        if (Collection.class.isAssignableFrom(rawType)) {
            JavaType subtype = this.constructSimpleType(rawType, pt);
            JavaType[] collectionParams = this.findTypeParameters(subtype, Collection.class);
            if (collectionParams.length != 1) {
                throw new IllegalArgumentException("Could not find 1 type parameter for Collection class " + rawType.getName() + " (found " + collectionParams.length + ")");
            }
            return CollectionType.construct(rawType, collectionParams[0]);
        }
        if (paramCount == 0) {
            return new SimpleType(rawType);
        }
        return this.constructSimpleType(rawType, pt);
    }

    protected JavaType _fromArrayType(GenericArrayType type, TypeBindings context) {
        JavaType compType = this._constructType(type.getGenericComponentType(), context);
        return ArrayType.construct(compType, null, null);
    }

    protected JavaType _fromVariable(TypeVariable<?> type, TypeBindings context) {
        if (context == null) {
            return this._unknownType();
        }
        String name = type.getName();
        JavaType actualType = context.findType(name);
        if (actualType != null) {
            return actualType;
        }
        Type[] bounds = type.getBounds();
        context._addPlaceholder(name);
        return this._constructType(bounds[0], context);
    }

    protected JavaType _fromWildcard(WildcardType type, TypeBindings context) {
        return this._constructType(type.getUpperBounds()[0], context);
    }

    private JavaType _mapType(Class<?> rawClass) {
        JavaType[] typeParams = this.findTypeParameters(rawClass, Map.class);
        if (typeParams == null) {
            return MapType.construct(rawClass, this._unknownType(), this._unknownType());
        }
        if (typeParams.length != 2) {
            throw new IllegalArgumentException("Strange Map type " + rawClass.getName() + ": can not determine type parameters");
        }
        return MapType.construct(rawClass, typeParams[0], typeParams[1]);
    }

    private JavaType _collectionType(Class<?> rawClass) {
        JavaType[] typeParams = this.findTypeParameters(rawClass, Collection.class);
        if (typeParams == null) {
            return CollectionType.construct(rawClass, this._unknownType());
        }
        if (typeParams.length != 1) {
            throw new IllegalArgumentException("Strange Collection type " + rawClass.getName() + ": can not determine type parameters");
        }
        return CollectionType.construct(rawClass, typeParams[0]);
    }

    protected JavaType _resolveVariableViaSubTypes(HierarchicType leafType, String variableName, TypeBindings bindings) {
        if (leafType != null && leafType.isGeneric()) {
            TypeVariable<Class<?>>[] typeVariables = leafType.getRawClass().getTypeParameters();
            int len = typeVariables.length;
            for (int i = 0; i < len; ++i) {
                TypeVariable<Class<?>> tv = typeVariables[i];
                if (!variableName.equals(tv.getName())) continue;
                Type type = leafType.asGeneric().getActualTypeArguments()[i];
                if (type instanceof TypeVariable) {
                    return this._resolveVariableViaSubTypes(leafType.getSubType(), ((TypeVariable)type).getName(), bindings);
                }
                return this._constructType(type, bindings);
            }
        }
        return this._unknownType();
    }

    protected JavaType _unknownType() {
        return new SimpleType(Object.class);
    }

    protected HierarchicType _findSuperTypeChain(Class<?> subtype, Class<?> supertype) {
        if (supertype.isInterface()) {
            return this._findSuperInterfaceChain(subtype, supertype);
        }
        return this._findSuperClassChain(subtype, supertype);
    }

    protected HierarchicType _findSuperClassChain(Type currentType, Class<?> target) {
        HierarchicType sup;
        HierarchicType current = new HierarchicType(currentType);
        Class<?> raw = current.getRawClass();
        if (raw == target) {
            return current;
        }
        Type parent = raw.getGenericSuperclass();
        if (parent != null && (sup = this._findSuperClassChain(parent, target)) != null) {
            sup.setSubType(current);
            current.setSuperType(sup);
            return current;
        }
        return null;
    }

    protected HierarchicType _findSuperInterfaceChain(Type currentType, Class<?> target) {
        HierarchicType current = new HierarchicType(currentType);
        Class<?> raw = current.getRawClass();
        if (raw == target) {
            return new HierarchicType(currentType);
        }
        if (raw == HashMap.class && target == Map.class) {
            return this._hashMapSuperInterfaceChain(current);
        }
        if (raw == ArrayList.class && target == List.class) {
            return this._arrayListSuperInterfaceChain(current);
        }
        return this._doFindSuperInterfaceChain(current, target);
    }

    protected HierarchicType _doFindSuperInterfaceChain(HierarchicType current, Class<?> target) {
        HierarchicType sup;
        Type parent;
        Class<?> raw = current.getRawClass();
        Type[] parents = raw.getGenericInterfaces();
        if (parents != null) {
            for (Type parent2 : parents) {
                HierarchicType sup2 = this._findSuperInterfaceChain(parent2, target);
                if (sup2 == null) continue;
                sup2.setSubType(current);
                current.setSuperType(sup2);
                return current;
            }
        }
        if ((parent = raw.getGenericSuperclass()) != null && (sup = this._findSuperInterfaceChain(parent, target)) != null) {
            sup.setSubType(current);
            current.setSuperType(sup);
            return current;
        }
        return null;
    }

    protected synchronized HierarchicType _hashMapSuperInterfaceChain(HierarchicType current) {
        if (this._cachedHashMapType == null) {
            HierarchicType base = current.deepCloneWithoutSubtype();
            this._doFindSuperInterfaceChain(base, Map.class);
            this._cachedHashMapType = base.getSuperType();
        }
        HierarchicType t = this._cachedHashMapType.deepCloneWithoutSubtype();
        current.setSuperType(t);
        t.setSubType(current);
        return current;
    }

    protected synchronized HierarchicType _arrayListSuperInterfaceChain(HierarchicType current) {
        if (this._cachedArrayListType == null) {
            HierarchicType base = current.deepCloneWithoutSubtype();
            this._doFindSuperInterfaceChain(base, List.class);
            this._cachedArrayListType = base.getSuperType();
        }
        HierarchicType t = this._cachedArrayListType.deepCloneWithoutSubtype();
        current.setSuperType(t);
        t.setSubType(current);
        return current;
    }
}

