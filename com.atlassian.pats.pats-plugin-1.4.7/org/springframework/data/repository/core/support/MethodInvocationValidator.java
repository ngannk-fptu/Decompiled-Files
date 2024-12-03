/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.intercept.MethodInterceptor
 *  org.aopalliance.intercept.MethodInvocation
 *  org.springframework.core.DefaultParameterNameDiscoverer
 *  org.springframework.core.MethodParameter
 *  org.springframework.core.ParameterNameDiscoverer
 *  org.springframework.dao.EmptyResultDataAccessException
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.ConcurrentReferenceHashMap
 *  org.springframework.util.ConcurrentReferenceHashMap$ReferenceType
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.data.repository.core.support;

import java.lang.annotation.ElementType;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.util.KotlinReflectionUtils;
import org.springframework.data.util.NullableUtils;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ObjectUtils;

public class MethodInvocationValidator
implements MethodInterceptor {
    private final ParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();
    private final Map<Method, Nullability> nullabilityCache = new ConcurrentReferenceHashMap(16, ConcurrentReferenceHashMap.ReferenceType.WEAK);

    public static boolean supports(Class<?> repositoryInterface) {
        return KotlinReflectionUtils.isSupportedKotlinClass(repositoryInterface) || NullableUtils.isNonNull(repositoryInterface, ElementType.METHOD) || NullableUtils.isNonNull(repositoryInterface, ElementType.PARAMETER);
    }

    @Nullable
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        Nullability nullability = this.nullabilityCache.get(method);
        if (nullability == null) {
            nullability = Nullability.of(method, this.discoverer);
            this.nullabilityCache.put(method, nullability);
        }
        Object[] arguments = invocation.getArguments();
        for (int i = 0; i < method.getParameterCount(); ++i) {
            if (nullability.isNullableParameter(i) || arguments.length >= i && arguments[i] != null) continue;
            throw new IllegalArgumentException(String.format("Parameter %s in %s.%s must not be null!", nullability.getMethodParameterName(i), ClassUtils.getShortName(method.getDeclaringClass()), method.getName()));
        }
        Object result = invocation.proceed();
        if (result == null && !nullability.isNullableReturn()) {
            throw new EmptyResultDataAccessException("Result must not be null!", 1);
        }
        return result;
    }

    static final class Nullability {
        private final boolean nullableReturn;
        private final boolean[] nullableParameters;
        private final MethodParameter[] methodParameters;

        private Nullability(boolean nullableReturn, boolean[] nullableParameters, MethodParameter[] methodParameters) {
            this.nullableReturn = nullableReturn;
            this.nullableParameters = nullableParameters;
            this.methodParameters = methodParameters;
        }

        static Nullability of(Method method, ParameterNameDiscoverer discoverer) {
            boolean nullableReturn = Nullability.isNullableParameter(new MethodParameter(method, -1));
            boolean[] nullableParameters = new boolean[method.getParameterCount()];
            MethodParameter[] methodParameters = new MethodParameter[method.getParameterCount()];
            for (int i = 0; i < method.getParameterCount(); ++i) {
                MethodParameter parameter = new MethodParameter(method, i);
                parameter.initParameterNameDiscovery(discoverer);
                nullableParameters[i] = Nullability.isNullableParameter(parameter);
                methodParameters[i] = parameter;
            }
            return new Nullability(nullableReturn, nullableParameters, methodParameters);
        }

        String getMethodParameterName(int index) {
            String parameterName = this.methodParameters[index].getParameterName();
            if (parameterName == null) {
                parameterName = String.format("of type %s at index %d", ClassUtils.getShortName((Class)this.methodParameters[index].getParameterType()), index);
            }
            return parameterName;
        }

        boolean isNullableReturn() {
            return this.nullableReturn;
        }

        boolean isNullableParameter(int index) {
            return this.nullableParameters[index];
        }

        private static boolean isNullableParameter(MethodParameter parameter) {
            return Nullability.requiresNoValue(parameter) || NullableUtils.isExplicitNullable(parameter) || KotlinReflectionUtils.isSupportedKotlinClass(parameter.getDeclaringClass()) && ReflectionUtils.isNullable(parameter);
        }

        private static boolean requiresNoValue(MethodParameter parameter) {
            return ReflectionUtils.isVoid(parameter.getParameterType());
        }

        public boolean[] getNullableParameters() {
            return this.nullableParameters;
        }

        public MethodParameter[] getMethodParameters() {
            return this.methodParameters;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Nullability)) {
                return false;
            }
            Nullability that = (Nullability)o;
            if (this.nullableReturn != that.nullableReturn) {
                return false;
            }
            if (!ObjectUtils.nullSafeEquals((Object)this.nullableParameters, (Object)that.nullableParameters)) {
                return false;
            }
            return ObjectUtils.nullSafeEquals((Object)this.methodParameters, (Object)that.methodParameters);
        }

        public int hashCode() {
            int result = this.nullableReturn ? 1 : 0;
            result = 31 * result + ObjectUtils.nullSafeHashCode((boolean[])this.nullableParameters);
            result = 31 * result + ObjectUtils.nullSafeHashCode((Object[])this.methodParameters);
            return result;
        }

        public String toString() {
            return "MethodInvocationValidator.Nullability(nullableReturn=" + this.isNullableReturn() + ", nullableParameters=" + Arrays.toString(this.getNullableParameters()) + ", methodParameters=" + Arrays.deepToString(this.getMethodParameters()) + ")";
        }
    }
}

