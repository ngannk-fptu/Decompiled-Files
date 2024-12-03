/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.annotation;

import java.beans.Introspector;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

public class AnnotationBeanNameGenerator
implements BeanNameGenerator {
    private static final String COMPONENT_ANNOTATION_CLASSNAME = "org.springframework.stereotype.Component";

    @Override
    public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
        String beanName;
        if (definition instanceof AnnotatedBeanDefinition && StringUtils.hasText(beanName = this.determineBeanNameFromAnnotation((AnnotatedBeanDefinition)definition))) {
            return beanName;
        }
        return this.buildDefaultBeanName(definition, registry);
    }

    @Nullable
    protected String determineBeanNameFromAnnotation(AnnotatedBeanDefinition annotatedDef) {
        AnnotationMetadata amd = annotatedDef.getMetadata();
        Set<String> types = amd.getAnnotationTypes();
        String beanName = null;
        for (String type : types) {
            String strVal;
            Object value;
            AnnotationAttributes attributes = AnnotationConfigUtils.attributesFor((AnnotatedTypeMetadata)amd, type);
            if (attributes == null || !this.isStereotypeWithNameValue(type, amd.getMetaAnnotationTypes(type), attributes) || !((value = attributes.get("value")) instanceof String) || !StringUtils.hasLength(strVal = (String)value)) continue;
            if (beanName != null && !strVal.equals(beanName)) {
                throw new IllegalStateException("Stereotype annotations suggest inconsistent component names: '" + beanName + "' versus '" + strVal + "'");
            }
            beanName = strVal;
        }
        return beanName;
    }

    protected boolean isStereotypeWithNameValue(String annotationType, Set<String> metaAnnotationTypes, @Nullable Map<String, Object> attributes) {
        boolean isStereotype = annotationType.equals(COMPONENT_ANNOTATION_CLASSNAME) || metaAnnotationTypes.contains(COMPONENT_ANNOTATION_CLASSNAME) || annotationType.equals("javax.annotation.ManagedBean") || annotationType.equals("javax.inject.Named");
        return isStereotype && attributes != null && attributes.containsKey("value");
    }

    protected String buildDefaultBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
        return this.buildDefaultBeanName(definition);
    }

    protected String buildDefaultBeanName(BeanDefinition definition) {
        String beanClassName = definition.getBeanClassName();
        Assert.state(beanClassName != null, "No bean class name set");
        String shortClassName = ClassUtils.getShortName(beanClassName);
        return Introspector.decapitalize(shortClassName);
    }
}

