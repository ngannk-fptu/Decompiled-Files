/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections4;

import java.util.List;
import org.apache.commons.collections4.MultiValuedMap;

public interface ListValuedMap<K, V>
extends MultiValuedMap<K, V> {
    @Override
    public List<V> get(K var1);

    @Override
    public List<V> remove(Object var1);
}

