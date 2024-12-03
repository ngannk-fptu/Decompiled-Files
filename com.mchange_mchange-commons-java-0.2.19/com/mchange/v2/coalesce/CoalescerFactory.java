/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.coalesce;

import com.mchange.v2.coalesce.CoalesceChecker;
import com.mchange.v2.coalesce.Coalescer;
import com.mchange.v2.coalesce.StrongCcCoalescer;
import com.mchange.v2.coalesce.StrongEqualsCoalescer;
import com.mchange.v2.coalesce.SyncedCoalescer;
import com.mchange.v2.coalesce.WeakCcCoalescer;
import com.mchange.v2.coalesce.WeakEqualsCoalescer;

public final class CoalescerFactory {
    public static Coalescer createCoalescer() {
        return CoalescerFactory.createCoalescer(true, true);
    }

    public static Coalescer createCoalescer(boolean bl, boolean bl2) {
        return CoalescerFactory.createCoalescer(null, bl, bl2);
    }

    public static Coalescer createCoalescer(CoalesceChecker coalesceChecker, boolean bl, boolean bl2) {
        Coalescer coalescer = coalesceChecker == null ? (bl ? new WeakEqualsCoalescer() : new StrongEqualsCoalescer()) : (bl ? new WeakCcCoalescer(coalesceChecker) : new StrongCcCoalescer(coalesceChecker));
        return bl2 ? new SyncedCoalescer(coalescer) : coalescer;
    }
}

