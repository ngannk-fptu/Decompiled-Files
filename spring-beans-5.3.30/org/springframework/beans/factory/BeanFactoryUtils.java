/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.ResolvableType
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package org.springframework.beans.factory;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.HierarchicalBeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public abstract class BeanFactoryUtils {
    public static final String GENERATED_BEAN_NAME_SEPARATOR = "#";
    private static final Map<String, String> transformedBeanNameCache = new ConcurrentHashMap<String, String>();

    public static boolean isFactoryDereference(@Nullable String name) {
        return name != null && name.startsWith("&");
    }

    public static String transformedBeanName(String name) {
        Assert.notNull((Object)name, (String)"'name' must not be null");
        if (!name.startsWith("&")) {
            return name;
        }
        return transformedBeanNameCache.computeIfAbsent(name, beanName -> {
            while ((beanName = beanName.substring("&".length())).startsWith("&")) {
            }
            return beanName;
        });
    }

    public static boolean isGeneratedBeanName(@Nullable String name) {
        return name != null && name.contains(GENERATED_BEAN_NAME_SEPARATOR);
    }

    public static String originalBeanName(String name) {
        Assert.notNull((Object)name, (String)"'name' must not be null");
        int separatorIndex = name.indexOf(GENERATED_BEAN_NAME_SEPARATOR);
        return separatorIndex != -1 ? name.substring(0, separatorIndex) : name;
    }

    public static int countBeansIncludingAncestors(ListableBeanFactory lbf) {
        return BeanFactoryUtils.beanNamesIncludingAncestors(lbf).length;
    }

    public static String[] beanNamesIncludingAncestors(ListableBeanFactory lbf) {
        return BeanFactoryUtils.beanNamesForTypeIncludingAncestors(lbf, Object.class);
    }

    public static String[] beanNamesForTypeIncludingAncestors(ListableBeanFactory lbf, ResolvableType type) {
        HierarchicalBeanFactory hbf;
        Assert.notNull((Object)lbf, (String)"ListableBeanFactory must not be null");
        String[] result = lbf.getBeanNamesForType(type);
        if (lbf instanceof HierarchicalBeanFactory && (hbf = (HierarchicalBeanFactory)((Object)lbf)).getParentBeanFactory() instanceof ListableBeanFactory) {
            String[] parentResult = BeanFactoryUtils.beanNamesForTypeIncludingAncestors((ListableBeanFactory)hbf.getParentBeanFactory(), type);
            result = BeanFactoryUtils.mergeNamesWithParent(result, parentResult, hbf);
        }
        return result;
    }

    public static String[] beanNamesForTypeIncludingAncestors(ListableBeanFactory lbf, ResolvableType type, boolean includeNonSingletons, boolean allowEagerInit) {
        HierarchicalBeanFactory hbf;
        Assert.notNull((Object)lbf, (String)"ListableBeanFactory must not be null");
        String[] result = lbf.getBeanNamesForType(type, includeNonSingletons, allowEagerInit);
        if (lbf instanceof HierarchicalBeanFactory && (hbf = (HierarchicalBeanFactory)((Object)lbf)).getParentBeanFactory() instanceof ListableBeanFactory) {
            String[] parentResult = BeanFactoryUtils.beanNamesForTypeIncludingAncestors((ListableBeanFactory)hbf.getParentBeanFactory(), type, includeNonSingletons, allowEagerInit);
            result = BeanFactoryUtils.mergeNamesWithParent(result, parentResult, hbf);
        }
        return result;
    }

    public static String[] beanNamesForTypeIncludingAncestors(ListableBeanFactory lbf, Class<?> type) {
        HierarchicalBeanFactory hbf;
        Assert.notNull((Object)lbf, (String)"ListableBeanFactory must not be null");
        String[] result = lbf.getBeanNamesForType(type);
        if (lbf instanceof HierarchicalBeanFactory && (hbf = (HierarchicalBeanFactory)((Object)lbf)).getParentBeanFactory() instanceof ListableBeanFactory) {
            String[] parentResult = BeanFactoryUtils.beanNamesForTypeIncludingAncestors((ListableBeanFactory)hbf.getParentBeanFactory(), type);
            result = BeanFactoryUtils.mergeNamesWithParent(result, parentResult, hbf);
        }
        return result;
    }

    public static String[] beanNamesForTypeIncludingAncestors(ListableBeanFactory lbf, Class<?> type, boolean includeNonSingletons, boolean allowEagerInit) {
        HierarchicalBeanFactory hbf;
        Assert.notNull((Object)lbf, (String)"ListableBeanFactory must not be null");
        String[] result = lbf.getBeanNamesForType(type, includeNonSingletons, allowEagerInit);
        if (lbf instanceof HierarchicalBeanFactory && (hbf = (HierarchicalBeanFactory)((Object)lbf)).getParentBeanFactory() instanceof ListableBeanFactory) {
            String[] parentResult = BeanFactoryUtils.beanNamesForTypeIncludingAncestors((ListableBeanFactory)hbf.getParentBeanFactory(), type, includeNonSingletons, allowEagerInit);
            result = BeanFactoryUtils.mergeNamesWithParent(result, parentResult, hbf);
        }
        return result;
    }

    public static String[] beanNamesForAnnotationIncludingAncestors(ListableBeanFactory lbf, Class<? extends Annotation> annotationType) {
        HierarchicalBeanFactory hbf;
        Assert.notNull((Object)lbf, (String)"ListableBeanFactory must not be null");
        String[] result = lbf.getBeanNamesForAnnotation(annotationType);
        if (lbf instanceof HierarchicalBeanFactory && (hbf = (HierarchicalBeanFactory)((Object)lbf)).getParentBeanFactory() instanceof ListableBeanFactory) {
            String[] parentResult = BeanFactoryUtils.beanNamesForAnnotationIncludingAncestors((ListableBeanFactory)hbf.getParentBeanFactory(), annotationType);
            result = BeanFactoryUtils.mergeNamesWithParent(result, parentResult, hbf);
        }
        return result;
    }

    public static <T> Map<String, T> beansOfTypeIncludingAncestors(ListableBeanFactory lbf, Class<T> type) throws BeansException {
        HierarchicalBeanFactory hbf;
        Assert.notNull((Object)lbf, (String)"ListableBeanFactory must not be null");
        LinkedHashMap result = new LinkedHashMap(4);
        result.putAll(lbf.getBeansOfType(type));
        if (lbf instanceof HierarchicalBeanFactory && (hbf = (HierarchicalBeanFactory)((Object)lbf)).getParentBeanFactory() instanceof ListableBeanFactory) {
            Map<String, T> parentResult = BeanFactoryUtils.beansOfTypeIncludingAncestors((ListableBeanFactory)hbf.getParentBeanFactory(), type);
            parentResult.forEach((beanName, beanInstance) -> {
                if (!result.containsKey(beanName) && !hbf.containsLocalBean((String)beanName)) {
                    result.put((String)beanName, (Object)beanInstance);
                }
            });
        }
        return result;
    }

    public static <T> Map<String, T> beansOfTypeIncludingAncestors(ListableBeanFactory lbf, Class<T> type, boolean includeNonSingletons, boolean allowEagerInit) throws BeansException {
        HierarchicalBeanFactory hbf;
        Assert.notNull((Object)lbf, (String)"ListableBeanFactory must not be null");
        LinkedHashMap result = new LinkedHashMap(4);
        result.putAll(lbf.getBeansOfType(type, includeNonSingletons, allowEagerInit));
        if (lbf instanceof HierarchicalBeanFactory && (hbf = (HierarchicalBeanFactory)((Object)lbf)).getParentBeanFactory() instanceof ListableBeanFactory) {
            Map<String, T> parentResult = BeanFactoryUtils.beansOfTypeIncludingAncestors((ListableBeanFactory)hbf.getParentBeanFactory(), type, includeNonSingletons, allowEagerInit);
            parentResult.forEach((beanName, beanInstance) -> {
                if (!result.containsKey(beanName) && !hbf.containsLocalBean((String)beanName)) {
                    result.put((String)beanName, (Object)beanInstance);
                }
            });
        }
        return result;
    }

    public static <T> T beanOfTypeIncludingAncestors(ListableBeanFactory lbf, Class<T> type) throws BeansException {
        Map<String, T> beansOfType = BeanFactoryUtils.beansOfTypeIncludingAncestors(lbf, type);
        return BeanFactoryUtils.uniqueBean(type, beansOfType);
    }

    public static <T> T beanOfTypeIncludingAncestors(ListableBeanFactory lbf, Class<T> type, boolean includeNonSingletons, boolean allowEagerInit) throws BeansException {
        Map<String, T> beansOfType = BeanFactoryUtils.beansOfTypeIncludingAncestors(lbf, type, includeNonSingletons, allowEagerInit);
        return BeanFactoryUtils.uniqueBean(type, beansOfType);
    }

    public static <T> T beanOfType(ListableBeanFactory lbf, Class<T> type) throws BeansException {
        Assert.notNull((Object)lbf, (String)"ListableBeanFactory must not be null");
        Map<String, T> beansOfType = lbf.getBeansOfType(type);
        return BeanFactoryUtils.uniqueBean(type, beansOfType);
    }

    public static <T> T beanOfType(ListableBeanFactory lbf, Class<T> type, boolean includeNonSingletons, boolean allowEagerInit) throws BeansException {
        Assert.notNull((Object)lbf, (String)"ListableBeanFactory must not be null");
        Map<String, T> beansOfType = lbf.getBeansOfType(type, includeNonSingletons, allowEagerInit);
        return BeanFactoryUtils.uniqueBean(type, beansOfType);
    }

    private static String[] mergeNamesWithParent(String[] result, String[] parentResult, HierarchicalBeanFactory hbf) {
        if (parentResult.length == 0) {
            return result;
        }
        ArrayList<String> merged = new ArrayList<String>(result.length + parentResult.length);
        merged.addAll(Arrays.asList(result));
        for (String beanName : parentResult) {
            if (merged.contains(beanName) || hbf.containsLocalBean(beanName)) continue;
            merged.add(beanName);
        }
        return StringUtils.toStringArray(merged);
    }

    private static <T> T uniqueBean(Class<T> type, Map<String, T> matchingBeans) {
        int count = matchingBeans.size();
        if (count == 1) {
            return matchingBeans.values().iterator().next();
        }
        if (count > 1) {
            throw new NoUniqueBeanDefinitionException(type, matchingBeans.keySet());
        }
        throw new NoSuchBeanDefinitionException(type);
    }
}

