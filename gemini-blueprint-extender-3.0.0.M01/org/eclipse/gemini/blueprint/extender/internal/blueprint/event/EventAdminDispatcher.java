/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.osgi.framework.BundleContext
 *  org.osgi.service.blueprint.container.BlueprintEvent
 *  org.springframework.util.ClassUtils
 */
package org.eclipse.gemini.blueprint.extender.internal.blueprint.event;

import java.security.AccessController;
import java.security.PrivilegedAction;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.gemini.blueprint.extender.internal.blueprint.event.EventDispatcher;
import org.eclipse.gemini.blueprint.extender.internal.blueprint.event.OsgiEventDispatcher;
import org.eclipse.gemini.blueprint.extender.internal.blueprint.event.PublishType;
import org.osgi.framework.BundleContext;
import org.osgi.service.blueprint.container.BlueprintEvent;
import org.springframework.util.ClassUtils;

public class EventAdminDispatcher {
    private static final Log log;
    private static final boolean eventAdminAvailable;
    private final EventDispatcher dispatcher;

    public EventAdminDispatcher(BundleContext bundleContext) {
        this.dispatcher = eventAdminAvailable ? EventAdminDispatcherFactory.createDispatcher(bundleContext) : null;
    }

    public void beforeClose(final BlueprintEvent event) {
        if (this.dispatcher != null) {
            try {
                if (System.getSecurityManager() != null) {
                    AccessController.doPrivileged(new PrivilegedAction<Object>(){

                        @Override
                        public Object run() {
                            EventAdminDispatcher.this.dispatcher.beforeClose(event);
                            return null;
                        }
                    });
                } else {
                    this.dispatcher.beforeClose(event);
                }
            }
            catch (Throwable th) {
                log.warn((Object)("Cannot dispatch event " + event), th);
            }
        }
    }

    public void beforeRefresh(final BlueprintEvent event) {
        if (this.dispatcher != null) {
            try {
                if (System.getSecurityManager() != null) {
                    AccessController.doPrivileged(new PrivilegedAction<Object>(){

                        @Override
                        public Object run() {
                            EventAdminDispatcher.this.dispatcher.beforeRefresh(event);
                            return null;
                        }
                    });
                } else {
                    this.dispatcher.beforeRefresh(event);
                }
            }
            catch (Throwable th) {
                log.warn((Object)("Cannot dispatch event " + event), th);
            }
        }
    }

    public void afterClose(final BlueprintEvent event) {
        if (this.dispatcher != null) {
            try {
                if (System.getSecurityManager() != null) {
                    AccessController.doPrivileged(new PrivilegedAction<Object>(){

                        @Override
                        public Object run() {
                            EventAdminDispatcher.this.dispatcher.afterClose(event);
                            return null;
                        }
                    });
                } else {
                    this.dispatcher.afterClose(event);
                }
            }
            catch (Throwable th) {
                log.warn((Object)("Cannot dispatch event " + event), th);
            }
        }
    }

    public void afterRefresh(final BlueprintEvent event) {
        if (this.dispatcher != null) {
            try {
                if (System.getSecurityManager() != null) {
                    AccessController.doPrivileged(new PrivilegedAction<Object>(){

                        @Override
                        public Object run() {
                            EventAdminDispatcher.this.dispatcher.afterRefresh(event);
                            return null;
                        }
                    });
                } else {
                    this.dispatcher.afterRefresh(event);
                }
            }
            catch (Throwable th) {
                log.warn((Object)("Cannot dispatch event " + event), th);
            }
        }
    }

    public void refreshFailure(final BlueprintEvent event) {
        if (this.dispatcher != null) {
            try {
                if (System.getSecurityManager() != null) {
                    AccessController.doPrivileged(new PrivilegedAction<Object>(){

                        @Override
                        public Object run() {
                            EventAdminDispatcher.this.dispatcher.refreshFailure(event);
                            return null;
                        }
                    });
                } else {
                    this.dispatcher.refreshFailure(event);
                }
            }
            catch (Throwable th) {
                log.warn((Object)("Cannot dispatch event " + event), th);
            }
        }
    }

    public void grace(final BlueprintEvent event) {
        if (this.dispatcher != null) {
            try {
                if (System.getSecurityManager() != null) {
                    AccessController.doPrivileged(new PrivilegedAction<Object>(){

                        @Override
                        public Object run() {
                            EventAdminDispatcher.this.dispatcher.grace(event);
                            return null;
                        }
                    });
                } else {
                    this.dispatcher.grace(event);
                }
            }
            catch (Throwable th) {
                log.warn((Object)("Cannot dispatch event " + event), th);
            }
        }
    }

    public void waiting(final BlueprintEvent event) {
        if (this.dispatcher != null) {
            try {
                if (System.getSecurityManager() != null) {
                    AccessController.doPrivileged(new PrivilegedAction<Object>(){

                        @Override
                        public Object run() {
                            EventAdminDispatcher.this.dispatcher.waiting(event);
                            return null;
                        }
                    });
                } else {
                    this.dispatcher.waiting(event);
                }
            }
            catch (Throwable th) {
                log.warn((Object)("Cannot dispatch event " + event), th);
            }
        }
    }

    static {
        eventAdminAvailable = ClassUtils.isPresent((String)"org.osgi.service.event.EventAdmin", (ClassLoader)EventAdminDispatcher.class.getClassLoader());
        log = LogFactory.getLog(EventAdminDispatcher.class);
        if (!eventAdminAvailable) {
            log.info((Object)"EventAdmin package not found; no Blueprint lifecycle events will be published");
        }
    }

    private static abstract class EventAdminDispatcherFactory {
        private EventAdminDispatcherFactory() {
        }

        private static EventDispatcher createDispatcher(BundleContext bundleContext) {
            if (log.isTraceEnabled()) {
                log.trace((Object)("Creating [" + OsgiEventDispatcher.class.getName() + "]"));
            }
            return new OsgiEventDispatcher(bundleContext, PublishType.POST);
        }
    }
}

