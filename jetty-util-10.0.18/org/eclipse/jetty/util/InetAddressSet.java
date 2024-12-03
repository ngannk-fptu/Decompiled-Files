/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jetty.util;

import java.net.InetAddress;
import java.util.AbstractSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import org.eclipse.jetty.util.InetAddressPattern;

public class InetAddressSet
extends AbstractSet<String>
implements Set<String>,
Predicate<InetAddress> {
    private Map<String, InetAddressPattern> _patterns = new HashMap<String, InetAddressPattern>();

    @Override
    public boolean add(String pattern) {
        return this._patterns.put(pattern, InetAddressPattern.from(pattern)) == null;
    }

    @Override
    public boolean remove(Object pattern) {
        return this._patterns.remove(pattern) != null;
    }

    @Override
    public Iterator<String> iterator() {
        return this._patterns.keySet().iterator();
    }

    @Override
    public int size() {
        return this._patterns.size();
    }

    @Override
    public boolean test(InetAddress address) {
        if (address == null) {
            return false;
        }
        for (InetAddressPattern pattern : this._patterns.values()) {
            if (!pattern.test(address)) continue;
            return true;
        }
        return false;
    }
}

