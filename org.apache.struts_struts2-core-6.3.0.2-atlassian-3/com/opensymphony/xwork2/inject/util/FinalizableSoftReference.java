/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.inject.util;

import com.opensymphony.xwork2.inject.util.FinalizableReference;
import com.opensymphony.xwork2.inject.util.FinalizableReferenceQueue;
import java.lang.ref.SoftReference;

public abstract class FinalizableSoftReference<T>
extends SoftReference<T>
implements FinalizableReference {
    protected FinalizableSoftReference(T referent) {
        super(referent, FinalizableReferenceQueue.getInstance());
    }
}

