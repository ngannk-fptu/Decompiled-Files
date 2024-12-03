/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jetty.util;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.eclipse.jetty.util.Index;

abstract class AbstractTrie<V>
implements Index.Mutable<V> {
    final boolean _caseSensitive;

    protected AbstractTrie(boolean caseSensitive) {
        this._caseSensitive = caseSensitive;
    }

    public boolean isCaseInsensitive() {
        return !this._caseSensitive;
    }

    public boolean isCaseSensitive() {
        return this._caseSensitive;
    }

    @Override
    public boolean put(V v) {
        return this.put(v.toString(), v);
    }

    @Override
    public V remove(String s) {
        V o = this.get(s);
        this.put(s, null);
        return o;
    }

    @Override
    public V get(String s) {
        return this.get(s, 0, s.length());
    }

    @Override
    public V get(ByteBuffer b) {
        return this.get(b, 0, b.remaining());
    }

    @Override
    public V getBest(String s) {
        return this.getBest(s, 0, s.length());
    }

    @Override
    public V getBest(byte[] b, int offset, int len) {
        return this.getBest(new String(b, offset, len, StandardCharsets.ISO_8859_1));
    }

    protected static int requiredCapacity(Set<String> keys, boolean caseSensitive) {
        ArrayList<String> list = caseSensitive ? new ArrayList<String>(keys) : keys.stream().map(String::toLowerCase).collect(Collectors.toList());
        Collections.sort(list);
        return 1 + AbstractTrie.requiredCapacity(list, 0, list.size(), 0);
    }

    private static int requiredCapacity(List<String> keys, int offset, int length, int index) {
        int required = 0;
        while (true) {
            Character nodeChar = null;
            for (int i = 0; i < length; ++i) {
                String k = keys.get(offset + i);
                if (k.length() <= index) continue;
                char c = k.charAt(index);
                if (nodeChar != null && c == nodeChar.charValue()) continue;
                ++required;
                if (nodeChar != null) {
                    required += AbstractTrie.requiredCapacity(keys, offset, i, index + 1);
                }
                nodeChar = Character.valueOf(c);
                offset += i;
                length -= i;
                i = 0;
            }
            if (nodeChar == null) break;
            ++index;
        }
        return required;
    }

    protected boolean putAll(Map<String, V> contents) {
        for (Map.Entry<String, V> entry : contents.entrySet()) {
            if (this.put(entry.getKey(), entry.getValue())) continue;
            return false;
        }
        return true;
    }
}

