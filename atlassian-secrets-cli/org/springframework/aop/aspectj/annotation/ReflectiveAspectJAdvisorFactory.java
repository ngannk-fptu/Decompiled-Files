/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aspectj.lang.annotation.After
 *  org.aspectj.lang.annotation.AfterReturning
 *  org.aspectj.lang.annotation.AfterThrowing
 *  org.aspectj.lang.annotation.Around
 *  org.aspectj.lang.annotation.Before
 *  org.aspectj.lang.annotation.DeclareParents
 *  org.aspectj.lang.annotation.Pointcut
 */
package org.springframework.aop.aspectj.annotation;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.aopalliance.aop.Advice;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.DeclareParents;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AbstractAspectJAdvice;
import org.springframework.aop.aspectj.AspectJAfterAdvice;
import org.springframework.aop.aspectj.AspectJAfterReturningAdvice;
import org.springframework.aop.aspectj.AspectJAfterThrowingAdvice;
import org.springframework.aop.aspectj.AspectJAroundAdvice;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.aspectj.AspectJMethodBeforeAdvice;
import org.springframework.aop.aspectj.DeclareParentsAdvisor;
import org.springframework.aop.aspectj.annotation.AbstractAspectJAdvisorFactory;
import org.springframework.aop.aspectj.annotation.InstantiationModelAwarePointcutAdvisorImpl;
import org.springframework.aop.aspectj.annotation.LazySingletonAspectInstanceFactoryDecorator;
import org.springframework.aop.aspectj.annotation.MetadataAwareAspectInstanceFactory;
import org.springframework.aop.framework.AopConfigException;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.convert.converter.ConvertingComparator;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.comparator.InstanceComparator;

