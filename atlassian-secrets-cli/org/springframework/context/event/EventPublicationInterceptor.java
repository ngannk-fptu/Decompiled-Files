/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.event;

import java.lang.reflect.Constructor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class EventPublicationInterceptor
implements MethodInterceptor,
ApplicationEventPublisherAware,
InitializingBean {
    @Nullable
    private Constructor<?> applicationEventClassConstructor;
    @Nullable
    private ApplicationEventPublisher applicationEventPublisher;

    public void setApplicationEventClass(Class<?> applicationEventClass) {
        if (ApplicationEvent.class == applicationEventClass || !ApplicationEvent.class.isAssignableFrom(applicationEventClass)) {
            throw new IllegalArgumentException("'applicationEventClass' needs to extend ApplicationEvent");
        }
        try {
            this.applicationEventClassConstructor = applicationEventClass.getConstructor(Object.class);
        }
        catch (NoSuchMethodException ex) {
            throw new IllegalArgumentException("ApplicationEvent class [" + applicationEventClass.getName() + "] does not have the required Object constructor: " + ex);
        }
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (this.applicationEventClassConstructor == null) {
            throw new IllegalArgumentException("Property 'applicationEventClass' is required");
        }
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object retVal = invocation.proceed();
        Assert.state(this.applicationEventClassConstructor != null, "No ApplicationEvent class set");
        ApplicationEvent event = (ApplicationEvent)this.applicationEventClassConstructor.newInstance(invocation.getThis());
        Assert.state(this.applicationEventPublisher != null, "No ApplicationEventPublisher available");
        this.applicationEventPublisher.publishEvent(event);
        return retVal;
    }
}

