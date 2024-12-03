/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.util;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.hibernate.validator.internal.util.ReflectionHelper;

public class StringHelper {
    private static final Pattern DOT = Pattern.compile("\\.");

    private StringHelper() {
    }

    public static String join(Object[] array, String separator) {
        return array != null ? StringHelper.join(Arrays.asList(array), separator) : null;
    }

    public static String join(Iterable<?> iterable, String separator) {
        if (iterable == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (Object object : iterable) {
            if (!isFirst) {
                sb.append(separator);
            } else {
                isFirst = false;
            }
            sb.append(object);
        }
        return sb.toString();
    }

    public static String decapitalize(String string) {
        if (string == null || string.isEmpty() || StringHelper.startsWithSeveralUpperCaseLetters(string)) {
            return string;
        }
        return string.substring(0, 1).toLowerCase(Locale.ROOT) + string.substring(1);
    }

    public static boolean isNullOrEmptyString(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static String toShortString(Member member) {
        if (member instanceof Field) {
            return StringHelper.toShortString((Field)member);
        }
        if (member instanceof Method) {
            return StringHelper.toShortString((Method)member);
        }
        return member.toString();
    }

    private static String toShortString(Field field) {
        return StringHelper.toShortString(field.getGenericType()) + " " + StringHelper.toShortString(field.getDeclaringClass()) + "#" + field.getName();
    }

    private static String toShortString(Method method) {
        return StringHelper.toShortString(method.getGenericReturnType()) + " " + method.getName() + Arrays.stream(method.getGenericParameterTypes()).map(StringHelper::toShortString).collect(Collectors.joining(", ", "(", ")"));
    }

    public static String toShortString(Type type) {
        if (type instanceof Class) {
            return StringHelper.toShortString((Class)type);
        }
        if (type instanceof ParameterizedType) {
            return StringHelper.toShortString((ParameterizedType)type);
        }
        return type.toString();
    }

    private static String toShortString(Class<?> type) {
        if (type.isArray()) {
            return StringHelper.toShortString(type.getComponentType()) + "[]";
        }
        if (type.getEnclosingClass() != null) {
            return StringHelper.toShortString(type.getEnclosingClass()) + "$" + type.getSimpleName();
        }
        if (type.getPackage() == null) {
            return type.getName();
        }
        return StringHelper.toShortString(type.getPackage()) + "." + type.getSimpleName();
    }

    private static String toShortString(ParameterizedType parameterizedType) {
        Class<?> rawType = ReflectionHelper.getClassFromType(parameterizedType);
        if (rawType.getPackage() == null) {
            return parameterizedType.toString();
        }
        String typeArgumentsString = Arrays.stream(parameterizedType.getActualTypeArguments()).map(t -> StringHelper.toShortString(t)).collect(Collectors.joining(", ", "<", ">"));
        return StringHelper.toShortString(rawType) + typeArgumentsString;
    }

    private static String toShortString(Package pakkage) {
        String[] packageParts = DOT.split(pakkage.getName());
        return Arrays.stream(packageParts).map(n -> n.substring(0, 1)).collect(Collectors.joining("."));
    }

    private static boolean startsWithSeveralUpperCaseLetters(String string) {
        return string.length() > 1 && Character.isUpperCase(string.charAt(0)) && Character.isUpperCase(string.charAt(1));
    }
}

