/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Repeatable;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AliasFor;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationConfigurationException;
import org.springframework.core.annotation.DefaultAnnotationAttributeExtractor;
import org.springframework.core.annotation.MapAnnotationAttributeExtractor;
import org.springframework.core.annotation.SynthesizedAnnotation;
import org.springframework.core.annotation.SynthesizedAnnotationInvocationHandler;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

public abstract class AnnotationUtils {
    public static final String VALUE = "value";
    private static final Map<AnnotationCacheKey, Annotation> findAnnotationCache = new ConcurrentReferenceHashMap<AnnotationCacheKey, Annotation>(256);
    private static final Map<AnnotationCacheKey, Boolean> metaPresentCache = new ConcurrentReferenceHashMap<AnnotationCacheKey, Boolean>(256);
    private static final Map<Class<?>, Set<Method>> annotatedBaseTypeCache = new ConcurrentReferenceHashMap(256);
    @Deprecated
    private static final Map<Class<?>, ?> annotatedInterfaceCache = annotatedBaseTypeCache;
    private static final Map<Class<? extends Annotation>, Boolean> synthesizableCache = new ConcurrentReferenceHashMap<Class<? extends Annotation>, Boolean>(256);
    private static final Map<Class<? extends Annotation>, Map<String, List<String>>> attributeAliasesCache = new ConcurrentReferenceHashMap<Class<? extends Annotation>, Map<String, List<String>>>(256);
    private static final Map<Class<? extends Annotation>, List<Method>> attributeMethodsCache = new ConcurrentReferenceHashMap<Class<? extends Annotation>, List<Method>>(256);
    private static final Map<Method, AliasDescriptor> aliasDescriptorCache = new ConcurrentReferenceHashMap<Method, AliasDescriptor>(256);
    @Nullable
    private static transient Log logger;

    @Nullable
    public static <A extends Annotation> A getAnnotation(Annotation annotation, Class<A> annotationType) {
        if (annotationType.isInstance(annotation)) {
            return (A)AnnotationUtils.synthesizeAnnotation(annotation);
        }
        Class<? extends Annotation> annotatedElement = annotation.annotationType();
        try {
            A metaAnn = annotatedElement.getAnnotation(annotationType);
            return metaAnn != null ? (A)AnnotationUtils.synthesizeAnnotation(metaAnn, annotatedElement) : null;
        }
        catch (Throwable ex) {
            AnnotationUtils.handleIntrospectionFailure(annotatedElement, ex);
            return null;
        }
    }

    @Nullable
    public static <A extends Annotation> A getAnnotation(AnnotatedElement annotatedElement, Class<A> annotationType) {
        try {
            A annotation = annotatedElement.getAnnotation(annotationType);
            if (annotation == null) {
                Annotation metaAnn;
                Annotation[] annotationArray = annotatedElement.getAnnotations();
                int n = annotationArray.length;
                for (int i = 0; i < n && (annotation = (metaAnn = annotationArray[i]).annotationType().getAnnotation(annotationType)) == null; ++i) {
                }
            }
            return annotation != null ? (A)AnnotationUtils.synthesizeAnnotation(annotation, annotatedElement) : null;
        }
        catch (Throwable ex) {
            AnnotationUtils.handleIntrospectionFailure(annotatedElement, ex);
            return null;
        }
    }

    @Nullable
    public static <A extends Annotation> A getAnnotation(Method method, Class<A> annotationType) {
        Method resolvedMethod = BridgeMethodResolver.findBridgedMethod(method);
        return AnnotationUtils.getAnnotation((AnnotatedElement)resolvedMethod, annotationType);
    }

    @Nullable
    public static Annotation[] getAnnotations(AnnotatedElement annotatedElement) {
        try {
            return AnnotationUtils.synthesizeAnnotationArray(annotatedElement.getAnnotations(), annotatedElement);
        }
        catch (Throwable ex) {
            AnnotationUtils.handleIntrospectionFailure(annotatedElement, ex);
            return null;
        }
    }

    @Nullable
    public static Annotation[] getAnnotations(Method method) {
        try {
            return AnnotationUtils.synthesizeAnnotationArray(BridgeMethodResolver.findBridgedMethod(method).getAnnotations(), method);
        }
        catch (Throwable ex) {
            AnnotationUtils.handleIntrospectionFailure(method, ex);
            return null;
        }
    }

    public static <A extends Annotation> Set<A> getRepeatableAnnotations(AnnotatedElement annotatedElement, Class<A> annotationType) {
        return AnnotationUtils.getRepeatableAnnotations(annotatedElement, annotationType, null);
    }

    public static <A extends Annotation> Set<A> getRepeatableAnnotations(AnnotatedElement annotatedElement, Class<A> annotationType, @Nullable Class<? extends Annotation> containerAnnotationType) {
        Class superclass;
        Set<A> annotations = AnnotationUtils.getDeclaredRepeatableAnnotations(annotatedElement, annotationType, containerAnnotationType);
        if (!annotations.isEmpty()) {
            return annotations;
        }
        if (annotatedElement instanceof Class && (superclass = ((Class)annotatedElement).getSuperclass()) != null && superclass != Object.class) {
            return AnnotationUtils.getRepeatableAnnotations(superclass, annotationType, containerAnnotationType);
        }
        return AnnotationUtils.getRepeatableAnnotations(annotatedElement, annotationType, containerAnnotationType, false);
    }

    public static <A extends Annotation> Set<A> getDeclaredRepeatableAnnotations(AnnotatedElement annotatedElement, Class<A> annotationType) {
        return AnnotationUtils.getDeclaredRepeatableAnnotations(annotatedElement, annotationType, null);
    }

    public static <A extends Annotation> Set<A> getDeclaredRepeatableAnnotations(AnnotatedElement annotatedElement, Class<A> annotationType, @Nullable Class<? extends Annotation> containerAnnotationType) {
        return AnnotationUtils.getRepeatableAnnotations(annotatedElement, annotationType, containerAnnotationType, true);
    }

    private static <A extends Annotation> Set<A> getRepeatableAnnotations(AnnotatedElement annotatedElement, Class<A> annotationType, @Nullable Class<? extends Annotation> containerAnnotationType, boolean declaredMode) {
        try {
            if (annotatedElement instanceof Method) {
                annotatedElement = BridgeMethodResolver.findBridgedMethod((Method)annotatedElement);
            }
            return new AnnotationCollector<A>(annotationType, containerAnnotationType, declaredMode).getResult(annotatedElement);
        }
        catch (Throwable ex) {
            AnnotationUtils.handleIntrospectionFailure(annotatedElement, ex);
            return Collections.emptySet();
        }
    }

