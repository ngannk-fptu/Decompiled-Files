/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.util;

import com.google.inject.internal.util.$FinalizableReference;
import com.google.inject.internal.util.$FinalizableReferenceQueue;
import java.lang.ref.WeakReference;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class $FinalizableWeakReference<T>
extends WeakReference<T>
implements $FinalizableReference {
    protected $FinalizableWeakReference(T referent, $FinalizableReferenceQueue queue) {
        super(referent, queue.queue);
        queue.cleanUp();
    }
}

