/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.tomcat.util.compat;

import java.lang.reflect.Field;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.compat.Jre16Compat;
import org.apache.tomcat.util.res.StringManager;

public class Jre19Compat
extends Jre16Compat {
    private static final Log log = LogFactory.getLog(Jre19Compat.class);
    private static final StringManager sm = StringManager.getManager(Jre19Compat.class);
    private static final boolean supported;

    static boolean isSupported() {
        return supported;
    }

    @Override
    public Object getExecutor(Thread thread) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Object result = super.getExecutor(thread);
        if (result == null) {
            Object holder = null;
            Object task = null;
            try {
                Field holderField = thread.getClass().getDeclaredField("holder");
                holderField.setAccessible(true);
                holder = holderField.get(thread);
                Field taskField = holder.getClass().getDeclaredField("task");
                taskField.setAccessible(true);
                task = taskField.get(holder);
            }
            catch (NoSuchFieldException nfe) {
                return null;
            }
            if (task != null && task.getClass().getCanonicalName() != null && (task.getClass().getCanonicalName().equals("org.apache.tomcat.util.threads.ThreadPoolExecutor.Worker") || task.getClass().getCanonicalName().equals("java.util.concurrent.ThreadPoolExecutor.Worker"))) {
                Field executorField = task.getClass().getDeclaredField("this$0");
                executorField.setAccessible(true);
                result = executorField.get(task);
            }
        }
        return result;
    }

    static {
        Class<?> c1 = null;
        try {
            c1 = Class.forName("java.lang.WrongThreadException");
        }
        catch (ClassNotFoundException cnfe) {
            log.debug((Object)sm.getString("jre19Compat.javaPre19"), (Throwable)cnfe);
        }
        supported = c1 != null;
    }
}