    @Nullable
    public static <A extends Annotation> A findAnnotation(AnnotatedElement annotatedElement, Class<A> annotationType) {
        A ann = AnnotationUtils.findAnnotation(annotatedElement, annotationType, new HashSet<Annotation>());
        return ann != null ? (A)AnnotationUtils.synthesizeAnnotation(ann, annotatedElement) : null;
    }

    @Nullable
    private static <A extends Annotation> A findAnnotation(AnnotatedElement annotatedElement, Class<A> annotationType, Set<Annotation> visited) {
        try {
            A annotation = annotatedElement.getDeclaredAnnotation(annotationType);
            if (annotation != null) {
                return annotation;
            }
            for (Annotation declaredAnn : annotatedElement.getDeclaredAnnotations()) {
                Class<? extends Annotation> declaredType = declaredAnn.annotationType();
                if (AnnotationUtils.isInJavaLangAnnotationPackage(declaredType) || !visited.add(declaredAnn) || (annotation = AnnotationUtils.findAnnotation(declaredType, annotationType, visited)) == null) continue;
                return annotation;
            }
        }
        catch (Throwable ex) {
            AnnotationUtils.handleIntrospectionFailure(annotatedElement, ex);
        }
        return null;
    }

    @Nullable
    public static <A extends Annotation> A findAnnotation(Method method, @Nullable Class<A> annotationType) {
        Assert.notNull((Object)method, "Method must not be null");
        if (annotationType == null) {
            return null;
        }
        AnnotationCacheKey cacheKey = new AnnotationCacheKey(method, annotationType);
        Annotation result = findAnnotationCache.get(cacheKey);
        if (result == null) {
            Method resolvedMethod = BridgeMethodResolver.findBridgedMethod(method);
            result = AnnotationUtils.findAnnotation((AnnotatedElement)resolvedMethod, annotationType);
            if (result == null) {
                result = AnnotationUtils.searchOnInterfaces(method, annotationType, method.getDeclaringClass().getInterfaces());
            }
            Class<?> clazz = method.getDeclaringClass();
            while (result == null && (clazz = clazz.getSuperclass()) != null && clazz != Object.class) {
                Set<Method> annotatedMethods = AnnotationUtils.getAnnotatedMethodsInBaseType(clazz);
                if (!annotatedMethods.isEmpty()) {
                    for (Method annotatedMethod : annotatedMethods) {
                        Method resolvedSuperMethod;
                        if (AnnotationUtils.isOverride(method, annotatedMethod) && (result = AnnotationUtils.findAnnotation((AnnotatedElement)(resolvedSuperMethod = BridgeMethodResolver.findBridgedMethod(annotatedMethod)), annotationType)) != null) break;
                    }
                }
                if (result != null) continue;
                result = AnnotationUtils.searchOnInterfaces(method, annotationType, clazz.getInterfaces());
            }
            if (result != null) {
                result = AnnotationUtils.synthesizeAnnotation(result, method);
                findAnnotationCache.put(cacheKey, result);
            }
        }
        return (A)result;
    }

    @Nullable
    private static <A extends Annotation> A searchOnInterfaces(Method method, Class<A> annotationType, Class<?> ... ifcs) {
        for (Class<?> ifc : ifcs) {
            Set<Method> annotatedMethods = AnnotationUtils.getAnnotatedMethodsInBaseType(ifc);
            if (annotatedMethods.isEmpty()) continue;
            for (Method annotatedMethod : annotatedMethods) {
                A annotation;
                if (!AnnotationUtils.isOverride(method, annotatedMethod) || (annotation = AnnotationUtils.getAnnotation(annotatedMethod, annotationType)) == null) continue;
                return annotation;
            }
        }
        return null;
    }

    static boolean isOverride(Method method, Method candidate) {
        if (!candidate.getName().equals(method.getName()) || candidate.getParameterCount() != method.getParameterCount()) {
            return false;
        }
        Object[] paramTypes = method.getParameterTypes();
        if (Arrays.equals(candidate.getParameterTypes(), paramTypes)) {
            return true;
        }
        for (int i = 0; i < paramTypes.length; ++i) {
            if (paramTypes[i] == ResolvableType.forMethodParameter(candidate, i, method.getDeclaringClass()).resolve()) continue;
            return false;
        }
        return true;
    }

    static Set<Method> getAnnotatedMethodsInBaseType(Class<?> baseType) {
        Method[] methods;
        boolean ifcCheck = baseType.isInterface();
        if (ifcCheck && ClassUtils.isJavaLanguageInterface(baseType)) {
            return Collections.emptySet();
        }
        Set<Method> annotatedMethods = annotatedBaseTypeCache.get(baseType);
        if (annotatedMethods != null) {
            return annotatedMethods;
        }
        for (Method baseMethod : methods = ifcCheck ? baseType.getMethods() : baseType.getDeclaredMethods()) {
            try {
                if (!ifcCheck && Modifier.isPrivate(baseMethod.getModifiers()) || !AnnotationUtils.hasSearchableAnnotations(baseMethod)) continue;
                if (annotatedMethods == null) {
                    annotatedMethods = new HashSet();
                }
                annotatedMethods.add(baseMethod);
            }
            catch (Throwable ex) {
                AnnotationUtils.handleIntrospectionFailure(baseMethod, ex);
            }
        }
        if (annotatedMethods == null) {
            annotatedMethods = Collections.emptySet();
        }
        annotatedBaseTypeCache.put(baseType, annotatedMethods);
        return annotatedMethods;
    }

    private static boolean hasSearchableAnnotations(Method ifcMethod) {
        Annotation[] anns = ifcMethod.getAnnotations();
        if (anns.length == 0) {
            return false;
        }
        for (Annotation ann : anns) {
            Class<? extends Annotation> annType = ann.annotationType();
            if (annType == Nullable.class || annType == Deprecated.class) continue;
            return true;
        }
        return false;
    }

    @Nullable
    public static <A extends Annotation> A findAnnotation(Class<?> clazz, Class<A> annotationType) {
        return AnnotationUtils.findAnnotation(clazz, annotationType, true);
    }

