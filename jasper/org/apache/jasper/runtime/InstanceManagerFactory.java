/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletConfig
 *  org.apache.tomcat.InstanceManager
 */
package org.apache.jasper.runtime;

import javax.servlet.ServletConfig;
import org.apache.jasper.compiler.Localizer;
import org.apache.tomcat.InstanceManager;

public class InstanceManagerFactory {
    private InstanceManagerFactory() {
    }

    public static InstanceManager getInstanceManager(ServletConfig config) {
        InstanceManager instanceManager = (InstanceManager)config.getServletContext().getAttribute(InstanceManager.class.getName());
        if (instanceManager == null) {
            throw new IllegalStateException(Localizer.getMessage("jsp.error.noInstanceManager"));
        }
        return instanceManager;
    }
}

