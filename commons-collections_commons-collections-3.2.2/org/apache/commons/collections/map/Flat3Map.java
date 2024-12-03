/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import org.apache.commons.collections.IterableMap;
import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.ResettableIterator;
import org.apache.commons.collections.iterators.EmptyIterator;
import org.apache.commons.collections.iterators.EmptyMapIterator;
import org.apache.commons.collections.map.AbstractHashedMap;
import org.apache.commons.collections.map.HashedMap;

public class Flat3Map
implements IterableMap,
Serializable,
Cloneable {
    private static final long serialVersionUID = -6701087419741928296L;
    private transient int size;
    private transient int hash1;
    private transient int hash2;
    private transient int hash3;
    private transient Object key1;
    private transient Object key2;
    private transient Object key3;
    private transient Object value1;
    private transient Object value2;
    private transient Object value3;
    private transient AbstractHashedMap delegateMap;

    public Flat3Map() {
    }

    public Flat3Map(Map map) {
        this.putAll(map);
    }

    public Object get(Object key) {
        if (this.delegateMap != null) {
            return this.delegateMap.get(key);
        }
        if (key == null) {
            switch (this.size) {
                case 3: {
                    if (this.key3 == null) {
                        return this.value3;
                    }
                }
                case 2: {
                    if (this.key2 == null) {
                        return this.value2;
                    }
                }
                case 1: {
                    if (this.key1 != null) break;
                    return this.value1;
                }
            }
        } else if (this.size > 0) {
            int hashCode = key.hashCode();
            switch (this.size) {
                case 3: {
                    if (this.hash3 == hashCode && key.equals(this.key3)) {
                        return this.value3;
                    }
                }
                case 2: {
                    if (this.hash2 == hashCode && key.equals(this.key2)) {
                        return this.value2;
                    }
                }
                case 1: {
                    if (this.hash1 != hashCode || !key.equals(this.key1)) break;
                    return this.value1;
                }
            }
        }
        return null;
    }

    public int size() {
        if (this.delegateMap != null) {
            return this.delegateMap.size();
        }
        return this.size;
    }

    public boolean isEmpty() {
        return this.size() == 0;
    }

    public boolean containsKey(Object key) {
        if (this.delegateMap != null) {
            return this.delegateMap.containsKey(key);
        }
        if (key == null) {
            switch (this.size) {
                case 3: {
                    if (this.key3 == null) {
                        return true;
                    }
                }
                case 2: {
                    if (this.key2 == null) {
                        return true;
                    }
                }
                case 1: {
                    if (this.key1 != null) break;
                    return true;
                }
            }
        } else if (this.size > 0) {
            int hashCode = key.hashCode();
            switch (this.size) {
                case 3: {
                    if (this.hash3 == hashCode && key.equals(this.key3)) {
                        return true;
                    }
                }
                case 2: {
                    if (this.hash2 == hashCode && key.equals(this.key2)) {
                        return true;
                    }
                }
                case 1: {
                    if (this.hash1 != hashCode || !key.equals(this.key1)) break;
                    return true;
                }
            }
        }
        return false;
    }

    public boolean containsValue(Object value) {
        if (this.delegateMap != null) {
            return this.delegateMap.containsValue(value);
        }
        if (value == null) {
            switch (this.size) {
                case 3: {
                    if (this.value3 == null) {
                        return true;
                    }
                }
                case 2: {
                    if (this.value2 == null) {
                        return true;
                    }
                }
                case 1: {
                    if (this.value1 != null) break;
                    return true;
                }
            }
        } else {
            switch (this.size) {
                case 3: {
                    if (value.equals(this.value3)) {
                        return true;
                    }
                }
                case 2: {
                    if (value.equals(this.value2)) {
                        return true;
                    }
                }
                case 1: {
                    if (!value.equals(this.value1)) break;
                    return true;
                }
            }
        }
        return false;
    }

    public Object put(Object key, Object value) {
        if (this.delegateMap != null) {
            return this.delegateMap.put(key, value);
        }
        if (key == null) {
            switch (this.size) {
                case 3: {
                    if (this.key3 == null) {
                        Object old = this.value3;
                        this.value3 = value;
                        return old;
                    }
                }
                case 2: {
                    if (this.key2 == null) {
                        Object old = this.value2;
                        this.value2 = value;
                        return old;
                    }
                }
                case 1: {
                    if (this.key1 != null) break;
                    Object old = this.value1;
                    this.value1 = value;
                    return old;
                }
            }
        } else if (this.size > 0) {
            int hashCode = key.hashCode();
            switch (this.size) {
                case 3: {
                    if (this.hash3 == hashCode && key.equals(this.key3)) {
                        Object old = this.value3;
                        this.value3 = value;
                        return old;
                    }
                }
                case 2: {
                    if (this.hash2 == hashCode && key.equals(this.key2)) {
                        Object old = this.value2;
                        this.value2 = value;
                        return old;
                    }
                }
                case 1: {
                    if (this.hash1 != hashCode || !key.equals(this.key1)) break;
                    Object old = this.value1;
                    this.value1 = value;
                    return old;
                }
            }
        }
        switch (this.size) {
            default: {
                this.convertToMap();
                this.delegateMap.put(key, value);
                return null;
            }
            case 2: {
                this.hash3 = key == null ? 0 : key.hashCode();
                this.key3 = key;
                this.value3 = value;
                break;
            }
            case 1: {
                this.hash2 = key == null ? 0 : key.hashCode();
                this.key2 = key;
                this.value2 = value;
                break;
            }
            case 0: {
                this.hash1 = key == null ? 0 : key.hashCode();
                this.key1 = key;
                this.value1 = value;
            }
        }
        ++this.size;
        return null;
    }

    public void putAll(Map map) {
        int size = map.size();
        if (size == 0) {
            return;
        }
        if (this.delegateMap != null) {
            this.delegateMap.putAll(map);
            return;
        }
        if (size < 4) {
            Iterator it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = it.next();
                this.put(entry.getKey(), entry.getValue());
            }
        } else {
            this.convertToMap();
            this.delegateMap.putAll(map);
        }
    }

    private void convertToMap() {
        this.delegateMap = this.createDelegateMap();
        switch (this.size) {
            case 3: {
                this.delegateMap.put(this.key3, this.value3);
            }
            case 2: {
                this.delegateMap.put(this.key2, this.value2);
            }
            case 1: {
                this.delegateMap.put(this.key1, this.value1);
            }
        }
        this.size = 0;
        this.hash3 = 0;
        this.hash2 = 0;
        this.hash1 = 0;
        this.key3 = null;
        this.key2 = null;
        this.key1 = null;
        this.value3 = null;
        this.value2 = null;
        this.value1 = null;
    }

    protected AbstractHashedMap createDelegateMap() {
        return new HashedMap();
    }

    public Object remove(Object key) {
        if (this.delegateMap != null) {
            return this.delegateMap.remove(key);
        }
        if (this.size == 0) {
            return null;
        }
        if (key == null) {
            switch (this.size) {
                case 3: {
                    if (this.key3 == null) {
                        Object old = this.value3;
                        this.hash3 = 0;
                        this.key3 = null;
                        this.value3 = null;
                        this.size = 2;
                        return old;
                    }
                    if (this.key2 == null) {
                        Object old = this.value2;
                        this.hash2 = this.hash3;
                        this.key2 = this.key3;
                        this.value2 = this.value3;
                        this.hash3 = 0;
                        this.key3 = null;
                        this.value3 = null;
                        this.size = 2;
                        return old;
                    }
                    if (this.key1 == null) {
                        Object old = this.value1;
                        this.hash1 = this.hash3;
                        this.key1 = this.key3;
                        this.value1 = this.value3;
                        this.hash3 = 0;
                        this.key3 = null;
                        this.value3 = null;
                        this.size = 2;
                        return old;
                    }
                    return null;
                }
                case 2: {
                    if (this.key2 == null) {
                        Object old = this.value2;
                        this.hash2 = 0;
                        this.key2 = null;
                        this.value2 = null;
                        this.size = 1;
                        return old;
                    }
                    if (this.key1 == null) {
                        Object old = this.value1;
                        this.hash1 = this.hash2;
                        this.key1 = this.key2;
                        this.value1 = this.value2;
                        this.hash2 = 0;
                        this.key2 = null;
                        this.value2 = null;
                        this.size = 1;
                        return old;
                    }
                    return null;
                }
                case 1: {
                    if (this.key1 != null) break;
                    Object old = this.value1;
                    this.hash1 = 0;
                    this.key1 = null;
                    this.value1 = null;
                    this.size = 0;
                    return old;
                }
            }
        } else if (this.size > 0) {
            int hashCode = key.hashCode();
            switch (this.size) {
                case 3: {
                    if (this.hash3 == hashCode && key.equals(this.key3)) {
                        Object old = this.value3;
                        this.hash3 = 0;
                        this.key3 = null;
                        this.value3 = null;
                        this.size = 2;
                        return old;
                    }
                    if (this.hash2 == hashCode && key.equals(this.key2)) {
                        Object old = this.value2;
                        this.hash2 = this.hash3;
                        this.key2 = this.key3;
                        this.value2 = this.value3;
                        this.hash3 = 0;
                        this.key3 = null;
                        this.value3 = null;
                        this.size = 2;
                        return old;
                    }
                    if (this.hash1 == hashCode && key.equals(this.key1)) {
                        Object old = this.value1;
                        this.hash1 = this.hash3;
                        this.key1 = this.key3;
                        this.value1 = this.value3;
                        this.hash3 = 0;
                        this.key3 = null;
                        this.value3 = null;
                        this.size = 2;
                        return old;
                    }
                    return null;
                }
                case 2: {
                    if (this.hash2 == hashCode && key.equals(this.key2)) {
                        Object old = this.value2;
                        this.hash2 = 0;
                        this.key2 = null;
                        this.value2 = null;
                        this.size = 1;
                        return old;
                    }
                    if (this.hash1 == hashCode && key.equals(this.key1)) {
                        Object old = this.value1;
                        this.hash1 = this.hash2;
                        this.key1 = this.key2;
                        this.value1 = this.value2;
                        this.hash2 = 0;
                        this.key2 = null;
                        this.value2 = null;
                        this.size = 1;
                        return old;
                    }
                    return null;
                }
                case 1: {
                    if (this.hash1 != hashCode || !key.equals(this.key1)) break;
                    Object old = this.value1;
                    this.hash1 = 0;
                    this.key1 = null;
                    this.value1 = null;
                    this.size = 0;
                    return old;
                }
            }
        }
        return null;
    }

    public void clear() {
        if (this.delegateMap != null) {
            this.delegateMap.clear();
            this.delegateMap = null;
        } else {
            this.size = 0;
            this.hash3 = 0;
            this.hash2 = 0;
            this.hash1 = 0;
            this.key3 = null;
            this.key2 = null;
            this.key1 = null;
            this.value3 = null;
            this.value2 = null;
            this.value1 = null;
        }
    }

    public MapIterator mapIterator() {
        if (this.delegateMap != null) {
            return this.delegateMap.mapIterator();
        }
        if (this.size == 0) {
            return EmptyMapIterator.INSTANCE;
        }
        return new FlatMapIterator(this);
    }

    public Set entrySet() {
        if (this.delegateMap != null) {
            return this.delegateMap.entrySet();
        }
        return new EntrySet(this);
    }

    public Set keySet() {
        if (this.delegateMap != null) {
            return this.delegateMap.keySet();
        }
        return new KeySet(this);
    }

    public Collection values() {
        if (this.delegateMap != null) {
            return this.delegateMap.values();
        }
        return new Values(this);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeInt(this.size());
        MapIterator it = this.mapIterator();
        while (it.hasNext()) {
            out.writeObject(it.next());
            out.writeObject(it.getValue());
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        int count = in.readInt();
        if (count > 3) {
            this.delegateMap = this.createDelegateMap();
        }
        for (int i = count; i > 0; --i) {
            this.put(in.readObject(), in.readObject());
        }
    }

    public Object clone() {
        try {
            Flat3Map cloned = (Flat3Map)super.clone();
            if (cloned.delegateMap != null) {
                cloned.delegateMap = (HashedMap)cloned.delegateMap.clone();
            }
            return cloned;
        }
        catch (CloneNotSupportedException ex) {
            throw new InternalError();
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (this.delegateMap != null) {
            return this.delegateMap.equals(obj);
        }
        if (!(obj instanceof Map)) {
            return false;
        }
        Map other = (Map)obj;
        if (this.size != other.size()) {
            return false;
        }
        if (this.size > 0) {
            Object otherValue = null;
            switch (this.size) {
                case 3: {
                    if (!other.containsKey(this.key3)) {
                        return false;
                    }
                    otherValue = other.get(this.key3);
                    if (this.value3 == null ? otherValue != null : !this.value3.equals(otherValue)) {
                        return false;
                    }
                }
                case 2: {
                    if (!other.containsKey(this.key2)) {
                        return false;
                    }
                    otherValue = other.get(this.key2);
                    if (this.value2 == null ? otherValue != null : !this.value2.equals(otherValue)) {
                        return false;
                    }
                }
                case 1: {
                    if (!other.containsKey(this.key1)) {
                        return false;
                    }
                    otherValue = other.get(this.key1);
                    if (!(this.value1 == null ? otherValue != null : !this.value1.equals(otherValue))) break;
                    return false;
                }
            }
        }
        return true;
    }

    public int hashCode() {
        if (this.delegateMap != null) {
            return this.delegateMap.hashCode();
        }
        int total = 0;
        switch (this.size) {
            case 3: {
                total += this.hash3 ^ (this.value3 == null ? 0 : this.value3.hashCode());
            }
            case 2: {
                total += this.hash2 ^ (this.value2 == null ? 0 : this.value2.hashCode());
            }
            case 1: {
                total += this.hash1 ^ (this.value1 == null ? 0 : this.value1.hashCode());
            }
        }
        return total;
    }

    public String toString() {
        if (this.delegateMap != null) {
            return this.delegateMap.toString();
        }
        if (this.size == 0) {
            return "{}";
        }
        StringBuffer buf = new StringBuffer(128);
        buf.append('{');
        switch (this.size) {
            case 3: {
                buf.append(this.key3 == this ? "(this Map)" : this.key3);
                buf.append('=');
                buf.append(this.value3 == this ? "(this Map)" : this.value3);
                buf.append(',');
            }
            case 2: {
                buf.append(this.key2 == this ? "(this Map)" : this.key2);
                buf.append('=');
                buf.append(this.value2 == this ? "(this Map)" : this.value2);
                buf.append(',');
            }
            case 1: {
                buf.append(this.key1 == this ? "(this Map)" : this.key1);
                buf.append('=');
                buf.append(this.value1 == this ? "(this Map)" : this.value1);
            }
        }
        buf.append('}');
        return buf.toString();
    }

    static class ValuesIterator
    extends EntrySetIterator {
        ValuesIterator(Flat3Map parent) {
            super(parent);
        }

        public Object next() {
            super.next();
            return this.getValue();
        }
    }

    static class Values
    extends AbstractCollection {
        private final Flat3Map parent;

        Values(Flat3Map parent) {
            this.parent = parent;
        }

        public int size() {
            return this.parent.size();
        }

        public void clear() {
            this.parent.clear();
        }

        public boolean contains(Object value) {
            return this.parent.containsValue(value);
        }

        public Iterator iterator() {
            if (this.parent.delegateMap != null) {
                return this.parent.delegateMap.values().iterator();
            }
            if (this.parent.size() == 0) {
                return EmptyIterator.INSTANCE;
            }
            return new ValuesIterator(this.parent);
        }
    }

    static class KeySetIterator
    extends EntrySetIterator {
        KeySetIterator(Flat3Map parent) {
            super(parent);
        }

        public Object next() {
            super.next();
            return this.getKey();
        }
    }

    static class KeySet
    extends AbstractSet {
        private final Flat3Map parent;

        KeySet(Flat3Map parent) {
            this.parent = parent;
        }

        public int size() {
            return this.parent.size();
        }

        public void clear() {
            this.parent.clear();
        }

        public boolean contains(Object key) {
            return this.parent.containsKey(key);
        }

        public boolean remove(Object key) {
            boolean result = this.parent.containsKey(key);
            this.parent.remove(key);
            return result;
        }

        public Iterator iterator() {
            if (this.parent.delegateMap != null) {
                return this.parent.delegateMap.keySet().iterator();
            }
            if (this.parent.size() == 0) {
                return EmptyIterator.INSTANCE;
            }
            return new KeySetIterator(this.parent);
        }
    }

    static class EntrySetIterator
    implements Iterator,
    Map.Entry {
        private final Flat3Map parent;
        private int nextIndex = 0;
        private boolean canRemove = false;

        EntrySetIterator(Flat3Map parent) {
            this.parent = parent;
        }

        public boolean hasNext() {
            return this.nextIndex < this.parent.size;
        }

        public Object next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException("No next() entry in the iteration");
            }
            this.canRemove = true;
            ++this.nextIndex;
            return this;
        }

        public void remove() {
            if (!this.canRemove) {
                throw new IllegalStateException("remove() can only be called once after next()");
            }
            this.parent.remove(this.getKey());
            --this.nextIndex;
            this.canRemove = false;
        }

        public Object getKey() {
            if (!this.canRemove) {
                throw new IllegalStateException("getKey() can only be called after next() and before remove()");
            }
            switch (this.nextIndex) {
                case 3: {
                    return this.parent.key3;
                }
                case 2: {
                    return this.parent.key2;
                }
                case 1: {
                    return this.parent.key1;
                }
            }
            throw new IllegalStateException("Invalid map index");
        }

        public Object getValue() {
            if (!this.canRemove) {
                throw new IllegalStateException("getValue() can only be called after next() and before remove()");
            }
            switch (this.nextIndex) {
                case 3: {
                    return this.parent.value3;
                }
                case 2: {
                    return this.parent.value2;
                }
                case 1: {
                    return this.parent.value1;
                }
            }
            throw new IllegalStateException("Invalid map index");
        }

        public Object setValue(Object value) {
            if (!this.canRemove) {
                throw new IllegalStateException("setValue() can only be called after next() and before remove()");
            }
            Object old = this.getValue();
            switch (this.nextIndex) {
                case 3: {
                    this.parent.value3 = value;
                    break;
                }
                case 2: {
                    this.parent.value2 = value;
                    break;
                }
                case 1: {
                    this.parent.value1 = value;
                }
            }
            return old;
        }

        public boolean equals(Object obj) {
            if (!this.canRemove) {
                return false;
            }
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            Map.Entry other = (Map.Entry)obj;
            Object key = this.getKey();
            Object value = this.getValue();
            return (key == null ? other.getKey() == null : key.equals(other.getKey())) && (value == null ? other.getValue() == null : value.equals(other.getValue()));
        }

        public int hashCode() {
            if (!this.canRemove) {
                return 0;
            }
            Object key = this.getKey();
            Object value = this.getValue();
            return (key == null ? 0 : key.hashCode()) ^ (value == null ? 0 : value.hashCode());
        }

        public String toString() {
            if (this.canRemove) {
                return this.getKey() + "=" + this.getValue();
            }
            return "";
        }
    }

    static class EntrySet
    extends AbstractSet {
        private final Flat3Map parent;

        EntrySet(Flat3Map parent) {
            this.parent = parent;
        }

        public int size() {
            return this.parent.size();
        }

        public void clear() {
            this.parent.clear();
        }

        public boolean remove(Object obj) {
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            Map.Entry entry = (Map.Entry)obj;
            Object key = entry.getKey();
            boolean result = this.parent.containsKey(key);
            this.parent.remove(key);
            return result;
        }

        public Iterator iterator() {
            if (this.parent.delegateMap != null) {
                return this.parent.delegateMap.entrySet().iterator();
            }
            if (this.parent.size() == 0) {
                return EmptyIterator.INSTANCE;
            }
            return new EntrySetIterator(this.parent);
        }
    }

    static class FlatMapIterator
    implements MapIterator,
    ResettableIterator {
        private final Flat3Map parent;
        private int nextIndex = 0;
        private boolean canRemove = false;

        FlatMapIterator(Flat3Map parent) {
            this.parent = parent;
        }

        public boolean hasNext() {
            return this.nextIndex < this.parent.size;
        }

        public Object next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException("No next() entry in the iteration");
            }
            this.canRemove = true;
            ++this.nextIndex;
            return this.getKey();
        }

        public void remove() {
            if (!this.canRemove) {
                throw new IllegalStateException("remove() can only be called once after next()");
            }
            this.parent.remove(this.getKey());
            --this.nextIndex;
            this.canRemove = false;
        }

        public Object getKey() {
            if (!this.canRemove) {
                throw new IllegalStateException("getKey() can only be called after next() and before remove()");
            }
            switch (this.nextIndex) {
                case 3: {
                    return this.parent.key3;
                }
                case 2: {
                    return this.parent.key2;
                }
                case 1: {
                    return this.parent.key1;
                }
            }
            throw new IllegalStateException("Invalid map index");
        }

        public Object getValue() {
            if (!this.canRemove) {
                throw new IllegalStateException("getValue() can only be called after next() and before remove()");
            }
            switch (this.nextIndex) {
                case 3: {
                    return this.parent.value3;
                }
                case 2: {
                    return this.parent.value2;
                }
                case 1: {
                    return this.parent.value1;
                }
            }
            throw new IllegalStateException("Invalid map index");
        }

        public Object setValue(Object value) {
            if (!this.canRemove) {
                throw new IllegalStateException("setValue() can only be called after next() and before remove()");
            }
            Object old = this.getValue();
            switch (this.nextIndex) {
                case 3: {
                    this.parent.value3 = value;
                }
                case 2: {
                    this.parent.value2 = value;
                }
                case 1: {
                    this.parent.value1 = value;
                }
            }
            return old;
        }

        public void reset() {
            this.nextIndex = 0;
            this.canRemove = false;
        }

        public String toString() {
            if (this.canRemove) {
                return "Iterator[" + this.getKey() + "=" + this.getValue() + "]";
            }
            return "Iterator[]";
        }
    }
}

