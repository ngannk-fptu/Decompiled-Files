/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.MethodParameter
 *  org.springframework.core.ResolvableType
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.data.repository.core.support;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.MethodLookup;
import org.springframework.data.repository.core.support.RepositoryMethodInvoker;
import org.springframework.data.repository.util.ReactiveWrapperConverters;
import org.springframework.data.repository.util.ReactiveWrappers;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

interface MethodLookups {
    public static MethodLookup direct() {
        MethodLookup.MethodPredicate direct = (invoked, candidate) -> candidate.getName().equals(invoked.getName()) && candidate.getParameterCount() == invoked.getParameterCount() && Arrays.equals(candidate.getParameterTypes(), invoked.getParameterTypes());
        return () -> Collections.singletonList(direct);
    }

    public static MethodLookup forRepositoryTypes(RepositoryMetadata repositoryMetadata) {
        return MethodLookups.direct().and(new RepositoryAwareMethodLookup(repositoryMetadata));
    }

    public static MethodLookup forReactiveTypes(RepositoryMetadata repositoryMetadata) {
        return MethodLookups.direct().and(new ReactiveTypeInteropMethodLookup(repositoryMetadata));
    }

    public static class ReactiveTypeInteropMethodLookup
    extends RepositoryAwareMethodLookup {
        private final RepositoryMetadata repositoryMetadata;

        public ReactiveTypeInteropMethodLookup(RepositoryMetadata repositoryMetadata) {
            super(repositoryMetadata);
            this.repositoryMetadata = repositoryMetadata;
        }

        @Override
        public List<MethodLookup.MethodPredicate> getLookups() {
            MethodLookup.MethodPredicate convertibleComparison = (invokedMethod, candidate) -> {
                ArrayList<Supplier<Optional>> suppliers = new ArrayList<Supplier<Optional>>();
                if (ReactiveTypeInteropMethodLookup.usesParametersWithReactiveWrappers(invokedMethod.getMethod())) {
                    suppliers.add(() -> ReactiveTypeInteropMethodLookup.getMethodCandidate(invokedMethod, candidate, ReactiveTypeInteropMethodLookup.assignableWrapperMatch()));
                    suppliers.add(() -> ReactiveTypeInteropMethodLookup.getMethodCandidate(invokedMethod, candidate, ReactiveTypeInteropMethodLookup.wrapperConversionMatch()));
                }
                return suppliers.stream().anyMatch(supplier -> ((Optional)supplier.get()).isPresent());
            };
            MethodLookup.MethodPredicate detailedComparison = (invokedMethod, candidate) -> ReactiveTypeInteropMethodLookup.getMethodCandidate(invokedMethod, candidate, this.matchParameterOrComponentType(this.repositoryMetadata.getRepositoryInterface())).isPresent();
            return Arrays.asList(convertibleComparison, detailedComparison);
        }

        private Predicate<ParameterOverrideCriteria> matchParameterOrComponentType(Class<?> repositoryInterface) {
            return parameterCriteria -> {
                Class parameterType = parameterCriteria.getDeclared().withContainingClass(repositoryInterface).getParameterType();
                Type genericType = parameterCriteria.getGenericBaseType();
                if (genericType instanceof TypeVariable && !this.matchesGenericType((TypeVariable)genericType, ResolvableType.forMethodParameter((MethodParameter)parameterCriteria.getDeclared()))) {
                    return false;
                }
                return parameterCriteria.getBaseType().isAssignableFrom(parameterType) && parameterCriteria.isAssignableFromDeclared();
            };
        }

        private static boolean isNonUnwrappingWrapper(Class<?> parameterType) {
            Assert.notNull(parameterType, (String)"Parameter type must not be null!");
            return ReactiveWrappers.supports(parameterType);
        }

        private static boolean usesParametersWithReactiveWrappers(Method method) {
            Assert.notNull((Object)method, (String)"Method must not be null!");
            return Arrays.stream(method.getParameterTypes()).anyMatch(ReactiveTypeInteropMethodLookup::isNonUnwrappingWrapper);
        }

        private static Optional<Method> getMethodCandidate(MethodLookup.InvokedMethod invokedMethod, Method candidate, Predicate<ParameterOverrideCriteria> predicate) {
            return Optional.of(candidate).filter(it -> invokedMethod.getName().equals(it.getName())).filter(it -> ReactiveTypeInteropMethodLookup.parameterCountMatch(invokedMethod, it)).filter(it -> ReactiveTypeInteropMethodLookup.parametersMatch(invokedMethod.getMethod(), it, predicate));
        }

        private static boolean parametersMatch(Method declaredMethod, Method baseClassMethod, Predicate<ParameterOverrideCriteria> predicate) {
            return ReactiveTypeInteropMethodLookup.methodParameters(declaredMethod, baseClassMethod).allMatch(predicate);
        }

        private static Predicate<ParameterOverrideCriteria> wrapperConversionMatch() {
            return parameterCriteria -> ReactiveTypeInteropMethodLookup.isNonUnwrappingWrapper(parameterCriteria.getBaseType()) && ReactiveTypeInteropMethodLookup.isNonUnwrappingWrapper(parameterCriteria.getDeclaredType()) && ReactiveWrapperConverters.canConvert(parameterCriteria.getDeclaredType(), parameterCriteria.getBaseType());
        }

        private static Predicate<ParameterOverrideCriteria> assignableWrapperMatch() {
            return parameterCriteria -> ReactiveTypeInteropMethodLookup.isNonUnwrappingWrapper(parameterCriteria.getBaseType()) && ReactiveTypeInteropMethodLookup.isNonUnwrappingWrapper(parameterCriteria.getDeclaredType()) && parameterCriteria.getBaseType().isAssignableFrom(parameterCriteria.getDeclaredType());
        }

        private static boolean parameterCountMatch(MethodLookup.InvokedMethod invokedMethod, Method baseClassMethod) {
            return RepositoryMethodInvoker.canInvoke(invokedMethod.getMethod(), baseClassMethod);
        }

        private static Stream<ParameterOverrideCriteria> methodParameters(Method invokedMethod, Method baseClassMethod) {
            return IntStream.range(0, baseClassMethod.getParameterCount()).mapToObj(index -> ParameterOverrideCriteria.of(new MethodParameter(invokedMethod, index), new MethodParameter(baseClassMethod, index)));
        }

        static final class ParameterOverrideCriteria {
            private final MethodParameter declared;
            private final MethodParameter base;

            private ParameterOverrideCriteria(MethodParameter declared, MethodParameter base) {
                this.declared = declared;
                this.base = base;
            }

            public static ParameterOverrideCriteria of(MethodParameter declared, MethodParameter base) {
                return new ParameterOverrideCriteria(declared, base);
            }

            public Class<?> getBaseType() {
                return this.base.getParameterType();
            }

            public Type getGenericBaseType() {
                return this.base.getGenericParameterType();
            }

            public Class<?> getDeclaredType() {
                return this.declared.getParameterType();
            }

            public boolean isAssignableFromDeclared() {
                return this.getBaseType().isAssignableFrom(this.getDeclaredType());
            }

            public MethodParameter getDeclared() {
                return this.declared;
            }

            public MethodParameter getBase() {
                return this.base;
            }

            public boolean equals(Object o) {
                if (this == o) {
                    return true;
                }
                if (!(o instanceof ParameterOverrideCriteria)) {
                    return false;
                }
                ParameterOverrideCriteria that = (ParameterOverrideCriteria)o;
                if (!ObjectUtils.nullSafeEquals((Object)this.declared, (Object)that.declared)) {
                    return false;
                }
                return ObjectUtils.nullSafeEquals((Object)this.base, (Object)that.base);
            }

            public int hashCode() {
                int result = ObjectUtils.nullSafeHashCode((Object)this.declared);
                result = 31 * result + ObjectUtils.nullSafeHashCode((Object)this.base);
                return result;
            }

            public String toString() {
                return "MethodLookups.ReactiveTypeInteropMethodLookup.ParameterOverrideCriteria(declared=" + this.getDeclared() + ", base=" + this.getBase() + ")";
            }
        }
    }

