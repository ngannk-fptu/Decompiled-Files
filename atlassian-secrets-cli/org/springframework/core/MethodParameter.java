/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.reflect.KFunction
 *  kotlin.reflect.KParameter
 *  kotlin.reflect.KParameter$Kind
 *  kotlin.reflect.jvm.ReflectJvmMapping
 */
package org.springframework.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import kotlin.reflect.KFunction;
import kotlin.reflect.KParameter;
import kotlin.reflect.jvm.ReflectJvmMapping;
import org.springframework.core.KotlinDetector;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

public class MethodParameter {
    private static final Annotation[] EMPTY_ANNOTATION_ARRAY = new Annotation[0];
    private final Executable executable;
    private final int parameterIndex;
    @Nullable
    private volatile Parameter parameter;
    private int nestingLevel = 1;
    @Nullable
    Map<Integer, Integer> typeIndexesPerLevel;
    @Nullable
    private volatile Class<?> containingClass;
    @Nullable
    private volatile Class<?> parameterType;
    @Nullable
    private volatile Type genericParameterType;
    @Nullable
    private volatile Annotation[] parameterAnnotations;
    @Nullable
    private volatile ParameterNameDiscoverer parameterNameDiscoverer;
    @Nullable
    private volatile String parameterName;
    @Nullable
    private volatile MethodParameter nestedMethodParameter;

    public MethodParameter(Method method, int parameterIndex) {
        this(method, parameterIndex, 1);
    }

    public MethodParameter(Method method, int parameterIndex, int nestingLevel) {
        Assert.notNull((Object)method, "Method must not be null");
        this.executable = method;
        this.parameterIndex = MethodParameter.validateIndex(method, parameterIndex);
        this.nestingLevel = nestingLevel;
    }

    public MethodParameter(Constructor<?> constructor, int parameterIndex) {
        this(constructor, parameterIndex, 1);
    }

    public MethodParameter(Constructor<?> constructor, int parameterIndex, int nestingLevel) {
        Assert.notNull(constructor, "Constructor must not be null");
        this.executable = constructor;
        this.parameterIndex = MethodParameter.validateIndex(constructor, parameterIndex);
        this.nestingLevel = nestingLevel;
    }

    public MethodParameter(MethodParameter original) {
        Assert.notNull((Object)original, "Original must not be null");
        this.executable = original.executable;
        this.parameterIndex = original.parameterIndex;
        this.parameter = original.parameter;
        this.nestingLevel = original.nestingLevel;
        this.typeIndexesPerLevel = original.typeIndexesPerLevel;
        this.containingClass = original.containingClass;
        this.parameterType = original.parameterType;
        this.genericParameterType = original.genericParameterType;
        this.parameterAnnotations = original.parameterAnnotations;
        this.parameterNameDiscoverer = original.parameterNameDiscoverer;
        this.parameterName = original.parameterName;
    }

    @Nullable
    public Method getMethod() {
        return this.executable instanceof Method ? (Method)this.executable : null;
    }

    @Nullable
    public Constructor<?> getConstructor() {
        return this.executable instanceof Constructor ? (Constructor)this.executable : null;
    }

    public Class<?> getDeclaringClass() {
        return this.executable.getDeclaringClass();
    }

    public Member getMember() {
        return this.executable;
    }

    public AnnotatedElement getAnnotatedElement() {
        return this.executable;
    }

    public Executable getExecutable() {
        return this.executable;
    }

    public Parameter getParameter() {
        Parameter parameter = this.parameter;
        if (parameter == null) {
            this.parameter = parameter = this.getExecutable().getParameters()[this.parameterIndex];
        }
        return parameter;
    }

    public int getParameterIndex() {
        return this.parameterIndex;
    }

    public void increaseNestingLevel() {
        ++this.nestingLevel;
    }

    public void decreaseNestingLevel() {
        this.getTypeIndexesPerLevel().remove(this.nestingLevel);
        --this.nestingLevel;
    }

    public int getNestingLevel() {
        return this.nestingLevel;
    }

    public void setTypeIndexForCurrentLevel(int typeIndex) {
        this.getTypeIndexesPerLevel().put(this.nestingLevel, typeIndex);
    }

    @Nullable
    public Integer getTypeIndexForCurrentLevel() {
        return this.getTypeIndexForLevel(this.nestingLevel);
    }

    @Nullable
    public Integer getTypeIndexForLevel(int nestingLevel) {
        return this.getTypeIndexesPerLevel().get(nestingLevel);
    }

    private Map<Integer, Integer> getTypeIndexesPerLevel() {
        if (this.typeIndexesPerLevel == null) {
            this.typeIndexesPerLevel = new HashMap<Integer, Integer>(4);
        }
        return this.typeIndexesPerLevel;
    }

    public MethodParameter nested() {
        MethodParameter nestedParam = this.nestedMethodParameter;
        if (nestedParam != null) {
            return nestedParam;
        }
        nestedParam = this.clone();
        nestedParam.nestingLevel = this.nestingLevel + 1;
        this.nestedMethodParameter = nestedParam;
        return nestedParam;
    }