    @Nullable
    private static <A extends Annotation> A findAnnotation(Class<?> clazz, @Nullable Class<A> annotationType, boolean synthesize) {
        Assert.notNull(clazz, "Class must not be null");
        if (annotationType == null) {
            return null;
        }
        AnnotationCacheKey cacheKey = new AnnotationCacheKey(clazz, annotationType);
        Annotation result = findAnnotationCache.get(cacheKey);
        if (result == null && (result = AnnotationUtils.findAnnotation(clazz, annotationType, new HashSet<Annotation>())) != null && synthesize) {
            result = AnnotationUtils.synthesizeAnnotation(result, clazz);
            findAnnotationCache.put(cacheKey, result);
        }
        return (A)result;
    }

    @Nullable
    private static <A extends Annotation> A findAnnotation(Class<?> clazz, Class<A> annotationType, Set<Annotation> visited) {
        try {
            A annotation = clazz.getDeclaredAnnotation(annotationType);
            if (annotation != null) {
                return annotation;
            }
            Annotation[] annotationArray = clazz.getDeclaredAnnotations();
            int n = annotationArray.length;
            for (int i = 0; i < n; ++i) {
                Annotation declaredAnn = annotationArray[i];
                Class<? extends Annotation> declaredType = declaredAnn.annotationType();
                if (AnnotationUtils.isInJavaLangAnnotationPackage(declaredType) || !visited.add(declaredAnn) || (annotation = AnnotationUtils.findAnnotation(declaredType, annotationType, visited)) == null) continue;
                return annotation;
            }
        }
        catch (Throwable ex) {
            AnnotationUtils.handleIntrospectionFailure(clazz, ex);
            return null;
        }
        for (Class<?> ifc : clazz.getInterfaces()) {
            A annotation = AnnotationUtils.findAnnotation(ifc, annotationType, visited);
            if (annotation == null) continue;
            return annotation;
        }
        Class<?> superclass = clazz.getSuperclass();
        if (superclass == null || superclass == Object.class) {
            return null;
        }
        return AnnotationUtils.findAnnotation(superclass, annotationType, visited);
    }

    @Nullable
    public static Class<?> findAnnotationDeclaringClass(Class<? extends Annotation> annotationType, @Nullable Class<?> clazz) {
        if (clazz == null || clazz == Object.class) {
            return null;
        }
        if (AnnotationUtils.isAnnotationDeclaredLocally(annotationType, clazz)) {
            return clazz;
        }
        return AnnotationUtils.findAnnotationDeclaringClass(annotationType, clazz.getSuperclass());
    }

    @Nullable
    public static Class<?> findAnnotationDeclaringClassForTypes(List<Class<? extends Annotation>> annotationTypes, @Nullable Class<?> clazz) {
        if (clazz == null || clazz == Object.class) {
            return null;
        }
        for (Class<? extends Annotation> annotationType : annotationTypes) {
            if (!AnnotationUtils.isAnnotationDeclaredLocally(annotationType, clazz)) continue;
            return clazz;
        }
        return AnnotationUtils.findAnnotationDeclaringClassForTypes(annotationTypes, clazz.getSuperclass());
    }

    public static boolean isAnnotationDeclaredLocally(Class<? extends Annotation> annotationType, Class<?> clazz) {
        try {
            return clazz.getDeclaredAnnotation(annotationType) != null;
        }
        catch (Throwable ex) {
            AnnotationUtils.handleIntrospectionFailure(clazz, ex);
            return false;
        }
    }

    public static boolean isAnnotationInherited(Class<? extends Annotation> annotationType, Class<?> clazz) {
        return clazz.isAnnotationPresent(annotationType) && !AnnotationUtils.isAnnotationDeclaredLocally(annotationType, clazz);
    }

    public static boolean isAnnotationMetaPresent(Class<? extends Annotation> annotationType, @Nullable Class<? extends Annotation> metaAnnotationType) {
        Assert.notNull(annotationType, "Annotation type must not be null");
        if (metaAnnotationType == null) {
            return false;
        }
        AnnotationCacheKey cacheKey = new AnnotationCacheKey(annotationType, metaAnnotationType);
        Boolean metaPresent = metaPresentCache.get(cacheKey);
        if (metaPresent != null) {
            return metaPresent;
        }
        metaPresent = Boolean.FALSE;
        if (AnnotationUtils.findAnnotation(annotationType, metaAnnotationType, false) != null) {
            metaPresent = Boolean.TRUE;
        }
        metaPresentCache.put(cacheKey, metaPresent);
        return metaPresent;
    }

    public static boolean isInJavaLangAnnotationPackage(@Nullable Annotation annotation) {
        return annotation != null && AnnotationUtils.isInJavaLangAnnotationPackage(annotation.annotationType());
    }

    static boolean isInJavaLangAnnotationPackage(@Nullable Class<? extends Annotation> annotationType) {
        return annotationType != null && AnnotationUtils.isInJavaLangAnnotationPackage(annotationType.getName());
    }

    public static boolean isInJavaLangAnnotationPackage(@Nullable String annotationType) {
        return annotationType != null && annotationType.startsWith("java.lang.annotation");
    }

    public static void validateAnnotation(Annotation annotation) {
        for (Method method : AnnotationUtils.getAttributeMethods(annotation.annotationType())) {
            Class<?> returnType = method.getReturnType();
            if (returnType != Class.class && returnType != Class[].class) continue;
            try {
                method.invoke((Object)annotation, new Object[0]);
            }
            catch (Throwable ex) {
                throw new IllegalStateException("Could not obtain annotation attribute value for " + method, ex);
            }
        }
    }

    public static Map<String, Object> getAnnotationAttributes(Annotation annotation) {
        return AnnotationUtils.getAnnotationAttributes(null, annotation);
    }

    public static Map<String, Object> getAnnotationAttributes(Annotation annotation, boolean classValuesAsString) {
        return AnnotationUtils.getAnnotationAttributes(annotation, classValuesAsString, false);
    }

    public static AnnotationAttributes getAnnotationAttributes(Annotation annotation, boolean classValuesAsString, boolean nestedAnnotationsAsMap) {
        return AnnotationUtils.getAnnotationAttributes(null, annotation, classValuesAsString, nestedAnnotationsAsMap);
    }

    public static AnnotationAttributes getAnnotationAttributes(@Nullable AnnotatedElement annotatedElement, Annotation annotation) {
        return AnnotationUtils.getAnnotationAttributes(annotatedElement, annotation, false, false);
    }

