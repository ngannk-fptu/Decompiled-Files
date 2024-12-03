/*
 * Decompiled with CFR 0.152.
 */
package groovy.json.internal;

import groovy.json.internal.MapItemValue;
import groovy.json.internal.Value;
import java.util.Map;

public interface ValueMap<K, V>
extends Map<K, V> {
    public void add(MapItemValue var1);

    public int len();

    public boolean hydrated();

    public Map.Entry<String, Value>[] items();
}

