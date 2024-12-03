/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.aop.support.AopUtils
 *  org.springframework.core.Ordered
 *  org.springframework.core.ResolvableType
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ConcurrentReferenceHashMap
 */
package org.springframework.context.event;

import java.util.Map;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.GenericApplicationListener;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;

public class GenericApplicationListenerAdapter
implements GenericApplicationListener {
    private static final Map<Class<?>, ResolvableType> eventTypeCache = new ConcurrentReferenceHashMap();
    private final ApplicationListener<ApplicationEvent> delegate;
    @Nullable
    private final ResolvableType declaredEventType;

    public GenericApplicationListenerAdapter(ApplicationListener<?> delegate) {
        Assert.notNull(delegate, (String)"Delegate listener must not be null");
        this.delegate = delegate;
        this.declaredEventType = GenericApplicationListenerAdapter.resolveDeclaredEventType(this.delegate);
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        this.delegate.onApplicationEvent(event);
    }

    @Override
    public boolean supportsEventType(ResolvableType eventType) {
        if (this.delegate instanceof GenericApplicationListener) {
            return ((GenericApplicationListener)this.delegate).supportsEventType(eventType);
        }
        if (this.delegate instanceof SmartApplicationListener) {
            Class eventClass = eventType.resolve();
            return eventClass != null && ((SmartApplicationListener)this.delegate).supportsEventType(eventClass);
        }
        return this.declaredEventType == null || this.declaredEventType.isAssignableFrom(eventType);
    }

    @Override
    public boolean supportsSourceType(@Nullable Class<?> sourceType) {
        return !(this.delegate instanceof SmartApplicationListener) || ((SmartApplicationListener)this.delegate).supportsSourceType(sourceType);
    }

    @Override
    public int getOrder() {
        return this.delegate instanceof Ordered ? ((Ordered)this.delegate).getOrder() : Integer.MAX_VALUE;
    }

    @Override
    public String getListenerId() {
        return this.delegate instanceof SmartApplicationListener ? ((SmartApplicationListener)this.delegate).getListenerId() : "";
    }

    @Nullable
    private static ResolvableType resolveDeclaredEventType(ApplicationListener<ApplicationEvent> listener) {
        Class targetClass;
        ResolvableType declaredEventType = GenericApplicationListenerAdapter.resolveDeclaredEventType(listener.getClass());
        if ((declaredEventType == null || declaredEventType.isAssignableFrom(ApplicationEvent.class)) && (targetClass = AopUtils.getTargetClass(listener)) != listener.getClass()) {
            declaredEventType = GenericApplicationListenerAdapter.resolveDeclaredEventType(targetClass);
        }
        return declaredEventType;
    }

    @Nullable
    static ResolvableType resolveDeclaredEventType(Class<?> listenerType) {
        ResolvableType eventType = eventTypeCache.get(listenerType);
        if (eventType == null) {
            eventType = ResolvableType.forClass(listenerType).as(ApplicationListener.class).getGeneric(new int[0]);
            eventTypeCache.put(listenerType, eventType);
        }
        return eventType != ResolvableType.NONE ? eventType : null;
    }
}

