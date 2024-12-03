/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.tomcat.jdbc.pool;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class ClassLoaderUtil {
    private static final Log log = LogFactory.getLog(ClassLoaderUtil.class);
    private static final boolean onlyAttemptFirstLoader = Boolean.getBoolean("org.apache.tomcat.jdbc.pool.onlyAttemptCurrentClassLoader");

    public static Class<?> loadClass(String className, ClassLoader ... classLoaders) throws ClassNotFoundException {
        ClassNotFoundException last = null;
        StringBuilder errorMsg = null;
        for (ClassLoader cl : classLoaders) {
            try {
                if (cl != null) {
                    if (log.isDebugEnabled()) {
                        log.debug((Object)("Attempting to load class[" + className + "] from " + cl));
                    }
                    return Class.forName(className, true, cl);
                }
                throw new ClassNotFoundException("Classloader is null");
            }
            catch (ClassNotFoundException x) {
                last = x;
                if (errorMsg == null) {
                    errorMsg = new StringBuilder();
                } else {
                    errorMsg.append(';');
                }
                errorMsg.append("ClassLoader:");
                errorMsg.append(cl);
                if (onlyAttemptFirstLoader) break;
            }
        }
        throw new ClassNotFoundException("Unable to load class: " + className + " from " + errorMsg, last);
    }
}

