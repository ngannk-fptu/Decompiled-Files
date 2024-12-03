/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.config.BeanPostProcessor
 */
package com.atlassian.activeobjects.external;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.tx.TransactionalProxy;
import java.util.Objects;
import javax.annotation.ParametersAreNonnullByDefault;
import net.java.ao.Transaction;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

@ParametersAreNonnullByDefault
public final class TransactionalAnnotationProcessor
implements BeanPostProcessor {
    private final ActiveObjects ao;

    public TransactionalAnnotationProcessor(ActiveObjects ao) {
        Transaction.class.getAnnotations();
        this.ao = Objects.requireNonNull(ao);
    }

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return TransactionalProxy.isAnnotated(bean.getClass()) ? TransactionalProxy.transactional(this.ao, bean) : bean;
    }
}

