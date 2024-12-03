/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.eclipse.gemini.blueprint.util.OsgiBundleUtils
 *  org.eclipse.gemini.blueprint.util.OsgiStringUtils
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.ServiceReference
 *  org.osgi.framework.Version
 *  org.osgi.service.blueprint.container.BlueprintEvent
 *  org.osgi.service.event.Event
 *  org.osgi.service.event.EventAdmin
 *  org.springframework.util.ObjectUtils
 */
package org.eclipse.gemini.blueprint.extender.internal.blueprint.event;

import java.util.Dictionary;
import java.util.Hashtable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.gemini.blueprint.extender.internal.blueprint.event.BlueprintConstants;
import org.eclipse.gemini.blueprint.extender.internal.blueprint.event.EventDispatcher;
import org.eclipse.gemini.blueprint.extender.internal.blueprint.event.PublishType;
import org.eclipse.gemini.blueprint.util.OsgiBundleUtils;
import org.eclipse.gemini.blueprint.util.OsgiStringUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.service.blueprint.container.BlueprintEvent;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.springframework.util.ObjectUtils;

class OsgiEventDispatcher
implements EventDispatcher,
BlueprintConstants {
    private static final Log log = LogFactory.getLog(OsgiEventDispatcher.class);
    private static final String EVENT_ADMIN = "org.osgi.service.event.EventAdmin";
    private final BundleContext bundleContext;
    private final PublishType publisher;

    public OsgiEventDispatcher(BundleContext bundleContext, PublishType publisher) {
        this.bundleContext = bundleContext;
        this.publisher = publisher;
    }

    @Override
    public void afterClose(BlueprintEvent event) {
        Dictionary<String, Object> props = this.init(event);
        this.sendEvent(new Event("org/osgi/service/blueprint/container/DESTROYED", props));
    }

    @Override
    public void afterRefresh(BlueprintEvent event) {
        Dictionary<String, Object> props = this.init(event);
        this.sendEvent(new Event("org/osgi/service/blueprint/container/CREATED", props));
    }

    @Override
    public void beforeClose(BlueprintEvent event) {
        Dictionary<String, Object> props = this.init(event);
        this.sendEvent(new Event("org/osgi/service/blueprint/container/DESTROYING", props));
    }

    @Override
    public void beforeRefresh(BlueprintEvent event) {
        Dictionary<String, Object> props = this.init(event);
        this.sendEvent(new Event("org/osgi/service/blueprint/container/CREATING", props));
    }

    @Override
    public void refreshFailure(BlueprintEvent event) {
        Dictionary<String, Object> props = this.init(event);
        Throwable th = event.getCause();
        props.put("exception", th);
        props.put("cause", th);
        props.put("exception.class", th.getClass().getName());
        String msg = th.getMessage();
        props.put("exception.message", msg != null ? msg : "");
        this.initDependencies(props, event);
        this.sendEvent(new Event("org/osgi/service/blueprint/container/FAILURE", props));
    }

    @Override
    public void grace(BlueprintEvent event) {
        Dictionary<String, Object> props = this.init(event);
        this.initDependencies(props, event);
        this.sendEvent(new Event("org/osgi/service/blueprint/container/GRACE_PERIOD", props));
    }

    @Override
    public void waiting(BlueprintEvent event) {
        Dictionary<String, Object> props = this.init(event);
        this.initDependencies(props, event);
        this.sendEvent(new Event("org/osgi/service/blueprint/container/WAITING", props));
    }

    private void initDependencies(Dictionary<String, Object> props, BlueprintEvent event) {
        Object[] deps = event.getDependencies();
        if (!ObjectUtils.isEmpty((Object[])deps)) {
            props.put("dependencies", deps);
            props.put("dependencies.all", deps);
        }
    }

    private Dictionary<String, Object> init(BlueprintEvent event) {
        Hashtable<String, Object> props = new Hashtable<String, Object>();
        Bundle bundle = event.getBundle();
        ((Dictionary)props).put("timestamp", System.currentTimeMillis());
        ((Dictionary)props).put("event", event);
        ((Dictionary)props).put("type", event.getType());
        ((Dictionary)props).put("bundle", event.getBundle());
        ((Dictionary)props).put("bundle.id", bundle.getBundleId());
        String name = OsgiStringUtils.nullSafeName((Bundle)bundle);
        ((Dictionary)props).put("bundle.name", name);
        ((Dictionary)props).put("Bundle-Name", name);
        String symName = OsgiStringUtils.nullSafeSymbolicName((Bundle)bundle);
        ((Dictionary)props).put("bundle.symbolicName", symName);
        ((Dictionary)props).put("Bundle-SymbolicName", symName);
        Version version = OsgiBundleUtils.getBundleVersion((Bundle)bundle);
        ((Dictionary)props).put("bundle.version", version);
        ((Dictionary)props).put("Bundle-Version", version);
        Bundle extenderBundle = event.getExtenderBundle();
        ((Dictionary)props).put("extender.bundle", extenderBundle);
        ((Dictionary)props).put("extender.bundle.id", extenderBundle.getBundleId());
        ((Dictionary)props).put("extender.bundle.symbolicName", extenderBundle.getSymbolicName());
        Version extenderVersion = OsgiBundleUtils.getBundleVersion((Bundle)extenderBundle);
        ((Dictionary)props).put("extender.bundle.version", extenderVersion);
        return props;
    }

    private void sendEvent(Event osgiEvent) {
        boolean trace = log.isTraceEnabled();
        ServiceReference ref = this.bundleContext.getServiceReference(EVENT_ADMIN);
        if (ref != null) {
            EventAdmin eventAdmin = (EventAdmin)this.bundleContext.getService(ref);
            if (eventAdmin != null) {
                if (trace) {
                    StringBuilder sb = new StringBuilder();
                    String[] names = osgiEvent.getPropertyNames();
                    sb.append("{");
                    for (int i = 0; i < names.length; ++i) {
                        String name = names[i];
                        sb.append(name);
                        sb.append("=");
                        Object value = osgiEvent.getProperty(name);
                        sb.append(ObjectUtils.getDisplayString((Object)value));
                        if (i >= names.length - 1) continue;
                        sb.append(",");
                    }
                    sb.append("}");
                    log.trace((Object)("Broadcasting OSGi event " + osgiEvent + " w/ props " + sb.toString()));
                }
                this.publisher.publish(eventAdmin, osgiEvent);
            }
        } else {
            log.trace((Object)("No event admin found for broadcasting event " + osgiEvent));
        }
    }
}

