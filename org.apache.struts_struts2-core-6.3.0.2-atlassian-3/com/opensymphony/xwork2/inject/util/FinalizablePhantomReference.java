/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.inject.util;

import com.opensymphony.xwork2.inject.util.FinalizableReference;
import com.opensymphony.xwork2.inject.util.FinalizableReferenceQueue;
import java.lang.ref.PhantomReference;

public abstract class FinalizablePhantomReference<T>
extends PhantomReference<T>
implements FinalizableReference {
    protected FinalizablePhantomReference(T referent) {
        super(referent, FinalizableReferenceQueue.getInstance());
    }
}

