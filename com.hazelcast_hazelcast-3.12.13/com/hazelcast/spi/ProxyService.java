/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi;

import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.DistributedObjectListener;
import com.hazelcast.spi.CoreService;
import java.util.Collection;

public interface ProxyService
extends CoreService {
    public int getProxyCount();

    public void initializeDistributedObject(String var1, String var2);

    public DistributedObject getDistributedObject(String var1, String var2);

    public void destroyDistributedObject(String var1, String var2);

    public Collection<DistributedObject> getDistributedObjects(String var1);

    public Collection<String> getDistributedObjectNames(String var1);

    public Collection<DistributedObject> getAllDistributedObjects();

    public String addProxyListener(DistributedObjectListener var1);

    public boolean removeProxyListener(String var1);
}

