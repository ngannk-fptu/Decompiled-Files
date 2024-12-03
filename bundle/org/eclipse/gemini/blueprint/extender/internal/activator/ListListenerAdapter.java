/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextEvent
 *  org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextListener
 *  org.osgi.framework.BundleContext
 *  org.osgi.util.tracker.ServiceTracker
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.core.GenericTypeResolver
 */
package org.eclipse.gemini.blueprint.extender.internal.activator;

import java.util.Map;
import java.util.WeakHashMap;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextEvent;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextListener;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.GenericTypeResolver;

class ListListenerAdapter
implements OsgiBundleApplicationContextListener<OsgiBundleApplicationContextEvent>,
InitializingBean,
DisposableBean {
    private final ServiceTracker tracker;
    private final Map<Class<? extends OsgiBundleApplicationContextListener>, Class<? extends OsgiBundleApplicationContextEvent>> eventCache = new WeakHashMap<Class<? extends OsgiBundleApplicationContextListener>, Class<? extends OsgiBundleApplicationContextEvent>>();

    public ListListenerAdapter(BundleContext bundleContext) {
        this.tracker = new ServiceTracker(bundleContext, OsgiBundleApplicationContextListener.class.getName(), null);
    }

    public void afterPropertiesSet() {
        this.tracker.open();
    }

    public void destroy() {
        this.tracker.close();
        this.eventCache.clear();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void onOsgiApplicationEvent(OsgiBundleApplicationContextEvent event) {
        Object[] listeners = this.tracker.getServices();
        if (listeners != null) {
            Map<Class<? extends OsgiBundleApplicationContextListener>, Class<? extends OsgiBundleApplicationContextEvent>> map = this.eventCache;
            synchronized (map) {
                for (Object listnr : listeners) {
                    OsgiBundleApplicationContextListener listener = (OsgiBundleApplicationContextListener)listnr;
                    Class<?> listenerClass = listener.getClass();
                    Class<? extends OsgiBundleApplicationContextEvent> eventType = this.eventCache.get(listenerClass);
                    if (eventType == null) {
                        Class<OsgiBundleApplicationContextEvent> evtType = GenericTypeResolver.resolveTypeArgument(listenerClass, OsgiBundleApplicationContextListener.class);
                        if (evtType == null) {
                            evtType = OsgiBundleApplicationContextEvent.class;
                        }
                        eventType = evtType != null && OsgiBundleApplicationContextEvent.class.isAssignableFrom(evtType) ? evtType : OsgiBundleApplicationContextEvent.class;
                        this.eventCache.put(listenerClass, eventType);
                    }
                    if (!eventType.isInstance(event)) continue;
                    listener.onOsgiApplicationEvent(event);
                }
            }
        }
    }
}