    public boolean isOptional() {
        return this.getParameterType() == Optional.class || this.hasNullableAnnotation() || KotlinDetector.isKotlinType(this.getContainingClass()) && KotlinDelegate.isOptional(this);
    }

    private boolean hasNullableAnnotation() {
        for (Annotation ann : this.getParameterAnnotations()) {
            if (!"Nullable".equals(ann.annotationType().getSimpleName())) continue;
            return true;
        }
        return false;
    }

    public MethodParameter nestedIfOptional() {
        return this.getParameterType() == Optional.class ? this.nested() : this;
    }

    void setContainingClass(Class<?> containingClass) {
        this.containingClass = containingClass;
    }

    public Class<?> getContainingClass() {
        Class<?> containingClass = this.containingClass;
        return containingClass != null ? containingClass : this.getDeclaringClass();
    }

    void setParameterType(@Nullable Class<?> parameterType) {
        this.parameterType = parameterType;
    }

    public Class<?> getParameterType() {
        Class<Object> paramType = this.parameterType;
        if (paramType == null) {
            Method method;
            paramType = this.parameterIndex < 0 ? ((method = this.getMethod()) != null ? method.getReturnType() : Void.TYPE) : this.executable.getParameterTypes()[this.parameterIndex];
            this.parameterType = paramType;
        }
        return paramType;
    }

    public Type getGenericParameterType() {
        Type paramType = this.genericParameterType;
        if (paramType == null) {
            if (this.parameterIndex < 0) {
                Method method = this.getMethod();
                paramType = method != null ? method.getGenericReturnType() : Void.TYPE;
            } else {
                Type[] genericParameterTypes = this.executable.getGenericParameterTypes();
                int index = this.parameterIndex;
                if (this.executable instanceof Constructor && ClassUtils.isInnerClass(this.executable.getDeclaringClass()) && genericParameterTypes.length == this.executable.getParameterCount() - 1) {
                    index = this.parameterIndex - 1;
                }
                paramType = index >= 0 && index < genericParameterTypes.length ? genericParameterTypes[index] : this.getParameterType();
            }
            this.genericParameterType = paramType;
        }
        return paramType;
    }

    public Class<?> getNestedParameterType() {
        if (this.nestingLevel > 1) {
            Type arg;
            Type type = this.getGenericParameterType();
            for (int i = 2; i <= this.nestingLevel; ++i) {
                if (!(type instanceof ParameterizedType)) continue;
                Type[] args = ((ParameterizedType)type).getActualTypeArguments();
                Integer index = this.getTypeIndexForLevel(i);
                type = args[index != null ? index : args.length - 1];
            }
            if (type instanceof Class) {
                return (Class)type;
            }
            if (type instanceof ParameterizedType && (arg = ((ParameterizedType)type).getRawType()) instanceof Class) {
                return (Class)arg;
            }
            return Object.class;
        }
        return this.getParameterType();
    }

    public Type getNestedGenericParameterType() {
        if (this.nestingLevel > 1) {
            Type type = this.getGenericParameterType();
            for (int i = 2; i <= this.nestingLevel; ++i) {
                if (!(type instanceof ParameterizedType)) continue;
                Type[] args = ((ParameterizedType)type).getActualTypeArguments();
                Integer index = this.getTypeIndexForLevel(i);
                type = args[index != null ? index : args.length - 1];
            }
            return type;
        }
        return this.getGenericParameterType();
    }

    public Annotation[] getMethodAnnotations() {
        return this.adaptAnnotationArray(this.getAnnotatedElement().getAnnotations());
    }

    @Nullable
    public <A extends Annotation> A getMethodAnnotation(Class<A> annotationType) {
        A annotation = this.getAnnotatedElement().getAnnotation(annotationType);
        return annotation != null ? (A)this.adaptAnnotation(annotation) : null;
    }

    public <A extends Annotation> boolean hasMethodAnnotation(Class<A> annotationType) {
        return this.getAnnotatedElement().isAnnotationPresent(annotationType);
    }

    public Annotation[] getParameterAnnotations() {
        Annotation[] paramAnns = this.parameterAnnotations;
        if (paramAnns == null) {
            Annotation[][] annotationArray = this.executable.getParameterAnnotations();
            int index = this.parameterIndex;
            if (this.executable instanceof Constructor && ClassUtils.isInnerClass(this.executable.getDeclaringClass()) && annotationArray.length == this.executable.getParameterCount() - 1) {
                index = this.parameterIndex - 1;
            }
            paramAnns = index >= 0 && index < annotationArray.length ? this.adaptAnnotationArray(annotationArray[index]) : EMPTY_ANNOTATION_ARRAY;
            this.parameterAnnotations = paramAnns;
        }
        return paramAnns;
    }

    public boolean hasParameterAnnotations() {
        return this.getParameterAnnotations().length != 0;
    }

