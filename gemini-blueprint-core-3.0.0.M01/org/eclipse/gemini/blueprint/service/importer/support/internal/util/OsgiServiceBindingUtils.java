/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.osgi.framework.ServiceReference
 *  org.springframework.util.ObjectUtils
 */
package org.eclipse.gemini.blueprint.service.importer.support.internal.util;

import java.util.Dictionary;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.gemini.blueprint.service.importer.OsgiServiceLifecycleListener;
import org.eclipse.gemini.blueprint.util.OsgiServiceReferenceUtils;
import org.osgi.framework.ServiceReference;
import org.springframework.util.ObjectUtils;

public abstract class OsgiServiceBindingUtils {
    private static final Log log = LogFactory.getLog(OsgiServiceBindingUtils.class);

    public static void callListenersBind(Object serviceProxy, ServiceReference reference, OsgiServiceLifecycleListener[] listeners) {
        if (!ObjectUtils.isEmpty((Object[])listeners)) {
            boolean debug = log.isDebugEnabled();
            Dictionary properties = OsgiServiceReferenceUtils.getServicePropertiesSnapshot(reference);
            for (int i = 0; i < listeners.length; ++i) {
                if (debug) {
                    log.debug((Object)("Calling bind on " + listeners[i] + " w/ reference " + reference));
                }
                try {
                    listeners[i].bind(serviceProxy, (Map)((Object)properties));
                }
                catch (Exception ex) {
                    log.warn((Object)("Bind method on listener " + listeners[i] + " threw exception "), (Throwable)ex);
                }
                if (!debug) continue;
                log.debug((Object)("Called bind on " + listeners[i] + " w/ reference " + reference));
            }
        }
    }

    public static void callListenersUnbind(Object serviceProxy, ServiceReference reference, OsgiServiceLifecycleListener[] listeners) {
        if (!ObjectUtils.isEmpty((Object[])listeners)) {
            boolean debug = log.isDebugEnabled();
            Dictionary properties = reference != null ? OsgiServiceReferenceUtils.getServicePropertiesSnapshot(reference) : null;
            for (int i = 0; i < listeners.length; ++i) {
                if (debug) {
                    log.debug((Object)("Calling unbind on " + listeners[i] + " w/ reference " + reference));
                }
                try {
                    listeners[i].unbind(serviceProxy, (Map)((Object)properties));
                }
                catch (Exception ex) {
                    log.warn((Object)("Unbind method on listener " + listeners[i] + " threw exception "), (Throwable)ex);
                }
                if (!debug) continue;
                log.debug((Object)("Called unbind on " + listeners[i] + " w/ reference " + reference));
            }
        }
    }
}

