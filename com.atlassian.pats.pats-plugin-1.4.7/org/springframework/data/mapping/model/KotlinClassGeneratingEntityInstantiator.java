/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.reflect.KFunction
 *  kotlin.reflect.KParameter
 *  kotlin.reflect.jvm.ReflectJvmMapping
 *  org.springframework.lang.Nullable
 */
package org.springframework.data.mapping.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import kotlin.reflect.KFunction;
import kotlin.reflect.KParameter;
import kotlin.reflect.jvm.ReflectJvmMapping;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.PreferredConstructor;
import org.springframework.data.mapping.model.ClassGeneratingEntityInstantiator;
import org.springframework.data.mapping.model.EntityInstantiator;
import org.springframework.data.mapping.model.KotlinDefaultMask;
import org.springframework.data.mapping.model.MappingInstantiationException;
import org.springframework.data.mapping.model.ParameterValueProvider;
import org.springframework.data.util.KotlinReflectionUtils;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.lang.Nullable;

class KotlinClassGeneratingEntityInstantiator
extends ClassGeneratingEntityInstantiator {
    KotlinClassGeneratingEntityInstantiator() {
    }

    @Override
    protected EntityInstantiator doCreateEntityInstantiator(PersistentEntity<?, ?> entity) {
        PreferredConstructor<?, ?> defaultConstructor;
        PreferredConstructor<?, ?> constructor = entity.getPersistenceConstructor();
        if (KotlinReflectionUtils.isSupportedKotlinClass(entity.getType()) && constructor != null && (defaultConstructor = new DefaultingKotlinConstructorResolver(entity).getDefaultConstructor()) != null) {
            ClassGeneratingEntityInstantiator.ObjectInstantiator instantiator = this.createObjectInstantiator(entity, defaultConstructor);
            return new DefaultingKotlinClassInstantiatorAdapter(instantiator, constructor);
        }
        return super.doCreateEntityInstantiator(entity);
    }

    static class DefaultingKotlinClassInstantiatorAdapter
    implements EntityInstantiator {
        private final ClassGeneratingEntityInstantiator.ObjectInstantiator instantiator;
        private final KFunction<?> constructor;
        private final List<KParameter> kParameters;
        private final Constructor<?> synthetic;

        DefaultingKotlinClassInstantiatorAdapter(ClassGeneratingEntityInstantiator.ObjectInstantiator instantiator, PreferredConstructor<?, ?> constructor) {
            KFunction kotlinConstructor = ReflectJvmMapping.getKotlinFunction(constructor.getConstructor());
            if (kotlinConstructor == null) {
                throw new IllegalArgumentException("No corresponding Kotlin constructor found for " + constructor.getConstructor());
            }
            this.instantiator = instantiator;
            this.constructor = kotlinConstructor;
            this.kParameters = kotlinConstructor.getParameters();
            this.synthetic = constructor.getConstructor();
        }

        @Override
        public <T, E extends PersistentEntity<? extends T, P>, P extends PersistentProperty<P>> T createInstance(E entity, ParameterValueProvider<P> provider) {
            Object[] params = this.extractInvocationArguments(entity.getPersistenceConstructor(), provider);
            try {
                return (T)this.instantiator.newInstance(params);
            }
            catch (Exception e) {
                throw new MappingInstantiationException(entity, Arrays.asList(params), e);
            }
        }

        private <P extends PersistentProperty<P>, T> Object[] extractInvocationArguments(@Nullable PreferredConstructor<? extends T, P> preferredConstructor, ParameterValueProvider<P> provider) {
            if (preferredConstructor == null) {
                throw new IllegalArgumentException("PreferredConstructor must not be null!");
            }
            Object[] params = ClassGeneratingEntityInstantiator.allocateArguments(this.synthetic.getParameterCount() + KotlinDefaultMask.getMaskCount(this.synthetic.getParameterCount()) + 1);
            int userParameterCount = this.kParameters.size();
            List parameters = preferredConstructor.getParameters();
            for (int i = 0; i < userParameterCount; ++i) {
                PreferredConstructor.Parameter<Object, P> parameter = parameters.get(i);
                params[i] = provider.getParameterValue(parameter);
            }
            KotlinDefaultMask defaultMask = KotlinDefaultMask.from(this.constructor, it -> {
                int index = this.kParameters.indexOf(it);
                PreferredConstructor.Parameter parameter = (PreferredConstructor.Parameter)parameters.get(index);
                Class type = parameter.getType().getType();
                if (it.isOptional() && params[index] == null) {
                    if (type.isPrimitive()) {
                        params[index] = ReflectionUtils.getPrimitiveDefault(type);
                    }
                    return false;
                }
                return true;
            });
            int[] defaulting = defaultMask.getDefaulting();
            for (int i = 0; i < defaulting.length; ++i) {
                params[userParameterCount + i] = defaulting[i];
            }
            return params;
        }
    }

    static class DefaultingKotlinConstructorResolver {
        @Nullable
        private final PreferredConstructor<?, ?> defaultConstructor;

        DefaultingKotlinConstructorResolver(PersistentEntity<?, ?> entity) {
            Constructor<?> hit = DefaultingKotlinConstructorResolver.resolveDefaultConstructor(entity);
            PreferredConstructor<?, ?> persistenceConstructor = entity.getPersistenceConstructor();
            this.defaultConstructor = hit != null && persistenceConstructor != null ? new PreferredConstructor(hit, persistenceConstructor.getParameters().toArray(new PreferredConstructor.Parameter[0])) : null;
        }

        @Nullable
        private static Constructor<?> resolveDefaultConstructor(PersistentEntity<?, ?> entity) {
            PreferredConstructor<?, ?> persistenceConstructor = entity.getPersistenceConstructor();
            if (persistenceConstructor == null) {
                return null;
            }
            Constructor<?> hit = null;
            Constructor<?> constructor = persistenceConstructor.getConstructor();
            for (Constructor<?> candidate : entity.getType().getDeclaredConstructors()) {
                if (!candidate.isSynthetic()) continue;
                int syntheticParameters = KotlinDefaultMask.getMaskCount(constructor.getParameterCount()) + 1;
                if (constructor.getParameterCount() + syntheticParameters != candidate.getParameterCount()) continue;
                Parameter[] constructorParameters = constructor.getParameters();
                Parameter[] candidateParameters = candidate.getParameters();
                if (!candidateParameters[candidateParameters.length - 1].getType().getName().equals("kotlin.jvm.internal.DefaultConstructorMarker") || !DefaultingKotlinConstructorResolver.parametersMatch(constructorParameters, candidateParameters)) continue;
                hit = candidate;
                break;
            }
            return hit;
        }

        private static boolean parametersMatch(Parameter[] constructorParameters, Parameter[] candidateParameters) {
            return IntStream.range(0, constructorParameters.length).allMatch(i -> constructorParameters[i].getType().equals(candidateParameters[i].getType()));
        }

        @Nullable
        PreferredConstructor<?, ?> getDefaultConstructor() {
            return this.defaultConstructor;
        }
    }
}

