/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map;

import com.hazelcast.nio.serialization.BinaryInterface;
import java.io.Serializable;

@BinaryInterface
public interface MapInterceptor
extends Serializable {
    public Object interceptGet(Object var1);

    public void afterGet(Object var1);

    public Object interceptPut(Object var1, Object var2);

    public void afterPut(Object var1);

    public Object interceptRemove(Object var1);

    public void afterRemove(Object var1);
}

