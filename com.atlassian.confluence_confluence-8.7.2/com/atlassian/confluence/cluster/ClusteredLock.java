/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.cluster;

import com.atlassian.confluence.concurrent.Lock;
import java.io.Serializable;

@Deprecated
public interface ClusteredLock
extends Lock {
    public Serializable getValue();

    public void setValue(Serializable var1);
}

