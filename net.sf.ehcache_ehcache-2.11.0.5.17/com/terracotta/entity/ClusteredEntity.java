/*
 * Decompiled with CFR 0.152.
 */
package com.terracotta.entity;

import com.terracotta.entity.ClusteredEntityState;
import com.terracotta.entity.EntityConfiguration;
import java.io.Serializable;

public interface ClusteredEntity<T extends EntityConfiguration>
extends Serializable {
    public T getConfiguration();

    public ClusteredEntityState getState();
}

