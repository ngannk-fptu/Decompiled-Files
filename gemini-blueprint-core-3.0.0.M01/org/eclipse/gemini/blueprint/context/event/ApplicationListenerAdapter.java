/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.ApplicationEvent
 *  org.springframework.context.event.SmartApplicationListener
 *  org.springframework.core.GenericTypeResolver
 */
package org.eclipse.gemini.blueprint.context.event;

import org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextEvent;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextListener;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.core.GenericTypeResolver;

class ApplicationListenerAdapter<E extends OsgiBundleApplicationContextEvent>
implements SmartApplicationListener {
    private final OsgiBundleApplicationContextListener<E> osgiListener;
    private final Class<?> eventType;
    private final String toString;

    static <E extends OsgiBundleApplicationContextEvent> ApplicationListenerAdapter<E> createAdapter(OsgiBundleApplicationContextListener<E> listener) {
        return new ApplicationListenerAdapter<E>(listener);
    }

    private ApplicationListenerAdapter(OsgiBundleApplicationContextListener<E> listener) {
        this.osgiListener = listener;
        Class evtType = GenericTypeResolver.resolveTypeArgument(listener.getClass(), OsgiBundleApplicationContextListener.class);
        this.eventType = evtType == null ? OsgiBundleApplicationContextEvent.class : evtType;
        this.toString = "ApplicationListenerAdapter for listener " + this.osgiListener;
    }

    public void onApplicationEvent(ApplicationEvent event) {
        if (this.eventType.isInstance(event)) {
            this.osgiListener.onOsgiApplicationEvent((OsgiBundleApplicationContextEvent)event);
        }
    }

    public boolean equals(Object obj) {
        return this.osgiListener.equals(obj);
    }

    public int hashCode() {
        return this.osgiListener.hashCode();
    }

    public String toString() {
        return this.toString;
    }

    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return eventType != null && eventType.isAssignableFrom(eventType);
    }

    public boolean supportsSourceType(Class<?> sourceType) {
        return true;
    }

    public int getOrder() {
        return Integer.MAX_VALUE;
    }
}

