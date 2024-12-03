/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.httpclient.util;

import java.io.InterruptedIOException;
import java.lang.reflect.Method;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ExceptionUtil {
    private static final Log LOG = LogFactory.getLog(ExceptionUtil.class);
    private static final Method INIT_CAUSE_METHOD = ExceptionUtil.getInitCauseMethod();
    private static final Class SOCKET_TIMEOUT_CLASS = ExceptionUtil.SocketTimeoutExceptionClass();

    private static Method getInitCauseMethod() {
        try {
            Class[] paramsClasses = new Class[]{Throwable.class};
            return Throwable.class.getMethod("initCause", paramsClasses);
        }
        catch (NoSuchMethodException e) {
            return null;
        }
    }

    private static Class SocketTimeoutExceptionClass() {
        try {
            return Class.forName("java.net.SocketTimeoutException");
        }
        catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static void initCause(Throwable throwable, Throwable cause) {
        if (INIT_CAUSE_METHOD != null) {
            try {
                INIT_CAUSE_METHOD.invoke((Object)throwable, cause);
            }
            catch (Exception e) {
                LOG.warn((Object)"Exception invoking Throwable.initCause", (Throwable)e);
            }
        }
    }

    public static boolean isSocketTimeoutException(InterruptedIOException e) {
        if (SOCKET_TIMEOUT_CLASS != null) {
            return SOCKET_TIMEOUT_CLASS.isInstance(e);
        }
        return true;
    }
}

