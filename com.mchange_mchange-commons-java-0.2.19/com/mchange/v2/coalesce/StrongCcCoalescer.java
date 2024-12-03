/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.coalesce;

import com.mchange.v1.identicator.IdHashMap;
import com.mchange.v2.coalesce.AbstractStrongCoalescer;
import com.mchange.v2.coalesce.CoalesceChecker;
import com.mchange.v2.coalesce.CoalesceIdenticator;
import com.mchange.v2.coalesce.Coalescer;

final class StrongCcCoalescer
extends AbstractStrongCoalescer
implements Coalescer {
    StrongCcCoalescer(CoalesceChecker coalesceChecker) {
        super(new IdHashMap(new CoalesceIdenticator(coalesceChecker)));
    }
}

