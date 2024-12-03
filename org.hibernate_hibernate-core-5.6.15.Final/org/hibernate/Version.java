/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate;

import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.build.AllowSysOut;
import org.jboss.logging.Logger;

public final class Version {
    private static final String VERSION = Version.initVersion();

    private static String initVersion() {
        String version = Version.class.getPackage().getImplementationVersion();
        return version != null ? version : "[WORKING]";
    }

    private Version() {
    }

    public static String getVersionString() {
        return VERSION;
    }

    public static void logVersion() {
        ((CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)Version.class.getName())).version(Version.getVersionString());
    }

    @AllowSysOut
    public static void main(String[] args) {
        System.out.println("Hibernate ORM core version " + Version.getVersionString());
    }
}

