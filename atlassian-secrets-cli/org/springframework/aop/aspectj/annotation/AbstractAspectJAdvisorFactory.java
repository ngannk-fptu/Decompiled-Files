/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aspectj.lang.annotation.After
 *  org.aspectj.lang.annotation.AfterReturning
 *  org.aspectj.lang.annotation.AfterThrowing
 *  org.aspectj.lang.annotation.Around
 *  org.aspectj.lang.annotation.Aspect
 *  org.aspectj.lang.annotation.Before
 *  org.aspectj.lang.annotation.Pointcut
 *  org.aspectj.lang.reflect.AjType
 *  org.aspectj.lang.reflect.AjTypeSystem
 *  org.aspectj.lang.reflect.PerClauseKind
 */
package org.springframework.aop.aspectj.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.AjTypeSystem;
import org.aspectj.lang.reflect.PerClauseKind;
import org.springframework.aop.aspectj.annotation.AspectJAdvisorFactory;
import org.springframework.aop.aspectj.annotation.NotAnAtAspectException;
import org.springframework.aop.framework.AopConfigException;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;

public abstract class AbstractAspectJAdvisorFactory
implements AspectJAdvisorFactory {
    private static final String AJC_MAGIC = "ajc$";
    private static final Class<?>[] ASPECTJ_ANNOTATION_CLASSES = new Class[]{Pointcut.class, Around.class, Before.class, After.class, AfterReturning.class, AfterThrowing.class};
    protected final Log logger = LogFactory.getLog(this.getClass());
    protected final ParameterNameDiscoverer parameterNameDiscoverer = new AspectJAnnotationParameterNameDiscoverer();

    @Override
    public boolean isAspect(Class<?> clazz) {
        return this.hasAspectAnnotation(clazz) && !this.compiledByAjc(clazz);
    }

    private boolean hasAspectAnnotation(Class<?> clazz) {
        return AnnotationUtils.findAnnotation(clazz, Aspect.class) != null;
    }

    private boolean compiledByAjc(Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            if (!field.getName().startsWith(AJC_MAGIC)) continue;
            return true;
        }
        return false;
    }

    @Override
    public void validate(Class<?> aspectClass) throws AopConfigException {
        if (aspectClass.getSuperclass().getAnnotation(Aspect.class) != null && !Modifier.isAbstract(aspectClass.getSuperclass().getModifiers())) {
            throw new AopConfigException("[" + aspectClass.getName() + "] cannot extend concrete aspect [" + aspectClass.getSuperclass().getName() + "]");
        }
        AjType ajType = AjTypeSystem.getAjType(aspectClass);
        if (!ajType.isAspect()) {
            throw new NotAnAtAspectException(aspectClass);
        }
        if (ajType.getPerClause().getKind() == PerClauseKind.PERCFLOW) {
            throw new AopConfigException(aspectClass.getName() + " uses percflow instantiation model: This is not supported in Spring AOP.");
        }
        if (ajType.getPerClause().getKind() == PerClauseKind.PERCFLOWBELOW) {
            throw new AopConfigException(aspectClass.getName() + " uses percflowbelow instantiation model: This is not supported in Spring AOP.");
        }
    }

    @Nullable
    protected static AspectJAnnotation<?> findAspectJAnnotationOnMethod(Method method) {
        for (Class<?> clazz : ASPECTJ_ANNOTATION_CLASSES) {
            AspectJAnnotation<?> foundAnnotation = AbstractAspectJAdvisorFactory.findAnnotation(method, clazz);
            if (foundAnnotation == null) continue;
            return foundAnnotation;
        }
        return null;
    }

    @Nullable
    private static <A extends Annotation> AspectJAnnotation<A> findAnnotation(Method method, Class<A> toLookFor) {
        A result = AnnotationUtils.findAnnotation(method, toLookFor);
        if (result != null) {
            return new AspectJAnnotation<A>(result);
        }
        return null;
    }

    private static class AspectJAnnotationParameterNameDiscoverer
    implements ParameterNameDiscoverer {
        private AspectJAnnotationParameterNameDiscoverer() {
        }

        @Override
        @Nullable
        public String[] getParameterNames(Method method) {
            if (method.getParameterCount() == 0) {
                return new String[0];
            }
            AspectJAnnotation<?> annotation = AbstractAspectJAdvisorFactory.findAspectJAnnotationOnMethod(method);
            if (annotation == null) {
                return null;
            }
            StringTokenizer nameTokens = new StringTokenizer(annotation.getArgumentNames(), ",");
            if (nameTokens.countTokens() > 0) {
                String[] names = new String[nameTokens.countTokens()];
                for (int i = 0; i < names.length; ++i) {
                    names[i] = nameTokens.nextToken();
                }
                return names;
            }
            return null;
        }

        @Override
        @Nullable
        public String[] getParameterNames(Constructor<?> ctor) {
            throw new UnsupportedOperationException("Spring AOP cannot handle constructor advice");
        }
    }

    protected static class AspectJAnnotation<A extends Annotation> {
        private static final String[] EXPRESSION_ATTRIBUTES = new String[]{"pointcut", "value"};
        private static Map<Class<?>, AspectJAnnotationType> annotationTypeMap = new HashMap(8);
        private final A annotation;
        private final AspectJAnnotationType annotationType;
        private final String pointcutExpression;
        private final String argumentNames;

        public AspectJAnnotation(A annotation) {
            this.annotation = annotation;
            this.annotationType = this.determineAnnotationType(annotation);
            try {
                this.pointcutExpression = this.resolveExpression(annotation);
                Object argNames = AnnotationUtils.getValue(annotation, "argNames");
                this.argumentNames = argNames instanceof String ? (String)argNames : "";
            }
            catch (Exception ex) {
                throw new IllegalArgumentException(annotation + " is not a valid AspectJ annotation", ex);
            }
        }

        private AspectJAnnotationType determineAnnotationType(A annotation) {
            AspectJAnnotationType type = annotationTypeMap.get(annotation.annotationType());
            if (type != null) {
                return type;
            }
            throw new IllegalStateException("Unknown annotation type: " + annotation);
        }

        private String resolveExpression(A annotation) {
            for (String attributeName : EXPRESSION_ATTRIBUTES) {
                String str;
                Object val = AnnotationUtils.getValue(annotation, attributeName);
                if (!(val instanceof String) || (str = (String)val).isEmpty()) continue;
                return str;
            }
            throw new IllegalStateException("Failed to resolve expression: " + annotation);
        }

        public AspectJAnnotationType getAnnotationType() {
            return this.annotationType;
        }

        public A getAnnotation() {
            return this.annotation;
        }

        public String getPointcutExpression() {
            return this.pointcutExpression;
        }

        public String getArgumentNames() {
            return this.argumentNames;
        }

        public String toString() {
            return this.annotation.toString();
        }

        static {
            annotationTypeMap.put(Pointcut.class, AspectJAnnotationType.AtPointcut);
            annotationTypeMap.put(Around.class, AspectJAnnotationType.AtAround);
            annotationTypeMap.put(Before.class, AspectJAnnotationType.AtBefore);
            annotationTypeMap.put(After.class, AspectJAnnotationType.AtAfter);
            annotationTypeMap.put(AfterReturning.class, AspectJAnnotationType.AtAfterReturning);
            annotationTypeMap.put(AfterThrowing.class, AspectJAnnotationType.AtAfterThrowing);
        }
    }

    protected static enum AspectJAnnotationType {
        AtPointcut,
        AtAround,
        AtBefore,
        AtAfter,
        AtAfterReturning,
        AtAfterThrowing;

    }
}

