/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.ArrayUtils
 *  org.apache.commons.lang3.ClassUtils
 */
package com.opensymphony.xwork2.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;

public class AnnotationUtils {
    private static final Pattern SETTER_PATTERN = Pattern.compile("set([A-Z][A-Za-z0-9]*)$");
    private static final Pattern GETTER_PATTERN = Pattern.compile("(get|is|has)([A-Z][A-Za-z0-9]*)$");

    public static void addAllFields(Class<? extends Annotation> annotationClass, Class<?> clazz, List<Field> allFields) {
        Field[] fields;
        if (clazz == null) {
            return;
        }
        for (Field field : fields = clazz.getDeclaredFields()) {
            Annotation ann = field.getAnnotation(annotationClass);
            if (ann == null) continue;
            allFields.add(field);
        }
        AnnotationUtils.addAllFields(annotationClass, clazz.getSuperclass(), allFields);
    }

    public static void addAllMethods(Class<? extends Annotation> annotationClass, Class<?> clazz, List<Method> allMethods) {
        Method[] methods;
        if (clazz == null) {
            return;
        }
        for (Method method : methods = clazz.getDeclaredMethods()) {
            Annotation ann = method.getAnnotation(annotationClass);
            if (ann == null) continue;
            allMethods.add(method);
        }
        AnnotationUtils.addAllMethods(annotationClass, clazz.getSuperclass(), allMethods);
    }

    public static void addAllInterfaces(Class<?> clazz, List<Class<?>> allInterfaces) {
        if (clazz == null) {
            return;
        }
        Class<?>[] interfaces = clazz.getInterfaces();
        allInterfaces.addAll(Arrays.asList(interfaces));
        AnnotationUtils.addAllInterfaces(clazz.getSuperclass(), allInterfaces);
    }

    public static String resolvePropertyName(Method method) {
        Matcher matcher = SETTER_PATTERN.matcher(method.getName());
        if (matcher.matches() && method.getParameterTypes().length == 1) {
            String raw = matcher.group(1);
            return raw.substring(0, 1).toLowerCase() + raw.substring(1);
        }
        matcher = GETTER_PATTERN.matcher(method.getName());
        if (matcher.matches() && method.getParameterTypes().length == 0) {
            String raw = matcher.group(2);
            return raw.substring(0, 1).toLowerCase() + raw.substring(1);
        }
        return null;
    }

    public static <T extends Annotation> T findAnnotation(Class<?> clazz, Class<T> annotationClass) {
        T ann = clazz.getAnnotation(annotationClass);
        while (ann == null && clazz != null) {
            ann = clazz.getAnnotation(annotationClass);
            if (ann == null) {
                ann = clazz.getPackage().getAnnotation(annotationClass);
            }
            if (ann != null || (clazz = clazz.getSuperclass()) == null) continue;
            ann = clazz.getAnnotation(annotationClass);
        }
        return ann;
    }

    public static <T extends Annotation> List<T> findAnnotations(Class<?> clazz, Class<T> annotationClass) {
        ArrayList<T> anns = new ArrayList<T>();
        ArrayList classes = new ArrayList();
        classes.add(clazz);
        classes.addAll(ClassUtils.getAllSuperclasses(clazz));
        classes.addAll(ClassUtils.getAllInterfaces(clazz));
        for (Class clazz2 : classes) {
            T ann = clazz2.getAnnotation(annotationClass);
            if (ann != null) {
                anns.add(ann);
            }
            if ((ann = clazz2.getPackage().getAnnotation(annotationClass)) == null) continue;
            anns.add(ann);
        }
        return anns;
    }

    @SafeVarargs
    public static boolean isAnnotatedBy(AnnotatedElement annotatedElement, Class<? extends Annotation> ... annotation) {
        if (ArrayUtils.isEmpty((Object[])annotation)) {
            return false;
        }
        for (Class<? extends Annotation> c : annotation) {
            if (!annotatedElement.isAnnotationPresent(c)) continue;
            return true;
        }
        return false;
    }
}

