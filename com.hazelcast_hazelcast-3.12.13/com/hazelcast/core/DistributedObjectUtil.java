/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.PrefixedDistributedObject;

public final class DistributedObjectUtil {
    private DistributedObjectUtil() {
    }

    public static String getName(DistributedObject distributedObject) {
        if (distributedObject instanceof PrefixedDistributedObject) {
            return ((PrefixedDistributedObject)distributedObject).getPrefixedName();
        }
        return distributedObject.getName();
    }
}

