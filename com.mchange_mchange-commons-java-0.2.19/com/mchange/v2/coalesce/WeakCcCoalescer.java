/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.coalesce;

import com.mchange.v1.identicator.IdWeakHashMap;
import com.mchange.v2.coalesce.AbstractWeakCoalescer;
import com.mchange.v2.coalesce.CoalesceChecker;
import com.mchange.v2.coalesce.CoalesceIdenticator;
import com.mchange.v2.coalesce.Coalescer;

final class WeakCcCoalescer
extends AbstractWeakCoalescer
implements Coalescer {
    WeakCcCoalescer(CoalesceChecker coalesceChecker) {
        super(new IdWeakHashMap(new CoalesceIdenticator(coalesceChecker)));
    }
}

