/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.axis.components.logger;

import java.security.AccessController;
import java.security.PrivilegedAction;
import org.apache.commons.discovery.tools.DiscoverSingleton;
import org.apache.commons.logging.Log;

public class LogFactory {
    private static final org.apache.commons.logging.LogFactory logFactory = LogFactory.getLogFactory();
    static /* synthetic */ Class class$org$apache$commons$logging$LogFactory;

    public static Log getLog(String name) {
        return org.apache.commons.logging.LogFactory.getLog((String)name);
    }

    private static final org.apache.commons.logging.LogFactory getLogFactory() {
        return (org.apache.commons.logging.LogFactory)AccessController.doPrivileged(new PrivilegedAction(){

            public Object run() {
                return DiscoverSingleton.find(class$org$apache$commons$logging$LogFactory == null ? (class$org$apache$commons$logging$LogFactory = LogFactory.class$("org.apache.commons.logging.LogFactory")) : class$org$apache$commons$logging$LogFactory, "commons-logging.properties", "org.apache.commons.logging.impl.LogFactoryImpl");
            }
        });
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

