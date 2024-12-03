/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aspectj.lang.ProceedingJoinPoint
 *  org.aspectj.lang.reflect.MethodSignature
 */
package io.micrometer.common.annotation;

import io.micrometer.common.KeyValue;
import io.micrometer.common.annotation.AnnotatedParameter;
import io.micrometer.common.annotation.AnnotationUtils;
import io.micrometer.common.annotation.ValueExpressionResolver;
import io.micrometer.common.annotation.ValueResolver;
import io.micrometer.common.util.internal.logging.InternalLogger;
import io.micrometer.common.util.internal.logging.InternalLoggerFactory;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

public class AnnotationHandler<T> {
    private static final InternalLogger log = InternalLoggerFactory.getInstance(AnnotationHandler.class);
    private final BiConsumer<KeyValue, T> keyValueConsumer;
    private final Function<Class<? extends ValueResolver>, ? extends ValueResolver> resolverProvider;
    private final Function<Class<? extends ValueExpressionResolver>, ? extends ValueExpressionResolver> expressionResolverProvider;
    private final Class<? extends Annotation> annotationClass;
    private final BiFunction<Annotation, Object, KeyValue> toKeyValue;

    public AnnotationHandler(BiConsumer<KeyValue, T> keyValueConsumer, Function<Class<? extends ValueResolver>, ? extends ValueResolver> resolverProvider, Function<Class<? extends ValueExpressionResolver>, ? extends ValueExpressionResolver> expressionResolverProvider, Class<? extends Annotation> annotation, BiFunction<Annotation, Object, KeyValue> toKeyValue) {
        this.keyValueConsumer = keyValueConsumer;
        this.resolverProvider = resolverProvider;
        this.expressionResolverProvider = expressionResolverProvider;
        this.annotationClass = annotation;
        this.toKeyValue = toKeyValue;
    }

    public void addAnnotatedParameters(T objectToModify, ProceedingJoinPoint pjp) {
        try {
            Method method = ((MethodSignature)pjp.getSignature()).getMethod();
            method = pjp.getTarget().getClass().getMethod(method.getName(), method.getParameterTypes());
            List<AnnotatedParameter> annotatedParameters = AnnotationUtils.findAnnotatedParameters(this.annotationClass, method, pjp.getArgs());
            this.getAnnotationsFromInterfaces(pjp, method, annotatedParameters);
            this.addAnnotatedArguments(objectToModify, annotatedParameters);
        }
        catch (Exception ex) {
            log.error("Exception occurred while trying to add annotated parameters", ex);
        }
    }

    private void getAnnotationsFromInterfaces(ProceedingJoinPoint pjp, Method mostSpecificMethod, List<AnnotatedParameter> annotatedParameters) {
        Class<?>[] implementedInterfaces;
        for (Class<?> implementedInterface : implementedInterfaces = pjp.getThis().getClass().getInterfaces()) {
            for (Method methodFromInterface : implementedInterface.getMethods()) {
                if (!this.methodsAreTheSame(mostSpecificMethod, methodFromInterface)) continue;
                List<AnnotatedParameter> annotatedParametersForActualMethod = AnnotationUtils.findAnnotatedParameters(this.annotationClass, methodFromInterface, pjp.getArgs());
                this.mergeAnnotatedParameters(annotatedParameters, annotatedParametersForActualMethod);
            }
        }
    }

    private boolean methodsAreTheSame(Method mostSpecificMethod, Method method) {
        return method.getName().equals(mostSpecificMethod.getName()) && Arrays.equals(method.getParameterTypes(), mostSpecificMethod.getParameterTypes());
    }

    private void mergeAnnotatedParameters(List<AnnotatedParameter> annotatedParameters, List<AnnotatedParameter> annotatedParametersForActualMethod) {
        for (AnnotatedParameter container : annotatedParametersForActualMethod) {
            int index = container.parameterIndex;
            boolean parameterContained = false;
            for (AnnotatedParameter parameterContainer : annotatedParameters) {
                if (parameterContainer.parameterIndex != index) continue;
                parameterContained = true;
                break;
            }
            if (parameterContained) continue;
            annotatedParameters.add(container);
        }
    }

    private void addAnnotatedArguments(T objectToModify, List<AnnotatedParameter> toBeAdded) {
        for (AnnotatedParameter container : toBeAdded) {
            KeyValue keyValue = this.toKeyValue.apply(container.annotation, container.argument);
            this.keyValueConsumer.accept(keyValue, (KeyValue)objectToModify);
        }
    }

    public Function<Class<? extends ValueResolver>, ? extends ValueResolver> getResolverProvider() {
        return this.resolverProvider;
    }

    public Function<Class<? extends ValueExpressionResolver>, ? extends ValueExpressionResolver> getExpressionResolverProvider() {
        return this.expressionResolverProvider;
    }
}

