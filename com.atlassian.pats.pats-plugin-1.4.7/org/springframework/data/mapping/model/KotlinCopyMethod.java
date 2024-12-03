/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.jvm.JvmClassMappingKt
 *  kotlin.reflect.KClass
 *  kotlin.reflect.KFunction
 *  kotlin.reflect.KParameter
 *  kotlin.reflect.KParameter$Kind
 *  kotlin.reflect.KType
 *  kotlin.reflect.full.KClasses
 *  kotlin.reflect.jvm.ReflectJvmMapping
 *  org.springframework.core.ResolvableType
 *  org.springframework.util.Assert
 */
package org.springframework.data.mapping.model;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import kotlin.jvm.JvmClassMappingKt;
import kotlin.reflect.KClass;
import kotlin.reflect.KFunction;
import kotlin.reflect.KParameter;
import kotlin.reflect.KType;
import kotlin.reflect.full.KClasses;
import kotlin.reflect.jvm.ReflectJvmMapping;
import org.springframework.core.ResolvableType;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.model.KotlinDefaultMask;
import org.springframework.util.Assert;

class KotlinCopyMethod {
    private final Method publicCopyMethod;
    private final Method syntheticCopyMethod;
    private final int parameterCount;
    private final KFunction<?> copyFunction;

    private KotlinCopyMethod(Method publicCopyMethod, Method syntheticCopyMethod) {
        this.publicCopyMethod = publicCopyMethod;
        this.syntheticCopyMethod = syntheticCopyMethod;
        this.copyFunction = ReflectJvmMapping.getKotlinFunction((Method)publicCopyMethod);
        this.parameterCount = this.copyFunction != null ? this.copyFunction.getParameters().size() : 0;
    }

    public static Optional<KotlinCopyMethod> findCopyMethod(Class<?> type) {
        Assert.notNull(type, (String)"Type must not be null!");
        Optional<Method> syntheticCopyMethod = KotlinCopyMethod.findSyntheticCopyMethod(type);
        if (!syntheticCopyMethod.isPresent()) {
            return Optional.empty();
        }
        Optional<KotlinCopyMethod> publicCopyMethod = syntheticCopyMethod.flatMap(KotlinCopyMethod::findPublicCopyMethod);
        return publicCopyMethod.map(method -> new KotlinCopyMethod((Method)method, (Method)syntheticCopyMethod.get()));
    }

    public Method getPublicCopyMethod() {
        return this.publicCopyMethod;
    }

    public Method getSyntheticCopyMethod() {
        return this.syntheticCopyMethod;
    }

    public int getParameterCount() {
        return this.parameterCount;
    }

    public KFunction<?> getCopyFunction() {
        return this.copyFunction;
    }

    boolean supportsProperty(PersistentProperty<?> property) {
        return this.forProperty(property).isPresent();
    }

    Optional<KotlinCopyByProperty> forProperty(PersistentProperty<?> property) {
        int index = KotlinCopyByProperty.findIndex(this.copyFunction, property.getName());
        if (index == -1) {
            return Optional.empty();
        }
        return Optional.of(new KotlinCopyByProperty(this.copyFunction, property));
    }

