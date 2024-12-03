/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.util;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class $Finalizer
extends Thread {
    private static final Logger logger = Logger.getLogger($Finalizer.class.getName());
    private static final String FINALIZABLE_REFERENCE = "com.google.inject.internal.util.$FinalizableReference";
    private final WeakReference<Class<?>> finalizableReferenceClassReference;
    private final PhantomReference<Object> frqReference;
    private final ReferenceQueue<Object> queue = new ReferenceQueue();

    public static ReferenceQueue<Object> startFinalizer(Class<?> finalizableReferenceClass, Object frq) {
        if (!finalizableReferenceClass.getName().equals(FINALIZABLE_REFERENCE)) {
            throw new IllegalArgumentException("Expected com.google.inject.internal.util.FinalizableReference.");
        }
        $Finalizer finalizer = new $Finalizer(finalizableReferenceClass, frq);
        finalizer.start();
        return finalizer.queue;
    }

    private $Finalizer(Class<?> finalizableReferenceClass, Object frq) {
        super($Finalizer.class.getName());
        this.finalizableReferenceClassReference = new WeakReference(finalizableReferenceClass);
        this.frqReference = new PhantomReference<Object>(frq, this.queue);
        this.setDaemon(true);
    }

    @Override
    public void run() {
        try {
            while (true) {
                try {
                    while (true) {
                        this.cleanUp(this.queue.remove());
                    }
                }
                catch (InterruptedException e) {
                    continue;
                }
                break;
            }
        }
        catch (ShutDown shutDown) {
            return;
        }
    }

    private void cleanUp(Reference<?> reference) throws ShutDown {
        Method finalizeReferentMethod = this.getFinalizeReferentMethod();
        do {
            reference.clear();
            if (reference == this.frqReference) {
                throw new ShutDown();
            }
            try {
                finalizeReferentMethod.invoke(reference, new Object[0]);
            }
            catch (Throwable t) {
                logger.log(Level.SEVERE, "Error cleaning up after reference.", t);
            }
        } while ((reference = this.queue.poll()) != null);
    }

    private Method getFinalizeReferentMethod() throws ShutDown {
        Class finalizableReferenceClass = (Class)this.finalizableReferenceClassReference.get();
        if (finalizableReferenceClass == null) {
            throw new ShutDown();
        }
        try {
            return finalizableReferenceClass.getMethod("finalizeReferent", new Class[0]);
        }
        catch (NoSuchMethodException e) {
            throw new AssertionError((Object)e);
        }
    }

    private static class ShutDown
    extends Exception {
        private ShutDown() {
        }
    }
}