    @Nullable
    public <A extends Annotation> A getParameterAnnotation(Class<A> annotationType) {
        Annotation[] anns;
        for (Annotation ann : anns = this.getParameterAnnotations()) {
            if (!annotationType.isInstance(ann)) continue;
            return (A)ann;
        }
        return null;
    }

    public <A extends Annotation> boolean hasParameterAnnotation(Class<A> annotationType) {
        return this.getParameterAnnotation(annotationType) != null;
    }

    public void initParameterNameDiscovery(@Nullable ParameterNameDiscoverer parameterNameDiscoverer) {
        this.parameterNameDiscoverer = parameterNameDiscoverer;
    }

    @Nullable
    public String getParameterName() {
        ParameterNameDiscoverer discoverer = this.parameterNameDiscoverer;
        if (discoverer != null) {
            String[] parameterNames = null;
            if (this.executable instanceof Method) {
                parameterNames = discoverer.getParameterNames((Method)this.executable);
            } else if (this.executable instanceof Constructor) {
                parameterNames = discoverer.getParameterNames((Constructor)this.executable);
            }
            if (parameterNames != null) {
                this.parameterName = parameterNames[this.parameterIndex];
            }
            this.parameterNameDiscoverer = null;
        }
        return this.parameterName;
    }

    protected <A extends Annotation> A adaptAnnotation(A annotation) {
        return annotation;
    }

    protected Annotation[] adaptAnnotationArray(Annotation[] annotations) {
        return annotations;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MethodParameter)) {
            return false;
        }
        MethodParameter otherParam = (MethodParameter)other;
        return this.parameterIndex == otherParam.parameterIndex && this.getExecutable().equals(otherParam.getExecutable());
    }

    public int hashCode() {
        return this.getExecutable().hashCode() * 31 + this.parameterIndex;
    }

    public String toString() {
        Method method = this.getMethod();
        return (method != null ? "method '" + method.getName() + "'" : "constructor") + " parameter " + this.parameterIndex;
    }

    public MethodParameter clone() {
        return new MethodParameter(this);
    }

    @Deprecated
    public static MethodParameter forMethodOrConstructor(Object methodOrConstructor, int parameterIndex) {
        if (!(methodOrConstructor instanceof Executable)) {
            throw new IllegalArgumentException("Given object [" + methodOrConstructor + "] is neither a Method nor a Constructor");
        }
        return MethodParameter.forExecutable((Executable)methodOrConstructor, parameterIndex);
    }

    public static MethodParameter forExecutable(Executable executable, int parameterIndex) {
        if (executable instanceof Method) {
            return new MethodParameter((Method)executable, parameterIndex);
        }
        if (executable instanceof Constructor) {
            return new MethodParameter((Constructor)executable, parameterIndex);
        }
        throw new IllegalArgumentException("Not a Method/Constructor: " + executable);
    }

    public static MethodParameter forParameter(Parameter parameter) {
        return MethodParameter.forExecutable(parameter.getDeclaringExecutable(), MethodParameter.findParameterIndex(parameter));
    }

    protected static int findParameterIndex(Parameter parameter) {
        Executable executable = parameter.getDeclaringExecutable();
        Parameter[] allParams = executable.getParameters();
        for (int i = 0; i < allParams.length; ++i) {
            if (parameter != allParams[i]) continue;
            return i;
        }
        throw new IllegalArgumentException("Given parameter [" + parameter + "] does not match any parameter in the declaring executable");
    }

    private static int validateIndex(Executable executable, int parameterIndex) {
        int count = executable.getParameterCount();
        Assert.isTrue(parameterIndex < count, () -> "Parameter index needs to be between -1 and " + (count - 1));
        return parameterIndex;
    }

    private static class KotlinDelegate {
        private KotlinDelegate() {
        }

        public static boolean isOptional(MethodParameter param) {
            Method method = param.getMethod();
            Constructor<?> ctor = param.getConstructor();
            int index = param.getParameterIndex();
            if (method != null && index == -1) {
                KFunction function = ReflectJvmMapping.getKotlinFunction((Method)method);
                return function != null && function.getReturnType().isMarkedNullable();
            }
            KFunction function = null;
            Predicate<KParameter> predicate = null;
            if (method != null) {
                function = ReflectJvmMapping.getKotlinFunction((Method)method);
                predicate = p -> KParameter.Kind.VALUE.equals((Object)p.getKind());
            } else if (ctor != null) {
                function = ReflectJvmMapping.getKotlinFunction(ctor);
                predicate = p -> KParameter.Kind.VALUE.equals((Object)p.getKind()) || KParameter.Kind.INSTANCE.equals((Object)p.getKind());
            }
            if (function != null) {
                List parameters = function.getParameters();
                KParameter parameter = (KParameter)parameters.stream().filter(predicate).collect(Collectors.toList()).get(index);
                return parameter.getType().isMarkedNullable() || parameter.isOptional();
            }
            return false;
        }
    }
}

