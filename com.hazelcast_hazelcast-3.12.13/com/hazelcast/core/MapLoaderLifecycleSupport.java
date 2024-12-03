/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.core.HazelcastInstance;
import java.util.Properties;

public interface MapLoaderLifecycleSupport {
    public void init(HazelcastInstance var1, Properties var2, String var3);

    public void destroy();
}

