/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.base.Joiner
 *  com.google.common.base.Joiner$MapJoiner
 *  com.google.common.base.Preconditions
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Maps
 *  javax.inject.Named
 *  javax.inject.Qualifier
 *  javax.inject.Scope
 */
package com.google.inject.internal;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.inject.BindingAnnotation;
import com.google.inject.Key;
import com.google.inject.ScopeAnnotation;
import com.google.inject.TypeLiteral;
import com.google.inject.internal.Errors;
import com.google.inject.internal.ErrorsException;
import com.google.inject.internal.util.Classes;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import javax.inject.Qualifier;
import javax.inject.Scope;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Annotations {
    private static final LoadingCache<Class<? extends Annotation>, Annotation> cache = CacheBuilder.newBuilder().weakKeys().build((CacheLoader)new CacheLoader<Class<? extends Annotation>, Annotation>(){

        public Annotation load(Class<? extends Annotation> input) {
            return Annotations.generateAnnotationImpl(input);
        }
    });
    private static final Joiner.MapJoiner JOINER = Joiner.on((String)", ").withKeyValueSeparator("=");
    private static final Function<Object, String> DEEP_TO_STRING_FN = new Function<Object, String>(){

        public String apply(Object arg) {
            String s = Arrays.deepToString(new Object[]{arg});
            return s.substring(1, s.length() - 1);
        }
    };
    private static final AnnotationChecker scopeChecker = new AnnotationChecker(Arrays.asList(ScopeAnnotation.class, Scope.class));
    private static final AnnotationChecker bindingAnnotationChecker = new AnnotationChecker(Arrays.asList(BindingAnnotation.class, Qualifier.class));

    public static boolean isMarker(Class<? extends Annotation> annotationType) {
        return annotationType.getDeclaredMethods().length == 0;
    }

    public static boolean isAllDefaultMethods(Class<? extends Annotation> annotationType) {
        boolean hasMethods = false;
        for (Method m : annotationType.getDeclaredMethods()) {
            hasMethods = true;
            if (m.getDefaultValue() != null) continue;
            return false;
        }
        return hasMethods;
    }

    public static <T extends Annotation> T generateAnnotation(Class<T> annotationType) {
        Preconditions.checkState((boolean)Annotations.isAllDefaultMethods(annotationType), (String)"%s is not all default methods", (Object[])new Object[]{annotationType});
        return (T)((Annotation)cache.getUnchecked(annotationType));
    }

    private static <T extends Annotation> T generateAnnotationImpl(final Class<T> annotationType) {
        ImmutableMap<String, Object> members = Annotations.resolveMembers(annotationType);
        return (T)((Annotation)annotationType.cast(Proxy.newProxyInstance(annotationType.getClassLoader(), new Class[]{annotationType}, new InvocationHandler((Map)members){
            final /* synthetic */ Map val$members;
            {
                this.val$members = map;
            }

            public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
                String name = method.getName();
                if (name.equals("annotationType")) {
                    return annotationType;
                }
                if (name.equals("toString")) {
                    return Annotations.annotationToString(annotationType, this.val$members);
                }
                if (name.equals("hashCode")) {
                    return Annotations.annotationHashCode(annotationType, this.val$members);
                }
                if (name.equals("equals")) {
                    return Annotations.annotationEquals(annotationType, this.val$members, args[0]);
                }
                return this.val$members.get(name);
            }
        })));
    }

    private static ImmutableMap<String, Object> resolveMembers(Class<? extends Annotation> annotationType) {
        ImmutableMap.Builder result = ImmutableMap.builder();
        for (Method method : annotationType.getDeclaredMethods()) {
            result.put((Object)method.getName(), method.getDefaultValue());
        }
        return result.build();
    }

    private static boolean annotationEquals(Class<? extends Annotation> type, Map<String, Object> members, Object other) throws Exception {
        if (!type.isInstance(other)) {
            return false;
        }
        for (Method method : type.getDeclaredMethods()) {
            String name = method.getName();
            if (Arrays.deepEquals(new Object[]{method.invoke(other, new Object[0])}, new Object[]{members.get(name)})) continue;
            return false;
        }
        return true;
    }

    private static int annotationHashCode(Class<? extends Annotation> type, Map<String, Object> members) throws Exception {
        int result = 0;
        for (Method method : type.getDeclaredMethods()) {
            String name = method.getName();
            Object value = members.get(name);
            result += 127 * name.hashCode() ^ Arrays.deepHashCode(new Object[]{value}) - 31;
        }
        return result;
    }

    private static String annotationToString(Class<? extends Annotation> type, Map<String, Object> members) throws Exception {
        StringBuilder sb = new StringBuilder().append("@").append(type.getName()).append("(");
        JOINER.appendTo(sb, Maps.transformValues(members, DEEP_TO_STRING_FN));
        return sb.append(")").toString();
    }

    public static boolean isRetainedAtRuntime(Class<? extends Annotation> annotationType) {
        Retention retention = annotationType.getAnnotation(Retention.class);
        return retention != null && retention.value() == RetentionPolicy.RUNTIME;
    }

    public static Class<? extends Annotation> findScopeAnnotation(Errors errors, Class<?> implementation) {
        return Annotations.findScopeAnnotation(errors, implementation.getAnnotations());
    }

    public static Class<? extends Annotation> findScopeAnnotation(Errors errors, Annotation[] annotations) {
        Class<? extends Annotation> found = null;
        for (Annotation annotation : annotations) {
            Class<? extends Annotation> annotationType = annotation.annotationType();
            if (!Annotations.isScopeAnnotation(annotationType)) continue;
            if (found != null) {
                errors.duplicateScopeAnnotations(found, annotationType);
                continue;
            }
            found = annotationType;
        }
        return found;
    }

    public static boolean isScopeAnnotation(Class<? extends Annotation> annotationType) {
        return scopeChecker.hasAnnotations(annotationType);
    }

    public static void checkForMisplacedScopeAnnotations(Class<?> type, Object source, Errors errors) {
        if (Classes.isConcrete(type)) {
            return;
        }
        Class<? extends Annotation> scopeAnnotation = Annotations.findScopeAnnotation(errors, type);
        if (scopeAnnotation != null) {
            errors.withSource(type).scopeAnnotationOnAbstractType(scopeAnnotation, type, source);
        }
    }

    public static Key<?> getKey(TypeLiteral<?> type, Member member, Annotation[] annotations, Errors errors) throws ErrorsException {
        int numErrorsBefore = errors.size();
        Annotation found = Annotations.findBindingAnnotation(errors, member, annotations);
        errors.throwIfNewErrors(numErrorsBefore);
        return found == null ? Key.get(type) : Key.get(type, found);
    }

    public static Annotation findBindingAnnotation(Errors errors, Member member, Annotation[] annotations) {
        Annotation found = null;
        for (Annotation annotation : annotations) {
            Class<? extends Annotation> annotationType = annotation.annotationType();
            if (!Annotations.isBindingAnnotation(annotationType)) continue;
            if (found != null) {
                errors.duplicateBindingAnnotations(member, found.annotationType(), annotationType);
                continue;
            }
            found = annotation;
        }
        return found;
    }

    public static boolean isBindingAnnotation(Class<? extends Annotation> annotationType) {
        return bindingAnnotationChecker.hasAnnotations(annotationType);
    }

    public static Annotation canonicalizeIfNamed(Annotation annotation) {
        if (annotation instanceof javax.inject.Named) {
            return Names.named(((javax.inject.Named)annotation).value());
        }
        return annotation;
    }

    public static Class<? extends Annotation> canonicalizeIfNamed(Class<? extends Annotation> annotationType) {
        if (annotationType == javax.inject.Named.class) {
            return Named.class;
        }
        return annotationType;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static class AnnotationChecker {
        private final Collection<Class<? extends Annotation>> annotationTypes;
        private CacheLoader<Class<? extends Annotation>, Boolean> hasAnnotations = new CacheLoader<Class<? extends Annotation>, Boolean>(){

            public Boolean load(Class<? extends Annotation> annotationType) {
                for (Annotation annotation : annotationType.getAnnotations()) {
                    if (!AnnotationChecker.this.annotationTypes.contains(annotation.annotationType())) continue;
                    return true;
                }
                return false;
            }
        };
        final LoadingCache<Class<? extends Annotation>, Boolean> cache = CacheBuilder.newBuilder().weakKeys().build(this.hasAnnotations);

        AnnotationChecker(Collection<Class<? extends Annotation>> annotationTypes) {
            this.annotationTypes = annotationTypes;
        }

        boolean hasAnnotations(Class<? extends Annotation> annotated) {
            return (Boolean)this.cache.getUnchecked(annotated);
        }
    }
}

