/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.springframework.beans.factory.FactoryBean
 */
package com.atlassian.business.insights.confluence.config;

import java.util.Objects;
import javax.annotation.Nonnull;
import org.springframework.beans.factory.FactoryBean;

public class FactoryBeanUtil {
    private FactoryBeanUtil() {
        throw new UnsupportedOperationException(FactoryBeanUtil.class.getName() + " is a utility class");
    }

    static <T> FactoryBean<T> buildFactoryBean(final @Nonnull T bean) {
        Objects.requireNonNull(bean);
        return new FactoryBean<T>(){

            public T getObject() throws Exception {
                return bean;
            }

            public Class<?> getObjectType() {
                return bean.getClass();
            }
        };
    }
}

