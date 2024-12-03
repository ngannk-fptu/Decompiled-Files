/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.tomcat.util.buf;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.compat.JreCompat;
import org.apache.tomcat.util.res.StringManager;

public class ByteBufferUtils {
    private static final StringManager sm = StringManager.getManager(ByteBufferUtils.class);
    private static final Log log = LogFactory.getLog(ByteBufferUtils.class);
    private static final Object unsafe;
    private static final Method cleanerMethod;
    private static final Method cleanMethod;
    private static final Method invokeCleanerMethod;

    private ByteBufferUtils() {
    }

    public static ByteBuffer expand(ByteBuffer in, int newSize) {
        ByteBuffer out;
        if (in.capacity() >= newSize) {
            return in;
        }
        boolean direct = false;
        if (in.isDirect()) {
            out = ByteBuffer.allocateDirect(newSize);
            direct = true;
        } else {
            out = ByteBuffer.allocate(newSize);
        }
        in.flip();
        out.put(in);
        if (direct) {
            ByteBufferUtils.cleanDirectBuffer(in);
        }
        return out;
    }

    public static void cleanDirectBuffer(ByteBuffer buf) {
        block7: {
            if (cleanMethod != null) {
                try {
                    cleanMethod.invoke(cleanerMethod.invoke((Object)buf, new Object[0]), new Object[0]);
                }
                catch (IllegalAccessException | IllegalArgumentException | SecurityException | InvocationTargetException e) {
                    if (log.isDebugEnabled()) {
                        log.debug((Object)sm.getString("byteBufferUtils.cleaner"), (Throwable)e);
                    }
                    break block7;
                }
            }
            if (invokeCleanerMethod != null) {
                try {
                    invokeCleanerMethod.invoke(unsafe, buf);
                }
                catch (IllegalAccessException | IllegalArgumentException | SecurityException | InvocationTargetException e) {
                    if (!log.isDebugEnabled()) break block7;
                    log.debug((Object)sm.getString("byteBufferUtils.cleaner"), (Throwable)e);
                }
            }
        }
    }

    static {
        ByteBuffer tempBuffer = ByteBuffer.allocateDirect(0);
        Method cleanerMethodLocal = null;
        Method cleanMethodLocal = null;
        Object unsafeLocal = null;
        Method invokeCleanerMethodLocal = null;
        if (JreCompat.isJre9Available()) {
            try {
                Class<?> clazz = Class.forName("sun.misc.Unsafe");
                Field theUnsafe = clazz.getDeclaredField("theUnsafe");
                theUnsafe.setAccessible(true);
                unsafeLocal = theUnsafe.get(null);
                invokeCleanerMethodLocal = clazz.getMethod("invokeCleaner", ByteBuffer.class);
                invokeCleanerMethodLocal.invoke(unsafeLocal, tempBuffer);
            }
            catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | NoSuchFieldException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
                log.warn((Object)sm.getString("byteBufferUtils.cleaner"), (Throwable)e);
                unsafeLocal = null;
                invokeCleanerMethodLocal = null;
            }
        } else {
            try {
                cleanerMethodLocal = tempBuffer.getClass().getMethod("cleaner", new Class[0]);
                cleanerMethodLocal.setAccessible(true);
                Object cleanerObject = cleanerMethodLocal.invoke((Object)tempBuffer, new Object[0]);
                cleanMethodLocal = cleanerObject.getClass().getMethod("clean", new Class[0]);
                cleanMethodLocal.invoke(cleanerObject, new Object[0]);
            }
            catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
                log.warn((Object)sm.getString("byteBufferUtils.cleaner"), (Throwable)e);
                cleanerMethodLocal = null;
                cleanMethodLocal = null;
            }
        }
        cleanerMethod = cleanerMethodLocal;
        cleanMethod = cleanMethodLocal;
        unsafe = unsafeLocal;
        invokeCleanerMethod = invokeCleanerMethodLocal;
    }
}

