/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.ConstraintValidator
 */
package org.hibernate.validator.internal.util;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.MalformedParameterizedTypeException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.validation.ConstraintValidator;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorDescriptor;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.Contracts;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

public final class TypeHelper {
    private static final Map<Class<?>, Set<Class<?>>> SUBTYPES_BY_PRIMITIVE;
    private static final int CONSTRAINT_TYPE_INDEX = 0;
    private static final int VALIDATOR_TYPE_INDEX = 1;
    private static final Log LOG;

    private TypeHelper() {
        throw new AssertionError();
    }

    public static boolean isAssignable(Type supertype, Type type) {
        Contracts.assertNotNull(supertype, "supertype");
        Contracts.assertNotNull(type, "type");
        if (supertype.equals(type)) {
            return true;
        }
        if (supertype instanceof Class) {
            if (type instanceof Class) {
                return TypeHelper.isClassAssignable((Class)supertype, (Class)type);
            }
            if (type instanceof ParameterizedType) {
                return TypeHelper.isAssignable(supertype, ((ParameterizedType)type).getRawType());
            }
            if (type instanceof TypeVariable) {
                return TypeHelper.isTypeVariableAssignable(supertype, (TypeVariable)type);
            }
            if (type instanceof GenericArrayType) {
                if (((Class)supertype).isArray()) {
                    return TypeHelper.isAssignable(TypeHelper.getComponentType(supertype), TypeHelper.getComponentType(type));
                }
                return TypeHelper.isArraySupertype((Class)supertype);
            }
            if (type instanceof WildcardType) {
                return TypeHelper.isClassAssignableToWildcardType((Class)supertype, (WildcardType)type);
            }
            return false;
        }
        if (supertype instanceof ParameterizedType) {
            if (type instanceof Class) {
                return TypeHelper.isSuperAssignable(supertype, type);
            }
            if (type instanceof ParameterizedType) {
                return TypeHelper.isParameterizedTypeAssignable((ParameterizedType)supertype, (ParameterizedType)type);
            }
            return false;
        }
        if (type instanceof TypeVariable) {
            return TypeHelper.isTypeVariableAssignable(supertype, (TypeVariable)type);
        }
        if (supertype instanceof GenericArrayType) {
            if (TypeHelper.isArray(type)) {
                return TypeHelper.isAssignable(TypeHelper.getComponentType(supertype), TypeHelper.getComponentType(type));
            }
            return false;
        }
        if (supertype instanceof WildcardType) {
            return TypeHelper.isWildcardTypeAssignable((WildcardType)supertype, type);
        }
        return false;
    }

    public static Type getErasedType(Type type) {
        if (type instanceof ParameterizedType) {
            Type rawType = ((ParameterizedType)type).getRawType();
            return TypeHelper.getErasedType(rawType);
        }
        if (TypeHelper.isArray(type)) {
            Type componentType = TypeHelper.getComponentType(type);
            Type erasedComponentType = TypeHelper.getErasedType(componentType);
            return TypeHelper.getArrayType(erasedComponentType);
        }
        if (type instanceof TypeVariable) {
            Type[] bounds = ((TypeVariable)type).getBounds();
            return TypeHelper.getErasedType(bounds[0]);
        }
        if (type instanceof WildcardType) {
            Type[] upperBounds = ((WildcardType)type).getUpperBounds();
            return TypeHelper.getErasedType(upperBounds[0]);
        }
        return type;
    }

    public static Class<?> getErasedReferenceType(Type type) {
        Contracts.assertTrue(TypeHelper.isReferenceType(type), "type is not a reference type: %s", type);
        return (Class)TypeHelper.getErasedType(type);
    }

    public static boolean isArray(Type type) {
        return type instanceof Class && ((Class)type).isArray() || type instanceof GenericArrayType;
    }

    public static Type getComponentType(Type type) {
        if (type instanceof Class) {
            Class klass = (Class)type;
            return klass.isArray() ? klass.getComponentType() : null;
        }
        if (type instanceof GenericArrayType) {
            return ((GenericArrayType)type).getGenericComponentType();
        }
        return null;
    }

    private static Type getArrayType(Type componentType) {
        Contracts.assertNotNull(componentType, "componentType");
        if (componentType instanceof Class) {
            return Array.newInstance((Class)componentType, 0).getClass();
        }
        return TypeHelper.genericArrayType(componentType);
    }

    public static GenericArrayType genericArrayType(final Type componentType) {
        return new GenericArrayType(){

            @Override
            public Type getGenericComponentType() {
                return componentType;
            }
        };
    }