    public static AnnotationAttributes getAnnotationAttributes(@Nullable AnnotatedElement annotatedElement, Annotation annotation, boolean classValuesAsString, boolean nestedAnnotationsAsMap) {
        return AnnotationUtils.getAnnotationAttributes((Object)annotatedElement, annotation, classValuesAsString, nestedAnnotationsAsMap);
    }

    private static AnnotationAttributes getAnnotationAttributes(@Nullable Object annotatedElement, Annotation annotation, boolean classValuesAsString, boolean nestedAnnotationsAsMap) {
        AnnotationAttributes attributes = AnnotationUtils.retrieveAnnotationAttributes(annotatedElement, annotation, classValuesAsString, nestedAnnotationsAsMap);
        AnnotationUtils.postProcessAnnotationAttributes(annotatedElement, attributes, classValuesAsString, nestedAnnotationsAsMap);
        return attributes;
    }

    static AnnotationAttributes retrieveAnnotationAttributes(@Nullable Object annotatedElement, Annotation annotation, boolean classValuesAsString, boolean nestedAnnotationsAsMap) {
        Class<? extends Annotation> annotationType = annotation.annotationType();
        AnnotationAttributes attributes = new AnnotationAttributes(annotationType);
        for (Method method : AnnotationUtils.getAttributeMethods(annotationType)) {
            try {
                Object attributeValue = method.invoke((Object)annotation, new Object[0]);
                Object defaultValue = method.getDefaultValue();
                if (defaultValue != null && ObjectUtils.nullSafeEquals(attributeValue, defaultValue)) {
                    attributeValue = new DefaultValueHolder(defaultValue);
                }
                attributes.put(method.getName(), AnnotationUtils.adaptValue(annotatedElement, attributeValue, classValuesAsString, nestedAnnotationsAsMap));
            }
            catch (Throwable ex) {
                if (ex instanceof InvocationTargetException) {
                    Throwable targetException = ((InvocationTargetException)ex).getTargetException();
                    AnnotationUtils.rethrowAnnotationConfigurationException(targetException);
                }
                throw new IllegalStateException("Could not obtain annotation attribute value for " + method, ex);
            }
        }
        return attributes;
    }

    @Nullable
    static Object adaptValue(@Nullable Object annotatedElement, @Nullable Object value, boolean classValuesAsString, boolean nestedAnnotationsAsMap) {
        if (classValuesAsString) {
            if (value instanceof Class) {
                return ((Class)value).getName();
            }
            if (value instanceof Class[]) {
                Class[] clazzArray = (Class[])value;
                String[] classNames = new String[clazzArray.length];
                for (int i = 0; i < clazzArray.length; ++i) {
                    classNames[i] = clazzArray[i].getName();
                }
                return classNames;
            }
        }
        if (value instanceof Annotation) {
            Annotation annotation = (Annotation)value;
            if (nestedAnnotationsAsMap) {
                return AnnotationUtils.getAnnotationAttributes(annotatedElement, annotation, classValuesAsString, true);
            }
            return AnnotationUtils.synthesizeAnnotation(annotation, annotatedElement);
        }
        if (value instanceof Annotation[]) {
            Annotation[] annotations = (Annotation[])value;
            if (nestedAnnotationsAsMap) {
                AnnotationAttributes[] mappedAnnotations = new AnnotationAttributes[annotations.length];
                for (int i = 0; i < annotations.length; ++i) {
                    mappedAnnotations[i] = AnnotationUtils.getAnnotationAttributes(annotatedElement, annotations[i], classValuesAsString, true);
                }
                return mappedAnnotations;
            }
            return AnnotationUtils.synthesizeAnnotationArray(annotations, annotatedElement);
        }
        return value;
    }

    public static void registerDefaultValues(AnnotationAttributes attributes) {
        Class<? extends Annotation> annotationType = attributes.annotationType();
        if (annotationType != null && Modifier.isPublic(annotationType.getModifiers())) {
            for (Method annotationAttribute : AnnotationUtils.getAttributeMethods(annotationType)) {
                String attributeName = annotationAttribute.getName();
                AnnotationAttributes[] defaultValue = annotationAttribute.getDefaultValue();
                if (defaultValue == null || attributes.containsKey(attributeName)) continue;
                if (defaultValue instanceof Annotation) {
                    defaultValue = AnnotationUtils.getAnnotationAttributes((Annotation)defaultValue, false, true);
                } else if (defaultValue instanceof Annotation[]) {
                    Annotation[] realAnnotations = (Annotation[])defaultValue;
                    AnnotationAttributes[] mappedAnnotations = new AnnotationAttributes[realAnnotations.length];
                    for (int i = 0; i < realAnnotations.length; ++i) {
                        mappedAnnotations[i] = AnnotationUtils.getAnnotationAttributes(realAnnotations[i], false, true);
                    }
                    defaultValue = mappedAnnotations;
                }
                attributes.put(attributeName, new DefaultValueHolder(defaultValue));
            }
        }
    }

    public static void postProcessAnnotationAttributes(@Nullable Object annotatedElement, AnnotationAttributes attributes, boolean classValuesAsString) {
        AnnotationUtils.postProcessAnnotationAttributes(annotatedElement, attributes, classValuesAsString, false);
    }

