/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.coalesce;

import com.mchange.v2.coalesce.AbstractStrongCoalescer;
import com.mchange.v2.coalesce.Coalescer;
import java.util.HashMap;

final class StrongEqualsCoalescer
extends AbstractStrongCoalescer
implements Coalescer {
    StrongEqualsCoalescer() {
        super(new HashMap());
    }
}