    public static class RepositoryAwareMethodLookup
    implements MethodLookup {
        private static final TypeVariable<Class<Repository>>[] PARAMETERS = Repository.class.getTypeParameters();
        private static final String DOMAIN_TYPE_NAME = PARAMETERS[0].getName();
        private static final String ID_TYPE_NAME = PARAMETERS[1].getName();
        private final ResolvableType entityType;
        private final ResolvableType idType;
        private final Class<?> repositoryInterface;

        public RepositoryAwareMethodLookup(RepositoryMetadata repositoryMetadata) {
            Assert.notNull((Object)repositoryMetadata, (String)"Repository metadata must not be null!");
            this.entityType = ResolvableType.forClass(repositoryMetadata.getDomainType());
            this.idType = ResolvableType.forClass(repositoryMetadata.getIdType());
            this.repositoryInterface = repositoryMetadata.getRepositoryInterface();
        }

        @Override
        public List<MethodLookup.MethodPredicate> getLookups() {
            MethodLookup.MethodPredicate detailedComparison = (invoked, candidate) -> Optional.of(candidate).filter(baseClassMethod -> baseClassMethod.getName().equals(invoked.getName())).filter(baseClassMethod -> baseClassMethod.getParameterCount() == invoked.getParameterCount()).filter(baseClassMethod -> this.parametersMatch(invoked.getMethod(), (Method)baseClassMethod)).isPresent();
            return Collections.singletonList(detailedComparison);
        }

        protected boolean matchesGenericType(TypeVariable<?> variable, ResolvableType parameterType) {
            Object declaration = variable.getGenericDeclaration();
            if (declaration instanceof Class) {
                if (ID_TYPE_NAME.equals(variable.getName()) && parameterType.isAssignableFrom(this.idType)) {
                    return true;
                }
                Type boundType = variable.getBounds()[0];
                String referenceName = boundType instanceof TypeVariable ? boundType.toString() : variable.toString();
                return DOMAIN_TYPE_NAME.equals(referenceName) && parameterType.isAssignableFrom(this.entityType);
            }
            for (Type type : variable.getBounds()) {
                if (!ResolvableType.forType((Type)type).isAssignableFrom(parameterType)) continue;
                return true;
            }
            return false;
        }

        private boolean parametersMatch(Method invokedMethod, Method candidate) {
            Class<?>[] methodParameterTypes = invokedMethod.getParameterTypes();
            Type[] genericTypes = candidate.getGenericParameterTypes();
            Class<?>[] types = candidate.getParameterTypes();
            for (int i = 0; i < genericTypes.length; ++i) {
                Type genericType = genericTypes[i];
                Class<?> type = types[i];
                MethodParameter parameter = new MethodParameter(invokedMethod, i).withContainingClass(this.repositoryInterface);
                Class parameterType = parameter.getParameterType();
                if (!(genericType instanceof TypeVariable ? !this.matchesGenericType((TypeVariable)genericType, ResolvableType.forMethodParameter((MethodParameter)parameter)) : !types[i].equals(parameterType) && (!type.isAssignableFrom(parameterType) || !type.equals(methodParameterTypes[i])))) continue;
                return false;
            }
            return true;
        }
    }
}

