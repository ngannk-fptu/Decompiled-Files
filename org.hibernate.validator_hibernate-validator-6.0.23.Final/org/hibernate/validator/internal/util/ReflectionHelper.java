/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.util;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.StringHelper;
import org.hibernate.validator.internal.util.TypeHelper;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

public final class ReflectionHelper {
    private static final String PROPERTY_ACCESSOR_PREFIX_GET = "get";
    private static final String PROPERTY_ACCESSOR_PREFIX_IS = "is";
    private static final String PROPERTY_ACCESSOR_PREFIX_HAS = "has";
    public static final String[] PROPERTY_ACCESSOR_PREFIXES = new String[]{"get", "is", "has"};
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_WRAPPER_TYPES;
    private static final Map<Class<?>, Class<?>> WRAPPER_TO_PRIMITIVE_TYPES;

    private ReflectionHelper() {
    }

    public static String getPropertyName(Member member) {
        String name = null;
        if (member instanceof Field) {
            name = member.getName();
        }
        if (member instanceof Method) {
            String methodName = member.getName();
            for (String prefix : PROPERTY_ACCESSOR_PREFIXES) {
                if (!methodName.startsWith(prefix)) continue;
                name = StringHelper.decapitalize(methodName.substring(prefix.length()));
            }
        }
        return name;
    }

    public static boolean isGetterMethod(Executable executable) {
        if (!(executable instanceof Method)) {
            return false;
        }
        Method method = (Method)executable;
        if (method.getParameterTypes().length != 0) {
            return false;
        }
        String methodName = method.getName();
        if (methodName.startsWith(PROPERTY_ACCESSOR_PREFIX_GET) && method.getReturnType() != Void.TYPE) {
            return true;
        }
        if (methodName.startsWith(PROPERTY_ACCESSOR_PREFIX_IS) && method.getReturnType() == Boolean.TYPE) {
            return true;
        }
        return methodName.startsWith(PROPERTY_ACCESSOR_PREFIX_HAS) && method.getReturnType() == Boolean.TYPE;
    }

    public static Type typeOf(Member member) {
        Type type;
        if (member instanceof Field) {
            type = ((Field)member).getGenericType();
        } else if (member instanceof Method) {
            type = ((Method)member).getGenericReturnType();
        } else if (member instanceof Constructor) {
            type = member.getDeclaringClass();
        } else {
            throw LOG.getMemberIsNeitherAFieldNorAMethodException(member);
        }
        if (type instanceof TypeVariable) {
            type = TypeHelper.getErasedType(type);
        }
        return type;
    }

    public static Type typeOf(Executable executable, int parameterIndex) {
        Type type;
        Type[] genericParameterTypes = executable.getGenericParameterTypes();
        if (parameterIndex >= genericParameterTypes.length) {
            genericParameterTypes = executable.getParameterTypes();
        }
        if ((type = genericParameterTypes[parameterIndex]) instanceof TypeVariable) {
            type = TypeHelper.getErasedType(type);
        }
        return type;
    }

    public static Object getValue(Field field, Object object) {
        try {
            return field.get(object);
        }
        catch (IllegalAccessException e) {
            throw LOG.getUnableToAccessMemberException(field.getName(), e);
        }
    }

    public static Object getValue(Method method, Object object) {
        try {
            return method.invoke(object, new Object[0]);
        }
        catch (IllegalAccessException | InvocationTargetException e) {
            throw LOG.getUnableToAccessMemberException(method.getName(), e);
        }
    }

    public static boolean isCollection(Type type) {
        return ReflectionHelper.isIterable(type) || ReflectionHelper.isMap(type) || TypeHelper.isArray(type);
    }

