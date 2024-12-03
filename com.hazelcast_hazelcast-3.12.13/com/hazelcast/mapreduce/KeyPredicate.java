/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce;

import com.hazelcast.nio.serialization.BinaryInterface;
import java.io.Serializable;

@Deprecated
@BinaryInterface
public interface KeyPredicate<Key>
extends Serializable {
    public boolean evaluate(Key var1);
}

