/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.tomcat.util.compat;

import java.lang.reflect.Method;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.compat.Jre21Compat;
import org.apache.tomcat.util.res.StringManager;

public class Jre22Compat
extends Jre21Compat {
    private static final Log log = LogFactory.getLog(Jre22Compat.class);
    private static final StringManager sm = StringManager.getManager(Jre22Compat.class);
    private static final boolean hasPanama;

    static boolean isSupported() {
        return hasPanama;
    }

    static {
        Class<?> c1 = null;
        Class<?> c2 = null;
        Method m1 = null;
        try {
            c1 = Class.forName("java.lang.foreign.MemorySegment");
            c2 = Class.forName("java.io.Console");
            m1 = c1.getMethod("getString", Long.TYPE);
            c2.getMethod("isTerminal", new Class[0]);
        }
        catch (ClassNotFoundException e) {
            log.debug((Object)sm.getString("jre22Compat.javaPre22"), (Throwable)e);
        }
        catch (ReflectiveOperationException e) {
            log.debug((Object)sm.getString("jre22Compat.unexpected"), (Throwable)e);
        }
        hasPanama = m1 != null;
    }
}

