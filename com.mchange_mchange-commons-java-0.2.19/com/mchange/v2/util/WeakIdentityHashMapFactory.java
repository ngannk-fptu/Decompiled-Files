/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.util;

import com.mchange.v1.identicator.IdWeakHashMap;
import com.mchange.v1.identicator.StrongIdentityIdenticator;
import java.util.Map;

public final class WeakIdentityHashMapFactory {
    public static Map create() {
        StrongIdentityIdenticator strongIdentityIdenticator = new StrongIdentityIdenticator();
        return new IdWeakHashMap(strongIdentityIdenticator);
    }
}

