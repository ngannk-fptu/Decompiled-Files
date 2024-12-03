/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.spi;

import com.hazelcast.core.DistributedObject;
import com.hazelcast.cp.CPGroupId;

public interface RaftRemoteService {
    public <T extends DistributedObject> T createProxy(String var1);

    public boolean destroyRaftObject(CPGroupId var1, String var2);
}