    static void postProcessAnnotationAttributes(@Nullable Object annotatedElement, @Nullable AnnotationAttributes attributes, boolean classValuesAsString, boolean nestedAnnotationsAsMap) {
        if (attributes == null) {
            return;
        }
        Class<? extends Annotation> annotationType = attributes.annotationType();
        HashSet valuesAlreadyReplaced = new HashSet();
        if (!attributes.validated) {
            Map<String, List<String>> aliasMap = AnnotationUtils.getAttributeAliasMap(annotationType);
            aliasMap.forEach((attributeName, aliasedAttributeNames) -> {
                if (valuesAlreadyReplaced.contains(attributeName)) {
                    return;
                }
                Object value = attributes.get(attributeName);
                boolean valuePresent = value != null && !(value instanceof DefaultValueHolder);
                for (String aliasedAttributeName : aliasedAttributeNames) {
                    boolean aliasPresent;
                    if (valuesAlreadyReplaced.contains(aliasedAttributeName)) continue;
                    Object aliasedValue = attributes.get(aliasedAttributeName);
                    boolean bl = aliasPresent = aliasedValue != null && !(aliasedValue instanceof DefaultValueHolder);
                    if (!valuePresent && !aliasPresent) continue;
                    if (valuePresent && aliasPresent) {
                        if (ObjectUtils.nullSafeEquals(value, aliasedValue)) continue;
                        String elementAsString = annotatedElement != null ? annotatedElement.toString() : "unknown element";
                        throw new AnnotationConfigurationException(String.format("In AnnotationAttributes for annotation [%s] declared on %s, attribute '%s' and its alias '%s' are declared with values of [%s] and [%s], but only one is permitted.", attributes.displayName, elementAsString, attributeName, aliasedAttributeName, ObjectUtils.nullSafeToString(value), ObjectUtils.nullSafeToString(aliasedValue)));
                    }
                    if (aliasPresent) {
                        attributes.put(attributeName, AnnotationUtils.adaptValue(annotatedElement, aliasedValue, classValuesAsString, nestedAnnotationsAsMap));
                        valuesAlreadyReplaced.add(attributeName);
                        continue;
                    }
                    attributes.put(aliasedAttributeName, AnnotationUtils.adaptValue(annotatedElement, value, classValuesAsString, nestedAnnotationsAsMap));
                    valuesAlreadyReplaced.add(aliasedAttributeName);
                }
            });
            attributes.validated = true;
        }
        for (String attributeName2 : attributes.keySet()) {
            Object value;
            if (valuesAlreadyReplaced.contains(attributeName2) || !((value = attributes.get(attributeName2)) instanceof DefaultValueHolder)) continue;
            value = ((DefaultValueHolder)value).defaultValue;
            attributes.put(attributeName2, AnnotationUtils.adaptValue(annotatedElement, value, classValuesAsString, nestedAnnotationsAsMap));
        }
    }

    @Nullable
    public static Object getValue(Annotation annotation) {
        return AnnotationUtils.getValue(annotation, VALUE);
    }

    @Nullable
    public static Object getValue(@Nullable Annotation annotation, @Nullable String attributeName) {
        if (annotation == null || !StringUtils.hasText(attributeName)) {
            return null;
        }
        try {
            Method method = annotation.annotationType().getDeclaredMethod(attributeName, new Class[0]);
            ReflectionUtils.makeAccessible(method);
            return method.invoke((Object)annotation, new Object[0]);
        }
        catch (NoSuchMethodException ex) {
            return null;
        }
        catch (InvocationTargetException ex) {
            AnnotationUtils.rethrowAnnotationConfigurationException(ex.getTargetException());
            throw new IllegalStateException("Could not obtain value for annotation attribute '" + attributeName + "' in " + annotation, ex);
        }
        catch (Throwable ex) {
            AnnotationUtils.handleIntrospectionFailure(annotation.getClass(), ex);
            return null;
        }
    }

    @Nullable
    public static Object getDefaultValue(Annotation annotation) {
        return AnnotationUtils.getDefaultValue(annotation, VALUE);
    }

    @Nullable
    public static Object getDefaultValue(@Nullable Annotation annotation, @Nullable String attributeName) {
        if (annotation == null) {
            return null;
        }
        return AnnotationUtils.getDefaultValue(annotation.annotationType(), attributeName);
    }

    @Nullable
    public static Object getDefaultValue(Class<? extends Annotation> annotationType) {
        return AnnotationUtils.getDefaultValue(annotationType, VALUE);
    }

    @Nullable
    public static Object getDefaultValue(@Nullable Class<? extends Annotation> annotationType, @Nullable String attributeName) {
        if (annotationType == null || !StringUtils.hasText(attributeName)) {
            return null;
        }
        try {
            return annotationType.getDeclaredMethod(attributeName, new Class[0]).getDefaultValue();
        }
        catch (Throwable ex) {
            AnnotationUtils.handleIntrospectionFailure(annotationType, ex);
            return null;
        }
    }

    static <A extends Annotation> A synthesizeAnnotation(A annotation) {
        return AnnotationUtils.synthesizeAnnotation(annotation, null);
    }

    public static <A extends Annotation> A synthesizeAnnotation(A annotation, @Nullable AnnotatedElement annotatedElement) {
        return AnnotationUtils.synthesizeAnnotation(annotation, (Object)annotatedElement);
    }

    static <A extends Annotation> A synthesizeAnnotation(A annotation, @Nullable Object annotatedElement) {
        if (annotation instanceof SynthesizedAnnotation) {
            return annotation;
        }
        Class<? extends Annotation> annotationType = annotation.annotationType();
        if (!AnnotationUtils.isSynthesizable(annotationType)) {
            return annotation;
        }
        DefaultAnnotationAttributeExtractor attributeExtractor = new DefaultAnnotationAttributeExtractor(annotation, annotatedElement);
        SynthesizedAnnotationInvocationHandler handler = new SynthesizedAnnotationInvocationHandler(attributeExtractor);
        Class[] exposedInterfaces = new Class[]{annotationType, SynthesizedAnnotation.class};
        return (A)((Annotation)Proxy.newProxyInstance(annotation.getClass().getClassLoader(), exposedInterfaces, (InvocationHandler)handler));
    }

    public static <A extends Annotation> A synthesizeAnnotation(Map<String, Object> attributes, Class<A> annotationType, @Nullable AnnotatedElement annotatedElement) {
        Class[] classArray;
        MapAnnotationAttributeExtractor attributeExtractor = new MapAnnotationAttributeExtractor(attributes, annotationType, annotatedElement);
        SynthesizedAnnotationInvocationHandler handler = new SynthesizedAnnotationInvocationHandler(attributeExtractor);
        if (AnnotationUtils.canExposeSynthesizedMarker(annotationType)) {
            Class[] classArray2 = new Class[2];
            classArray2[0] = annotationType;
            classArray = classArray2;
            classArray2[1] = SynthesizedAnnotation.class;
        } else {
            Class[] classArray3 = new Class[1];
            classArray = classArray3;
            classArray3[0] = annotationType;
        }
        Class[] exposedInterfaces = classArray;
        return (A)((Annotation)Proxy.newProxyInstance(annotationType.getClassLoader(), exposedInterfaces, (InvocationHandler)handler));
    }

    public static <A extends Annotation> A synthesizeAnnotation(Class<A> annotationType) {
        return AnnotationUtils.synthesizeAnnotation(Collections.emptyMap(), annotationType, null);
    }

    static Annotation[] synthesizeAnnotationArray(Annotation[] annotations, @Nullable Object annotatedElement) {
        Annotation[] synthesized = (Annotation[])Array.newInstance(annotations.getClass().getComponentType(), annotations.length);
        for (int i = 0; i < annotations.length; ++i) {
            synthesized[i] = AnnotationUtils.synthesizeAnnotation(annotations[i], annotatedElement);
        }
        return synthesized;
    }

