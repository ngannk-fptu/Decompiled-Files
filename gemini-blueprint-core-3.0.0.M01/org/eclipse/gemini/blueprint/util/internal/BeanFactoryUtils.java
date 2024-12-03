/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.BeanFactoryUtils
 *  org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 *  org.springframework.util.Assert
 */
package org.eclipse.gemini.blueprint.util.internal;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.Assert;

public abstract class BeanFactoryUtils {
    public static String[] getTransitiveDependenciesForBean(ConfigurableListableBeanFactory beanFactory, String beanName, boolean rawFactoryBeans, Class<?> type) {
        Assert.notNull((Object)beanFactory);
        Assert.hasText((String)beanName);
        Assert.isTrue((boolean)beanFactory.containsBean(beanName), (String)("no bean by name [" + beanName + "] can be found"));
        LinkedHashSet<String> beans = new LinkedHashSet<String>(8);
        LinkedHashSet<String> innerBeans = new LinkedHashSet<String>(4);
        BeanFactoryUtils.getTransitiveBeans(beanFactory, beanName, rawFactoryBeans, beans, innerBeans);
        if (type != null) {
            Iterator iter = beans.iterator();
            while (iter.hasNext()) {
                String bean = (String)iter.next();
                if (beanFactory.isTypeMatch(bean, type)) continue;
                iter.remove();
            }
        }
        return beans.toArray(new String[beans.size()]);
    }

    private static void getTransitiveBeans(ConfigurableListableBeanFactory beanFactory, String beanName, boolean rawFactoryBeans, Set<String> beanNames, Set<String> innerBeans) {
        String transformedBeanName = org.springframework.beans.factory.BeanFactoryUtils.transformedBeanName((String)beanName);
        String[] beans = beanFactory.getDependenciesForBean(transformedBeanName);
        for (int i = 0; i < beans.length; ++i) {
            String bean = beans[i];
            if (beanFactory.containsBean(bean)) {
                if (rawFactoryBeans && beanFactory.isFactoryBean(bean)) {
                    bean = "&" + beans[i];
                }
                if (beanNames.contains(bean)) continue;
                beanNames.add(bean);
                BeanFactoryUtils.getTransitiveBeans(beanFactory, bean, rawFactoryBeans, beanNames, innerBeans);
                continue;
            }
            if (!innerBeans.add(bean)) continue;
            BeanFactoryUtils.getTransitiveBeans(beanFactory, bean, rawFactoryBeans, beanNames, innerBeans);
        }
    }
}