    boolean shouldUsePublicCopyMethod(PersistentEntity<?, ?> entity) {
        ArrayList persistentProperties = new ArrayList();
        entity.doWithProperties(persistentProperties::add);
        if (persistentProperties.size() > 1) {
            return false;
        }
        if (this.publicCopyMethod.getParameterCount() != 1) {
            return false;
        }
        if (Modifier.isStatic(this.publicCopyMethod.getModifiers())) {
            return false;
        }
        Class<?>[] parameterTypes = this.publicCopyMethod.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; ++i) {
            if (parameterTypes[i].equals(((PersistentProperty)persistentProperties.get(i)).getType())) continue;
            return false;
        }
        return true;
    }

    private static Optional<Method> findPublicCopyMethod(Method defaultKotlinMethod) {
        Class<?> type = defaultKotlinMethod.getDeclaringClass();
        KClass kotlinClass = JvmClassMappingKt.getKotlinClass(type);
        KFunction primaryConstructor = KClasses.getPrimaryConstructor((KClass)kotlinClass);
        if (primaryConstructor == null) {
            return Optional.empty();
        }
        List<KParameter> constructorArguments = KotlinCopyMethod.getComponentArguments(primaryConstructor);
        return Arrays.stream(type.getDeclaredMethods()).filter(it -> it.getName().equals("copy") && !it.isSynthetic() && !Modifier.isStatic(it.getModifiers()) && it.getReturnType().equals(type) && it.getParameterCount() == constructorArguments.size()).filter(it -> {
            KFunction kotlinFunction = ReflectJvmMapping.getKotlinFunction((Method)it);
            if (kotlinFunction == null) {
                return false;
            }
            return KotlinCopyMethod.parameterMatches(constructorArguments, kotlinFunction);
        }).findFirst();
    }

    private static boolean parameterMatches(List<KParameter> constructorArguments, KFunction<?> kotlinFunction) {
        boolean foundInstance = false;
        int constructorArgIndex = 0;
        for (KParameter parameter : kotlinFunction.getParameters()) {
            if (parameter.getKind() == KParameter.Kind.INSTANCE) {
                foundInstance = true;
                continue;
            }
            if (constructorArguments.size() <= constructorArgIndex) {
                return false;
            }
            KParameter constructorParameter = constructorArguments.get(constructorArgIndex);
            if (constructorParameter.getName() == null || !constructorParameter.getName().equals(parameter.getName()) || !constructorParameter.getType().equals(parameter.getType())) {
                return false;
            }
            ++constructorArgIndex;
        }
        return foundInstance;
    }

    private static Optional<Method> findSyntheticCopyMethod(Class<?> type) {
        KClass kotlinClass = JvmClassMappingKt.getKotlinClass(type);
        KFunction primaryConstructor = KClasses.getPrimaryConstructor((KClass)kotlinClass);
        if (primaryConstructor == null) {
            return Optional.empty();
        }
        return Arrays.stream(type.getDeclaredMethods()).filter(it -> it.getName().equals("copy$default") && Modifier.isStatic(it.getModifiers()) && it.getReturnType().equals(type)).filter(Method::isSynthetic).filter(it -> KotlinCopyMethod.matchesPrimaryConstructor(it.getParameterTypes(), primaryConstructor)).findFirst();
    }

    private static boolean matchesPrimaryConstructor(Class<?>[] parameterTypes, KFunction<?> primaryConstructor) {
        List<KParameter> constructorArguments = KotlinCopyMethod.getComponentArguments(primaryConstructor);
        int defaultingArgs = KotlinDefaultMask.from(primaryConstructor, kParameter -> false).getDefaulting().length;
        if (parameterTypes.length != 1 + constructorArguments.size() + defaultingArgs + 1) {
            return false;
        }
        if (!KotlinCopyMethod.isAssignableFrom(parameterTypes[0], primaryConstructor.getReturnType())) {
            return false;
        }
        for (int i = 0; i < constructorArguments.size(); ++i) {
            KParameter kParameter2 = constructorArguments.get(i);
            if (KotlinCopyMethod.isAssignableFrom(parameterTypes[i + 1], kParameter2.getType())) continue;
            return false;
        }
        return true;
    }

    private static List<KParameter> getComponentArguments(KFunction<?> primaryConstructor) {
        return primaryConstructor.getParameters().stream().filter(it -> it.getKind() == KParameter.Kind.VALUE).collect(Collectors.toList());
    }

    private static boolean isAssignableFrom(Class<?> target, KType source) {
        Type parameterType = ReflectJvmMapping.getJavaType((KType)source);
        Class rawClass = ResolvableType.forType((Type)parameterType).getRawClass();
        return rawClass == null || target.isAssignableFrom(rawClass);
    }

    static class KotlinCopyByProperty {
        private final int parameterPosition;
        private final int parameterCount;
        private final KotlinDefaultMask defaultMask;

        KotlinCopyByProperty(KFunction<?> copyFunction, PersistentProperty<?> property) {
            this.parameterPosition = KotlinCopyByProperty.findIndex(copyFunction, property.getName());
            this.parameterCount = copyFunction.getParameters().size();
            this.defaultMask = KotlinDefaultMask.from(copyFunction, it -> property.getName().equals(it.getName()));
        }

        static int findIndex(KFunction<?> function, String parameterName) {
            for (KParameter parameter : function.getParameters()) {
                if (!parameterName.equals(parameter.getName())) continue;
                return parameter.getIndex();
            }
            return -1;
        }

        public int getParameterPosition() {
            return this.parameterPosition;
        }

        public int getParameterCount() {
            return this.parameterCount;
        }

        public KotlinDefaultMask getDefaultMask() {
            return this.defaultMask;
        }
    }
}

