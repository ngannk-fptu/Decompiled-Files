/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.tomcat.util.compat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.compat.Jre19Compat;
import org.apache.tomcat.util.res.StringManager;

public class Jre21Compat
extends Jre19Compat {
    private static final Log log = LogFactory.getLog(Jre21Compat.class);
    private static final StringManager sm = StringManager.getManager(Jre21Compat.class);
    private static final Method nameMethod;
    private static final Method startMethod;
    private static final Method ofVirtualMethod;

    static boolean isSupported() {
        return ofVirtualMethod != null;
    }

    @Override
    public Object createVirtualThreadBuilder(String name) {
        try {
            Object threadBuilder = ofVirtualMethod.invoke(null, (Object[])null);
            nameMethod.invoke(threadBuilder, name, 0L);
            return threadBuilder;
        }
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    @Override
    public void threadBuilderStart(Object threadBuilder, Runnable command) {
        try {
            startMethod.invoke(threadBuilder, command);
        }
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    static {
        Class<?> c1 = null;
        Method m1 = null;
        Method m2 = null;
        Method m3 = null;
        try {
            c1 = Class.forName("java.lang.Thread$Builder");
            m1 = c1.getMethod("name", String.class, Long.TYPE);
            m2 = c1.getMethod("start", Runnable.class);
            m3 = Thread.class.getMethod("ofVirtual", null);
        }
        catch (ClassNotFoundException e) {
            log.debug((Object)sm.getString("jre21Compat.javaPre21"), (Throwable)e);
        }
        catch (ReflectiveOperationException e) {
            log.error((Object)sm.getString("jre21Compat.unexpected"), (Throwable)e);
        }
        nameMethod = m1;
        startMethod = m2;
        ofVirtualMethod = m3;
    }
}

