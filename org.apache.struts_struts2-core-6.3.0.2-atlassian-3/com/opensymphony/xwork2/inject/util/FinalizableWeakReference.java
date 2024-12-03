/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.inject.util;

import com.opensymphony.xwork2.inject.util.FinalizableReference;
import com.opensymphony.xwork2.inject.util.FinalizableReferenceQueue;
import java.lang.ref.WeakReference;

public abstract class FinalizableWeakReference<T>
extends WeakReference<T>
implements FinalizableReference {
    protected FinalizableWeakReference(T referent) {
        super(referent, FinalizableReferenceQueue.getInstance());
    }
}

