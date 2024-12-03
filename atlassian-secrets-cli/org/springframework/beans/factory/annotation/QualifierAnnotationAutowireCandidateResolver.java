/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.AutowireCandidateQualifier;
import org.springframework.beans.factory.support.GenericTypeAwareAutowireCandidateResolver;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public class QualifierAnnotationAutowireCandidateResolver
extends GenericTypeAwareAutowireCandidateResolver {
    private final Set<Class<? extends Annotation>> qualifierTypes = new LinkedHashSet<Class<? extends Annotation>>(2);
    private Class<? extends Annotation> valueAnnotationType = Value.class;

    public QualifierAnnotationAutowireCandidateResolver() {
        this.qualifierTypes.add(Qualifier.class);
        try {
            this.qualifierTypes.add(ClassUtils.forName("javax.inject.Qualifier", QualifierAnnotationAutowireCandidateResolver.class.getClassLoader()));
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
    }

    public QualifierAnnotationAutowireCandidateResolver(Class<? extends Annotation> qualifierType) {
        Assert.notNull(qualifierType, "'qualifierType' must not be null");
        this.qualifierTypes.add(qualifierType);
    }

    public QualifierAnnotationAutowireCandidateResolver(Set<Class<? extends Annotation>> qualifierTypes) {
        Assert.notNull(qualifierTypes, "'qualifierTypes' must not be null");
        this.qualifierTypes.addAll(qualifierTypes);
    }

    public void addQualifierType(Class<? extends Annotation> qualifierType) {
        this.qualifierTypes.add(qualifierType);
    }

    public void setValueAnnotationType(Class<? extends Annotation> valueAnnotationType) {
        this.valueAnnotationType = valueAnnotationType;
    }

    @Override
    public boolean isAutowireCandidate(BeanDefinitionHolder bdHolder, DependencyDescriptor descriptor) {
        Method method;
        MethodParameter methodParam;
        boolean match = super.isAutowireCandidate(bdHolder, descriptor);
        if (match && (match = this.checkQualifiers(bdHolder, descriptor.getAnnotations())) && (methodParam = descriptor.getMethodParameter()) != null && ((method = methodParam.getMethod()) == null || Void.TYPE == method.getReturnType())) {
            match = this.checkQualifiers(bdHolder, methodParam.getMethodAnnotations());
        }
        return match;
    }

    protected boolean checkQualifiers(BeanDefinitionHolder bdHolder, Annotation[] annotationsToSearch) {
        if (ObjectUtils.isEmpty(annotationsToSearch)) {
            return true;
        }
        SimpleTypeConverter typeConverter = new SimpleTypeConverter();
        for (Annotation annotation : annotationsToSearch) {
            Class<? extends Annotation> type = annotation.annotationType();
            boolean checkMeta = true;
            boolean fallbackToMeta = false;
            if (this.isQualifier(type)) {
                if (!this.checkQualifier(bdHolder, annotation, typeConverter)) {
                    fallbackToMeta = true;
                } else {
                    checkMeta = false;
                }
            }
            if (!checkMeta) continue;
            boolean foundMeta = false;
            for (Annotation metaAnn : type.getAnnotations()) {
                Class<? extends Annotation> metaType = metaAnn.annotationType();
                if (!this.isQualifier(metaType)) continue;
                foundMeta = true;
                if ((!fallbackToMeta || !StringUtils.isEmpty(AnnotationUtils.getValue(metaAnn))) && this.checkQualifier(bdHolder, metaAnn, typeConverter)) continue;
                return false;
            }
            if (!fallbackToMeta || foundMeta) continue;
            return false;
        }
        return true;
    }

    protected boolean isQualifier(Class<? extends Annotation> annotationType) {
        for (Class<? extends Annotation> qualifierType : this.qualifierTypes) {
            if (!annotationType.equals(qualifierType) && !annotationType.isAnnotationPresent(qualifierType)) continue;
            return true;
        }
        return false;
    }

    protected boolean checkQualifier(BeanDefinitionHolder bdHolder, Annotation annotation, TypeConverter typeConverter) {
        Map<String, Object> attributes;
        Class<? extends Annotation> type = annotation.annotationType();
        RootBeanDefinition bd = (RootBeanDefinition)bdHolder.getBeanDefinition();
        AutowireCandidateQualifier qualifier = bd.getQualifier(type.getName());
        if (qualifier == null) {
            qualifier = bd.getQualifier(ClassUtils.getShortName(type));
        }
        if (qualifier == null) {
            RootBeanDefinition dbd;
            Annotation targetAnnotation = this.getQualifiedElementAnnotation(bd, type);
            if (targetAnnotation == null) {
                targetAnnotation = this.getFactoryMethodAnnotation(bd, type);
            }
            if (targetAnnotation == null && (dbd = this.getResolvedDecoratedDefinition(bd)) != null) {
                targetAnnotation = this.getFactoryMethodAnnotation(dbd, type);
            }
            if (targetAnnotation == null) {
                if (this.getBeanFactory() != null) {
                    try {
                        Class<?> beanType = this.getBeanFactory().getType(bdHolder.getBeanName());
                        if (beanType != null) {
                            targetAnnotation = AnnotationUtils.getAnnotation(ClassUtils.getUserClass(beanType), type);
                        }
                    }
                    catch (NoSuchBeanDefinitionException noSuchBeanDefinitionException) {
                        // empty catch block
                    }
                }
                if (targetAnnotation == null && bd.hasBeanClass()) {
                    targetAnnotation = AnnotationUtils.getAnnotation(ClassUtils.getUserClass(bd.getBeanClass()), type);
                }
            }
            if (targetAnnotation != null && targetAnnotation.equals(annotation)) {
                return true;
            }
        }
        if ((attributes = AnnotationUtils.getAnnotationAttributes(annotation)).isEmpty() && qualifier == null) {
            return false;
        }
        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            String attributeName = entry.getKey();
            Object expectedValue = entry.getValue();
            Object actualValue = null;
            if (qualifier != null) {
                actualValue = qualifier.getAttribute(attributeName);
            }
            if (actualValue == null) {
                actualValue = bd.getAttribute(attributeName);
            }
            if (actualValue == null && attributeName.equals("value") && expectedValue instanceof String && bdHolder.matchesName((String)expectedValue)) continue;
            if (actualValue == null && qualifier != null) {
                actualValue = AnnotationUtils.getDefaultValue(annotation, attributeName);
            }
            if (actualValue != null) {
                actualValue = typeConverter.convertIfNecessary(actualValue, expectedValue.getClass());
            }
            if (expectedValue.equals(actualValue)) continue;
            return false;
        }
        return true;
    }

    @Nullable
    protected Annotation getQualifiedElementAnnotation(RootBeanDefinition bd, Class<? extends Annotation> type) {
        AnnotatedElement qualifiedElement = bd.getQualifiedElement();
        return qualifiedElement != null ? AnnotationUtils.getAnnotation(qualifiedElement, type) : null;
    }

    @Nullable
    protected Annotation getFactoryMethodAnnotation(RootBeanDefinition bd, Class<? extends Annotation> type) {
        Method resolvedFactoryMethod = bd.getResolvedFactoryMethod();
        return resolvedFactoryMethod != null ? AnnotationUtils.getAnnotation(resolvedFactoryMethod, type) : null;
    }

    @Override
    public boolean isRequired(DependencyDescriptor descriptor) {
        if (!super.isRequired(descriptor)) {
            return false;
        }
        Autowired autowired = descriptor.getAnnotation(Autowired.class);
        return autowired == null || autowired.required();
    }

    @Override
    public boolean hasQualifier(DependencyDescriptor descriptor) {
        for (Annotation ann : descriptor.getAnnotations()) {
            if (!this.isQualifier(ann.annotationType())) continue;
            return true;
        }
        return false;
    }

    @Override
    @Nullable
    public Object getSuggestedValue(DependencyDescriptor descriptor) {
        MethodParameter methodParam;
        Object value = this.findValue(descriptor.getAnnotations());
        if (value == null && (methodParam = descriptor.getMethodParameter()) != null) {
            value = this.findValue(methodParam.getMethodAnnotations());
        }
        return value;
    }

    @Nullable
    protected Object findValue(Annotation[] annotationsToSearch) {
        AnnotationAttributes attr;
        if (annotationsToSearch.length > 0 && (attr = AnnotatedElementUtils.getMergedAnnotationAttributes(AnnotatedElementUtils.forAnnotations(annotationsToSearch), this.valueAnnotationType)) != null) {
            return this.extractValue(attr);
        }
        return null;
    }

    protected Object extractValue(AnnotationAttributes attr) {
        Object value = attr.get("value");
        if (value == null) {
            throw new IllegalStateException("Value annotation must have a value attribute");
        }
        return value;
    }
}