public class ReflectiveAspectJAdvisorFactory
extends AbstractAspectJAdvisorFactory
implements Serializable {
    private static final Comparator<Method> METHOD_COMPARATOR;
    @Nullable
    private final BeanFactory beanFactory;

    public ReflectiveAspectJAdvisorFactory() {
        this(null);
    }

    public ReflectiveAspectJAdvisorFactory(@Nullable BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public List<Advisor> getAdvisors(MetadataAwareAspectInstanceFactory aspectInstanceFactory) {
        Class<?> aspectClass = aspectInstanceFactory.getAspectMetadata().getAspectClass();
        String aspectName = aspectInstanceFactory.getAspectMetadata().getAspectName();
        this.validate(aspectClass);
        LazySingletonAspectInstanceFactoryDecorator lazySingletonAspectInstanceFactory = new LazySingletonAspectInstanceFactoryDecorator(aspectInstanceFactory);
        ArrayList<Advisor> advisors = new ArrayList<Advisor>();
        for (Method method : this.getAdvisorMethods(aspectClass)) {
            Advisor advisor = this.getAdvisor(method, lazySingletonAspectInstanceFactory, advisors.size(), aspectName);
            if (advisor == null) continue;
            advisors.add(advisor);
        }
        if (!advisors.isEmpty() && lazySingletonAspectInstanceFactory.getAspectMetadata().isLazilyInstantiated()) {
            SyntheticInstantiationAdvisor instantiationAdvisor = new SyntheticInstantiationAdvisor(lazySingletonAspectInstanceFactory);
            advisors.add(0, instantiationAdvisor);
        }
        for (Field field : aspectClass.getDeclaredFields()) {
            Advisor advisor = this.getDeclareParentsAdvisor(field);
            if (advisor == null) continue;
            advisors.add(advisor);
        }
        return advisors;
    }

    private List<Method> getAdvisorMethods(Class<?> aspectClass) {
        ArrayList<Method> methods = new ArrayList<Method>();
        ReflectionUtils.doWithMethods(aspectClass, method -> {
            if (AnnotationUtils.getAnnotation(method, Pointcut.class) == null) {
                methods.add(method);
            }
        });
        methods.sort(METHOD_COMPARATOR);
        return methods;
    }

    @Nullable
    private Advisor getDeclareParentsAdvisor(Field introductionField) {
        DeclareParents declareParents = introductionField.getAnnotation(DeclareParents.class);
        if (declareParents == null) {
            return null;
        }
        if (DeclareParents.class == declareParents.defaultImpl()) {
            throw new IllegalStateException("'defaultImpl' attribute must be set on DeclareParents");
        }
        return new DeclareParentsAdvisor(introductionField.getType(), declareParents.value(), declareParents.defaultImpl());
    }

    @Override
    @Nullable
    public Advisor getAdvisor(Method candidateAdviceMethod, MetadataAwareAspectInstanceFactory aspectInstanceFactory, int declarationOrderInAspect, String aspectName) {
        this.validate(aspectInstanceFactory.getAspectMetadata().getAspectClass());
        AspectJExpressionPointcut expressionPointcut = this.getPointcut(candidateAdviceMethod, aspectInstanceFactory.getAspectMetadata().getAspectClass());
        if (expressionPointcut == null) {
            return null;
        }
        return new InstantiationModelAwarePointcutAdvisorImpl(expressionPointcut, candidateAdviceMethod, this, aspectInstanceFactory, declarationOrderInAspect, aspectName);
    }

    @Nullable
    private AspectJExpressionPointcut getPointcut(Method candidateAdviceMethod, Class<?> candidateAspectClass) {
        AbstractAspectJAdvisorFactory.AspectJAnnotation<?> aspectJAnnotation = AbstractAspectJAdvisorFactory.findAspectJAnnotationOnMethod(candidateAdviceMethod);
        if (aspectJAnnotation == null) {
            return null;
        }
        AspectJExpressionPointcut ajexp = new AspectJExpressionPointcut(candidateAspectClass, new String[0], new Class[0]);
        ajexp.setExpression(aspectJAnnotation.getPointcutExpression());
        if (this.beanFactory != null) {
            ajexp.setBeanFactory(this.beanFactory);
        }
        return ajexp;
    }

    @Override
    @Nullable
    public Advice getAdvice(Method candidateAdviceMethod, AspectJExpressionPointcut expressionPointcut, MetadataAwareAspectInstanceFactory aspectInstanceFactory, int declarationOrder, String aspectName) {
        AbstractAspectJAdvice springAdvice;
        Class<?> candidateAspectClass = aspectInstanceFactory.getAspectMetadata().getAspectClass();
        this.validate(candidateAspectClass);
        AbstractAspectJAdvisorFactory.AspectJAnnotation<?> aspectJAnnotation = AbstractAspectJAdvisorFactory.findAspectJAnnotationOnMethod(candidateAdviceMethod);
        if (aspectJAnnotation == null) {
            return null;
        }
        if (!this.isAspect(candidateAspectClass)) {
            throw new AopConfigException("Advice must be declared inside an aspect type: Offending method '" + candidateAdviceMethod + "' in class [" + candidateAspectClass.getName() + "]");
        }
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Found AspectJ method: " + candidateAdviceMethod);
        }
        switch (aspectJAnnotation.getAnnotationType()) {
            case AtPointcut: {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Processing pointcut '" + candidateAdviceMethod.getName() + "'");
                }
                return null;
            }
            case AtAround: {
                springAdvice = new AspectJAroundAdvice(candidateAdviceMethod, expressionPointcut, aspectInstanceFactory);
                break;
            }
            case AtBefore: {
                springAdvice = new AspectJMethodBeforeAdvice(candidateAdviceMethod, expressionPointcut, aspectInstanceFactory);
                break;
            }
            case AtAfter: {
                springAdvice = new AspectJAfterAdvice(candidateAdviceMethod, expressionPointcut, aspectInstanceFactory);
                break;
            }
            case AtAfterReturning: {
                springAdvice = new AspectJAfterReturningAdvice(candidateAdviceMethod, expressionPointcut, aspectInstanceFactory);
                AfterReturning afterReturningAnnotation = (AfterReturning)aspectJAnnotation.getAnnotation();
                if (!StringUtils.hasText(afterReturningAnnotation.returning())) break;
                springAdvice.setReturningName(afterReturningAnnotation.returning());
                break;
            }
            case AtAfterThrowing: {
                springAdvice = new AspectJAfterThrowingAdvice(candidateAdviceMethod, expressionPointcut, aspectInstanceFactory);
                AfterThrowing afterThrowingAnnotation = (AfterThrowing)aspectJAnnotation.getAnnotation();
                if (!StringUtils.hasText(afterThrowingAnnotation.throwing())) break;
                springAdvice.setThrowingName(afterThrowingAnnotation.throwing());
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unsupported advice type on method: " + candidateAdviceMethod);
            }
        }
        springAdvice.setAspectName(aspectName);
        springAdvice.setDeclarationOrder(declarationOrder);
        String[] argNames = this.parameterNameDiscoverer.getParameterNames(candidateAdviceMethod);
        if (argNames != null) {
            springAdvice.setArgumentNamesFromStringArray(argNames);
        }
        springAdvice.calculateArgumentBindings();
        return springAdvice;
    }

    static {
        ConvertingComparator<Method, Annotation> adviceKindComparator = new ConvertingComparator<Method, Annotation>(new InstanceComparator(Around.class, Before.class, After.class, AfterReturning.class, AfterThrowing.class), method -> {
            AbstractAspectJAdvisorFactory.AspectJAnnotation<?> annotation = AbstractAspectJAdvisorFactory.findAspectJAnnotationOnMethod(method);
            return annotation != null ? (Annotation)annotation.getAnnotation() : null;
        });
        ConvertingComparator<Method, String> methodNameComparator = new ConvertingComparator<Method, String>(Method::getName);
        METHOD_COMPARATOR = adviceKindComparator.thenComparing(methodNameComparator);
    }

    protected static class SyntheticInstantiationAdvisor
    extends DefaultPointcutAdvisor {
        public SyntheticInstantiationAdvisor(MetadataAwareAspectInstanceFactory aif) {
            super(aif.getAspectMetadata().getPerClausePointcut(), (method, args, target) -> aif.getAspectInstance());
        }
    }
}

