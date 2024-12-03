/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextEvent
 *  org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextListener
 *  org.eclipse.gemini.blueprint.context.event.OsgiBundleContextClosedEvent
 *  org.eclipse.gemini.blueprint.context.event.OsgiBundleContextFailedEvent
 *  org.eclipse.gemini.blueprint.context.event.OsgiBundleContextRefreshedEvent
 */
package org.eclipse.gemini.blueprint.extender.internal.support;

import org.apache.commons.logging.Log;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextEvent;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextListener;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleContextClosedEvent;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleContextFailedEvent;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleContextRefreshedEvent;

public class DefaultOsgiBundleApplicationContextListener
implements OsgiBundleApplicationContextListener<OsgiBundleApplicationContextEvent> {
    private final Log log;

    public DefaultOsgiBundleApplicationContextListener(Log log) {
        this.log = log;
    }

    public void onOsgiApplicationEvent(OsgiBundleApplicationContextEvent event) {
        String applicationContextString = event.getApplicationContext().getDisplayName();
        if (event instanceof OsgiBundleContextRefreshedEvent) {
            this.log.info((Object)("Application context successfully refreshed (" + applicationContextString + ")"));
        }
        if (event instanceof OsgiBundleContextFailedEvent) {
            OsgiBundleContextFailedEvent failureEvent = (OsgiBundleContextFailedEvent)event;
            this.log.error((Object)("Application context refresh failed (" + applicationContextString + ")"), failureEvent.getFailureCause());
        }
        if (event instanceof OsgiBundleContextClosedEvent) {
            OsgiBundleContextClosedEvent closedEvent = (OsgiBundleContextClosedEvent)event;
            Throwable error = closedEvent.getFailureCause();
            if (error == null) {
                this.log.info((Object)("Application context succesfully closed (" + applicationContextString + ")"));
            } else {
                this.log.error((Object)("Application context close failed (" + applicationContextString + ")"), error);
            }
        }
    }
}

