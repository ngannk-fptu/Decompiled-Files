/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi;

import com.hazelcast.spi.NodeEngine;
import java.util.Properties;

public interface ManagedService {
    public void init(NodeEngine var1, Properties var2);

    public void reset();

    public void shutdown(boolean var1);
}

