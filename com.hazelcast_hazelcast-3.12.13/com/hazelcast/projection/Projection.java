/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.projection;

import java.io.Serializable;

public abstract class Projection<I, O>
implements Serializable {
    public abstract O transform(I var1);
}

