/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.ItemListener;
import java.util.Collection;

public interface ICollection<E>
extends Collection<E>,
DistributedObject {
    @Override
    public String getName();

    public String addItemListener(ItemListener<E> var1, boolean var2);

    public boolean removeItemListener(String var1);
}

