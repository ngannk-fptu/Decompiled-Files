/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.util;

import java.lang.ref.SoftReference;
import javax.media.jai.OperationRegistry;
import javax.media.jai.util.ImagingListener;

public class ImagingListenerImpl
implements ImagingListener {
    private static SoftReference reference = new SoftReference<Object>(null);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static ImagingListenerImpl getInstance() {
        SoftReference softReference = reference;
        synchronized (softReference) {
            ImagingListenerImpl listener;
            Object referent = reference.get();
            if (referent == null) {
                listener = new ImagingListenerImpl();
                reference = new SoftReference<ImagingListenerImpl>(listener);
            } else {
                listener = (ImagingListenerImpl)referent;
            }
            return listener;
        }
    }

    private ImagingListenerImpl() {
    }

    public synchronized boolean errorOccurred(String message, Throwable thrown, Object where, boolean isRetryable) throws RuntimeException {
        if (thrown instanceof RuntimeException && !(where instanceof OperationRegistry)) {
            throw (RuntimeException)thrown;
        }
        System.err.println("Error: " + message);
        System.err.println("Occurs in: " + (where instanceof Class ? ((Class)where).getName() : where.getClass().getName()));
        thrown.printStackTrace(System.err);
        return false;
    }
}

