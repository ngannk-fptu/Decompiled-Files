/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.tomcat.util.security;

import java.lang.reflect.Field;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

public class PrivilegedSetAccessControlContext
implements PrivilegedAction<Void> {
    private static final Log log = LogFactory.getLog(PrivilegedSetAccessControlContext.class);
    private static final StringManager sm = StringManager.getManager(PrivilegedSetAccessControlContext.class);
    private static final AccessControlContext acc = AccessController.getContext();
    private static final Field field;
    private final Thread t;

    public PrivilegedSetAccessControlContext(Thread t) {
        this.t = t;
    }

    @Override
    public Void run() {
        try {
            if (field != null) {
                field.set(this.t, acc);
            }
        }
        catch (IllegalAccessException | IllegalArgumentException e) {
            log.warn((Object)sm.getString("privilegedSetAccessControlContext.setFailed"), (Throwable)e);
        }
        return null;
    }

    static {
        Field f = null;
        try {
            f = Thread.class.getDeclaredField("inheritedAccessControlContext");
            f.setAccessible(true);
        }
        catch (NoSuchFieldException | SecurityException e) {
            log.warn((Object)sm.getString("privilegedSetAccessControlContext.lookupFailed"), (Throwable)e);
        }
        field = f;
    }
}