    public static Type getCollectionElementType(Type type) {
        Type indexedType = null;
        if (ReflectionHelper.isIterable(type) && type instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType)type;
            indexedType = paramType.getActualTypeArguments()[0];
        } else if (ReflectionHelper.isMap(type) && type instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType)type;
            indexedType = paramType.getActualTypeArguments()[1];
        } else if (TypeHelper.isArray(type)) {
            indexedType = TypeHelper.getComponentType(type);
        }
        return indexedType;
    }

    public static boolean isIndexable(Type type) {
        return ReflectionHelper.isList(type) || ReflectionHelper.isMap(type) || TypeHelper.isArray(type);
    }

    public static Class<?> getClassFromType(Type type) {
        if (type instanceof Class) {
            return (Class)type;
        }
        if (type instanceof ParameterizedType) {
            return ReflectionHelper.getClassFromType(((ParameterizedType)type).getRawType());
        }
        if (type instanceof GenericArrayType) {
            return Object[].class;
        }
        throw LOG.getUnableToConvertTypeToClassException(type);
    }

    public static boolean isIterable(Type type) {
        if (type instanceof Class && Iterable.class.isAssignableFrom((Class)type)) {
            return true;
        }
        if (type instanceof ParameterizedType) {
            return ReflectionHelper.isIterable(((ParameterizedType)type).getRawType());
        }
        if (type instanceof WildcardType) {
            Type[] upperBounds = ((WildcardType)type).getUpperBounds();
            return upperBounds.length != 0 && ReflectionHelper.isIterable(upperBounds[0]);
        }
        return false;
    }

    public static boolean isMap(Type type) {
        if (type instanceof Class && Map.class.isAssignableFrom((Class)type)) {
            return true;
        }
        if (type instanceof ParameterizedType) {
            return ReflectionHelper.isMap(((ParameterizedType)type).getRawType());
        }
        if (type instanceof WildcardType) {
            Type[] upperBounds = ((WildcardType)type).getUpperBounds();
            return upperBounds.length != 0 && ReflectionHelper.isMap(upperBounds[0]);
        }
        return false;
    }

    public static boolean isList(Type type) {
        if (type instanceof Class && List.class.isAssignableFrom((Class)type)) {
            return true;
        }
        if (type instanceof ParameterizedType) {
            return ReflectionHelper.isList(((ParameterizedType)type).getRawType());
        }
        if (type instanceof WildcardType) {
            Type[] upperBounds = ((WildcardType)type).getUpperBounds();
            return upperBounds.length != 0 && ReflectionHelper.isList(upperBounds[0]);
        }
        return false;
    }

    public static Object getIndexedValue(Object value, int index) {
        Iterable<?> iterable;
        if (value == null) {
            return null;
        }
        Class<?> type = value.getClass();
        if (ReflectionHelper.isIterable(type)) {
            iterable = (Iterable<?>)value;
        } else if (TypeHelper.isArray(type)) {
            iterable = CollectionHelper.iterableFromArray(value);
        } else {
            return null;
        }
        int i = 0;
        for (Object o : iterable) {
            if (i == index) {
                return o;
            }
            ++i;
        }
        return null;
    }

    public static Object getMappedValue(Object value, Object key) {
        if (!(value instanceof Map)) {
            return null;
        }
        Map map = (Map)value;
        return map.get(key);
    }

    private static Class<?> internalBoxedType(Class<?> primitiveType) {
        Class<?> wrapperType = PRIMITIVE_TO_WRAPPER_TYPES.get(primitiveType);
        if (wrapperType == null) {
            throw LOG.getHasToBeAPrimitiveTypeException(primitiveType.getClass());
        }
        return wrapperType;
    }

    public static Type boxedType(Type type) {
        if (type instanceof Class && ((Class)type).isPrimitive()) {
            return ReflectionHelper.internalBoxedType((Class)type);
        }
        return type;
    }

    public static Class<?> boxedType(Class<?> type) {
        if (type.isPrimitive()) {
            return ReflectionHelper.internalBoxedType(type);
        }
        return type;
    }

    public static Class<?> unBoxedType(Class<?> type) {
        Class<?> wrapperType = WRAPPER_TO_PRIMITIVE_TYPES.get(type);
        if (wrapperType == null) {
            throw LOG.getHasToBeABoxedTypeException(type.getClass());
        }
        return wrapperType;
    }

    static {
        HashMap<Class<Object>, Class<Object>> tmpMap = CollectionHelper.newHashMap(9);
        tmpMap.put(Boolean.TYPE, Boolean.class);
        tmpMap.put(Character.TYPE, Character.class);
        tmpMap.put(Double.TYPE, Double.class);
        tmpMap.put(Float.TYPE, Float.class);
        tmpMap.put(Long.TYPE, Long.class);
        tmpMap.put(Integer.TYPE, Integer.class);
        tmpMap.put(Short.TYPE, Short.class);
        tmpMap.put(Byte.TYPE, Byte.class);
        tmpMap.put(Void.TYPE, Void.TYPE);
        PRIMITIVE_TO_WRAPPER_TYPES = Collections.unmodifiableMap(tmpMap);
        tmpMap = CollectionHelper.newHashMap(9);
        tmpMap.put(Boolean.class, Boolean.TYPE);
        tmpMap.put(Character.class, Character.TYPE);
        tmpMap.put(Double.class, Double.TYPE);
        tmpMap.put(Float.class, Float.TYPE);
        tmpMap.put(Long.class, Long.TYPE);
        tmpMap.put(Integer.class, Integer.TYPE);
        tmpMap.put(Short.class, Short.TYPE);
        tmpMap.put(Byte.class, Byte.TYPE);
        tmpMap.put(Void.TYPE, Void.TYPE);
        WRAPPER_TO_PRIMITIVE_TYPES = Collections.unmodifiableMap(tmpMap);
    }
}

