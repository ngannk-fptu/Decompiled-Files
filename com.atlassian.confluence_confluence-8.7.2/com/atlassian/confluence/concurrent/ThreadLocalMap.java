/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 */
package com.atlassian.confluence.concurrent;

import com.atlassian.confluence.concurrent.ResettableThreadLocal;
import com.google.common.collect.Maps;
import java.util.Map;

public class ThreadLocalMap<K, V>
extends ResettableThreadLocal<Map<K, V>> {
    @Override
    protected Map<K, V> initialValue() {
        return Maps.newHashMap();
    }
}

