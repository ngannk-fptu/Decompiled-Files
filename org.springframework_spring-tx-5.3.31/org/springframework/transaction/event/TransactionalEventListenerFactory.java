/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.ApplicationListener
 *  org.springframework.context.event.EventListenerFactory
 *  org.springframework.core.Ordered
 *  org.springframework.core.annotation.AnnotatedElementUtils
 */
package org.springframework.transaction.event;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListenerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.transaction.event.TransactionalApplicationListenerMethodAdapter;
import org.springframework.transaction.event.TransactionalEventListener;

public class TransactionalEventListenerFactory
implements EventListenerFactory,
Ordered {
    private int order = 50;

    public void setOrder(int order) {
        this.order = order;
    }

    public int getOrder() {
        return this.order;
    }

    public boolean supportsMethod(Method method) {
        return AnnotatedElementUtils.hasAnnotation((AnnotatedElement)method, TransactionalEventListener.class);
    }

    public ApplicationListener<?> createApplicationListener(String beanName, Class<?> type, Method method) {
        return new TransactionalApplicationListenerMethodAdapter(beanName, type, method);
    }
}

