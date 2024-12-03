/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.jvm.JvmClassMappingKt
 *  kotlin.reflect.KClass
 *  kotlin.reflect.KFunction
 *  kotlin.reflect.full.KClasses
 *  kotlin.reflect.jvm.ReflectJvmMapping
 *  org.springframework.core.DefaultParameterNameDiscoverer
 *  org.springframework.core.ParameterNameDiscoverer
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.data.mapping.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import kotlin.jvm.JvmClassMappingKt;
import kotlin.reflect.KClass;
import kotlin.reflect.KFunction;
import kotlin.reflect.full.KClasses;
import kotlin.reflect.jvm.ReflectJvmMapping;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.PreferredConstructor;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.data.util.KotlinReflectionUtils;
import org.springframework.data.util.TypeInformation;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public interface PreferredConstructorDiscoverer<T, P extends PersistentProperty<P>> {
    @Nullable
    public static <T, P extends PersistentProperty<P>> PreferredConstructor<T, P> discover(Class<T> type) {
        Assert.notNull(type, (String)"Type must not be null!");
        return Discoverers.findDiscoverer(type).discover(ClassTypeInformation.from(type), null);
    }

    @Nullable
    public static <T, P extends PersistentProperty<P>> PreferredConstructor<T, P> discover(PersistentEntity<T, P> entity) {
        Assert.notNull(entity, (String)"PersistentEntity must not be null!");
        return Discoverers.findDiscoverer(entity.getType()).discover(entity.getTypeInformation(), entity);
    }

    public static enum Discoverers {
        DEFAULT{

            @Override
            @Nullable
            <T, P extends PersistentProperty<P>> PreferredConstructor<T, P> discover(TypeInformation<T> type, @Nullable PersistentEntity<T, P> entity) {
                Class<T> rawOwningType = type.getType();
                ArrayList candidates = new ArrayList();
                Constructor<?> noArg = null;
                for (Constructor<?> candidate : rawOwningType.getDeclaredConstructors()) {
                    if (candidate.isSynthetic()) continue;
                    if (candidate.isAnnotationPresent(PersistenceConstructor.class)) {
                        return Discoverers.buildPreferredConstructor(candidate, type, entity);
                    }
                    if (candidate.getParameterCount() == 0) {
                        noArg = candidate;
                        continue;
                    }
                    candidates.add(candidate);
                }
                if (noArg != null) {
                    return Discoverers.buildPreferredConstructor(noArg, type, entity);
                }
                return candidates.size() > 1 || candidates.isEmpty() ? null : Discoverers.buildPreferredConstructor((Constructor)candidates.iterator().next(), type, entity);
            }
        }
        ,
        KOTLIN{

            @Override
            @Nullable
            <T, P extends PersistentProperty<P>> PreferredConstructor<T, P> discover(TypeInformation<T> type, @Nullable PersistentEntity<T, P> entity) {
                Class<T> rawOwningType = type.getType();
                return Arrays.stream(rawOwningType.getDeclaredConstructors()).filter(it -> !it.isSynthetic()).filter(it -> it.isAnnotationPresent(PersistenceConstructor.class)).map(it -> Discoverers.buildPreferredConstructor(it, type, entity)).findFirst().orElseGet(() -> {
                    KFunction primaryConstructor = KClasses.getPrimaryConstructor((KClass)JvmClassMappingKt.getKotlinClass(type.getType()));
                    if (primaryConstructor == null) {
                        return DEFAULT.discover(type, entity);
                    }
                    Constructor javaConstructor = ReflectJvmMapping.getJavaConstructor((KFunction)primaryConstructor);
                    return javaConstructor != null ? Discoverers.buildPreferredConstructor(javaConstructor, type, entity) : null;
                });
            }
        };

        private static final ParameterNameDiscoverer PARAMETER_NAME_DISCOVERER;

        private static Discoverers findDiscoverer(Class<?> type) {
            return KotlinReflectionUtils.isSupportedKotlinClass(type) ? KOTLIN : DEFAULT;
        }

        @Nullable
        abstract <T, P extends PersistentProperty<P>> PreferredConstructor<T, P> discover(TypeInformation<T> var1, @Nullable PersistentEntity<T, P> var2);

        private static <T, P extends PersistentProperty<P>> PreferredConstructor<T, P> buildPreferredConstructor(Constructor<?> constructor, TypeInformation<T> typeInformation, @Nullable PersistentEntity<T, P> entity) {
            if (constructor.getParameterCount() == 0) {
                return new PreferredConstructor(constructor, new PreferredConstructor.Parameter[0]);
            }
            List<TypeInformation<?>> parameterTypes = typeInformation.getParameterTypes(constructor);
            String[] parameterNames = PARAMETER_NAME_DISCOVERER.getParameterNames(constructor);
            PreferredConstructor.Parameter[] parameters = new PreferredConstructor.Parameter[parameterTypes.size()];
            Annotation[][] parameterAnnotations = constructor.getParameterAnnotations();
            for (int i = 0; i < parameterTypes.size(); ++i) {
                String name = parameterNames == null || parameterNames.length <= i ? null : parameterNames[i];
                TypeInformation<?> type = parameterTypes.get(i);
                Annotation[] annotations = parameterAnnotations[i];
                parameters[i] = new PreferredConstructor.Parameter(name, type, annotations, entity);
            }
            return new PreferredConstructor(constructor, parameters);
        }

        static {
            PARAMETER_NAME_DISCOVERER = new DefaultParameterNameDiscoverer();
        }
    }
}