    @Nullable
    static <A extends Annotation> A[] synthesizeAnnotationArray(@Nullable Map<String, Object>[] maps, Class<A> annotationType) {
        if (maps == null) {
            return null;
        }
        Annotation[] synthesized = (Annotation[])Array.newInstance(annotationType, maps.length);
        for (int i = 0; i < maps.length; ++i) {
            synthesized[i] = AnnotationUtils.synthesizeAnnotation(maps[i], annotationType, null);
        }
        return synthesized;
    }

    static Map<String, List<String>> getAttributeAliasMap(@Nullable Class<? extends Annotation> annotationType) {
        if (annotationType == null) {
            return Collections.emptyMap();
        }
        Map<String, List<String>> map = attributeAliasesCache.get(annotationType);
        if (map != null) {
            return map;
        }
        map = new LinkedHashMap<String, List<String>>();
        for (Method attribute : AnnotationUtils.getAttributeMethods(annotationType)) {
            List<String> aliasNames = AnnotationUtils.getAttributeAliasNames(attribute);
            if (aliasNames.isEmpty()) continue;
            map.put(attribute.getName(), aliasNames);
        }
        attributeAliasesCache.put(annotationType, map);
        return map;
    }

    private static boolean canExposeSynthesizedMarker(Class<? extends Annotation> annotationType) {
        try {
            return Class.forName(SynthesizedAnnotation.class.getName(), false, annotationType.getClassLoader()) == SynthesizedAnnotation.class;
        }
        catch (ClassNotFoundException ex) {
            return false;
        }
    }

    private static boolean isSynthesizable(Class<? extends Annotation> annotationType) {
        if (AnnotationUtils.isInJavaLangAnnotationPackage(annotationType)) {
            return false;
        }
        Boolean synthesizable = synthesizableCache.get(annotationType);
        if (synthesizable != null) {
            return synthesizable;
        }
        synthesizable = Boolean.FALSE;
        for (Method attribute : AnnotationUtils.getAttributeMethods(annotationType)) {
            Class<?> nestedAnnotationType;
            if (!AnnotationUtils.getAttributeAliasNames(attribute).isEmpty()) {
                synthesizable = Boolean.TRUE;
                break;
            }
            Class<?> returnType = attribute.getReturnType();
            if (Annotation[].class.isAssignableFrom(returnType)) {
                nestedAnnotationType = returnType.getComponentType();
                if (!AnnotationUtils.isSynthesizable(nestedAnnotationType)) continue;
                synthesizable = Boolean.TRUE;
                break;
            }
            if (!Annotation.class.isAssignableFrom(returnType) || !AnnotationUtils.isSynthesizable(nestedAnnotationType = returnType)) continue;
            synthesizable = Boolean.TRUE;
            break;
        }
        synthesizableCache.put(annotationType, synthesizable);
        return synthesizable;
    }

    static List<String> getAttributeAliasNames(Method attribute) {
        AliasDescriptor descriptor = AliasDescriptor.from(attribute);
        return descriptor != null ? descriptor.getAttributeAliasNames() : Collections.emptyList();
    }

    @Nullable
    static String getAttributeOverrideName(Method attribute, @Nullable Class<? extends Annotation> metaAnnotationType) {
        AliasDescriptor descriptor = AliasDescriptor.from(attribute);
        return descriptor != null && metaAnnotationType != null ? descriptor.getAttributeOverrideName(metaAnnotationType) : null;
    }

    static List<Method> getAttributeMethods(Class<? extends Annotation> annotationType) {
        List<Method> methods = attributeMethodsCache.get(annotationType);
        if (methods != null) {
            return methods;
        }
        methods = new ArrayList<Method>();
        for (Method method : annotationType.getDeclaredMethods()) {
            if (!AnnotationUtils.isAttributeMethod(method)) continue;
            ReflectionUtils.makeAccessible(method);
            methods.add(method);
        }
        attributeMethodsCache.put(annotationType, methods);
        return methods;
    }

    @Nullable
    static Annotation getAnnotation(AnnotatedElement element, String annotationName) {
        for (Annotation annotation : element.getAnnotations()) {
            if (!annotation.annotationType().getName().equals(annotationName)) continue;
            return annotation;
        }
        return null;
    }

    static boolean isAttributeMethod(@Nullable Method method) {
        return method != null && method.getParameterCount() == 0 && method.getReturnType() != Void.TYPE;
    }

    static boolean isAnnotationTypeMethod(@Nullable Method method) {
        return method != null && method.getName().equals("annotationType") && method.getParameterCount() == 0;
    }

    @Nullable
    static Class<? extends Annotation> resolveContainerAnnotationType(Class<? extends Annotation> annotationType) {
        Repeatable repeatable = AnnotationUtils.getAnnotation(annotationType, Repeatable.class);
        return repeatable != null ? repeatable.value() : null;
    }

    static void rethrowAnnotationConfigurationException(Throwable ex) {
        if (ex instanceof AnnotationConfigurationException) {
            throw (AnnotationConfigurationException)ex;
        }
    }

    static void handleIntrospectionFailure(@Nullable AnnotatedElement element, Throwable ex) {
        AnnotationUtils.rethrowAnnotationConfigurationException(ex);
        Log loggerToUse = logger;
        if (loggerToUse == null) {
            logger = loggerToUse = LogFactory.getLog(AnnotationUtils.class);
        }
        if (element instanceof Class && Annotation.class.isAssignableFrom((Class)element)) {
            if (loggerToUse.isDebugEnabled()) {
                loggerToUse.debug("Failed to meta-introspect annotation " + element + ": " + ex);
            }
        } else if (loggerToUse.isInfoEnabled()) {
            loggerToUse.info("Failed to introspect annotations on " + element + ": " + ex);
        }
    }

    public static void clearCache() {
        findAnnotationCache.clear();
        metaPresentCache.clear();
        annotatedBaseTypeCache.clear();
        synthesizableCache.clear();
        attributeAliasesCache.clear();
        attributeMethodsCache.clear();
        aliasDescriptorCache.clear();
    }

    private static class DefaultValueHolder {
        final Object defaultValue;

        public DefaultValueHolder(Object defaultValue) {
            this.defaultValue = defaultValue;
        }
    }