    public static boolean isInstance(Type type, Object object) {
        return TypeHelper.getErasedReferenceType(type).isInstance(object);
    }

    public static ParameterizedType parameterizedType(final Class<?> rawType, final Type ... actualTypeArguments) {
        return new ParameterizedType(){

            @Override
            public Type[] getActualTypeArguments() {
                return actualTypeArguments;
            }

            @Override
            public Type getRawType() {
                return rawType;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }
        };
    }

    private static Type getResolvedSuperclass(Type type) {
        Contracts.assertNotNull(type, "type");
        Class<?> rawType = TypeHelper.getErasedReferenceType(type);
        Type supertype = rawType.getGenericSuperclass();
        if (supertype == null) {
            return null;
        }
        return TypeHelper.resolveTypeVariables(supertype, type);
    }

    private static Type[] getResolvedInterfaces(Type type) {
        Contracts.assertNotNull(type, "type");
        Class<?> rawType = TypeHelper.getErasedReferenceType(type);
        Type[] interfaces = rawType.getGenericInterfaces();
        Type[] resolvedInterfaces = new Type[interfaces.length];
        for (int i = 0; i < interfaces.length; ++i) {
            resolvedInterfaces[i] = TypeHelper.resolveTypeVariables(interfaces[i], type);
        }
        return resolvedInterfaces;
    }

    public static <A extends Annotation> Map<Type, ConstraintValidatorDescriptor<A>> getValidatorTypes(Class<A> annotationType, List<ConstraintValidatorDescriptor<A>> validators) {
        HashMap<Type, ConstraintValidatorDescriptor<A>> validatorsTypes = CollectionHelper.newHashMap();
        for (ConstraintValidatorDescriptor<A> validator : validators) {
            Type type = validator.getValidatedType();
            ConstraintValidatorDescriptor<A> previous = validatorsTypes.put(type, validator);
            if (previous == null) continue;
            throw LOG.getMultipleValidatorsForSameTypeException(annotationType, type, previous.getValidatorClass(), validator.getValidatorClass());
        }
        return validatorsTypes;
    }

    public static Type extractValidatedType(Class<? extends ConstraintValidator<?, ?>> validator) {
        return TypeHelper.extractConstraintValidatorTypeArgumentType(validator, 1);
    }

    public static Type extractConstraintType(Class<? extends ConstraintValidator<?, ?>> validator) {
        return TypeHelper.extractConstraintValidatorTypeArgumentType(validator, 0);
    }

    public static Type extractConstraintValidatorTypeArgumentType(Class<? extends ConstraintValidator<?, ?>> validator, int typeArgumentIndex) {
        HashMap<Type, Type> resolvedTypes = new HashMap<Type, Type>();
        Type constraintValidatorType = TypeHelper.resolveTypes(resolvedTypes, validator);
        Type type = ((ParameterizedType)constraintValidatorType).getActualTypeArguments()[typeArgumentIndex];
        if (type == null) {
            throw LOG.getNullIsAnInvalidTypeForAConstraintValidatorException();
        }
        if (type instanceof GenericArrayType) {
            type = TypeHelper.getArrayType(TypeHelper.getComponentType(type));
        }
        while (resolvedTypes.containsKey(type)) {
            type = (Type)resolvedTypes.get(type);
        }
        return type;
    }

    public static boolean isUnboundWildcard(Type type) {
        if (!(type instanceof WildcardType)) {
            return false;
        }
        WildcardType wildcardType = (WildcardType)type;
        return TypeHelper.isEmptyBounds(wildcardType.getUpperBounds()) && TypeHelper.isEmptyBounds(wildcardType.getLowerBounds());
    }

