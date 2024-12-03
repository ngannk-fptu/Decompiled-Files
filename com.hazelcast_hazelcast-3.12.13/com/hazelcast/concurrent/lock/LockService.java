/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.lock;

import com.hazelcast.concurrent.lock.LockResource;
import com.hazelcast.concurrent.lock.LockStore;
import com.hazelcast.concurrent.lock.LockStoreInfo;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.spi.SharedService;
import com.hazelcast.util.ConstructorFunction;
import java.util.Collection;

public interface LockService
extends SharedService {
    public static final String SERVICE_NAME = "hz:impl:lockService";

    public void registerLockStoreConstructor(String var1, ConstructorFunction<ObjectNamespace, LockStoreInfo> var2);

    public LockStore createLockStore(int var1, ObjectNamespace var2);

    public void clearLockStore(int var1, ObjectNamespace var2);

    public Collection<LockResource> getAllLocks();

    public long getMaxLeaseTimeInMillis();
}