    private static final class AliasDescriptor {
        private final Method sourceAttribute;
        private final Class<? extends Annotation> sourceAnnotationType;
        private final String sourceAttributeName;
        private final Method aliasedAttribute;
        private final Class<? extends Annotation> aliasedAnnotationType;
        private final String aliasedAttributeName;
        private final boolean isAliasPair;

        @Nullable
        public static AliasDescriptor from(Method attribute) {
            AliasDescriptor descriptor = (AliasDescriptor)aliasDescriptorCache.get(attribute);
            if (descriptor != null) {
                return descriptor;
            }
            AliasFor aliasFor = attribute.getAnnotation(AliasFor.class);
            if (aliasFor == null) {
                return null;
            }
            descriptor = new AliasDescriptor(attribute, aliasFor);
            descriptor.validate();
            aliasDescriptorCache.put(attribute, descriptor);
            return descriptor;
        }

        private AliasDescriptor(Method sourceAttribute, AliasFor aliasFor) {
            Class<?> declaringClass = sourceAttribute.getDeclaringClass();
            this.sourceAttribute = sourceAttribute;
            this.sourceAnnotationType = declaringClass;
            this.sourceAttributeName = sourceAttribute.getName();
            this.aliasedAnnotationType = Annotation.class == aliasFor.annotation() ? this.sourceAnnotationType : aliasFor.annotation();
            this.aliasedAttributeName = this.getAliasedAttributeName(aliasFor, sourceAttribute);
            if (this.aliasedAnnotationType == this.sourceAnnotationType && this.aliasedAttributeName.equals(this.sourceAttributeName)) {
                String msg = String.format("@AliasFor declaration on attribute '%s' in annotation [%s] points to itself. Specify 'annotation' to point to a same-named attribute on a meta-annotation.", sourceAttribute.getName(), declaringClass.getName());
                throw new AnnotationConfigurationException(msg);
            }
            try {
                this.aliasedAttribute = this.aliasedAnnotationType.getDeclaredMethod(this.aliasedAttributeName, new Class[0]);
            }
            catch (NoSuchMethodException ex) {
                String msg = String.format("Attribute '%s' in annotation [%s] is declared as an @AliasFor nonexistent attribute '%s' in annotation [%s].", this.sourceAttributeName, this.sourceAnnotationType.getName(), this.aliasedAttributeName, this.aliasedAnnotationType.getName());
                throw new AnnotationConfigurationException(msg, ex);
            }
            this.isAliasPair = this.sourceAnnotationType == this.aliasedAnnotationType;
        }

        private void validate() {
            Class<?> aliasedReturnType;
            Class<?> returnType;
            if (!this.isAliasPair && !AnnotationUtils.isAnnotationMetaPresent(this.sourceAnnotationType, this.aliasedAnnotationType)) {
                String msg = String.format("@AliasFor declaration on attribute '%s' in annotation [%s] declares an alias for attribute '%s' in meta-annotation [%s] which is not meta-present.", this.sourceAttributeName, this.sourceAnnotationType.getName(), this.aliasedAttributeName, this.aliasedAnnotationType.getName());
                throw new AnnotationConfigurationException(msg);
            }
            if (this.isAliasPair) {
                AliasFor mirrorAliasFor = this.aliasedAttribute.getAnnotation(AliasFor.class);
                if (mirrorAliasFor == null) {
                    String msg = String.format("Attribute '%s' in annotation [%s] must be declared as an @AliasFor [%s].", this.aliasedAttributeName, this.sourceAnnotationType.getName(), this.sourceAttributeName);
                    throw new AnnotationConfigurationException(msg);
                }
                String mirrorAliasedAttributeName = this.getAliasedAttributeName(mirrorAliasFor, this.aliasedAttribute);
                if (!this.sourceAttributeName.equals(mirrorAliasedAttributeName)) {
                    String msg = String.format("Attribute '%s' in annotation [%s] must be declared as an @AliasFor [%s], not [%s].", this.aliasedAttributeName, this.sourceAnnotationType.getName(), this.sourceAttributeName, mirrorAliasedAttributeName);
                    throw new AnnotationConfigurationException(msg);
                }
            }
            if (!((returnType = this.sourceAttribute.getReturnType()) == (aliasedReturnType = this.aliasedAttribute.getReturnType()) || aliasedReturnType.isArray() && returnType == aliasedReturnType.getComponentType())) {
                String msg = String.format("Misconfigured aliases: attribute '%s' in annotation [%s] and attribute '%s' in annotation [%s] must declare the same return type.", this.sourceAttributeName, this.sourceAnnotationType.getName(), this.aliasedAttributeName, this.aliasedAnnotationType.getName());
                throw new AnnotationConfigurationException(msg);
            }
            if (this.isAliasPair) {
                this.validateDefaultValueConfiguration(this.aliasedAttribute);
            }
        }

        private void validateDefaultValueConfiguration(Method aliasedAttribute) {
            Object defaultValue = this.sourceAttribute.getDefaultValue();
            Object aliasedDefaultValue = aliasedAttribute.getDefaultValue();
            if (defaultValue == null || aliasedDefaultValue == null) {
                String msg = String.format("Misconfigured aliases: attribute '%s' in annotation [%s] and attribute '%s' in annotation [%s] must declare default values.", this.sourceAttributeName, this.sourceAnnotationType.getName(), aliasedAttribute.getName(), aliasedAttribute.getDeclaringClass().getName());
                throw new AnnotationConfigurationException(msg);
            }
            if (!ObjectUtils.nullSafeEquals(defaultValue, aliasedDefaultValue)) {
                String msg = String.format("Misconfigured aliases: attribute '%s' in annotation [%s] and attribute '%s' in annotation [%s] must declare the same default value.", this.sourceAttributeName, this.sourceAnnotationType.getName(), aliasedAttribute.getName(), aliasedAttribute.getDeclaringClass().getName());
                throw new AnnotationConfigurationException(msg);
            }
        }

        private void validateAgainst(AliasDescriptor otherDescriptor) {
            this.validateDefaultValueConfiguration(otherDescriptor.sourceAttribute);
        }

        private boolean isOverrideFor(Class<? extends Annotation> metaAnnotationType) {
            return this.aliasedAnnotationType == metaAnnotationType;
        }

        private boolean isAliasFor(AliasDescriptor otherDescriptor) {
            for (AliasDescriptor lhs = this; lhs != null; lhs = lhs.getAttributeOverrideDescriptor()) {
                for (AliasDescriptor rhs = otherDescriptor; rhs != null; rhs = rhs.getAttributeOverrideDescriptor()) {
                    if (!lhs.aliasedAttribute.equals(rhs.aliasedAttribute)) continue;
                    return true;
                }
            }
            return false;
        }

