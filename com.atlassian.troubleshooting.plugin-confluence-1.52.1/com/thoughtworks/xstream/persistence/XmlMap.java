/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.persistence;

import com.thoughtworks.xstream.persistence.PersistenceStrategy;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;

public class XmlMap
extends AbstractMap {
    private final PersistenceStrategy persistenceStrategy;

    public XmlMap(PersistenceStrategy streamStrategy) {
        this.persistenceStrategy = streamStrategy;
    }

    public int size() {
        return this.persistenceStrategy.size();
    }

    public Object get(Object key) {
        return this.persistenceStrategy.get(key);
    }

    public Object put(Object key, Object value) {
        return this.persistenceStrategy.put(key, value);
    }

    public Object remove(Object key) {
        return this.persistenceStrategy.remove(key);
    }

    public Set entrySet() {
        return new XmlMapEntries();
    }

    class XmlMapEntries
    extends AbstractSet {
        XmlMapEntries() {
        }

        public int size() {
            return XmlMap.this.size();
        }

        public boolean isEmpty() {
            return XmlMap.this.isEmpty();
        }

        public Iterator iterator() {
            return XmlMap.this.persistenceStrategy.iterator();
        }
    }
}

