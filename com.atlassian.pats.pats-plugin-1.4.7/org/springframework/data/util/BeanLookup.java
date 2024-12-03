/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.ListableBeanFactory
 *  org.springframework.beans.factory.NoUniqueBeanDefinitionException
 *  org.springframework.util.Assert
 */
package org.springframework.data.util;

import java.util.Map;
import javax.annotation.Nullable;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.data.util.Lazy;
import org.springframework.util.Assert;

public abstract class BeanLookup {
    private BeanLookup() {
    }

    public static <T> Lazy<T> lazyIfAvailable(Class<T> type, BeanFactory beanFactory) {
        Assert.notNull(type, (String)"Type must not be null!");
        Assert.isInstanceOf(ListableBeanFactory.class, (Object)beanFactory);
        return Lazy.of(() -> BeanLookup.lookupBean(type, (ListableBeanFactory)beanFactory));
    }

    @Nullable
    private static <T> T lookupBean(Class<T> type, ListableBeanFactory beanFactory) {
        Map names = beanFactory.getBeansOfType(type, false, false);
        switch (names.size()) {
            case 0: {
                return null;
            }
            case 1: {
                return (T)names.values().iterator().next();
            }
        }
        throw new NoUniqueBeanDefinitionException(type, names.keySet());
    }
}

