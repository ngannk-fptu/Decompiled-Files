/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.coalesce;

import com.mchange.v2.coalesce.AbstractWeakCoalescer;
import java.util.WeakHashMap;

class WeakEqualsCoalescer
extends AbstractWeakCoalescer {
    WeakEqualsCoalescer() {
        super(new WeakHashMap());
    }
}

