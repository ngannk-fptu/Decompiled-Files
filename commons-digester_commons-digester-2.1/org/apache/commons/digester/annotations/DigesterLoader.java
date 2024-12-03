/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester.annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.apache.commons.digester.RuleSet;
import org.apache.commons.digester.annotations.AnnotationRuleProvider;
import org.apache.commons.digester.annotations.DigesterLoaderHandler;
import org.apache.commons.digester.annotations.DigesterRule;
import org.apache.commons.digester.annotations.DigesterRuleList;
import org.apache.commons.digester.annotations.FromAnnotationsRuleSet;
import org.apache.commons.digester.annotations.handlers.DefaultLoaderHandler;
import org.apache.commons.digester.annotations.internal.RuleSetCache;
import org.apache.commons.digester.annotations.reflect.MethodArgument;
import org.apache.commons.digester.annotations.spi.AnnotationRuleProviderFactory;
import org.apache.commons.digester.annotations.spi.DigesterLoaderHandlerFactory;
import org.apache.commons.digester.annotations.utils.AnnotationUtils;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class DigesterLoader {
    private final RuleSetCache cachedRuleSet = new RuleSetCache();
    private final AnnotationRuleProviderFactory annotationRuleProviderFactory;
    private final DigesterLoaderHandlerFactory digesterLoaderHandlerFactory;

    protected DigesterLoader(AnnotationRuleProviderFactory annotationRuleProviderFactory, DigesterLoaderHandlerFactory digesterLoaderHandlerFactory) {
        this.annotationRuleProviderFactory = annotationRuleProviderFactory;
        this.digesterLoaderHandlerFactory = digesterLoaderHandlerFactory;
    }

    protected AnnotationRuleProviderFactory getAnnotationRuleProviderFactory() {
        return this.annotationRuleProviderFactory;
    }

    protected DigesterLoaderHandlerFactory getDigesterLoaderHandlerFactory() {
        return this.digesterLoaderHandlerFactory;
    }

    public Digester createDigester(Class<?> target) {
        Digester digester = new Digester();
        digester.setClassLoader(target.getClassLoader());
        this.addRules(target, digester);
        return digester;
    }

    public void addRules(Class<?> target, Digester digester) {
        RuleSet ruleSet = this.getRuleSet(target);
        ruleSet.addRuleInstances(digester);
    }

    public RuleSet getRuleSet(Class<?> target) {
        if (this.cachedRuleSet.containsKey(target)) {
            return this.cachedRuleSet.get(target);
        }
        FromAnnotationsRuleSet ruleSet = new FromAnnotationsRuleSet(this);
        this.addRulesTo(target, ruleSet);
        this.cachedRuleSet.put(target, ruleSet);
        return ruleSet;
    }

    public void addRulesTo(Class<?> target, FromAnnotationsRuleSet ruleSet) {
        if (target == Object.class || target.isInterface() || ruleSet.mapsClass(target)) {
            return;
        }
        if (this.cachedRuleSet.containsKey(target)) {
            ruleSet.addRulesProviderFrom(this.cachedRuleSet.get(target));
            ruleSet.addMappedClass(target);
            return;
        }
        this.handle(target, ruleSet);
        for (Field field : target.getDeclaredFields()) {
            this.handle(field, ruleSet);
        }
        for (AccessibleObject accessibleObject : target.getDeclaredMethods()) {
            this.handle(accessibleObject, ruleSet);
            Annotation[][] parameterAnnotations = ((Method)accessibleObject).getParameterAnnotations();
            Class<?>[] parameterTypes = ((Method)accessibleObject).getParameterTypes();
            for (int i = 0; i < parameterTypes.length; ++i) {
                this.handle(new MethodArgument(i, parameterTypes[i], parameterAnnotations[i]), ruleSet);
            }
        }
        ruleSet.addMappedClass(target);
        this.addRulesTo(target.getSuperclass(), ruleSet);
    }

    private void handle(AnnotatedElement element, FromAnnotationsRuleSet ruleSet) {
        for (Annotation annotation : element.getAnnotations()) {
            this.handle(annotation, element, ruleSet);
        }
    }

    private <A extends Annotation, E extends AnnotatedElement, R extends Rule> void handle(A annotation, E element, FromAnnotationsRuleSet ruleSet) {
        Class<? extends Annotation> annotationType = annotation.annotationType();
        if (annotationType.isAnnotationPresent(DigesterRuleList.class)) {
            Annotation[] annotations = AnnotationUtils.getAnnotationsArrayValue(annotation);
            if (annotations != null && annotations.length > 0) {
                for (Annotation ptr : annotations) {
                    this.handle(ptr, element, ruleSet);
                }
            }
        } else if (annotationType.isAnnotationPresent(DigesterRule.class)) {
            DigesterRule digesterRule = annotationType.getAnnotation(DigesterRule.class);
            if (DefaultLoaderHandler.class == digesterRule.handledBy()) {
                Class<? extends AnnotationRuleProvider<? extends Annotation, ? extends AnnotatedElement, ? extends Rule>> providerType = digesterRule.providedBy();
                ruleSet.addRuleProvider(AnnotationUtils.getAnnotationPattern(annotation), providerType, annotation, element);
            } else {
                Class<? extends DigesterLoaderHandler<? extends Annotation, ? extends AnnotatedElement>> handlerType = digesterRule.handledBy();
                DigesterLoaderHandler<? extends Annotation, ? extends AnnotatedElement> handler = this.digesterLoaderHandlerFactory.newInstance(handlerType);
                handler.handle(annotation, element, ruleSet);
            }
        }
    }
}

