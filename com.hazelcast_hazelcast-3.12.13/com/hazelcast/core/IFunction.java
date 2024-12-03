/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.nio.serialization.BinaryInterface;
import java.io.Serializable;

@BinaryInterface
public interface IFunction<T, R>
extends Serializable {
    public R apply(T var1);
}