        public List<String> getAttributeAliasNames() {
            if (this.isAliasPair) {
                return Collections.singletonList(this.aliasedAttributeName);
            }
            ArrayList<String> aliases = new ArrayList<String>();
            for (AliasDescriptor otherDescriptor : this.getOtherDescriptors()) {
                if (!this.isAliasFor(otherDescriptor)) continue;
                this.validateAgainst(otherDescriptor);
                aliases.add(otherDescriptor.sourceAttributeName);
            }
            return aliases;
        }

        private List<AliasDescriptor> getOtherDescriptors() {
            ArrayList<AliasDescriptor> otherDescriptors = new ArrayList<AliasDescriptor>();
            for (Method currentAttribute : AnnotationUtils.getAttributeMethods(this.sourceAnnotationType)) {
                AliasDescriptor otherDescriptor;
                if (this.sourceAttribute.equals(currentAttribute) || (otherDescriptor = AliasDescriptor.from(currentAttribute)) == null) continue;
                otherDescriptors.add(otherDescriptor);
            }
            return otherDescriptors;
        }

        @Nullable
        public String getAttributeOverrideName(Class<? extends Annotation> metaAnnotationType) {
            for (AliasDescriptor desc = this; desc != null; desc = desc.getAttributeOverrideDescriptor()) {
                if (!desc.isOverrideFor(metaAnnotationType)) continue;
                return desc.aliasedAttributeName;
            }
            return null;
        }

        @Nullable
        private AliasDescriptor getAttributeOverrideDescriptor() {
            if (this.isAliasPair) {
                return null;
            }
            return AliasDescriptor.from(this.aliasedAttribute);
        }

        private String getAliasedAttributeName(AliasFor aliasFor, Method attribute) {
            String attributeName = aliasFor.attribute();
            String value = aliasFor.value();
            boolean attributeDeclared = StringUtils.hasText(attributeName);
            boolean valueDeclared = StringUtils.hasText(value);
            if (attributeDeclared && valueDeclared) {
                String msg = String.format("In @AliasFor declared on attribute '%s' in annotation [%s], attribute 'attribute' and its alias 'value' are present with values of [%s] and [%s], but only one is permitted.", attribute.getName(), attribute.getDeclaringClass().getName(), attributeName, value);
                throw new AnnotationConfigurationException(msg);
            }
            attributeName = attributeDeclared ? attributeName : value;
            return StringUtils.hasText(attributeName) ? attributeName.trim() : attribute.getName();
        }

        public String toString() {
            return String.format("%s: @%s(%s) is an alias for @%s(%s)", this.getClass().getSimpleName(), this.sourceAnnotationType.getSimpleName(), this.sourceAttributeName, this.aliasedAnnotationType.getSimpleName(), this.aliasedAttributeName);
        }
    }

    private static class AnnotationCollector<A extends Annotation> {
        private final Class<A> annotationType;
        @Nullable
        private final Class<? extends Annotation> containerAnnotationType;
        private final boolean declaredMode;
        private final Set<AnnotatedElement> visited = new HashSet<AnnotatedElement>();
        private final Set<A> result = new LinkedHashSet<A>();

        AnnotationCollector(Class<A> annotationType, @Nullable Class<? extends Annotation> containerAnnotationType, boolean declaredMode) {
            this.annotationType = annotationType;
            this.containerAnnotationType = containerAnnotationType != null ? containerAnnotationType : AnnotationUtils.resolveContainerAnnotationType(annotationType);
            this.declaredMode = declaredMode;
        }

        Set<A> getResult(AnnotatedElement element) {
            this.process(element);
            return Collections.unmodifiableSet(this.result);
        }

        private void process(AnnotatedElement element) {
            if (this.visited.add(element)) {
                try {
                    Annotation[] annotations;
                    for (Annotation ann : annotations = this.declaredMode ? element.getDeclaredAnnotations() : element.getAnnotations()) {
                        Class<? extends Annotation> currentAnnotationType = ann.annotationType();
                        if (ObjectUtils.nullSafeEquals(this.annotationType, currentAnnotationType)) {
                            this.result.add(AnnotationUtils.synthesizeAnnotation(ann, element));
                            continue;
                        }
                        if (ObjectUtils.nullSafeEquals(this.containerAnnotationType, currentAnnotationType)) {
                            this.result.addAll(this.getValue(element, ann));
                            continue;
                        }
                        if (AnnotationUtils.isInJavaLangAnnotationPackage(currentAnnotationType)) continue;
                        this.process(currentAnnotationType);
                    }
                }
                catch (Throwable ex) {
                    AnnotationUtils.handleIntrospectionFailure(element, ex);
                }
            }
        }

        private List<A> getValue(AnnotatedElement element, Annotation annotation) {
            try {
                ArrayList<Annotation> synthesizedAnnotations = new ArrayList<Annotation>();
                Annotation[] value = (Annotation[])AnnotationUtils.getValue(annotation);
                if (value != null) {
                    for (Annotation anno : value) {
                        synthesizedAnnotations.add(AnnotationUtils.synthesizeAnnotation(anno, element));
                    }
                }
                return synthesizedAnnotations;
            }
            catch (Throwable ex) {
                AnnotationUtils.handleIntrospectionFailure(element, ex);
                return Collections.emptyList();
            }
        }
    }

    private static final class AnnotationCacheKey
    implements Comparable<AnnotationCacheKey> {
        private final AnnotatedElement element;
        private final Class<? extends Annotation> annotationType;

        public AnnotationCacheKey(AnnotatedElement element, Class<? extends Annotation> annotationType) {
            this.element = element;
            this.annotationType = annotationType;
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof AnnotationCacheKey)) {
                return false;
            }
            AnnotationCacheKey otherKey = (AnnotationCacheKey)other;
            return this.element.equals(otherKey.element) && this.annotationType.equals(otherKey.annotationType);
        }

        public int hashCode() {
            return this.element.hashCode() * 29 + this.annotationType.hashCode();
        }

        public String toString() {
            return "@" + this.annotationType + " on " + this.element;
        }

        @Override
        public int compareTo(AnnotationCacheKey other) {
            int result = this.element.toString().compareTo(other.element.toString());
            if (result == 0) {
                result = this.annotationType.getName().compareTo(other.annotationType.getName());
            }
            return result;
        }
    }
}

