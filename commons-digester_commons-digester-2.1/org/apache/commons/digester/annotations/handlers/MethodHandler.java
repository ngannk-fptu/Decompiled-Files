/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester.annotations.handlers;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.apache.commons.digester.Rule;
import org.apache.commons.digester.annotations.AnnotationRuleProvider;
import org.apache.commons.digester.annotations.CreationRule;
import org.apache.commons.digester.annotations.DigesterLoaderHandler;
import org.apache.commons.digester.annotations.DigesterLoadingException;
import org.apache.commons.digester.annotations.DigesterRule;
import org.apache.commons.digester.annotations.DigesterRuleList;
import org.apache.commons.digester.annotations.FromAnnotationsRuleSet;
import org.apache.commons.digester.annotations.utils.AnnotationUtils;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class MethodHandler
implements DigesterLoaderHandler<Annotation, Method> {
    private static final int SUPPORTED_ARGS = 1;

    @Override
    public void handle(Annotation annotation, Method element, FromAnnotationsRuleSet ruleSet) {
        if (1 != element.getParameterTypes().length) {
            DigesterRule rule = annotation.annotationType().getAnnotation(DigesterRule.class);
            throw new DigesterLoadingException("Methods annotated with digester annotation rule @" + rule.reflectsRule().getName() + " must have just one argument");
        }
        Object explicitTypesObject = AnnotationUtils.getAnnotationValue(annotation);
        if (explicitTypesObject == null || !explicitTypesObject.getClass().isArray() || Class.class != explicitTypesObject.getClass().getComponentType()) {
            throw new DigesterLoadingException("Impossible to apply this handler, @" + annotation.getClass().getName() + ".value() has to be of type 'Class<?>[]'");
        }
        Class[] explicitTypes = (Class[])explicitTypesObject;
        Class<?> paramType = element.getParameterTypes()[0];
        if (explicitTypes.length > 0) {
            for (Class explicitType : explicitTypes) {
                if (!paramType.isAssignableFrom(explicitType)) {
                    throw new DigesterLoadingException("Impossible to handle annotation " + annotation + " on method " + element.toGenericString() + ", " + explicitType.getName() + " has to be a " + paramType.getName());
                }
                this.doHandle(annotation, element, explicitType, ruleSet);
            }
        } else {
            this.doHandle(annotation, element, paramType, ruleSet);
        }
    }

    private void doHandle(Annotation methodAnnotation, Method method, Class<?> type, FromAnnotationsRuleSet ruleSet) {
        if (type.isInterface() && Modifier.isAbstract(type.getModifiers())) {
            throw new DigesterLoadingException("Impossible to proceed analyzing " + methodAnnotation + ", specified type '" + type.getName() + "' is an interface/abstract");
        }
        for (Annotation annotation : type.getAnnotations()) {
            this.doHandle(methodAnnotation, annotation, method, type, ruleSet);
        }
    }

    private <A extends Annotation, R extends Rule> void doHandle(A methodAnnotation, Annotation annotation, Method method, Class<?> type, FromAnnotationsRuleSet ruleSet) {
        Annotation[] annotations;
        if (annotation.annotationType().isAnnotationPresent(DigesterRule.class) && annotation.annotationType().isAnnotationPresent(CreationRule.class)) {
            ruleSet.addRules(type);
            DigesterRule digesterRule = methodAnnotation.annotationType().getAnnotation(DigesterRule.class);
            Class<? extends AnnotationRuleProvider<? extends Annotation, ? extends AnnotatedElement, ? extends Rule>> providerType = digesterRule.providedBy();
            ruleSet.addRuleProvider(AnnotationUtils.getAnnotationPattern(annotation), providerType, methodAnnotation, method);
        } else if (annotation.annotationType().isAnnotationPresent(DigesterRuleList.class) && (annotations = AnnotationUtils.getAnnotationsArrayValue(annotation)) != null) {
            for (Annotation ptr : annotations) {
                this.doHandle(methodAnnotation, ptr, method, type, ruleSet);
            }
        }
    }
}

