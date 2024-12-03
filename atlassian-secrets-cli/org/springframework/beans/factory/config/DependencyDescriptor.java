/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.reflect.KProperty
 *  kotlin.reflect.jvm.ReflectJvmMapping
 */
package org.springframework.beans.factory.config;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;
import kotlin.reflect.KProperty;
import kotlin.reflect.jvm.ReflectJvmMapping;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.core.KotlinDetector;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

public class DependencyDescriptor
extends InjectionPoint
implements Serializable {
    private final Class<?> declaringClass;
    @Nullable
    private String methodName;
    @Nullable
    private Class<?>[] parameterTypes;
    private int parameterIndex;
    @Nullable
    private String fieldName;
    private final boolean required;
    private final boolean eager;
    private int nestingLevel = 1;
    @Nullable
    private Class<?> containingClass;
    @Nullable
    private volatile transient ResolvableType resolvableType;
    @Nullable
    private volatile transient TypeDescriptor typeDescriptor;

    public DependencyDescriptor(MethodParameter methodParameter, boolean required) {
        this(methodParameter, required, true);
    }

    public DependencyDescriptor(MethodParameter methodParameter, boolean required, boolean eager) {
        super(methodParameter);
        this.declaringClass = methodParameter.getDeclaringClass();
        if (methodParameter.getMethod() != null) {
            this.methodName = methodParameter.getMethod().getName();
        }
        this.parameterTypes = methodParameter.getExecutable().getParameterTypes();
        this.parameterIndex = methodParameter.getParameterIndex();
        this.containingClass = methodParameter.getContainingClass();
        this.required = required;
        this.eager = eager;
    }

    public DependencyDescriptor(Field field, boolean required) {
        this(field, required, true);
    }

    public DependencyDescriptor(Field field, boolean required, boolean eager) {
        super(field);
        this.declaringClass = field.getDeclaringClass();
        this.fieldName = field.getName();
        this.required = required;
        this.eager = eager;
    }

    public DependencyDescriptor(DependencyDescriptor original) {
        super(original);
        this.declaringClass = original.declaringClass;
        this.methodName = original.methodName;
        this.parameterTypes = original.parameterTypes;
        this.parameterIndex = original.parameterIndex;
        this.fieldName = original.fieldName;
        this.containingClass = original.containingClass;
        this.required = original.required;
        this.eager = original.eager;
        this.nestingLevel = original.nestingLevel;
    }

    public boolean isRequired() {
        if (!this.required) {
            return false;
        }
        if (this.field != null) {
            return this.field.getType() != Optional.class && !this.hasNullableAnnotation() && (!KotlinDetector.isKotlinReflectPresent() || !KotlinDetector.isKotlinType(this.field.getDeclaringClass()) || !KotlinDelegate.isNullable(this.field));
        }
        return !this.obtainMethodParameter().isOptional();
    }

    private boolean hasNullableAnnotation() {
        for (Annotation ann : this.getAnnotations()) {
            if (!"Nullable".equals(ann.annotationType().getSimpleName())) continue;
            return true;
        }
        return false;
    }

    public boolean isEager() {
        return this.eager;
    }

    @Nullable
    public Object resolveNotUnique(ResolvableType type, Map<String, Object> matchingBeans) throws BeansException {
        throw new NoUniqueBeanDefinitionException(type, matchingBeans.keySet());
    }

    @Deprecated
    @Nullable
    public Object resolveNotUnique(Class<?> type, Map<String, Object> matchingBeans) throws BeansException {
        throw new NoUniqueBeanDefinitionException(type, matchingBeans.keySet());
    }

    @Nullable
    public Object resolveShortcut(BeanFactory beanFactory) throws BeansException {
        return null;
    }

    public Object resolveCandidate(String beanName, Class<?> requiredType, BeanFactory beanFactory) throws BeansException {
        return beanFactory.getBean(beanName);
    }

    public void increaseNestingLevel() {
        ++this.nestingLevel;
        this.resolvableType = null;
        if (this.methodParameter != null) {
            this.methodParameter = this.methodParameter.nested();
        }
    }

    public void setContainingClass(Class<?> containingClass) {
        this.containingClass = containingClass;
        this.resolvableType = null;
        if (this.methodParameter != null) {
            this.methodParameter = this.methodParameter.withContainingClass(containingClass);
        }
    }

    public ResolvableType getResolvableType() {
        ResolvableType resolvableType = this.resolvableType;
        if (resolvableType == null) {
            this.resolvableType = resolvableType = this.field != null ? ResolvableType.forField(this.field, this.nestingLevel, this.containingClass) : ResolvableType.forMethodParameter(this.obtainMethodParameter());
        }
        return resolvableType;
    }

    public TypeDescriptor getTypeDescriptor() {
        TypeDescriptor typeDescriptor = this.typeDescriptor;
        if (typeDescriptor == null) {
            this.typeDescriptor = typeDescriptor = this.field != null ? new TypeDescriptor(this.getResolvableType(), this.getDependencyType(), this.getAnnotations()) : new TypeDescriptor(this.obtainMethodParameter());
        }
        return typeDescriptor;
    }

    public boolean fallbackMatchAllowed() {
        return false;
    }

    public DependencyDescriptor forFallbackMatch() {
        return new DependencyDescriptor(this){

            @Override
            public boolean fallbackMatchAllowed() {
                return true;
            }
        };
    }

    public void initParameterNameDiscovery(@Nullable ParameterNameDiscoverer parameterNameDiscoverer) {
        if (this.methodParameter != null) {
            this.methodParameter.initParameterNameDiscovery(parameterNameDiscoverer);
        }
    }

    @Nullable
    public String getDependencyName() {
        return this.field != null ? this.field.getName() : this.obtainMethodParameter().getParameterName();
    }

    public Class<?> getDependencyType() {
        if (this.field != null) {
            if (this.nestingLevel > 1) {
                Type arg;
                Type type = this.field.getGenericType();
                for (int i = 2; i <= this.nestingLevel; ++i) {
                    if (!(type instanceof ParameterizedType)) continue;
                    Type[] args = ((ParameterizedType)type).getActualTypeArguments();
                    type = args[args.length - 1];
                }
                if (type instanceof Class) {
                    return (Class)type;
                }
                if (type instanceof ParameterizedType && (arg = ((ParameterizedType)type).getRawType()) instanceof Class) {
                    return (Class)arg;
                }
                return Object.class;
            }
            return this.field.getType();
        }
        return this.obtainMethodParameter().getNestedParameterType();
    }

    @Override
    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!super.equals(other)) {
            return false;
        }
        DependencyDescriptor otherDesc = (DependencyDescriptor)other;
        return this.required == otherDesc.required && this.eager == otherDesc.eager && this.nestingLevel == otherDesc.nestingLevel && this.containingClass == otherDesc.containingClass;
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + ObjectUtils.nullSafeHashCode(this.containingClass);
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        try {
            if (this.fieldName != null) {
                this.field = this.declaringClass.getDeclaredField(this.fieldName);
            } else {
                this.methodParameter = this.methodName != null ? new MethodParameter(this.declaringClass.getDeclaredMethod(this.methodName, this.parameterTypes), this.parameterIndex) : new MethodParameter(this.declaringClass.getDeclaredConstructor(this.parameterTypes), this.parameterIndex);
                for (int i = 1; i < this.nestingLevel; ++i) {
                    this.methodParameter = this.methodParameter.nested();
                }
            }
        }
        catch (Throwable ex) {
            throw new IllegalStateException("Could not find original class structure", ex);
        }
    }

    private static class KotlinDelegate {
        private KotlinDelegate() {
        }

        public static boolean isNullable(Field field) {
            KProperty property = ReflectJvmMapping.getKotlinProperty((Field)field);
            return property != null && property.getReturnType().isMarkedNullable();
        }
    }
}

