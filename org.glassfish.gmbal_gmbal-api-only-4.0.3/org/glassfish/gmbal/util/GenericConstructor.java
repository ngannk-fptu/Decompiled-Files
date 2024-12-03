/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.gmbal.util;

import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GenericConstructor<T> {
    private final Object lock = new Object();
    private String typeName;
    private Class<T> resultType;
    private Class<?> type;
    private Class<?>[] signature;
    private Constructor constructor;

    public GenericConstructor(Class<T> type, String className, Class<?> ... signature) {
        this.resultType = type;
        this.typeName = className;
        this.signature = (Class[])signature.clone();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void getConstructor() {
        Object object = this.lock;
        synchronized (object) {
            if (this.type == null || this.constructor == null) {
                try {
                    this.type = Class.forName(this.typeName);
                    this.constructor = AccessController.doPrivileged(new PrivilegedExceptionAction<Constructor>(){

                        /*
                         * WARNING - Removed try catching itself - possible behaviour change.
                         */
                        @Override
                        public Constructor run() throws Exception {
                            Object object = GenericConstructor.this.lock;
                            synchronized (object) {
                                return GenericConstructor.this.type.getDeclaredConstructor(GenericConstructor.this.signature);
                            }
                        }
                    });
                }
                catch (Exception exc) {
                    Logger.getLogger("org.glassfish.gmbal.util").log(Level.FINE, "Failure in getConstructor", exc);
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized T create(Object ... args) {
        Object object = this.lock;
        synchronized (object) {
            T result = null;
            for (int ctr = 0; ctr <= 1; ++ctr) {
                this.getConstructor();
                if (this.constructor == null) break;
                try {
                    result = this.resultType.cast(this.constructor.newInstance(args));
                    break;
                }
                catch (Exception exc) {
                    this.constructor = null;
                    Logger.getLogger("org.glassfish.gmbal.util").log(Level.WARNING, "Error invoking constructor", exc);
                    continue;
                }
            }
            return result;
        }
    }
}