    private static Type resolveTypes(Map<Type, Type> resolvedTypes, Type type) {
        if (type == null) {
            return null;
        }
        if (type instanceof Class) {
            Class clazz = (Class)type;
            Type returnedType = TypeHelper.resolveTypeForClassAndHierarchy(resolvedTypes, clazz);
            if (returnedType != null) {
                return returnedType;
            }
        } else if (type instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType)type;
            if (!(paramType.getRawType() instanceof Class)) {
                return null;
            }
            Class rawType = (Class)paramType.getRawType();
            TypeVariable<Class<T>>[] originalTypes = rawType.getTypeParameters();
            Type[] partiallyResolvedTypes = paramType.getActualTypeArguments();
            int nbrOfParams = originalTypes.length;
            for (int i = 0; i < nbrOfParams; ++i) {
                resolvedTypes.put(originalTypes[i], partiallyResolvedTypes[i]);
            }
            if (rawType.equals(ConstraintValidator.class)) {
                return type;
            }
            Type returnedType = TypeHelper.resolveTypeForClassAndHierarchy(resolvedTypes, rawType);
            if (returnedType != null) {
                return returnedType;
            }
        }
        return null;
    }

    private static Type resolveTypeForClassAndHierarchy(Map<Type, Type> resolvedTypes, Class<?> clazz) {
        Type returnedType = TypeHelper.resolveTypes(resolvedTypes, clazz.getGenericSuperclass());
        if (returnedType != null) {
            return returnedType;
        }
        for (Type genericInterface : clazz.getGenericInterfaces()) {
            returnedType = TypeHelper.resolveTypes(resolvedTypes, genericInterface);
            if (returnedType == null) continue;
            return returnedType;
        }
        return null;
    }

    private static void putPrimitiveSubtypes(Map<Class<?>, Set<Class<?>>> subtypesByPrimitive, Class<?> primitiveType, Class<?> ... directSubtypes) {
        HashSet subtypes = CollectionHelper.newHashSet();
        for (Class<?> directSubtype : directSubtypes) {
            subtypes.add(directSubtype);
            subtypes.addAll((Collection)subtypesByPrimitive.get(directSubtype));
        }
        subtypesByPrimitive.put(primitiveType, Collections.unmodifiableSet(subtypes));
    }

    private static boolean isClassAssignable(Class<?> supertype, Class<?> type) {
        if (supertype.isPrimitive() && type.isPrimitive()) {
            return SUBTYPES_BY_PRIMITIVE.get(supertype).contains(type);
        }
        return supertype.isAssignableFrom(type);
    }

    private static boolean isClassAssignableToWildcardType(Class<?> supertype, WildcardType type) {
        for (Type upperBound : type.getUpperBounds()) {
            if (TypeHelper.isAssignable(supertype, upperBound)) continue;
            return false;
        }
        return true;
    }

    private static boolean isParameterizedTypeAssignable(ParameterizedType supertype, ParameterizedType type) {
        Type[] typeArgs;
        Type rawType;
        Type rawSupertype = supertype.getRawType();
        if (!rawSupertype.equals(rawType = type.getRawType())) {
            if (rawSupertype instanceof Class && rawType instanceof Class && !((Class)rawSupertype).isAssignableFrom((Class)rawType)) {
                return false;
            }
            return TypeHelper.isSuperAssignable(supertype, type);
        }
        Type[] supertypeArgs = supertype.getActualTypeArguments();
        if (supertypeArgs.length != (typeArgs = type.getActualTypeArguments()).length) {
            return false;
        }
        for (int i = 0; i < supertypeArgs.length; ++i) {
            Type supertypeArg = supertypeArgs[i];
            Type typeArg = typeArgs[i];
            if (!(supertypeArg instanceof WildcardType ? !TypeHelper.isWildcardTypeAssignable((WildcardType)supertypeArg, typeArg) : !supertypeArg.equals(typeArg))) continue;
            return false;
        }
        return true;
    }

    private static boolean isTypeVariableAssignable(Type supertype, TypeVariable<?> type) {
        for (Type bound : type.getBounds()) {
            if (!TypeHelper.isAssignable(supertype, bound)) continue;
            return true;
        }
        return false;
    }

    private static boolean isWildcardTypeAssignable(WildcardType supertype, Type type) {
        for (Type upperBound : supertype.getUpperBounds()) {
            if (TypeHelper.isAssignable(upperBound, type)) continue;
            return false;
        }
        for (Type lowerBound : supertype.getLowerBounds()) {
            if (TypeHelper.isAssignable(type, lowerBound)) continue;
            return false;
        }
        return true;
    }

    private static boolean isSuperAssignable(Type supertype, Type type) {
        Type superclass = TypeHelper.getResolvedSuperclass(type);
        if (superclass != null && TypeHelper.isAssignable(supertype, superclass)) {
            return true;
        }
        for (Type interphace : TypeHelper.getResolvedInterfaces(type)) {
            if (!TypeHelper.isAssignable(supertype, interphace)) continue;
            return true;
        }
        return false;
    }

    private static boolean isReferenceType(Type type) {
        return type == null || type instanceof Class || type instanceof ParameterizedType || type instanceof TypeVariable || type instanceof GenericArrayType || type instanceof WildcardType;
    }

    private static boolean isArraySupertype(Class<?> type) {
        return Object.class.equals(type) || Cloneable.class.equals(type) || Serializable.class.equals(type);
    }

    private static Type resolveTypeVariables(Type type, Type subtype) {
        if (!(type instanceof ParameterizedType)) {
            return type;
        }
        Map<Type, Type> actualTypeArgumentsByParameter = TypeHelper.getActualTypeArgumentsByParameter(type, subtype);
        Class<?> rawType = TypeHelper.getErasedReferenceType(type);
        return TypeHelper.parameterizeClass(rawType, actualTypeArgumentsByParameter);
    }

    private static Map<Type, Type> getActualTypeArgumentsByParameter(Type ... types) {
        LinkedHashMap<Type, Type> actualTypeArgumentsByParameter = new LinkedHashMap<Type, Type>();
        for (Type type : types) {
            actualTypeArgumentsByParameter.putAll(TypeHelper.getActualTypeArgumentsByParameterInternal(type));
        }
        return TypeHelper.normalize(actualTypeArgumentsByParameter);
    }

    private static Map<Type, Type> getActualTypeArgumentsByParameterInternal(Type type) {
        Type[] typeArguments;
        if (!(type instanceof ParameterizedType)) {
            return Collections.emptyMap();
        }
        TypeVariable<Class<?>>[] typeParameters = TypeHelper.getErasedReferenceType(type).getTypeParameters();
        if (typeParameters.length != (typeArguments = ((ParameterizedType)type).getActualTypeArguments()).length) {
            throw new MalformedParameterizedTypeException();
        }
        LinkedHashMap<Type, Type> actualTypeArgumentsByParameter = new LinkedHashMap<Type, Type>();
        for (int i = 0; i < typeParameters.length; ++i) {
            if (typeParameters[i].equals(typeArguments[i])) continue;
            actualTypeArgumentsByParameter.put(typeParameters[i], typeArguments[i]);
        }
        return actualTypeArgumentsByParameter;
    }

    private static ParameterizedType parameterizeClass(Class<?> type, Map<Type, Type> actualTypeArgumentsByParameter) {
        return TypeHelper.parameterizeClassCapture(type, actualTypeArgumentsByParameter);
    }

    private static <T> ParameterizedType parameterizeClassCapture(Class<T> type, Map<Type, Type> actualTypeArgumentsByParameter) {
        TypeVariable<Class<T>>[] typeParameters = type.getTypeParameters();
        Type[] actualTypeArguments = new Type[typeParameters.length];
        for (int i = 0; i < typeParameters.length; ++i) {
            TypeVariable<Class<T>> typeParameter = typeParameters[i];
            Type actualTypeArgument = actualTypeArgumentsByParameter.get(typeParameter);
            if (actualTypeArgument == null) {
                throw LOG.getMissingActualTypeArgumentForTypeParameterException(typeParameter);
            }
            actualTypeArguments[i] = actualTypeArgument;
        }
        return TypeHelper.parameterizedType(TypeHelper.getErasedReferenceType(type), actualTypeArguments);
    }

    private static <K, V> Map<K, V> normalize(Map<K, V> map) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            K key = entry.getKey();
            V value = entry.getValue();
            while (map.containsKey(value)) {
                value = map.get(value);
            }
            map.put(key, value);
        }
        return map;
    }

    private static boolean isEmptyBounds(Type[] bounds) {
        return bounds == null || bounds.length == 0 || bounds.length == 1 && Object.class.equals((Object)bounds[0]);
    }

    static {
        LOG = LoggerFactory.make(MethodHandles.lookup());
        HashMap<Class<?>, Set<Class<?>>> subtypesByPrimitive = CollectionHelper.newHashMap();
        TypeHelper.putPrimitiveSubtypes(subtypesByPrimitive, Void.TYPE, new Class[0]);
        TypeHelper.putPrimitiveSubtypes(subtypesByPrimitive, Boolean.TYPE, new Class[0]);
        TypeHelper.putPrimitiveSubtypes(subtypesByPrimitive, Byte.TYPE, new Class[0]);
        TypeHelper.putPrimitiveSubtypes(subtypesByPrimitive, Character.TYPE, new Class[0]);
        TypeHelper.putPrimitiveSubtypes(subtypesByPrimitive, Short.TYPE, Byte.TYPE);
        TypeHelper.putPrimitiveSubtypes(subtypesByPrimitive, Integer.TYPE, Character.TYPE, Short.TYPE);
        TypeHelper.putPrimitiveSubtypes(subtypesByPrimitive, Long.TYPE, Integer.TYPE);
        TypeHelper.putPrimitiveSubtypes(subtypesByPrimitive, Float.TYPE, Long.TYPE);
        TypeHelper.putPrimitiveSubtypes(subtypesByPrimitive, Double.TYPE, Float.TYPE);
        SUBTYPES_BY_PRIMITIVE = Collections.unmodifiableMap(subtypesByPrimitive);
    }
}

