/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.ServiceRegistration
 */
package org.eclipse.gemini.blueprint.util;

import org.osgi.framework.ServiceRegistration;

public abstract class OsgiServiceUtils {
    public static boolean unregisterService(ServiceRegistration registration) {
        try {
            if (registration != null) {
                registration.unregister();
                return true;
            }
        }
        catch (IllegalStateException illegalStateException) {
            // empty catch block
        }
        return false;
    }
}

