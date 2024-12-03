/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.annotation;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.AutowireCandidateQualifier;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public abstract class BeanFactoryAnnotationUtils {
    public static <T> Map<String, T> qualifiedBeansOfType(ListableBeanFactory beanFactory, Class<T> beanType, String qualifier) throws BeansException {
        String[] candidateBeans = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(beanFactory, beanType);
        LinkedHashMap<String, T> result = new LinkedHashMap<String, T>(4);
        for (String beanName : candidateBeans) {
            if (!BeanFactoryAnnotationUtils.isQualifierMatch(qualifier::equals, beanName, beanFactory)) continue;
            result.put(beanName, beanFactory.getBean(beanName, beanType));
        }
        return result;
    }

    public static <T> T qualifiedBeanOfType(BeanFactory beanFactory, Class<T> beanType, String qualifier) throws BeansException {
        Assert.notNull((Object)beanFactory, "BeanFactory must not be null");
        if (beanFactory instanceof ListableBeanFactory) {
            return BeanFactoryAnnotationUtils.qualifiedBeanOfType((ListableBeanFactory)beanFactory, beanType, qualifier);
        }
        if (beanFactory.containsBean(qualifier)) {
            return beanFactory.getBean(qualifier, beanType);
        }
        throw new NoSuchBeanDefinitionException(qualifier, "No matching " + beanType.getSimpleName() + " bean found for bean name '" + qualifier + "'! (Note: Qualifier matching not supported because given BeanFactory does not implement ConfigurableListableBeanFactory.)");
    }

    private static <T> T qualifiedBeanOfType(ListableBeanFactory bf, Class<T> beanType, String qualifier) {
        String[] candidateBeans = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(bf, beanType);
        String matchingBean = null;
        for (String beanName : candidateBeans) {
            if (!BeanFactoryAnnotationUtils.isQualifierMatch(qualifier::equals, beanName, bf)) continue;
            if (matchingBean != null) {
                throw new NoUniqueBeanDefinitionException(beanType, matchingBean, beanName);
            }
            matchingBean = beanName;
        }
        if (matchingBean != null) {
            return bf.getBean(matchingBean, beanType);
        }
        if (bf.containsBean(qualifier)) {
            return bf.getBean(qualifier, beanType);
        }
        throw new NoSuchBeanDefinitionException(qualifier, "No matching " + beanType.getSimpleName() + " bean found for qualifier '" + qualifier + "' - neither qualifier match nor bean name match!");
    }

    public static boolean isQualifierMatch(Predicate<String> qualifier, String beanName, @Nullable BeanFactory beanFactory) {
        if (qualifier.test(beanName)) {
            return true;
        }
        if (beanFactory != null) {
            for (String alias : beanFactory.getAliases(beanName)) {
                if (!qualifier.test(alias)) continue;
                return true;
            }
            try {
                Qualifier targetAnnotation;
                Class<?> beanType = beanFactory.getType(beanName);
                if (beanFactory instanceof ConfigurableBeanFactory) {
                    Qualifier targetAnnotation2;
                    Method factoryMethod;
                    Object value;
                    AbstractBeanDefinition abd;
                    AutowireCandidateQualifier candidate;
                    BeanDefinition bd = ((ConfigurableBeanFactory)beanFactory).getMergedBeanDefinition(beanName);
                    if (bd instanceof AbstractBeanDefinition && (candidate = (abd = (AbstractBeanDefinition)bd).getQualifier(Qualifier.class.getName())) != null && (value = candidate.getAttribute("value")) != null && qualifier.test(value.toString())) {
                        return true;
                    }
                    if (bd instanceof RootBeanDefinition && (factoryMethod = ((RootBeanDefinition)bd).getResolvedFactoryMethod()) != null && (targetAnnotation2 = AnnotationUtils.getAnnotation(factoryMethod, Qualifier.class)) != null) {
                        return qualifier.test(targetAnnotation2.value());
                    }
                }
                if (beanType != null && (targetAnnotation = AnnotationUtils.getAnnotation(beanType, Qualifier.class)) != null) {
                    return qualifier.test(targetAnnotation.value());
                }
            }
            catch (NoSuchBeanDefinitionException noSuchBeanDefinitionException) {
                // empty catch block
            }
        }
        return false;
    }
}

